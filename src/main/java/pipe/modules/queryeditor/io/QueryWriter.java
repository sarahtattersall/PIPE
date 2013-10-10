/**
 * QueryWriter
 * 
 * This class contains all relevant methods for serialising the query 
 * into an XML file. This is what happens when a query is saved.
 * 
 * @author Tamas Suto
 * @date 26/07/07
 */

package pipe.modules.queryeditor.io;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import pipe.common.PetriNetNode;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeArc;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeNode;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroDefinition;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.OperationNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.ActionsNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.StatesNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.ValueNode;

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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class QueryWriter
{

	private static QueryData				queryData;
	private static MacroDefinition			macro;
	private static final HashMap<String, String>	nodesProcessed	= new HashMap<String, String>();

	/**
	 * This method is responsible for saving a query as a PTML document. The
	 * document follows the structure of the tree.
     * @param file
     * @throws NullPointerException
     * @throws org.w3c.dom.DOMException
     * @throws java.io.IOException
     */
	public static void saveQuery(final File file)	throws NullPointerException,
													IOException,
            DOMException
    {
		if (file == null)
			throw new NullPointerException("Null file in saveQuery");

		// load in current QueryData
		QueryWriter.queryData = QueryManager.getData();

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
			Element ptml = ptDOM.createElement("ptml");
			ptDOM.appendChild(ptml);

			// put nodes into HashMap, so that we can check whether they have
			// been processed already
			// (in case of recursion when child nodes are processed)
			PerformanceTreeNode[] nodesArray = QueryWriter.queryData.getNodes();
            for(PerformanceTreeNode nodeNotProcessedYet : nodesArray)
            {
                String nodeID = nodeNotProcessedYet.getId();
                QueryWriter.nodesProcessed.put(nodeID, "false");
            }

			// serialise node and their arcs
            for(PerformanceTreeNode nodeToSerialise : nodesArray)
            {
                String nodeToSerialiseID = nodeToSerialise.getId();
                boolean nodeProcessedAlready = false;
                if((QueryWriter.nodesProcessed.get(nodeToSerialiseID)).equals("true"))
                {
                    nodeProcessedAlready = true;
                }
                else if((QueryWriter.nodesProcessed.get(nodeToSerialiseID)).equals("false"))
                {
                    nodeProcessedAlready = false;
                }

                if(!nodeProcessedAlready)
                    QueryWriter.createNodeElement(nodeToSerialise, false, ptml, ptDOM);
            }

			// serialise state and action labels
			QueryWriter.serialiseStateAndActionLabels(ptml, ptDOM);

			// serialise macros
			QueryWriter.serialiseMacros(ptml, ptDOM);

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
																		"WriteQueryXML.xsl"));
			transformer = TransformerFactory.newInstance().newTransformer(xsltSource);

			// Write file and do XSLT transformation to generate correct PTML
            DOMSource source = new DOMSource(ptDOM);
			StreamResult result = new StreamResult(file);
			transformer.transform(source, result);
		}
		catch (DOMException e)
		{
			System.out.println("DOMException thrown in savePTML()" +
								" : QueryDataWriter Class : modules.queryeditor.io Package" +
								" : filename=\"" + file.getCanonicalPath() + "\" xslt=\"" +
								xsltSource.getSystemId() + "\" transformer=\"" +
								transformer.getURIResolver() + "\"");
		}
		catch (ParserConfigurationException e)
		{
			System.out.println("ParserConfigurationException thrown in savePTML()" +
								" : QueryDataWriter Class : modules.queryeditor.io Package" +
								" : filename=\"" + file.getCanonicalPath() + "\" xslt=\"" +
								"\" transformer=\"" + "\"");
		}
		catch (TransformerConfigurationException e)
		{
			System.out.println("TransformerConfigurationException thrown in savePTML()" +
								" : QueryDataWriter Class : modules.queryeditor.io Package" +
								" : filename=\"" + file.getCanonicalPath() + "\" xslt=\"" +
								xsltSource.getSystemId() + "\" transformer=\"" +
								transformer.getURIResolver() + "\"");
		}
		catch (TransformerException e)
		{
			System.out.println("TransformerException thrown in savePTML()" +
								" : QueryDataWriter Class : modules.queryeditor.io Package" +
								" : filename=\"" + file.getCanonicalPath() + "\" xslt=\"" +
								xsltSource.getSystemId() + "\" transformer=\"" +
								transformer.getURIResolver() + "\"");
		}
	}

	/**
	 * This method serialises the information contained within a
	 * PerformanceTreeNode and recurses down to serialise info about its arcs
     * @param inputNode
     * @param macroNode
     * @param parentElement
     * @param document
     */
	private static void createNodeElement(	final PerformanceTreeNode inputNode,
											boolean macroNode,
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
		QueryWriter.nodesProcessed.put(nodeID, "true");

		if (nodeIncomingArcID == null || nodeIncomingArcID.equals(""))
		{
			Element tree = document.createElement("tree");
			parentElement.appendChild(tree);
			tree.appendChild(nodeElement);
		}
		else parentElement.appendChild(nodeElement);

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
			if (inputNode instanceof MacroNode)
			{
				String nodeLabel;
				if (((MacroNode) inputNode).getNodeLabelObject() != null)
					nodeLabel = ((MacroNode) inputNode).getNodeLabel();
				else nodeLabel = "";
				nodeElement.setAttribute("label", nodeLabel);
			}
			String nodeOperation = ((OperationNode) inputNode).getOperation();
			nodeElement.setAttribute("operation", nodeOperation);

			Element outgoingArcsElement = document.createElement("outgoingArcs");
			nodeElement.appendChild(outgoingArcsElement);
			Collection nodeOutgoingArcIDs = ((OperationNode) inputNode).getOutgoingArcIDs();
			Iterator i = nodeOutgoingArcIDs.iterator();
			while (i.hasNext())
			{
				String outgoingArcID = (String) i.next();
				PerformanceTreeArc nodeArc;
				if (!macroNode)
					nodeArc = QueryWriter.queryData.getArc(outgoingArcID);
				else nodeArc = QueryWriter.macro.getMacroArc(outgoingArcID);
				QueryWriter.createArcElement(nodeArc, outgoingArcsElement, document);
			}

			Element childNodesElement = document.createElement("childNodes");
			nodeElement.appendChild(childNodesElement);
			i = nodeOutgoingArcIDs.iterator();
			while (i.hasNext())
			{
				String outgoingArcID = (String) i.next();
				PerformanceTreeArc nodeArc;
				if (!macroNode)
					nodeArc = QueryWriter.queryData.getArc(outgoingArcID);
				else nodeArc = QueryWriter.macro.getMacroArc(outgoingArcID);

				if (nodeArc.getTargetID() != null)
				{
					PerformanceTreeNode childNode;
					if (!macroNode)
						childNode = nodeArc.getTarget();
					else
					{
						String childNodeID = nodeArc.getTargetID();
						childNode = QueryWriter.macro.getMacroNode(childNodeID);
					}
					boolean nodeProcessedAlready = false;
					if ((QueryWriter.nodesProcessed.get(childNode.getId())).equals("true"))
						nodeProcessedAlready = true;
					else if ((QueryWriter.nodesProcessed.get(childNode.getId())).equals("false"))
						nodeProcessedAlready = false;

					if (!nodeProcessedAlready)
					{
						if (!macroNode)
							QueryWriter.createNodeElement(childNode, false, childNodesElement, document);
						else QueryWriter.createNodeElement(childNode, true, childNodesElement, document);
					}
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
		String arcSourceID = inputArc.getSourceID();
		String arcTargetID;
		if (inputArc.getTargetID() != null)
			arcTargetID = inputArc.getTargetID();
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
			QueryWriter.createArcPathPoint(point[i][0], point[i][1], point[i][2], arcElement, document, i);
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
		HashMap<String, ArrayList<String>> retrievedStateLabels = QueryWriter.queryData.getStateLabels();
		HashMap<String, ArrayList<String>> stateLabels = new HashMap<String, ArrayList<String>>();
		ArrayList<String> actionLabels = new ArrayList<String>();

		// only add the state and action labels to the respective array lists
		// that are actually
		// occurring in the query
		PerformanceTreeNode[] nodesArray = QueryWriter.queryData.getNodes();
        for(PerformanceTreeNode node : nodesArray)
        {
            if(node instanceof StatesNode)
            {
                StatesNode statesNode = (StatesNode) node;
                if(statesNode.getNodeLabelObject() != null)
                {
                    String stateLabel = statesNode.getNodeLabel();
                    ArrayList<String> states = retrievedStateLabels.get(stateLabel);
                    stateLabels.put(stateLabel, states);
                }
            }
            else if(node instanceof ActionsNode)
            {
                ActionsNode actionsNode = (ActionsNode) node;
                if(actionsNode.getNodeLabelObject() != null)
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

	/**
	 * This method serialises all macros based on the MacroNodes found in the
	 * document
	 * 
	 * @param tree
     * @param document
	 */
	private static void serialiseMacros(final Element tree, final Document document)
	{
		PerformanceTreeNode[] allNodes = QueryWriter.queryData.getNodes();
        for(PerformanceTreeNode node : allNodes)
        {
            if(node instanceof MacroNode)
            {
                // if a MacroNode is found, see what macro it stands for and
                // serialise
                // all information about that macro
                String macroName = ((MacroNode) node).getNodeLabel();
                if(macroName != null && !macroName.equals(""))
                {
                    // we could just have a macro node that has not been
                    // assigned a macro
                    // yet, in which case we don't want to serialise macro
                    // definitions as
                    // well

                    QueryWriter.macro = QueryWriter.queryData.getMacro(macroName);

                    // Macro Element
                    Element macroElement = document.createElement("macro");
                    macroElement.setAttribute("name", QueryWriter.macro.getName());
                    macroElement.setAttribute("description", QueryWriter.macro.getDescription());
                    macroElement.setAttribute("returntype", QueryWriter.macro.getReturnType());
                    tree.appendChild(macroElement);

                    // put nodes into HashMap, so that we can check whether they
                    // have been processed already
                    // (in case of recursion when child nodes are processed)
                    ArrayList<PerformanceTreeNode> nodesArray = QueryWriter.macro.getMacroNodes();
                    Iterator<PerformanceTreeNode> j = nodesArray.iterator();
                    while(j.hasNext())
                    {
                        PerformanceTreeNode nodeNotProcessedYet = j.next();
                        String nodeID = nodeNotProcessedYet.getId();
                        QueryWriter.nodesProcessed.put(nodeID, "false");
                    }

                    // serialise nodes and their arcs
                    j = nodesArray.iterator();
                    while(j.hasNext())
                    {
                        PerformanceTreeNode nodeToSerialise = j.next();
                        String nodeToSerialiseID = nodeToSerialise.getId();
                        boolean nodeProcessedAlready = false;
                        if((QueryWriter.nodesProcessed.get(nodeToSerialiseID)).equals("true"))
                            nodeProcessedAlready = true;
                        else if((QueryWriter.nodesProcessed.get(nodeToSerialiseID)).equals("false"))
                            nodeProcessedAlready = false;

                        if(!nodeProcessedAlready)
                            QueryWriter.createNodeElement(nodeToSerialise, true, macroElement, document);
                    }
                }
            }
        }
	}

}