/**
 * 
 */
package pipe.modules.queryeditor.evaluator.gui;

import javax.swing.AbstractAction;

/**
 * @author dazz
 * 
 */
public abstract class TabOpenAction extends AbstractAction
{

	final QueryOperationNode	node;

	TabOpenAction(final QueryOperationNode node) {
		this.node = node;
	}

}
