/*
 * ArcWeightEdit.java
 */

package pipe.historyActions;

import pipe.models.Arc;
import pipe.models.PetriNet;
import pipe.models.Token;
import pipe.views.ArcView;
import pipe.views.MarkingView;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Alex Charalambous
 */
public class ArcWeight extends HistoryItem {

    private final Arc arc;
    private final PetriNet petriNet;
    private final Token token;
    private final String newWeight;
    private final String oldWeight;

    public ArcWeight(final Arc arc, final PetriNet petriNet, final Token token,
                     final String oldWeight, final String newWeight) {

        this.arc = arc;
        this.petriNet = petriNet;
        this.token = token;
        this.oldWeight = oldWeight;
        this.newWeight = newWeight;
    }

    /** */
    public void undo() {
        arc.setWeight(token, oldWeight);
        petriNet.notifyObservers();
    }

    /** */
    public void redo() {
        arc.setWeight(token, newWeight);
        petriNet.notifyObservers();
    }

}
