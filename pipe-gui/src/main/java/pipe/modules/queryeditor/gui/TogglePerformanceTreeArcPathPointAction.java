/**
 * TogglePerformanceTreeArcPathPointAction
 * 
 * @author Tamas Suto
 * @date 22/04/07
 */

package pipe.modules.queryeditor.gui;

import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeArcPathPoint;

import javax.swing.*;
import java.awt.event.ActionEvent;


class TogglePerformanceTreeArcPathPointAction extends AbstractAction {

	private final PerformanceTreeArcPathPoint selected;


	public TogglePerformanceTreeArcPathPointAction(PerformanceTreeArcPathPoint component) {
		selected = component;
	}		


	public void actionPerformed(ActionEvent e) {
		selected.togglePointType();		
	}

}
