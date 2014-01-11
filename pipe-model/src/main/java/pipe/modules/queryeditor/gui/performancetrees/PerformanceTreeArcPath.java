/**
 * PerformanceTreeArcPath
 * 
 * @author Tamas Suto
 * @date 20/04/07
 */

package pipe.modules.queryeditor.gui.performancetrees;

import pipe.modules.interfaces.QueryConstants;
import pipe.modules.queryeditor.gui.PerformanceTreeArcPathPointHandler;
import pipe.modules.queryeditor.gui.QueryView;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroManager;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroView;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class PerformanceTreeArcPath implements Shape, Cloneable, QueryConstants {
	
	private PerformanceTreeArc myArc;
	private GeneralPath path = new GeneralPath();
	private GeneralPath arcSelection;
	private GeneralPath pointSelection;
	private List pathPoints = new ArrayList();

	private PerformanceTreeArcPathPoint currentPoint;
	private boolean pointLock = false;
	private static Stroke proximityStroke = new BasicStroke(ARC_PATH_PROXIMITY_WIDTH/*, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER*/);
	private static Stroke stroke = new BasicStroke(ARC_PATH_SELECTION_WIDTH);
	private Shape shape, proximityShape;
	private int nodeAngle;
	private boolean showControlPoints = false;
	
	
	public PerformanceTreeArcPath(PerformanceTreeArc a) {
		myArc = a;
		nodeAngle = 0;
	}
	
	
	public GeneralPath getPath() {
		return path;
	}
	
	public GeneralPath getArcSelection() {
		return arcSelection;
	}
	
	public GeneralPath getPointSelection() {
		return pointSelection;
	}
	
	public PerformanceTreeArc getArc() {
		return myArc;
	}
	
	public PerformanceTreeArcPathPoint getPathPoint(int index) {
		return ((PerformanceTreeArcPathPoint)pathPoints.get(index));
	}
	
	public void createPath() {
		setControlPoints();	
		currentPoint = null;
		path = new GeneralPath();
		currentPoint = (PerformanceTreeArcPathPoint)pathPoints.get(0);
		path.moveTo(currentPoint.getPoint().x, currentPoint.getPoint().y);
		currentPoint.setPointType(PerformanceTreeArcPathPoint.STRAIGHT);
		for (int c = 1; c <= getEndIndex(); c++) {
			currentPoint = (PerformanceTreeArcPathPoint)pathPoints.get(c);
			if (!currentPoint.getPointType()){
				path.lineTo(currentPoint.getPoint().x, currentPoint.getPoint().y); 
			}
			else if (currentPoint.getPointType()){
				if (showControlPoints){//draw control lines for illustrative purposes
					path.lineTo(currentPoint.getControl1().x,currentPoint.getControl1().y);
					path.lineTo(currentPoint.getControl2().x,currentPoint.getControl2().y);
					path.lineTo(currentPoint.getPoint().x, currentPoint.getPoint().y);
					path.moveTo(((PerformanceTreeArcPathPoint)pathPoints.get(c-1)).getPoint().x, ((PerformanceTreeArcPathPoint)pathPoints.get(c-1)).getPoint().y);
				}
				path.curveTo(currentPoint.getControl1().x,currentPoint.getControl1().y,currentPoint.getControl2().x,currentPoint.getControl2().y,currentPoint.getPoint().x,currentPoint.getPoint().y);
			}
		}
		shape = stroke.createStrokedShape(this);
		proximityShape = proximityStroke.createStrokedShape(this);
	}
	
	public Point2D.Float getStartPoint() {
		return ((PerformanceTreeArcPathPoint)pathPoints.get(0)).getPoint();
	}
	
	public Point2D.Float getEndPoint() {
		return ((PerformanceTreeArcPathPoint)pathPoints.get(getEndIndex())).getPoint();
	}
	
	/** Return a control point for curve CD with incoming vector AB
     * @param A
     * @param B
     * @param C
     * @param D
     * @return*/
	private Point2D.Float getControlPoint(Point2D.Float A, Point2D.Float B, Point2D.Float C, Point2D.Float D) {
		Point2D.Float p = new Point2D.Float(0, 0);	
		double modAB = getMod(A, B);
		double modCD = getMod(C, D);		
		double ABx = (B.x - A.x) / modAB; 
		double ABy = (B.y - A.y) / modAB;
		
		if (modAB < 7) { 	
			// hack that stops division by zero. modAB can only be this low
			// if the points are virtually superimposed.
			p = (Point2D.Float)C.clone();
		}
		else{
			p.x = C.x + (float) (ABx * modCD / ARC_CONTROL_POINT_CONSTANT );
			p.y = C.y + (float) (ABy * modCD / ARC_CONTROL_POINT_CONSTANT );
		}
		return p;
	}	
	
	private void setControlPoints() {
        // must be in this order
		setCurveControlPoints(); 
		setStraightControlPoints();
		setEndControlPoints();
	}
	
	/** Set control points for any curved sections of the path */
	private void setCurveControlPoints(){
		if(pathPoints.size()<1) 
			return;
		PerformanceTreeArcPathPoint myCurrentPoint = (PerformanceTreeArcPathPoint)pathPoints.get(0);		
		myCurrentPoint.setPointType(PerformanceTreeArcPathPoint.STRAIGHT);	
		Cubic[] X,Y;
		int endIndex = getEndIndex();

		for (int c=1; c <= endIndex;){
			int curveStartIndex = 0;
			int curveEndIndex = 0;
			myCurrentPoint = (PerformanceTreeArcPathPoint)pathPoints.get(c);

			if (myCurrentPoint.getPointType()){
				curveStartIndex = c-1;
				for(; c<= endIndex && myCurrentPoint.getPointType(); c++){
					myCurrentPoint = (PerformanceTreeArcPathPoint)pathPoints.get(c);
					curveEndIndex = c;
				}
				/* calculate a cubic for each section of the curve */
				int lengthOfCurve = curveEndIndex - curveStartIndex;
				int k1;
				int x[] = new int[lengthOfCurve + 2];
				int y[] = new int[lengthOfCurve + 2];
				X = new Cubic[lengthOfCurve + 2];
				Y = new Cubic[lengthOfCurve + 2];
				for (k1= 0; k1 <= (curveEndIndex - curveStartIndex); k1++) {
					x[k1] = (int)((PerformanceTreeArcPathPoint)pathPoints.get(curveStartIndex + k1)).getPoint().x;
					y[k1] = (int)((PerformanceTreeArcPathPoint)pathPoints.get(curveStartIndex + k1)).getPoint().y;
				}
				x[k1] = x[k1-1];
				y[k1] = y[k1-1];
				X = calcNaturalCubic(k1,x);
				Y = calcNaturalCubic(k1,y);
				for (int k2 = 1; k2 <= lengthOfCurve; k2++){
					myCurrentPoint = (PerformanceTreeArcPathPoint)pathPoints.get(k2 + curveStartIndex);
					myCurrentPoint.setControl1(X[k2-1].getX1(),Y[k2-1].getX1());
					myCurrentPoint.setControl2(X[k2-1].getX2(),Y[k2-1].getX2());
				}
			}
			else {
				c++;
			}
		}			
	}
	
	/**
	 * Set the control points for any straight sections and for smooth 
	 * intersection between straight and curved sections 
	 */
	private void setStraightControlPoints() {
		PerformanceTreeArcPathPoint myCurrentPoint = (PerformanceTreeArcPathPoint)pathPoints.get(0);		
		PerformanceTreeArcPathPoint myPreviousButOnePoint = null;
		PerformanceTreeArcPathPoint myNextPoint = null;
		PerformanceTreeArcPathPoint myPreviousPoint = null;

		for (int c=1; c<=getEndIndex(); c++){
			myPreviousPoint = (PerformanceTreeArcPathPoint)pathPoints.get(c-1);
			myCurrentPoint = (PerformanceTreeArcPathPoint)pathPoints.get(c);	
			if (!myCurrentPoint.getPointType()){
				myCurrentPoint.setControl1(getControlPoint(myPreviousPoint.getPoint(),
						myCurrentPoint.getPoint(),
						myPreviousPoint.getPoint(),
						myCurrentPoint.getPoint()));
				myCurrentPoint.setControl2(getControlPoint(myCurrentPoint.getPoint(),
						myPreviousPoint.getPoint(),
						myCurrentPoint.getPoint(),
						myPreviousPoint.getPoint()));
			}
			else {
				if (c>1 && !myPreviousPoint.getPointType()){
					myPreviousButOnePoint = (PerformanceTreeArcPathPoint)pathPoints.get(c-2);
					myCurrentPoint.setControl1(getControlPoint(myPreviousButOnePoint.getPoint(),
							myPreviousPoint.getPoint(),
							myPreviousPoint.getPoint(),
							myCurrentPoint.getPoint()));
				}
				if (c<getEndIndex()){
					myNextPoint = (PerformanceTreeArcPathPoint)pathPoints.get(c+1);
					if (!myNextPoint.getPointType()){
						myCurrentPoint.setControl2(getControlPoint(myNextPoint.getPoint(),
								myCurrentPoint.getPoint(),
								myCurrentPoint.getPoint(),
								myPreviousPoint.getPoint()));
					}
				}
			}
		}
	}
		
	private void setEndControlPoints() {
		PerformanceTreeObject source = getArc().getSource();
		PerformanceTreeObject target = getArc().getTarget();
		double anAngle = Math.toRadians(nodeAngle);
		
		if  (!(getEndIndex() > 0))
        {
        }
		else if (source != null && source instanceof PerformanceTreeNode &&
                ((PerformanceTreeArcPathPoint) pathPoints.get(1)).getPointType()) {

			PerformanceTreeArcPathPoint myPoint = (PerformanceTreeArcPathPoint)pathPoints.get(1);
			PerformanceTreeArcPathPoint myLastPoint = (PerformanceTreeArcPathPoint)pathPoints.get(0);
			float distance = (float)getMod(myPoint.getPoint(), myLastPoint.getPoint())/ARC_CONTROL_POINT_CONSTANT;
			myPoint.setControl1((float)(myLastPoint.getPoint().x + Math.cos(anAngle)*distance),
								(float)(myLastPoint.getPoint().y + Math.sin(anAngle)*distance) );
			myPoint = (PerformanceTreeArcPathPoint)pathPoints.get(getEndIndex());
			myPoint.setControl2(getControlPoint(myPoint.getPoint(),
					myPoint.getControl1(),
					myPoint.getPoint(),
					myPoint.getControl1()));
		}
		else if (target != null && source instanceof PerformanceTreeNode &&
                ((PerformanceTreeArcPathPoint) pathPoints.get(getEndIndex())).getPointType()){

			PerformanceTreeArcPathPoint myPoint = (PerformanceTreeArcPathPoint)pathPoints.get(getEndIndex());
			PerformanceTreeArcPathPoint myLastPoint = (PerformanceTreeArcPathPoint)pathPoints.get(getEndIndex()-1);
			float distance = (float)getMod(myPoint.getPoint(), myLastPoint.getPoint())/ARC_CONTROL_POINT_CONSTANT;
			myPoint.setControl2((float)(myPoint.getPoint().x + Math.cos(anAngle)*distance),
					(float)(myPoint.getPoint().y + Math.sin(anAngle)*distance) );
			myPoint = (PerformanceTreeArcPathPoint)pathPoints.get(1);
			myPoint.setControl1(getControlPoint(((PerformanceTreeArcPathPoint)pathPoints.get(0)).getPoint(),
					myPoint.getControl2(),
					((PerformanceTreeArcPathPoint)pathPoints.get(0)).getPoint(),
					myPoint.getControl2()));		
		}
	}
		
	public void addPoint(float x, float y, boolean type) {
		pathPoints.add(new PerformanceTreeArcPathPoint(x, y, type, this));		
	}

	public void addPoint(double x, double y, boolean type) {
		pathPoints.add(new PerformanceTreeArcPathPoint((float)x, (float)y, type, this));		
	}
	public void addPoint() {
		pathPoints.add(new PerformanceTreeArcPathPoint(this));
	}
	
	public void deletePoint(PerformanceTreeArcPathPoint a) {
		pathPoints.remove(a);
	}
	
	public void updateArc() {
		myArc.updateArcPosition();
	}
	
	public void translatePoints(float displacementX, float displacementY) {
		for (int i=0; i < pathPoints.size(); i++) {
			translatePoint(i, displacementX, displacementY);	
		}
	}
	
	public void translatePoint(int index, float x, float y) {
		PerformanceTreeArcPathPoint point = (PerformanceTreeArcPathPoint)pathPoints.get(index);
		point.setPointLocation(point.getPoint().x+x,point.getPoint().y+y);
	}
	
	public boolean contains(double arg0, double arg1) {
		return false;
	}
	
	public int getEndIndex() {
		return pathPoints.size()-1;
	}

	public void setPointLocation(int index, double x, double y) {
    if(index<pathPoints.size() && index>=0)
		((PerformanceTreeArcPathPoint)pathPoints.get(index)).setPointLocation((float)x,(float)y);
	}
	
	public void setPointLocation(int index, Point2D.Double point) {
		((PerformanceTreeArcPathPoint)pathPoints.get(index)).setPointLocation((float)point.x,(float)point.y);
	}
	public void setPointType(int index, boolean type) {
		((PerformanceTreeArcPathPoint)pathPoints.get(index)).setPointType(type);
	}
	
	public void setFinalPointType(boolean type) {
		((PerformanceTreeArcPathPoint)pathPoints.get(getEndIndex())).setPointType(type);
	}
	
	public void togglePointType(int index) {
		((PerformanceTreeArcPathPoint)pathPoints.get(index)).togglePointType();
	}
	
	public boolean isPointSelected(int index) {
		return ((PerformanceTreeArcPathPoint)pathPoints.get(index)).isSelected();
	}
	
	public void selectPoint(int index) {
		((PerformanceTreeArcPathPoint)pathPoints.get(index)).select();
	}
	
	public void deselectPoint(int index) {
		((PerformanceTreeArcPathPoint)pathPoints.get(index)).deselect();
	}
	
	public int getNumPoints() {
		return pathPoints.size();
	}
		
	public Point2D.Float getPoint(int index) {
		return ((PerformanceTreeArcPathPoint)pathPoints.get(index)).getPoint();
	}
	
	public void showPoints() {
		if (!pointLock) {
            for(Object pathPoint : pathPoints) ((PerformanceTreeArcPathPoint) pathPoint).setVisible(true);
		}
	}
	
	public void hidePoints() {
		if (!pointLock) {
            for(Object pathPoint : pathPoints)
            {
                currentPoint = ((PerformanceTreeArcPathPoint) pathPoint);
                if(!currentPoint.isSelected())
                    currentPoint.setVisible(false);
            }
		}
	}
	
	public void setPointVisibilityLock(boolean lock) {
		pointLock = lock;
	}	
	
	/**
	 * Modified to use control points, ensures a curve hits a place tangentially
     * @return
     */
	public double getEndAngle() {
		if (getEndIndex()>0){
			if (getArc().getTarget() instanceof PerformanceTreeNode)
				return ((PerformanceTreeArcPathPoint)pathPoints.get(getEndIndex())).getAngle(((PerformanceTreeArcPathPoint)(pathPoints.get(getEndIndex()))).getControl2());
			else
				return ((PerformanceTreeArcPathPoint)pathPoints.get(getEndIndex())).getAngle(((PerformanceTreeArcPathPoint)(pathPoints.get(getEndIndex()))).getControl1());
		}
		return 0;
	}
	
	public double getStartAngle() {
		if (getEndIndex()>0)
			return ((PerformanceTreeArcPathPoint)pathPoints.get(0)).getAngle(((PerformanceTreeArcPathPoint)(pathPoints.get(1))).getControl2());
		return 0;
	}

	public boolean contains(double arg0, double arg1, double arg2, double arg3) {
		return false;
	}
	
	public boolean contains(Point2D p) {
		return shape.contains(p);
	} 
	
	public boolean contains(Rectangle2D arg0) {
		return false;
	}
	
	public boolean proximityContains(Point2D p) {
		return proximityShape.contains(p);
	} 

	public boolean intersects(double arg0, double arg1, double arg2, double arg3) {
		return false;
	}
	
	public boolean intersects(Rectangle2D r) {
		return shape.intersects(r);
	}
	
	public boolean proximityIntersects(Rectangle2D r) {
		return proximityShape.intersects(r);
	}

	public Rectangle getBounds() {
		return path.getBounds();
	}

	public Rectangle2D getBounds2D() {
		return null;
	}

	public PathIterator getPathIterator(AffineTransform arg0) {
		return path.getPathIterator(arg0);
	}

	public PathIterator getPathIterator(AffineTransform arg0, double arg1) {
		return path.getPathIterator(arg0, arg1);
	}

	private Cubic[] calcNaturalCubic(int n, int[] x) {
		float[] gamma = new float[n+1];
		float[] delta = new float[n+1];
		float[] D = new float[n+1];
		int i;
		/* We solve the equation
		 [2 1       ] [D[0]]   [3(x[1] - x[0])  ]
		 |1 4 1     | |D[1]|   |3(x[2] - x[0])  |
		 |  1 4 1   | | .  | = |      .         |
		 |    ..... | | .  |   |      .         |
		 |     1 4 1| | .  |   |3(x[n] - x[n-2])|
		 [       1 2] [D[n]]   [3(x[n] - x[n-1])]
		 
		 by using row operations to convert the matrix to upper triangular
		 and then back sustitution.  The D[i] are the derivatives at the knots.
		 */
		
		gamma[0] = 1.0f/2.0f;
		for ( i = 1; i < n; i++) {
			gamma[i] = 1/(4-gamma[i-1]);
		}
		gamma[n] = 1/(2-gamma[n-1]);
		
		delta[0] = 3*(x[1]-x[0])*gamma[0];
		for ( i = 1; i < n; i++) {
			delta[i] = (3*(x[i+1]-x[i-1])-delta[i-1])*gamma[i];
		}
		delta[n] = (3*(x[n]-x[n-1])-delta[n-1])*gamma[n];
		
		D[n] = delta[n];
		for ( i = n-1; i >= 0; i--) {
			D[i] = delta[i] - gamma[i]*D[i+1];
		}
		
		/* now compute the coefficients of the cubics */
		Cubic[] C = new Cubic[n];
		for ( i = 0; i < n; i++) {
			C[i] = new Cubic(x[i], D[i], 3*(x[i+1] - x[i]) - 2*D[i] - D[i+1],
					2*(x[i] - x[i+1]) + D[i] + D[i+1]);
		}
		return C;
	}
		
	public void addPointsToGui(JLayeredPane editWindow) {
		PerformanceTreeArcPathPoint pathPoint;
		// set start point to not draggable
		((PerformanceTreeArcPathPoint)pathPoints.get(0)).setDraggable(false);
		// set end point to draggable
		((PerformanceTreeArcPathPoint)pathPoints.get(pathPoints.size()-1)).setDraggable(true);
		PerformanceTreeArcPathPointHandler pointHandler;

        for(Object pathPoint1 : pathPoints)
        {
            pathPoint = (PerformanceTreeArcPathPoint) pathPoint1;
            pathPoint.setVisible(false);

            // Check whether the point has already been added to the gui
            // as addPointsToGui() may have been called after the user
            // split an existing point. If this is the case, we don't want
            // to add all the points again along with new action listeners,
            // we just want to add the new point.
            // Nadeem 21/06/2005
            if(MacroManager.getEditor() == null)
            {
                // need to make this distinction, otherwise it will be very difficult to
                // select endpoints on the canvas
                if(editWindow.getIndexOf(pathPoint) < 0)
                {
                    ((QueryView) editWindow).add(pathPoint);
                    pointHandler = new PerformanceTreeArcPathPointHandler(editWindow, pathPoint);
                    pathPoint.addMouseListener(pointHandler);
                    pathPoint.addMouseMotionListener(pointHandler);
                    pathPoint.updatePointLocation();
                }
            }
            else
            {
                if(editWindow.getIndexOf(pathPoint) < 0)
                {
                    ((MacroView) editWindow).add(pathPoint);
                    pointHandler = new PerformanceTreeArcPathPointHandler(editWindow, pathPoint);
                    pathPoint.addMouseListener(pointHandler);
                    pathPoint.addMouseMotionListener(pointHandler);
                    pathPoint.updatePointLocation();
                }
            }
        }
	}
	
	public void delete() {						
		// Tells the arc points to remove themselves
		while (!pathPoints.isEmpty()){
			// force delete of ALL points
			((PerformanceTreeArcPathPoint)pathPoints.get(0)).kill();	
		}
	  }
	
	public String[][] getArcPathDetails() {
		int length = getEndIndex() + 1;
		String[][] details = new String[length][3];
		for (int c = 0; c < length; c++) {
			details[c][0] = String.valueOf(((PerformanceTreeArcPathPoint)pathPoints.get(c)).getX());
			details[c][1] = String.valueOf(((PerformanceTreeArcPathPoint)pathPoints.get(c)).getY());
			details[c][2] = String.valueOf(((PerformanceTreeArcPathPoint)pathPoints.get(c)).getPointType());
		}
		return details;
	}
	
	public void purgePathPoints() {
		// Dangerous! Only called from QueryData when loading ArcPaths
		pathPoints.clear();
	}
	
	public void setNodeAngle(int angle) {
		nodeAngle = angle;
		nodeAngle %= 360;
	}
	
	/**
	 * Inserts a new point into the Array List of path points
	 * at the specified index and shifts all the following points along
	 * 
	 * @param index, newpoint
     * @param newpoint
	 */
	public void insertPoint(int index, PerformanceTreeArcPathPoint newpoint){
		pathPoints.add(index, newpoint);
		if (MacroManager.getEditor() == null) {
			// not in macro mode
			addPointsToGui((QueryView)myArc.getParent());
		}
		else {
			// in macro mode
			addPointsToGui((MacroView)myArc.getParent());
		}
		
	}
	
	/**
	 * Goes through neighbouring pairs of ArcPathPoints determining
	 * the midpoint between them. Then calculates the distance from
	 * midpoint to the point passed as an argument. The pair of
	 * ArcPathPoints resulting in the shortest distance then have
	 * an extra point added between them at the midpoint effectively
	 * splitting that segment into two.
	 * @param mouseposition
	 */
	public void splitSegment(Point2D.Float mouseposition){		
		// An array to store all the distances from the midpoints
		double[] distances = new double[pathPoints.size() - 1];	
		// Calculate the midpoints and distances to them
		for(int index = 0; index < (pathPoints.size() - 1); index++){
			PerformanceTreeArcPathPoint first = (PerformanceTreeArcPathPoint)pathPoints.get(index);
			PerformanceTreeArcPathPoint second = (PerformanceTreeArcPathPoint)pathPoints.get(index+1);
			Point2D.Float midpoint = first.getMidPoint(second);
			distances[index] = midpoint.distance(mouseposition);
		}		
		// Now determine the shortest midpoint
		double shortest = distances[0];
		int wantedpoint = 0;
		for(int index = 0; index < pathPoints.size() - 1; index++){
			if(distances[index] < shortest){
				shortest = distances[index];
				wantedpoint = index;
			}
		}	
		// wantedpoint is now the index of the first point
		// in the pair of arc points marking the segment to
		// be split. So we have all we need to split the arc.
		PerformanceTreeArcPathPoint first = (PerformanceTreeArcPathPoint)pathPoints.get(wantedpoint);
		PerformanceTreeArcPathPoint second = (PerformanceTreeArcPathPoint)pathPoints.get(wantedpoint+1);
		PerformanceTreeArcPathPoint newpoint = new PerformanceTreeArcPathPoint(second.getMidPoint(first), first.getPointType(), this);
		insertPoint(wantedpoint+1, newpoint);
		createPath();
		myArc.updateArcPosition();		
	}
	
	private double getMod(Point2D.Float A, Point2D.Float B) {
		double ABx = A.x - B.x; 
		double ABy = A.y - B.y; 
		
		return Math.sqrt(ABx*ABx + ABy*ABy);
	}
	
	public PerformanceTreeArcPath clone(PerformanceTreeArc parentArc) {
		try {
			PerformanceTreeArcPath clonedArcPath = (PerformanceTreeArcPath)super.clone();
			clonedArcPath.myArc = parentArc;
			clonedArcPath.path = (GeneralPath)path.clone();
			
			if (arcSelection != null)
				clonedArcPath.arcSelection = (GeneralPath)arcSelection.clone();
			else
				clonedArcPath.arcSelection = arcSelection;
			
			if (pointSelection != null)
				clonedArcPath.pointSelection = (GeneralPath)pointSelection.clone();
			else
				clonedArcPath.pointSelection = pointSelection;
			
			clonedArcPath.currentPoint = currentPoint.clone(clonedArcPath);
			clonedArcPath.pointLock = pointLock;
			proximityStroke = proximityStroke;
			clonedArcPath.proximityShape = proximityShape;
			stroke = stroke;
			clonedArcPath.shape = shape;
			clonedArcPath.nodeAngle = nodeAngle;
			clonedArcPath.showControlPoints = showControlPoints;
			List clonedPathPoints = new ArrayList();
			Iterator i = pathPoints.iterator();
			while (i.hasNext()) {
				clonedPathPoints.add(((PerformanceTreeArcPathPoint)i.next()).clone(clonedArcPath));
			}
			clonedArcPath.pathPoints = clonedPathPoints;
			return clonedArcPath;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}



class Cubic {
	
	private final float a;
    private final float b;
    private final float c;
    private final float d;         /* a + b*u + c*u^2 +d*u^3 */
	
	public Cubic(float a, float b, float c, float d){
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}
	
	// Return first control point coordinate (calculated from coefficients)
	public float getX1() {
		return ((b+3*a)/3);
	}
	
	// Return second control point coordinate (calculated from coefficients)
	public float getX2() {
		return ((c+2*b+3*a)/3);
	}
	
	/** evaluate cubic
     * @param u
     * @return*/
	public float eval(float u) {
		return (((d*u) + c)*u + b)*u + a;
	}
	
}
