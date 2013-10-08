/**
 * PerformanceTreeObjectHandler
 * 
 * Class used to implement methods corresponding to mouse events on all PerformanceTreeObjects.
 * 
 * @author Tamas Suto
 * @date 22/04/07
 */

package pipe.modules.queryeditor.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.MouseInputAdapter;

import pipe.common.PetriNetNode;
import pipe.modules.interfaces.QueryConstants;
import pipe.gui.Grid;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeArc;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeArcPathPoint;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeNode;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeObject;
import pipe.modules.queryeditor.gui.performancetrees.macros.ArgumentNode;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroEditor;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroManager;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroNode;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroView;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.FiringRateNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.MomentNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.ResultNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.SequentialNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.NumNode;

public class PerformanceTreeObjectHandler extends MouseInputAdapter implements QueryConstants
{

	final Container				contentPane;
	PerformanceTreeObject	myObject		= null;
	static boolean		justSelected	= false;		// set to
	// true on
	// press,
	// and false
	// on
	// release;
    boolean				isDragging		= false;
	boolean				enablePopup		= false;
	Point					dragInit		= new Point();

	PerformanceTreeObjectHandler(final Container contentpane, final PerformanceTreeObject obj) {
		this.contentPane = contentpane;
		this.myObject = obj;
	}

	public void enablePopupMenu(final boolean allow)
	{
		this.enablePopup = allow;
	}

	/**
	 * Creates the popup menu that the user will see when they right click on a
	 * component
     * @param e
     * @return
     */
    JPopupMenu getPopup(final MouseEvent e)
	{
		JPopupMenu popup = new JPopupMenu();
		if (myObject.enablePopup){
			JMenuItem menuItem = new JMenuItem(new DeletePerformanceTreeObjectAction(this.myObject));
			menuItem.setText("Delete");
			popup.add(menuItem);
		}
		return popup;
	}

	/** Displays the popup menu
     * @param e*/
	private void checkForPopup(final MouseEvent e)
	{
		if (myObject.enablePopup){
			if (e.isPopupTrigger())
			{
				JPopupMenu m = getPopup(e);
				if (m != null)
					m.show(this.myObject, e.getX(), e.getY());
			}
		}
	}

	@Override
	public void mousePressed(final MouseEvent e)
	{
		if (this.enablePopup)
			checkForPopup(e);

		int switchCondition;
		if (MacroManager.getEditor() == null)
			switchCondition = QueryManager.getMode();
		else switchCondition = MacroManager.getMode();
		switch (switchCondition)
		{
			case SELECT :
			{
				if (!this.myObject.isSelected())
				{
					if (!e.isShiftDown())
					{
						if (MacroManager.getEditor() == null)
							((QueryView) this.contentPane).getSelectionObject().clearSelection();
						else ((MacroView) this.contentPane).getSelectionObject().clearSelection();
					}
					this.myObject.select();
					PerformanceTreeObjectHandler.justSelected = true;
				}
				// record the point at which we clicked
				this.dragInit = e.getPoint();
			}
		}
	}

	/**
	 * Handler for dragging PerformanceTreeObjects around
	 */
	@Override
	public void mouseDragged(final MouseEvent e)
	{
		int switchCondition;
		if (MacroManager.getEditor() == null)
			switchCondition = QueryManager.getMode();
		else switchCondition = MacroManager.getMode();
		switch (switchCondition)
		{
			case SELECT :
			{
				boolean oneSelectionObject;
				if (MacroManager.getEditor() == null)
					oneSelectionObject = ((QueryView) this.contentPane)	.getSelectionObject()
																		.getSelectionCount() == 1;
				else oneSelectionObject = ((MacroView) this.contentPane).getSelectionObject()
																		.getSelectionCount() == 1;

				if (!this.isDragging && oneSelectionObject)
				{
					if (this.myObject instanceof PerformanceTreeNode)
					{
						// we're moving around a node
						// update node's centre to current location
						((PerformanceTreeNode) this.myObject).setCentre(Grid.getModifiedX(((PerformanceTreeNode) this.myObject)	.getCentre()
																																.getX()),
																		Grid.getModifiedY(((PerformanceTreeNode) this.myObject)	.getCentre()
																																.getY()));
						// update natural language representation of query
						QueryManager.printNaturalLanguageRepresentation();
					}
					else if (this.myObject instanceof PerformanceTreeArcPathPoint)
					{
						// we're moving around an arc point
						// update point's position to cursor's current location
						((PerformanceTreeArcPathPoint) this.myObject).setPointLocation(	Grid.getModifiedX(((PerformanceTreeArcPathPoint) this.myObject)	.getPoint()
																																						.getX()),
																						Grid.getModifiedY(((PerformanceTreeArcPathPoint) this.myObject)	.getPoint()
																																						.getY()));

						// clear InfoBox in case there was a message there
						if (MacroManager.getEditor() == null)
							QueryManager.clearInfoBox();
						else MacroEditor.writeToInfoBox("");

						// check if it's the endpoint of an arc that's being
						// dragged. If so, indicate that
						// the arc is being modified
						PerformanceTreeArcPathPoint thisPoint = (PerformanceTreeArcPathPoint) this.myObject;
						PerformanceTreeArc thisPointsArc = thisPoint.getArcPath().getArc();
						String thisPointsArcID = thisPointsArc.getId();

						int thisPointsIndex = thisPoint.getIndex();
						int arcEndpointsIndex = thisPoint.getArcPath().getEndIndex();

						if (thisPointsIndex == arcEndpointsIndex)
						{
							// the arcPathPoint is the endpoint of the arc being
							// modified, so indicate that
							// through the variable in QueryView and assign the
							// arc to the temp variable in
							// QueryView, so that it can be accessed upon
							// release
							PerformanceTreeArc storedArc;
							if (MacroManager.getEditor() == null)
								storedArc = QueryManager.getData().getArc(thisPointsArcID);
							else storedArc = MacroManager.getEditor().getArc(thisPointsArcID);

							if (storedArc != null)
							{
								if (MacroManager.getEditor() == null)
								{
									// not in macro mode
									QueryManager.getView().setArcBeingModified(storedArc);
									if (QueryManager.getView().getArcBeingModified().getTarget() != null)
									{
										// we're trying to drag an arc endpoint
										// away from the node that the
										// arc was connected to so far, so make
										// the necessary adjustments
										// in the arc, nodes and data stuctures

										// debug
// System.out.println("Before arc disconnect in mouseDragged");
// System.out.println("Arc info");
// printArcData(storedArc);
// System.out.println("Source node info");
// if (storedArc.getSource() != null) {
// PerformanceTreeNode dataSource = storedArc.getSource();
// printNodeData(dataSource);
// }
// System.out.println("Target node info");
// if (storedArc.getTarget() != null) {
// PerformanceTreeNode dataTarget = storedArc.getTarget();
// printNodeData(dataTarget);
// }
// System.out.println("QueryData info");
// QueryManager.getData().printQueryDataContents();
										// debug

										performArcDisconnect();

										// debug
// System.out.println("After arc disconnect in mouseDragged");
// // arc has been updated in the meantime, so get the updated version
// System.out.println("Arc info");
// PerformanceTreeArc updatedStoredArc =
// QueryManager.getData().getArc(storedArc.getId());
// printArcData(updatedStoredArc);
// System.out.println("Source node info");
// if (updatedStoredArc.getSource() != null) {
// PerformanceTreeNode dataSource = updatedStoredArc.getSource();
// printNodeData(dataSource);
// }
// System.out.println("Target node info");
// if (updatedStoredArc.getTarget() != null) {
// PerformanceTreeNode dataTarget = updatedStoredArc.getTarget();
// printNodeData(dataTarget);
// }
// System.out.println("QueryData info");
// QueryManager.getData().printQueryDataContents();
										// debug
									}
								}
								else
								{
									// in macro mode
									MacroManager.getView().setArcBeingModified(storedArc);
									if (MacroManager.getView().getArcBeingModified().getTarget() != null)
									{
										performArcDisconnect();
									}
								}
							}
						}
					}
				}

				if (this.myObject.isDraggable())
					this.isDragging = true;

				// Perform translation and visualisation
				int transX = Grid.getModifiedX(e.getX() - this.dragInit.x);
				int transY = Grid.getModifiedY(e.getY() - this.dragInit.y);

				if (MacroManager.getEditor() == null)
					((QueryView) this.contentPane).getSelectionObject().translateSelection(transX, transY);
				else ((MacroView) this.contentPane).getSelectionObject().translateSelection(transX, transY);
			}
		}
	}

	/**
	 * Performs maintenance operations after having disconnected an arc from a
	 * node.
	 */
	private void performArcDisconnect()
	{
		PerformanceTreeArc arc;
		PerformanceTreeNode target;

		if (MacroManager.getEditor() == null)
		{
			// not in macro mode
			arc = QueryManager.getView().getArcBeingModified();
			target = arc.getTarget();

			// remove target from arc
			arc.setTarget(null);
			QueryManager.getData().updateArc(arc);

			// remove incoming arc from the ex-target node
			target.setIncomingArcID(null);
			target.removeArcCompareObject(arc);
			QueryManager.getData().updateNode(target);
		}
		else
		{
			// in macro mode
			arc = MacroManager.getView().getArcBeingModified();
			target = arc.getTarget();

			// remove target from arc
			arc.setTarget(null);
			MacroManager.getEditor().updateArc(arc);

			// remove incoming arc from the ex-target node
			target.setIncomingArcID(null);
			target.removeArcCompareObject(arc);
			MacroManager.getEditor().updateNode(target);
		}
	}

	/**
	 * Event handler for when the user releases the mouse, used in conjunction
	 * with mouseDragged and mouseReleased to implement the moving action
	 */
	@Override
	public void mouseReleased(final MouseEvent e)
	{
		// Have to check for popup here as well as on pressed for
		// crossplatform!!
		if (this.enablePopup)
			checkForPopup(e);

		int switchCondition;
		if (MacroManager.getEditor() == null)
			switchCondition = QueryManager.getMode();
		else switchCondition = MacroManager.getMode();

		switch (switchCondition)
		{
			case SELECT :
				if (this.isDragging)
				{
					this.isDragging = false;
					if (this.myObject instanceof PerformanceTreeArcPathPoint)
					{
						// This deals with what happens when the mouse is
						// released over a node
						PerformanceTreeArc arc;
						if (MacroManager.getEditor() == null)
							arc = QueryManager.getView().getArcBeingModified();
						else arc = MacroManager.getView().getArcBeingModified();
						if (arc != null)
						{
							// we are adjusting the endpoint of an arc
							PerformanceTreeArcPathPoint arcPoint = (PerformanceTreeArcPathPoint) this.myObject;
							// check all components on the view and see if the
							// current location of the point
							// falls into the bounds of a node
							Component[] viewObjects;
							if (MacroManager.getEditor() == null)
								viewObjects = QueryManager.getView().getComponents();
							else viewObjects = MacroManager.getView().getComponents();
                            for(Component viewObject : viewObjects)
                            {
                                if(viewObject instanceof PerformanceTreeNode)
                                {
                                    PerformanceTreeNode node = (PerformanceTreeNode) viewObject;
                                    // check if we released the point over a
                                    // node
                                    if(endpointWithinNodeBounds(arcPoint, node))
                                    {
                                        // we did, since the point is located
                                        // within the bounds of a node
                                        PerformanceTreeNode nodeToLinkUpWith = (PerformanceTreeNode) viewObject;
                                        // check that the node isn't the source
                                        // node of the arc that the point
                                        // belongs to
                                        if(!nodeToLinkUpWith.getId().equals(arc.getSourceID()))
                                        {
                                            // it isn't, so establish new
                                            // connection between the arc and
                                            // the node
                                            // make sure that that node doesn't
                                            // already have another arc
                                            // connected to it,
                                            // linking it to its parent node
                                            if(nodeToLinkUpWith.getIncomingArcID() == null)
                                            {
                                                // only connect arc to node if
                                                // it doesn't have a "parent
                                                // arc"
                                                PerformanceTreeNode sourceNode = arc.getSource();
                                                // check if node assignment is
                                                // valid according to
                                                // Performance Tree semantics
                                                if(arcConnectionValid(arc, nodeToLinkUpWith))
                                                {
                                                    // valid, so go ahead with
                                                    // the connection
                                                    performArcConnect(nodeToLinkUpWith);
                                                    // take care of the special
                                                    // case of SequentialNode
                                                    // (optional arcs appearing)
                                                    sequentialNodeCase(sourceNode);
                                                    // print out current textual
                                                    // representation of the
                                                    // tree
                                                    QueryManager.printNaturalLanguageRepresentation();
                                                    // do the translation
                                                    int transX = Grid.getModifiedX(e.getX() - this.dragInit.x);
                                                    int transY = Grid.getModifiedY(e.getY() - this.dragInit.y);
                                                    if(MacroManager.getEditor() == null)
                                                        ((QueryView) this.contentPane).getSelectionObject()
                                                                .translateSelection(transX,
                                                                                    transY);
                                                    else ((MacroView) this.contentPane).getSelectionObject()
                                                            .translateSelection(transX,
                                                                                transY);
                                                }
                                            }
                                            else
                                            {
                                                // write info message
                                                String message = "You are trying to connect to a node that is already connected elsewhere. "
                                                        + "First break the existing connection and then try connecting again.";
                                                writeErrorMessage(message);
                                            }
                                        }
                                        else
                                        {
                                            // write info message
                                            String message = "You are trying to assign the node to itself. Please drag the endpoint "
                                                    + "of the arc to a valid node in order to establish a connection.";
                                            writeErrorMessage(message);
                                        }
                                    }
                                    else
                                    {
                                        // we dragged the endpoint of the arc
                                        // somewhere on the canvas
                                        int transX = Grid.getModifiedX(e.getX() - this.dragInit.x);
                                        int transY = Grid.getModifiedY(e.getY() - this.dragInit.y);
                                        if(MacroManager.getEditor() == null)
                                            ((QueryView) this.contentPane).getSelectionObject()
                                                    .translateSelection(transX,
                                                                        transY);
                                        else ((MacroView) this.contentPane).getSelectionObject()
                                                .translateSelection(transX,
                                                                    transY);
                                    }
                                }
                            }

							// indicate that we have finished with modifying the
							// arc
							if (MacroManager.getEditor() == null)
							{
								QueryManager.getView().setArcBeingModified(null);
								QueryManager.getView().setShiftDown(false);
							}
							else
							{
								MacroManager.getView().setArcBeingModified(null);
								MacroManager.getView().setShiftDown(false);
							}
						}
					}
				}
				else
				{
					if (!PerformanceTreeObjectHandler.justSelected)
					{
						if (e.isShiftDown())
							this.myObject.deselect();
						else
						{
							if (MacroManager.getEditor() == null)
								((QueryView) this.contentPane).getSelectionObject().clearSelection();
							else ((MacroView) this.contentPane).getSelectionObject().clearSelection();
							this.myObject.select();
						}
					}
				}
				break;
		}
		PerformanceTreeObjectHandler.justSelected = false;
	}

	private void writeErrorMessage(final String message)
	{
		String msg = QueryManager.addColouring(message);
		if (MacroManager.getEditor() == null)
			QueryManager.writeToInfoBox(msg);
		else MacroEditor.writeToInfoBox(msg);
	}

	/**
	 * Checks if a point is located within the bounds of a node
	 * 
	 * @param arcPoint
	 * @param node
     * @return
	 */
	private boolean endpointWithinNodeBounds(	final PerformanceTreeArcPathPoint arcPoint,
												final PerformanceTreeNode node)
	{
		double pointLocationX = arcPoint.getPoint().getX();
		double pointLocationY = arcPoint.getPoint().getY();
		double nodeMinX = node.getPositionX();
		double nodeMinY = node.getPositionY();
		double nodeMaxX = nodeMinX + node.getComponentWidth();
		double nodeMaxY = nodeMinY + node.getComponentHeight();

        return pointLocationX >= nodeMinX && pointLocationX <= nodeMaxX && pointLocationY >= nodeMinY &&
                pointLocationY <= nodeMaxY;
	}

	/**
	 * This method checks various cases that need to be considered when trying
	 * to connect one node to another
	 * 
	 * @param sourceNode
	 * @param arc
     * @param targetNode
	 * @return
	 */
	private boolean arcConnectionValid(final PerformanceTreeArc arc, final PerformanceTreeNode targetNode)
	{
		boolean arcConnectionOK = false;
		PerformanceTreeNode sourceNode = arc.getSource();

		if (sourceNode.childAssignmentValid(arc, targetNode) || MacroManager.getEditor() != null &&
			targetNode instanceof ArgumentNode && !(sourceNode instanceof MacroNode))
		{
			// general PT semantics & ArgumentNode
			if (momentNodeCaseChecked(arc, targetNode))
			{
				// decimal number case for MomentNode and NumNode
				arcConnectionOK = true;
			}
			else
			{
				String message = "The node you are trying to connect to the Moment node "
									+ "returns a decimal value, which is invalid in this scenario. "
									+ "Only integer values are defined for moments.";
				writeErrorMessage(message);
			}
		}
		else
		{
			PetriNetNode sourceNodeType = sourceNode.getNodeType();
			PetriNetNode targetNodeType = targetNode.getNodeType();
			String arcRole = arc.getArcLabel();
			String message;
			if (targetNode instanceof MacroNode)
			{
				message = "A " +
							targetNodeType +
							" node cannot be assigned to another node before " +
							"it hasn't been assigned a macro definition. Please assign a macro definition to " +
							"the node and try again.";
			}
			else
			{
				if (sourceNode instanceof ResultNode)
				{
					message = "A " + targetNodeType + " node cannot be assigned to a " + sourceNodeType +
								" node.";
				}
				else
				{
					message = "A " + targetNodeType + " node cannot be assigned to a " + sourceNodeType +
								" node with role " + arcRole + ".";
				}
			}
			writeErrorMessage(message);
		}

		return arcConnectionOK;
	}

	/**
	 * This method checks the special case when we're connecting a MomentNode to
	 * a NumNode and that NumNode already has a double value assigned to it. We
	 * only want an integer value, since we're representing the number of the
	 * moment with that node.
	 * 
	 * @param arc
	 * @param nodeToLinkUpWith
	 * @return
	 */
	private boolean momentNodeCaseChecked(	final PerformanceTreeArc arc,
											final PerformanceTreeNode nodeToLinkUpWith)
	{
		boolean okToGoAhead = false;

		if (arc.getSource() instanceof MomentNode && nodeToLinkUpWith instanceof NumNode)
		{
			NumNode childNode = (NumNode) nodeToLinkUpWith;
			if (childNode.getNumObject() != null)
			{
				double childValue = childNode.getNumValue();
                okToGoAhead = isDecimalAnInteger(childValue);
			}
			else
			{
				// no value has been specified for the NumNode yet, so nothing
				// to worry about
				okToGoAhead = true;
			}
		}
		else
		{
			// it's not the special case, so we don't worry about it
			okToGoAhead = !(nodeToLinkUpWith instanceof FiringRateNode && arc.getSource() instanceof MomentNode);
		}

		return okToGoAhead;
	}

	/**
	 * Performs the necessary actions upon a new arc connection
	 * 
	 * @param arc
	 * @param nodeToLinkUpWith
	 */
	private void performArcConnect(final PerformanceTreeNode nodeToLinkUpWith)
	{
		if (MacroManager.getEditor() == null)
		{
			// not in macro mode
			PerformanceTreeArc arc = QueryManager.getView().getArcBeingModified();
			String arcID = arc.getId();

			// update arc's endpoint to being the middle of the top side of the
			// node and
			// update its information about its target node
			arc.setSelectable(true);
			arc = updateArcEndPoint(arc, nodeToLinkUpWith);
			arc.setTarget(nodeToLinkUpWith);
			QueryManager.getData().updateArc(arc);

			// let target-to-be node know that there's an incoming arc
			// connecting to it
			nodeToLinkUpWith.setIncomingArcID(arcID);
			QueryManager.getData().updateNode(nodeToLinkUpWith);
		}
		else
		{
			// in macro mode
			PerformanceTreeArc arc = MacroManager.getView().getArcBeingModified();
			String arcID = arc.getId();

			// update arc's endpoint to being the middle of the top side of the
			// node and
			// update its information about its target node
			arc.setSelectable(true);
			arc = updateArcEndPoint(arc, nodeToLinkUpWith);
			arc.setTarget(nodeToLinkUpWith);
			MacroManager.getEditor().updateArc(arc);

			// let target-to-be node know that there's an incoming arc
			// connecting to it
			nodeToLinkUpWith.setIncomingArcID(arcID);
			MacroManager.getEditor().updateNode(nodeToLinkUpWith);
		}
	}

	/**
	 * Sets the endpoint coordinates of the arc to be at the middle of the node.
	 * Snap-to methods will take care of appropriate alignment afterwards.
	 * 
	 * @param arc
	 * @param node
     * @return
	 */
	private PerformanceTreeArc updateArcEndPoint(final PerformanceTreeArc arc, final PerformanceTreeNode node)
	{
		double newEndPointX = node.getPositionX() + node.getComponentWidth() / 2;
		double newEndPointY = node.getPositionY();
		arc.setTargetLocation(newEndPointX, newEndPointY);
		return arc;
	}

	/**
	 * This method checks after each node assignment whether the assignment was
	 * made between a SequentialNode and some other node. In case it was, it
	 * needs to check to see whether a new optional arc needs to be drawn once
	 * all arcs have been linked up with nodes.
	 * 
	 * @param sourceNode
	 */
	private void sequentialNodeCase(final PerformanceTreeNode sourceNode)
	{
		if (sourceNode instanceof SequentialNode)
		{
			if (((SequentialNode) sourceNode).allArcsAssigned() &&
				!((SequentialNode) sourceNode).haveOptionalArcAvailable())
			{
				// all current arcs of SequentialNode have been assigned to
				// nodes
				// and there's no available optional arc, so create one
				((SequentialNode) sourceNode).drawAdditionalOptionalArc();
			}
		}
	}

	private boolean isDecimalAnInteger(final double decimalNo)
	{
		boolean decimalIsAnInteger = true;
		String stringRepresentation = Double.toString(decimalNo);

		if (!stringRepresentation.equals(""))
		{
			int indexOfDecimalDot = stringRepresentation.indexOf(".");
			if (indexOfDecimalDot != -1)
			{
				// all characters after the decimal dot (if one exists) have to
				// be a 0
				for (int i = indexOfDecimalDot + 1; i < stringRepresentation.length(); i++)
				{
					char chr = stringRepresentation.charAt(i);
					if (!String.valueOf(chr).equals("0"))
					{
						decimalIsAnInteger = false;
						break;
					}
				}
			}
		}
		return decimalIsAnInteger;
	}
}
