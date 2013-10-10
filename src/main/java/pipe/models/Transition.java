package pipe.models;

import java.io.Serializable;

/*
 * @author yufei wang(minor changes)
 */
public class Transition extends Connectable implements Serializable
{
   // private double _rate;
    private Integer _priority;
	private String _rateExpr;
    public Transition(String id, String name)
    {
        this(id, name, "1", 1);
    }

    public Transition(String id, String name, String rateExpr, int priority)
    {
        super(id, name);
//        _rate = rate;
        _rateExpr=rateExpr;
        _priority = priority;
    }


//    public double getRate()
//    {
//        return _rate;
//    }
//
//    public void setRate(double rate)
//    {
//        _rate = rate;
//    }
    

    public Integer getPriority()
    {
        return _priority;
    }

    public void setPriority(Integer priority)
    {
        _priority = priority;
    }

//	public String getfunctionalRates() {
//		return _functionalRates;
//	}
//
//	public void setfunctionalRates(String _functionalRates) {
//		this._functionalRates = _functionalRates;
//	}

	public String getRateExpr() {
		return _rateExpr;
	}

	public void setRateExpr(String string) {
		_rateExpr=string;
	}
	public void setRateExpr(double expr) {
		_rateExpr=expr+"";
	}


}
