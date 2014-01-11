/**
 * 
 */
package pipe.modules.queryeditor.evaluator.gui;

import java.awt.event.ActionEvent;

/**
 * @author dazz
 * 
 */
class TabOpenHistogram extends TabOpenAction
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -7521125767975466671L;

	public TabOpenHistogram(final QueryOperationNode node) {
		super(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(final ActionEvent arg0)
	{

		ResultProvider.setupSSPBarChartTab(this.node);

	}

}
