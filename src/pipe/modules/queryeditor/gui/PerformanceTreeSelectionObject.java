/**
 * PerformanceTreeSelectionObject
 * 
 * Handles selection rectangle functionality
 * 
 * @author Tamas Suto
 * @date 22/04/07
 */

package pipe.modules.queryeditor.gui;

import pipe.modules.interfaces.QueryConstants;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.performancetrees.*;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroManager;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroNode;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroView;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.ResultNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.SequentialNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Iterator;


public class PerformanceTreeSelectionObject extends JComponent implements MouseListener, MouseMotionListener, QueryConstants
{

	private static final long serialVersionUID = 1L;

	private Point selectionInit;
	private final Rectangle selectionRectangle = new Rectangle(-1,-1);
	private final Rectangle tempBounds = new Rectangle();
	private static final Color selectionColor = new Color(0,0,255,30);
	private static final Color selectionColorOutline = new Color(0,0,100);	
	private boolean isSelecting;
	private boolean enabled;
	private JLayeredPane edit_window;


	public PerformanceTreeSelectionObject(QueryView _edit_window) {
		this();
		edit_window = _edit_window;
	}

	public PerformanceTreeSelectionObject(MacroView _edit_window) {
		this();
		edit_window = _edit_window;
	}

	private PerformanceTreeSelectionObject() {
		addMouseListener(this);
		addMouseMotionListener(this);
	}


	public void enableSelection() {
		if (!enabled) {			
			edit_window.add(this);
			enabled = true;
			updateBounds();
		}
	}

	public void disableSelection() {
		if (enabled) {
			edit_window.remove(this);
			enabled = false;
		}
	}

	public void updateBounds() {
		if (enabled) {
			setBounds(0,0,edit_window.getWidth(),edit_window.getHeight());	
		}
	}

	private void processSelection(MouseEvent e) {
		if (!e.isShiftDown()) 
			clearSelection();

		// Get all the objects in the current window
		Component netObj[] = edit_window.getComponents();

        for(Component aNetObj : netObj)
        {
            // Handle PerformanceTreeNodes
            if((aNetObj instanceof PerformanceTreeNode) && ((PerformanceTreeObject) aNetObj).isSelectable())
            {
                if(selectionRectangle.intersects(aNetObj.getBounds(tempBounds)))
                {
                    ((PerformanceTreeNode) aNetObj).select();
                }
            }

            // Handle PerformanceTreeArcs
            if((aNetObj instanceof PerformanceTreeArc) && ((PerformanceTreeObject) aNetObj).isSelectable())
            {
                PerformanceTreeArc thisArc = (PerformanceTreeArc) aNetObj;
                PerformanceTreeArcPath thisArcPath = thisArc.getArcPath();
                if(thisArcPath.proximityIntersects(selectionRectangle))
                    thisArcPath.showPoints();
                else
                    thisArcPath.hidePoints();
                if(thisArcPath.intersects(selectionRectangle))
                {
                    thisArc.select();
                }
            }

            // Handle PerformanceTreeArcPathPoints
            if((aNetObj instanceof PerformanceTreeArcPathPoint) && ((PerformanceTreeObject) aNetObj).isSelectable())
            {
                if(selectionRectangle.intersects(aNetObj.getBounds(tempBounds)))
                {
                    ((PerformanceTreeArcPathPoint) aNetObj).select();
                }
            }
        }
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;	
		g2d.setPaint(selectionColor);
		g2d.fill(selectionRectangle);
		g2d.setPaint(selectionColorOutline);
		g2d.draw(selectionRectangle);
	}

	public boolean contains(int x, int y) {
		return true;
	}

	public void mouseClicked(MouseEvent arg0) {
		// Not needed
	}

	public void mouseEntered(MouseEvent arg0) {
		// Not needed
	}

	public void mouseExited(MouseEvent arg0) {
		// Not needed
	}

	public void mousePressed(MouseEvent arg0) {		
		isSelecting = true;
		edit_window.setLayer(this, SELECTION_LAYER_OFFSET);
		selectionInit = arg0.getPoint();
		selectionRectangle.setRect(selectionInit.getX(), selectionInit.getY(), 0, 0);
		// Select anything that intersects with the rectangle.
		processSelection(arg0);	
		repaint();
	}

	public void mouseDragged(MouseEvent arg0) {
		if (isSelecting) {
			selectionRectangle.setSize((int)Math.abs(arg0.getX()-selectionInit.getX()),
					(int)Math.abs(arg0.getY()-selectionInit.getY()));
			selectionRectangle.setLocation((int)Math.min(selectionInit.getX(), arg0.getX()),
					(int)Math.min(selectionInit.getY(), arg0.getY()));
			// Select anything that intersects with the rectangle.
			processSelection(arg0);	
		}
		repaint();
	}

	public void mouseReleased(MouseEvent arg0) {
		if (isSelecting) {		
			isSelecting = false;
			edit_window.setLayer(this, LOWEST_LAYER_OFFSET);
			selectionRectangle.setRect(-1,-1,0,0);
			repaint();
		}
	}

	public void mouseMoved(MouseEvent arg0) {
		// Not needed
	}

	public void deleteSelection() {
		if (MacroManager.getEditor() == null)
			QueryManager.clearInfoBox();
		else
			MacroManager.getEditor().writeToInfoBox("");

		Component[] netObj;
		if (MacroManager.getEditor() == null)
			netObj = edit_window.getComponents();
		else 
			netObj = edit_window.getComponents();

        for(Component aNetObj : netObj)
        {
            if((aNetObj instanceof PerformanceTreeObject) && (((PerformanceTreeObject) aNetObj).isSelected()))
            {
                if(aNetObj instanceof PerformanceTreeArc)
                {
                    if(QueryManager.allowDeletionOfArcs)
                    {
                        ((PerformanceTreeArc) aNetObj).delete();
                    }
                }
                else if(aNetObj instanceof PerformanceTreeNode)
                {
                    //delete node is not allowed in text query editor mode
                    if(((PerformanceTreeNode) aNetObj).enablePopup)
                    {
                        if(aNetObj instanceof ResultNode)
                        {
                            String msg = QueryManager.addColouring("Deletion of the topmost node in the tree is not permitted.");
                            if(MacroManager.getEditor() == null)
                                QueryManager.writeToInfoBox(msg);
                            else
                                MacroManager.getEditor().writeToInfoBox(msg);
                        }
                        else if((aNetObj instanceof MacroNode) && MacroManager.getEditor() != null)
                        {
                            String msg = QueryManager.addColouring("Deletion of the topmost macro node in the tree is not permitted.");
                            MacroManager.getEditor().writeToInfoBox(msg);
                        }
                        else
                        {
                            if(!sequentialNodeCase((PerformanceTreeNode) aNetObj))
                            {
                                // just delete the node, not the associated arc
                                ((PerformanceTreeNode) aNetObj).delete();
                            }
                        }
                        //not permit delete in text editing mode
                    }
                    else
                    {
                        String msg = QueryManager.addColouring("Deletion in the text query editing mode is not permitted.");
                        if(MacroManager.getEditor() == null)
                            QueryManager.writeToInfoBox(msg);
                        else
                            MacroManager.getEditor().writeToInfoBox(msg);
                    }
                }
                else
                {
                    ((PerformanceTreeObject) aNetObj).delete();
                }
            }
        }
	}

	public void clearSelection() {
		Component netObj[] = edit_window.getComponents();
		// Get all the objects in the current window
        for(Component aNetObj : netObj)
        {
            if((aNetObj instanceof PerformanceTreeArc) && ((PerformanceTreeObject) aNetObj).isSelectable())
            {
                PerformanceTreeArc thisArc = (PerformanceTreeArc) aNetObj;
                thisArc.deselect();
                PerformanceTreeArcPath thisArcPath = thisArc.getArcPath();
                thisArcPath.hidePoints();
                for(int j = 1; j < thisArcPath.getEndIndex(); j++)
                {
                    thisArcPath.deselectPoint(j);
                }
            }
            else if((aNetObj instanceof PerformanceTreeObject) && ((PerformanceTreeObject) aNetObj).isSelectable())
            {
                ((PerformanceTreeObject) aNetObj).deselect();
            }
        }
	}

	public void translateSelection(int transX, int transY) {
		if(transX == 0 && transY == 0)
			return;
		// Get all the objects in the current window
		Component netObj[] = edit_window.getComponents();	
		// First see if translation will put anything at a negative location
		Point p,topleft = null;
        for(Component aNetObj1 : netObj)
        {
            if(aNetObj1 instanceof PerformanceTreeObject)
            {
                if(((PerformanceTreeObject) aNetObj1).isSelected())
                {
                    p = aNetObj1.getLocation();
                    if(topleft == null)
                    {
                        topleft = p;
                    }
                    else
                    {
                        if(p.x < topleft.x)
                            topleft.x = p.x;
                        if(p.y < topleft.y)
                            topleft.y = p.y;
                    }
                }
            }
        }

		if(topleft!=null) {
			topleft.translate(transX,transY);
			if(topleft.x<0) 
				transX -= topleft.x;
			if(topleft.y<0) 
				transY -= topleft.y;
			if(transX==0 && transY==0)
				return;
		}

        for(Component aNetObj : netObj)
        {
            if(aNetObj instanceof PerformanceTreeNode)
            {
                if(((PerformanceTreeNode) aNetObj).isSelected())
                {
                    // Translate the object
                    ((PerformanceTreeNode) aNetObj).translate(transX, transY);
                    // Update all attached arcs to the new location
                    ((PerformanceTreeNode) aNetObj).updateConnected();
                }
            }
            else if(aNetObj instanceof PerformanceTreeArc)
            {
                PerformanceTreeArc thisArc = (PerformanceTreeArc) aNetObj;
                for(int j = 1; j <= thisArc.getArcPath().getEndIndex(); j++)
                    if(thisArc.getArcPath().isPointSelected(j))
                        thisArc.getArcPath().translatePoint(j, transX, transY);
                thisArc.updateArcPosition();
                thisArc.updateLabelPosition();
            }
        }

		if (MacroManager.getEditor() == null)
			((QueryView)edit_window).updatePreferredSize();
		else 
			((MacroView)edit_window).updatePreferredSize();
	}

	public int getSelectionCount() {
		Component netObj[] = edit_window.getComponents();
		int selectionCount = 0;
		// Get all the objects in the current window
        for(Component aNetObj : netObj)
        {
            // Handle PerformanceTreeNodes
            if((aNetObj instanceof PerformanceTreeNode) && ((PerformanceTreeObject) aNetObj).isSelectable())
            {
                if(((PerformanceTreeNode) aNetObj).isSelected())
                {
                    selectionCount++;
                }
            }

            // Handle Arcs and PerformanceTreeArc Points
            if((aNetObj instanceof PerformanceTreeArc) && ((PerformanceTreeObject) aNetObj).isSelectable())
            {
                PerformanceTreeArc thisArc = (PerformanceTreeArc) aNetObj;
                PerformanceTreeArcPath thisArcPath = thisArc.getArcPath();
                for(int j = 1; j <= thisArcPath.getEndIndex(); j++)
                {
                    if(thisArcPath.isPointSelected(j))
                    {
                        selectionCount++;
                    }
                }
            }
        }
		return selectionCount;
	}

	/** This method takes care of the case when a node is linked directly to
	 *  a SequentialNode through an optional arc. In such as case, the arc 
	 *  should be removed along with the node.
	 * @param node
     * @return
	 */
	private boolean sequentialNodeCase(PerformanceTreeNode node) {
		if (node.getIncomingArc() != null) {
			PerformanceTreeArc incomingArc = node.getIncomingArc();
			PerformanceTreeNode parentNode = incomingArc.getSource();
			if (!incomingArc.isRequired() && 
					parentNode instanceof SequentialNode &&
					sequentialNodeHasAtLeastOneOptionalArc(parentNode)) {
				node.delete();
				incomingArc.delete();
				return true;
			}
			else
				return false;
		}
		else
			return false;
	}

	/**
	 * We should only allow deletion of the associated optional arc if there are
	 * at least two optional arcs. This is so, because a new optional arc is only
	 * created whenever the last free arc is assigned to a node.
	 * @param node
	 * @return
	 */
	private boolean sequentialNodeHasAtLeastOneOptionalArc(PerformanceTreeNode node) {
		if (node instanceof SequentialNode) {
			SequentialNode seqNode = (SequentialNode)node;
			ArrayList<String> outgoingArcIDs = (ArrayList<String>)seqNode.getOutgoingArcIDs();
			Iterator<String> i = outgoingArcIDs.iterator();
			int optionalArcCount = 0;
			while (i.hasNext()) {
				PerformanceTreeArc arc = QueryManager.getData().getArc(i.next());
				if (!arc.isRequired()) 
					optionalArcCount++;
			}
            return optionalArcCount > 1;
		}
		else return false;
	}
}
