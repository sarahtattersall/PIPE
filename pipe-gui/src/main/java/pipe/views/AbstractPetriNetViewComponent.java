package pipe.views;

import pipe.controllers.PetriNetController;
import uk.ac.imperial.pipe.models.petrinet.PetriNetComponent;

import javax.swing.*;
import java.awt.Container;
import java.awt.Rectangle;


public abstract class AbstractPetriNetViewComponent<T extends PetriNetComponent> extends JComponent implements PetriNetViewComponent {

    public static final int COMPONENT_DRAW_OFFSET = 5;

    protected static boolean _ignoreSelection;

    protected final PetriNetController petriNetController;

    protected String _id;

    /**
     * Parent that this component is contained in.
     */
    protected final Container parent;

    protected boolean _copyPasteable;

    protected Rectangle bounds;

    protected boolean _deleted;

    protected boolean _markedAsDeleted;

    protected T model;

    boolean _selectable;

    private boolean _draggable;

    public AbstractPetriNetViewComponent(String id, T model, PetriNetController controller, Container parent) {
        _id = id;
        this.parent = parent;
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
