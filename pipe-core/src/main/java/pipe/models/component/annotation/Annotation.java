package pipe.models.component.annotation;

import pipe.models.component.AbstractPetriNetComponent;
import pipe.visitor.foo.PetriNetComponentVisitor;

public class Annotation extends AbstractPetriNetComponent {

    /**
     * True if display border for annotation box
     */
    private boolean border;

    /**
     * Top left x position
     */
    private int x;

    /**
     * Top left y position
     */
    private int y;

    public void setText(String text) {
        String old = this.text;
        this.text = text;
        changeSupport.firePropertyChange("text", old, text);
    }

    /**
     * Text to be displayed
     */
    private String text;

    /**
     * Annotation box width
     */
    private int width;

    /**
     * Annotation box height
     */
    private int height;

    public Annotation(int x, int y, String text, int width, int height, boolean border) {
        this.border = border;
        this.x = x;
        this.y = y;
        this.text = text;
        this.width = width;
        this.height = height;
    }

    public boolean hasBoarder() {
        return border;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getText() {
        return text;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
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
