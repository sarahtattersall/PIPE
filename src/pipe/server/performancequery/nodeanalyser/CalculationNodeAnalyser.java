/**
 * 
 */
package pipe.server.performancequery.nodeanalyser;

import pipe.common.PetriNetNode;

/**
 * @author dazz
 * 
 */
public abstract class CalculationNodeAnalyser extends NodeAnalyser
{
	CalculationNodeAnalyser(final PetriNetNode type) {
		super(type);
	}
}
