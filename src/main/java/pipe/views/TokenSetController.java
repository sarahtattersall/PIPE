package pipe.views;
  // Although this is a controller, it's currently in the views package because it collaborates closely with TokenView.   
  // It could be moved, if some TokenView methods were changed from protected to public.  
import java.awt.Color;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import pipe.exceptions.TokenLockedException;


/**
 * Controller for the set of TokenViews used in a given {@link PetriNetView}.
 * <p>
 * At construction, the set is always given a default TokenView (enabled, id="Default", black color).   
 * The contents of the set may be updated by one or more TokenViews, e.g., built from a GUI or from a PNML file. 
 * <p> 
 * The normal use cases are only interested in enabled TokenViews, so getTokenViews() only returns enabled TokenViews.
 * Method getAllTokenViews() returns both enabled and disable TokenViews; currently only of interest when editing the list of TokenViews. 
 * @author stevedoubleday
 *
 */

public class TokenSetController extends Observable
{

	private static final String DEFAULT = "default";
	protected static final String NO_TOKEN_VIEW_FOUND_WITH_ID = "TokenSetController.setActiveTokenView: no TokenView found with ID ";
	protected static final String NEW_TOKENVIEW_LIST_HAS_CONFLICTING_OR_INVALID_ENTRIES = "TokenSetController.setTokenViews: new list of TokenViews has conflicting or invalid entries.  Current set not replaced.\n" +
			"Consider making changes one at a time.";
	protected static final String NEW_TOKENVIEW_MATCHES_ID_AND_COLOR_OF_TWO_EXISTING_TOKENVIEWS = "TokenSetController.updateOrAddTokenView: new TokenView matches the Id of one existing TokenView and the Color of another existing TokenView, and has a different enabled status.  No updates were made.\nDetails: ";
	protected static final String CANT_DISABLE_LAST_ENABLED_TOKENVIEW = "TokenSetController.updateOrAddTokenView: new TokenView attempts to disable last remaining enabled TokenView.  No updates were made.";
	private boolean onlyEnabledTokenViewIsInitialDefault;
	private Map<String,TokenView> mapIdsToTokenViews;
	private Map<Color, TokenView> mapColorsToTokenViews;
	private String currentId;
	private TokenView activeTokenView;
	private TokenView existingIdTokenView;
	private TokenView existingColorTokenView;
	private TokenView candidateStatusChangeTokenView;
	private List<TokenView> enabledTokenViews;
	private List<TokenView> allTokenViews;
	private TokenSetController tempController;
	private boolean duplicateAllowed;

	/**
	 * Creates a TokenSetController with a default TokenView:  enabled, id="Default", black color.  
	 */
	public TokenSetController()
	{
		duplicateAllowed = false; 
		mapIdsToTokenViews = new Hashtable<String, TokenView>(); 
		mapColorsToTokenViews = new Hashtable<Color, TokenView>(); 
		enabledTokenViews = new LinkedList<TokenView>(); 
		allTokenViews = new LinkedList<TokenView>(); 
		addDefaultTokenViewToMaps(); 
		onlyEnabledTokenViewIsInitialDefault = true; 
	}
	private void addDefaultTokenViewToMaps()
	{
		TokenView defaultTokenView = new TokenView(true, "Default", Color.BLACK);
		buildCurrentId(defaultTokenView);
		try
		{
			addOrUpdateMapsAndLists(defaultTokenView);
		}
		catch (TokenLockedException e)
		{
			e.printStackTrace();  // bug if end up here.
		}
		setActiveTokenView(DEFAULT); 
	}
	/**
	 * Sets the active TokenView to be the one whose ID is the same as argument ID
	 * @param tokenViewId
	 * @throws IllegalArgumentException if there is no TokenView with the argument ID
	 */
	public void setActiveTokenView(String tokenViewId)
	{
		TokenView tokenView = mapIdsToTokenViews.get(normalize(tokenViewId)); 
		if (tokenView == null) throw new IllegalArgumentException(NO_TOKEN_VIEW_FOUND_WITH_ID+tokenViewId);
		else activeTokenView = tokenView; 
		setChanged(); 
		notifyObservers(activeTokenView); 

	}
	/**
	 * Validates a {@link TokenView}, and if valid, adds it to the set (subject to the restriction below on the default TokenView).
	 * A valid TokenView has a non-blank ID that does not match the ID of any other TokenView in the set.
     *
     * The argument will be added as a new TokenView or will update an existing TokenView as follows:
     * <ul>
     * <li>If the argument tokenView matches both the ID and the Color of an existing TokenView, no change is made.  Returns false.
     * <li>If the argument tokenView matches the ID but not the Color of an existing TokenView, then the Color of the existing TokenView is updated to the color of the argument.  Returns true.
     * <li>If the argument tokenView matches the Color but not the ID of an existing TokenView, then the ID of the existing TokenView is updated to the ID of the argument.  Returns true.
     * <li>If the argument tokenView does not match the Color and does not match the ID of an existing TokenView, then the argument tokenView is added to the set of TokenViews.  Returns true. 
     * </ul>
	 * The first valid enabled TokenView (TokenView.isEnabled()) added will replace or update the default TokenView created in the constructor, so the size of the set remains unchanged.  
	 * 
	 * All other valid TokenView additions will be appended to the set, incrementing the size.
	 * @param tokenView 
	 * @return true if added; false if not added (implies tokenView is not valid)
	 * @throws TokenLockedException if an attempt is made to disable a TokenView that is in use by 1 or more PlaceViews 
	 */
	public boolean updateOrAddTokenView(TokenView tokenView) throws TokenLockedException
	{
		if (isNull(tokenView)) return false; 
		buildCurrentId(tokenView);
		if (currentId.isEmpty()) return false; 
		if (onlyEnabledTokenViewIsInitialDefault) return removeOrUpdateInitialDefault(tokenView); 
		else return addOrUpdateMapsAndLists(tokenView); 
	}
	private void buildCurrentId(TokenView tokenView)
	{
		currentId = tokenView.getNormalizedID();
	}
	private boolean addOrUpdateMapsAndLists(TokenView tokenView) throws TokenLockedException 
	{ 
		extractExistingTokenViewsFromMaps(tokenView);
		if (matchesExistingIdAndColorOfSingleTokenView(tokenView)) 
		{
			if (sameEnabledStatus(tokenView)) return duplicateAllowed; 
			changeEnabledStatus(tokenView); 
		}
		else if (matchesExistingIdAndColorOfTwoDifferentTokenViews(tokenView))
		{
			if (sameEnabledStatus(tokenView)) return duplicateAllowed; 
			else throw new IllegalArgumentException(NEW_TOKENVIEW_MATCHES_ID_AND_COLOR_OF_TWO_EXISTING_TOKENVIEWS+tokenView.toString()); 
		}
		else if (matchesExistingId()) 
		{
			changeColor(tokenView); 
		}
		else if (matchesExistingColor(tokenView)) 
		{
			rename(tokenView);
		}
		else
		{
			addToMapsAndLists(tokenView); 
			if (onlyEnabledTokenViewIsInitialDefault)
			{
				removeInitialDefaultTokenView();
			}
		}
		if (!sameEnabledStatus(tokenView)) changeEnabledStatus(tokenView); 
			
		return true;
	}
	private void changeEnabledStatus(TokenView tokenView) throws TokenLockedException
	{
		if (candidateStatusChangeTokenView != null)
		{
			boolean enable = tokenView.isEnabled(); 
			if (enable)
			{
				candidateStatusChangeTokenView.setEnabled(enable);
				enabledTokenViews.add(candidateStatusChangeTokenView);
			}
			else
			{
				if (enabledTokenViews.size() < 2) throw new IllegalArgumentException(CANT_DISABLE_LAST_ENABLED_TOKENVIEW);
				boolean isActive = candidateStatusChangeTokenView.equals(getActiveTokenView()); 
				candidateStatusChangeTokenView.disableAndNotifyObservers(); 
				enabledTokenViews.remove(candidateStatusChangeTokenView);
				if (isActive) shiftActiveToFirstEnabledTokenView(); 
			}
		}
	}
	private void shiftActiveToFirstEnabledTokenView()
	{
		setActiveTokenView(enabledTokenViews.get(0).getID()); 
	}
	private boolean sameEnabledStatus(TokenView tokenView)
	{
//		if ((existingIdTokenView == null) && (existingColorTokenView == null)) return false;
		boolean sameColorTokenStatus = (existingColorTokenView != null) ? (existingColorTokenView.isEnabled() == tokenView.isEnabled()) : true; 
		boolean sameIdTokenStatus = (existingIdTokenView != null) ? (existingIdTokenView.isEnabled() == tokenView.isEnabled()) : true;
		return (sameColorTokenStatus && sameIdTokenStatus);
	}
	private void removeInitialDefaultTokenView()
	{
		mapColorsToTokenViews.remove(Color.black);
		mapIdsToTokenViews.remove(DEFAULT); 
		allTokenViews.remove(0);
		enabledTokenViews.remove(0);
	}
	private void addToMapsAndLists(TokenView tokenView)
	{
		mapIdsToTokenViews.put(currentId, tokenView);
		mapColorsToTokenViews.put(tokenView.getColor(), tokenView);
		allTokenViews.add(tokenView);
		if (tokenView.isEnabled()) enabledTokenViews.add(tokenView); 
	}
	private boolean removeOrUpdateInitialDefault(TokenView tokenView) throws TokenLockedException
	{
		boolean result = false; 
		if (tokenView.isEnabled())
		{
			result = addOrUpdateMapsAndLists(tokenView); 
			onlyEnabledTokenViewIsInitialDefault = false; 
			setActiveTokenView(tokenView.getID());
		}
		else
		{
			addToMapsAndLists(tokenView); 
			result = true;
		}
		return result; 
	}
	private boolean matchesExistingColor(TokenView tokenView)
	{
		boolean colorMatch = (existingColorTokenView != null); 
		if (colorMatch) candidateStatusChangeTokenView = existingColorTokenView; 
		return colorMatch;
	}
	protected boolean matchesExistingId()
	{
		boolean idMatch = (existingIdTokenView != null);
		if (idMatch) candidateStatusChangeTokenView = existingIdTokenView; 
		return idMatch;  
	}
	private void extractExistingTokenViewsFromMaps(TokenView tokenView)
	{
		candidateStatusChangeTokenView = null; 
		existingIdTokenView = mapIdsToTokenViews.get(currentId);  
		existingColorTokenView = mapColorsToTokenViews.get(tokenView.getColor()); 
	}
	private boolean matchesExistingIdAndColorOfSingleTokenView(TokenView tokenView)
	{
		boolean allMatch = (matchesExistingIdAndColor() && (existingIdTokenView.equals(existingColorTokenView))); 
		if (allMatch) candidateStatusChangeTokenView = existingIdTokenView; 
		return allMatch;
	}
	private boolean matchesExistingIdAndColorOfTwoDifferentTokenViews(TokenView tokenView)
	{
		return (matchesExistingIdAndColor() && (!existingIdTokenView.equals(existingColorTokenView)));
	}
	private boolean matchesExistingIdAndColor()
	{
		return (existingIdTokenView != null) && (existingColorTokenView != null);
	}

	private void changeColor(TokenView tokenView)
	{
		Color tempColor = existingIdTokenView.getColor(); 
		existingIdTokenView.setColor(tokenView.getColor());
		mapColorsToTokenViews.remove(tempColor);
		mapColorsToTokenViews.put(existingIdTokenView.getColor(), existingIdTokenView); 
	}
	private void rename(TokenView tokenView)
	{
		String tempId = existingColorTokenView.getNormalizedID(); 
		existingColorTokenView.setID(tokenView.getID());
		mapIdsToTokenViews.remove(tempId); 
		mapIdsToTokenViews.put(existingColorTokenView.getNormalizedID(), existingColorTokenView); 
	}
	private boolean isNull(TokenView tokenView)
	{
		if ((tokenView == null) || (tokenView.getID() == null)) return true; 
		else return false; 
	}
	/**
	 * Returns the list of TokenViews that are enabled (TokenView.isEnabled()).  
	 * To get the full list of TokenViews, both enabled and disabled, use getAllTokenViews() 
	 * @return
	 * @see TokenSetController#getAllTokenViews()                                                                      
	 */
	public List<TokenView> getTokenViews()
	{
		return enabledTokenViews; 
	}
	/**
	 * Returns all TokenViews, both enabled and disabled.  
	 * To get only the list of enabled TokenViews, use getTokenViews() 
	 * @return
	 * @see TokenSetController#getTokenViews();
	 */
	public List<TokenView> getAllTokenViews()
	{
		return allTokenViews; 
	}
	/**
	 * Returns the active token view set previously by calling setActiveTokenView(String).  By default, the default TokenView created at construction is set as active.   
	 * @return
	 * @see TokenSetController#setActiveTokenView(String)
	 */
	public TokenView getActiveTokenView()
	{
		return activeTokenView;
	}
	/**
	 * Returns the TokenView with the argument Id, or null if no TokenView exists with that Id.  
	 * @param Id
	 * @return
	 */
	public TokenView getTokenView(String Id)
	{
		return mapIdsToTokenViews.get(normalize(Id));
	}
	/**
	 * Attempts to update the set's current TokenViews with the information from the entries in the list of TokenViews in the argument.
	 * <p>
	 * If the update is successful, the current TokenViews are updated with ID or Color of the corresponding TokenView in the argument.
	 * Either the ID or the Color of a TokenView can be changed.  If both are changed, the update will fail, and IllegalArgumentException is thrown. 
	 * <p>
	 * If the update is not successful, the current set of TokenViews is not replaced, and IllegalArgumentException is thrown.   
	 * <p>     
	 * @param tokenViewList
	 * @return true - if update is successful, or false if the current set of TokenViews has been replace replaced with the new set. 
	 * @throws IllegalArgumentException - if the new list has conflicting or invalid entries.  The original list is not replaced.
	 * @throws TokenLockedException  if one or more TokenViews that will be replaced or updated is in use by 1 or more PlaceViews
	 * 
	 */
	public boolean updateOrReplaceTokenViews(List<TokenView> tokenViewList) throws TokenLockedException
	{
		boolean updated = true;
		if (!populateTempControllerOneByOne(tokenViewList))
			throw new IllegalArgumentException(NEW_TOKENVIEW_LIST_HAS_CONFLICTING_OR_INVALID_ENTRIES); 
		if (!populateThisControllerOneByOne(tokenViewList))
			throw new IllegalArgumentException(NEW_TOKENVIEW_LIST_HAS_CONFLICTING_OR_INVALID_ENTRIES); 
		{
//			replaceCurrentSetFromTempController();
//			updated = false; 
		}
		return updated; 
	}
	private boolean populateTempControllerOneByOne(List<TokenView> tokenViewList) throws TokenLockedException
	{
		tempController = new TokenSetController(); 
		return populateControllerOneByOne(tempController, tokenViewList);
	}
	private boolean populateThisControllerOneByOne(List<TokenView> tokenViewList) throws TokenLockedException
	{
		boolean populated = populateControllerOneByOne(this, tokenViewList);
		if ((populated) && numberOfEnabledTokenViewsMismatch()) populated = false; 
		return populated;
	}
	protected boolean populateControllerOneByOne(TokenSetController tokenSetController, List<TokenView> tokenViewList) throws TokenLockedException
	{
		tokenSetController.duplicateAllowed = true; 
		for (TokenView tokenView : tokenViewList)
		{
			if (!tokenSetController.updateOrAddTokenView(tokenView)) return false; 
		}
		tokenSetController.duplicateAllowed = false; 
		return true;
	}
	private boolean numberOfEnabledTokenViewsMismatch()
	{
		return getTokenViews().size() != tempController.getTokenViews().size();
	}
//	private void replaceCurrentSetFromTempController() throws TokenLockedException
//	{ 
//		updateNewTokenViewsFromOriginalTokenViews(); 
//		this.mapIdsToTokenViews = tempController.mapIdsToTokenViews; 
//		this.mapColorsToTokenViews = tempController.mapColorsToTokenViews; 
//		this.enabledTokenViews = tempController.enabledTokenViews; 
//		this.allTokenViews = tempController.allTokenViews; 
//		this.activeTokenView = tempController.activeTokenView; 
//		this.onlyEnabledTokenViewIsInitialDefault = tempController.onlyEnabledTokenViewIsInitialDefault; 
//	}
//	private void updateNewTokenViewsFromOriginalTokenViews() throws TokenLockedException
//	{
//		int size = tempController.allTokenViews.size(); 
//		for (int i = 0; i < size; i++)
//		{
//			tempController.allTokenViews.get(i).updateModelFromPrevious(allTokenViews.get(i)); 
//		}
//	}
	//TODO refactor to some common place; also used by TokenView
	private String normalize(String target)
	{
		if (target == null) return ""; 
		else return target.trim().toLowerCase();	
	}

}
