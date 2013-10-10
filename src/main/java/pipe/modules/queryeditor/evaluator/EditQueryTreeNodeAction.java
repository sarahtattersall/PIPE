/**
 * EditQueryTreeNodeAction
 * 
 * @author Tamas Suto
 * @date 25/11/07
 */

package pipe.modules.queryeditor.evaluator;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import pipe.modules.interfaces.QueryConstants;
import pipe.modules.queryeditor.evaluator.gui.QueryOperationNode;

class EditQueryTreeNodeAction extends AbstractAction implements QueryConstants
{

	/**
	 * 
	 */
	private static final long			serialVersionUID	= -6655411271193095285L;
	private final QueryOperationNode	node;
	private final String				actionType;

	public EditQueryTreeNodeAction(final QueryOperationNode nodeInput, final String actionTypeInput) {
		this.node = nodeInput;
		this.actionType = actionTypeInput;
	}

	public void actionPerformed(final ActionEvent e)
	{
		if (this.actionType.equals(QueryConstants.EVALNOTSUPPORTED))
		{
			this.node.setStatus(QueryConstants.EVALNOTSUPPORTED);
		}
		else if (this.actionType.equals(QueryConstants.EVALNOTSTARTED))
		{
			this.node.setStatus(QueryConstants.EVALNOTSTARTED);
		}
		else if (this.actionType.equals(QueryConstants.EVALINPROGRESS))
		{
			this.node.setStatus(QueryConstants.EVALINPROGRESS);
		}
		else if (this.actionType.equals(QueryConstants.EVALCOMPLETE))
		{
			this.node.setStatus(QueryConstants.EVALCOMPLETE);
		}
		else if (this.actionType.equals(QueryConstants.EVALFAILED))
		{
			this.node.setStatus(QueryConstants.EVALFAILED);
		}
		this.node.repaint();
	}

}
