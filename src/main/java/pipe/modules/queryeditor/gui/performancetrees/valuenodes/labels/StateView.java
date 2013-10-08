/**
 * StateView
 * 
 * Visualises the underlying SPN model in such a way that conditions can be specified 
 * on it
 * 
 * @author Barry Kearns
 * @author Tamas Suto
 * @date 18/08/07
 */


package pipe.modules.queryeditor.gui.performancetrees.valuenodes.labels;

import pipe.common.dataLayer.StateElement;
import pipe.common.dataLayer.StateGroup;
import pipe.modules.interfaces.QueryConstants;
import pipe.views.*;
import pipe.views.viewComponents.AnnotationNote;
import pipe.views.viewComponents.ArcPathPoint;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;


public class StateView extends JLayeredPane implements QueryConstants {

	private static final long serialVersionUID = 1L;

    private StateGroup activeStateGroup;
	private ArrayList<ConditionPlaceView> _condPlaceViews;
	private JDialog parent;

	public StateView() {
		setLayout(null);
		setOpaque(true);
		setDoubleBuffered(true);
		setAutoscrolls(true);
		setBackground(ELEMENT_FILL_COLOUR);			
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));				
	}

	
	public void setParent(JDialog parent) {
		this.parent = parent;	
	}

	/**
	 * Create a clone of the PetriNet object as only one GUI can display an instance of
	 * a PetriNetViewComponent at a time and it is currently being displayed by the primary
	 * window (PetriNetTab)
	 * 
	 * @param pnmldata
	 * @param stateGroupData
	 */
	public void drawPetriNet(PetriNetView pnmldata, StateGroup stateGroupData) {
        PetriNetView stateDataLayer = pnmldata.clone();
		activeStateGroup = stateGroupData;
		_condPlaceViews = new ArrayList<ConditionPlaceView>();

		// Iterate through the petri-net objects adding them to the GUI
		Iterator PNObjects = stateDataLayer.getPetriNetObjects();
		while(PNObjects.hasNext())
			insertUI( PNObjects.next() );			

		updatePreferredSize();		
	}

	void insertUI(Object diffObj) {
		if((diffObj instanceof PetriNetViewComponent) && (diffObj != null))
			add((PetriNetViewComponent)diffObj);
		repaint();
	}

	void updatePreferredSize() {
		// iterate over net objects and setPreferredSize() accordingly
		Component[] components=getComponents();
		Dimension d=new Dimension(0,0);
		int x,y;
        for(Component component : components)
        {
            Rectangle r = component.getBounds();
            x = r.x + r.width + 100;
            y = r.y + r.height + 100;
            if(x > d.width)
                d.width = x;
            if(y > d.height)
                d.height = y;
        }
		setPreferredSize(d);
	}

	void add(PetriNetViewComponent currentObj) {
		if (currentObj instanceof PlaceView) {
			ConditionPlaceView placeView = new ConditionPlaceView((PlaceView)currentObj);
			// Set the state group condition associated with the place
			StateElement placeCondition = activeStateGroup.getCondition(placeView.getId());
			if (placeCondition != null)
				placeView.setCondition(placeCondition.getOperator(), placeCondition.getPlaceB());

			ConditionPlaceHandler handler = new ConditionPlaceHandler(parent, placeView);
			placeView.addMouseListener(handler);
			super.add(placeView);
			setLayer(placeView, DEFAULT_LAYER.intValue() + PLACE_TRANSITION_LAYER_OFFSET);
			placeView.addedToGui(); // this will add the place labels
			_condPlaceViews.add(placeView);
		}
		else {
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
	public void setEqualZeroCond() {
		for(ConditionPlaceView curPlaceView : _condPlaceViews) {
			// Update the 'change buffer' in PassageState
			((StateGroupEditor)parent).addStateElement(curPlaceView.getId(), "=", "0");
			// Update the UI
			curPlaceView.setCondition("=", "0");
		}
	}


	/**
	 * This method clears the marking on all states
	 */
	public void clearAllCond() {
		for(ConditionPlaceView curPlaceView : _condPlaceViews) {
			// Update the 'change buffer' with a blank condition; this will remove the condition
			((StateGroupEditor)parent).addStateElement(curPlaceView.getId(), "", "");
			// Update the UI
			curPlaceView.removeCondition();
		}
	}

	/**
	 * This method sets the condition on each place to be equal its initial marking
	 */
	public void setInitialCond() {
		for(ConditionPlaceView curPlaceView : _condPlaceViews) {
			String currentMarking = Integer.toString(curPlaceView.getCurrentMarking() );
			((StateGroupEditor)parent).addStateElement(curPlaceView.getId(), "=", currentMarking);
			// Update the UI
			curPlaceView.setCondition("=", currentMarking);
		}
	}	
	
	/**
	 * Checks whether at least one condition has been specified on the model
	 * @return
	 */
	public boolean someConditionHasBeenSpecified() {
		boolean conditionHasBeenSpecified = false;
		for(ConditionPlaceView curPlaceView : _condPlaceViews) {
			if (curPlaceView.conditionHasBeenSpecified())
				conditionHasBeenSpecified = true;
		}
		return conditionHasBeenSpecified;
	}

}

