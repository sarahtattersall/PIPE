package pipe.common;

import pipe.views.PetriNetView;
import pipe.views.PlaceView;

import java.io.Serializable;


public class SimplePlaces implements Serializable
{
	/**
	 * Simple Places is a simplified version of the pipe.dataLayer.Place class
	 * Its purpose is to provide a simple serializable object for socket
	 * transmission to processing clusters. The necessary attributes from Places
	 * for building a 'mod' file are IDs, current markings and length
	 */
	private static final long	serialVersionUID	= 1L;

	public final String[]		ids;
	public final int[]			marking;
	public final int			length;
	public final String[]		names;

	public SimplePlaces(final PetriNetView pnmldata) {
		int i;
		final PlaceView[] places = pnmldata.places();

		this.length = places.length;

		this.ids = new String[this.length];
		this.marking = new int[this.length];
		this.names = new String[this.length];

		for (i = 0; i < this.length; i++)
		{
			this.ids[i] = places[i].getId();
			this.names[i] = places[i].getName();
			// TODO: Modified to use getTotalMarking from getCurrentMarking, former returns an int. Could be wrong!
			// This might break things -- we should probably make sure the net is unfolded first, then pass the actual value of tokens,
			// rather than the total of the separate token classes.
			// Update: now that we are passing an unfolded net, it *should* be safe to use TotalMarking(!)
			this.marking[i] = places[i].getTotalMarking();
		}
	}
}
