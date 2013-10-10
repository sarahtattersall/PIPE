/**
 * SplitPerformanceTreeArcPathPointAction
 * 
 * This class is used to split a point on an arc into two to
 * allow the arc to be manipulated further.
 * 
 * @author Tamas Suto
 * @date 22/04/07
 */

package pipe.modules.queryeditor.gui;


import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeArcPathPoint;


class SplitPerformanceTreeArcPathPointAction extends AbstractAction {
	
	  private final PerformanceTreeArcPathPoint selected;
	  
	  public SplitPerformanceTreeArcPathPointAction(PerformanceTreeArcPathPoint component) {
	    selected = component;
	  }
	  
	  public void actionPerformed(ActionEvent e) {
		selected.splitPoint();
	}

}
