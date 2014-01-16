package pipe.io.adapters.model;

import javax.xml.bind.annotation.XmlElement;

public class PositionGraphics {
    @XmlElement(name = "position")
    public Point point;
}
