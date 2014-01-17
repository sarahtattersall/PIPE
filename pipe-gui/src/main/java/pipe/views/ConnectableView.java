package pipe.views;

import pipe.controllers.PetriNetController;
import pipe.gui.Constants;
import pipe.gui.ZoomController;
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
    //    private ConnectableView _lastCopy = null;
    //    private ConnectableView _original = null;
    private int _copyNumber = 0;

    ConnectableView(T model) {
        this("", model);
    }

    private ConnectableView(String id, T model) {
        this(id, model.getName(), Constants.DEFAULT_OFFSET_X, Constants.DEFAULT_OFFSET_Y, model, null);
    }

    ConnectableView(String id, String name, double nameOffsetX, double nameOffsetY,
                    T model, PetriNetController controller) {
        super(id, name, nameOffsetX, nameOffsetY, model, controller);
        setLocation((int) model.getX(), (int) model.getY());
//        ZoomController zoomController = controller.getZoomController();
//        addZoomController(zoomController);

        addChangeListener();
        updateLabelLocation();
//        updateBounds();
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
                    String newName = (String) propertyChangeEvent.getNewValue();
                    setNameLabelName(newName);
                }
            }

        });
    }

    @Override
    public String getName() {
        return (_nameLabel != null) ? super.getNameLabelName() : _id;
    }

    @Override
    public void setName(String nameInput) {
        super.setNameLabelName(nameInput);
    }

    @Override
    public void setId(String idInput) {
        _id = idInput;
    }

    public Point2D getIntersectOffset(Point2D start) {
        return new Point2D.Double();
    }

    @Override
    public String getId() {
        return (_id != null) ? _id : _nameLabel.getName();
    }

    public int centreOffsetTop() {
        return (int) (ZoomController.getZoomedValue(model.getHeight() / 2.0, _zoomPercentage));
    }

    public int centreOffsetLeft() {
        return (int) (ZoomController.getZoomedValue(model.getWidth() / 2.0, _zoomPercentage));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.translate(getComponentDrawOffset(), getComponentDrawOffset());
        g2.transform(ZoomController.getTransform(_zoomPercentage));
    }

    public void addInbound(ArcView newArcView) {
        //        model.addInbound(newArcView);
    }

    public void addOutbound(ArcView newArcView) {
        //        model.addOutbound(newArcView);
    }

    public void addInboundOrOutbound(ArcView newArcView) {
        //        model.addInboundOrOutbound(newArcView);
    }

    public void removeFromArc(ArcView oldArcView) {
        //        model.removeFromArcs(oldArcView);
    }

    public void removeToArc(ArcView oldArcView) {
        //        model.removeToArc(oldArcView);
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

    /**
     * Updates label position according to the Connectable location
     */
    private void updateLabelLocation() {
        double zoomedX = ZoomController.getZoomedValue(model.getX(), _zoomPercentage);
        double zoomedY = ZoomController.getZoomedValue(model.getY(), _zoomPercentage);
        _nameLabel.setPosition(zoomedX + model.getNameXOffset(), zoomedY + model.getNameYOffset());
    }

    protected void updateBounds() {
        System.out.println("UPDATE BOUNDS");
        double scaleFactor = ZoomController.getScaleFactor(_zoomPercentage);
        double x = model.getX() * scaleFactor;
        double y = model.getY() * scaleFactor;
        bounds.setBounds((int) x, (int) y, (int) (model.getHeight() * scaleFactor),
                (int) (model.getHeight() * scaleFactor));
        bounds.grow(getComponentDrawOffset(), getComponentDrawOffset());
        setBounds(bounds);
    }

    void setCentre(double x, double y) {
        //        setPositionX(x - (getWidth() / 2.0));
        //        setPositionY(y - (getHeight() / 2.0));
//        update();
    }

    boolean areNotSameType(ConnectableView<?> o) {
        return (this.getClass() != o.getClass());
    }

    public Iterator<?> getConnectFromIterator() {
        return null;
        //        return model.outboundArcs().iterator();
    }

    public Iterator<?> getConnectToIterator() {
        return null;
        //        return model.inboundArcs().iterator();
    }

    //TODO: DELETE
    int getCopyNumber() {
        //        if (_original != null) {
        //            _original._copyNumber++;
        //            return _original._copyNumber;
        //        } else {
        //            return 0;
        //        }
        return 0;
    }

    @Override
    public void delete() {
        if (getParent() != null) {
            getParent().remove(_nameLabel);
        }
        super.delete();
    }

    void newCopy(ConnectableView<?> ptObject) {
        //        if (_original != null) {
        //            _original._lastCopy = ptObject;
        //        };
    }

    @Override
    public void addedToGui() {
        _deleted = false;
        _markedAsDeleted = false;
        addLabelToContainer();
        updateBounds();
//        update();
    }

    //TODO: DELETE
    public ConnectableView<?> getLastCopy() {
        return null;
    }

    public void resetLastCopy() {
        //        _lastCopy = null;
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

    @Override
    public void zoomUpdate(int value) {
        _zoomPercentage = value;
//        update();
    }


    @Override
    public int getLayerOffset() {
        return Constants.PLACE_TRANSITION_LAYER_OFFSET;
    }


    @Override
    public AbstractPetriNetViewComponent clone() {
        AbstractPetriNetViewComponent<?> pnCopy = super.clone();
        pnCopy.setNameLabel((NameLabel) _nameLabel.clone());
        return pnCopy;
    }

    @Override
    public T getModel() {
        return model;
    }
}
