package pipe.io.adapters.model;

import pipe.io.adapters.valueAdapter.BooleanValueAdapter;
import pipe.io.adapters.valueAdapter.IntValueAdapter;
import pipe.io.adapters.valueAdapter.StringValueAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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

    @XmlElement(name = "toolspecific")
    private ToolSpecific toolSpecific;

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

    public ToolSpecific getToolSpecific() {
        return toolSpecific;
    }

    public void setToolSpecific(ToolSpecific toolSpecific) {
        this.toolSpecific = toolSpecific;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ToolSpecific {
        public String getTool() {
            return tool;
        }

        public void setTool(String tool) {
            this.tool = tool;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getRateDefinition() {
            return rateDefinition;
        }

        public void setRateDefinition(String rateDefinition) {
            this.rateDefinition = rateDefinition;
        }

        @XmlAttribute
        private String tool = "PIPE";

        @XmlAttribute
        private String version = "2.5";

        @XmlAttribute
        private String rateDefinition;
    }
}
