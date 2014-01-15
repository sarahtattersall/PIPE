package pipe.petrinet.adapters.model;

import pipe.petrinet.adapters.valueAdapter.StringValueAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


@XmlAccessorType(XmlAccessType.FIELD)
public class AdaptedConnectable {

    @XmlElement
    private PositionGraphics graphics;

    @XmlAttribute
    private String id;

    @XmlElement(name="name")
    @XmlJavaTypeAdapter(StringValueAdapter.class)
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PositionGraphics getGraphics() {
        return graphics;
    }

    public void setGraphics(PositionGraphics graphics) {
        this.graphics = graphics;
    }
}
