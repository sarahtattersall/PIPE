/**
 * PerformanceTreeArcPathPointHandler
 * 
 * This class implements the methods for the manipulation of ArcPathPoints
 * 
 * @author Tamas Suto
 * @date 22/04/07
 */

package pipe.modules.queryeditor.gui;


import java.awt.Container;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeArcPathPoint;


public class PerformanceTreeArcPathPointHandler extends PerformanceTreeObjectHandler {

	public PerformanceTreeArcPathPointHandler(Container contentpane, PerformanceTreeArcPathPoint obj) {
		super(contentpane, obj);
		enablePopup = true;
	}	

	/** 
	 * Creates the popup menu that the user will see when they right click on a component 
	 */
	public JPopupMenu getPopup(MouseEvent e) {
		JPopupMenu popup;
		if(((PerformanceTreeArcPathPoint)myObject).isDeleteable()) 
			popup = super.getPopup(e);
		else 
			popup = new JPopupMenu();

		if(((PerformanceTreeArcPathPoint)myObject).getIndex() == 0) 
			return null;
		else {
			JMenuItem menuItem = new JMenuItem(new TogglePerformanceTreeArcPathPointAction((PerformanceTreeArcPathPoint)myObject));
			if (!((PerformanceTreeArcPathPoint) myObject).getPointType())
				menuItem.setText("Change to Curved");
			else
				menuItem.setText("Change to Straight");
			popup.add(menuItem);

			menuItem = new JMenuItem(new SplitPerformanceTreeArcPathPointAction((PerformanceTreeArcPathPoint)myObject));
			menuItem.setText("Split Point");
			popup.add(menuItem);
		}
		return popup;
	}

	public void mousePressed(MouseEvent e) {
		if (myObject.isEnabled()) {
			((PerformanceTreeArcPathPoint)e.getComponent()).setVisibilityLock(true);
			super.mousePressed(e);
		}
	}

	public void mouseDragged(MouseEvent e) {
		super.mouseDragged(e);	
	}

	public void mouseReleased(MouseEvent e) {
		((PerformanceTreeArcPathPoint)e.getComponent()).setVisibilityLock(false);
		super.mouseReleased(e);
	}
	
}
