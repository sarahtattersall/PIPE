/*
 * PlaceMarkingEdit.java
 */

package pipe.historyActions;

import pipe.gui.ApplicationSettings;
import pipe.models.PetriNet;
import pipe.models.PetriNetComponent;
import pipe.models.Place;
import pipe.models.Token;
import pipe.views.MarkingView;
import pipe.views.PlaceView;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author corveau
 */
public class PlaceMarking extends HistoryItem
{

    private final Place place;
    private final PetriNet petriNet;
    private final Token token;
    private final int previousCount;
    private final int newCount;

	public PlaceMarking(Place place, PetriNet petriNet, Token token, int previousCount, int newCount) {
		this.place = place;
        this.petriNet = petriNet;
        this.token = token;
        this.previousCount = previousCount;
        this.newCount = newCount;
    }

	public void undo() {
        place.setTokenCount(token, previousCount);
        //TODO MAKE NET OBSERVE PLACE
        petriNet.notifyObservers();

	}

	public void redo() {
        place.setTokenCount(token, newCount);
        petriNet.notifyObservers();

	}

}
