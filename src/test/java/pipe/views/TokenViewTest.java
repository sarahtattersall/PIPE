package pipe.views;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.junit.Before;
import org.junit.Test;

import pipe.exceptions.TokenLockedException;
import pipe.models.Token;
import pipe.utilities.math.Matrix;

public class TokenViewTest implements Observer
{

	private TokenView tokenView;
	private TokenView newTokenView;
	private boolean called;
	private boolean newTokenViewShouldBeNull;

	@Before
	public void setUp() throws Exception
	{
		tokenView = new TokenView(true, "Default", Color.black); 
		called = false; 
		newTokenViewShouldBeNull = false; 
	}
	@Test
	public void verifyPrintsSummaryInfo() throws Exception
	{
		assertEquals("TokenView: Enabled=true, Id=Default, Color=java.awt.Color[r=0,g=0,b=0], Lock count=0", tokenView.toString()); 
	}
	@Test
	public void verifyIdIsNormalizedToTrimmedNotNullLowerCase() throws Exception
	{
		tokenView = new TokenView(true, "Default ", Color.black); 
		assertEquals("Default ",tokenView.getID()); 
		assertEquals("default",tokenView.getNormalizedID()); 
		tokenView = new TokenView(true, null, Color.black); 
		assertEquals(null,tokenView.getID()); 
		assertEquals("avoid NPE, although this is not a valid ID; should we throw?", 
				"",tokenView.getNormalizedID()); 
	}
	@Test
	public void verifyValidTokenViewHasNonBlankNonNullIdAndIsEnabled() throws Exception
	{
		assertTrue(tokenView.isValid()); 
		tokenView = new TokenView(true,  "", Color.black); 
		assertTrue(tokenView.isValid()); 
		tokenView = new TokenView(false,  "", Color.black); 
		assertFalse(tokenView.isValid()); 
	}
	@Test
	public void verifyCanOnlyBeDisabledIfNotLocked() throws Exception
	{
		tokenView.setEnabled(false); 
		assertTrue(!tokenView.isEnabled());
		tokenView.setEnabled(true); 
		assertTrue(tokenView.isEnabled());
		tokenView.incrementLock(); 
		assertTrue(tokenView.isLocked());
		try 
		{
			tokenView.setEnabled(false); 
			fail("should throw");
		}
		catch (TokenLockedException e)
		{
			assertEquals("TokenSetController.updateOrAddTokenView: Enabled TokenView is in use for 1 Places.  It may not be disabled unless markings are removed from those Places.\n"+
             "Details: TokenView: Enabled=true, Id=Default, Color=java.awt.Color[r=0,g=0,b=0], Lock count=1", e.getMessage()); 
		}
	}
	@Test
	public void verifyTokenViewCanBeReplacedWithNewInstancePreservingInternalState() throws Exception
	{
		tokenView.addObserver(this); 
		Token model = tokenView.getModel();
		//TODO previousIncidenceMatrix is populated by getIncidenceMatrix(); reconcile this 
		
		assertNull(tokenView.getIncidenceMatrix()); 
		tokenView.createIncidenceMatrix(new ArrayList<ArcView>(), new ArrayList<TransitionView>(), new ArrayList<PlaceView>());
		Matrix matrix = tokenView.getIncidenceMatrix(); 
		assertEquals("previousIncidenceMatrix is populated by getIncidenceMatrix()",
				matrix, tokenView.getPreviousIncidenceMatrix()); 
		assertNotNull(matrix);
		newTokenView = new TokenView(true, "Fred", Color.blue); 
		newTokenView.updateModelFromPrevious(tokenView); 
		assertEquals(model,newTokenView.getModel()); 
		assertEquals(matrix,newTokenView.getPreviousIncidenceMatrix()); 
		assertEquals(matrix,newTokenView.getIncidenceMatrix()); 
		assertEquals(true,newTokenView.isEnabled()); 
		assertEquals("Fred",newTokenView.getID()); 
		assertEquals(Color.blue,newTokenView.getColor()); 
		while (!called)
		{
			Thread.sleep(10);
		}	
	}
	@Test
	public void verifyTokenViewTellsObserversToDeleteSelfIfUpdatedTokenViewIsDisabled() throws Exception
	{
		tokenView.addObserver(this); 
		newTokenView = new TokenView(false, "Fred", Color.black); 
		newTokenViewShouldBeNull = true; 
		newTokenView.updateModelFromPrevious(tokenView); 
		assertEquals("Fred",newTokenView.getID()); 
		while (!called)
		{
			Thread.sleep(10);
		}	
	}
	@Test
	public void verifyTokenViewTellsObserversToDeleteSelfIfIsSetDisabled() throws Exception
	{
		tokenView.addObserver(this); 
		newTokenViewShouldBeNull = true; 
		tokenView.disableAndNotifyObservers(); 
		while (!called)
		{
			Thread.sleep(10);
		}	
	}
	@Override
	public void update(Observable oldObject, Object newObject)
	{
		called = true;
		assertEquals((TokenView) oldObject, tokenView); 
		if (newTokenViewShouldBeNull)  assertNull(newObject);
		else assertEquals((TokenView) newObject, newTokenView); 
	}
}
