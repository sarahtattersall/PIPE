package pipe.views;

import pipe.controllers.PetriNetController;
import pipe.gui.Constants;
import pipe.gui.PetriNetTab;
import pipe.handlers.LabelHandler;
import pipe.models.component.Connectable;
import pipe.views.viewComponents.NameLabel;

import java.awt.*;
import java.awt.geom.Point2D;
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
    boolean _attributesVisible = false;

    ConnectableView(T model) {
        this("", model);
    }

    private ConnectableView(String id, T model) {
        this(id, model, null);
    }

    protected NameLabel nameLabel;

    ConnectableView(String id, T model,
                    PetriNetController controller) {
        super(id, model, controller);
        setLocation((int) model.getX(), (int) model.getY());

        int x = (int) (model.getX() + model.getNameXOffset());
        int y = (int) (model.getX() + model.getNameXOffset());
        nameLabel = new NameLabel(model.getName(), x, y);
        addChangeListener();
//        updateLabelLocation();
    }

    /**
     * Updates label position according to the Connectable location
     */
    private void updateLabelLocation() {
        nameLabel.setPosition(model.getX() + model.getNameXOffset(), model.getY() + model.getNameYOffset());
    }

    private void addChangeListener() {
        model.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                String name = propertyChangeEvent.getPropertyName();
                if (name.equals("x") || name.equals("y")) {
                    updateBounds();
                    updateLabelLocation();
                } else if (name.equals("nameOffsetX") || name.equals("nameOffsetY")) {
                    updateLabelLocation();
                } else if (name.equals("id") || name.equals("name")) {
                    //TODO: NAMELABEL SHOULD LISTEN?
                    String newName = (String) propertyChangeEvent.getNewValue();
                    nameLabel.setName(newName);
                    nameLabel.repaint();
                }
            }

        });
    }

    protected void updateBounds() {
        bounds.setBounds((int) model.getX(), (int) model.getY(), model.getHeight(), model.getHeight());
        bounds.grow(getComponentDrawOffset(), getComponentDrawOffset());
        setBounds(bounds);
    }

    @Override
    public void setId(String idInput) {
        _id = idInput;
    }

    @Override
    public String getId() {
        return model.getId();
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

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.translate(getComponentDrawOffset(), getComponentDrawOffset());
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

    @Override
    public void delete() {
        if (getParent() != null) {
            getParent().remove(nameLabel);
        }
        super.delete();
    }

    void setOriginal(ConnectableView<?> ptObject) {
        //        _original = ptObject;
    }

    public abstract void showEditor();

    @Override
    public void addedToGui() {
        _deleted = false;
        _markedAsDeleted = false;
        updateBounds();
        //        update();
    }

    public boolean getAttributesVisible() {
        return _attributesVisible;
    }

    public void setAttributesVisible(boolean flag) {
        _attributesVisible = flag;
    }

    public abstract void toggleAttributesVisible();

    @Override
    public int getLayerOffset() {
        return Constants.PLACE_TRANSITION_LAYER_OFFSET;
    }


    @Override
    public AbstractPetriNetViewComponent clone() {
        AbstractPetriNetViewComponent<?> pnCopy = super.clone();
        return pnCopy;
    }

    @Override
    public T getModel() {
        return model;
    }
}
