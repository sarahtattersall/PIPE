package pipe.views;

import pipe.controllers.PetriNetController;
import pipe.gui.Constants;
import pipe.gui.PetriNetTab;
import pipe.models.component.ConditionalPlace;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.io.Serializable;

public class ConditionPlaceView extends ConnectableView implements Cloneable, Constants, Serializable {
    private static final long serialVersionUID = 1L;
    public final static String type = "ConditionalPlace";

    private static final int DIAMETER = PLACE_TRANSITION_HEIGHT;
    private static final Ellipse2D.Double place = new Ellipse2D.Double(0, 0, DIAMETER, DIAMETER);
    private Integer currentMarking = null;
    private final Color defaultColor = Color.lightGray;
    private final Color validColor = Color.green;
    private Color currentColor = defaultColor;
    private String condOperator = "", condOperand = "";
    private boolean tagged = false;

    public ConditionPlaceView(PlaceView inputPlaceView, PetriNetController controller) {
        //MODEL
        super(inputPlaceView.getId(),
                inputPlaceView.getName(), 0, 0, new ConditionalPlace(inputPlaceView.getId(), inputPlaceView.getName()),
                controller);
        currentMarking = new Integer(inputPlaceView.getCurrentMarkingView().get(0).getCurrentMarking());
        updateBounds();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        AffineTransform saveXform = g2.getTransform();
        AffineTransform scaledXform = zoomControl.getTransform();
        Insets insets = getInsets();
        int x = insets.left;
        int y = insets.top;

        g2.translate(COMPONENT_DRAW_OFFSET, COMPONENT_DRAW_OFFSET);
        g2.transform(scaledXform);

        g2.setStroke(new BasicStroke(1.0f));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(currentColor);
        g2.fill(place);

        if (isSelected() && !_ignoreSelection) {
            g2.setPaint(SELECTION_LINE_COLOUR);
        } else {
            g2.setPaint(ELEMENT_LINE_COLOUR);
        }
        g2.draw(place);

        g.drawString(condOperator + condOperand, x + 5, y + 20);
        g2.setTransform(saveXform);
    }


    public int getCurrentMarking() {
        if (currentMarking == null) {
            return 0;
        } else {
            return currentMarking.intValue();
        }
    }

    public int boundsWidth() {
        return WIDTH + 1;
    }

    public int boundsHeight() {
        return HEIGHT + 1;
    }

    public int getDiameter() {
        int zoomBy = zoomControl.getPercent();
        return (int) (DIAMETER * zoomBy * 0.01);
    }

    public boolean contains(int x, int y) {
        int zoomPercentage = zoomControl.getPercent();
        double unZoomedX = (x - COMPONENT_DRAW_OFFSET) / (zoomPercentage / 100.0);
        double unZoomedY = (y - COMPONENT_DRAW_OFFSET) / (zoomPercentage / 100.0);

        return place.contains((int) unZoomedX, (int) unZoomedY);
    }

    public AbstractPetriNetViewComponent clone() {
        return super.clone();
    }

    @Override
    public void addToPetriNetTab(PetriNetTab tab) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setCondition(String operator, String operand) {
        if (operator.equals("<=")) {
            operator = "\u2264";
        } else if (operator.equals(">=")) {
            operator = "\u2265";
        }

        if (operator.equals("T")) {
            setTagged(true);
            operand = "T";
        } else {
            setTagged(false);
        }

        condOperator = operator;
        condOperand = operand;
        currentColor = validColor;
        repaint();
    }

    public void removeCondition() {
        condOperator = "";
        condOperand = "";
        currentColor = defaultColor;
        repaint();
    }

    @Override
    public void showEditor() {
        // TODO Auto-generated method stub

    }

    @Override
    public void toggleAttributesVisible() {
    }

    public PetriNetViewComponent copy() {
        return null;
    }

    public AbstractPetriNetViewComponent paste(double despX, double despY, boolean notInTheSameView, PetriNetView model) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean conditionHasBeenSpecified() {
        return !condOperator.equals("") && !condOperand.equals("");
    }

    public boolean isTagged() {
        return tagged;
    }

    void setTagged(boolean setTo) {
        tagged = setTo;
    }
}