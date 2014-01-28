package pipe.models.component.annotation;

import pipe.models.component.PlaceablePetriNetComponent;
import pipe.visitor.foo.PetriNetComponentVisitor;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * This class is for labels that can be added to the Petri net
 */
public class Annotation extends PlaceablePetriNetComponent {

    /**
     * True if display border for annotation box
     */
    @XmlAttribute
    private boolean border;

    /**
     * Top left x position
     */
    @XmlAttribute
    private int x;

    /**
     * Top left y position
     */
    @XmlAttribute
    private int y;

    /**
     * Text to be displayed
     */
    @XmlElement
    private String text;

    /**
     * Annotation box width
     */
    @XmlAttribute
    private int width;

    /**
     * Annotation box height
     */
    @XmlAttribute
    private int height;

    public Annotation(int x, int y, String text, int width, int height, boolean border) {
        this.border = border;
        this.x = x;
        this.y = y;
        this.text = text;
        this.width = width;
        this.height = height;
    }

    public void setBorder(boolean border) {
        this.border = border;
    }

    public boolean hasBoarder() {
        return border;
    }

    @Override
    public int getX() {
        return x;
    }

    public void setX(int x) {
        int old = this.x;
        this.x = x;
        changeSupport.firePropertyChange("x", old, x);
    }

    @Override
    public int getY() {
        return y;
    }

    public void setY(int y) {
        int old = this.y;
        this.y = y;
        changeSupport.firePropertyChange("y", old, y);
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        String old = this.text;
        this.text = text;
        changeSupport.firePropertyChange("text", old, text);
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    @Override
    public boolean isDraggable() {
        return true;
    }

    @Override
    public void accept(PetriNetComponentVisitor visitor) {
        if (visitor instanceof AnnotationVisitor) {
            ((AnnotationVisitor) visitor).visit(this);
        }
    }

    //TODO: WORK OUT WHAT THESE SHOULD DO
    @Override
    public String getId() {
        return "";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setId(String id) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setName(String name) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Toggles the border on/off
     */
    public void toggleBorder() {
        border = !border;
        changeSupport.firePropertyChange("toggleBorder", !border, border);
    }
}
