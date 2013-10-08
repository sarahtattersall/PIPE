/**
 * SplitPerformanceTreeArcAction
 * 
 * @author Tamas Suto
 * @date 22/04/07
 */

package pipe.modules.queryeditor.gui;


import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;

import javax.swing.AbstractAction;

import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeArc;


class SplitPerformanceTreeArcAction extends AbstractAction {
	
	private final PerformanceTreeArc selected;
	private final Point2D.Float mouseposition;
	
	public SplitPerformanceTreeArcAction(PerformanceTreeArc arc, Point mousepos) {
		selected = arc;

		// Mousepos is relative to selected component i.e. the arc
		// Need to convert this into actual coordinates
		Point2D.Float offset = new Point2D.Float(selected.getX(), selected.getY());
		mouseposition = new Point2D.Float(mousepos.x+offset.x, mousepos.y+offset.y);
	}

	public void actionPerformed(ActionEvent arg0) {
		selected.split(mouseposition);
	}

}
