package pipe.views;

import pipe.controllers.PetriNetController;
import pipe.handlers.LabelHandler;
import uk.ac.imperial.pipe.models.petrinet.Connectable;

import javax.swing.*;
import java.awt.Component;
import java.awt.Container;
import java.awt.Shape;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

/**
 * Abstract connectable view for connectable models
 * @param <T> Connectable model
 */
public abstract class ConnectableView<T extends Connectable> extends AbstractPetriNetViewComponent<T>
        implements Cloneable, Serializable {
    /**
     * Name label for the connectable item
     */
    protected NameLabel nameLabel;

    /**
     * Shape of the place on the Petri net
     */
    protected final Shape shape;

    /**
     * Constructor
     * @param id
     * @param model
     * @param controller
     * @param parent
     * @param shape
     */
    ConnectableView(String id, T model, PetriNetController controller, Container parent, Shape shape) {
        super(id, model, controller, parent);
        this.shape = shape;
        setLocation(model.getX(), model.getY());

        int x = (int) (model.getX() + model.getNameXOffset());
        int y = (int) (model.getX() + model.getNameXOffset());
        nameLabel = new NameLabel(model.getName(), x, y);
        addChangeListener();
        updateBounds();
    }

    private void addChangeListener() {
        model.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                String name = propertyChangeEvent.getPropertyName();
                if (name.equals(Connectable.X_CHANGE_MESSAGE) || name.equals(Connectable.Y_CHANGE_MESSAGE)) {
                    updateBounds();
                    updateLabelLocation();
                } else if (name.equals(Connectable.NAME_X_OFFSET_CHANGE_MESSAGE) || name.equals(
                        Connectable.NAME_Y_OFFSET_CHANGE_MESSAGE)) {
                    updateLabelLocation();
                } else if (name.equals(Connectable.ID_CHANGE_MESSAGE) || name.equals(Connectable.NAME_CHANGE_MESSAGE)) {
                    //TODO: NAMELABEL SHOULD LISTEN?
                    String newName = (String) propertyChangeEvent.getNewValue();
                    nameLabel.setName(newName);
                    nameLabel.repaint();
                }
            }

        });
    }

    /**
     * Updates label position according to the Connectable location
     */
    private final void updateLabelLocation() {
        nameLabel.setPosition(model.getX() + model.getNameXOffset(), model.getY() + model.getNameYOffset());
    }

    /**
     * Changes the displayed bounds of the object relative to its x,y width and height
     */
    protected final void updateBounds() {
        setBounds(model.getX(), model.getY(), model.getWidth() + getComponentDrawOffset(), model.getHeight() + getComponentDrawOffset());

        //TODO: THIS IS A DIRTY HACK IN ORDER TO GET DRAGGIGN WHEN ZOOMED WORKING
        Component root = SwingUtilities.getRoot(this);
        if (root != null) {
            root.repaint();
        }
    }

    /**
     * Performs a connectable specific delete by removing the name label from the parent
     */
    @Override
    public final void componentSpecificDelete() {
        if (getParent() != null) {
            getParent().remove(nameLabel);
        }
    }

    /**
     * Adds the name label to the container
     * @param container
     */
    protected final void addLabelToContainer(Container container) {
        container.add(nameLabel);
        nameLabel.setPosition(model.getX() + model.getNameXOffset(), model.getY() + model.getNameYOffset());
        LabelHandler<T> labelHandler = new LabelHandler<>(nameLabel, this);
        nameLabel.addMouseListener(labelHandler);
        nameLabel.addMouseMotionListener(labelHandler);
        nameLabel.addMouseWheelListener(labelHandler);
    }

}
