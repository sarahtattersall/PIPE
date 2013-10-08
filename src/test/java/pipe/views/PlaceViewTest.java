package pipe.views;

import static org.junit.Assert.*;

import java.awt.Color;

import org.junit.Before;
import org.junit.Test;

public class PlaceViewTest
{
	private MarkingView markingView;
	private PlaceView placeView;
	private TokenView tokenView;
	@Before
	public void setUp() throws Exception
	{
		tokenView = new TokenView(true, "Default", Color.black);
	}
	@Test
	public void verifyDeletesMarkingViewWhenItRequestsUpdate() throws Exception
	{
		placeView = new PlaceView(); 
		assertEquals(0, placeView.getCurrentMarkingView().size());
		placeView.setActiveTokenView(tokenView); 
		assertEquals(1, placeView.getCurrentMarkingView().size());
		markingView = placeView.getCurrentMarkingView().get(0); 
		assertEquals(tokenView, markingView.getToken());
		//TODO determine whether initialmarkingview needs similar logic 
		placeView.getInitialMarkingView().add(markingView); 
		// notify is asynchronous, but in the debugger it seems to run serially in the same thread, so haven't bothered with sleeping til done (see MarkingViewTest)
		tokenView.disableAndNotifyObservers(); 
		assertEquals("tokenView, then markingView request deletion",0, placeView.getCurrentMarkingView().size());
	}
}
