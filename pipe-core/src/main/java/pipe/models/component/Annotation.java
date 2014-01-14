package pipe.models.component;

import pipe.visitor.PetriNetComponentVisitor;

public class Annotation extends AbstractPetriNetComponent {
    public Annotation(double x, double y, String text,
                      double width,
                      double height, boolean border) {
        this.border = border;
        this.x = x;
        this.y = y;
        this.text = text;
        this.width = width;
        this.height = height;
    }

    private boolean border;
    private double x;
    private double y;
    private String text;
    private double width;
    private double height;

    public boolean hasBoarder() {
        return border;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String getText() {
        return text;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    @Override
    public boolean isSelectable() {
        return  true;
    }

    @Override
    public boolean isDraggable() {
        return true;
    }

    @Override
    public void accept(PetriNetComponentVisitor visitor) {
        visitor.visit(this);
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
}
