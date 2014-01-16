/*
 * ArcWeightEdit.java
 */

package pipe.historyActions;

import pipe.models.component.arc.Arc;
import pipe.models.component.Connectable;
import pipe.models.component.token.Token;

/**
 * @author Alex Charalambous
 */
public class ArcWeight<S extends Connectable, T extends Connectable> extends HistoryItem {

    private final Arc<S,T> arc;
    private final Token token;
    private final String newWeight;
    private final String oldWeight;

    public ArcWeight(final Arc<S,T> arc, final Token token,
                     final String oldWeight, final String newWeight) {

        this.arc = arc;
        this.token = token;
        this.oldWeight = oldWeight;
        this.newWeight = newWeight;
    }


    /** */
    @Override
    public void undo() {
        arc.setWeight(token, oldWeight);
    }

    /** */
    @Override
    public void redo() {
        arc.setWeight(token, newWeight);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArcWeight arcWeight = (ArcWeight) o;

        if (!arc.equals(arcWeight.arc)) return false;
        if (!newWeight.equals(arcWeight.newWeight)) return false;
        if (!oldWeight.equals(arcWeight.oldWeight)) return false;
        if (!token.equals(arcWeight.token)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = arc.hashCode();
        result = 31 * result + token.hashCode();
        result = 31 * result + newWeight.hashCode();
        result = 31 * result + oldWeight.hashCode();
        return result;
    }
}
