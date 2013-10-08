package pipe.views;

import pipe.gui.ApplicationSettings;
import pipe.gui.Constants;
import pipe.gui.Grid;
import pipe.gui.ZoomController;
import pipe.models.Connectable;
import pipe.models.interfaces.IObserver;
import pipe.views.viewComponents.NameLabel;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

public abstract class ConnectableView extends PetriNetViewComponent implements Cloneable, IObserver,Serializable
{
    private ConnectableView _lastCopy = null;
    private ConnectableView _original = null;
    private int _copyNumber = 0;
    public final Connectable _model;

    boolean _attributesVisible = false;

    ConnectableView(double positionXInput, double positionYInput, Connectable model)
    {
        this(positionXInput, positionYInput, "", model);
    }

    private ConnectableView(double positionX, double positionY, String id, Connectable model)
    {
        this(positionX, positionY, id, "", Constants.DEFAULT_OFFSET_X, Constants.DEFAULT_OFFSET_Y, model);
    }

    ConnectableView(double positionX, double positionY, String id, String name, double nameOffsetX, double nameOffsetY, Connectable model)
    {
        super(id, name, positionX, positionY, nameOffsetX, nameOffsetY);
        _model = model;

        if(ApplicationSettings.getApplicationView() != null)
        {
            this.addZoomController(ApplicationSettings.getApplicationView().getCurrentTab().getZoomController());
        }
    }

    public void setName(String nameInput)
    {
        super.setNameLabelName(nameInput);
    }

    public void setId(String idInput)
    {
        _id = idInput;
        setName(_id);
    }

    public String getId()
    {
        return (_id != null) ? _id : _nameLabel.getName();
    }

    public String getName()
    {
        return (_nameLabel != null) ? super.getNameLabelName() : _id;
    }


    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.translate(getComponentDrawOffset(), getComponentDrawOffset());
        g2.transform(ZoomController.getTransform(_zoomPercentage));
    }

    public Point2D getIntersectOffset(Point2D start)
    {
        return new Point2D.Double();
    }

    public int centreOffsetTop()
    {
        return (int) (ZoomController.getZoomedValue(_componentHeight / 2.0, _zoomPercentage));
    }

    public int centreOffsetLeft()
    {
        return (int) (ZoomController.getZoomedValue(_componentWidth / 2.0, _zoomPercentage));
    }

    void updateBounds()
    {
        double scaleFactor = ZoomController.getScaleFactor(_zoomPercentage);
        _positionX = _locationX * scaleFactor;
        _positionY = _locationY * scaleFactor;
        _bounds.setBounds((int) _positionX, (int) _positionY,(int) (_componentWidth * scaleFactor),(int) (_componentHeight * scaleFactor));
        _bounds.grow(getComponentDrawOffset(), getComponentDrawOffset());
        setBounds(_bounds);
    }

    public void addInbound(ArcView newArcView)
    {
        _model.addInbound(newArcView);
    }

    public void addOutbound(ArcView newArcView)
    {
        _model.addOutbound(newArcView);
    }

    public void addInboundOrOutbound(ArcView newArcView)
    {
        _model.addInboundOrOutbound(newArcView);
    }

    public void removeFromArc(ArcView oldArcView)
    {
        _model.removeFromArcs(oldArcView);
    }

    public void removeToArc(ArcView oldArcView)
    {
        _model.removeToArc(oldArcView);
    }

    public void updateConnected()
    {
        updateArcs(_model.outboundArcs());
        updateArcs(_model.inboundArcs());
    }

    private void updateArcs(LinkedList<ArcView> arcsFrom)
    {
        for(ArcView someArcView : arcsFrom)
        {
            updateEndPoint(someArcView);
            if(someArcView != null)
            {
                someArcView.updateArcPosition();
            }
        }
    }

    public LinkedList<ArcView> outboundArcs()
    {
        return _model.outboundArcs();
    }

    public LinkedList<ArcView> inboundArcs()
    {
        return _model.inboundArcs();
    }

    public void translate(int x, int y)
    {
        setPositionX(_positionX + x);
        setPositionY(_positionY + y);
        update();
    }

    void setCentre(double x, double y)
    {
        setPositionX(x - (getWidth() / 2.0));
        setPositionY(y - (getHeight() / 2.0));
        update();
    }

    public void update()
    {
        updateBounds();
        updateLabelLocation();
        updateConnected();
    }


    private void updateLabelLocation()
    {
        _nameLabel.setPosition(Grid.getModifiedX((int) (_positionX + ZoomController
                .getZoomedValue(_nameOffsetX, _zoomPercentage))), Grid
                .getModifiedY((int) (_positionY + ZoomController.getZoomedValue(
                        _nameOffsetY, _zoomPercentage))));
    }

    public void delete()
    {
        if(getParent() != null)
        {
            getParent().remove(_nameLabel);
        }
        super.delete();
    }

    public void select()
    {
        if(_selectable && !_selected)
        {
            _selected = true;

            Iterator arcsFrom = _model.outboundArcs().iterator();
            while(arcsFrom.hasNext())
            {
                ((ArcView) arcsFrom.next()).select();
            }

            Iterator arcsTo = _model.inboundArcs().iterator();
            while(arcsTo.hasNext())
            {
                ((ArcView) arcsTo.next()).select();
            }
            repaint();
        }
    }

    public void addedToGui()
    {
        _deleted = false;
        _markedAsDeleted = false;
        addLabelToContainer();
        update();
    }

    boolean areNotSameType(ConnectableView o)
    {
        return (this.getClass() != o.getClass());
    }

    public Iterator getConnectFromIterator()
    {
        return _model.outboundArcs().iterator();
    }

    public Iterator getConnectToIterator()
    {
        return _model.inboundArcs().iterator();
    }

    public abstract void updateEndPoint(ArcView arcView);

    int getCopyNumber()
    {
        if(_original != null)
        {
            _original._copyNumber++;
            return _original._copyNumber;
        }
        else
        {
            return 0;
        }
    }

    void newCopy(ConnectableView ptObject)
    {
        if(_original != null)
        {
            _original._lastCopy = ptObject;
        }
    }

    public ConnectableView getLastCopy()
    {
        return _lastCopy;
    }

    public void resetLastCopy()
    {
        _lastCopy = null;
    }

    void setOriginal(ConnectableView ptObject)
    {
        _original = ptObject;
    }

    public ConnectableView getOriginal()
    {
        return _original;
    }

    public abstract void showEditor();

    public void setAttributesVisible(boolean flag)
    {
        _attributesVisible = flag;
    }

    public boolean getAttributesVisible()
    {
        return _attributesVisible;
    }

    public int getLayerOffset()
    {
        return Constants.PLACE_TRANSITION_LAYER_OFFSET;
    }

    public abstract void toggleAttributesVisible();

    public void zoomUpdate(int value)
    {
        _zoomPercentage = value;
        update();
    }

    public PetriNetViewComponent clone()
    {
        PetriNetViewComponent pnCopy = super.clone();
        pnCopy.setNameLabel((NameLabel) _nameLabel.clone());
        return pnCopy;
    }

    public Connectable getModel()
    {
        return _model;
    }
}
