package pipe.petrinet.adapters.model;

import pipe.models.component.ArcPoint;
import pipe.models.component.Token;
import pipe.petrinet.adapters.modelAdapter.ArcPointAdapter;
import pipe.petrinet.adapters.valueAdapter.StringAttributeValueAdaptor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
public class AdaptedArc {
    @XmlAttribute
    private String id;

    @XmlAttribute
    private String source;

    @XmlAttribute
    private String target;

    @XmlElement(name="arcpath")
    @XmlJavaTypeAdapter(ArcPointAdapter.class)
    private List<ArcPoint> arcPoints = new ArrayList<ArcPoint>();

    @XmlElement
    @XmlJavaTypeAdapter(StringAttributeValueAdaptor.class)
    private String type = "normal";

    private Inscription inscription = new Inscription();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Inscription getInscription() {
        return inscription;
    }

    public void setInscription(Inscription inscription) {
        this.inscription = inscription;
    }

    public List<ArcPoint> getArcPoints() {
        return arcPoints;
    }

    public void setArcPoints(List<ArcPoint> arcPoints) {
        this.arcPoints = arcPoints;
    }


    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Inscription {
        @XmlElement(name = "value")
        private String tokenCounts = "";

        private OffsetGraphics graphics;

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
