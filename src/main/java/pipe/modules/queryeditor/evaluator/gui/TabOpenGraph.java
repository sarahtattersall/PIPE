/**
 * 
 */
package pipe.modules.queryeditor.evaluator.gui;

import java.awt.event.ActionEvent;

/**
 * @author dazz
 * 
 */
class TabOpenGraph extends TabOpenAction
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 4014286612678520625L;

	public TabOpenGraph(final QueryOperationNode node) {
		super(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(final ActionEvent arg0)
	{

		ResultProvider.setupGraphTab(this.node);

	}
}
