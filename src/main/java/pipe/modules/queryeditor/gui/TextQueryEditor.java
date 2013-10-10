/**
 * TextQueryEditor
 * 
 * - Create Query according to the text input
 * - convert the user input into a Performance Tree query and print it onto the cancas
 * 
 * @author Lei Wang	
 * @date 21/04/08
 */

package pipe.modules.queryeditor.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.undo.UndoManager;

import pipe.gui.Grid;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeArc;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeNode;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeObject;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroEditor;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroManager;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.ArithCompNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.ArithOpNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.ConvolutionNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.DisconNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.DistributionNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.FiringRateNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.InIntervalNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.MomentNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.NegationNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.OperationNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.PassageTimeDensityNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.PercentileNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.ProbInIntervalNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.ProbInStatesNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.RangeNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.ResultNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.SequentialNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.StatesAtTimeNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.SteadyStateProbNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.SteadyStateStatesNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.SubsetNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.ActionsNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.BoolNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.NumNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.StateFunctionNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.StatesNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.ValueNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.labels.StateLabelManager;
import pipe.modules.queryeditor.io.QueryData;

public class TextQueryEditor extends JPanel {

	private final JTextPane queryDisplay = new JTextPane();
	private final String[] queryInit = {"-- Select --", "Is it true that [bool]", "What is the [quantitative measures]"};
	private final JComboBox selector = new JComboBox(queryInit);
	private String currentQuery = "";
    //private StyledDocument styledDoc = queryDisplay.getStyledDocument();
	private StyledDocument styledDoc = new DefaultStyledDocument();
    private String currentParam = "";
	//keep record of parameters' position 
	private int currentParamLeft = 0;
	private int currentParamRight = 0;
	//adding color test
	private String currentTextColor = "black"; 
	//private Style textColor;
	private static JDialog guiDialog;
    //add undo and redo manager
	private final JButton undoButton = new JButton("Undo");
	private JButton redoButton = new JButton("Redo");
	private JMenuBar menuBar = new JMenuBar();

	//protected UndoAction undoAction;
	//protected RedoAction redoAction;
    private final UndoManager undo = new UndoManager();
	//
	//keep tracking of current undo state
	private int currentUndoStep = 0;
	private final ArrayList<UndoState> undoList = new ArrayList<UndoState>();
	//
	private QueryData queryData;
    //record previous node been added to data structure
	private PerformanceTreeNode prev_node = null; 
	private double newNodePositionX = 0;
	private double newNodePositionY = 0;

	public TextQueryEditor(){
		styledDoc = queryDisplay.getStyledDocument();
		this.setBorder(new TitledBorder(new EtchedBorder(), "Text Query Editor"));
		queryDisplay.setEditable(false);
		queryDisplay.setPreferredSize(new Dimension(400,100));
		selector.setPreferredSize(new Dimension(700,20));

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane,BoxLayout.Y_AXIS));
        JButton resetButton = new JButton("Reset Query");
        buttonPane.add(resetButton);
        JButton doneButton = new JButton("Query Done");
        buttonPane.add(doneButton);

		doneButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                queryDone();
            }
        });

		resetButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                clearQuery();
            }
        });

		JPanel topPane = new JPanel();
		topPane.add(selector);
		//add undo and redo button to panel
		JMenu editMenu = new JMenu("Undo/Redo");
		//editMenu.add(undoAction);
		//editMenu.add(redoAction);
		//menuBar.add(editMenu);
		//topPane.add(menuBar);
		topPane.add(undoButton);
		//topPane.add(redoButton);
		undoButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (undoList.size()>=1){
					UndoState undo = undoList.get(undoList.size()-1);
					undo.undo();
				}
			}
		});
		undoButton.setEnabled(false);


		selector.addActionListener(new SelectorListener());
		//styledDoc.addDocumentListener(new MyDocumentListener());

		//add style to text panel
		//add main style 
		Style mainStyle = queryDisplay.addStyle("main", null);
		StyleConstants.setFontFamily(mainStyle, "serif");
		StyleConstants.setFontSize(mainStyle, 16);
		StyleConstants.setItalic(mainStyle, true);

		//add parameter style
		Style paramStyle = queryDisplay.addStyle("param", null);
		StyleConstants.setFontFamily(paramStyle, "serif");
		StyleConstants.setFontSize(paramStyle, 16);
		StyleConstants.setForeground(paramStyle, Color.blue);
		StyleConstants.setBold(paramStyle, true);

		//add current requested parameter style
		Style currentParamStyle = queryDisplay.addStyle("currentparam", null);
		StyleConstants.setFontFamily(currentParamStyle, "serif");
		StyleConstants.setFontSize(currentParamStyle, 16);
		StyleConstants.setForeground(currentParamStyle, Color.red);
		StyleConstants.setBold(currentParamStyle, true);

		//add color to each part

		Style red = queryDisplay.addStyle("red", null);
		StyleConstants.setForeground(red, Color.red);

		Style blue = queryDisplay.addStyle("blue", null);
		StyleConstants.setForeground(blue, Color.blue);

		Style green = queryDisplay.addStyle("green", null);
		StyleConstants.setForeground(green, Color.green);

		Style cyan = queryDisplay.addStyle("cyan", null);
		StyleConstants.setForeground(cyan, Color.cyan);

		Style magenta = queryDisplay.addStyle("magenta", null);
		StyleConstants.setForeground(magenta, Color.magenta);

		Style black = queryDisplay.addStyle("black", null);
		StyleConstants.setForeground(black, Color.black);


		this.setLayout(new BorderLayout(5,5));
		this.add(topPane, BorderLayout.PAGE_START);
		this.add(queryDisplay, BorderLayout.CENTER);
		this.add(buttonPane, BorderLayout.LINE_END);

		//add result node to the query data structure
		//PerformanceTreeNode resultNode = new ResultNode(newNodePositionX, newNodePositionY);
		//queryData.addNode(resultNode);

		this.validate();
		this.setVisible(true);
		this.repaint();
	}

	private class SelectorListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			//
			String item = (String)selector.getSelectedItem();
			if (item != null){
				if (!item.equals("Query Done")){	
					//add the new node to data structure and record previous created node
					prev_node = createNode((String)selector.getSelectedItem());
					//queryData.printQueryDataContents();
					String newQuery = ""; 
					String selectItem = (String)selector.getSelectedItem();
					if (!selector.getSelectedItem().equals("-- Select --")){
                        String addtion = "";
						if (currentQuery.equals("")){
							newQuery = (String)selector.getSelectedItem();
							addtion = (String)selector.getSelectedItem();
						}else{
                            boolean prev_valuenode = true;
							if (!(selectItem.equals("Assign States")||selectItem.equals("Set State Function")||selectItem.equals("Set Numerical Value")||selectItem.equals("Assign Actions"))){
								prev_valuenode = false;

								addtion = (String)selector.getSelectedItem();


							}else if (selectItem.equals("Set Numerical Value")){
								boolean inputValid = false;
								String numLabel = "";
								while (!inputValid)
								{
									String input = JOptionPane.showInputDialog("Numerical value to be represented by Num node:");
									try{
										if (input == null)
										{
											inputValid = true;
											addtion = "0";
										}else{
											double numVal = Double.parseDouble(input);
											//add the value input to the node
											if (prev_node instanceof NumNode){
												NumNode nnode = (NumNode)prev_node;
												nnode.setNumValue(numVal);
												numLabel = Double.toString(numVal);
												nnode.setNodeLabel(numLabel);
												if (prev_node.getParentNode() instanceof MomentNode){
													int intNumVal = (int)numVal;
													if (intNumVal == 1){
														numLabel = intNumVal + "st";
													}else if (intNumVal == 2){
														numLabel = intNumVal + "nd";
													}else if (intNumVal == 3){
														numLabel = intNumVal + "rd";
													}else{
														numLabel = intNumVal + "th";
													}
												}
											}
											//String numLabel = Double.toString(numVal);
											addtion = numLabel;
											inputValid = true;
										}

									}catch (Exception exc){
										if (input != null) JOptionPane.showMessageDialog(	null,
												"Please enter a valid real number.",
												"Invalid entry",
												JOptionPane.ERROR_MESSAGE);
									}
								}
							}else if (selectItem.equals("Set State Function")){
								boolean inputValid = false;
								while (!inputValid){
									String input = JOptionPane.showInputDialog("State function to be represented by StateFunc node:");
									try{
										if (input == null)
										{
											inputValid = false;
										}else{
											if (MacroEditor.containsText(input))
											{
												addtion = "the state function " + input;
												if (prev_node instanceof StateFunctionNode){
													StateFunctionNode snode = (StateFunctionNode)prev_node;
													snode.setNodeLabel(input);
												}
												inputValid = true;
											}else{
												JOptionPane.showMessageDialog(	null,
														"Please specify a valid string for the state function\n"
														+ "consisting of letters and possibly numbers.",
														"Invalid entry",
														JOptionPane.ERROR_MESSAGE);
											}
										}
									}catch(Exception exc){
										if (input != null) JOptionPane.showMessageDialog(	null,
												"Please enter a valid string.",
												"Invalid entry",
												JOptionPane.ERROR_MESSAGE);
									}
								}
							}else if (selectItem.equals("Assign Actions")){
								//
								//PerformanceTreeNode node = null;
								//node = new ActionsNode(0, 0, "actionNode");
								//ActionLabelManager.actionLabelAssignmentDialog((ActionsNode)node);
								// create popup dialogue
								/*boolean okToProceed = QueryManager.getData().getCurrentNetData("Actions");	
							if (okToProceed){
								guiDialog = new JDialog(QueryManager.getEditor(),"Action Specification",true);
								Container contentPane = guiDialog.getContentPane();
								contentPane.setLayout(new BoxLayout(contentPane,BoxLayout.Y_AXIS));     

								ArrayList<String> actionLabels = new ArrayList<String>();
								actionLabels.add("-- Select --");
								actionLabels.addAll(QueryManager.getData().getActionLabels());		
								JComboBox comboBox = new JComboBox(actionLabels.toArray());	
								comboBox.setSelectedItem("-- Select --");

								ActionListener comboBoxListener = new ActionListener() {
									public void actionPerformed(ActionEvent e) {
										JComboBox cb = (JComboBox)e.getSource();
										String actionLabel = (String)cb.getSelectedItem();				
										if (!actionLabel.equals("-- Select --")) {
											addtion = "the action identified by label '" +actionLabel+"'";
											guiDialog.dispose();
										}
									} 
								};	

								comboBox.addActionListener(comboBoxListener);
								contentPane.add(comboBox);    

								// add buttons
								ActionListener cancelButtonListener = new ActionListener() {
									public void actionPerformed(ActionEvent e) {
										addtion = " the action that has not been specified yet ";
										guiDialog.dispose();
									} 
								};
								contentPane.add(new ButtonBar("Cancel", cancelButtonListener));     

								// visualise popup
								guiDialog.pack();
								guiDialog.setLocationRelativeTo(null);
								guiDialog.setVisible(true);	

								//
							}*/
							}else if(selectItem.equals("Assign States")){
								if (prev_node instanceof StatesNode){
									StateLabelManager.stateLabelAssignmentDialog((StatesNode)prev_node);
								}
								StatesNode snode = (StatesNode)prev_node;
								if (snode.getStateLabel()!=null){
									addtion = " the set of states identified by label '" +snode.getStateLabel()+"'";
								}else{
									addtion = " the set of states that has not been specified yet";
								}
							}
							newQuery = replacequery(addtion);
						}
						int removeLeft = currentParamLeft;
						int removeRight = currentParamRight;


						updateQuery(newQuery,removeLeft, removeRight, addtion,false);
					}
				}else{
					queryDone();	
				}
			}
		}
	}

	//update query in the query box
    private void updateQuery(String newQuery, int rmvl, int rmvr, String add, boolean undoupdate){
		currentParamLeft = newQuery.indexOf("[");
		currentParamRight = newQuery.indexOf("]");
		if (currentParamLeft<0) currentParamLeft = 0;
		if (currentParamRight<0) currentParamRight = 0;

		//apply styles to the query text
		//StyledDocument styledDoc = queryDisplay.getStyledDocument();

		styledDoc.setParagraphAttributes(0, 1, queryDisplay.getStyle("main"), false);

		//store the current undo State if its not a undoupdate	

		if (!undoupdate){
			UndoState undostep = new UndoState(currentQuery, prev_node);
			undostep.set_undo_rmvl(rmvl);
			undostep.set_undo_rmvr(rmvl+add.length()-1);
			if(currentQuery.length()>0){
				undostep.set_undo_addtion(currentQuery.substring(rmvl, rmvr+1));
			}
			undoList.add(undostep);
			currentUndoStep = undoList.indexOf(undostep);
		}
		updateUndoButton();

		//update the query displayed
		try{
			if (styledDoc.getLength()>0){
				try{
					//styledDoc.remove(0, styledDoc.getLength());
					styledDoc.remove(rmvl, rmvr-rmvl+1);
				}catch (Exception e){
					System.out.println("position does not exist in document: " + e);
					System.exit(1);
				}
			}
			styledDoc.insertString(rmvl, add, null);
			styledDoc.setCharacterAttributes(rmvl, add.length(), queryDisplay.getStyle(currentTextColor), false);
		}catch (Exception e) {
			System.out.println("Exception when constructing document: " + e);
			System.exit(1);
		}

		//check if there is still required parameter
		if (currentParamLeft>0 & currentParamRight>0){
			currentQuery = newQuery;
			currentParam = currentQuery.substring(currentParamLeft, currentParamRight+1);

			ArrayList paramList = new ArrayList(); 		
			char[] query = newQuery.toCharArray();
			int length = newQuery.length();

			for (int i = 0; i < length; i++){
				if (query[i] == '['){
                    for (int j = i+1; j < length; j++){
						if (query[j] == ']'){
                            paramList.add(new Position(i, j));
							break;
						}
					}
				}
			}

			Iterator<Position> i = paramList.iterator();
			while (i.hasNext()){
				Position pos = i.next();
				int left = pos.getleft();
				int right = pos.getright();
				styledDoc.setCharacterAttributes(left, (right - left + 1), queryDisplay.getStyle("param"), true);
			}
			styledDoc.setCharacterAttributes(currentParamLeft, (currentParamRight - currentParamLeft + 1), queryDisplay.getStyle("currentparam"), true);
			queryDisplay.setSelectedTextColor(Color.white);
			queryDisplay.setSelectionColor(Color.blue);
			queryDisplay.select(currentParamLeft, currentParamRight);
		}

		updateSelector();
		//update the natural language representation
		QueryManager.printNaturalLanguageRepresentation();
		this.validate();
		this.repaint();

	}


	private void updateSelector(){
		selector.removeAllItems();
		currentTextColor = "black";
		if (currentParam.equals("")){
			selector.addItem("-- Select --");
			selector.addItem("Is it true that [bool]");
			selector.addItem("What is the [quantitative measures]");
		}else{
			if (currentParamLeft>0 & currentParamRight>0){
				if (currentParam.equals("[bool]")){
					//
					currentTextColor = "black";
					//
					selector.addItem("-- Select --");
					selector.addItem("[num] lies within [...]");
					selector.addItem("the negation of [bool] holds");
					selector.addItem("[bool] and [bool] hold");
					selector.addItem("[bool] or [bool] hold");
					selector.addItem("[states] is a subset of [states]");
					selector.addItem("[num] greater equal than [num]");
					selector.addItem("[num] less equal than [num]");
					selector.addItem("[num] equal than [num]");
					selector.addItem("[num] greater than [num]");
					selector.addItem("[num] less than [num]");

				}else if (currentParam.equals("[quantitative measures]")) {
					//
					currentTextColor = "red";
					//
					selector.addItem("-- Select --");
					selector.addItem("the passage time density defined by [states] and [states]");
					selector.addItem("the cumulative distribution function calculated from [PTD]");
					//
					selector.addItem("the [num] percentile of [PTD]");
					selector.addItem("the [num] percentile of [Dist]");
					//
					selector.addItem("the convolution of [PTD] and [PTD]");
					selector.addItem("the steady-state probability distribution of [statefunc] applied over [states]");
					selector.addItem("the set of states that provided that the system has started in [states] has a certain steady-state probability lying in [...]");
					selector.addItem("the set of states that the system can be in at the time instant given by [inum] with a certain probability given by [...]");
					selector.addItem("the transient probability of the system having started in [states] and being in [states] at the time instant given by [inum]");
					selector.addItem("the probability with which a value sampled from [PTD] lies within [...]");
					selector.addItem("the [inum] raw moment of [PTD]");
					selector.addItem("the [inum] raw moment of [Dist]");
					selector.addItem("the average rate of occurrence of [Action]");
					selector.addItem("[num] plus [num]");
					selector.addItem("[num] minus [num]");
					selector.addItem("[num] raised to the power of [num]");
					selector.addItem("[num] multiply by [num]");
					selector.addItem("[num] divide by [num]");
					selector.addItem("the range [num] to [num]");
				}else if (currentParam.equals("[PTD]")){
					//
					currentTextColor = "blue";
					//
					selector.addItem("-- Select --");
					selector.addItem("the passage time density defined by [states] and [states]");
					selector.addItem("the convolution of [PTD] and [PTD]");
				/*}else if (currentParam.equals("[Dist]")){
					selector.addItem("-- Select --");
					selector.addItem("the cumulative distribution function calculated from [PTD]");*/
				}else if (currentParam.equals("[states]")){
					//
					currentTextColor = "cyan";
					//
					selector.addItem("-- Select --");
					selector.addItem("Assign States");
//					selector.addItem("the set of states that provided that the system has started in [states] has a certain steady-state probability lying in [...]");
//					selector.addItem("the set of states that the system can be in at the time instant given by [num] with a certain probability given by [...]");
				}else if (currentParam.equals("[statefunc]")){
					selector.addItem("-- Select --");
					selector.addItem("Set State Function");
				}else if (currentParam.equals("[Dist]")){
					//
					currentTextColor = "green";
					//
					selector.addItem("-- Select --");
					selector.addItem("the cumulative distribution function calculated from [PTD]");
				}else if (currentParam.equals("[inum]")){
					selector.addItem("-- Select --");
					selector.addItem("Set Numerical Value");
				}else if (currentParam.equals("[num]")){
					//
					currentTextColor = "magenta";
					//
					selector.addItem("-- Select --");
					selector.addItem("Set Numerical Value");
					selector.addItem("the transient probability of the system having started in [states] and being in [states] at the time instant given by [inum]");
					selector.addItem("the probability with which a value sampled from [PTD] lies within [...]");
					selector.addItem("the frequency of [Action]");
					selector.addItem("the [inum] raw moment of [PTD]");
					selector.addItem("the [inum] raw moment of [Dist]");
				}else if (currentParam.equals("[...]")){
					selector.addItem("-- Select --");
					selector.addItem("the range [num] to [num]");
				}else if (currentParam.equals("[Action]")){
					selector.addItem("-- Select --");
					selector.addItem("Assign Actions");
				}else if (currentParam.equals("[statefunc]")){
					selector.addItem("-- Select --");
					selector.addItem("Set State Function");
				}
			}else{
				selector.addItem("-- Select --");
				selector.addItem("Query Done");
			}
		}
	}

	private String replacequery(String replacement){
		String partBefore = currentQuery.substring(0, currentParamLeft);
		String partAfter = currentQuery.substring(currentParamRight + 1);
		return partBefore + replacement + partAfter;
	}

	class Position{
		public final int left;
		public final int right;
		public Position(int l, int r){
			left = l;
			right = r;
		}
		public int getleft(){
			return left;
		}
		public int getright(){
			return right;
		}		
	}

	public void updateButtons() {
		undoButton.setText(undo.getUndoPresentationName());
		//redoButton.setText(undo.getRedoPresentationName());
		undoButton.setEnabled(undo.canUndo());
		//redoButton.setEnabled(undo.canRedo());
	}

	private void clearQuery(){
		int result=JOptionPane.showConfirmDialog(null,
				"Reset Cannot Be Undo, Confirm Reset?",
				"Confirm Reset The Query",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE);

		if (result == JOptionPane.YES_OPTION){
			currentQuery = "";
			currentParam = "";
			if (styledDoc.getLength()>0){
				try{
					styledDoc.remove(0, styledDoc.getLength());
				}catch (Exception ex){
					System.out.println("position does not exist in document: " + ex);
					System.exit(1);
				}
			}
			currentParamLeft = 0;
			currentParamRight = 0;
			currentTextColor = "black";
			currentUndoStep = 0;
			undoList.clear();
			updateSelector();
			//clear the data structure and the canvas
			clearQueryTree();
			//queryData.emptyData();
			queryData.nodeCounter = 1;
			queryData.arcCounter = 1;
			prev_node = null;
		}
	}

	//And this one listens for any changes to the document.
    private class MyDocumentListener
	implements DocumentListener {
		public void insertUpdate(DocumentEvent e) {
			//updateSelector();
		}
		public void removeUpdate(DocumentEvent e) {
			//updateSelector();
		}
		public void changedUpdate(DocumentEvent e) {
			updateSelector();
		}
	}
	private void updateUndoButton(){
		UndoState undostep = new UndoState(currentQuery, prev_node);
		if (undostep.canundo()) undoButton.setEnabled(true);
		else undoButton.setEnabled(false);
	}

	class UndoState{
		int undo_rmvl = 0;
		int undo_rmvr = 0;
		String undo_addtion = "";
		String undo_query = "";
		boolean undoable = false;
		final PerformanceTreeNode undo_node;

		public UndoState(String uquery, PerformanceTreeNode unode){
			undo_query = uquery;
			undo_node = unode;
		}
		public boolean canundo(){
            return undoList.size() > 0;
		}
		public void undo(){
			if (canundo()){
				if(undoList.size()>1){
					//find the number of arcs need to be deleted
					int numOfArcsToDelete = 0;
					if (undo_node instanceof OperationNode){
						numOfArcsToDelete = ((OperationNode)undo_node).getRequiredArcs().size();
					}
					//delete the node previous added
					deleteNode(undo_node);
					undoList.remove(undoList.size()-1);
					//reassign the prev_node after delete the current node
					UndoState nextundostep = undoList.get(undoList.size()-1);
					prev_node = nextundostep.undo_node;
					//restore the arcCount and nodeCount
					queryData.nodeCounter -= 1;
					queryData.arcCounter -= numOfArcsToDelete;

					updateQuery(undo_query, undo_rmvl, undo_rmvr, undo_addtion,true);
				}else {
					currentQuery = "";
					currentParam = "";
					if (styledDoc.getLength()>0){
						try{
							styledDoc.remove(0, styledDoc.getLength());
						}catch (Exception ex){
							System.out.println("position does not exist in document: " + ex);
							System.exit(1);
						}
					}
					currentParamLeft = 0;
					currentParamRight = 0;
					currentTextColor = "black";
					currentUndoStep = 0;
					undoList.clear();
					updateSelector();
				}

				updateUndoButton();
			}
		}
		public void set_undo_rmvl(int urmvl){
			//if (undoList.size()==0) undo_rmvl = 0;
			undo_rmvl = urmvl;	
		}
		public void set_undo_rmvr(int urmvr){
			//if (undoList.size()==0) undo_rmvr = 0;				
			undo_rmvr = urmvr;
		}
		public void set_undo_addtion(String uadd){
			//if (undoList.size()==0) undo_addtion = "";
			undo_addtion = uadd;
		}
		public void set_undo_query(String uquery){
			//if (undoList.size()==0) undo_query = "";
			undo_query = uquery;
		}
	}

	private PerformanceTreeNode createNode(String node_str){
		if (node_str != null){
			if (!node_str.equals("-- Select --")){
				PerformanceTreeNode node = null;
				String expectingArcID = getExpectingArcID();
				//create operation node
				if (node_str.equals("Is it true that [bool]") || node_str.equals("What is the [quantitative measures]")){
					//node = new ResultNode(newNodePositionX, newNodePositionY);
					queryData = QueryManager.getData();
					PerformanceTreeNode[] nodes = queryData.getNodes();
					if (nodes.length == 1){
						PerformanceTreeNode firstNode = nodes[0];
						if (firstNode instanceof ResultNode){
							newNodePositionX = firstNode.getPositionX();
							newNodePositionY = firstNode.getPositionY() + firstNode.getComponentWidth()*1.5;
							return firstNode;
						}
					}
				}else if(node_str.equals("[num] lies within [...]")){
					node = new InIntervalNode(newNodePositionX, newNodePositionY);
				}else if(node_str.equals("the negation of [bool] holds")){
					node = new NegationNode(newNodePositionX, newNodePositionY);
				}else if(node_str.equals("[states] is a subset of [states]")){
					node = new SubsetNode(newNodePositionX, newNodePositionY);
				}else if(node_str.equals("[num] greater equal than [num]")||node_str.equals("[num] less equal than [num]")||node_str.equals("[num] equal than [num]")||node_str.equals("[num] greater than [num]")||node_str.equals("[num] less than [num]")){
					node = new ArithCompNode(newNodePositionX, newNodePositionY);
					if(node_str.equals("[num] greater equal than [num]")) ((OperationNode)node).setOperation("geq");
					if(node_str.equals("[num] less equal than [num]")) ((OperationNode)node).setOperation("leq");
					if(node_str.equals("[num] equal than [num]")) ((OperationNode)node).setOperation("eq");
					if(node_str.equals("[num] greater than [num]")) ((OperationNode)node).setOperation("gt");
					if(node_str.equals("[num] less than [num]")) ((OperationNode)node).setOperation("lt");
				}else if(node_str.equals("[bool] and [bool] hold")||node_str.equals("[bool] or [bool] hold")){
					node = new DisconNode(newNodePositionX, newNodePositionY);
					if(node_str.equals("[bool] and [bool] hold")) ((OperationNode)node).setOperation("and");
					if(node_str.equals("[bool] or [bool] hold")) ((OperationNode)node).setOperation("or");
				}else if(node_str.equals("the passage time density defined by [states] and [states]")){
					node = new PassageTimeDensityNode(newNodePositionX, newNodePositionY);
				}else if(node_str.equals("the cumulative distribution function calculated from [PTD]")){
					node = new DistributionNode(newNodePositionX, newNodePositionY);
				}else if(node_str.equals("the convolution of [PTD] and [PTD]")){
					node = new ConvolutionNode(newNodePositionX, newNodePositionY);
				}else if(node_str.equals("the steady-state probability distribution of [statefunc] applied over [states]")){
					node = new SteadyStateProbNode(newNodePositionX, newNodePositionY);
				}else if(node_str.equals("the set of states that provided that the system has started in [states] has a certain steady-state probability lying in [...]")){
					node = new SteadyStateStatesNode(newNodePositionX, newNodePositionY);
				}else if(node_str.equals("the set of states that the system can be in at the time instant given by [inum] with a certain probability given by [...]")){
					node = new StatesAtTimeNode(newNodePositionX, newNodePositionY);
				}else if(node_str.equals("the transient probability of the system having started in [states] and being in [states] at the time instant given by [inum]")){
					node = new ProbInStatesNode(newNodePositionX, newNodePositionY);
				}else if(node_str.equals("the probability with which a value sampled from [PTD] lies within [...]")){
					node = new ProbInIntervalNode(newNodePositionX, newNodePositionY);
				}else if(node_str.equals("the [inum] raw moment of [PTD]")||node_str.equals("the [inum] raw moment of [Dist]")){
					node = new MomentNode(newNodePositionX, newNodePositionY);
				}else if(node_str.equals("the frequency of [Action]")){
					node = new FiringRateNode(newNodePositionX, newNodePositionY);
				}else if(node_str.equals("[num] plus [num]")||node_str.equals("[num] minus [num]")||node_str.equals("[num] raised to the power of [num]")||node_str.equals("[num] multiply by [num]")||node_str.equals("[num] divide by [num]")){
					node = new ArithOpNode(newNodePositionX, newNodePositionY);
					if(node_str.equals("[num] plus [num]")) ((OperationNode)node).setOperation("plus");
					if(node_str.equals("[num] minus [num]")) ((OperationNode)node).setOperation("minus");
					if(node_str.equals("[num] raised to the power of [num]")) ((OperationNode)node).setOperation("power");
					if(node_str.equals("[num] multiply by [num]")) ((OperationNode)node).setOperation("times");
					if(node_str.equals("[num] divide by [num]")) ((OperationNode)node).setOperation("div");
				}else if(node_str.equals("the range [num] to [num]")){
					node = new RangeNode(newNodePositionX, newNodePositionY);
					//create value node
				}else if(node_str.equals("Assign States")){
					node = new StatesNode(newNodePositionX, newNodePositionY);
				}else if(node_str.equals("Set State Function")){
					node = new StateFunctionNode(newNodePositionX, newNodePositionY);
				}else if(node_str.equals("Set Numerical Value")){
					node = new NumNode(newNodePositionX, newNodePositionY);
				}else if(node_str.equals("Assign Actions")){
					node = new ActionsNode(newNodePositionX, newNodePositionY);
				}else if(node_str.equals("Assign Boolean")){
					node = new BoolNode(newNodePositionX, newNodePositionY);
				}else if (node_str.equals("the [num] percentile of [PTD]")||node_str.equals("the [num] percentile of [Dist]")){
					node = new PercentileNode(newNodePositionX, newNodePositionY);
				}

				queryData.addNode(node);
				// update node to reflect change in label
				queryData.updateNode(node);

				//String expectingArcID = getExpectingArcID();
				node.setIncomingArcID(expectingArcID);
				PerformanceTreeArc expectingArc = queryData.getArc(expectingArcID);
				expectingArc.setTarget(node);
				
				//adjust the horizontal position of node according to the tree level
				adjustNewNodePositionX(node);
				//update the coordinates of the new node
				node.setPositionX(newNodePositionX);
				node.setPositionY(newNodePositionY);	
				//node.setLocation((int)newNodePositionX, (int)newNodePositionY);

				//set the required arc end point for new node
				/*int arccounter = 0;
		ArrayList requiredArcIDs = ((OperationNode)prev_node).getOutgoingArcIDs();
		int totalArcRequired = requiredArcIDs.size();
		Iterator arcIt = requiredArcIDs.iterator();
		while (arcIt.hasNext()){
			arccounter++;
			String currentArcID = (String)arcIt.next(); 
			PerformanceTreeArc currentArc = queryData.getArc(currentArcID);
			if (totalArcRequired == 1){
				currentArc.setEndPoint((int)newNodePositionX, (int)newNodePositionY + node.getComponentWidth(), true);
				currentArc.updateArcPosition();
				currentArc.updateLabelPosition();
				//currentArc.setLocation((int)newNodePositionX, (int)(newNodePositionY + node.getComponentWidth()));
			}else if (totalArcRequired == 2 && arccounter == 1){
				currentArc.setEndPoint((int)newNodePositionX - node.getComponentWidth(), (int)newNodePositionY + node.getComponentWidth(), true);
				currentArc.updateArcPosition();
				currentArc.updateLabelPosition();
			}else if (totalArcRequired == arccounter){
				currentArc.setEndPoint((int)newNodePositionX + node.getComponentWidth(), (int)newNodePositionY + node.getComponentWidth(), true);
				currentArc.updateArcPosition();
				currentArc.updateLabelPosition();
			}else if(totalArcRequired == 3 && arccounter == 1){
				currentArc.setEndPoint((int)newNodePositionX - node.getComponentWidth(), (int)newNodePositionY + node.getComponentWidth(), true);
				currentArc.updateArcPosition();
				currentArc.updateLabelPosition();
			}else if(totalArcRequired == 3 && arccounter == 2){
				currentArc.setEndPoint((int)newNodePositionX, (int)newNodePositionY + node.getComponentWidth(), true);
				currentArc.updateArcPosition();
				currentArc.updateLabelPosition();
			}else{
				currentArc.setEndPoint((int)newNodePositionX, (int)newNodePositionY + node.getComponentWidth(), true);
				currentArc.updateArcPosition();
				currentArc.updateLabelPosition();
			}
		}*/

				// update node to reflect change in label
				queryData.updateNode(node);


				// make sure all arcs connect to the node nicely
				node.updateConnected();

				queryData.printQueryDataContents();

				//draw the change on the canvas
				QueryView queryView = QueryManager.getView(); 
				queryView.addNewPerformanceTreeObject(node);
				ViewExpansionComponent expand = new ViewExpansionComponent(getWidth(), getHeight());
				expand.addZoomController(queryView.getZoomController());
				queryView.add(expand);

				// we're moving around a node
				// update node's centre to current location
				node.setCentre(Grid.getModifiedX(node.getCentre()
                                                         .getX()),
                               Grid.getModifiedY(node.getCentre()
                                                         .getY()));
				QueryManager.printNaturalLanguageRepresentation();


				queryView.repaint();


				return node;
			}
		}
		return prev_node;
	}

	private String getExpectingArcID(){
		String arcToBeAssigned = null;
		//trace back the tree to find the next arc the current new node should be connected to if previous created node is a valuenode
		if (prev_node instanceof ValueNode){
			OperationNode parentNode = (OperationNode)prev_node.getParentNode();
			arcToBeAssigned = TraceBack(parentNode);
			//if the previous created node is a operationnode, the next arc need to be assigned is the first outgoing arc of the previous created node
		}else{
			if (prev_node instanceof OperationNode){
				ArrayList requiredArcIDs = ((OperationNode)prev_node).getOutgoingArcIDs();
				arcToBeAssigned = (String)requiredArcIDs.get(0);
				//work out the coordinates of the new node
				int totalArcRequired = requiredArcIDs.size();
				if (totalArcRequired == 1){
					newNodePositionX = prev_node.getPositionX();
					newNodePositionY = prev_node.getPositionY() + prev_node.getComponentWidth()*1.5;
				}else{
					//newNodePositionX = prev_node.getPositionX() - prev_node.getComponentWidth()*getHrate();
					newNodePositionX = prev_node.getPositionX() - prev_node.getComponentWidth();
					newNodePositionY = prev_node.getPositionY() + prev_node.getComponentWidth()*1.5;
				}

			}else{
				arcToBeAssigned = "impossible";
			}
		}
		return arcToBeAssigned;
	}

	//adjust horizontal coordinates of nodes
	private void adjustNewNodePositionX(PerformanceTreeNode newNode){
		int adjustRate = 1;
		if (newNode.getParentNode() == null){
			adjustRate = 3;
		}else if((newNode.getParentNode()).getParentNode() == null){
			adjustRate = 3;
		}else if (((newNode.getParentNode()).getParentNode()).getParentNode() == null){
			adjustRate = 2;
		}else if ((((newNode.getParentNode()).getParentNode()).getParentNode()).getParentNode() == null){
			adjustRate = 1;
		}else{
			adjustRate = 0;
		}
		if (prev_node instanceof OperationNode){
			ArrayList requiredArcIDs = ((OperationNode)prev_node).getOutgoingArcIDs();
			//work out the coordinates of the new node
			int totalArcRequired = requiredArcIDs.size();
			if (totalArcRequired == 1){

			}else{
				newNodePositionX -= prev_node.getComponentWidth()*adjustRate;
			}
		}else if (prev_node instanceof ValueNode){
			OperationNode parentNode = (OperationNode)newNode.getParentNode();
			ArrayList requiredArcIDs = parentNode.getOutgoingArcIDs();

			String currentArcID = newNode.getIncomingArcID();
			int totalArcRequired = requiredArcIDs.size();
			int arcIndex = requiredArcIDs.indexOf(currentArcID);
			if (arcIndex == totalArcRequired - 1){
				//newNodePositionX = nodeInput.getPositionX() + nodeInput.getComponentWidth()*getHrate();
				newNodePositionX += parentNode.getComponentWidth()*adjustRate;
			}		
		}
	}
		
	//find the next Arc need to be Assign Value
	private String TraceBack(OperationNode nodeInput){
		if (nodeInput instanceof ResultNode){
			return "Query Complete";
		}else{
			ArrayList requiredArcIDs = nodeInput.getOutgoingArcIDs();
			Iterator arcIt = requiredArcIDs.iterator();
			while (arcIt.hasNext()){
				String currentArcID = (String)arcIt.next();
				PerformanceTreeArc currentArc = queryData.getArc(currentArcID);
				if (currentArc.getTargetID()==null){
					//work out the coordinates of the new node
					int totalArcRequired = requiredArcIDs.size();
					int arcIndex = requiredArcIDs.indexOf(currentArcID);
					//if the new node is the only required child node of parents node
					if (totalArcRequired == 1){
						newNodePositionX = nodeInput.getPositionX();
						newNodePositionY = nodeInput.getPositionY() + nodeInput.getComponentWidth()*1.5;
						//if the new node is the last required child node of the parents node
					}else if (arcIndex == totalArcRequired - 1){
						//newNodePositionX = nodeInput.getPositionX() + nodeInput.getComponentWidth()*getHrate();
						newNodePositionX = nodeInput.getPositionX() + nodeInput.getComponentWidth();
						newNodePositionY = nodeInput.getPositionY() + nodeInput.getComponentWidth()*1.5;
					}else{
						newNodePositionX = nodeInput.getPositionX();
						newNodePositionY = nodeInput.getPositionY() + nodeInput.getComponentWidth()*1.5;
					}
					return currentArcID;
				}				
			}
			OperationNode parentNode = (OperationNode)nodeInput.getParentNode();
			return TraceBack(parentNode);
		}
	}
	//reset the query data structure and visualization
	private void clearQueryTree(){

		if (MacroManager.getEditor() == null)
			QueryManager.clearInfoBox();
		else
			MacroManager.getEditor().writeToInfoBox("");

		Component[] netObj;
		netObj = queryData.getNodes();

        for(Component aNetObj : netObj)
        {
            if((aNetObj instanceof PerformanceTreeObject))
            {
                if(aNetObj instanceof PerformanceTreeArc)
                {
                    if(QueryManager.allowDeletionOfArcs)
                    {
                        ((PerformanceTreeArc) aNetObj).delete();
                    }
                }
                else if(aNetObj instanceof PerformanceTreeNode)
                {
                    if(aNetObj instanceof ResultNode)
                    {
                        String msg = QueryManager.addColouring("Deletion of the topmost node in the tree is not permitted.");
                        if(MacroManager.getEditor() == null)
                            QueryManager.writeToInfoBox(msg);
                        else
                            MacroManager.getEditor().writeToInfoBox(msg);
                    }
                    else if((aNetObj instanceof MacroNode) && MacroManager.getEditor() != null)
                    {
                        String msg = QueryManager.addColouring("Deletion of the topmost macro node in the tree is not permitted.");
                        MacroManager.getEditor().writeToInfoBox(msg);
                    }
                    else
                    {
                        if(!sequentialNodeCase((PerformanceTreeNode) aNetObj))
                        {
                            // just delete the node, not the associated arc
                            ((PerformanceTreeNode) aNetObj).delete();
                        }
                    }
                }
                else
                {
                    ((PerformanceTreeObject) aNetObj).delete();
                }
            }
        }
	}

	private void deleteNode(PerformanceTreeNode node){
		if (node instanceof PerformanceTreeNode){
			if (node instanceof ResultNode) {
				String msg = QueryManager.addColouring("Deletion of the topmost node in the tree is not permitted.");
				if (MacroManager.getEditor() == null)
					QueryManager.writeToInfoBox(msg);
				else
					MacroManager.getEditor().writeToInfoBox(msg);
			}					
			else {
				if(!sequentialNodeCase(node)) {
					// just delete the node, not the associated arc
					node.delete();
				}
			}
		}
	}
	/** This method takes care of the case when a node is linked directly to
	 *  a SequentialNode through an optional arc. In such as case, the arc 
	 *  should be removed along with the node.
	 * @param node
     * @return
	 */
	private boolean sequentialNodeCase(PerformanceTreeNode node) {
		if (node.getIncomingArc() != null) {
			PerformanceTreeArc incomingArc = node.getIncomingArc();
			PerformanceTreeNode parentNode = incomingArc.getSource();
			if (!incomingArc.isRequired() && 
					parentNode instanceof SequentialNode &&
					sequentialNodeHasAtLeastOneOptionalArc(parentNode)) {
				node.delete();
				incomingArc.delete();
				return true;
			}
			else
				return false;
		}
		else
			return false;
	}

	/**
	 * We should only allow deletion of the associated optional arc if there are
	 * at least two optional arcs. This is so, because a new optional arc is only
	 * created whenever the last free arc is assigned to a node.
	 * @param node
	 * @return
	 */
	private boolean sequentialNodeHasAtLeastOneOptionalArc(PerformanceTreeNode node) {
		if (node instanceof SequentialNode) {
			SequentialNode seqNode = (SequentialNode)node;
			ArrayList<String> outgoingArcIDs = (ArrayList<String>)seqNode.getOutgoingArcIDs();
			Iterator<String> i = outgoingArcIDs.iterator();
			int optionalArcCount = 0;
			while (i.hasNext()) {
				PerformanceTreeArc arc = QueryManager.getData().getArc(i.next());
				if (!arc.isRequired()) 
					optionalArcCount++;
			}
            return optionalArcCount > 1;
		}
		else return false;
	}
	//the rate to adjust the nodes' horizontal coordinates
	private int getHrate(){
		int hrate  = 1;
		/*if (prev_node instanceof OperationNode){
			if (prev_node.getParentNode() == null){
				hrate = 3;
			}else if((prev_node.getParentNode()).getParentNode() == null){
				hrate = 2;
			}else if (((prev_node.getParentNode()).getParentNode()).getParentNode() == null){
				hrate = 1;
			}else{
				hrate = 1;
			}
		}else{*/
			if (prev_node.getParentNode() == null){
				hrate = 3;
			}else if((prev_node.getParentNode()).getParentNode() == null){
				hrate = 3;
			}else if (((prev_node.getParentNode()).getParentNode()).getParentNode() == null){
				hrate = 2;
			}else{
				hrate = 1;
			}
		//}
		return hrate;
	}

	public void queryDone(){
		QueryManager.botPanel.setSelectedIndex(0);
		QueryManager.checkTextEditing();
		//clearText();
		prev_node = null;
	}
	
	public void clearText(){
		currentQuery = "";
		currentParam = "";
		if (styledDoc.getLength()>0){
			try{
				styledDoc.remove(0, styledDoc.getLength());
			}catch (Exception ex){
				System.out.println("position does not exist in document: " + ex);
				System.exit(1);
			}
		}
		currentParamLeft = 0;
		currentParamRight = 0;
		currentTextColor = "black";
		currentUndoStep = 0;
		undoList.clear();
		updateSelector();
	}

}

