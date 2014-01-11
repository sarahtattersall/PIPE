package pipe.modules.tagged;

import pipe.views.ArcView;
import pipe.views.PetriNetView;
import pipe.views.TransitionView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;



public class SimpleTransitions implements Serializable
{
	/**
	 *	Simple Transitions is a simplified version of the pipe.dataLayer.Transition class
	 *	Its purpose is to provide a simple serializable object for socket transmission
	 *	to processing clusters.
	 *	The necessary attributes from Places for building a '.mod' file are the
	 *	place IDs with the respective IDs of all their To/From transition targets
	**/
	
	private static final long serialVersionUID = 1L;
	
	public String[] ids;
	public boolean[] timed;
	public double[] rate;
	public ArrayList<LinkedList<SimpleArc>> arcsTo, arcsFrom;
	public int length;
	
	

	public SimpleTransitions (PetriNetView pnmldata)
	{
		int i;
		Iterator arcsToIter;
		Iterator arcsFromIter;
		
		
		TransitionView[] transitions;
		transitions = pnmldata.getTransitionViews();
		
		// Declare the objects variables
		length = transitions.length;
		
		ids = new String[length];
		timed = new boolean[length];
		rate = new double[length];
		arcsTo = new ArrayList<LinkedList<SimpleArc>>(length);
		arcsFrom = new ArrayList<LinkedList<SimpleArc>>(length);
		
		
		
		for (i=0; i< length; i++)
		{
			ids[i] = transitions[i].getId();
			timed[i] = transitions[i].isTimed();
			rate[i] = transitions[i].getRate();
			
			
			arcsTo.add(i, new LinkedList<SimpleArc>());			
			arcsFrom.add(i, new LinkedList<SimpleArc>() );
			
			arcsToIter = transitions[i].getConnectToIterator();
			arcsFromIter = transitions[i].getConnectFromIterator();
			
	
			// Create list of all targets from current place 
			while (arcsToIter.hasNext())
			{
				ArcView currentArc = (ArcView) arcsToIter.next();
				SimpleArc newTransArc = new SimpleArc( currentArc.getSource().getId(), currentArc.getSimpleWeight(), currentArc.isTagged());
				arcsTo.get(i).add ( newTransArc );
			}
			
			// Create list of source places to current
			while (arcsFromIter.hasNext())
			{
				ArcView currentArc = (ArcView) arcsFromIter.next();
				SimpleArc newTransArc = new SimpleArc( currentArc.getTarget().getId(), currentArc.getSimpleWeight(), currentArc.isTagged());
				arcsFrom.get(i).add( newTransArc );
			}
		}
	}
	
}