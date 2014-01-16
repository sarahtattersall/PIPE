package pipe.io.adapters.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class AdaptedArcPoint {
    @XmlAttribute
    private String id = "";

    @XmlAttribute
    private double x;

    @XmlAttribute
    private double y;

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public boolean isCurved() {
        return curved;
    }

    public void setCurved(boolean curved) {
        this.curved = curved;
    }

    @XmlAttribute(name = "curvePoint")
    private boolean curved;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
