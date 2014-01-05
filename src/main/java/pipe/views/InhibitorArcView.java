package pipe.views;

import pipe.controllers.PetriNetController;
import pipe.gui.Constants;
import pipe.gui.ZoomController;
import pipe.models.component.Arc;
import pipe.utilities.Copier;
import pipe.views.viewComponents.NameLabel;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.Serializable;
import java.util.LinkedList;


/**
 * @author Pere Bonet
 * @version 1.0
 */
public class InhibitorArcView extends ArcView implements Serializable {

    private final static String type = "inhibitor";
    private final static int OVAL_X = -4;
    private final static int OVAL_Y = -8;
    private final static int OVAL_WIDTH = 8;
    private final static int OVAL_HEIGHT = 8;


    public InhibitorArcView(Arc model, PetriNetController controller) {
        super(model, controller);
    }

    @Override
    protected void arcSpecificUpdate() {

    }

    @Override
    protected void arcSpecificDelete() {

    }

    @Override
    protected void arcSpecificAdd() {

    }

    private InhibitorArcView(InhibitorArcView arcView) {
//        for (int i = 0; i <= arcView.arcPath.getEndIndex(); i++) {
//            this.arcPath.addIntermediatePoint(arcView.arcPath.getPoint(i).getX(), arcView.arcPath.getPoint(i).getY(),
//                    arcView.arcPath.getPointType(i));
//        }
//        this.arcPath.createPath();
//        this.updateBounds();
//        this._id = arcView._id;
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
        return new InhibitorArcView(this);
    }


    public String getType() {
        return type;
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.translate(getComponentDrawOffset() + zoomGrow - arcPath.getBounds().getX(),
                getComponentDrawOffset() + zoomGrow - arcPath.getBounds().getY());

        if (isSelected() && !_ignoreSelection) {
            g2.setPaint(Constants.SELECTION_LINE_COLOUR);
        } else {
            g2.setPaint(Constants.ELEMENT_LINE_COLOUR);
        }

        g2.setStroke(new BasicStroke(0.01f * _zoomPercentage));
        g2.draw(arcPath);

        g2.translate(arcPath.getPoint(arcPath.getEndIndex()).getX(), arcPath.getPoint(arcPath.getEndIndex()).getY());

        g2.rotate(arcPath.getEndAngle() + Math.PI);
        g2.setColor(java.awt.Color.WHITE);

        AffineTransform reset = g2.getTransform();
        g2.transform(ZoomController.getTransform(_zoomPercentage));

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
