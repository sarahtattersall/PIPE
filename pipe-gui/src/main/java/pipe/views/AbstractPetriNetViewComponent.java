package pipe.views;

import pipe.controllers.PetriNetController;
import uk.ac.imperial.pipe.models.petrinet.PetriNetComponent;

import javax.swing.*;
import java.awt.Container;
import java.awt.Rectangle;


/**
 * Abstract view component
 * @param <T> underlying Petri net component model
 */
public abstract class AbstractPetriNetViewComponent<T extends PetriNetComponent> extends JComponent implements PetriNetViewComponent {

    /**
     * x, y offset from the model position
     */
    public static final int COMPONENT_DRAW_OFFSET = 5;

    /**
     * If ignore selection = true then the component should not be selected
     */
    protected static boolean ignoreSelection = false;

    /**
     * Controller for the Petri net that the Petri net component is housed in
     */
    protected final PetriNetController petriNetController;

    /**
     * Petri net component id
     */
    protected String id;

    /**
     * Parent that this component is contained in.
     */
    protected final Container parent;

    /**
     * true if the item can be copy and pasted
     */
    protected boolean copyPasteable;

    /**
     * View item bounds
     */
    protected Rectangle bounds;

    /**
     * True if this view has been deleted
     */
    protected boolean deleted;

    /**
     * Legacy mark as deleted code
     */
    @Deprecated
    protected boolean markedAsDeleted;

    /**
     * Underlying model
     */
    protected T model;

    /**
     * True if the component is selectable
     */
    @Deprecated
    protected boolean selectable;

    /**
     * Constructor
     * @param id component id
     * @param model model
     * @param controller Petri net controller that the model belongs to
     * @param parent Parent of the view
     */
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

    /**
     * @return model id
     */
    public final String getId() {
        return id;
    }

    /**
     *
     * @return underlying model
     */
    public final T getModel() {
        return model;
    }

    /**
     * Delete the view
     */
    @Override
    public final void delete() {
        componentSpecificDelete();
        deleted = true;
        removeFromContainer();
        removeAll();
    }

    /**
     * Remove the view from its container
     */
    protected final void removeFromContainer() {
        Container c = getParent();

        if (c != null) {
            c.remove(this);
        }
    }


    /**
     *
     * @return the x, y draw offset
     */
    protected static int getComponentDrawOffset() {
        return COMPONENT_DRAW_OFFSET;
    }

    /**
     * @return true if model selected
     */
    public final boolean isSelected() {
        return petriNetController.isSelected(model);
    }
}
