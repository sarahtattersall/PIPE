/**
 * PerformanceTreeArcHandler
 * 
 * Class used to implement methods corresponding to mouse events on Performance Tree arcs.
 * 
 * @author Tamas Suto
 * @date 22/04/07
 */

package pipe.modules.queryeditor.gui;

import java.awt.Container;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import pipe.gui.Grid;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeArc;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroManager;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroView;


public class PerformanceTreeArcHandler extends PerformanceTreeObjectHandler {

	public PerformanceTreeArcHandler(Container contentpane, PerformanceTreeArc obj) {
		super(contentpane, obj);
		enablePopup = true;
	}	
	
	/** 
	 * Creates the popup menu that the user will see when they right click on a component 
	 */
	public JPopupMenu getPopup(MouseEvent e) {	
		JPopupMenu popup;
		if (QueryManager.allowDeletionOfArcs)
			popup = super.getPopup(e);	
		else
			popup = new JPopupMenu();
	    JMenuItem menuItem = new JMenuItem(new SplitPerformanceTreeArcAction((PerformanceTreeArc)myObject, e.getPoint()));
	    menuItem.setText("Split Arc Segment");
	    popup.add(menuItem);
		
		return popup;
	}

	public void mousePressed(MouseEvent e) {
		super.mousePressed(e);	
		
		if (e.getClickCount() == 2) {
			((PerformanceTreeArc)myObject).getSource().select();
			if (((PerformanceTreeArc)myObject).getTarget() != null)
				((PerformanceTreeArc)myObject).getTarget().select();
			justSelected = true;
		}
	}

	public void mouseDragged(MouseEvent e) {
		int switchCondition;
		if (MacroManager.getEditor() == null)
			switchCondition = QueryManager.getMode();
		else
			switchCondition = MacroManager.getMode();
		switch (switchCondition) {
		case SELECT:
			if (!isDragging) 
				break;		
			PerformanceTreeArc currentObject = (PerformanceTreeArc)myObject;
			Point oldLocation = currentObject.getLocation();
			// Calculate translation in mouse
			int transX = Grid.getModifiedX(e.getX() - dragInit.x);
			int transY = Grid.getModifiedY(e.getY() - dragInit.y);
			if (MacroManager.getEditor() == null) 
				((QueryView)contentPane).getSelectionObject().translateSelection(transX, transY);
			else 
				((MacroView)contentPane).getSelectionObject().translateSelection(transX, transY);
			
			dragInit.translate(-(currentObject.getLocation().x - oldLocation.x - transX),
							   -(currentObject.getLocation().y - oldLocation.y - transY));
		}
	}
}

