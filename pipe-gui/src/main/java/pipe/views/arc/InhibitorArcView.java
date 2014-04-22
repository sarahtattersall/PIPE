package pipe.views.arc;

import pipe.controllers.PetriNetController;
import pipe.gui.Constants;
import pipe.models.component.arc.Arc;
import pipe.models.component.place.Place;
import pipe.models.component.transition.Transition;
import pipe.views.ArcView;
import pipe.views.PetriNetView;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.Serializable;


/**
 * @author Pere Bonet
 * @version 1.0
 */
public class InhibitorArcView extends ArcView<Place, Transition> implements Serializable {

    private final static String type = "inhibitor";

    ArcHead arcHead = new InhibitorArcHead();

    public InhibitorArcView(Arc<Place, Transition> model, PetriNetController controller) {
        super(model, controller);
    }

    @Override
    public void arcSpecificUpdate() {

    }

    @Override
    protected void arcSpecificDelete() {

    }

    @Override
    protected void arcSpecificAdd() {

    }




    public InhibitorArcView copy() {
        return null;
//        return new InhibitorArcView(this);
    }


    @Override
    public String getType() {
        return type;
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        AffineTransform reset = g2.getTransform();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.translate(getComponentDrawOffset() + ZOOM_GROW - arcPath.getBounds().getX(),
                getComponentDrawOffset() + ZOOM_GROW - arcPath.getBounds().getY());

        if (isSelected() && !_ignoreSelection) {
            g2.setPaint(Constants.SELECTION_LINE_COLOUR);
        } else {
            g2.setPaint(Constants.ELEMENT_LINE_COLOUR);
        }

        g2.setStroke(new BasicStroke(1f));
        g2.draw(arcPath);


        g2.translate(arcPath.getPoint(arcPath.getEndIndex()).getX(), arcPath.getPoint(arcPath.getEndIndex()).getY());
        g2.rotate(model.getEndAngle());

        arcHead.draw(g2);

        if (isSelected() && !_ignoreSelection) {
            g2.setPaint(Constants.SELECTION_LINE_COLOUR);
        } else {
            g2.setPaint(Constants.ELEMENT_LINE_COLOUR);
        }

        g2.setTransform(reset);
    }
}
