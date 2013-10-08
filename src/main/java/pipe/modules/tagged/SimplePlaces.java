package pipe.modules.tagged;

import pipe.views.PetriNetView;
import pipe.views.PlaceView;

import java.io.Serializable;



public class SimplePlaces implements Serializable
{
	/**
	 *	Simple Places is a simplified version of the pipe.dataLayer.Place class
	 *	Its purpose is to provide a simple serializable object for socket transmission
	 *	to processing clusters.
	 *	The necessary attributes from Places for building a 'mod' file are IDs, current markings and length
	**/
	private static final long serialVersionUID = 1L;

	public String[] ids;
	public int[] marking;
	public int length;
	public boolean[] tagged;
	
	public SimplePlaces(PetriNetView pnmldata)
	{
		int i;
		PlaceView[] places = pnmldata.places();
		
		length = places.length;
		
		ids = new String[length];
		marking = new int[length];
		tagged = new boolean[length];
		
		
		for (i=0; i< length; i++)
		{
			ids[i] = places[i].getId();
			marking[i] = places[i].getCurrentMarkingView().getFirst().getCurrentMarking();

            //FIX THIS
			tagged[i] = true;//places[i].isTagged();
			//if(tagged[i]==true)
			//	System.out.println("Found a tagged place!!!");
		}
	}	
}

