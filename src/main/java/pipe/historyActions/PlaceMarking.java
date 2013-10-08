/*
 * PlaceMarkingEdit.java
 */

package pipe.historyActions;

import pipe.gui.ApplicationSettings;
import pipe.views.MarkingView;
import pipe.views.PlaceView;

import java.util.LinkedList;

/**
 * 
 * @author corveau
 */
public class PlaceMarking extends HistoryItem
{

	private final PlaceView _placeView;
	private final LinkedList<MarkingView> newMarking;
	private final LinkedList<MarkingView> oldMarking;

	public PlaceMarking(PlaceView _placeView, LinkedList<MarkingView> _oldMarking,
                        LinkedList<MarkingView> _newMarking) {
		this._placeView = _placeView;
		oldMarking = _oldMarking;
		newMarking = _newMarking;
	}

	public void undo() {
		// Restore references to tokenClasses so that updates are reflected
		// in marking.
		for (MarkingView m : oldMarking) {
            m.setToken(ApplicationSettings.getApplicationView().getCurrentPetriNetView().getTokenClassFromID(
                    m.getToken().getID()));
		}
		_placeView.setCurrentMarking(oldMarking);
	}

	public void redo() {
		// Restore references to tokenClasses so that updates are reflected
		// in marking.
		for (MarkingView m : newMarking) {
            m.setToken(ApplicationSettings.getApplicationView().getCurrentPetriNetView().getTokenClassFromID(
                    m.getToken().getID()));
		}
		_placeView.setCurrentMarking(newMarking);
	}

}
