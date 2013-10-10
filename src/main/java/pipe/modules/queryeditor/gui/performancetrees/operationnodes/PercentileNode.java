/**
 * 
 */
package pipe.modules.queryeditor.gui.performancetrees.operationnodes;

import java.util.ArrayList;
import java.util.Iterator;

import pipe.common.PetriNetNode;
import pipe.modules.interfaces.QueryConstants;
import pipe.handlers.StringHelper;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.NumNode;

/**
 * @author dazz
 * 
 */
public class PercentileNode extends OperationNode
{
	public PercentileNode(final double positionXInput, final double positionYInput, final String idInput) {
		super(positionXInput, positionYInput, idInput);
		initialiseNode();
	}

	public PercentileNode(final double positionXInput, final double positionYInput) {
		super(positionXInput, positionYInput);
		initialiseNode();
	}

	private void initialiseNode()
	{
		// set name and node type
		setName("PercentileNode");
		setNodeType(PetriNetNode.PERCENTILE);

		// only one subnode
		setRequiredArguments(2);

		// and nothing else
		setMaxArguments(2);

		// set up required arguments of node
		initialiseRequiredChildNodes();

		// set return type
		setReturnType(QueryConstants.NUM_TYPE);

		// indicate that we want labels on the arcs
		this.showArcLabels = true;

		// set up outgoing arcs (implemented in OperationNode)
		setupOutgoingArcs();
	}

	private void initialiseRequiredChildNodes()
	{
		setRequiredChildNode(QueryConstants.percentileChildNum, QueryConstants.NUM_TYPE);
		ArrayList<String> requiredChildTypes = new ArrayList<String>();
		requiredChildTypes.add(QueryConstants.DIST_TYPE);
		requiredChildTypes.add(QueryConstants.DENS_TYPE);
		setRequiredChildNode(QueryConstants.percentileChildDensity, requiredChildTypes);
	}

	public static String getTooltip()
	{
		return "Percentile  (Obtains a specific percentile of a distribution)";
	}

	public static String getNodeInfo()
	{
		return QueryManager.addColouring("The Percentile node represents the time value of a passage time distribution or density at a specified probability value, calculated "
											+ "from a passage time distribution.<br><br>"
											+ "The required argument is a passage time distribution or density and a numeric value representing the percentile of interest.<br>"
											+ "The operator returns  the time value corresponding to a probability value (real value)");
	}

	@Override
	public String printTextualRepresentation() {
		StringBuilder description = new StringBuilder();
		String op = " of";
		String numString, passageString;

		ArrayList children = getChildNodes();
		if (children != null) {
			Iterator<PerformanceTreeNode> i = children.iterator();
			while (i.hasNext()) {
				PerformanceTreeNode child = i.next();
				String childsReturnType = child.getReturnType();
				if (childsReturnType.equals(QueryConstants.NUM_TYPE)) {
					// we know we're dealing with the argument that specifies
					// which percentile we want
					if (child instanceof NumNode) {
						if (((NumNode) child).getNumObject() != null) {
							double intNumVal = Double.parseDouble(((NumNode) child).getNumObject().toString());
							String numth = StringHelper.getStringTH(intNumVal);
							description.append(QueryManager.addColouring("the " + numth + " percentile"));
						}
						else {
							description.append(QueryManager.addColouring("the yet unspecified percentile of "));
						}
					}
					else if (child instanceof ArithOpNode) {
						String numth = child.printTextualRepresentation();
						description.append(QueryManager.addColouring("the percentile given by " + numth));
					}
				}
				else if (child.getNodeType() == PetriNetNode.DISTRIBUTION ||
						 child.getNodeType() == PetriNetNode.PASSAGETIMEDENSITY ||
						 child.getNodeType() == PetriNetNode.CONVOLUTION) {
					QueryManager.colourUp();
					description.append(child.printTextualRepresentation());
					QueryManager.colourDown();
				}
				
				if (i.hasNext()) {
                    description.append(QueryManager.addColouring(op)).append(" ");
				} 
				else {
					if (children.size() == 1) {
						if ((child.getNodeType() == PetriNetNode.NUM) || (child.getNodeType() == PetriNetNode.ARITHOP)) {
							description.append(QueryManager.addColouring(op));
							description.append(QueryManager.addColouring(" an undefined passage time density / distribution"));
							QueryManager.colourDown();
						}
						else {
							description.insert(0, QueryManager.addColouring("the yet unspecified percentile of "));
						}
					}
				}
			}
		}
		else {
			description.append(QueryManager.addColouring("the yet unspecified percentile"));
			description.append(QueryManager.addColouring(op));
			description.append(QueryManager.addColouring(" an undefined passage time density / distribution"));
			QueryManager.colourDown();
		}
		return description.toString();
	}

}
