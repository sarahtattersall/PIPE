/**
 * PerformanceTreeArcLabel
 * 
 * @author Tamas Suto
 * @date 16/05/07
 */

package pipe.modules.queryeditor.gui.performancetrees;


import javax.swing.JLabel;

import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroManager;


public class PerformanceTreeObjectLabel extends JLabel implements Cloneable {

	private String id = null;
	private int positionX;
	private int positionY;
	private double xCoord;
	private double yCoord;
	public int arcboundsLeft;
	public int arcboundsTop;
	private boolean required;

	
	public PerformanceTreeObjectLabel(double positionXInput, double positionYInput, String text, String idInput) {
		super(text);
		id = idInput;
		xCoord = positionXInput;
		yCoord = positionYInput;
		required = true;
	}
	
	public PerformanceTreeObjectLabel(String text){
		super(text);
		setHorizontalAlignment(LEFT);
		setVerticalAlignment(TOP);
	}
	
	public PerformanceTreeObjectLabel(){
		super();
	}
	
	
	public String getID() {
		return id;
	}
	
	public void setID(String idInput) {
		id = idInput;
	}
	
	public double getXPosition() {
		return xCoord;
	}

	public double getYPosition() {
		return yCoord;
	}

	public void setPosition(int x, int y) {
		positionX = x;
		positionY = y;
		updatePosition();
	}
	
	void updatePosition() {
		setLocation(positionX-getPreferredSize().width, positionY-getPreferredSize().height);
	}

	public void updateSize() {
		// To get round Java bug #4352983, the size needs to be expanded a bit
		setSize((int)(getPreferredSize().width*1.2),(int)(getPreferredSize().height*1.2));
		updatePosition();
	}

	public void translate(int x, int y) {
		setPosition(positionX+x, positionY+y);
	}
	
	public boolean getRequired() {
		return required;
	}
	
	public void setRequired(boolean req) {
		required = req;
	}
	
	public void delete() {
		if (MacroManager.getEditor() == null) {
			QueryManager.getView().remove(this);
		}
		else
			MacroManager.getView().remove(this);
	}
	
	public PerformanceTreeObjectLabel clone() {
		try {
			return (PerformanceTreeObjectLabel)super.clone();
		} catch (CloneNotSupportedException e){
			e.printStackTrace();	}
		return null;
	}

}

