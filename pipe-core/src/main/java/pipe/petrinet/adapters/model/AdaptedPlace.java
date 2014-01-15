package pipe.petrinet.adapters.model;

import pipe.petrinet.adapters.valueAdapter.IntValueAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
public class AdaptedPlace extends AdaptedConnectable {

    @XmlJavaTypeAdapter(IntValueAdapter.class)
    private Integer capacity = 0;

    private InitialMarking initialMarking = new InitialMarking();

    public InitialMarking getInitialMarking() {
        return initialMarking;
    }

    public void setInitialMarking(InitialMarking initialMarking) {
        this.initialMarking = initialMarking;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }


    @XmlAccessorType(XmlAccessType.FIELD)
    public static class InitialMarking {
        OffsetGraphics graphics;

        @XmlElement(name = "value")
        private String tokenCounts = "";

        public String getTokenCounts() {
            return tokenCounts;
        }

        public void setTokenCounts(String tokenCounts) {
            this.tokenCounts = tokenCounts;
        }

        public OffsetGraphics getGraphics() {
            return graphics;
        }

        public void setGraphics(OffsetGraphics graphics) {
            this.graphics = graphics;
        }
    }


}
