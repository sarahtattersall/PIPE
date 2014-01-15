package pipe.io.adapters.model;

import pipe.io.adapters.valueAdapter.BooleanValueAdapter;
import pipe.io.adapters.valueAdapter.IntValueAdapter;
import pipe.io.adapters.valueAdapter.StringValueAdapter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class AdaptedTransition extends AdaptedConnectable {

    @XmlJavaTypeAdapter(BooleanValueAdapter.class)
    private Boolean infiniteServer = false;
    @XmlJavaTypeAdapter(BooleanValueAdapter.class)
    private Boolean timed = false;
    @XmlElement(name = "priority")
    @XmlJavaTypeAdapter(IntValueAdapter.class)
    private Integer priority = 0;
    @XmlElement(name = "orientation")
    @XmlJavaTypeAdapter(IntValueAdapter.class)
    private Integer angle = 0;
    @XmlElement(name = "rate")
    @XmlJavaTypeAdapter(StringValueAdapter.class)
    private String rate = "";

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
