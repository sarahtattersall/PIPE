/*
 * TransitionServerSemanticEdit.java
 */
package pipe.historyActions.transition;


import uk.ac.imperial.pipe.models.petrinet.Transition;

import javax.swing.undo.AbstractUndoableEdit;

/**
 * Controls if a transition is an infinite server
 */
public class TransitionInfiniteServer extends AbstractUndoableEdit {

    /**
     * Underlying transition model
     */
    private final Transition transition;

    /**
     * true if infinite server
     */
    private final boolean value;

    /**
     * Constructor
     * @param transition underlying transition model
     * @param value true if infinite server
     */
    public TransitionInfiniteServer(final Transition transition, final boolean value) {

        this.transition = transition;
        this.value = value;
    }

    @Override
    public int hashCode() {
        int result = transition != null ? transition.hashCode() : 0;
        result = 31 * result + (value ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final TransitionInfiniteServer that = (TransitionInfiniteServer) o;

        if (value != that.value) {
            return false;
        }
        if (transition != null ? !transition.equals(that.transition) : that.transition != null) {
            return false;
        }

        return true;
    }

    /**
     * Toggles the transitions server semantics to the previous value
     */
    @Override
    public void undo() {
        super.undo();
        transition.setInfiniteServer(!value);
    }


    /**
     * Toggles the transitions server semantics to the new value
     */
    @Override
    public void redo() {
        super.redo();
        transition.setInfiniteServer(value);
    }

}
