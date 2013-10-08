package pipe.modules.passage;

import pipe.common.dataLayer.StateElement;
import pipe.common.dataLayer.StateGroup;
import pipe.gui.Constants;
import pipe.views.*;
import pipe.views.viewComponents.AnnotationNote;
import pipe.views.viewComponents.ArcPathPoint;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;


public class StateViewer extends JLayeredPane implements Constants
{
	private static final long serialVersionUID = 1L;

	private StateGroup activeStateGroup;
	private ArrayList<ConditionPlaceView> _condPlaceViews;
	private JDialog parent;

	public StateViewer()
	{
			setLayout(null);
			setOpaque(true);
			setDoubleBuffered(true);
			setAutoscrolls(true);
			setBackground(ELEMENT_FILL_COLOUR);			
			setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));				
	}
	
	public void setParent(JDialog parent)
	{
		this.parent = parent;	
	}
	
	
	public void drawPetriNet(PetriNetView pnmldata, StateGroup stateGroupData)
	{

		// Create a clone of the PetriNet object as only one GUI can display an instance of a PetriNetViewComponent at a time
		// and it is currently being displayed by the primary window (PetriNetTab)
		PetriNetView stateDataLayer = pnmldata.clone();
		activeStateGroup = stateGroupData;
		_condPlaceViews = new ArrayList<ConditionPlaceView>();
		
		// Iterate through the petri-net objects adding them to the GUI
		Iterator PNObjects = stateDataLayer.getPetriNetObjects();

		while(PNObjects.hasNext())
			insertUI( PNObjects.next() );			
		
		updatePreferredSize();		
	}
	
	void insertUI(Object diffObj)
	{
		if (diffObj!=null  &&  diffObj instanceof PetriNetViewComponent)
			add((PetriNetViewComponent)diffObj);
		
		repaint();
	}
	
	void updatePreferredSize() {
		// iterate over net objects and setPreferredSize() accordingly
		Component[] components=getComponents();
		Dimension d=new Dimension(0,0);
		int x,y;
		
		for(int i=0;i<components.length;i++)
		{
			Rectangle r=components[i].getBounds();
			x=r.x+r.width+100;
			y=r.y+r.height+100;
			if (x>d.width)  d.width =x;
			if (y>d.height) d.height=y;
		}
		setPreferredSize(d);
	}

	
	void add(PetriNetViewComponent currentObj)
	{
		if (currentObj instanceof PlaceView)
		{
			ConditionPlaceView placeView = new ConditionPlaceView((PlaceView)currentObj);
			
			// Set the state group condition associated with the place
			StateElement placeCondition = activeStateGroup.getCondition(placeView.getId());
			if (placeCondition != null)
				placeView.setCondition(placeCondition.getOperator(), placeCondition.getPlaceB());
					

			ConditionPlaceHandler handler = new ConditionPlaceHandler(parent, placeView);
			placeView.addMouseListener(handler);
			placeView.deselect();
					
			super.add(placeView);
			
			setLayer(placeView, DEFAULT_LAYER.intValue() + PLACE_TRANSITION_LAYER_OFFSET);
			placeView.addedToGui(); // this will add the place labels
			
			_condPlaceViews.add(placeView);
		}

		// We ignore the Annotation nodes - these nodes will need further development
		else if (currentObj instanceof AnnotationNote);
			
		else
		{
			currentObj.deselect();
			super.add(currentObj);
			
	
			if (currentObj instanceof ArcPathPoint)
				setLayer(currentObj, DEFAULT_LAYER.intValue() + ARC_POINT_LAYER_OFFSET);
			
			else if (currentObj instanceof ArcView)
				setLayer(currentObj, DEFAULT_LAYER.intValue() + ARC_LAYER_OFFSET);	
			
			else if (currentObj instanceof TransitionView)
				setLayer(currentObj, DEFAULT_LAYER.intValue() + PLACE_TRANSITION_LAYER_OFFSET);
				
			else if (currentObj instanceof AnnotationNote)
				setLayer(currentObj, DEFAULT_LAYER.intValue() + ANNOTATION_LAYER_OFFSET);	
		}
	}
	
	
	/**
	 * This method sets the condition on all places to be equal zero
	 */
	public void setEqualZeroCond()
	{
		for(ConditionPlaceView curPlaceView : _condPlaceViews)
		{
			// Update the 'change buffer' in PassageState
			((StateEditor)parent).addStateElement(curPlaceView.getId(), "=", "0");
			
			// Update the UI
			curPlaceView.setCondition("=", "0");
		}
	}
	

	/**
	 * This method clears the marking on all states
	 */
	public void clearAllCond()
	{
		for(ConditionPlaceView curPlaceView : _condPlaceViews)
		{
			// Update the 'change buffer' with a blank condition; this will remove the condition
			((StateEditor)parent).addStateElement(curPlaceView.getId(), "", "");
			
			// Update the UI
			curPlaceView.removeCondition();
		}
	}
	
	/**
	 * This method sets the condition on each place to be equal its initial marking
	 */
	public void setInitialCond()
	{
		for(ConditionPlaceView curPlaceView : _condPlaceViews)
		{
			String currentMarking = Integer.toString(curPlaceView.getCurrentMarking() );
			((StateEditor)parent).addStateElement(curPlaceView.getId(), "=", currentMarking);
			
			// Update the UI
			curPlaceView.setCondition("=", currentMarking);
		}
	}	
	

}

