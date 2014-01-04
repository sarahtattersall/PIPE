package pipe.views;

import pipe.controllers.PetriNetController;
import pipe.gui.ApplicationSettings;
import pipe.gui.Constants;
import pipe.gui.PetriNetTab;
import pipe.gui.ZoomController;
import pipe.models.component.Connectable;
import pipe.models.interfaces.IObserver;
import pipe.views.viewComponents.NameLabel;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

public abstract class ConnectableView<T extends Connectable> extends PetriNetViewComponent<T> implements Cloneable, IObserver, Serializable {
    private ConnectableView _lastCopy = null;
    private ConnectableView _original = null;
    private int _copyNumber = 0;

    boolean _attributesVisible = false;

    ConnectableView(T model) {
        this("", model);
    }

    private ConnectableView(String id, T model) {
        this(id, model.getName(), Constants.DEFAULT_OFFSET_X, Constants.DEFAULT_OFFSET_Y, model, null);
    }

    ConnectableView(String id, String name, double nameOffsetX, double nameOffsetY,
            T model, PetriNetController controller) {
        super(id, name, nameOffsetX, nameOffsetY, model, controller);
        setLocation((int) model.getX(),  (int) model.getY());
        PipeApplicationView view = ApplicationSettings.getApplicationView();
        if (view != null) {
            PetriNetTab tab = view.getCurrentTab();
            ZoomController zoomController = tab.getZoomController();
            addZoomController(zoomController);
        }
    }

    public void setName(String nameInput) {
        super.setNameLabelName(nameInput);
    }

    public void setId(String idInput) {
        _id = idInput;
    }

    public String getId() {
        return (_id != null) ? _id : _nameLabel.getName();
    }

    public String getName() {
        return (_nameLabel != null) ? super.getNameLabelName() : _id;
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.translate(getComponentDrawOffset(), getComponentDrawOffset());
        g2.transform(ZoomController.getTransform(_zoomPercentage));
    }

    public Point2D getIntersectOffset(Point2D start) {
        return new Point2D.Double();
    }

    public int centreOffsetTop() {
        return (int) (ZoomController.getZoomedValue(model.getHeight() / 2.0, _zoomPercentage));
    }

    public int centreOffsetLeft() {
        return (int) (ZoomController.getZoomedValue(model.getWidth() / 2.0, _zoomPercentage));
    }

    void updateBounds() {
        double scaleFactor = ZoomController.getScaleFactor(_zoomPercentage);
        double x = model.getX() * scaleFactor;
        double y = model.getY() * scaleFactor;
        bounds.setBounds((int) x, (int) y, (int) (model.getHeight() * scaleFactor),
                (int) (model.getHeight() * scaleFactor));
        bounds.grow(getComponentDrawOffset(), getComponentDrawOffset());
        setBounds(bounds);
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

    public void updateConnected() {
//        updateArcs(model.outboundArcs());
//        updateArcs(model.inboundArcs());
    }

    public LinkedList<ArcView> outboundArcs() {
//        return model.outboundArcs();
        return null;
    }

    public LinkedList<ArcView> inboundArcs() {
//        return model.inboundArcs();
        return null;
    }

    public void translate(int x, int y) {
//        setPositionX(_positionX + x);
//        setPositionY(_positionY + y);
        update();
    }

    void setCentre(double x, double y) {
//        setPositionX(x - (getWidth() / 2.0));
//        setPositionY(y - (getHeight() / 2.0));
        update();
    }

    public void update() {
        updateBounds();
        updateLabelLocation();
        updateConnected();
    }


    /**
     * Updates label position according to the Connectable location
     */
    private void updateLabelLocation() {
        double zoomedX = ZoomController.getZoomedValue(model.getX(), _zoomPercentage);
        double zoomedY = ZoomController.getZoomedValue(model.getY(), _zoomPercentage);
        _nameLabel.setPosition(zoomedX + model.getNameXOffset(), zoomedY + model.getNameYOffset());
    }

    public void delete() {
        if (getParent() != null) {
            getParent().remove(_nameLabel);
        }
        super.delete();
    }

    public void addedToGui() {
        _deleted = false;
        _markedAsDeleted = false;
        addLabelToContainer();
        update();
    }

    boolean areNotSameType(ConnectableView o) {
        return (this.getClass() != o.getClass());
    }

    public Iterator getConnectFromIterator() {
        return model.outboundArcs().iterator();
    }

    public Iterator getConnectToIterator() {
        return model.inboundArcs().iterator();
    }

    int getCopyNumber() {
        if (_original != null) {
            _original._copyNumber++;
            return _original._copyNumber;
        } else {
            return 0;
        }
    }

    void newCopy(ConnectableView ptObject) {
        if (_original != null) {
            _original._lastCopy = ptObject;
        }
    }

    public ConnectableView getLastCopy() {
        return _lastCopy;
    }

    public void resetLastCopy() {
        _lastCopy = null;
    }

    void setOriginal(ConnectableView ptObject) {
        _original = ptObject;
    }

    public ConnectableView getOriginal() {
        return _original;
    }

    public abstract void showEditor();

    public void setAttributesVisible(boolean flag) {
        _attributesVisible = flag;
    }

    public boolean getAttributesVisible() {
        return _attributesVisible;
    }

    public int getLayerOffset() {
        return Constants.PLACE_TRANSITION_LAYER_OFFSET;
    }

    public abstract void toggleAttributesVisible();

    public void zoomUpdate(int value) {
        _zoomPercentage = value;
        update();
    }

    public PetriNetViewComponent clone() {
        PetriNetViewComponent pnCopy = super.clone();
        pnCopy.setNameLabel((NameLabel) _nameLabel.clone());
        return pnCopy;
    }

    public T getModel() {
        return model;
    }
}
