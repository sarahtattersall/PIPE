package pipe.models;

import pipe.gui.Constants;
import pipe.views.viewComponents.RateParameter;

import java.io.Serializable;

/*
 * @author yufei wang(minor changes)
 */
public class Transition extends Connectable implements Serializable
{
    private int priority;
	private String rateExpr;
    private int orientation = 0;
    private boolean timed = false;
    private boolean infiniteServer = false;
    private double nameXOffset = 0;
    private double nameYOffset = 0;
    private int angle = 0;
    private boolean timedTransition;
    private RateParameter rateParameter;

    public static final int TRANSITION_HEIGHT = Constants.PLACE_TRANSITION_HEIGHT;
    public static final int TRANSITION_WIDTH = TRANSITION_HEIGHT / 3;

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
    public double getCentreX() {
        return getX() + getWidth()/2;
    }

    @Override
    public double getCentreY() {
        return getY() + getHeight()/2;
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
