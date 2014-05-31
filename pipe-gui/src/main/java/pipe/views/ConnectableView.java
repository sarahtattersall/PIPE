package pipe.views;

import pipe.controllers.PetriNetController;
import pipe.gui.Constants;
import pipe.gui.PetriNetTab;
import pipe.handlers.LabelHandler;
import pipe.views.viewComponents.NameLabel;
import uk.ac.imperial.pipe.models.petrinet.Connectable;

import javax.swing.*;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Shape;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @param <T> Connectable model
 */
public abstract class ConnectableView<T extends Connectable> extends AbstractPetriNetViewComponent<T>
        implements Cloneable, Serializable {
    protected NameLabel nameLabel;


    /**
     * Shape of the place on the Petri net
     */
    protected final Shape shape;

    boolean _attributesVisible = false;

    ConnectableView(T model, Shape shape) {
        this("", model, shape);
    }

    private ConnectableView(String id, T model, Shape shape) {
        this(id, model, null, shape);
    }

    ConnectableView(String id, T model, PetriNetController controller, Shape shape) {
        super(id, model, controller);
        this.shape = shape;
        setLocation(model.getX(), model.getY());

        int x = (int) (model.getX() + model.getNameXOffset());
        int y = (int) (model.getX() + model.getNameXOffset());
        nameLabel = new NameLabel(model.getName(), x, y);
        addChangeListener();
        updateBounds();
    }

    private void addChangeListener() {
        final ConnectableView view = this;
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
    private void updateLabelLocation() {
        nameLabel.setPosition(model.getX() + model.getNameXOffset(), model.getY() + model.getNameYOffset());
    }

    protected void updateBounds() {
        setBounds(model.getX(), model.getY(), model.getWidth() + getComponentDrawOffset(), model.getHeight() + getComponentDrawOffset());

        //TODO: THIS IS A DIRTY HACK IN ORDER TO GET DRAGGIGN WHEN ZOOMED WORKING
        Component root = SwingUtilities.getRoot(this);
        if (root != null) {
            root.repaint();
        }
    }

    @Override
    public String getName() {
        return model.getName();
    }

    @Override
    public String getId() {
        return model.getId();
    }

    @Override
    public void setId(String idInput) {
        _id = idInput;
    }

    @Override
    public T getModel() {
        return model;
    }

    @Override
    public void addedToGui() {
        _deleted = false;
        _markedAsDeleted = false;
        updateBounds();
        //        update();
    }

    @Override
    public void delete() {
        if (getParent() != null) {
            getParent().remove(nameLabel);
        }
        super.delete();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    @Override
    public int getLayerOffset() {
        return Constants.PLACE_TRANSITION_LAYER_OFFSET;
    }

    // TODO: DELETE
    @Override
    public AbstractPetriNetViewComponent clone() {
        AbstractPetriNetViewComponent<?> pnCopy = super.clone();
        return pnCopy;
    }

    public int centreOffsetTop() {
        return model.getHeight() / 2;
    }

    public int centreOffsetLeft() {
        return model.getWidth() / 2;
    }

    protected void addLabelToContainer(PetriNetTab tab) {
        tab.add(nameLabel);
        nameLabel.setPosition(model.getX() + model.getNameXOffset(), model.getY() + model.getNameYOffset());
        LabelHandler labelHandler = new LabelHandler(nameLabel, this);
        nameLabel.addMouseListener(labelHandler);
        nameLabel.addMouseMotionListener(labelHandler);
        nameLabel.addMouseWheelListener(labelHandler);
    }

    public void removeFromArc(ArcView oldArcView) {
        //        model.removeFromArcs(oldArcView);
    }

    public LinkedList<ArcView> outboundArcs() {
        //        return model.outboundArcs();
        return null;
    }

    public LinkedList<ArcView> inboundArcs() {
        //        return model.inboundArcs();
        return null;
    }

    @Override
    public void translate(int x, int y) {
        //        setPositionX(_positionX + x);
        //        setPositionY(_positionY + y);
        //        update();
    }

    void setCentre(double x, double y) {
        //        setPositionX(x - (getWidth() / 2.0));
        //        setPositionY(y - (getHeight() / 2.0));
        //        update();
    }

    public Iterator<?> getConnectFromIterator() {
        return null;
        //        return model.outboundArcs().iterator();
    }

    public Iterator<?> getConnectToIterator() {
        return null;
        //        return model.inboundArcs().iterator();
    }

    void newCopy(ConnectableView<?> ptObject) {
        //        if (_original != null) {
        //            _original._lastCopy = ptObject;
        //        };
    }

    //TODO: DELETE
    public ConnectableView<?> getOriginal() {
        //        return _original;
        return null;
    }

    void setOriginal(ConnectableView<?> ptObject) {
        //        _original = ptObject;
    }

    public abstract void showEditor();

    public boolean getAttributesVisible() {
        return _attributesVisible;
    }

    public void setAttributesVisible(boolean flag) {
        _attributesVisible = flag;
    }

    public abstract void toggleAttributesVisible();
}
