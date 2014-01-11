package pipe.views;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.junit.Before;
import org.junit.Test;

import pipe.exceptions.TokenLockedException;

public class TokenSetControllerTest implements Observer
{

	private TokenSetController set;
	private List<TokenView> tokenViewList;
	private TokenView oneTokenView;
	private TokenView twoTokenView;
	private Object activeTokenView;
	//TODO refactor to make tests more clear, ensure some cases haven't been overlooked
	@Before
	public void setUp() throws Exception
	{
		set = new TokenSetController(); 
	}
	@Test
	public void verifyNewTokenViewSetHasDefaultTokenView() throws Exception
	{
		assertEquals(1, set.getTokenViews().size()); 
		assertTrue(set.getTokenViews().get(0).isEnabled()); 
		assertEquals("Default",set.getTokenViews().get(0).getID()); 
		assertEquals(Color.black,set.getTokenViews().get(0).getColor()); 
	}
	@Test
	public void verifyBlankTokenViewIdOrNullTokenViewIsNotAdded() throws Exception
	{
		assertFalse(set.updateOrAddTokenView(null));
		assertFalse(set.updateOrAddTokenView(new TokenView(true, null, Color.black)));
		assertFalse(set.updateOrAddTokenView(new TokenView(true, " ", Color.black)));
	}
	@Test
 	public void verifyTwoTokensCannotBeAddedWithTheSameIdAndTheSameColor() throws Exception
	{
		assertTrue(set.updateOrAddTokenView(new TokenView(true, "Fred", Color.red))); 
		assertEquals("default deleted",1, set.getTokenViews().size()); 
		assertFalse("same ID and color can't be added twice",
				set.updateOrAddTokenView(new TokenView(true, "Fred", Color.red))); 
	}
	@Test
 	public void verifyTokenCannotBeAddedWithTheSameColorAsOneTokenAndSameIdAsAnother() throws Exception
	{
		assertTrue(set.updateOrAddTokenView(new TokenView(true, "Mary", Color.black))); 
		assertTrue(set.updateOrAddTokenView(new TokenView(true, "Fred", Color.red))); 
		assertEquals(2, set.getTokenViews().size()); 
		assertFalse("can't reuse Color of a previous token (Mary) with same ID as Fred",
				set.updateOrAddTokenView(new TokenView(true, "Fred", Color.black))); 
		assertEquals(2, set.getTokenViews().size()); 
		assertFalse("can't reuse Id of a previous token (Mary) with same color as Fred",
				set.updateOrAddTokenView(new TokenView(true, "Mary", Color.red))); 
		assertEquals(2, set.getTokenViews().size()); 
	}
 	@Test
 	public void verifyFirstTokenViewAddedReplacesOrUpdatesExistingTokenViewAndSubsequentTokenViewsAreAdded() throws Exception
	{
		TokenView defaultTokenView = set.getTokenViews().get(0); 
		set.updateOrAddTokenView(new TokenView(true, "Default1", Color.black)); 
		assertEquals("initial TokenView is always replaced or updated if new one is enabled",
				1, set.getTokenViews().size()); 
		assertEquals(defaultTokenView,set.getTokenViews().get(0));
		set.updateOrAddTokenView(new TokenView(true, "red", Color.red)); 
		assertEquals(2, set.getTokenViews().size()); 
	}
 	@Test
 	public void verifyExistingTokenViewCanBeRenamedWithNewIdKeepingSameColor() throws Exception
	{
		TokenView maryTokenView = new TokenView(true, "Mary", Color.blue); 
		set.updateOrAddTokenView(maryTokenView); 
		assertEquals(1, set.getTokenViews().size()); 
		assertTrue(set.updateOrAddTokenView(new TokenView(true, "MaryTwo", Color.blue))); 
		assertEquals(1, set.getTokenViews().size()); 
		assertEquals("MaryTwo",set.getTokenViews().get(0).getID()); 
		assertEquals(maryTokenView, set.getTokenViews().get(0)); 
	}
	@Test
 	public void verifyExistingTokenViewCanChangeColorKeepingSameId() throws Exception
	{
		TokenView blueTokenView = new TokenView(true, "Mary", Color.blue); 
		set.updateOrAddTokenView(blueTokenView); 
		TokenView redTokenView = new TokenView(true, "Mary", Color.red); 
		assertTrue(set.updateOrAddTokenView(redTokenView)); 
		assertEquals(blueTokenView, set.getTokenViews().get(0)); 
		assertEquals("same token view, different color",Color.red, blueTokenView.getColor()); 
		assertEquals(1, set.getTokenViews().size()); 
	}
	@Test
 	public void verifyDefaultReplacedIfMismatchesColorAndId() throws Exception
	{
		TokenView newTokenView = new TokenView(true, "Mary", Color.blue); 
		assertTrue(set.updateOrAddTokenView(newTokenView)); 
		assertEquals(1, set.getTokenViews().size()); 
		assertEquals(newTokenView,set.getTokenViews().get(0));
	}
	@Test
 	public void verifyDefaultUpdatedIfOnlyMismatchesColor() throws Exception
	{
		TokenView defaultTokenView = set.getTokenViews().get(0); 
		TokenView newTokenView = new TokenView(true, "Default", Color.blue); 
		assertTrue(set.updateOrAddTokenView(newTokenView)); 
		assertEquals(1, set.getTokenViews().size()); 
		assertEquals(defaultTokenView,set.getTokenViews().get(0));
		assertEquals(Color.blue,set.getTokenViews().get(0).getColor());
	}
	@Test
 	public void verifyDefaultUpdatedIfOnlyMismatchesId() throws Exception
	{
		TokenView defaultTokenView = set.getTokenViews().get(0); 
		TokenView newTokenView = new TokenView(true, "Newname", Color.black); 
		assertTrue(set.updateOrAddTokenView(newTokenView)); 
		assertEquals(1, set.getTokenViews().size()); 
		assertEquals(defaultTokenView,set.getTokenViews().get(0));
		assertEquals("Newname",set.getTokenViews().get(0).getID());
	}
	@Test
 	public void verifyIfFirstAddedTokenHasDefaultIdAndColorItWillNotReplaceOriginalDefaultEvenThoughDuplicateName() throws Exception
	{
		TokenView defaultTokenView = set.getTokenViews().get(0); 
		TokenView firstAddedTokenView = new TokenView(true, "Default", Color.black); 
		assertFalse("duplicate so doesn't replace",
				set.updateOrAddTokenView(firstAddedTokenView)); 
		assertEquals(1, set.getTokenViews().size()); 
		assertEquals(defaultTokenView,set.getTokenViews().get(0));
	}
	@Test
	public void verifyDisabledTokenCanBeEnabled() throws Exception
	{
		oneTokenView = new TokenView(true, "red", Color.red);
		set.updateOrAddTokenView(oneTokenView); 
		assertEquals(1, set.getAllTokenViews().size()); 
		assertEquals(oneTokenView, set.getActiveTokenView()); 
		twoTokenView = new TokenView(false, "blue", Color.blue);
		set.updateOrAddTokenView(twoTokenView); 
		assertEquals(2, set.getAllTokenViews().size()); 
		assertEquals(1, set.getTokenViews().size()); 
		assertTrue(set.updateOrAddTokenView(new TokenView(true, "blue", Color.blue))); 
		assertEquals(2, set.getAllTokenViews().size()); 
		assertEquals(2, set.getTokenViews().size()); 
		assertEquals(twoTokenView, set.getTokenViews().get(1)); 
		assertTrue(twoTokenView.isEnabled()); 
	}
	@Test
	public void verifyEnabledTokenCanBeDisabledIfNotYetLocked() throws Exception
	{
		oneTokenView = new TokenView(true, "red", Color.red);
		set.updateOrAddTokenView(oneTokenView); 
		assertEquals(1, set.getAllTokenViews().size()); 
		assertEquals(oneTokenView, set.getActiveTokenView()); 
		twoTokenView = new TokenView(true, "blue", Color.blue);
		set.updateOrAddTokenView(twoTokenView); 
		assertEquals(2, set.getAllTokenViews().size()); 
		assertEquals(2, set.getTokenViews().size()); 
		assertTrue(set.updateOrAddTokenView(new TokenView(false, "blue", Color.blue))); 
		assertEquals(2, set.getAllTokenViews().size()); 
		assertEquals("dropped from enabled token views",1, set.getTokenViews().size()); 
		assertEquals("in all token views",twoTokenView, set.getAllTokenViews().get(1)); 
		assertFalse(twoTokenView.isEnabled()); 
	}
	@Test
	public void verifyThrowsIfAttemptToDisableLockedToken() throws Exception
	{
		oneTokenView = new TokenView(true, "red", Color.red);
		set.updateOrAddTokenView(oneTokenView); 
		assertEquals(1, set.getAllTokenViews().size()); 
		assertEquals(oneTokenView, set.getActiveTokenView()); 
		twoTokenView = new TokenView(true, "blue", Color.blue);
		twoTokenView.incrementLock();
		set.updateOrAddTokenView(twoTokenView); 
		try 
		{
			set.updateOrAddTokenView(new TokenView(false, "blue", Color.blue)); 
			fail("should throw");
		}
		catch (TokenLockedException e)
		{
		}
		assertEquals(2, set.getTokenViews().size()); 
		assertTrue("still enabled",twoTokenView.isEnabled()); 
	}
	@Test
	public void verifyIdCanChangeWhileTokenViewIsGoingEnabled() throws Exception
	{
		oneTokenView = new TokenView(true, "red", Color.red);
		set.updateOrAddTokenView(oneTokenView); 
		assertEquals(1, set.getAllTokenViews().size()); 
		assertEquals(oneTokenView, set.getActiveTokenView()); 
		twoTokenView = new TokenView(false, "blue", Color.blue);
		set.updateOrAddTokenView(twoTokenView); 
		assertEquals(2, set.getAllTokenViews().size()); 
		assertEquals(1, set.getTokenViews().size()); 
		assertTrue(set.updateOrAddTokenView(new TokenView(true, "newblueId", Color.blue))); 
		assertEquals(2, set.getAllTokenViews().size()); 
		assertEquals(2, set.getTokenViews().size()); 
		assertEquals(twoTokenView, set.getTokenViews().get(1)); 
		assertTrue(twoTokenView.isEnabled()); 
		assertEquals("newblueId", twoTokenView.getID());
	}
	@Test
	public void verifyColorCanChangeWhileTokenViewIsGoingEnabled() throws Exception
	{
		oneTokenView = new TokenView(true, "red", Color.red);
		set.updateOrAddTokenView(oneTokenView); 
		assertEquals(1, set.getAllTokenViews().size()); 
		assertEquals(oneTokenView, set.getActiveTokenView()); 
		twoTokenView = new TokenView(false, "blue", Color.blue);
		set.updateOrAddTokenView(twoTokenView); 
		assertEquals(2, set.getAllTokenViews().size()); 
		assertEquals(1, set.getTokenViews().size()); 
		assertTrue(set.updateOrAddTokenView(new TokenView(true, "blue", Color.green))); 
		assertEquals(2, set.getAllTokenViews().size()); 
		assertEquals(2, set.getTokenViews().size()); 
		assertEquals(twoTokenView, set.getTokenViews().get(1)); 
		assertTrue(twoTokenView.isEnabled()); 
		assertEquals(Color.green, twoTokenView.getColor());
	}
	@Test
 	public void verifyTokenViewsReturnsOnlyEnabledTokenViewsUnlessAllRequested() throws Exception
	{
		set.updateOrAddTokenView(new TokenView(true, "red", Color.red)); 
		set.updateOrAddTokenView(new TokenView(false, "blue", Color.blue)); 
		set.updateOrAddTokenView(new TokenView(false, "black", Color.black)); 
		assertEquals(1, set.getTokenViews().size()); 
		assertEquals(3, set.getAllTokenViews().size()); 
	}
	@Test
 	public void verifyDefaultTokenViewIsOnlyReplacedOnceAnEnabledTokenViewIsAdded() throws Exception
	{
		TokenView defaultTokenView = set.getTokenViews().get(0); 
		set.updateOrAddTokenView(new TokenView(false, "blue", Color.blue)); 
		assertEquals(1, set.getTokenViews().size()); 
		assertEquals(2, set.getAllTokenViews().size()); 
		assertEquals("not replaced, because added TokenView is not enabled",
				defaultTokenView,set.getTokenViews().get(0));
		set.updateOrAddTokenView(new TokenView(true, "Default", Color.red)); 
		assertEquals("default has been dropped in favor of enabled ; no net change",1, set.getTokenViews().size()); 
		assertEquals(2, set.getAllTokenViews().size()); 
		assertEquals("default now replaced",
				defaultTokenView,set.getTokenViews().get(0));
		assertEquals(Color.red, set.getTokenViews().get(0).getColor()); 
	}
	@Test
 	public void verifyTracksCurrentActiveTokenView() throws Exception
	{
		set.addObserver(this); 
		assertEquals("Default",set.getActiveTokenView().getID());
		TokenView fredTokenView = new TokenView(true, "Fred", Color.black); 
		activeTokenView = null;  
		set.updateOrAddTokenView(fredTokenView); 
		assertEquals("not the same as fredTokenView, because first added tokenView causes a rename, not a replacement",
				set.getTokenView("Fred"), set.getActiveTokenView()); 
		assertEquals("when initial default is replaced, the replacement automatically becomes active",
				"Fred",set.getActiveTokenView().getID());
		TokenView maryTokenView = new TokenView(true, "Mary", Color.blue); 
		set.updateOrAddTokenView(maryTokenView); 
		assertEquals("Fred still active","Fred",set.getActiveTokenView().getID());
		activeTokenView = maryTokenView; 
		set.setActiveTokenView("Mary"); 
		assertEquals("Mary explicitly set as active","Mary",set.getActiveTokenView().getID());
		set.setActiveTokenView("maRy "); 
		assertEquals("If identified with blanks or different case, original will still be returned",
				"Mary",set.getActiveTokenView().getID());
		activeTokenView = set.getTokenView("Fred"); 
		set.updateOrAddTokenView(new TokenView(false, "Mary", Color.blue)); 
		assertEquals("When active becomes disabled, active shifts to first remaining active TV",
				"Fred",set.getActiveTokenView().getID());

	}
	@Test
 	public void verifyThrowsIfInvalidTokenViewSetAsActive() throws Exception
	{
		checkIllegalArgumentReceivedForInvalidTokenViewId("Fred","Id Fred not added yet, so invalid"); 
		checkIllegalArgumentReceivedForInvalidTokenViewId(null,"Can't add null token view"); 
	}
	@Test
	// FIXME
	public void verifyThrowsIfReplacementListOfTokenViewsCantBeReconciledWithAnyExistingEntry() throws Exception
	{
		assertTrue(set.updateOrAddTokenView(new TokenView(true, "Mary", Color.blue))); 
		assertEquals(1,set.getAllTokenViews().size());
		tokenViewList = buildTokenViewList(new TokenView(false, "Fred", Color.yellow), 
				new TokenView(true, "Sam", Color.red), new TokenView(false, "Frank", Color.green) ); 
		try 
		{
			set.updateOrReplaceTokenViews(tokenViewList); 
			fail("should throw"); 
		}
		catch (IllegalArgumentException e) 
		{ 
			// Mary not replaced; can't determine whether Fred, Sam and Frank should replace or add 
		}
	}
	@Test
	public void verifyListOfTokenViewsIsKeptFirstInFirstOut() throws Exception
	{
		set.updateOrAddTokenView(new TokenView(true, "Mary", Color.blue)); 
		set.updateOrAddTokenView(new TokenView(false, "Sam", Color.red)); 
		set.updateOrAddTokenView(new TokenView(true, "Frank", Color.green)); 
		set.updateOrAddTokenView(new TokenView(true, "Louise", Color.yellow)); 
		checkListPosition(0, 0, "Mary"); 
		checkListPosition(1, -1, "Sam"); 
		checkListPosition(2, 1, "Frank"); 
		checkListPosition(3, 2, "Louise"); 
	}
	private void checkListPosition(int allIndex, int activeIndex, String Id)
	{
		assertEquals(Id+" not in expected position in all token views",
				Id,set.getAllTokenViews().get(allIndex).getID());
		if (activeIndex >= 0) assertEquals(Id+" not in expected position in active token views",
				Id,set.getTokenViews().get(activeIndex).getID());
	}
	@Test
	public void verifyThrowsAndOriginalListStaysIntactIfInvalidEntriesInReplacementList() throws Exception
	{
		TokenView defaultTokenView = set.getTokenViews().get(0); 
		tokenViewList = buildTokenViewList(new TokenView(true, "", Color.black), 
				new TokenView(true, "Sam", Color.black), null ); 
		try 
		{
				set.updateOrReplaceTokenViews(tokenViewList); 
		}
		catch (IllegalArgumentException e)
		{
			assertEquals("invalid entries cause processing to stop even though one entry is actuall valid",
					TokenSetController.NEW_TOKENVIEW_LIST_HAS_CONFLICTING_OR_INVALID_ENTRIES, e.getMessage()); 
		}
		assertEquals(1,set.getTokenViews().size());
		assertEquals("original set not replaced",defaultTokenView,set.getActiveTokenView()); 
	}
	@Test
	public void verifyAllMembersOfListCanChangeBothIdAndColorRetainingOriginalTokenViewsIfDoneInSteps() throws Exception
	{
		TokenView oneTokenView = set.getTokenViews().get(0); 
		assertTrue(set.updateOrAddTokenView(new TokenView(true, "Alpha", Color.black)));
		assertEquals(oneTokenView, set.getTokenViews().get(0)); 
		assertEquals("Alpha", set.getTokenViews().get(0).getID()); 
		TokenView twoTokenView = new TokenView(true, "Beta", Color.blue);
		assertTrue(set.updateOrAddTokenView(twoTokenView));
		assertEquals(2,set.getTokenViews().size());
		
		tokenViewList = buildTokenViewList(new TokenView(true, "Alpha", Color.green), 
				new TokenView(true, "Delta", Color.blue)); 
		assertTrue("changes should have been made retaining original token views",
				set.updateOrReplaceTokenViews(tokenViewList)); 
		assertEquals(oneTokenView, set.getTokenViews().get(0)); 
		assertEquals(Color.green, set.getTokenViews().get(0).getColor()); 
		assertEquals(twoTokenView, set.getTokenViews().get(1)); 
		assertEquals("Delta", set.getTokenViews().get(1).getID()); 

		tokenViewList = buildTokenViewList(
				new TokenView(true, "Charlie", Color.green), 
				new TokenView(true, "Delta", Color.black)
				);  // order in the list does not matter, as long as changes are unambiguous (one element at a time)
		assertTrue("changes should have been made retaining original token views",
				set.updateOrReplaceTokenViews(tokenViewList)); 
		assertEquals(oneTokenView, set.getTokenViews().get(0)); 
		assertEquals("Charlie", set.getTokenViews().get(0).getID()); 
		assertEquals(Color.green, set.getTokenViews().get(0).getColor()); 
		assertEquals(twoTokenView, set.getTokenViews().get(1)); 
		assertEquals(Color.black, set.getTokenViews().get(1).getColor()); 
		assertEquals("Delta", set.getTokenViews().get(1).getID()); 
		
		tokenViewList = buildTokenViewList(
				new TokenView(true, "Delta", Color.black), 
				new TokenView(true, "Charlie", Color.blue) 
				);  // order in the list does not matter, as long as changes are unambiguous (one element at a time)
		assertTrue("changes should have been made retaining original token views",
				set.updateOrReplaceTokenViews(tokenViewList)); 
		assertEquals(oneTokenView, set.getTokenViews().get(0)); 
		assertEquals("Charlie", set.getTokenViews().get(0).getID()); 
		assertEquals(Color.blue, set.getTokenViews().get(0).getColor()); 
		assertEquals(twoTokenView, set.getTokenViews().get(1)); 
		assertEquals(Color.black, set.getTokenViews().get(1).getColor()); 
		assertEquals("Delta", set.getTokenViews().get(1).getID()); 
		assertEquals(2,set.getTokenViews().size());
		assertEquals(2,set.getAllTokenViews().size());
		
	}
	@Test
	public void verifyAllMembersOfListWillNotChangeBothIdAndColorRetainingOriginalTokenViewsIfDoneInOneStep() throws Exception
	{ 
		buildTwoTokenViewsForUpdateOrReplacement();

		tokenViewList = buildTokenViewList(
				new TokenView(true, "Delta", Color.black),
				new TokenView(true, "Charlie", Color.blue)
		);  // order in the list does not matter, as long as changes are unambiguous (one element at a time)
		assertTrue("original token views updated",
				set.updateOrReplaceTokenViews(tokenViewList)); 
		assertEquals(oneTokenView, set.getTokenViews().get(0)); 
		assertEquals(Color.black, set.getTokenViews().get(0).getColor()); 
		assertEquals("Delta", set.getTokenViews().get(0).getID()); 
		assertEquals(twoTokenView, set.getTokenViews().get(1)); 
		assertEquals("Charlie", set.getTokenViews().get(1).getID()); 
		assertEquals(Color.blue, set.getTokenViews().get(1).getColor()); 
	}
	protected void buildTwoTokenViewsForUpdateOrReplacement() throws Exception
	{
		oneTokenView = set.getTokenViews().get(0); 
		assertTrue(set.updateOrAddTokenView(new TokenView(true, "Alpha", Color.black)));
		assertEquals(oneTokenView, set.getTokenViews().get(0)); 
		assertEquals("Alpha", set.getTokenViews().get(0).getID()); 
		twoTokenView = new TokenView(true, "Beta", Color.blue);
		assertTrue(set.updateOrAddTokenView(twoTokenView));
		assertEquals(2,set.getTokenViews().size());
	}
	@Test
	public void verifyThrowsIfIdAndColorForMultipleListItemsAreBothChanging() throws Exception
	{
		buildTwoTokenViewsForUpdateOrReplacement();
		
		tokenViewList = buildTokenViewList(
				new TokenView(true, "Delta", Color.green),
				new TokenView(true, "Charlie", Color.red)
				); 
		try
		{
			set.updateOrReplaceTokenViews(tokenViewList); 
			fail("should throw"); 
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(TokenSetController.NEW_TOKENVIEW_LIST_HAS_CONFLICTING_OR_INVALID_ENTRIES, e.getMessage()); 
		}
	}
	@Test
	public void verifyDisablingTokenViewsMarkingViewToUpdateTheirReferenceToNull() throws Exception
	{	
		buildTwoTokenViewsForUpdateOrReplacement();
		MarkingView oneMarkingView = new MarkingView(oneTokenView, 1); 
		MarkingView twoMarkingView = new MarkingView(twoTokenView, 1); 
		TokenView newTwoTokenView = new TokenView(false, "Beta", Color.blue); 
		tokenViewList = buildTokenViewList(newTwoTokenView); 
		assertTrue("second token views replaced with null",
				set.updateOrReplaceTokenViews(tokenViewList)); 
		assertEquals(oneTokenView, oneMarkingView.getToken()); 
		assertNull(twoMarkingView.getToken()); 
	}
	@Test
	public void verifyTokenViewRetrievedById() throws Exception
	{	
		assertEquals("Default",set.getTokenView("Default").getID()); 
		assertNull(set.getTokenView("DoesntExist")); 
	}
	private List<TokenView> buildTokenViewList(TokenView... tokenViews)
	{
		List<TokenView> tokenViewList = new ArrayList<TokenView>(); 
		for (int i = 0; i < tokenViews.length; i++)
		{
			tokenViewList.add(tokenViews[i]); 
		}
		return tokenViewList;
	}
	private void checkIllegalArgumentReceivedForInvalidTokenViewId(
			String tokenViewId, String comment)
	{
		try 
		{
			set.setActiveTokenView(tokenViewId); 
			fail("should throw"); 
		}
		catch (IllegalArgumentException e)
		{
			assertEquals(comment,TokenSetController.NO_TOKEN_VIEW_FOUND_WITH_ID+tokenViewId, e.getMessage());
		}
	}
	@Override
	public void update(Observable o, Object arg)
	{
		if (activeTokenView != null) assertEquals(arg, activeTokenView); 
	}
	
	
}
