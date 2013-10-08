/**
 * Subtree
 * 
 * This class represents a subtree of the performance tree query and
 * is used for dependency analysis
 * 
 * @author Tamas Suto
 * @date 11/01/08
 */

package pipe.server.performancequery.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import pipe.common.EvaluationStatus;
import pipe.common.PetriNetNode;
import pipe.modules.queryresult.ResultWrapper;
import pipe.server.performancequery.SimpleNode;
import pipe.server.performancequery.StatusIndicatorUpdater;

public abstract class Subtree implements StructureLoggingHandler, ResultGetter
{

	private final ParentSubtree				parent;

	private final ArrayList<Subtree>		decendantSubtrees;

	private final HashMap<String, Subtree>	childSubtreesByRole;

	private final SimpleNode				node;

	private EvaluationStatus				status;

	private final ResultSubtree				root;

	private final StatusIndicatorUpdater	updater;

	Subtree(final SimpleNode thisNode,
            final StatusIndicatorUpdater updater,
            final ParentSubtree parent,
            final ResultSubtree root,
            final String roleForParent) {
		this.node = thisNode;
		this.updater = updater;
		this.decendantSubtrees = new ArrayList<Subtree>();
		this.childSubtreesByRole = new HashMap<String, Subtree>();
		this.root = root;
		this.parent = parent;
		if (!(this instanceof ResultSubtree))
		{
			if (this instanceof ValueSubtree)
			{
				this.status = EvaluationStatus.EVALCOMPLETE;
			}
			else
			{
				this.setStatus(EvaluationStatus.EVALNOTSTARTED);
			}
			// add subtree as a child subtree of current subtree
			parent.addDecendantSubtree(this);
			parent.addChildSubtreeByRole(this, roleForParent);
			if (parent == root)
			{
				root.setResultGetter(this);
			}
			else if (parent instanceof SequentialSubtree)
			{
				((SequentialSubtree) parent).addResultGetter(this);
			}
		}
	}

	void addChildSubtreeByRole(final Subtree subtree, final String role)
	{
		this.childSubtreesByRole.put(role, subtree);
	}

	void addDecendantSubtree(final Subtree subtree)
	{
		if (!this.decendantSubtrees.contains(subtree))
		{
			this.decendantSubtrees.add(subtree);
			this.parent.addDecendantSubtree(subtree);
		}
	}

	public int canBeEvaluated()
	{
		int evaluated = 0;
		for (final Subtree s : this.decendantSubtrees)
		{
			if (s.hasEvalCompleted())
			{
				evaluated++;
			}
		}
		return evaluated;
	}

	public void evaluated()
	{
		this.setStatus(EvaluationStatus.EVALCOMPLETE);
	}

	public void failed()
	{
		this.setStatus(EvaluationStatus.EVALFAILED);
	}

	public Subtree getChildByRole(final String role)
	{
		return this.childSubtreesByRole.get(role);
	}

	public ArrayList<Subtree> getDecendantSubtrees()
	{
		return this.decendantSubtrees;
	}

	public String getID()
	{
		return this.node.getID();
	}

	/**
	 * @return the node
	 */
	public SimpleNode getNode()
	{
		return this.node;
	}

	protected Subtree getParent()
	{
		return this.parent;
	}

	public abstract ResultWrapper getResult() throws ExecutionException, InterruptedException;

	/**
	 * @return the root
	 */
	protected ResultSubtree getRoot()
	{
		return this.root;
	}

	/**
	 * @return the status
	 */
    private EvaluationStatus getStatus()
	{
		return this.status;
	}

	public PetriNetNode getType()
	{
		return this.getNode().getType();
	}

	public boolean hasEvalCompleted()
	{
		return this.status != EvaluationStatus.EVALCOMPLETE;
	}

	public boolean hasFailed()
	{
		return this.status == EvaluationStatus.EVALFAILED;
	}

	public abstract boolean hasResult();

	public void inProgress()
	{
		this.setStatus(EvaluationStatus.EVALINPROGRESS);
	}

	public boolean isInProgress()
	{
		return this.status == EvaluationStatus.EVALINPROGRESS;
	}

	public boolean isRoot()
	{
		return this == this.root;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	private void setStatus(final EvaluationStatus status)
	{
		if (this.status == null || status.compareTo(this.status) > 0)
		{
			StructureLoggingHandler.logger.info(this.getType() + " " + status.toString());
			this.status = status;
			this.update();
		}
	}

	private void update()
	{
		this.updater.updateNodeStatus(this.getStatus().toString(), this.getID());
	}

}
