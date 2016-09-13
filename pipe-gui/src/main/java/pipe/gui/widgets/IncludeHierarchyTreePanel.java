package pipe.gui.widgets;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import pipe.controllers.application.PipeApplicationController;
import uk.ac.imperial.pipe.models.petrinet.IncludeHierarchy;
import uk.ac.imperial.pipe.models.petrinet.IncludeIterator;

@SuppressWarnings("serial")
public class IncludeHierarchyTreePanel extends JPanel implements TreeSelectionListener, PropertyChangeListener {

    private JEditorPane htmlPane;
    private JTree tree;
    private URL helpURL;
	private IncludeHierarchy includes;
	private DefaultMutableTreeNode rootNode;
	private Map<IncludeHierarchy, DefaultMutableTreeNode> nodeMap;
	private PipeApplicationController controller;
 
 

	
	public IncludeHierarchyTreePanel(PipeApplicationController controller) {
		this.controller = controller; 
		build(); 
	}

	public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
        		tree.getLastSelectedPathComponent();
	    if (node == null) return;
	    IncludeHierarchy include = (IncludeHierarchy) node.getUserObject();
	    controller.setActiveIncludeHierarchyAndNotifyView(include); 
	}
 
    private void build() {
       createNodes(); // returns false if no rootNode, but JTree doesn't care :) 
       buildTree();
       JScrollPane treeView = new JScrollPane(tree);
       add(treeView); 
    }

	protected void buildTree() {
		tree = new JTree(rootNode);
		tree.getSelectionModel().setSelectionMode
		        (TreeSelectionModel.SINGLE_TREE_SELECTION);
	 
		//Listen for when the selection changes.
		tree.addTreeSelectionListener(this);
		setSimpleLook();
		expandAllRows();
		selectActiveInclude(); 
	}

	private void selectActiveInclude() {
		if (includes != null) {
			select(includes); 
		}
	}

	protected void setSimpleLook() {
		tree.putClientProperty("JTree.lineStyle", "Angled");
        tree.setShowsRootHandles(true); 
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setLeafIcon(null);
        renderer.setOpenIcon(null);
        renderer.setClosedIcon(null);
        renderer.setTextNonSelectionColor(Color.GRAY); 
        renderer.setTextSelectionColor(Color.BLACK);
        renderer.setBackgroundSelectionColor(Color.LIGHT_GRAY);
        tree.setCellRenderer(renderer);
	}

	protected void expandAllRows() {
		for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
	}
	protected boolean createNodes() {
		boolean created = buildIncludes();
		if (created) {
			includes.addPropertyChangeListener(this); 
			refreshNodes();
		}
	    return created; 
	}

	protected void refreshNodes() {
		nodeMap = new HashMap<IncludeHierarchy, DefaultMutableTreeNode>();
		rootNode = new DefaultMutableTreeNode(includes);
		nodeMap.put(includes, rootNode); 
		IncludeIterator it = includes.iterator();
		IncludeHierarchy include = it.next(); // skip top node
		DefaultMutableTreeNode parent = null; 
		DefaultMutableTreeNode child = null; 
		while (it.hasNext())  {
			include = it.next(); 
			parent = nodeMap.get(include.getParent());
			child = new DefaultMutableTreeNode(include);
			parent.add(child); 
			nodeMap.put(include, child); 
		}
	}

	protected boolean buildIncludes() {
		includes = controller.getActiveIncludeHierarchy();  
		if (includes == null) {
			if (controller.getActivePetriNetController() != null) {
				includes = controller.getActivePetriNetController().getPetriNet().getIncludeHierarchy(); 
			}
		}
		
		return (includes != null);
	}
 
	public void select(IncludeHierarchy include) {
		DefaultMutableTreeNode node = nodeMap.get(include); 
		if (node != null) {
			TreePath path = new TreePath(node.getPath());
			tree.setSelectionPath(path);
			tree.expandPath(path); 
			expandAllRows();

		}
	}

    
	protected DefaultMutableTreeNode getRootNode() {
		return rootNode; 
	}

	protected Map<IncludeHierarchy, DefaultMutableTreeNode> getNodeMap() {
		return nodeMap;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (IncludeHierarchy.INCLUDE_HIERARCHY_STRUCTURE_CHANGE.equals(evt.getPropertyName())) {
			includes = (IncludeHierarchy) evt.getNewValue(); 
			refreshNodes(); 
			buildTree(); 
		}
	}

	protected final JTree getTree() {
		return tree;
	}

 
}

