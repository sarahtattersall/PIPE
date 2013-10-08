/**
 * MacroWriter
 * 
 * This class contains all relevant methods for serialising a macro 
 * into an XML file. 
 * 
 * @author Tamas Suto
 * @date 19/12/07
 */

package pipe.modules.queryeditor.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import pipe.common.PetriNetNode;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeArc;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeNode;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroDefinition;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroManager;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.OperationNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.ActionsNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.StatesNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.ValueNode;

public class MacroWriter
{

	private static MacroDefinition			macro;
	private static final HashMap<String, String>	nodesProcessed	= new HashMap<String, String>();

	/**
	 * This method is responsible for saving a macro as an XML document. The
	 * document follows the structure of the macro tree.
     * @param macroDef
     * @param saveLocation
     * @throws NullPointerException
     * @throws org.w3c.dom.DOMException
     * @throws java.io.IOException
     */
	public static void saveMacro(final MacroDefinition macroDef, final String saveLocation)	throws NullPointerException,
																							IOException,
            DOMException
    {

		MacroWriter.macro = macroDef;
		File macroFile = null;

		if (saveLocation.equals(""))
		{
			// test if macro save location exists
			File macroSaveLocation = new File(MacroManager.macroSaveLocation);
			if (!macroSaveLocation.exists())
				macroSaveLocation.mkdir();

			String macroPath = MacroManager.macroSaveLocation + MacroWriter.macro.getName() + ".xml";
			macroFile = new File(macroPath);
		}
		else
		{
			macroFile = new File(saveLocation);
		}

		Document ptDOM = null;
		StreamSource xsltSource = null;
		Transformer transformer = null;

		try
		{
			// Build a PTML Document
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			ptDOM = builder.newDocument();

			// PTML Element
			Element ptmlElement = ptDOM.createElement("ptml");
			ptDOM.appendChild(ptmlElement);

			// Macro Element
			Element macroElement = ptDOM.createElement("macro");
			macroElement.setAttribute("name", MacroWriter.macro.getName());
			macroElement.setAttribute("description", MacroWriter.macro.getDescription());
			macroElement.setAttribute("returntype", MacroWriter.macro.getReturnType());
			ptmlElement.appendChild(macroElement);

			// put nodes into HashMap, so that we can check whether they have
			// been processed already
			// (in case of recursion when child nodes are processed)
			ArrayList<PerformanceTreeNode> nodesArray = MacroWriter.macro.getMacroNodes();
			Iterator<PerformanceTreeNode> i = nodesArray.iterator();
			while (i.hasNext())
			{
				PerformanceTreeNode nodeNotProcessedYet = i.next();
				String nodeID = nodeNotProcessedYet.getId();
				MacroWriter.nodesProcessed.put(nodeID, "false");
			}

			// serialise node and their arcs
			i = nodesArray.iterator();
			while (i.hasNext())
			{
				PerformanceTreeNode nodeToSerialise = i.next();
				String nodeToSerialiseID = nodeToSerialise.getId();
				boolean nodeProcessedAlready = false;
				if ((MacroWriter.nodesProcessed.get(nodeToSerialiseID)).equals("true"))
				{
					nodeProcessedAlready = true;
				}
				else if ((MacroWriter.nodesProcessed.get(nodeToSerialiseID)).equals("false"))
				{
					nodeProcessedAlready = false;
				}

				if (!nodeProcessedAlready)
					MacroWriter.createNodeElement(nodeToSerialise, macroElement, ptDOM);
			}

			// serialise state and action labels
			MacroWriter.serialiseStateAndActionLabels(macroElement, ptDOM);

			ptDOM.normalize();

			// Create Transformer with XSL Source File
			xsltSource = new StreamSource(Thread.currentThread()
												.getContextClassLoader()
												.getResourceAsStream("pipe" +
																		System.getProperty("file.separator") +
																		"modules" +
																		System.getProperty("file.separator") +
																		"queryeditor" +
																		System.getProperty("file.separator") +
																		"io" +
																		System.getProperty("file.separator") +
																		"WriteMacroXML.xsl"));
			transformer = TransformerFactory.newInstance().newTransformer(xsltSource);

			// Write file and do XSLT transformation to generate correct PTML
            DOMSource source = new DOMSource(ptDOM);
			StreamResult result = new StreamResult(macroFile);
			transformer.transform(source, result);
		}
		catch (DOMException e)
		{
			System.out.println("DOMException thrown in saveMacro()" +
								" : MacroWriter Class : modules.queryeditor.io Package" + " : filename=\"" +
								macroFile.getCanonicalPath() + "\" xslt=\"" + xsltSource.getSystemId() +
								"\" transformer=\"" + transformer.getURIResolver() + "\"");
		}
		catch (ParserConfigurationException e)
		{
			System.out.println("ParserConfigurationException thrown in saveMacro()" +
								" : MacroWriter Class : modules.queryeditor.io Package" + " : filename=\"" +
								macroFile.getCanonicalPath() + "\" xslt=\"" + "\" transformer=\"" + "\"");
		}
		catch (TransformerConfigurationException e)
		{
			System.out.println("TransformerConfigurationException thrown in saveMacro()" +
								" : MacroWriter Class : modules.queryeditor.io Package" + " : filename=\"" +
								macroFile.getCanonicalPath() + "\" xslt=\"" + xsltSource.getSystemId() +
								"\" transformer=\"" + transformer.getURIResolver() + "\"");
		}
		catch (TransformerException e)
		{
			System.out.println("TransformerException thrown in saveMacro()" +
								" : MacroWriter Class : modules.queryeditor.io Package" + " : filename=\"" +
								macroFile.getCanonicalPath() + "\" xslt=\"" + xsltSource.getSystemId() +
								"\" transformer=\"" + transformer.getURIResolver() + "\"");
		}
	}

	/**
	 * This method serialises the information contained within a
	 * PerformanceTreeNode and recurses down to serialise info about its arcs
     * @param inputNode
     * @param parentElement
     * @param document
     */
	private static void createNodeElement(	final PerformanceTreeNode inputNode,
											final Element parentElement,
											final Document document)
	{

		Element nodeElement = document.createElement("node");

		// common data to serialise for all PT nodes
		String nodeID = inputNode.getId();
		Double nodePositionX = inputNode.getPositionXObject();
		Double nodePositionY = inputNode.getPositionYObject();
		PetriNetNode nodeType = inputNode.getNodeType();
		String nodeIncomingArcID = inputNode.getIncomingArcID();
		nodeElement.setAttribute("id", nodeID);
		nodeElement.setAttribute("type", nodeType.toString());
		nodeElement.setAttribute("x", String.valueOf(nodePositionX));
		nodeElement.setAttribute("y", String.valueOf(nodePositionY));
		nodeElement.setAttribute("incomingArc", nodeIncomingArcID);

		// indicate that this node has been processed already
		MacroWriter.nodesProcessed.put(nodeID, "true");

		if (nodeIncomingArcID == null || nodeIncomingArcID.equals(""))
		{
			Element tree = document.createElement("tree");
			parentElement.appendChild(tree);
			tree.appendChild(nodeElement);
		}
		else
		{
			parentElement.appendChild(nodeElement);
		}

		// special cases
		if (inputNode instanceof ValueNode)
		{
			String nodeLabel;
			if (((ValueNode) inputNode).getNodeLabelObject() != null)
				nodeLabel = ((ValueNode) inputNode).getNodeLabel();
			else nodeLabel = "";
			nodeElement.setAttribute("label", nodeLabel);
		}
		else if (inputNode instanceof OperationNode)
		{
			String nodeOperation = ((OperationNode) inputNode).getOperation();
			nodeElement.setAttribute("operation", nodeOperation);
			Element outgoingArcsElement = document.createElement("outgoingArcs");
			nodeElement.appendChild(outgoingArcsElement);
			Collection nodeOutgoingArcIDs = ((OperationNode) inputNode).getOutgoingArcIDs();
			Iterator i = nodeOutgoingArcIDs.iterator();
			while (i.hasNext())
			{
				String outgoingArcID = (String) i.next();
				PerformanceTreeArc nodeArc = MacroWriter.macro.getMacroArc(outgoingArcID);
				MacroWriter.createArcElement(nodeArc, outgoingArcsElement, document);

			}

			Element childNodesElement = document.createElement("childNodes");
			nodeElement.appendChild(childNodesElement);
			i = nodeOutgoingArcIDs.iterator();
			while (i.hasNext())
			{
				String outgoingArcID = (String) i.next();
				PerformanceTreeArc nodeArc = MacroWriter.macro.getMacroArc(outgoingArcID);
				if (nodeArc.getTarget() != null)
				{
					PerformanceTreeNode childNode = nodeArc.getTarget();
					boolean nodeProcessedAlready = false;
					if ((MacroWriter.nodesProcessed.get(childNode.getId())).equals("true"))
					{
						nodeProcessedAlready = true;
					}
					else if ((MacroWriter.nodesProcessed.get(childNode.getId())).equals("false"))
					{
						nodeProcessedAlready = false;
					}
					if (!nodeProcessedAlready)
						MacroWriter.createNodeElement(childNode, childNodesElement, document);
				}
			}
		}
	}

	/**
	 * This method serialises the data contained within a PerformanceTreeArc. It
	 * also serialises the ArcPathPoints information
     * @param inputArc
     * @param nodeElement
     * @param document
     */
	private static void createArcElement(	final PerformanceTreeArc inputArc,
											final Element nodeElement,
											final Document document)
	{

		Element arcElement = document.createElement("arc");
		nodeElement.appendChild(arcElement);

		String arcID = inputArc.getId();
		String arcLabel = inputArc.getArcLabel();
		String arcSourceID = inputArc.getSource().getId();
		String arcTargetID;
		if (inputArc.getTarget() != null)
			arcTargetID = inputArc.getTarget().getId();
		else arcTargetID = "";
		String arcRequired = Boolean.toString(inputArc.isRequired());
		String arcStartX = Double.toString(inputArc.getArcPath().getStartPoint().getX());
		String arcStartY = Double.toString(inputArc.getArcPath().getStartPoint().getY());
		String arcEndX = Double.toString(inputArc.getArcPath().getEndPoint().getX());
		String arcEndY = Double.toString(inputArc.getArcPath().getEndPoint().getY());

		arcElement.setAttribute("id", arcID);
		arcElement.setAttribute("label", arcLabel);
		arcElement.setAttribute("source", arcSourceID);
		arcElement.setAttribute("target", arcTargetID);
		arcElement.setAttribute("required", arcRequired);
		arcElement.setAttribute("startX", arcStartX);
		arcElement.setAttribute("startY", arcStartY);
		arcElement.setAttribute("endX", arcEndX);
		arcElement.setAttribute("endY", arcEndY);

		// arcPathPoint information needs to be saved, too
		int arcPathPoints = inputArc.getArcPath().getArcPathDetails().length;
		String[][] point = inputArc.getArcPath().getArcPathDetails();
		for (int i = 0; i < arcPathPoints; i++)
		{
			MacroWriter.createArcPathPoint(point[i][0], point[i][1], point[i][2], arcElement, document, i);
		}
	}

	/**
	 * This method serialises the information of an ArcPathPoint
     * @param x
     * @param y
     * @param type
     * @param arcElement
     * @param document
     * @param id
     */
	private static void createArcPathPoint(	final String x,
											final String y,
											final String type,
											final Element arcElement,
											final Document document,
											final int id)
	{
		Element arcPathPoint = document.createElement("arcpathpoint");
		arcElement.appendChild(arcPathPoint);

		String pointId = String.valueOf(id);
		if (pointId.length() < 3)
			pointId = "0" + pointId;
		if (pointId.length() < 3)
			pointId = "0" + pointId;

		arcPathPoint.setAttribute("id", pointId);
		arcPathPoint.setAttribute("type", type);
		arcPathPoint.setAttribute("x", x);
		arcPathPoint.setAttribute("y", y);
	}

	/**
	 * This method serialises the state and action labels
	 * 
	 * @param tree
     * @param document
	 */
	private static void serialiseStateAndActionLabels(final Element tree, final Document document)
	{
		HashMap<String, ArrayList<String>> retrievedStateLabels = QueryManager.getData().getStateLabels();
		HashMap<String, ArrayList<String>> stateLabels = new HashMap<String, ArrayList<String>>();
		ArrayList<String> actionLabels = new ArrayList<String>();

		// only add the state and action labels to the respective array lists
		// that are actually
		// occurring in the macro
		ArrayList<PerformanceTreeNode> nodesArray = MacroWriter.macro.getMacroNodes();
		Iterator<PerformanceTreeNode> ni = nodesArray.iterator();
		while (ni.hasNext())
		{
			PerformanceTreeNode node = ni.next();
			if (node instanceof StatesNode)
			{
				StatesNode statesNode = (StatesNode) node;
				if (statesNode.getNodeLabelObject() != null)
				{
					String stateLabel = statesNode.getNodeLabel();
					ArrayList<String> states = retrievedStateLabels.get(stateLabel);
					stateLabels.put(stateLabel, states);
				}
			}
			else if (node instanceof ActionsNode)
			{
				ActionsNode actionsNode = (ActionsNode) node;
				if (actionsNode.getNodeLabelObject() != null)
				{
					String actionLabel = actionsNode.getNodeLabel();
					actionLabels.add(actionLabel);
				}
			}
		}

		// serialise state labels
		Element stateLabelsElement = document.createElement("stateLabels");
		tree.appendChild(stateLabelsElement);
		Iterator<String> i = stateLabels.keySet().iterator();
		while (i.hasNext())
		{
			Element stateLabelElement = document.createElement("statelabel");
			stateLabelsElement.appendChild(stateLabelElement);
			String stateLabel = i.next();
			ArrayList<String> stateLabelsStates = stateLabels.get(stateLabel);
			stateLabelElement.setAttribute("name", stateLabel);
			Iterator<String> j = stateLabelsStates.iterator();
			while (j.hasNext())
			{
				Element stateElement = document.createElement("state");
				stateLabelElement.appendChild(stateElement);
				String stateName = j.next();
				stateElement.setAttribute("name", stateName);
			}
		}

		// serialise action labels
		Element actionLabelsElement = document.createElement("actionLabels");
		tree.appendChild(actionLabelsElement);
		Element actionLabelElement;
		i = actionLabels.iterator();
		while (i.hasNext())
		{
			actionLabelElement = document.createElement("actionlabel");
			actionLabelsElement.appendChild(actionLabelElement);
			String actionLabel = i.next();
			actionLabelElement.setAttribute("label", actionLabel);
		}
	}

}
