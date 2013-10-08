/** QueryLoader
 * 
 * This class contains all relevant methods for loading in a query 
 * from an XML file. 
 * 
 * @author Tamas Suto
 * @date 01/08/07
 */

package pipe.modules.queryeditor.io;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import pipe.common.PetriNetNode;
import pipe.modules.interfaces.QueryConstants;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeArc;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeNode;
import pipe.modules.queryeditor.gui.performancetrees.macros.ArgumentNode;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroDefinition;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroManager;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.ArithCompNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.ArithOpNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.ConvolutionNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.DisconNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.DistributionNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.FiringRateNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.InIntervalNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.MomentNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.NegationNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.OperationNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.PassageTimeDensityNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.PercentileNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.ProbInIntervalNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.ProbInStatesNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.RangeNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.ResultNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.SequentialNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.StatesAtTimeNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.SteadyStateProbNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.SteadyStateStatesNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.SubsetNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.ActionsNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.BoolNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.NumNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.StateFunctionNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.StatesNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.ValueNode;

public class QueryLoader implements QueryConstants
{

	private static QueryData		queryData;
	private static boolean			importSuccessful;

	private static MacroDefinition	macro;

	/**
	 * This method loads in a PTML document and creates a query tree on the
	 * canvas
	 * 
	 * @param PTMLDoc
     * @param queryDataInput
     * @return
	 */
	public static QueryData loadQueryFromXML(final Document PTMLDoc, final QueryData queryDataInput)
	{
		// load in current QueryData and set it up for fresh input
		QueryLoader.queryData = queryDataInput;

		try
		{
			// set mode
			QueryManager.setMode(QueryConstants.LOADING);

			// create query tree
			NodeList ptmlElements = PTMLDoc.getDocumentElement().getChildNodes();
			for (int i = 0; i < ptmlElements.getLength(); i++)
			{
				Node ptmlNode = ptmlElements.item(i);
				if (ptmlNode instanceof Element)
				{
					Element ptmlElement = (Element) ptmlNode;
					if ("tree".equals(ptmlElement.getNodeName()))
					{
						NodeList treeElements = ptmlElement.getChildNodes();
						for (int j = 0; j < treeElements.getLength(); j++)
						{
							Node treeNode = treeElements.item(j);
							if (treeNode instanceof Element)
							{
								Element treeElement = (Element) treeNode;
								if ("node".equals(treeElement.getNodeName()))
								{
									QueryLoader.createNode(treeElement);
								}
							}
						}
					}
					else if ("stateLabels".equals(ptmlElement.getNodeName()))
					{
						NodeList stateLabelsElements = ptmlElement.getChildNodes();
						for (int k = 0; k < stateLabelsElements.getLength(); k++)
						{
							Node stateLabelsNode = stateLabelsElements.item(k);
							if (stateLabelsNode instanceof Element)
							{
								Element stateLabelElement = (Element) stateLabelsNode;
								if ("statelabel".equals(stateLabelElement.getNodeName()))
								{
									QueryLoader.createStateLabel(stateLabelElement);
								}
							}
						}
					}
					else if ("actionLabels".equals(ptmlElement.getNodeName()))
					{
						NodeList actionLabelsElements = ptmlElement.getChildNodes();
						for (int l = 0; l < actionLabelsElements.getLength(); l++)
						{
							Node actionLabelsNode = actionLabelsElements.item(l);
							if (actionLabelsNode instanceof Element)
							{
								Element actionLabelElement = (Element) actionLabelsNode;
								if ("actionlabel".equals(actionLabelElement.getNodeName()))
								{
									QueryLoader.createActionLabel(actionLabelElement);
								}
							}
						}
					}
					else if ("macro".equals(ptmlElement.getNodeName()))
					{
						QueryLoader.createMacro(ptmlElement);
					}
				}
			}

			// restore mode
			QueryManager.restoreMode();

			// print NL representation of query
			QueryManager.printNaturalLanguageRepresentation();
			
			return QueryLoader.queryData;

		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Create a Node object from a Node DOM Element
	 * 
	 * @param inputElement -
	 *            Input PerformanceTreeNode DOM Element
	 * @return PerformanceTreeNode Object
	 */
	private static void createNode(final Element inputElement)
	{
		String nodeID = null;
		String nodeType = null;
		String nodeOperation = null;
		String nodeLabel = null;
		double nodePositionX = 0;
		double nodePositionY = 0;
		String nodeIncomingArcID = null;
		ArrayList<String> nodeOutgoingArcIDs = new ArrayList<String>();

		// retrieve info from element's attributes
		String retrievedNodeID = inputElement.getAttribute("id");
		if (retrievedNodeID.length() > 0)
			nodeID = retrievedNodeID;
		String retrievedNodeType = inputElement.getAttribute("type");
		if (retrievedNodeType.length() > 0)
			nodeType = retrievedNodeType;
		String retrievedNodeOperation = inputElement.getAttribute("operation");
		if (retrievedNodeOperation.length() > 0)
			nodeOperation = retrievedNodeOperation;
		String retrievedNodeLabel = inputElement.getAttribute("label");
		if (retrievedNodeLabel.length() > 0)
			nodeLabel = retrievedNodeLabel;
		String retrievedNodePositionX = inputElement.getAttribute("x");
		if (retrievedNodePositionX.length() > 0)
		{
			nodePositionX = Double.valueOf(retrievedNodePositionX).doubleValue() *
							(1) +
							(1);
			nodePositionX += QueryConstants.NODE_HEIGHT / 2 - 1;
		}
		String retrievedNodePositionY = inputElement.getAttribute("y");
		if (retrievedNodePositionY.length() > 0)
		{
			nodePositionY = Double.valueOf(retrievedNodePositionY).doubleValue() *
							(1) +
							(1);
			nodePositionY += QueryConstants.NODE_HEIGHT / 2 - 1;
		}

		// extract incoming and outgoing arc ids
		NodeList nodeElementList = inputElement.getChildNodes();
		for (int i = 0; i < nodeElementList.getLength(); i++)
		{
			Node nodeNode = nodeElementList.item(i);
			if (nodeNode instanceof Element)
			{
				Element nodeElement = (Element) nodeNode;
				if ("incomingArc".equals(nodeElement.getNodeName()))
				{
					String retrievedNodeIncomingArcID = nodeElement.getTextContent();
					if (retrievedNodeIncomingArcID.length() > 0)
						nodeIncomingArcID = retrievedNodeIncomingArcID;
				}
				else if ("outgoingArcs".equals(nodeElement.getNodeName()))
				{
					// extract arc IDs and put them into the array of outgoing
					// arc IDs
					NodeList outgoingArcsElementList = nodeElement.getChildNodes();
					for (int j = 0; j < outgoingArcsElementList.getLength(); j++)
					{
						Node outgoingArcsNode = outgoingArcsElementList.item(j);
						if (outgoingArcsNode instanceof Element)
						{
							Element outgoingArcsElement = (Element) outgoingArcsNode;
							if ("arc".equals(outgoingArcsElement.getNodeName()))
							{
								String outgoingArcID = outgoingArcsElement.getAttribute("id");
								nodeOutgoingArcIDs.add(outgoingArcID);
							}
						}
					}
				}
			}
		}

		// create nodes with the retrieved values
		PerformanceTreeNode node = null;
		if (nodeType.equals(PetriNetNode.RESULT.toString()))
			node = new ResultNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.SEQUENTIAL.toString()))
			node = new SequentialNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.ARITHCOMP.toString()))
			node = new ArithCompNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.ARITHOP.toString()))
			node = new ArithOpNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.CONVOLUTION.toString()))
			node = new ConvolutionNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.DISCON.toString()))
			node = new DisconNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.DISTRIBUTION.toString()))
			node = new DistributionNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.FIRINGRATE.toString()))
			node = new FiringRateNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.ININTERVAL.toString()))
			node = new InIntervalNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.MOMENT.toString()))
			node = new MomentNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.PERCENTILE.toString()))
			node = new PercentileNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.NEGATION.toString()))
			node = new NegationNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.PASSAGETIMEDENSITY.toString()))
			node = new PassageTimeDensityNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.PROBININTERVAL.toString()))
			node = new ProbInIntervalNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.PROBINSTATES.toString()))
			node = new ProbInStatesNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.RANGE.toString()))
			node = new RangeNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.STATESATTIME.toString()))
			node = new StatesAtTimeNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.STEADYSTATEPROB.toString()))
			node = new SteadyStateProbNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.STEADYSTATESTATES.toString()))
			node = new SteadyStateStatesNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.MACRO.toString()))
			node = new MacroNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.SUBSET.toString()))
			node = new SubsetNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.ACTIONS.toString()))
			node = new ActionsNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.BOOL.toString()))
			node = new BoolNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.NUM.toString()))
			node = new NumNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.STATEFUNCTION.toString()))
			node = new StateFunctionNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.STATES.toString()))
			node = new StatesNode(nodePositionX, nodePositionY, nodeID);

		if (nodeIncomingArcID != null)
			node.setIncomingArcID(nodeIncomingArcID);

		// need to add node before checking if it's a value node because if the
		// label is to
		// be drawn then the node has to exist already
		QueryLoader.queryData.addNode(node);

		if (node instanceof OperationNode)
		{
			if (nodeOutgoingArcIDs.size() != 0)
				((OperationNode) node).setOutgoingArcIDs(nodeOutgoingArcIDs);
			if (nodeOperation != null)
			{
				((OperationNode) node).setOperation(nodeOperation);
			}
			if (node instanceof MacroNode)
			{
				if (nodeLabel != null)
					((MacroNode) node).setNodeLabel(nodeLabel);
			}
		}
		else if (node instanceof ValueNode)
		{
			if (nodeLabel != null)
			{
				// set the visible label
				((ValueNode) node).setNodeLabel(nodeLabel);
				if (node instanceof StatesNode)
				{
					// set the state label variable
					((StatesNode) node).setStateLabel(nodeLabel);
				}
				else if (node instanceof NumNode)
				{
					((NumNode) node).setNumValue(Double.valueOf(nodeLabel));
				}
				else if (node instanceof ActionsNode)
				{
					// set the action label variable
					((ActionsNode) node).setActionLabel(nodeLabel);
				}
				else if (node instanceof StateFunctionNode)
				{
					// set the action label variable
					((StateFunctionNode) node).setFunction(nodeLabel);
				}
			}
		}

		// update node to reflect change in label
		QueryLoader.queryData.updateNode(node);

		// create outgoing arcs
		for (int i = 0; i < nodeElementList.getLength(); i++)
		{
			Node nodeNode = nodeElementList.item(i);
			if (nodeNode instanceof Element)
			{
				Element nodeElement = (Element) nodeNode;
				if ("outgoingArcs".equals(nodeElement.getNodeName()))
				{
					NodeList outgoingArcsElementList = nodeElement.getChildNodes();
					for (int j = 0; j < outgoingArcsElementList.getLength(); j++)
					{
						Node outgoingArcsNode = outgoingArcsElementList.item(j);
						if (outgoingArcsNode instanceof Element)
						{
							Element outgoingArcsElement = (Element) outgoingArcsNode;
							if ("arc".equals(outgoingArcsElement.getNodeName()))
							{
								QueryLoader.createArc(outgoingArcsElement);
							}
						}
					}
				}
			}
		}
		// make sure all arcs connect to the node nicely
		node.updateConnected();

		// create child nodes if applicable
		for (int i = 0; i < nodeElementList.getLength(); i++)
		{
			Node nodeNode = nodeElementList.item(i);
			if (nodeNode instanceof Element)
			{
				Element nodeElement = (Element) nodeNode;
				if ("childNodes".equals(nodeElement.getNodeName()))
				{
					NodeList childNodesElementList = nodeElement.getChildNodes();
					for (int j = 0; j < childNodesElementList.getLength(); j++)
					{
						Node childNodesNode = childNodesElementList.item(j);
						if (childNodesNode instanceof Element)
						{
							Element childNodesElement = (Element) childNodesNode;
							if ("node".equals(childNodesElement.getNodeName()))
							{
								QueryLoader.createNode(childNodesElement);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Create a PerformanceTreeArc object from an PerformanceTreeArc DOM Element
	 * 
	 * @param inputElement -
	 *            Input PerformanceTreeArc DOM Element
	 * @return PerformanceTreeArc Object
	 */
	private static void createArc(final Element inputElement)
	{
		String arcID = null;
		String arcLabel = null;
		String arcSourceID = null;
		String arcTargetID = null;
		double arcStartX = 0;
		double arcStartY = 0;
		double arcEndX = 0;
		double arcEndY = 0;
		boolean arcRequired = true;
		boolean labelRequired = true;

		// retrieve info from element's attributes
		String retrievedArcID = inputElement.getAttribute("id");
		if (retrievedArcID.length() > 0)
			arcID = retrievedArcID;
		String retrievedArcLabel = inputElement.getAttribute("label");
		if (retrievedArcLabel.length() > 0)
			arcLabel = retrievedArcLabel;
		String retrievedArcSourceID = inputElement.getAttribute("source");
		if (retrievedArcSourceID.length() > 0)
			arcSourceID = retrievedArcSourceID;
		String retrievedArcTargetID = inputElement.getAttribute("target");
		if (retrievedArcTargetID.length() > 0)
			arcTargetID = retrievedArcTargetID;
		String retrievedArcRequired = inputElement.getAttribute("required");
		if (retrievedArcRequired.length() > 0)
		{
			if (retrievedArcRequired.equals("true"))
				arcRequired = true;
			else if (retrievedArcRequired.equals("false"))
				arcRequired = false;
		}
		String retrievedArcStartX = inputElement.getAttribute("startX");
		if (retrievedArcStartX.length() > 0)
			arcStartX = Double.parseDouble(retrievedArcStartX);
		String retrievedArcStartY = inputElement.getAttribute("startY");
		if (retrievedArcStartY.length() > 0)
			arcStartY = Double.parseDouble(retrievedArcStartY);
		String retrievedArcEndX = inputElement.getAttribute("endX");
		if (retrievedArcEndX.length() > 0)
			arcEndX = Double.parseDouble(retrievedArcEndX);
		String retrievedArcEndY = inputElement.getAttribute("endY");
		if (retrievedArcEndY.length() > 0)
			arcEndY = Double.parseDouble(retrievedArcEndY);

		PetriNetNode parentNodeType = QueryLoader.queryData.getNode(arcSourceID).getNodeType();
		if (parentNodeType.equals(PetriNetNode.RESULT))
			labelRequired = false;

		// create arc
		PerformanceTreeArc tempArc = new PerformanceTreeArc(arcStartX,
															arcStartY,
															arcEndX,
															arcEndY,
															arcSourceID,
															arcTargetID,
															arcLabel,
															labelRequired,
															arcRequired,
															arcID);

		// extract arcPathPoint details and attach them to arc
		float arcPathPointX = 0;
		float arcPathPointY = 0;
		boolean arcPathPointType = false;
		NodeList arcChildNodes = inputElement.getChildNodes();
		if (arcChildNodes.getLength() > 0)
		{
			// delete arc path points, so that we can add these ones
			tempArc.getArcPath().purgePathPoints();
			for (int i = 1; i < arcChildNodes.getLength() - 1; i++)
			{
				Node arcChildNode = arcChildNodes.item(i);
				if (arcChildNode instanceof Element)
				{
					Element arcElement = (Element) arcChildNode;
					if (arcElement.getNodeName().equals("arcpathpoint"))
					{
						String retrievedArcPathPointType = arcElement.getAttribute("type");
						if (retrievedArcPathPointType.length() > 0)
						{
							if (retrievedArcPathPointType.equals("true"))
								arcPathPointType = true;
							else if (retrievedArcPathPointType.equals("false"))
								arcPathPointType = false;
						}
						String retrievedArcPathPointX = arcElement.getAttribute("x");
						if (retrievedArcPathPointX.length() > 0)
						{
							arcPathPointX = Float.parseFloat(retrievedArcPathPointX);
							arcPathPointX += QueryConstants.ARC_CONTROL_POINT_CONSTANT + 1;
						}
						String retrievedArcPathPointY = arcElement.getAttribute("y");
						if (retrievedArcPathPointY.length() > 0)
						{
							arcPathPointY = Float.parseFloat(retrievedArcPathPointY);
							arcPathPointY += QueryConstants.ARC_CONTROL_POINT_CONSTANT + 1;
						}
						// attach arc path point to arc
						tempArc.getArcPath().addPoint(arcPathPointX, arcPathPointY, arcPathPointType);
					}
				}
			}
		}
		// add arc into the data structure
		QueryLoader.queryData.addArc(tempArc);
	}

	/**
	 * Extracts state labels from the xml document and stores them in QueryData
	 * 
	 * @param inputElement
	 */
	private static void createStateLabel(final Element inputElement)
	{
		// retrieve info from element's elements
		String stateLabel = inputElement.getAttribute("name");
		// create the state label as one that has no states assigned to it yet
		QueryLoader.queryData.addStateLabel(stateLabel, null);
		NodeList statelabelElements = inputElement.getChildNodes();
		for (int i = 0; i < statelabelElements.getLength(); i++)
		{
			Node stateNode = statelabelElements.item(i);
			if (stateNode instanceof Element)
			{
				Element stateElement = (Element) stateNode;
				if ("state".equals(stateElement.getNodeName()))
				{
					String stateName = stateElement.getAttribute("name");
					if (!stateLabel.equals("") && stateName.length() > 0)
					{
						// add the state to the definition of the state label
						QueryLoader.queryData.addStateLabel(stateLabel, stateName);
					}
				}
			}
		}
	}

	/**
	 * Extracts action labels from the xml document and stores them in QueryData
	 * 
	 * @param inputElement
	 */
	private static void createActionLabel(final Element inputElement)
	{
		// retrieve info from element's elements
		String actionLabel = "";
		String retrievedActionLabel = inputElement.getAttribute("label");
		if (retrievedActionLabel.length() > 0)
			actionLabel = retrievedActionLabel;
		// add action label to QueryData
		QueryLoader.queryData.addActionLabel(actionLabel);
	}

	/**
	 * This method is called by MacroLoader and is responsible for importing a
	 * macro. It only adds the macro definition to QueryData.
	 * 
	 * @param PTMLDoc
	 * @param queryDataInput
     * @return
	 */
	public static boolean importMacro(final Document PTMLDoc)
	{
		QueryLoader.importSuccessful = false;

		// need to set queryManager's mode to LOADING here, so that the
		// system knows that we're loading in a macro in the background
		// without visualisation
		QueryManager.setMode(QueryConstants.LOADING);

		QueryLoader.queryData = QueryManager.getData();
		NodeList ptmlElements = PTMLDoc.getDocumentElement().getChildNodes();
		for (int i = 0; i < ptmlElements.getLength(); i++)
		{
			Node ptmlNode = ptmlElements.item(i);
			if (ptmlNode instanceof Element)
			{
				Element ptmlElement = (Element) ptmlNode;
				if ("macro".equals(ptmlElement.getNodeName()))
				{
					QueryLoader.createMacro(ptmlElement);
					QueryLoader.importSuccessful = true;
				}
			}
		}

		// restore mode
		QueryManager.restoreMode();

		return QueryLoader.importSuccessful;
	}

	/**
	 * Extracts macro information from the xml document and stores it in
	 * QueryData
	 * 
	 * @param inputElement
	 */
	private static void createMacro(final Element inputElement)
	{
		// set mode
		MacroManager.setMode(QueryConstants.LOADING);

		// retrieve macro information from element's attributes
		String macroName = inputElement.getAttribute("name");
		String macroDescription = inputElement.getAttribute("description");
		String macroReturnType = inputElement.getAttribute("returntype");

		// store retrieved information in a MacroDefinition
		QueryLoader.macro = new MacroDefinition(macroName);
		QueryLoader.macro.setDescription(macroDescription);
		QueryLoader.macro.setReturnType(macroReturnType);

		// store macro tree in MacroDefinition as well
		NodeList macroElements = inputElement.getChildNodes();
		for (int i = 0; i < macroElements.getLength(); i++)
		{
			Node macroNode = macroElements.item(i);
			if (macroNode instanceof Element)
			{
				Element macroElement = (Element) macroNode;
				if ("tree".equals(macroElement.getNodeName()))
				{
					NodeList treeElements = macroElement.getChildNodes();
					for (int j = 0; j < treeElements.getLength(); j++)
					{
						Node treeNode = treeElements.item(j);
						if (treeNode instanceof Element)
						{
							Element treeElement = (Element) treeNode;
							if ("node".equals(treeElement.getNodeName()))
							{
								QueryLoader.macro = QueryLoader.createMacroNode(treeElement,
																				QueryLoader.macro);
							}
						}
					}
				}
			}
		}

		// store MacroDefinition in QueryData's macros HashMap
		QueryLoader.queryData.saveMacro(QueryLoader.macro);

		// restore mode
		MacroManager.restoreMode();
	}

	/**
	 * Creates a PerformanceTreeNode object in the MacroDefinition
     * @param inputElement
     * @param macro
     * @return
     */
	private static MacroDefinition createMacroNode(final Element inputElement, MacroDefinition macro)
	{
		String nodeID = null;
		String nodeType = null;
		String nodeOperation = null;
		String nodeLabel = null;
		double nodePositionX = 0;
		double nodePositionY = 0;
		String nodeIncomingArcID = null;
		ArrayList<String> nodeOutgoingArcIDs = new ArrayList<String>();

		// retrieve info from element's attributes
		String retrievedNodeID = inputElement.getAttribute("id");
		if (retrievedNodeID.length() > 0)
			nodeID = retrievedNodeID;
		String retrievedNodeType = inputElement.getAttribute("type");
		if (retrievedNodeType.length() > 0)
			nodeType = retrievedNodeType;
		String retrievedNodeOperation = inputElement.getAttribute("operation");
		if (retrievedNodeOperation.length() > 0)
			nodeOperation = retrievedNodeOperation;
		String retrievedNodeLabel = inputElement.getAttribute("label");
		if (retrievedNodeLabel.length() > 0)
			nodeLabel = retrievedNodeLabel;
		String retrievedNodePositionX = inputElement.getAttribute("x");
		if (retrievedNodePositionX.length() > 0)
		{
			nodePositionX = Double.valueOf(retrievedNodePositionX).doubleValue() *
							(1) +
							(1);
			nodePositionX += QueryConstants.NODE_HEIGHT / 2 - 1;
		}
		String retrievedNodePositionY = inputElement.getAttribute("y");
		if (retrievedNodePositionY.length() > 0)
		{
			nodePositionY = Double.valueOf(retrievedNodePositionY).doubleValue() *
							(1) +
							(1);
			nodePositionY += QueryConstants.NODE_HEIGHT / 2 - 1;
		}

		// extract incoming and outgoing arc ids
		NodeList nodeElementList = inputElement.getChildNodes();
		for (int i = 0; i < nodeElementList.getLength(); i++)
		{
			Node nodeNode = nodeElementList.item(i);
			if (nodeNode instanceof Element)
			{
				Element nodeElement = (Element) nodeNode;
				if ("incomingArc".equals(nodeElement.getNodeName()))
				{
					String retrievedNodeIncomingArcID = nodeElement.getTextContent();
					if (retrievedNodeIncomingArcID.length() > 0)
						nodeIncomingArcID = retrievedNodeIncomingArcID;
				}
				else if ("outgoingArcs".equals(nodeElement.getNodeName()))
				{
					// extract arc IDs and put them into the array of outgoing
					// arc IDs
					NodeList outgoingArcsElementList = nodeElement.getChildNodes();
					for (int j = 0; j < outgoingArcsElementList.getLength(); j++)
					{
						Node outgoingArcsNode = outgoingArcsElementList.item(j);
						if (outgoingArcsNode instanceof Element)
						{
							Element outgoingArcsElement = (Element) outgoingArcsNode;
							if ("arc".equals(outgoingArcsElement.getNodeName()))
							{
								String outgoingArcID = outgoingArcsElement.getAttribute("id");
								nodeOutgoingArcIDs.add(outgoingArcID);
							}
						}
					}
				}
			}
		}

		// create nodes with the retrieved values
		PerformanceTreeNode node = null;
		if (nodeType.equals(PetriNetNode.RESULT.toString()))
			node = new ResultNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.SEQUENTIAL.toString()))
			node = new SequentialNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.ARITHCOMP.toString()))
			node = new ArithCompNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.ARITHOP.toString()))
			node = new ArithOpNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.CONVOLUTION.toString()))
			node = new ConvolutionNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.DISCON.toString()))
			node = new DisconNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.DISTRIBUTION.toString()))
			node = new DistributionNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.FIRINGRATE.toString()))
			node = new FiringRateNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.ININTERVAL.toString()))
			node = new InIntervalNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.MOMENT.toString()))
			node = new MomentNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.PERCENTILE.toString()))
			node = new PercentileNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.NEGATION.toString()))
			node = new NegationNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.PASSAGETIMEDENSITY.toString()))
			node = new PassageTimeDensityNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.PROBININTERVAL.toString()))
			node = new ProbInIntervalNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.PROBINSTATES.toString()))
			node = new ProbInStatesNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.RANGE.toString()))
			node = new RangeNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.STATESATTIME.toString()))
			node = new StatesAtTimeNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.STEADYSTATEPROB.toString()))
			node = new SteadyStateProbNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.STEADYSTATESTATES.toString()))
			node = new SteadyStateStatesNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.MACRO.toString()))
			node = new MacroNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.SUBSET.toString()))
			node = new SubsetNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.ACTIONS.toString()))
			node = new ActionsNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.BOOL.toString()))
			node = new BoolNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.NUM.toString()))
			node = new NumNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.STATEFUNCTION.toString()))
			node = new StateFunctionNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.STATES.toString()))
			node = new StatesNode(nodePositionX, nodePositionY, nodeID);
		else if (nodeType.equals(PetriNetNode.ARGUMENT.toString()))
			node = new ArgumentNode(nodePositionX, nodePositionY, nodeID);

		if (nodeIncomingArcID != null)
			node.setIncomingArcID(nodeIncomingArcID);

		// need to add node before checking if it's a value node because if the
		// label is to
		// be drawn then the node has to exist already
		macro.addMacroNode(node);

		if (node instanceof OperationNode)
		{
			if (nodeOutgoingArcIDs.size() != 0)
				((OperationNode) node).setOutgoingArcIDs(nodeOutgoingArcIDs);
			if (nodeOperation != null)
			{
				((OperationNode) node).setOperation(nodeOperation);
			}
		}
		else if (node instanceof ValueNode)
		{
			if (nodeLabel != null)
			{
				// set the visible label
				((ValueNode) node).setNodeLabel(nodeLabel);
				if (node instanceof StatesNode)
				{
					// set the state label variable
					((StatesNode) node).setStateLabel(nodeLabel);
				}
				else if (node instanceof ActionsNode)
				{
					// set the action label variable
					((ActionsNode) node).setActionLabel(nodeLabel);
				}
				else if (node instanceof ArgumentNode)
				{
					// set the argument name
					((ArgumentNode) node).setArgumentName(nodeLabel);
				}
			}
		}

		// update node to reflect change in label
		macro.updateMacroNode(node);

		// create outgoing arcs
		for (int i = 0; i < nodeElementList.getLength(); i++)
		{
			Node nodeNode = nodeElementList.item(i);
			if (nodeNode instanceof Element)
			{
				Element nodeElement = (Element) nodeNode;
				if ("outgoingArcs".equals(nodeElement.getNodeName()))
				{
					NodeList outgoingArcsElementList = nodeElement.getChildNodes();
					for (int j = 0; j < outgoingArcsElementList.getLength(); j++)
					{
						Node outgoingArcsNode = outgoingArcsElementList.item(j);
						if (outgoingArcsNode instanceof Element)
						{
							Element outgoingArcsElement = (Element) outgoingArcsNode;
							if ("arc".equals(outgoingArcsElement.getNodeName()))
							{
								macro = QueryLoader.createMacroArc(outgoingArcsElement, macro);
							}
						}
					}
				}
			}
		}

		// create child nodes if applicable
		for (int i = 0; i < nodeElementList.getLength(); i++)
		{
			Node nodeNode = nodeElementList.item(i);
			if (nodeNode instanceof Element)
			{
				Element nodeElement = (Element) nodeNode;
				if ("childNodes".equals(nodeElement.getNodeName()))
				{
					NodeList childNodesElementList = nodeElement.getChildNodes();
					for (int j = 0; j < childNodesElementList.getLength(); j++)
					{
						Node childNodesNode = childNodesElementList.item(j);
						if (childNodesNode instanceof Element)
						{
							Element childNodesElement = (Element) childNodesNode;
							if ("node".equals(childNodesElement.getNodeName()))
							{
								macro = QueryLoader.createMacroNode(childNodesElement, macro);
							}
						}
					}
				}
			}
		}

		return macro;
	}

	/**
	 * Creates a PerformanceTreeArc object from an PerformanceTreeArc DOM
	 * Element
     * @param inputElement
     * @param macro
     * @return
     */
	private static MacroDefinition createMacroArc(final Element inputElement, final MacroDefinition macro)
	{
		String arcID = null;
		String arcLabel = null;
		String arcSourceID = null;
		String arcTargetID = null;
		double arcStartX = 0;
		double arcStartY = 0;
		double arcEndX = 0;
		double arcEndY = 0;
		boolean arcRequired = true;
		boolean labelRequired = true;

		// retrieve info from element's attributes
		String retrievedArcID = inputElement.getAttribute("id");
		if (retrievedArcID.length() > 0)
			arcID = retrievedArcID;
		String retrievedArcLabel = inputElement.getAttribute("label");
		if (retrievedArcLabel.length() > 0)
			arcLabel = retrievedArcLabel;
		String retrievedArcSourceID = inputElement.getAttribute("source");
		if (retrievedArcSourceID.length() > 0)
			arcSourceID = retrievedArcSourceID;
		String retrievedArcTargetID = inputElement.getAttribute("target");
		if (retrievedArcTargetID.length() > 0)
			arcTargetID = retrievedArcTargetID;
		String retrievedArcRequired = inputElement.getAttribute("required");
		if (retrievedArcRequired.length() > 0)
		{
			if (retrievedArcRequired.equals("true"))
				arcRequired = true;
			else if (retrievedArcRequired.equals("false"))
				arcRequired = false;
		}
		String retrievedArcStartX = inputElement.getAttribute("startX");
		if (retrievedArcStartX.length() > 0)
			arcStartX = Double.parseDouble(retrievedArcStartX);
		String retrievedArcStartY = inputElement.getAttribute("startY");
		if (retrievedArcStartY.length() > 0)
			arcStartY = Double.parseDouble(retrievedArcStartY);
		String retrievedArcEndX = inputElement.getAttribute("endX");
		if (retrievedArcEndX.length() > 0)
			arcEndX = Double.parseDouble(retrievedArcEndX);
		String retrievedArcEndY = inputElement.getAttribute("endY");
		if (retrievedArcEndY.length() > 0)
			arcEndY = Double.parseDouble(retrievedArcEndY);

		PetriNetNode parentNodeType = macro.getMacroNode(arcSourceID).getNodeType();
		if (parentNodeType.equals(PetriNetNode.MACRO))
			labelRequired = false;

		// create arc
		PerformanceTreeArc tempArc = new PerformanceTreeArc(arcStartX,
															arcStartY,
															arcEndX,
															arcEndY,
															arcSourceID,
															arcTargetID,
															arcLabel,
															labelRequired,
															arcRequired,
															arcID);

		// extract arcPathPoint details and attach them to arc
		float arcPathPointX = 0;
		float arcPathPointY = 0;
		boolean arcPathPointType = false;
		NodeList arcChildNodes = inputElement.getChildNodes();
		if (arcChildNodes.getLength() > 0)
		{
			// delete arc path points, so that we can add these ones
			tempArc.getArcPath().purgePathPoints();
			for (int i = 1; i < arcChildNodes.getLength() - 1; i++)
			{
				Node arcChildNode = arcChildNodes.item(i);
				if (arcChildNode instanceof Element)
				{
					Element arcElement = (Element) arcChildNode;
					if (arcElement.getNodeName().equals("arcpathpoint"))
					{
						String retrievedArcPathPointType = arcElement.getAttribute("type");
						if (retrievedArcPathPointType.length() > 0)
						{
							if (retrievedArcPathPointType.equals("true"))
								arcPathPointType = true;
							else if (retrievedArcPathPointType.equals("false"))
								arcPathPointType = false;
						}
						String retrievedArcPathPointX = arcElement.getAttribute("x");
						if (retrievedArcPathPointX.length() > 0)
						{
							arcPathPointX = Float.parseFloat(retrievedArcPathPointX);
							arcPathPointX += QueryConstants.ARC_CONTROL_POINT_CONSTANT + 1;
						}
						String retrievedArcPathPointY = arcElement.getAttribute("y");
						if (retrievedArcPathPointY.length() > 0)
						{
							arcPathPointY = Float.parseFloat(retrievedArcPathPointY);
							arcPathPointY += QueryConstants.ARC_CONTROL_POINT_CONSTANT + 1;
						}
						// attach arc path point to arc
						tempArc.getArcPath().addPoint(arcPathPointX, arcPathPointY, arcPathPointType);
					}
				}
			}
		}

		// add arc into the data structure
		macro.addMacroArc(tempArc);
		return macro;
	}

	public static MacroDefinition getMacro()
	{
		return QueryLoader.macro;
	}

}
