package pipe.modules.steadyStateCloud;

import pipe.modules.clientCommon.PetriNetBrowsePanel;
import pipe.views.PetriNetView;
import pipe.views.PlaceView;
import pipe.views.TransitionView;

import javax.swing.*;

public class FileBrowserPanel extends PetriNetBrowsePanel
{
	private static final long serialVersionUID = 1L;
	
	public FileBrowserPanel(String title, PetriNetView currNetView){
		super(title, currNetView);
	}

	  private JList placeList = null;
    private JList transitionList = null;
	    
	  public void setPlaceList(JList listName)
	  {
		  placeList = listName;
		  updateUIList();
	  }
	  
	  public void setTransitionList(JList listName)
	  {
		  transitionList = listName;
		  updateUIList();
	  }
	  
	  // This method updates the JList
	  protected void updateUIList()
	  {
		  if (placeList != null)
		  {
			  // Load the list of place names
			  String[] names = getPlaceNames();
			  
			  if (names != null)
				  placeList.setListData(names);
			  else
			  	  placeList.removeAll();	  
		  }
		  
		  if (transitionList != null)
		  {
			  // Load the list of transition names
			  String[] names = getTransitionNames();
			  
			  if (names != null)
				  transitionList.setListData(names);
			  else
				  transitionList.removeAll();		  
		  }	
	  }
	  
	  private String[] getPlaceNames()
	  {
		  int i;
		  String[] names = null;
		  
		  if (_selectedNetView != null)
		  {
			  PlaceView[] placeViews = _selectedNetView.places();
			  int length = placeViews.length;
			  
			  names = new String[length];			
				
			  for (i=0; i< length; i++)
			  		names[i] = placeViews[i].getName();
		  }
		  	  
		  return names;
	  }
	  
	  private String[] getTransitionNames()
	  {
		  int i;
		  String[] names = null;
		  
		  if (_selectedNetView != null)
		  {
			  TransitionView[] transitionViews = _selectedNetView.getTransitionViews();

			  int length = transitionViews.length;
			  names = new String[length];			
				
			  for (i=0; i< length; i++)
				  names[i] = transitionViews[i].getName();

		  }
		  	  
		  return names;
	  }

}
