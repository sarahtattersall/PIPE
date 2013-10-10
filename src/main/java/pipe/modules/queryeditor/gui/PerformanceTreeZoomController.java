/**
 * PerformanceTreeZoomController
 * 
 * author Tim Kimber, Barry Kearns, Tamas Suto
 * date 05/10/07
 */

package pipe.modules.queryeditor.gui;

import java.awt.geom.AffineTransform;

import javax.swing.JLayeredPane;

import pipe.modules.queryeditor.gui.performancetrees.macros.MacroView;


public class PerformanceTreeZoomController {
	
	private int percent;
	private final AffineTransform transform = new AffineTransform();
	private final JLayeredPane queryView;
	
	
	public PerformanceTreeZoomController(QueryView view){
		this(100, view);
	}
	
	public PerformanceTreeZoomController(MacroView view){
		this(100, view);
	}

	public PerformanceTreeZoomController(int pct, QueryView view) {
		percent = pct;
		queryView = view;
	}
	
	public PerformanceTreeZoomController(int pct, MacroView view) {
		percent = pct;
		queryView = view;
	}

	
	public void zoomOut() {
		percent -= 10;
	
		if(percent<40){
			percent += 10;
			return;
		}
		transform.setToScale(percent * 0.01,percent * 0.01);
	}
	
	public void zoomIn(){
		percent += 10;
		transform.setToScale(percent * 0.01,percent * 0.01);		
	}

	public AffineTransform getTransform() {
		return transform;
	}

	public int getPercent() {
		return percent;
	}
	
	void setPercent(int newPercent) {
		if(newPercent>=40 && newPercent<=2000)
			percent=newPercent;
	}

	/**
	 * Calculates the value of the screen distance val at 100% zoom
	 * 
	 * @param val
	 * @return
	 */
	public int getUnzoomedValue(double val) {
		return (int)(val / (percent * 0.01));
	}

	/**
	 * Calculates where the correct screen x position at the current zoom is
	 * for an object with "real" x value locationX.
	 * 
	 * @param locationX
	 * @return
	 */
	public double getZoomPositionForXLocation(double locationX) {
		return locationX * percent * 0.01;
	}

	/**
	 * Calculates where the correct screen y position at the current zoom is
	 * for an object with "real" y value locationY.
	 * 
	 * @param locationY
	 * @return
	 */
	public double getZoomPositionForYLocation(double locationY) {
		return locationY * percent * 0.01;
	}

	public void setZoom(int newPercent) {
		setPercent(newPercent);
		transform.setToScale(percent * 0.01,percent * 0.01);
	}
	
}
