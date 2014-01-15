package pipe.petrinet.adapters.model;

import pipe.petrinet.adapters.valueAdapter.BooleanValueAdapter;
import pipe.petrinet.adapters.valueAdapter.IntValueAdapter;
import pipe.petrinet.adapters.valueAdapter.StringValueAdapter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class AdaptedTransition extends AdaptedConnectable {

    @XmlJavaTypeAdapter(BooleanValueAdapter.class)
    private Boolean infiniteServer;
    @XmlJavaTypeAdapter(BooleanValueAdapter.class)
    private Boolean timed;
    @XmlElement(name = "priority")
    @XmlJavaTypeAdapter(IntValueAdapter.class)
    private Integer priority;
    @XmlElement(name = "orientation")
    @XmlJavaTypeAdapter(IntValueAdapter.class)
    private Integer angle;
    @XmlElement(name = "rate")
    @XmlJavaTypeAdapter(StringValueAdapter.class)
    private String rate;

    public Boolean getTimed() {
        return timed;
    }

    public void setTimed(Boolean timed) {
        this.timed = timed;
    }

    public Boolean getInfiniteServer() {
        return infiniteServer;
    }

    public void setInfiniteServer(Boolean infiniteServer) {
        this.infiniteServer = infiniteServer;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}
