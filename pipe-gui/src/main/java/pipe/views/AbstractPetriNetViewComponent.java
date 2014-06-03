package pipe.views;

import pipe.controllers.PetriNetController;
import uk.ac.imperial.pipe.models.petrinet.PetriNetComponent;

import javax.swing.*;
import java.awt.Container;
import java.awt.Rectangle;


public abstract class AbstractPetriNetViewComponent<T extends PetriNetComponent> extends JComponent implements PetriNetViewComponent {

    public static final int COMPONENT_DRAW_OFFSET = 5;

    protected static boolean ignoreSelection = false;

    protected final PetriNetController petriNetController;

    protected String id;

    /**
     * Parent that this component is contained in.
     */
    protected final Container parent;

    protected boolean copyPasteable;

    protected Rectangle bounds;

    protected boolean deleted;

    protected boolean markedAsDeleted;

    protected T model;

    protected boolean selectable;

    public AbstractPetriNetViewComponent(String id, T model, PetriNetController controller, Container parent) {
        this.id = id;
        this.parent = parent;
        selectable = true;
        copyPasteable = true;
        bounds = new Rectangle();
        deleted = false;
        markedAsDeleted = false;
        this.model = model;
        this.petriNetController = controller;
    }

    /**
     * Any code that must be executed in order to perform a component delete
     */
    public abstract void componentSpecificDelete();

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

    public final String getId() {
        return id;
    }

    public final void setId(String idInput) {
        id = idInput;
    }

    public final T getModel() {
        return model;
    }

    @Override
    public final void delete() {
        componentSpecificDelete();
        deleted = true;
        removeFromContainer();
        removeAll();
    }

    protected final void removeFromContainer() {
        Container c = getParent();

        if (c != null) {
            c.remove(this);
        }
    }


    protected static int getComponentDrawOffset() {
        return COMPONENT_DRAW_OFFSET;
    }

    public final PetriNetController getPetriNetController() {
        return petriNetController;
    }

    /**
     * @return true if model selected
     */
    public final boolean isSelected() {
        return petriNetController.isSelected(model);
    }
}
