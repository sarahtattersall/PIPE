package pipe.gui;

import java.awt.Container;
import java.util.LinkedList;
import java.util.List;

import pipe.handlers.PlaceHandler;
import pipe.historyActions.HistoryManager;
import pipe.models.Marking;
import pipe.models.Place;
import pipe.views.MarkingView;
import pipe.views.PlaceView;

public class TestingPlaceHandler extends PlaceHandler
{

	public TestingPlaceHandler(Container contentpane, Place obj)
	{
		super(contentpane, obj);
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
