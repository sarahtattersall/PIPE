package pipe.models.component;

import parser.ExprEvaluator;
import pipe.gui.Constants;
import pipe.models.visitor.connectable.ConnectableVisitor;
import pipe.models.visitor.PetriNetComponentVisitor;
import pipe.views.viewComponents.RateParameter;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.Map;


public class Transition extends Connectable implements Serializable
{

    @Pnml("priority")
    private int priority;

    @Pnml("rate")
	private String rateExpr;

    private int orientation = 0;

    @Pnml("timed")
    private boolean timed = false;

    @Pnml("infiniteServer")
    private boolean infiniteServer = false;

    @Pnml("angle")
    private int angle = 0;

    private RateParameter rateParameter;

    public static final int TRANSITION_HEIGHT = Constants.PLACE_TRANSITION_HEIGHT;
    public static final int TRANSITION_WIDTH = TRANSITION_HEIGHT / 3;
    private static final double ROOT_THREE_OVER_TWO = 0.5 * Math.sqrt(3);
    private boolean enabled = false;

    public Transition(String id, String name)
    {
        this(id, name, "1", 1);
    }

    @Override
    public int getHeight() {
        return TRANSITION_HEIGHT;
    }

    @Override
    public int getWidth() {
        return TRANSITION_WIDTH;
    }

    @Override
    public Point2D.Double getCentre() {
        return new Point2D.Double(getX() + getHeight()/2, getY() + getHeight()/2);
    }

    /**
     * Rotates point on transition around transition center
     * @param angle
     * @param point
     * @return
     */
    private Point2D.Double rotateAroundCenter(double angle, Point2D.Double point)
    {
        AffineTransform tx = new AffineTransform();
        Point2D center = getCentre();
        tx.rotate(angle, center.getX(), center.getY());
        Point2D.Double rotatedPoint = new Point2D.Double();
        tx.transform(point, rotatedPoint);
        return rotatedPoint;
    }

    @Override
    public Point2D.Double getArcEdgePoint(double angle) {
        double half_height = getHeight()/2;
        double centre_x = x + half_height; //Use height since the actual object is a square, width is just the displayed width
        double centre_y = y + half_height;

        Point2D.Double connectionPoint = new Point2D.Double(centre_x, centre_y);

        double half_width = getWidth()/2;
        double rotatedAngle = angle + Math.toRadians(this.angle);
        if (connectToTop(rotatedAngle)) {
            connectionPoint.y -= half_height;
        } else if (connectToBottom(rotatedAngle)) {
            connectionPoint.y += half_height;
        } else if (connectToLeft(rotatedAngle)) {
            connectionPoint.x -= half_width;
        } else { //connectToRight
            connectionPoint.x += half_width;
        }

        return rotateAroundCenter(Math.toRadians(this.angle), connectionPoint);
    }

    @Override
    public boolean isEndPoint() {
        return true;
    }

    @Override
    public void accept(final ConnectableVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Return true if an arc connecting to this should
     * connect to the left
     * @param angle in radians
     * @return
     */
    private boolean connectToLeft(double angle) {
        return (Math.sin(angle) > 0);
    }

    /**
     * Return true if an arc connecting to this should
     * connect to the bottom
     * @param angle in radians
     * @return
     */
    private boolean connectToBottom(double angle) {
        return Math.cos(angle) < -ROOT_THREE_OVER_TWO;
    }

    /**
     * Return true if an arc connecting to this should
     * connect to the top
     * @param angle in radians
     * @return
     */
    private boolean connectToTop(double angle) {
        return Math.cos(angle) > ROOT_THREE_OVER_TWO;
    }

    public Transition(String id, String name, String rateExpr, int priority)
    {
        super(id, name);
        this.rateExpr =rateExpr;
        this.priority = priority;
    }

    public int getPriority()
    {
        return priority;
    }

    public void setPriority(int priority)
    {
        int old = this.priority;
        this.priority = priority;
        changeSupport.firePropertyChange("priority", old, priority);
    }

	public String getRateExpr() {
		return rateExpr;
	}

	public void setRateExpr(String string) {
		rateExpr = string;
	}
	public void setRateExpr(double expr) {
		rateExpr = Double.toString(expr);
	}

    public int getAngle() {
        return angle;
    }

    public int getOrientation() {
        return orientation;
    }

    public boolean isTimed() {
        return timed;
    }

    public boolean isInfiniteServer() {
        return infiniteServer;
    }

    public void setOrientation(int orientation) {
        int old = this.orientation;
        this.orientation = orientation;
        changeSupport.firePropertyChange("oritentation", old, orientation);
    }

    public void setTimed(boolean timed) {
        boolean old = this.timed;
        this.timed = timed;
        changeSupport.firePropertyChange("oritentation", old, timed);
    }

    public void setInfiniteServer(boolean infiniteServer) {
        boolean old = this.infiniteServer;
        this.infiniteServer = infiniteServer;
        changeSupport.firePropertyChange("infiniteServer", old, infiniteServer);
    }



    public void setAngle(int angle) {
        int old = this.angle;
        this.angle = angle;
        changeSupport.firePropertyChange("angle", old, angle);
    }

    public RateParameter getRateParameter() {
        return rateParameter;
    }

    public void setRateParameter(RateParameter rateParameter) {
        this.rateParameter = rateParameter;
    }

    @Override
    public boolean isSelectable() {
        return  true;
    }

    @Override
    public boolean isDraggable() {
        return true;
    }

    @Override
    public void accept(PetriNetComponentVisitor visitor) {
        visitor.visit(this);
    }

    public void enable() {
        enabled = true;
//        notifyObservers();
    }

    public void disable() {
        enabled = false;
//        notifyObservers();
    }

    public boolean isEnabled() {
        return enabled;
    }


}
