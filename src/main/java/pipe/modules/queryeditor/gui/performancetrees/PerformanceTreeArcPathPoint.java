/**
 * PerformanceTreeArcPath
 * 
 * @author Tamas Suto
 * @date 21/04/07
 */

package pipe.modules.queryeditor.gui.performancetrees;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

import pipe.modules.interfaces.QueryConstants;
import pipe.gui.Zoomable;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroManager;


public class PerformanceTreeArcPathPoint extends PerformanceTreeObject implements Cloneable, QueryConstants, Zoomable {
	
	public static final boolean STRAIGHT = false;
	public static final boolean CURVED = true;
	private static final int SIZE = 4;
	private static final int SIZE_OFFSET = 4;
    private RectangularShape shape = new Rectangle2D.Double(0,0,2*SIZE,2*SIZE);
    private PerformanceTreeArcPath myArcPath;
	private Point2D.Float point  = new Point2D.Float();
	private Point2D.Float realPoint  = new Point2D.Float();
	private Point2D.Float control1 = new Point2D.Float();
	private Point2D.Float control2 = new Point2D.Float();
	private boolean pointType;

	
	PerformanceTreeArcPathPoint(Point2D.Float point, boolean _pointType, PerformanceTreeArcPath a){
		this(point.x, point.y, _pointType, a);
	}
	
	PerformanceTreeArcPathPoint(float x, float y, boolean _pointType, PerformanceTreeArcPath a) {
		this(a);
		setPointLocation(x,y);
		pointType = _pointType;
	}
	
	public PerformanceTreeArcPathPoint(float x, float y, boolean _pointType) {
		setPointLocation(x,y);
		pointType = _pointType;
	}
	
	PerformanceTreeArcPathPoint(PerformanceTreeArcPath a) {	
		myArcPath = a;
		if (MacroManager.getEditor() == null) {
			if (QueryManager.getEditor()!=null) 
				addZoomController(QueryManager.getView().getZoomController());
		}
		else {
			addZoomController(MacroManager.getView().getZoomController());
		}
		setPointLocation(0,0);	
	}
	

	public Point2D.Float getPoint() {
		return point;
	}

	public void setPointLocation(float x, float y) {
		double realX, realY;
		if (MacroManager.getEditor() == null) {
			if (QueryManager.getEditor()!=null){
				realX = getZoomController().getUnzoomedValue(x);
				realY = getZoomController().getUnzoomedValue(y);
				realPoint.setLocation(realX,realY);
			}
		}
		else {
			realX = getZoomController().getUnzoomedValue(x);
			realY = getZoomController().getUnzoomedValue(y);
			realPoint.setLocation(realX,realY);
		}
		point.setLocation(x,y);
		setBounds((int)x - SIZE, (int)y - SIZE, 2*SIZE + SIZE_OFFSET, 2*SIZE + SIZE_OFFSET);
	}

	public boolean getPointType() {
		return pointType;
	}	

	public void updatePointLocation() {
		setPointLocation(point.x, point.y);
	}

	public void setPointType(boolean type) {
		if (pointType != type) {
			pointType = type;
			myArcPath.createPath();
			myArcPath.getArc().updateArcPosition();
		}
	}

	public void togglePointType() {
		pointType = !pointType;
		myArcPath.createPath();
		myArcPath.getArc().updateArcPosition();
	}

	public void setVisibilityLock(boolean lock) {
		myArcPath.setPointVisibilityLock(lock);
	}

	public double getAngle(PerformanceTreeArcPathPoint p2) {
		double angle;
		if (point.y <= p2.point.y)
			angle = Math.atan((point.x - p2.point.x) / (p2.point.y - point.y));
		else
			angle = Math.atan((point.x - p2.point.x) / (p2.point.y - point.y))+Math.PI;
		
		// Needed to eliminate an exception on Windows
		if (point.equals(p2.point))
			angle = 0;
		
		return angle;
	}

	public double getAngle(Point2D.Float p2) {
		double angle;
		if (point.y <= p2.y)
			angle = Math.atan((point.x - p2.x) / (p2.y - point.y));
		else
			angle = Math.atan((point.x - p2.x) / (p2.y - point.y))+Math.PI;
		
		// Needed to eliminate an exception on Windows
		if (point.equals(p2))
			angle = 0;
		
		return angle;
	}

	public void paintComponent(Graphics g) {
		if (!ignoreSelection) {	
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			if (pointType)
				shape = new Ellipse2D.Double(0,0,2*SIZE,2*SIZE);
			else 
				shape = new Rectangle2D.Double(0,0,2*SIZE,2*SIZE);

			if (selected) {
				g2.setPaint(SELECTION_FILL_COLOUR);
				g2.fill(shape);
				g2.setPaint(SELECTION_LINE_COLOUR);
				g2.draw(shape);
			} 
			else {
				g2.setPaint(ELEMENT_FILL_COLOUR);
				g2.fill(shape);
				g2.setPaint(ELEMENT_LINE_COLOUR);
				g2.draw(shape);
			}
		}
	}

	public int getIndex() { 
		for(int i=0;i<myArcPath.getNumPoints();i++) 
			if(myArcPath.getPathPoint(i)==this) 
				return i;
		return -1;    
	}
  
	/**
	 * This method is called when the user selects the popup menu
	 * option Split Point on an PerformanceTreeArc Point.
	 * The method determines the index of the selected point in the
	 * listarray of ArcPathPoints that an arcpath has. Then then a 
	 * new point is created BEFORE this one in the list and offset
	 * by a small delta in the x direction.
	 *
	 */
	public void splitPoint(){
		int i = getIndex(); // Get the index of this point
        int DELTA = 10;
        PerformanceTreeArcPathPoint newpoint = new PerformanceTreeArcPathPoint(point.x + DELTA, point.y, pointType, myArcPath);
		myArcPath.insertPoint(i+1, newpoint);
		myArcPath.createPath();
		myArcPath.getArc().updateArcPosition();
	}

	public Point2D.Float getMidPoint(PerformanceTreeArcPathPoint target){
		return new Point2D.Float((target.point.x + point.x)/2, (target.point.y + point.y)/2);
	}

	public boolean isDeleteable() {
		int i=getIndex();
		return(i>0 && i!=myArcPath.getNumPoints()-1);    
	}

	public void delete() {
		// Won't delete if only two points left. General delete.
		if(isDeleteable()) {
			kill();
			myArcPath.updateArc();
		}
	}

	public void kill() {
		// delete without the safety check :)
		// called internally by ArcPoint and parent PerformanceTreeArcPath
		super.removeFromContainer();
		myArcPath.deletePoint(this);		
	}

	public Point2D.Float getControl1() {
		return control1;
	}

	public Point2D.Float getControl2() {
		return control2;
	}

	public void setControl1(float _x, float _y) {
		control1.x = _x;
		control1.y = _y;
	}

	public void setControl2(float _x, float _y) {
		control2.x = _x;
		control2.y = _y;
	}

	public void setControl1(Point2D.Float p) {
		control1.x = p.x;
		control1.y = p.y;
	}

	public void setControl2(Point2D.Float p) {
		control2.x = p.x;
		control2.y = p.y;
	}

	public PerformanceTreeArcPath getArcPath() {
		return myArcPath;
	}

	public void zoomUpdate() {
		if (getZoomController() != null){
			float x = (float)(getZoomController().getZoomPositionForXLocation(realPoint.x));
			float y = (float)(getZoomController().getZoomPositionForYLocation(realPoint.y));
			point.setLocation(x,y);
			setBounds((int)x - SIZE, (int)y - SIZE, 2*SIZE + SIZE_OFFSET, 2*SIZE + SIZE_OFFSET);
		}

	}
	
	public void updateBounds() {
		
	}
	
	public PerformanceTreeArcPathPoint clone(PerformanceTreeArcPath parentPath) {
		PerformanceTreeArcPathPoint clonedArcPathPoint = (PerformanceTreeArcPathPoint)super.clone();
		clonedArcPathPoint.myArcPath = parentPath;
		clonedArcPathPoint.shape = (RectangularShape)shape.clone();
		clonedArcPathPoint.point = (Point2D.Float)point.clone();
		clonedArcPathPoint.realPoint = (Point2D.Float)realPoint.clone();
		clonedArcPathPoint.control1 = (Point2D.Float)control1.clone();
		clonedArcPathPoint.control2 = (Point2D.Float)control2.clone();
		clonedArcPathPoint.pointType = pointType;
		return clonedArcPathPoint;
	}
	
}