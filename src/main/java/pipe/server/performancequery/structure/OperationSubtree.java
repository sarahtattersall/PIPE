package pipe.server.performancequery.structure;

import java.io.File;

import pipe.server.performancequery.QueryServerException;
import pipe.server.performancequery.SimpleNode;
import pipe.server.performancequery.StatusIndicatorUpdater;

public class OperationSubtree extends ParentSubtree
{
	private File	modFile;

	public OperationSubtree(final SimpleNode thisNode,
							final StatusIndicatorUpdater updater,
							final ParentSubtree parent,
							final ResultSubtree root,
							final String roleForParent) throws QueryServerException {
		super(thisNode, updater, parent, root, roleForParent);
		this.modFile = null;

		if (thisNode.getType().isValueNode())
		{
			throw new QueryServerException("Operation Subtree only supported for Operation PTNodes, not " +
											thisNode.getType());
		}

	}

	/**
	 * @return the path to modfile if this has one, else null
	 */
	public String getModFilePath()
	{
		return this.modFile == null ? null : this.modFile.getAbsolutePath();
	}

	public boolean hasModFile()
	{
		return this.modFile != null;
	}

	/**
	 * @param modFile
	 *            the modFile to set
	 */
	public void setModFile(final File modFile)
	{
		this.modFile = modFile;
	}

}
