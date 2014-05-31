package pipe.views;

import pipe.controllers.PetriNetController;
import pipe.gui.Translatable;
import pipe.historyActions.HistoryItem;
import uk.ac.imperial.pipe.models.petrinet.PetriNetComponent;

import javax.swing.*;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.io.Serializable;
import java.util.EventListener;


public abstract class AbstractPetriNetViewComponent<T extends PetriNetComponent> extends JComponent implements Cloneable, Translatable, Serializable, PetriNetViewComponent {

    public static final int COMPONENT_DRAW_OFFSET = 5;

    protected static boolean _ignoreSelection;

    protected final PetriNetController petriNetController;

    protected String _id;

    protected boolean _copyPasteable;

    protected Rectangle bounds;

    protected boolean _deleted;

    protected boolean _markedAsDeleted;

    protected T model;

    boolean _selectable;

    private boolean _draggable;

    public AbstractPetriNetViewComponent(String id, T model, PetriNetController controller) {
        _id = id;
        _selectable = true;
        _draggable = true;
        _copyPasteable = true;
        _ignoreSelection = false;
        bounds = new Rectangle();
        _deleted = false;
        _markedAsDeleted = false;
        this.model = model;
        this.petriNetController = controller;
    }

    public static void ignoreSelection(boolean ignore) {
        _ignoreSelection = ignore;
    }

    @Override
    public int hashCode() {
        return model.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AbstractPetriNetViewComponent that = (AbstractPetriNetViewComponent) o;

        if (!model.equals(that.model)) {
            return false;
        }

        return true;
    }

    public String getId() {
        return _id;
    }

    public void setId(String idInput) {
        _id = idInput;
    }

    public T getModel() {
        return model;
    }

    public boolean isDraggable() {
        return _draggable;
    }

    public void setDraggable(boolean allow) {
        _draggable = allow;
    }

    public abstract void addedToGui();

    @Override
    public void delete() {
        _deleted = true;
        removeFromContainer();
        removeAll();
    }

    protected void removeFromContainer() {
        Container c = getParent();

        if (c != null) {
            c.remove(this);
        }
    }

    @Override
    public boolean isDeleted() {
        return _deleted || _markedAsDeleted;
    }

    //TODO: REMOVE
    public HistoryItem setPNObjectName(String name) {
        //        String oldName = this.getName();
        //        this.setId(name);
        //        this.setName(name);
        //        return new PetriNetObjectName(this, oldName, name);
        return null;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }


    public abstract int getLayerOffset();

    @Override
    public AbstractPetriNetViewComponent<T> clone() {
        try {
            AbstractPetriNetViewComponent<T> pnCopy = (AbstractPetriNetViewComponent<T>) super.clone();

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

    protected static int getComponentDrawOffset() {
        return COMPONENT_DRAW_OFFSET;
    }

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
