package pipe.views;

import static org.junit.Assert.*;

import java.awt.Color;
import java.util.Observable;
import java.util.Observer;

import org.junit.Before;
import org.junit.Test;

import pipe.models.PipeObservable;
import pipe.models.Token;

public class MarkingViewTest implements Observer
{
	private MarkingView markingView;
	private TokenView tokenView;
	private boolean called;
	private boolean newMarkingViewShouldBeNull;

	@Before
	public void setUp() throws Exception
	{
		tokenView = new TokenView(true, "Fred", Color.black); 
	}
	@Test
	public void verifyTokenViewCanBeReplacedWithNewInstance() throws Exception
	{
		Token model = tokenView.getModel(); 
		markingView = new MarkingView(tokenView, 3); 
		assertEquals("Fred", markingView.getToken().getID()); 
		TokenView newTokenView = new TokenView(true, "Mary", Color.green); 
		newTokenView.updateModelFromPrevious(tokenView); 
		assertEquals(newTokenView, markingView.getToken()); 
		assertEquals("Mary", markingView.getToken().getID()); 
		assertEquals(Color.green, markingView.getToken().getColor()); 
		assertEquals(model, markingView.getToken().getModel()); 
	}
	@Test
	public void verifyTokenViewThatIsLaterDisabledGeneratesNullUpdate() throws Exception
	{
		markingView = new MarkingView(tokenView, 3); 
		assertEquals(tokenView, markingView.getToken()); 
		tokenView.disableAndNotifyObservers(); 
		assertNull(markingView.getToken()); 
	}
	@Test
	public void verifyMarkingViewTellsObserversToDeleteSelfIfItsTokenViewIsSetDisabled() throws Exception
	{
		markingView = new MarkingView(tokenView, 3); 
		markingView.addObserver(this); 
		newMarkingViewShouldBeNull = true; 
		markingView.setChanged();
		markingView.notifyObservers(null); 
		while (!called)
		{
			Thread.sleep(10);
		}	
	}
	@Override
	public void update(Observable oldObject, Object newObject)
	{
		called = true;
		MarkingView view=null; 
		if (oldObject instanceof PipeObservable)
		{
			view = (MarkingView) ((PipeObservable) oldObject).getObservable();
		}
		assertEquals(view , markingView); 
		if (newMarkingViewShouldBeNull)  assertNull(newObject);
	}

}
