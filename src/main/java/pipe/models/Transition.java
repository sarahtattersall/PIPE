package pipe.models;

import java.io.Serializable;

/*
 * @author yufei wang(minor changes)
 */
public class Transition extends Connectable implements Serializable
{
    private int priority;
	private String rateExpr;
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


}
