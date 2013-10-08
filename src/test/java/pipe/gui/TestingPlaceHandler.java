package pipe.gui;

import java.awt.Container;
import java.util.LinkedList;

import pipe.handlers.PlaceHandler;
import pipe.historyActions.HistoryManager;
import pipe.views.MarkingView;
import pipe.views.PlaceView;

public class TestingPlaceHandler extends PlaceHandler
{

	public TestingPlaceHandler(Container contentpane, PlaceView obj)
	{
		super(contentpane, obj);
	}
	
	public void addTokenForTesting(LinkedList<MarkingView> oldMarkingViews,
			HistoryManager historyManager)
	{
		super.addToken(oldMarkingViews, historyManager);
	}
	public void deleteTokenForTesting(LinkedList<MarkingView> oldMarkingViews,
			HistoryManager historyManager)
	{
		super.deleteToken(oldMarkingViews, historyManager);
	}
}
