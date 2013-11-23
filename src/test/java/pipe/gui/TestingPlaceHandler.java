package pipe.gui;

import java.awt.Container;
import java.util.LinkedList;
import java.util.List;

import pipe.controllers.PetriNetController;
import pipe.handlers.PlaceHandler;
import pipe.historyActions.HistoryManager;
import pipe.models.Marking;
import pipe.models.Place;
import pipe.views.MarkingView;
import pipe.views.PlaceView;

public class TestingPlaceHandler extends PlaceHandler
{

	public TestingPlaceHandler(PlaceView view, Container contentpane, Place obj, PetriNetController controller)
	{
		super(view, contentpane, obj, controller);
	}
	
	public void addTokenForTesting(List<Marking> oldMarkingViews,
			HistoryManager historyManager)
	{
		super.addToken(oldMarkingViews, historyManager);
	}
	public void deleteTokenForTesting(List<Marking> oldMarkingViews,
			HistoryManager historyManager)
	{
		super.deleteToken(oldMarkingViews, historyManager);
	}
}
