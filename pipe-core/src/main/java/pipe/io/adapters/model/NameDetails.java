package pipe.io.adapters.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class NameDetails {
    @XmlElement(name = "value")
    String name;

    @XmlElement
    OffsetGraphics graphics = new OffsetGraphics();

    public OffsetGraphics getGraphics() {
        return graphics;
    }

    public void setGraphics(OffsetGraphics graphics) {
        this.graphics = graphics;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}