package pipe.server.performancequery.structure;

import java.util.Comparator;

/**
 * Note: this comparator imposes orderings that are inconsistent with equals.
 * 
 * @author dkb03
 * 
 */
public class SubtreeComparator implements Comparator<ParentSubtree>
{

	public int compare(ParentSubtree a, ParentSubtree b)
	{
		return (a.canBeEvaluated() - b.canBeEvaluated());
	}

}
