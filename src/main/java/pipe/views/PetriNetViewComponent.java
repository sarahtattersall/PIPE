package pipe.views;

import pipe.controllers.PetriNetController;
import pipe.gui.*;
import pipe.historyActions.HistoryItem;
import pipe.models.component.PetriNetComponent;
import pipe.views.viewComponents.NameLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.io.Serializable;
import java.util.EventListener;

public abstract class PetriNetViewComponent<T extends PetriNetComponent> extends JComponent implements Zoomable, CopyPasteable, Cloneable, Translatable, Serializable {
    static final int COMPONENT_DRAW_OFFSET = 5;
    protected String _id;
    public NameLabel _nameLabel;
    boolean _selectable;
    private boolean _draggable;
    protected boolean _copyPasteable;
    protected static boolean _ignoreSelection;
    protected Rectangle _bounds;
    protected boolean _deleted;
    protected boolean _markedAsDeleted;
    private ZoomController zoomControl;

    protected T model;
    protected int _zoomPercentage;
    protected final PetriNetController petriNetController;

    protected PetriNetViewComponent() {
        this(null, null, 0, 0, null, null);
    }

    PetriNetViewComponent(String id, String name, double namePositionX, double namePositionY, T model, PetriNetController controller) {
        _id = id;
        _selectable = true;
        _draggable = true;
        _copyPasteable = true;
        _ignoreSelection = false;
        _bounds = new Rectangle();
        _deleted = false;
        _markedAsDeleted = false;
        _zoomPercentage = 100;
        this.model = model;
        _nameLabel = new NameLabel(name, _zoomPercentage, namePositionX, namePositionY);
        this.petriNetController = controller;
    }

    void setNameLabelName(String name) {
        _nameLabel.setName(name);
    }

    protected void addZoomController(final ZoomController zoomControl2) {
        this.zoomControl = zoomControl2;
    }

    public void setId(String idInput) {
        _id = idInput;
    }

    public String getId() {
        return _id;
    }

    public NameLabel getNameLabel() {
        return _nameLabel;
    }

    public T getModel() {
        return model;
    }

    void addLabelToContainer() {
        if (getParent() != null && _nameLabel.getParent() == null) {
            getParent().add(_nameLabel);
        }
    }

//    public void select() {
//        if (_selectable && !_selected) {
//            _selected = true;
//            repaint();
//        }
//    }
//
//    public void deselect() {
//        if (_selected) {
//            _selected = false;
//            repaint();
//        }
//    }

    public boolean isSelectable() {
        return _selectable;
    }

    public void setSelectable(boolean allow) {
        _selectable = allow;
    }

    public static void ignoreSelection(boolean ignore) {
        _ignoreSelection = ignore;
    }

    public boolean isDraggable() {
        return _draggable;
    }

    public void setDraggable(boolean allow) {
        _draggable = allow;
    }

    public abstract void addedToGui();

    public void delete() {
        _deleted = true;
        removeFromContainer();
        removeAll();
    }

    public void undelete(PetriNetView model, PetriNetTab view) {
        model.addPetriNetObject(this);
        view.add(this);
    }

    protected void removeFromContainer() {
        Container c = getParent();

        if (c != null) {
            c.remove(this);
        }
    }

    //TODO: REMOVE
    public HistoryItem setPNObjectName(String name) {
//        String oldName = this.getName();
//        this.setId(name);
//        this.setName(name);
//        return new PetriNetObjectName(this, oldName, name);
        return null;
    }

    public boolean isDeleted() {
        return _deleted || _markedAsDeleted;
    }

    public void markAsDeleted() {
        _markedAsDeleted = true;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    public boolean isCopyPasteable() {
        return _copyPasteable;
    }

    public abstract int getLayerOffset();

    public int getZoomPercentage() {
        return _zoomPercentage;
    }


    public PetriNetViewComponent clone() {
        try {
            PetriNetViewComponent pnCopy = (PetriNetViewComponent) super.clone();

            EventListener[] mouseListeners = pnCopy.getListeners(MouseListener.class);
            for (EventListener mouseListener2 : mouseListeners) {
                pnCopy.removeMouseListener((MouseListener) mouseListener2);
            }

            mouseListeners = pnCopy.getListeners(MouseMotionListener.class);

            for (EventListener mouseListener1 : mouseListeners) {
                pnCopy.removeMouseMotionListener((MouseMotionListener) mouseListener1);
            }

            mouseListeners = pnCopy.getListeners(MouseWheelListener.class);

            for (EventListener mouseListener : mouseListeners) {
                pnCopy.removeMouseWheelListener((MouseWheelListener) mouseListener);
            }

            return pnCopy;
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }

    static int getComponentDrawOffset() {
        return COMPONENT_DRAW_OFFSET;
    }

    ZoomController getZoomController() {
        return this.zoomControl;
    }

    String getNameLabelName() {
        return _nameLabel.getName();
    }

    void setNameLabel(NameLabel nameLabel) {
        _nameLabel = nameLabel;
    }

    /**
     * Each subclass should know how to add itself to a PetriNetTab
     * @param tab to add itself to
     */
    public abstract void addToPetriNetTab(PetriNetTab tab);

    public PetriNetController getPetriNetController() {
        return petriNetController;
    }

    /**
     * @return true if model selected
     */
    public boolean isSelected() {
        return petriNetController.isSelected(model);
    }
}
