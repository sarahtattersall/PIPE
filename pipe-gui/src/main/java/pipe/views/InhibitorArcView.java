package pipe.views;

import pipe.actions.gui.PipeApplicationModel;
import pipe.constants.GUIConstants;
import pipe.controllers.PetriNetController;
import uk.ac.imperial.pipe.models.petrinet.Arc;
import uk.ac.imperial.pipe.models.petrinet.Place;
import uk.ac.imperial.pipe.models.petrinet.Transition;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.geom.AffineTransform;


/**
 * View of an inhibitor arc that connects places to transitions
 */
public class InhibitorArcView extends ArcView<Place, Transition> {

    /**
     * The graphical representation of the arc head
     */
    private ArcHead arcHead = new InhibitorArcHead();

    /**
     * Constructor
     * @param model underlying inhibitor arc
     * @param controller Petri net controller for the Petri net the arc is house in
     * @param parent parent of this view
     * @param handler mouse action handler when mouse events are performed on the arc
     * @param applicationModel main PIPE application model
     */
    public InhibitorArcView(Arc<Place, Transition> model, PetriNetController controller, Container parent, MouseInputAdapter handler, PipeApplicationModel applicationModel) {
        super(model, controller, parent, handler, applicationModel);
    }


    /**
     * When the arc is added to the container the inhibitor view will add the arc path points to it
     * @param container to add itself to
     */
    @Override
    public void addToContainer(Container container) {
        updatePath();
    }

    /**
     * Paints the arc and its path using the arcHead
     * @param g
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        AffineTransform reset = g2.getTransform();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.translate(getComponentDrawOffset() + ZOOM_GROW - arcPath.getBounds().getX(),
                getComponentDrawOffset() + ZOOM_GROW - arcPath.getBounds().getY());

        if (isSelected() && !ignoreSelection) {
            g2.setPaint(GUIConstants.SELECTION_LINE_COLOUR);
        } else {
            g2.setPaint(GUIConstants.ELEMENT_LINE_COLOUR);
        }

        g2.setStroke(new BasicStroke(1f));
        g2.draw(arcPath);


        g2.translate(arcPath.getPoint(arcPath.getEndIndex()).getX(), arcPath.getPoint(arcPath.getEndIndex()).getY());
        g2.rotate(model.getEndAngle());

        arcHead.draw(g2);

        if (isSelected() && !ignoreSelection) {
            g2.setPaint(GUIConstants.SELECTION_LINE_COLOUR);
        } else {
            g2.setPaint(GUIConstants.ELEMENT_LINE_COLOUR);
        }

        g2.setTransform(reset);
    }
}
