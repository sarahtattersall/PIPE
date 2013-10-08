/**
 * DeletePerformanceTreeObjectAction
 * 
 * @author Tamas Suto
 * @date 22/04/07
 */

package pipe.modules.queryeditor.gui;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.AbstractAction;

import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeArc;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeNode;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeObject;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroManager;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.ResultNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.SequentialNode;


class DeletePerformanceTreeObjectAction extends AbstractAction {

	private final PerformanceTreeObject selected;

	public DeletePerformanceTreeObjectAction(PerformanceTreeObject component) {
		selected = component;
	}		

	/**
	 * This only corresponds to the Delete action that is invoked from
	 * the right-click menu. If something is selected and the delete button
	 * pressed, these actions won't apply. That's catered for in
	 * PerformanceTreeSelectionObject#deleteSelection
	 */
	public void actionPerformed(ActionEvent e) {
		if (selected instanceof PerformanceTreeNode) {
			if (selected instanceof ResultNode) {
				String msg = QueryManager.addColouring("Deletion of the topmost node in the tree is not permitted.");
				QueryManager.writeToInfoBox(msg);
			}
			else if (selected instanceof MacroNode && MacroManager.getEditor() != null) {
				String msg = QueryManager.addColouring("Deletion of the topmost macro node in the tree is not permitted.");
				MacroManager.getEditor().writeToInfoBox(msg);
			}
			else {
				if(!sequentialNodeCase()) {
					// just delete the node, not the associated arc
					selected.delete();
				}
			}
		}	
		else {
			selected.delete();
		}
	}
	
	/** This method takes care of the case when a node is linked directly to
	 *  a SequentialNode through an optional arc. In such as case, the arc 
	 *  should be removed along with the node.
	 * @return
	 */
	private boolean sequentialNodeCase() {
		PerformanceTreeNode node = (PerformanceTreeNode)selected;
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
