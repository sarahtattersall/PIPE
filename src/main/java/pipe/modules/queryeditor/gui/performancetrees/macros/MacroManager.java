/**
 * MacroManager
 * 
 * This class contains methods for dealing with the management and
 * assignment of macros. It is used as a static class that controls
 * all macro-related operations.
 * 
 * @author Tamas Suto
 * @date 06/10/07
 */


package pipe.modules.queryeditor.gui.performancetrees.macros;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.MouseInputAdapter;

import org.w3c.dom.Document;

import pipe.gui.widgets.ButtonBar;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.QueryFileBrowser;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeNode;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroDefinition.OutgoingArcInfo;
import pipe.modules.queryeditor.io.MacroLoader;
import pipe.modules.queryeditor.io.MacroTransformer;


public class MacroManager {
	
	private static MacroNode node;
	private static ArrayList<String> availableMacros;
	
	// macro editor components
	private static MacroEditor macroEditor;
	public static JDialog popupDialog, macroDialog;	
	private static JList macroList = new JList();
	private static JTextArea macroDescription = new JTextArea();
	
	// this is the location where the macro definitions that are created are saved to
	// so that they can be loaded in for reuse whenever the program is launched
	public static final String macroSaveLocation = "bin" + System.getProperty("file.separator") +
											 "Macros" + System.getProperty("file.separator"); 
	
	// the mode is used to differentiate between the cases when macros are loaded in 
	// and when they are just being created normally
	private static int mode, prev_mode;	
	
	
	public static int getMode() {
		return mode;
	}
	
	public static void setMode(int _mode) {
		if (mode != _mode){		// Don't bother unless new mode is different.
			prev_mode = mode;
			mode = _mode;
		}
	}
	
	public static void restoreMode() {
		mode = prev_mode;
	}
	
	/**
	 * This method launches a popup that enables the assignment of a macro
	 * definition to a Macro node
	 * @param nodeInput
	 */
	public static void macroAssignmentDialog(MacroNode nodeInput) {		
		// update our local copy of the node object. Need this to be able to
		// refer to the node from the ActionListeners
		node = nodeInput;
		
		// build popup
		popupDialog = new JDialog(QueryManager.getEditor(),"Macro Assignment",true);
		popupDialog.setMinimumSize(new Dimension(630,350));
	    Container contentPane = popupDialog.getContentPane();
	    contentPane.setLayout(new BoxLayout(contentPane,BoxLayout.PAGE_AXIS));
	    contentPane.add(getMacroAssignmentManagerPanel(false));
	    String[] buttonNames = {"OK", "Cancel"};
	    ActionListener okButtonListener = new ActionListener() {		 
	    	public void actionPerformed(ActionEvent arg0) {
	    		int[] macroListSelectedIndices = macroList.getSelectedIndices();
	    		if (macroListSelectedIndices.length == 1) {
	    			// only one macro selection allowed
	    			int selectionIndex = macroListSelectedIndices[0];
	    			String selectedMacroName = (String)macroList.getModel().getElementAt(selectionIndex);
	    			// assign macro label to node
	    			node.setNodeLabel(selectedMacroName);
	    			// set node's return type & draw arcs
	    			finaliseMacroNode(selectedMacroName);
	    			// update node in QueryData
	    			QueryManager.getData().updateNode(node);
	    			resetMacroManager();
	    			popupDialog.dispose();	 

	    		}
	    		else {
	    			JOptionPane.showMessageDialog(QueryManager.getEditor().getContentPane(),
	    					"Please select only one macro from the list \n"+
	    					"that you wish to assign to the Macro node.",
	    					"Warning",
	    					JOptionPane.ERROR_MESSAGE);
	    		}		
		    }
	    };   
	    ActionListener cancelButtonListener = new ActionListener() {		 
		    public void actionPerformed(ActionEvent arg0) {
		    	resetMacroManager();
		    	popupDialog.dispose();
		    }
	    };    
	    ActionListener[] buttonListeners = {okButtonListener, cancelButtonListener};
	    contentPane.add(new ButtonBar(buttonNames, buttonListeners));
	    
	    // take care of popup closing
	    popupDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	    popupDialog.addWindowListener(new WindowAdapter() {
	    	    public void windowClosing(WindowEvent we) {
	    	    	resetMacroManager();
			    	popupDialog.dispose();
	    	    }
	    	});
	    
	    // load in the latest info
	    update();
	    
	    // show popup
	    popupDialog.pack();
	    popupDialog.setLocationRelativeTo(null);
	    popupDialog.setVisible(true);    
	}
	
	/**
	 * Resets everything after the editor is exited
	 */
	private static void resetMacroManager() {
		resetEditor();
		node = null;
		availableMacros = null;
		macroList = new JList();
		macroDescription = new JTextArea();
	}
	
	private static void updateAvailableMacros() {
		availableMacros = QueryManager.getData().getMacroNames();
	}
	
	/**
	 * This method loads the names of the macros that have been
	 * defined so far
	 */ 
	private static void populateMacroList() {
		updateAvailableMacros();
		DefaultListModel model = new DefaultListModel();
		Iterator<String> i = availableMacros.iterator();
		while (i.hasNext()) {
			String macroName = i.next();
			model.addElement(macroName);
		}
		macroList.setModel(model);
	}
	
	/**
	 * This method shows the description corresponding to the macro
	 * @param macroName
	 */
	private static void showMacroDescription(String macroName) {
		MacroDefinition macro = getMacro(macroName);
		if (macro != null) {
			String macroDesc = macro.getDescription();
			if (macroDesc == null)
				macroDesc = "This macro has no description associated with it.";
			macroDescription.setText(macroDesc);
		}
		else
			macroDescription.setText("");
	}
	
	/**
	 * Gets a MacroDefinition from the local array holding a copy of the 
	 * available macros.
	 * @param macroName
	 * @return
     * @param macroToGetName
	 */
	private static MacroDefinition getMacro(String macroToGetName) {
		if (QueryManager.getData().getMacro(macroToGetName) != null) 
			return QueryManager.getData().getMacro(macroToGetName);
		else
			return null;
	}
	
	public static void update() {
		updateAvailableMacros();
		populateMacroList();
	}
	
	/**
	 * This method sets the macro's return type and is responsible
	 * for drawing the arcs on the QueryView canvas
	 * @param selectedMacroName
	 */
	private static void finaliseMacroNode(String selectedMacroName) {
		// set return type to that of the top node
		MacroDefinition selectedMacro = getMacro(selectedMacroName);		
		String returnType = selectedMacro.getReturnType();
		node.setReturnType(returnType);
		
		// needed for the case when a macro has been defined in the MacroEditor and then
		// we want to assign it to a macro node
		macroEditor = new MacroEditor(selectedMacro);
		
		// draw as many outgoing arcs as we have argument nodes in the macro
		HashMap<String,OutgoingArcInfo> outgoingArcInfo = selectedMacro.getOutgoingArcInformation();
		Iterator<String> i = outgoingArcInfo.keySet().iterator();
		while (i.hasNext()) {
			String argumentNodeName = i.next();
			OutgoingArcInfo arcInfo = outgoingArcInfo.get(argumentNodeName);
			ArrayList acceptableChildNodeTypes = arcInfo.getAcceptableChildNodeTypes();
			boolean isRequired = arcInfo.getArgumentRequired();	
			
			if (isRequired) 
				node.setRequiredChildNode(argumentNodeName, acceptableChildNodeTypes);
			else 
				node.setOptionalChildNode(argumentNodeName, acceptableChildNodeTypes);
		}
		
		// set max no. of arguments - needed for childAssignmentValid()
		int noOfOutgoingArcs = outgoingArcInfo.size();
		node.setMaxArguments(noOfOutgoingArcs);
		
		// update node
		QueryManager.getData().updateNode(node);
		
		// set editor to null, since we need to draw the arcs on QueryView now
		resetEditor();	
		
		// draw outgoing arcs
		node.setupOutgoingArcs();
	}
	
	private static void printOutgoingArcInfo(String arcRole, ArrayList childNodeTypes, boolean required) {
		System.out.println("-- begin arc info for macro node --");
		System.out.println("Arc role: "+arcRole);
		if (required)
			System.out.println("Required arc: true");
		else
			System.out.println("Required arc: false");
		Iterator i = childNodeTypes.iterator();
		while (i.hasNext()) {
			String childNodeType = (String)i.next();
			System.out.println("Acceptable child node type: "+childNodeType);
		}
		System.out.println("-- end arc info for macro node --");
	}

	/**
	 *  Creates the state label assignment manager
     * @param showMacroManagerButtons
     * @return
     */
	private static JPanel getMacroAssignmentManagerPanel(boolean showMacroManagerButtons) {
		JPanel macroAssignmentManagerPanel = new JPanel();
		macroAssignmentManagerPanel.setBorder((new TitledBorder(new EtchedBorder(),"Macros")));
		macroAssignmentManagerPanel.setLayout(new BoxLayout(macroAssignmentManagerPanel, BoxLayout.Y_AXIS));
		
		// panel to hold the available macros, the macro definition and the assignment buttons
		JPanel macroAssignmentPanel = new JPanel();
		macroAssignmentPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		JButton createMacroButton = new JButton();
		JButton editMacroButton = new JButton();
		JButton deleteMacroButton = new JButton();
		JButton importMacroButton = new JButton();
		
		// Create Macro
		JPanel macroButtonsPanel = new JPanel();
		createMacroButton = new JButton("Create Macro");
		ActionListener createMacroButtonListener = new ActionListener() {		 
			public void actionPerformed(ActionEvent arg0) {
				// bring up the macro editor popup
				macroEditor = new MacroEditor();
				macroEditor.createMacro();
			}
		};
		createMacroButton.addActionListener(createMacroButtonListener);
		
		if (showMacroManagerButtons) {
			// Edit Macro
			editMacroButton = new JButton("Edit Macro");
			ActionListener editMacroButtonListener = new ActionListener() {		 
				public void actionPerformed(ActionEvent arg0) {
					int[] selectedIndices = macroList.getSelectedIndices();
					if (selectedIndices.length > 0) {			
						if (selectedIndices.length > 1) {
							JOptionPane.showMessageDialog(QueryManager.getEditor().getContentPane(),
									"To edit a macro, please select a single entry from \n"+
									"the list of available macros.",
									"Warning",
									JOptionPane.INFORMATION_MESSAGE);
						}
						else {
							int selectedIndex = selectedIndices[0];
							// bring up the macro editor popup
							String selectedMacroName = (String)macroList.getModel().getElementAt(selectedIndex);
							if (!isMacroAssignedToOtherNodes(selectedMacroName)) {
								MacroDefinition selectedMacro = getMacro(selectedMacroName);
								macroEditor = new MacroEditor(selectedMacro);
								macroEditor.editMacro(selectedMacroName);
							}
							else {
								JOptionPane.showMessageDialog(QueryManager.getEditor().getContentPane(),
										"The macro definition you are attempting to edit has \n"+
										"been assigned to other macro nodes as well. Please make \n"+
										"sure to first delete the respective nodes and then attempt \n" +
										"editing the macro definition again.",
										"Warning",
										JOptionPane.ERROR_MESSAGE);	
							}
						}
					}
					else {
						JOptionPane.showMessageDialog(QueryManager.getEditor().getContentPane(),
								"To edit a macro, please select a single entry from \n"+
								"the list of available macros.",
								"Warning",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			};
			editMacroButton.addActionListener(editMacroButtonListener);
			
			// Import Macro
			importMacroButton = new JButton("Import Macro");
			ActionListener importMacroButtonListener = new ActionListener() {		 
				public void actionPerformed(ActionEvent arg0) {
					// import macro into QueryData
					File macroFile = new QueryFileBrowser(QueryManager.userPath).openFile();
					if (macroFile != null) {
						MacroTransformer transformer = new MacroTransformer();
						Document macroDocument = transformer.transformPTML(macroFile.getPath());
						boolean importSuccessful = MacroLoader.importMacro(macroDocument);

						// copy over XML file into the local macro store
						if (importSuccessful) {
							String copiedMacroFilePathName = MacroManager.macroSaveLocation + macroFile.getName();
							File copiedMacroFile = new File(copiedMacroFilePathName);
							try {
								copyFile(macroFile,copiedMacroFile);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						else {
							JOptionPane.showMessageDialog(QueryManager.getEditor().getContentPane(),
									"The macro import was unsuccessful. Please ensure that you \n"+
									"are attempting to import a valid macro XML document and not \n"+
									"one describing a performance query.\n",
									"Warning",
									JOptionPane.ERROR_MESSAGE);	
						}
					}

					// Update MacroManager display
					update();
				}
			};
			importMacroButton.addActionListener(importMacroButtonListener);
			
			// Delete Macro
			deleteMacroButton = new JButton("Delete Macro");
			ActionListener deleteMacroDefinitionButtonListener = new ActionListener() {		 
				public void actionPerformed(ActionEvent arg0) {
					int[] macroListSelectedIndices = macroList.getSelectedIndices();
					if (macroListSelectedIndices.length > 0) {
                        for(int selectionIndex : macroListSelectedIndices)
                        {
                            String selectedMacroName = (String) macroList.getModel().getElementAt(selectionIndex);
                            if(!isMacroAssignedToOtherNodes(selectedMacroName))
                            {
                                // remove macro from _dataLayer
                                QueryManager.getData().deleteMacro(selectedMacroName);

                                // remove XML storing macro
                                String fileName = MacroManager.macroSaveLocation + System.getProperty("file.separator") + selectedMacroName + ".xml";
                                File inFile = new File(fileName);
                                if(inFile.exists())
                                    inFile.delete();

                                showMacroDescription("");
                            }
                            else
                            {
                                JOptionPane.showMessageDialog(QueryManager.getEditor().getContentPane(),
                                                              "The macro definition you are attempting to delete has \n" +
                                                                      "been assigned to other macro nodes as well. Please make \n" +
                                                                      "sure to first delete the respective nodes and then attempt \n" +
                                                                      "deleting the macro definition again.",
                                                              "Warning",
                                                              JOptionPane.ERROR_MESSAGE);
                            }
                        }
						update(); 
					}
					else {
						JOptionPane.showMessageDialog(QueryManager.getEditor().getContentPane(),
								"To delete a macro, please select a single entry from \n"+
								"the list of available macros.",
								"Warning",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			};
			deleteMacroButton.addActionListener(deleteMacroDefinitionButtonListener);	
		}

		macroButtonsPanel.add(createMacroButton);

	    if (showMacroManagerButtons) {
	    	macroButtonsPanel.add(editMacroButton);
	    	macroButtonsPanel.add(importMacroButton);
	    	macroButtonsPanel.add(deleteMacroButton);
	    }
		// panel for the macro definitions
		JPanel macroDefinitionsPanel = new JPanel();
		macroDefinitionsPanel.setBorder((new TitledBorder(new EtchedBorder(),"Available Macro Definitions")));
		macroList.setLayoutOrientation(JList.VERTICAL);
		macroList.setBackground(Color.white);
//		macroList.setSelectionModel(new ToggleSelectionModel());
		macroList.setVisibleRowCount(-1);
		populateMacroList();
		MouseHandler listHandler = new MouseHandler();
		macroList.addMouseListener(listHandler);
		
		
		JScrollPane macroDefinitionsListScroller = new JScrollPane(macroList);
		macroDefinitionsListScroller.setPreferredSize(new Dimension(300, 200));
		macroDefinitionsPanel.add(macroDefinitionsListScroller);
		macroAssignmentPanel.add(macroDefinitionsPanel);
				
		// panel for the macro info
		JPanel macroDescriptionPanel = new JPanel();
		macroDescriptionPanel.setBorder((new TitledBorder(new EtchedBorder(),"Macro Description")));
		macroDescription.setBackground(Color.white);
		macroDescription.setEditable(false);
		macroDescription.setLineWrap(true);
		JScrollPane macroDescriptionScroller = new JScrollPane(macroDescription);
		macroDescriptionScroller.setPreferredSize(new Dimension(300, 200));
		macroDescriptionPanel.add(macroDescriptionScroller);
		macroAssignmentPanel.add(macroDescriptionPanel);
		
		// Add components to panel
		macroAssignmentManagerPanel.add(macroButtonsPanel);
		macroAssignmentManagerPanel.add(macroAssignmentPanel);
		
		return macroAssignmentManagerPanel;
	}
	
	private static void copyFile(File source, File dest) throws IOException {
	     FileChannel in = null, out = null;
	     try {          
	          in = new FileInputStream(source).getChannel();
	          out = new FileOutputStream(dest).getChannel();
	          long size = in.size();
	          MappedByteBuffer buf = in.map(FileChannel.MapMode.READ_ONLY, 0, size);
	          out.write(buf);
	     } finally {
	          if (in != null)          in.close();
	          if (out != null)     out.close();
	     }
	}
	
	/**
	 * This method checks whether other macro nodes have already been assigned the macro
	 * definition that we're looking at
	 * @param macroName
	 * @return
	 */
	private static boolean isMacroAssignedToOtherNodes(String macroName) {
		PerformanceTreeNode[] queryNodes = QueryManager.getData().getNodes();
        for(PerformanceTreeNode queryNode : queryNodes)
        {
            if(queryNode instanceof MacroNode)
            {
                if(((MacroNode) queryNode).getNodeLabel() != null)
                {
                    String assignedMacro = ((MacroNode) queryNode).getNodeLabel();
                    if(assignedMacro.equals(macroName))
                        return true;
                }
            }
        }
		return false;
	}

	/**
	 * This is the dialog that is invoked by Tools -> Macro Manager
	 */
	public static void macroManagerDialog() {		
		// build popup
		popupDialog = new JDialog(QueryManager.getEditor(),"Macro Manager",true);
		popupDialog.setMinimumSize(new Dimension(630,350));
	    Container contentPane = popupDialog.getContentPane();
	    contentPane.setLayout(new BoxLayout(contentPane,BoxLayout.PAGE_AXIS));
	    contentPane.add(getMacroAssignmentManagerPanel(true));
	    JPanel okButtonPanel = new JPanel();
	    okButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
	    JButton okButton = new JButton("OK");
	    ActionListener okButtonListener = new ActionListener() {		 
		    public void actionPerformed(ActionEvent arg0) {
		    	resetMacroManager();
			    popupDialog.dispose();
		    }
	    };   
	    okButton.addActionListener(okButtonListener);
	    okButtonPanel.add(okButton);
	    contentPane.add(okButtonPanel);
	    
	    // take care of popup closing
	    popupDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	    popupDialog.addWindowListener(new WindowAdapter() {
	    	    public void windowClosing(WindowEvent we) {
	    	    	resetMacroManager();
			    	popupDialog.dispose();
	    	    }
	    	});
	    
	    // load in the latest info
	    update();
	    
	    // show popup
	    popupDialog.pack();
	    popupDialog.setLocationRelativeTo(null);
	    popupDialog.setVisible(true);    

	}
	
	
	//---------------------------------------------------------------------------------
	// methods for use by MacroView and MacroEditor
	//---------------------------------------------------------------------------------
	
	public static MacroEditor getEditor() { 
	    return macroEditor;
	}
	
	public static void resetEditor() {
		macroEditor = null;
	}
	
	public static MacroView getView() {
		if (macroEditor != null)
			return macroEditor.getView();
		else 
			return null;
	}
	
	
	
	//---------------------------------------------------------------------------------

	private static class MouseHandler extends MouseInputAdapter {
		public void mouseClicked(MouseEvent e){			
			if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 1) {
				int[] macroListSelectedIndices = macroList.getSelectedIndices();
	    		if (macroListSelectedIndices.length > 0) {
	    			int selectionIndex = macroListSelectedIndices[0];
	    			String selectedMacroName = (String)macroList.getModel().getElementAt(selectionIndex);
	    			// Show macro's description
	    			MacroManager.showMacroDescription(selectedMacroName);
	    		}
			}
		}
	}

	
	/**
	 * This class allows a JList to operate in a click toggle fashion - see JList java doc
	 */ 
	static class ToggleSelectionModel extends DefaultListSelectionModel {
		
		private static final long serialVersionUID = 1L;
		boolean gestureStarted = false;
		
		public ToggleSelectionModel() {
			
		}
	    
		public void setSelectionInterval(int index0, int index1) {
			if (isSelectedIndex(index0) && !gestureStarted) {
				super.removeSelectionInterval(index0, index1);
			}
			else {
				super.setSelectionInterval(index0, index1);
			}
			gestureStarted = true;
		}

		public void setValueIsAdjusting(boolean isAdjusting) {
			if (!isAdjusting) {
				gestureStarted = false;
			}
		}
	}
	
}
