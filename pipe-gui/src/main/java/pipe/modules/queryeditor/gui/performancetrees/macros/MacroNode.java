/**
 * MacroNode
 * 
 * Represents a macro, i.e. a subtree of a performance query that can
 * be reused.
 * 
 * @author Tamas Suto
 * @date 23/05/07
 */

package pipe.modules.queryeditor.gui.performancetrees.macros;

import pipe.common.PetriNetNode;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.QueryView;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeArc;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeNode;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeObjectLabel;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.OperationNode;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;


public class MacroNode extends OperationNode {

	private PerformanceTreeObjectLabel nodeLabel = null;
	private Graphics2D g2d;
	
	
	public MacroNode(double positionXInput, double positionYInput, String idInput) {
		super(positionXInput, positionYInput, idInput);
		initialiseNode();
	}
	
	public MacroNode(double positionXInput, double positionYInput) {
		super(positionXInput, positionYInput);
		initialiseNode();
	}
	
	
	public static String getTooltip() {
		return "Macro  (an encoding of a concept)";
	}
	
	public static String getNodeInfo() {
		return QueryManager.addColouring("The Macro node represents a macro that is an encoding of a particular concept. <br><br>" +
		  "To create a macro, place a Macro node onto the drawing canvas, right-click on it and select 'Assign Macro'.");
	}
	
	public String printTextualRepresentation() {
		String description = "";
		ArrayList children = getChildNodes();
		if (MacroManager.getEditor() == null) {
			// in normal mode
			String op = " and ";
			if (getNodeLabelObject() != null) {
				String macroDescription = QueryManager.getData().getMacro(getNodeLabel()).getDescription();
				//description += QueryManager.addColouring("the "+getNodeLabel()+" with description '"+macroDescription+"' and an argument being ");
				description += QueryManager.addColouring("the "+getNodeLabel()+" ");
				if (children != null) {
					description += QueryManager.addColouring("determined by ");
					Iterator<PerformanceTreeNode> i = children.iterator();
					while (i.hasNext()) {
						PerformanceTreeNode child = i.next();
						QueryManager.colourUp();
						description += child.printTextualRepresentation();
						QueryManager.colourDown();
						if (i.hasNext()) 
							description += QueryManager.addColouring(op);
					}
				}
				else {
					description += QueryManager.addColouring("that has no arguments specified");
				}
			}
			return description;	
		}
		else {
			// in Macro mode
			description = QueryManager.addColouring("The macro representing the query that aims to find out ");
			if (children != null) {
				PerformanceTreeNode child = (PerformanceTreeNode)children.get(0);
				String childsReturnType = child.getReturnType();			
				if (childsReturnType.equals(BOOL_TYPE)) {
					description += QueryManager.addColouring("whether it is true that ");
				}
				else {
					description += QueryManager.addColouring("what is ");
				}
				description += child.printTextualRepresentation();
				QueryManager.resetTextColour();
				description += QueryManager.addColouring(" ?");	
			}
			return "''"+ description + "''";
		}
	}
	
	private void initialiseNode() {
		// set name and node type
		setName("MacroNode");
		setNodeType(PetriNetNode.MACRO);
		
		// initial setup
		setRequiredArguments(1);
		setMaxArguments(1);
		
		// set return type
		setReturnType(MACRO_TYPE);
		
		// indicate that we want labels on the arcs
		showArcLabels = true;	
	}
	
	public String getNodeLabel() {
		if (nodeLabel == null)
			return null;
		else
			return nodeLabel.getText();
	}
	
	public PerformanceTreeObjectLabel getNodeLabelObject() {
		return nodeLabel;
	}
	
	public void setNodeLabel(String labelInput) {
		if (nodeLabel == null) {
			nodeLabel = new PerformanceTreeObjectLabel();
		}
		nodeLabel.setText(labelInput);	
		
		if (g2d != null)
			displayNodeLabel();		
	}
	
	public void displayNodeLabel(){		
		if (g2d == null)
			System.out.println("g2d is null");
		int labelWidth = g2d.getFontMetrics().stringWidth(getNodeLabel());
		double nodeLabelPositionX = positionX + (componentWidth / 2) + (labelWidth / 2);
		double nodeLabelPositionY = positionY + componentHeight + 20;
		nodeLabel.setPosition((int)nodeLabelPositionX,(int)nodeLabelPositionY);
		nodeLabel.updateSize();
		
		// add node label to QueryView container
		QueryView queryView = QueryManager.getView();
		if (queryView != null) {
			if (!QueryManager.isEvaluatingQuery()) {
				// If this is the first time the node label is being added then 
				// the remove operation will have no affect. However, for subsequent
				// updates to the label, the remove will prevent it from 
				// being added more than once.	
				queryView.remove(nodeLabel);
				queryView.add(nodeLabel);
			}
			else {
				// we are evaluating a query so the label needs to be put onto the 
				// ProgressView
				QueryManager.getProgressView().remove(nodeLabel);
				QueryManager.getProgressView().add(nodeLabel);
			}
	  	}
	}

	public void setShowArcLabels(boolean show) {
		showArcLabels = show;
	}
	
	public void setupOutgoingArcs() {
		PerformanceTreeArc arc;
		String role;
		
		int arcsToDraw = getRequiredChildNodes().size() + getOptionalChildNodes().size();
		//setRequiredArguments(arcsToDraw);
		//setMaxArguments(arcsToDraw);
			
		// the center of the lower side of the node. The point that we
		// use as a reference to calculate the X-coordinates of the arc
		// end points
		double arcConnectionPointX = positionX + (componentWidth / 2);
		double arcConnectionPointY = positionY + componentHeight;

		double arcStartPointX, arcStartPointY, arcEndPointX, arcEndPointY;
		int noOfArcsOnTheSide;
		// how many arcs are on either side of the divider line coming down
		// from arcConnectionPoint
		double spacing = componentWidth * 2.5;
		// how much spacing there should be between the arcs' endpoints

		arcStartPointX = arcConnectionPointX;
		arcStartPointY = arcConnectionPointY;
		arcEndPointY = arcStartPointY + componentHeight * 2;

		// find out the coordinates of the leftmost arc. From then on
		// it's easy, since only a spacing value will need to be added
		// to get the next arc's endpoint
		if ((arcsToDraw % 2) == 0) {
			// we have an even number of arcs, so there won't be an arc coming down
			// straight from arcConnectionPoint in the middle of the node
			noOfArcsOnTheSide = arcsToDraw / 2;
			arcEndPointX = arcConnectionPointX - (((noOfArcsOnTheSide - 1) * spacing) + (spacing / 2));
		}
		else {
			// we have an odd number of arcs, so we will also have an arc
			// coming down straight from the middle of the component
			noOfArcsOnTheSide = ((arcsToDraw + 1) / 2) - 1;
			arcEndPointX = arcConnectionPointX - (noOfArcsOnTheSide * spacing);
		}

		// Set up arcs for required nodes	
		Iterator mapIterator = requiredChildNodesOrdered.iterator();
		while (mapIterator.hasNext()) {
			role = (String)mapIterator.next();
			arc = new PerformanceTreeArc(arcStartPointX, arcStartPointY, arcEndPointX, arcEndPointY, this, role, showArcLabels);
			arc.setRequired(true);
			
			// add the arc to the respective data structure
	    	if (MacroManager.getEditor() == null) {
	    		// draw arc onto QueryView (since we're not in macro creation mode
	    		arc = (PerformanceTreeArc)QueryManager.getData().addPerformanceTreeObject(arc);
	    	}
	    	else {
	    		// we're in macro creation mode, so draw the arc onto MacroView
	    		arc = (PerformanceTreeArc)MacroManager.getEditor().addPerformanceTreeObject(arc);	
	    	}
	    	
	    	// update node to indicate that it has an additional arc
	    	String arcID = arc.getId();
	    	outgoingArcIDs.add(arcID);
	    	requiredArcs.add(arc);
	    	
	    	// update and draw onto respective canvas
	    	if (MacroManager.getEditor() == null) {
	    		// not in macro creation mode, so update node in QueryData
	    		QueryManager.getData().updateNode(this);
	    		// get it to draw on the canvas
	        	QueryManager.getView().addNewPerformanceTreeObject(arc);
	    	}
	    	else {
	    		// we're in macro creation mode, so update node in MacroEditor
	    		MacroManager.getEditor().updateNode(this);
	    		// get it to draw on the canvas
	    		MacroManager.getView().addNewMacroObject(arc);
	    	}

			// Update coordinates for next arc
			arcEndPointX += spacing;
		}
		
		// Set up arcs for optional nodes	
		mapIterator = optionalChildNodesOrdered.iterator();
		while (mapIterator.hasNext()) {
			role = (String)mapIterator.next();
			arc = new PerformanceTreeArc(arcStartPointX, arcStartPointY, arcEndPointX, arcEndPointY, this, role, showArcLabels);
			arc.setRequired(false);
			
			// add the arc to the respective data structure
	    	if (MacroManager.getEditor() == null) {
	    		// draw arc onto QueryView (since we're not in macro creation mode
	    		arc = (PerformanceTreeArc)QueryManager.getData().addPerformanceTreeObject(arc);
	    	}
	    	else {
	    		// we're in macro creation mode, so draw the arc onto MacroView
	    		arc = (PerformanceTreeArc)MacroManager.getEditor().addPerformanceTreeObject(arc);	
	    	}
	    	
	    	// update node to indicate that it has an additional arc
	    	String arcID = arc.getId();
	    	outgoingArcIDs.add(arcID);
	    	optionalArcs.add(arc);
	    	
	    	// update and draw onto respective canvas
	    	if (MacroManager.getEditor() == null) {
	    		// not in macro creation mode, so update node in QueryData
	    		QueryManager.getData().updateNode(this);
	    		// get it to draw on the canvas
	        	QueryManager.getView().addNewPerformanceTreeObject(arc);
	    	}
	    	else {
	    		// we're in macro creation mode, so update node in MacroEditor
	    		MacroManager.getEditor().updateNode(this);
	    		// get it to draw on the canvas
	    		MacroManager.getView().addNewMacroObject(arc);
	    	}

			// Update coordinates for next arc
			arcEndPointX += spacing;
		}

	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
        g2d = (Graphics2D)g;
		
		// make sure node label follows node if node's being dragged around
		if (nodeLabel != null)
			if (!QueryManager.isEvaluatingQuery())
				displayNodeLabel();
	}
	
}
