package pipe.modules.tagged;

import java.io.Serializable;


/**
 * This class represents an arc from the transition's perspective.
 * We simply record the target place (placeId) and the weight of the arc to reach it
 */

public class SimpleArc  implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public String placeId;
	public int weight;
	public boolean tagged;
		
	public SimpleArc(String placeId, int weight, boolean isTagged)
	{
		this.placeId = placeId;
		this.weight = weight;
		this.tagged = isTagged;
	}

}
