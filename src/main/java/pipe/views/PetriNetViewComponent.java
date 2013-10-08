package pipe.views;

import pipe.historyActions.HistoryItem;
import pipe.historyActions.PetriNetObjectName;
import pipe.gui.*;
import pipe.views.viewComponents.NameLabel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.EventListener;

public abstract class PetriNetViewComponent extends JComponent implements Zoomable, CopyPasteable, Cloneable, Translatable, Serializable
{
    static final int COMPONENT_DRAW_OFFSET = 5;
    public double _positionX;
    public double _positionY;
    public double _nameOffsetX;
    public double _nameOffsetY;
    double _componentWidth;
    double _componentHeight;
    double _locationX;
    double _locationY;
    protected String _id;
    public NameLabel _nameLabel;
    protected boolean _selected;
    boolean _selectable;
    private boolean _draggable;
    protected boolean _copyPasteable;
    protected static boolean _ignoreSelection;
    protected Rectangle _bounds;
    protected boolean _deleted;
    protected boolean _markedAsDeleted;
    private ZoomController zoomControl;

    protected int _zoomPercentage;

    protected PetriNetViewComponent()
    {
        this(null, null, 0, 0, 0, 0);
    }

    PetriNetViewComponent(String id, String name, double positionX, double positionY, double nameOffsetX, double nameOffsetY)
    {
        _id = id;
        _positionX = positionX;
        _positionY = positionY;
        _selected = false;
        _selectable = true;
        _draggable = true;
        _copyPasteable = true;
        _ignoreSelection = false;
        _bounds = new Rectangle();
        _deleted = false;
        _markedAsDeleted = false;
        _zoomPercentage = 100;
        _nameLabel = new NameLabel(name, _zoomPercentage, nameOffsetX, nameOffsetY);
    }

    void setNameLabelName(String name)
    {
        _nameLabel.setName(name);
    }

    protected void addZoomController(final ZoomController zoomControl2)
    {
        this.zoomControl = zoomControl2;
    }

    public void setId(String idInput)
    {
        _id = idInput;
    }

    public String getId()
    {
        return _id;
    }

    public NameLabel getNameLabel()
    {
        return _nameLabel;
    }

    void addLabelToContainer()
    {
        if(getParent() != null && _nameLabel.getParent() == null)
        {
            getParent().add(_nameLabel);
        }
    }

    public boolean isSelected()
    {
        return _selected;
    }

    public void select()
    {
        if(_selectable && !_selected)
        {
            _selected = true;
            repaint();
        }
    }

    public void deselect()
    {
        if(_selected)
        {
            _selected = false;
            repaint();
        }
    }

    public boolean isSelectable()
    {
        return _selectable;
    }

    public void setSelectable(boolean allow)
    {
        _selectable = allow;
    }

    public static void ignoreSelection(boolean ignore)
    {
        _ignoreSelection = ignore;
    }

    public boolean isDraggable()
    {
        return _draggable;
    }

    public void setDraggable(boolean allow)
    {
        _draggable = allow;
    }

    public abstract void addedToGui();

    public void delete()
    {
        _deleted = true;
        ApplicationSettings.getApplicationView().getCurrentPetriNetView().removePetriNetObject(this);
        removeFromContainer();
        removeAll();
    }

    public void undelete(PetriNetView model, PetriNetTab view)
    {
        model.addPetriNetObject(this);
        view.add(this);
    }

    protected void removeFromContainer()
    {
        Container c = getParent();

        if(c != null)
        {
            c.remove(this);
        }
    }

    public HistoryItem setPNObjectName(String name)
    {
        String oldName = this.getName();
        this.setId(name);
        this.setName(name);
        return new PetriNetObjectName(this, oldName, name);
    }

    public boolean isDeleted()
    {
        return _deleted || _markedAsDeleted;
    }

    public void markAsDeleted()
    {
        _markedAsDeleted = true;
    }

    public void select(Rectangle selectionRectangle)
    {
        if(selectionRectangle.intersects(this.getBounds()))
        {
            select();
        }
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
    }

    public boolean isCopyPasteable()
    {
        return _copyPasteable;
    }

    public abstract int getLayerOffset();

    public int getZoomPercentage()
    {
        return _zoomPercentage;
    }


    public PetriNetViewComponent clone()
    {
        try
        {
            PetriNetViewComponent pnCopy = (PetriNetViewComponent) super.clone();

            EventListener[] mouseListeners = pnCopy.getListeners(MouseListener.class);
            for(EventListener mouseListener2 : mouseListeners)
            {
                pnCopy.removeMouseListener((MouseListener) mouseListener2);
            }

            mouseListeners = pnCopy.getListeners(MouseMotionListener.class);

            for(EventListener mouseListener1 : mouseListeners)
            {
                pnCopy.removeMouseMotionListener((MouseMotionListener) mouseListener1);
            }

            mouseListeners = pnCopy.getListeners(MouseWheelListener.class);

            for(EventListener mouseListener : mouseListeners)
            {
                pnCopy.removeMouseWheelListener((MouseWheelListener) mouseListener);
            }

            return pnCopy;
        }
        catch(CloneNotSupportedException e)
        {
            throw new Error(e);
        }
    }

    public void setPositionX(double positionXInput) {
		_positionX = positionXInput;
		_locationX = ZoomController.getUnzoomedValue(_positionX, _zoomPercentage);
	}

	void setPositionY(double positionYInput) {
		_positionY = positionYInput;
		_locationY = ZoomController.getUnzoomedValue(_positionY, _zoomPercentage);
	}

    public void setNameOffsetX(double nameOffsetXInput) {
		_nameOffsetX += ZoomController.getUnzoomedValue(nameOffsetXInput, _zoomPercentage);
	}

	public void setNameOffsetY(double nameOffsetYInput) {
		_nameOffsetY += ZoomController.getUnzoomedValue(nameOffsetYInput, _zoomPercentage);
	}

	public double getPositionX() {
		return _positionX;
	}

	public double getPositionY() {
		return _positionY;
	}

    public double getNameOffsetX() {
		return _nameOffsetX;
	}

	public double getNameOffsetY() {
		return _nameOffsetY;
	}

	public Double getNameOffsetXObject() {
		return this._nameOffsetX;
	}

	public Double getNameOffsetYObject() {
		return this._nameOffsetY;
	}

    public Point2D.Double getCentre() {
		return new Point2D.Double(_positionX + getWidth() / 2.0, _positionY + getHeight() / 2.0);
	}

    public Double getPositionXObject()
    {
        return new Double(_locationX);
    }

    public Double getPositionYObject()
    {
        return new Double(_locationY);
    }


    static int getComponentDrawOffset()
    {
        return COMPONENT_DRAW_OFFSET;
    }

    ZoomController getZoomController()
    {
        return this.zoomControl;
    }

    String getNameLabelName()
    {
        return _nameLabel.getName();
    }

    void setNameLabel(NameLabel nameLabel)
    {
        _nameLabel = nameLabel;
    }
}
