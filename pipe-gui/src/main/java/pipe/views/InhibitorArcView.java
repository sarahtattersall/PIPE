package pipe.views;

import pipe.controllers.PetriNetController;
import pipe.gui.Constants;
import pipe.gui.ZoomController;
import pipe.models.component.arc.Arc;
import pipe.models.component.place.Place;
import pipe.models.component.transition.Transition;

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
    private final static int OVAL_X = -4;
    private final static int OVAL_Y = -8;
    private final static int OVAL_WIDTH = 8;
    private final static int OVAL_HEIGHT = 8;


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




    public InhibitorArcView paste(double despX, double despY, boolean toAnotherView, PetriNetView model) {
//        ConnectableView source = this.getSource().getLastCopy();
//        ConnectableView target = this.getTarget().getLastCopy();
//
//        if (source == null && target == null) {
//            // don't paste an arc with neither source nor target
//            return null;
//        }
//
//        if (source == null) {
//            if (toAnotherView) {
//                // if the source belongs to another Petri Net, the arc can't be
//                // pasted
//                return null;
//            } else {
//                source = this.getSource();
//            }
//        }
//
//        if (target == null) {
//            if (toAnotherView) {
//                // if the target belongs to another Petri Net, the arc can't be
//                // pasted
//                return null;
//            } else {
//                target = this.getTarget();
//            }
//        }
//
//        //TODO: NEEDS ACTUAL WEIGHTS
//        InhibitorArc arc = new InhibitorArc(source.getModel(), target.getModel(), new HashMap<Token, String>());
//        InhibitorArcView copy =
//                new InhibitorArcView((double) 0, (double) 0, (double) 0, (double) 0, source, target, this.getWeight(),
//                        source.getId() + " to " + target.getId(), arc, petriNetController);
//
//        copy.arcPath.delete();
//        for (int i = 0; i <= this.arcPath.getEndIndex(); i++) {
//            copy.arcPath.addIntermediatePoint(this.arcPath.getPoint(i).getX() + despX, this.arcPath.getPoint(i).getY() + despY,
//                    this.arcPath.isCurved(i));
//            //copy.arcPath.selectPoint(i);
//        }
//
//        source.addOutbound(copy);
//        target.addInbound(copy);
//        return copy;
        return null;
    }


    public InhibitorArcView copy() {
        return null;
//        return new InhibitorArcView(this);
    }


    public String getType() {
        return type;
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

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


        Point2D endPoint = model.getEndPoint();
        g2.translate(endPoint.getX(), endPoint.getY());

        g2.rotate(arcPath.getEndAngle() + Math.PI);
        g2.setColor(java.awt.Color.WHITE);

        AffineTransform reset = g2.getTransform();

        g2.setStroke(new BasicStroke(0.8f));
        g2.fillOval(OVAL_X, OVAL_Y, OVAL_WIDTH, OVAL_HEIGHT);

        if (isSelected() && !_ignoreSelection) {
            g2.setPaint(Constants.SELECTION_LINE_COLOUR);
        } else {
            g2.setPaint(Constants.ELEMENT_LINE_COLOUR);
        }
        g2.drawOval(OVAL_X, OVAL_Y, OVAL_WIDTH, OVAL_HEIGHT);

        g2.setTransform(reset);
    }

//    @Override
//    public void update() {
//        repaint();
//    }
}
