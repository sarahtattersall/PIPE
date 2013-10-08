/**
 * ResultNode
 * 
 * This is the "?" node that stands for the final result of the performance
 * tree query. It only has one subnode, which is either a ";" node or an 
 * operation node.
 * 
 * @author Tamas Suto
 * @date 25/04/07
 */

package pipe.modules.queryeditor.gui.performancetrees.operationnodes;

import java.util.ArrayList;

import pipe.common.PetriNetNode;
import pipe.modules.interfaces.QueryConstants;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeNode;

public class ResultNode extends OperationNode
{

	public ResultNode(final double positionXInput, final double positionYInput, final String idInput) {
		super(positionXInput, positionYInput, idInput);
		initialiseNode();
	}

	public ResultNode(final double positionXInput, final double positionYInput) {
		super(positionXInput, positionYInput);
		initialiseNode();
	}

	private void initialiseNode()
	{
		// set name and node type
		setName("ResultNode");
		setNodeType(PetriNetNode.RESULT);

		// only one subnode
		setRequiredArguments(1);

		// and nothing else
		setMaxArguments(1);

		// set up required arguments of node
		initialiseRequiredChildNodes();

		// indicate that we don't want labels on the arcs
		this.showArcLabels = false;

		// set up outgoing arcs (implemented in OperationNode)
		setupOutgoingArcs();
	}

	private void initialiseRequiredChildNodes()
	{
		ArrayList requiredNodeTypes = new ArrayList();
		requiredNodeTypes.add(QueryConstants.MULTI_TYPE);
		requiredNodeTypes.add(QueryConstants.DENS_TYPE);
		requiredNodeTypes.add(QueryConstants.DIST_TYPE);
		requiredNodeTypes.add(QueryConstants.STATES_TYPE);
		requiredNodeTypes.add(QueryConstants.ACTIONS_TYPE);
		requiredNodeTypes.add(QueryConstants.NUM_TYPE);
		requiredNodeTypes.add(QueryConstants.RANGE_TYPE);
		requiredNodeTypes.add(QueryConstants.BOOL_TYPE);
		requiredNodeTypes.add(QueryConstants.FUNC_TYPE);
		requiredNodeTypes.add(QueryConstants.MACRO_TYPE);

		setRequiredChildNode(QueryConstants.resultQuery, requiredNodeTypes);
	}

	public static String getTooltip()
	{
		return "Result  (the overall output of the query)";
	}

	public static String getNodeInfo()
	{
		return "";
	}

	/**
	 * This method recursively prints out the textual representation of the node
	 * and its children
	 */
	@Override
	public String printTextualRepresentation()
	{
		String description = "";
		ArrayList children = getChildNodes();
		if (children != null)
		{
			PerformanceTreeNode child = (PerformanceTreeNode) children.get(0);
			String childsReturnType = child.getReturnType();
			if (childsReturnType.equals(QueryConstants.BOOL_TYPE))
			{
				description = QueryManager.addColouring("Is it true that ");
			}
			else
			{
				description = QueryManager.addColouring("What is ");
			}
//			if (!child.getNodeType().equals(PetriNetNode.SEQUENTIAL))
//				QueryManager.colourUp();
			description += child.printTextualRepresentation();
			QueryManager.resetTextColour();
			description += QueryManager.addColouring(" ?");
		}
		return "''" + description + "''";
	}

}
