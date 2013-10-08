/**
 * EditPerformanceTreeNodeAction
 * 
 * @author Tamas Suto
 * @date 22/04/07
 */

package pipe.modules.queryeditor.gui;

import pipe.gui.ApplicationSettings;
import pipe.handlers.StringHelper;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeNode;
import pipe.modules.queryeditor.gui.performancetrees.macros.ArgumentNode;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroEditor;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroManager;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.*;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.*;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.labels.ActionLabelManager;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.labels.StateLabelManager;
import pipe.views.PlaceView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.regex.Pattern;

class EditPerformanceTreeNodeAction extends AbstractAction
{

	private final Container				contentPane;
	private final PerformanceTreeNode	node;
	private final String				actionType;

	private String						previousStateFuncString	= null;

	public EditPerformanceTreeNodeAction(	final Container contentPaneInput,
											final PerformanceTreeNode nodeInput,
											final String actionTypeInput) {
		this.contentPane = contentPaneInput;
		this.node = nodeInput;
		this.actionType = actionTypeInput;
	}

	public void actionPerformed(final ActionEvent e)
	{
		validateTree();
	}

	void validateTree()
	{

		switch (this.node.getNodeType())
		{
			case DISCON :
			{
				if (this.actionType.equals("or") || this.actionType.equals("and"))
					((DisconNode) this.node).setOperation(this.actionType);
				if (MacroManager.getEditor() == null)
				{
					// not in macro mode
					QueryManager.getData().updateNode(this.node);
				}
				else
				{
					// in macro mode
					MacroManager.getEditor().updateNode(this.node);
				}
				break;
			}
			case ARITHOP :
			{
				if (this.actionType.equals("plus") || this.actionType.equals("minus") ||
					this.actionType.equals("times") || this.actionType.equals("div") ||
					this.actionType.equals("power"))
					((ArithOpNode) this.node).setOperation(this.actionType);
				if (MacroManager.getEditor() == null)
				{
					// not in macro mode
					QueryManager.getData().updateNode(this.node);
				}
				else
				{
					// in macro mode
					MacroManager.getEditor().updateNode(this.node);
				}
				break;
			}
			case ARITHCOMP :
			{
				if (this.actionType.equals("lt") || this.actionType.equals("leq") ||
					this.actionType.equals("eq") || this.actionType.equals("geq") ||
					this.actionType.equals("gt"))
					((ArithCompNode) this.node).setOperation(this.actionType);
				if (MacroManager.getEditor() == null)
				{
					// not in macro mode
					QueryManager.getData().updateNode(this.node);
				}
				else
				{
					// in macro mode
					MacroManager.getEditor().updateNode(this.node);
				}
				break;
			}
			case NUM :
			{
				if (this.actionType.equals("Num"))
				{
					boolean inputValid = false;
					while (!inputValid)
					{
						String input = JOptionPane.showInputDialog("Numerical value to be represented by Num node:");
						try
						{
							// check for Cancel
							if (input == null)
							{
								inputValid = true;
							}
							else
							{
								boolean momentCase = false;
								double numVal = Double.parseDouble(input);

								if (this.node.getParentNode() != null)
								{
									if (this.node.getParentNode() instanceof MomentNode)
									{
										// check for whether the parent node is
										// a
										// MomentNode, since in that
										// case we want to only allow integers
										// to be
										// specified
										try
										{
											momentCase = true;

											if (!isDecimalAnInteger(numVal))
												throw new NumberFormatException();

											// set numerical value in node
											((NumNode) this.node).setNumValue(numVal);

											// display numerical value below
											// node
											// String nodeLabel =
											// Integer.toString(intNumVal);
											String nodeLabel = Double.toString(numVal);
											((NumNode) this.node).setNodeLabel(nodeLabel);

											if (MacroManager.getEditor() == null)
											{
												// not in macro mode
												QueryManager.getData().updateNode(this.node);
											}
											else
											{
												// in macro mode
												MacroManager.getEditor().updateNode(this.node);
											}

											inputValid = true;
										}
										catch (NumberFormatException ex)
										{
											JOptionPane.showMessageDialog(	this.contentPane,
																			"Please enter a valid integer.",
																			"Invalid entry",
																			JOptionPane.ERROR_MESSAGE);
										}
									}
									else if (this.node.getParentNode() instanceof PercentileNode)
									{
										if (numVal < 0 || numVal > 100)
										{
											JOptionPane.showMessageDialog(	this.contentPane,
																			"Please enter a real number between 0 and 100.",
																			"Invalid entry",
																			JOptionPane.ERROR_MESSAGE);

										}
										else
										{
											// set numerical value in node
											((NumNode) this.node).setNumValue(numVal);

											// display numerical value below
											// node
											String nodeLabel = Double.toString(numVal);
											((NumNode) this.node).setNodeLabel(nodeLabel);

											if (MacroManager.getEditor() == null)
											{
												// not in macro mode
												QueryManager.getData().updateNode(this.node);
											}
											else
											{
												// in macro mode
												MacroManager.getEditor().updateNode(this.node);
											}

											inputValid = true;
										}
									}
								}

								if (!momentCase)
								{
									// set numerical value in node
									((NumNode) this.node).setNumValue(numVal);

									// display numerical value below node
									String nodeLabel = Double.toString(numVal);
									((NumNode) this.node).setNodeLabel(nodeLabel);

									if (MacroManager.getEditor() == null)
									{
										// not in macro mode
										QueryManager.getData().updateNode(this.node);
									}
									else
									{
										// in macro mode
										MacroManager.getEditor().updateNode(this.node);
									}

									inputValid = true;
								}
							}
						}
						catch (Exception exc)
						{
							if (input != null)
								JOptionPane.showMessageDialog(	this.contentPane,
																"Please enter a valid real number.",
																"Invalid entry",
																JOptionPane.ERROR_MESSAGE);
						}
					}
				}
				break;
			}
			case BOOL :
			{
				boolean boolValue = true;
				if (this.actionType.equals("true"))
					boolValue = true;
				else if (this.actionType.equals("false"))
					boolValue = false;
				((BoolNode) this.node).setBooleanValue(boolValue);

				// display boolean value below node
				String nodeLabel = Boolean.toString(boolValue);
				((BoolNode) this.node).setNodeLabel(nodeLabel);

				if (MacroManager.getEditor() == null)
				{
					// not in macro mode
					QueryManager.getData().updateNode(this.node);
				}
				else
				{
					// in macro mode
					MacroManager.getEditor().updateNode(this.node);
				}
				break;
			}
			case ACTIONS :
			{
				if (this.actionType.equals("Actions"))
					ActionLabelManager.actionLabelAssignmentDialog((ActionsNode) this.node);
				break;
			}
			case STATES :
			{
				if (this.actionType.equals("Assign States") || this.actionType.equals("Edit States"))
					StateLabelManager.stateLabelAssignmentDialog((StatesNode) this.node);
				break;
			}
			case STATEFUNCTION :
			{
				if (this.actionType.equals("StateFunction"))
				{
					boolean inputValid = false;
					while (!inputValid)
					{
						String input = JOptionPane.showInputDialog(	"State function to be represented by StateFunc node:",
																	this.previousStateFuncString);
						try
						{
							// check for Cancel
							if (input == null)
							{
								inputValid = true;
							}
							else
							{
								this.previousStateFuncString = input = input.trim();
								String statesDef = "#\\([\\w\\s]+\\)";

								String allArithOp = "\\s*[\\+\\-\\/\\*]\\s*";
								String allArithComp = "\\s*(\\<\\=|\\={2}|\\>\\=|>|<)\\s*";
								String conditionalOp = "\\s*(\\|{2}|\\&{2})\\s*";

								String aDouble = "(\\d+(\\.\\d+)?)";

								String valueStatement = "(" + aDouble + allArithOp + ")?" + statesDef;
								String valueExpression = "(" + valueStatement + "(" + allArithOp +
															valueStatement + ")*)+";

								String doubleOrValExp = "\\s*(" + aDouble + "|" + valueExpression + ")\\s*";

								String conditionalAssign = "\\s*\\?\\s*" + doubleOrValExp + "\\s*\\:\\s*" +
															doubleOrValExp;

								String conditionStatement = valueExpression + allArithComp + doubleOrValExp +
															"(" + conditionalOp + valueExpression +
															allArithComp + doubleOrValExp + ")*";
								String conditionExpression = "(" + conditionStatement + conditionalAssign +
																")+";

								String regex = "^" + conditionExpression + "|" + valueExpression + "$";

								StringBuilder replacement = new StringBuilder(input);
								HashMap<String, String> nameToId = new HashMap<String, String>();
                                PlaceView[] placeViews = ApplicationSettings.getApplicationView().getCurrentPetriNetView().places();
								for (PlaceView p : placeViews)
								{
									nameToId.put(p.getName(), p.getId());
								}

								Pattern p1 = Pattern.compile(regex);
								if (!p1.matcher(input).matches())
								{
									JOptionPane.showMessageDialog(	this.contentPane,
																	"Please specify a valid string for the state function!\n"
																	+ "states are referenced in the form '#(Statename)' case sensitively.\n"
																	+ "Arithmetic operators and numbers can be used to specify \n"
																	+ "queries referencing multiple states (forming a State expression) \nE.G. '3 * #(foo) + #(bar)'\n"
																	+ "where foo and bar are state names in the model.\n"

																	+ "State expressions can be used to form Conditional expressions in which state expressions are incorporated with an Arithmetic Comparison operator and a number to form a boolean."
																	+ "\nE.G. '3 * #(foo) + #(bar) <= 2'\n"
																	+ "To form a valid expression a Conditional Expression is then inserted into a statement of the form \n{'Conditional expression' ? 'number' : 'number'}",
																	"Invalid StateFunction",
																	JOptionPane.ERROR_MESSAGE);
								}
								else if (StringHelper.hasSpecifiedLabels(input, replacement, nameToId))
								{
									// replace input with string containing
									// place ids rather than
									// their user friendly names!
									input = replacement.toString();

									// set function value in node
									((StateFunctionNode) this.node).setFunction(input);
									// display numerical value below node
									((StateFunctionNode) this.node).setNodeLabel(input);

									if (MacroManager.getEditor() == null)
									{
										// not in macro mode
										QueryManager.getData().updateNode(this.node);
									}
									else
									{
										// in macro mode
										MacroManager.getEditor().updateNode(this.node);
									}
									inputValid = true;
								}
								else
								{
									JOptionPane.showMessageDialog(	this.contentPane,
																	"Some of the specified states are not found in the model, please\n"
																	+ "ensure correct spelling and case are used for all states",
																	"Invalid StateFunction",
																	JOptionPane.ERROR_MESSAGE);
								}

							}
						}
						catch (Exception exc)
						{
							if (input != null)
								JOptionPane.showMessageDialog(	this.contentPane,
																"Please enter a valid string.",
																"Invalid entry",
																JOptionPane.ERROR_MESSAGE);
						}
					}
				}
				break;
			}
			case MACRO :
			{
				if (this.actionType.equals("Assign Macro") || this.actionType.equals("Edit Macro"))
				{
					if (this.node.getParentNode() == null)
					{
						// only allow macro assignment if the macro node hasn't
						// been
						// linked up
						// with any other node yet (because of typing problems)
						MacroManager.macroAssignmentDialog((MacroNode) this.node);
					}
					else
					{
						JOptionPane.showMessageDialog(	QueryManager.getEditor().getContentPane(),
														"You can only assign a macro definition to a Macro\n"
														+ "node if that node has not been linked up with any\n"
														+ "other node yet. This is due to type compatibility\n"
														+ "considerations. Please ensure that you decouple the\n"
														+ "Macro node first before attempting to assign a macro\n"
														+ "definition to it.",
														"Warning",
														JOptionPane.ERROR_MESSAGE);
					}
				}
				break;
			}
			case ARGUMENT :
			{
				if (this.actionType.equals("Argument"))
				{
					boolean inputValid = false;
					while (!inputValid)
					{
						String input = JOptionPane.showInputDialog("Argument name:");
						try
						{
							// check for Cancel
							if (input == null)
							{
								inputValid = true;
							}
							else
							{
								if (MacroEditor.containsLetters(input))
								{
									// set argument name in node
									((ArgumentNode) this.node).setArgumentName(input);

									// display argument name below node
									((ArgumentNode) this.node).setNodeLabel(input);

									if (MacroManager.getEditor() != null)
									{
										MacroManager.getEditor().updateNode(this.node);
									}

									inputValid = true;
								}
								else
								{
									JOptionPane.showMessageDialog(	this.contentPane,
																	"Please specify a valid name for the argument\n"
																	+ "consisting of letters and possibly numbers.",
																	"Invalid entry",
																	JOptionPane.ERROR_MESSAGE);
								}
							}
						}
						catch (Exception exc)
						{
							if (input != null)
								JOptionPane.showMessageDialog(	this.contentPane,
																"Please enter a valid string.",
																"Invalid entry",
																JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
		}

		QueryManager.printNaturalLanguageRepresentation();
		this.node.repaint();
	}

	private boolean isDecimalAnInteger(final double decimalNo)
	{
		boolean decimalIsAnInteger = true;
		String stringRepresentation = Double.toString(decimalNo);

		if (!stringRepresentation.equals(""))
		{
			int indexOfDecimalDot = stringRepresentation.indexOf(".");
			if (indexOfDecimalDot != -1)
			{
				// all characters after the decimal dot (if one exists) have to
				// be a 0
				for (int i = indexOfDecimalDot + 1; i < stringRepresentation.length(); i++)
				{
					char chr = stringRepresentation.charAt(i);
					if (!String.valueOf(chr).equals("0"))
					{
						decimalIsAnInteger = false;
						break;
					}
				}
			}
		}
		return decimalIsAnInteger;
	}

}
