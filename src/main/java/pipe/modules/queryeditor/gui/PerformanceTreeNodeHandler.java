/**
 * PerformanceTreeNodeHandler
 * 
 * Class used to implement methods corresponding to mouse events on nodes.
 * 
 * @author Tamas Suto
 * @date 22/04/07
 */

package pipe.modules.queryeditor.gui;

import java.awt.Container;
import java.awt.event.MouseEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import pipe.common.PetriNetNode;
import pipe.modules.interfaces.QueryConstants;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeNode;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroManager;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.StatesNode;

public class PerformanceTreeNodeHandler extends PerformanceTreeObjectHandler implements QueryConstants
{

	public PerformanceTreeNodeHandler(final Container contentpane, final PerformanceTreeNode obj) {
		super(contentpane, obj);
	}

	@Override
	public void mouseClicked(final MouseEvent e)
	{
		super.mouseClicked(e);
		if (e.getButton() == MouseEvent.BUTTON2 || e.getButton() == MouseEvent.BUTTON3)
		{
			PetriNetNode nodeType = ((PerformanceTreeNode) this.myObject).getNodeType();
			if (!(nodeType == PetriNetNode.RESULT || nodeType == PetriNetNode.MACRO && MacroManager.getEditor() != null))
			{
				JPopupMenu popup = getPopup(e);
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	/**
	 * Creates the popup menu that the user will see when they right-click on a
	 * component
	 */
	@Override
    JPopupMenu getPopup(final MouseEvent e)
	{
		PetriNetNode nodeType = ((PerformanceTreeNode) this.myObject).getNodeType();
		JPopupMenu popup = super.getPopup(e); // make the "Delete" option
		// available

		switch (nodeType)
		{
			case DISCON :
			{
				JMenu menuItem = new JMenu("Set Operation");

				JMenuItem subMenuItem = new JMenuItem(new EditPerformanceTreeNodeAction(this.contentPane,
																						(PerformanceTreeNode) this.myObject,
																						"or"));
				subMenuItem.setText("Or");
				menuItem.add(subMenuItem);

				subMenuItem = new JMenuItem(new EditPerformanceTreeNodeAction(	this.contentPane,
																				(PerformanceTreeNode) this.myObject,
																				"and"));
				subMenuItem.setText("And");
				menuItem.add(subMenuItem);

				if (myObject.enablePopup) popup.add(menuItem);
				break;
			}
			case ARITHOP :
			{
				JMenu menuItem = new JMenu("Set Operation");

				JMenuItem subMenuItem = new JMenuItem(new EditPerformanceTreeNodeAction(this.contentPane,
																						(PerformanceTreeNode) this.myObject,
																						"plus"));
				subMenuItem.setText("+");
				menuItem.add(subMenuItem);

				subMenuItem = new JMenuItem(new EditPerformanceTreeNodeAction(	this.contentPane,
																				(PerformanceTreeNode) this.myObject,
																				"minus"));
				subMenuItem.setText("-");
				menuItem.add(subMenuItem);

				subMenuItem = new JMenuItem(new EditPerformanceTreeNodeAction(	this.contentPane,
																				(PerformanceTreeNode) this.myObject,
																				"times"));
				subMenuItem.setText("*");
				menuItem.add(subMenuItem);

				subMenuItem = new JMenuItem(new EditPerformanceTreeNodeAction(	this.contentPane,
																				(PerformanceTreeNode) this.myObject,
																				"div"));
				subMenuItem.setText("/");
				menuItem.add(subMenuItem);

				subMenuItem = new JMenuItem(new EditPerformanceTreeNodeAction(	this.contentPane,
																				(PerformanceTreeNode) this.myObject,
																				"power"));
				subMenuItem.setText("^");
				menuItem.add(subMenuItem);

				if (myObject.enablePopup) popup.add(menuItem);
				break;
			}
			case ARITHCOMP :
			{
				JMenu menuItem = new JMenu("Set Operation");

				JMenuItem subMenuItem = new JMenuItem(new EditPerformanceTreeNodeAction(this.contentPane,
																						(PerformanceTreeNode) this.myObject,
																						"lt"));
				subMenuItem.setText("<");
				menuItem.add(subMenuItem);

				subMenuItem = new JMenuItem(new EditPerformanceTreeNodeAction(	this.contentPane,
																				(PerformanceTreeNode) this.myObject,
																				"leq"));
				subMenuItem.setText("<=");
				menuItem.add(subMenuItem);

				subMenuItem = new JMenuItem(new EditPerformanceTreeNodeAction(	this.contentPane,
																				(PerformanceTreeNode) this.myObject,
																				"eq"));
				subMenuItem.setText("==");
				menuItem.add(subMenuItem);

				subMenuItem = new JMenuItem(new EditPerformanceTreeNodeAction(	this.contentPane,
																				(PerformanceTreeNode) this.myObject,
																				"geq"));
				subMenuItem.setText(">=");
				menuItem.add(subMenuItem);

				subMenuItem = new JMenuItem(new EditPerformanceTreeNodeAction(	this.contentPane,
																				(PerformanceTreeNode) this.myObject,
																				"gt"));
				subMenuItem.setText(">");
				menuItem.add(subMenuItem);

				if (myObject.enablePopup) popup.add(menuItem);
				break;
			}
			case NUM :
			{
				JMenuItem menuItem = new JMenuItem(new EditPerformanceTreeNodeAction(	this.contentPane,
																						(PerformanceTreeNode) this.myObject,
																						"Num"));
				menuItem.setText("Set Numerical Value");
				if (myObject.enablePopup){
					if (myObject.enablePopup) popup.add(menuItem);
				}
				break;
			}
			case BOOL :
			{
				JMenu menuItem = new JMenu("Set Boolean Value");
				JMenuItem subMenuItem = new JMenuItem(new EditPerformanceTreeNodeAction(this.contentPane,
																						(PerformanceTreeNode) this.myObject,
																						"true"));
				subMenuItem.setText("true");
				menuItem.add(subMenuItem);
				subMenuItem = new JMenuItem(new EditPerformanceTreeNodeAction(	this.contentPane,
																				(PerformanceTreeNode) this.myObject,
																				"false"));
				subMenuItem.setText("false");
				menuItem.add(subMenuItem);
				if (myObject.enablePopup) popup.add(menuItem);
				break;
			}
			case STATES :
			{
				JMenuItem menuItem = new JMenuItem(new EditPerformanceTreeNodeAction(	this.contentPane,
																						(PerformanceTreeNode) this.myObject,
																						"Assign States"));
				if (((StatesNode) this.myObject).getStateLabel() == null)
				{
					menuItem.setText("Assign States");
					if (myObject.enablePopup) popup.add(menuItem);
				}
				else
				{
					// a state label has already been assigned, so we might want
					// to
					// be able to modify its definition
					menuItem = new JMenuItem(new EditPerformanceTreeNodeAction(	this.contentPane,
																				(PerformanceTreeNode) this.myObject,
																				"Edit States"));
					menuItem.setText("Edit States");
					if (myObject.enablePopup) popup.add(menuItem);
				}
				break;
			}
			case ACTIONS :
			{
				JMenuItem menuItem = new JMenuItem(new EditPerformanceTreeNodeAction(	this.contentPane,
																						(PerformanceTreeNode) this.myObject,
																						"Actions"));
				menuItem.setText("Assign Actions");
				if (myObject.enablePopup) popup.add(menuItem);
				break;
			}
			case STATEFUNCTION :
			{
				JMenuItem menuItem = new JMenuItem(new EditPerformanceTreeNodeAction(	this.contentPane,
																						(PerformanceTreeNode) this.myObject,
																						"StateFunction"));
				menuItem.setText("Set State Function");
				if (myObject.enablePopup) popup.add(menuItem);
				break;
			}
			case MACRO :
				if (MacroManager.getEditor() == null)
				{
					JMenuItem menuItem = new JMenuItem(new EditPerformanceTreeNodeAction(	this.contentPane,
																							(PerformanceTreeNode) this.myObject,
																							"Assign Macro"));
					if (((MacroNode) this.myObject).getNodeLabel() == null)
					{
						menuItem.setText("Assign Macro");
						popup.add(menuItem);
					}
					break;
				}
			case ARGUMENT :
			{
				JMenuItem menuItem = new JMenuItem(new EditPerformanceTreeNodeAction(	this.contentPane,
																						(PerformanceTreeNode) this.myObject,
																						"Argument"));
				menuItem.setText("Set Argument Name");
				popup.add(menuItem);
			}
				break;
			default :
				break;

		}
		return popup;
	}
}