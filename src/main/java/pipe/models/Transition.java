package pipe.models;

import pipe.views.viewComponents.RateParameter;

import java.io.Serializable;

/*
 * @author yufei wang(minor changes)
 */
public class Transition extends Connectable implements Serializable
{
    private int priority;
	private String rateExpr;


    private double x = 0;
    private double y = 0;
    private int orientation = 0;
    private boolean timed = false;
    private boolean infiniteServer = false;
    private double nameXOffset = 0;
    private double nameYOffset = 0;
    private int angle = 0;
    private boolean timedTransition;
    private RateParameter rateParameter;

    public Transition(String id, String name)
    {
        this(id, name, "1", 1);
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

    public void setPriority(Integer priority)
    {
        this.priority = priority;
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


    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getNameXOffset() {
        return nameXOffset;
    }

    public double getNameYOffset() {
        return nameYOffset;
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

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public void setTimed(boolean timed) {
        this.timed = timed;
    }

    public void setInfiniteServer(boolean infiniteServer) {
        this.infiniteServer = infiniteServer;
    }

    public void setNameXOffset(double nameXOffset) {
        this.nameXOffset = nameXOffset;
    }

    public void setNameYOffset(double nameYOffset) {
        this.nameYOffset = nameYOffset;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public void setTimedTransition(boolean timedTransition) {
        this.timedTransition = timedTransition;
    }

    public boolean isTimedTransition() {
        return timedTransition;
    }

    public RateParameter getRateParameter() {
        return rateParameter;
    }

    public void setRateParameter(RateParameter rateParameter) {
        this.rateParameter = rateParameter;
    }
}
