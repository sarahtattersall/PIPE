/**
 * ViewExpansionComponent
 * 
 * @author Tamas Suto
 * @date 22/04/07
 */

package pipe.modules.queryeditor.gui;

import pipe.gui.Zoomable;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeObject;


public class ViewExpansionComponent extends PerformanceTreeObject implements Zoomable {
	
	private int originalX = 0;
	private int originalY = 0;

	
	private ViewExpansionComponent() {
		super();
		setSize(1,1);
	}
	
	public ViewExpansionComponent(int x, int y){
		this();
		setOriginalX(x);
		setOriginalY(y);
		setLocation(x,y);
	}
	
	
	void setOriginalX(int x) {
		this.originalX = x;
	}

	void setOriginalY(int y) {
		this.originalY = y;
	}
	
	public void zoomUpdate() {
		double scaleFactor = getZoomController().getPercent() * 0.01;
		setLocation((int)(originalX * scaleFactor),(int)(originalY * scaleFactor));
	}

}
