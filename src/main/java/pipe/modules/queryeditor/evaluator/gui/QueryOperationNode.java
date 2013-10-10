/**
 * 
 */
package pipe.modules.queryeditor.evaluator.gui;

import java.net.URL;

import pipe.common.PetriNetNode;
import pipe.modules.queryresult.ResultWrapper;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.OperationNode;

/**
 * @author dazz
 * 
 */
public class QueryOperationNode extends QueryTreeNode implements EvaluatorGuiLoggingHandler
{
	/**
	 * 
	 */
	private static final long		serialVersionUID	= -6891088530469724409L;

	private final StatusIndicator	statusIndicator;

	private String					operation;

	private ResultWrapper					result				= null;

	public QueryOperationNode(final OperationNode inputNode) {
		super(inputNode);
		this.statusIndicator = new StatusIndicator(inputNode.getPositionX(), inputNode.getPositionY());
		this.setOperation(inputNode.getOperation());
	}

	/**
	 * @return the operation
	 */
	public String getOperation()
	{
		return this.operation;
	}

	/**
	 * @return the result
	 */
	public ResultWrapper getResult()
	{
		return this.result;
	}

	public String getStatus()
	{
		if (this.statusIndicator != null)
		{
			return this.statusIndicator.getStatus();
		}
		return null;
	}

	public boolean hasResult()
	{
		return this.result != null;
	}

	void setOperation(final String operationInput)
	{
		// update variable to indicate what it now represents
		this.operation = operationInput;

		// update image
		String query = QueryManager.imgPath + this.nodeType;
		if (!operationInput.equals(""))
		{
			query += "-" + operationInput;
		}
		query += ".png";
		final URL newImageURL = Thread.currentThread().getContextClassLoader().getResource(query);
		this.setNodeImage(newImageURL);
	}

	/**
	 * @param result
	 *            the result to set
	 */
	public synchronized void setResult(final ResultWrapper result)
	{
		this.result = result;

		if (this.nodeType.compareTo(PetriNetNode.SEQUENTIAL) <= 0)
		{
			ResultProvider.setupAutomaticResult(this);
		}
	}

	/**
	 * This method changes the status indicator's colouring scheme that is
	 * associated with the node. The colouring indicates the progress of the
	 * evaluation of the query.
     * @param status
     */
	public void setStatus(final String status)
	{
		if (this.statusIndicator != null && status != null)
		{
			this.statusIndicator.setStatus(status);
		}
	}

	public void showStatusIndicator()
	{
		if (this.statusIndicator != null)
		{
			QueryManager.getProgressView().remove(this.statusIndicator);
			QueryManager.getProgressView().add(this.statusIndicator);
		}
	}

}
