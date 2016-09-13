package pipe.gui.widgets;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import pipe.controllers.application.PipeApplicationController;
import pipe.views.PipeApplicationView;
import uk.ac.imperial.pipe.models.petrinet.IncludeHierarchy;

@SuppressWarnings("serial")
public class IncludeHierarchyPanel extends JPanel implements PropertyChangeListener {

	private JRootPane rootPane;
	private PipeApplicationController controller;
    /**
     * OK button that will perform changes when pressed
     */
    private javax.swing.JButton okButton = new javax.swing.JButton();
	protected JTextField nameTextField = new JTextField("", 20); // force wide text field in layout
	private JLabel nameLabel;
	private JLabel uniqueNameLabel;
	private IncludeHierarchy include;
	protected JLabel uniqueNameValue;
	private JLabel fullyQualifiedNameValue;
	protected JLabel fullyQualifiedNameLabel;
	private IncludeHierarchyTreePanel treeEditorPanel;

	
	/**
	 * constructor for non-gui testing
	 * @param controller pipe application controller 
	 */

	public IncludeHierarchyPanel(PipeApplicationController controller) {
		this.controller = controller;
		controller.addPropertyChangeListener(this); 
		this.include = controller.getActiveIncludeHierarchy(); 
		buildComponents(); 
	}
	public IncludeHierarchyPanel(JRootPane rootPane,
			PipeApplicationController controller) {
		this(controller); 
		this.rootPane = rootPane; 
        this.rootPane.setDefaultButton(okButton);
	}

	private void buildComponents() {
		setLayout(new GridLayout(1,0));

		buildTreePanel();
        JScrollPane treeView = new JScrollPane(treeEditorPanel.getTree());
        treeView.setBorder(BorderFactory.createTitledBorder("Include Hierarchy"));
        JPanel holdingPanel = new JPanel();
        holdingPanel.setLayout(new java.awt.GridBagLayout());
        GridBagConstraints gridBagConstraints;
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        JScrollPane bottomView = new JScrollPane(holdingPanel);
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        Dimension minimumSize = new Dimension(300, 100);
        treeView.setMinimumSize(minimumSize);
        treeView.setPreferredSize(new Dimension(500,200));
        splitPane.setDividerLocation(150); 
        splitPane.setPreferredSize(new Dimension(500, 300));
        splitPane.setTopComponent(treeView);
        splitPane.setBottomComponent(bottomView);
        splitPane.setEnabled(true); 
        splitPane.setOneTouchExpandable(true); 

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new java.awt.GridBagLayout());
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		holdingPanel.add(topPanel, gridBagConstraints); 

        JPanel editorPanel = new JPanel();
        editorPanel.setLayout(new java.awt.GridBagLayout());
        editorPanel.setBorder(BorderFactory.createTitledBorder("Include Editor"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;

        buildIncludeNameEditor(editorPanel); 

		topPanel.add(editorPanel, gridBagConstraints);

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new java.awt.GridBagLayout());
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		holdingPanel.add(bottomPanel, gridBagConstraints); 


        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        initializeOkButton(buttonPanel);
        initializeCancelButton(buttonPanel);
        bottomPanel.add(buttonPanel,gridBagConstraints);         

        
//        JEditorPane resultsPane = new JEditorPane();
//        resultsPane.setEditable(false);

        add(splitPane);

	}

    private void buildIncludeNameEditor(JPanel editorPanel) {
        buildIncludeName(editorPanel);
        buildUniqueNameLabel(editorPanel);
        buildFullyQualifiedNameLabel(editorPanel);
        updateValues(); 
	}

	public void updateValues() {
		fullyQualifiedNameValue.setText(include.getFullyQualifiedName());
		uniqueNameValue.setText(include.getUniqueName());
		nameTextField.setText(include.getName());
		
	}
	private void buildFullyQualifiedNameLabel(JPanel editorPanel) {
		fullyQualifiedNameLabel = new JLabel();	
		fullyQualifiedNameLabel.setText("Fully qualified name:");
		addLabel(editorPanel, fullyQualifiedNameLabel, 2, 0);
		fullyQualifiedNameValue = new JLabel();
		addLabel(editorPanel, fullyQualifiedNameValue, 2, 1);
	}

	protected void buildUniqueNameLabel(JPanel editorPanel) {
		uniqueNameLabel = new JLabel();
        uniqueNameLabel.setText("Unique name:");
        addLabel(editorPanel, uniqueNameLabel, 1, 0);
        uniqueNameValue = new JLabel();
        addLabel(editorPanel, uniqueNameValue, 1, 1);
	}

	protected void buildIncludeName(JPanel editorPanel) {
		nameLabel = new JLabel();
        nameLabel.setText("Include Name:");
        addLabel(editorPanel, nameLabel, 0, 0);
        buildIncludeNameTextField(editorPanel);
	}

	protected void addLabel(JPanel editorPanel, JLabel nameLabel, int row, int col) {
		GridBagConstraints gridBagConstraints;
		gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = col;
        gridBagConstraints.gridy = row;
        if (col > 0) {
        	gridBagConstraints.gridwidth = 2;
        	gridBagConstraints.anchor = GridBagConstraints.WEST;
        } else {
        	gridBagConstraints.anchor = GridBagConstraints.EAST;
        }
        gridBagConstraints.insets = new Insets(3, 3, 3, 3);
        editorPanel.add(nameLabel, gridBagConstraints);
	}

	private void buildIncludeNameTextField(JPanel editorPanel) {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(3, 3, 3, 3);
        editorPanel.add(nameTextField, gridBagConstraints);
    }

	/**
     * Creates the editor panel
     * @return editor panel 
     */
    private IncludeHierarchyTreePanel buildTreePanel() {
        treeEditorPanel = new IncludeHierarchyTreePanel(controller);
        return treeEditorPanel;
    }

	
    private void initializeOkButton(JPanel editorPanel) {
        okButton.setText("OK");
        okButton.setMaximumSize(new Dimension(75, 25));
        okButton.setMinimumSize(new Dimension(75, 25));
        okButton.setPreferredSize(new Dimension(75, 25));
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonHandler(evt);
            }
        });
        okButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                okButtonKeyPressed(evt);
            }
        });

        GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 9);
        editorPanel.add(okButton, gridBagConstraints);
    }
    /**
     * Initialises the cancel button. When cancel is pressed the window exits and no changes
     * are saved
     * @param editorPanel cancel button 
     */
    private void initializeCancelButton(JPanel editorPanel) {
        GridBagConstraints gridBagConstraints;
        JButton cancelButton = new JButton();
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                cancelButtonHandler(evt);
            }
        });

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(8, 0, 8, 10);
        editorPanel.add(cancelButton, gridBagConstraints);
    }

    /**
     * Performs the OK event
     * @param evt OK event 
     */
    private void okButtonHandler(java.awt.event.ActionEvent evt) {
    	exit(); 
    }
    /**
     * Performs the ok action
     * @param evt event 
     */
    private void okButtonKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            exit(); 
        }
    }
    /**
     * Exits the dialog
     */
    private void exit() {
        rootPane.getParent().setVisible(false);
    }
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(PipeApplicationController.NEW_ACTIVE_INCLUDE_HIERARCHY)) {
			include =  (IncludeHierarchy) event.getNewValue(); 
			updateValues(); 
			treeEditorPanel.select(include); 
		}
	}
	protected final IncludeHierarchyTreePanel getTreeEditorPanel() {
		return treeEditorPanel;
	}
	
}
