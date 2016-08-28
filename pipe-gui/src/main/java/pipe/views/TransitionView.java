package pipe.views;

import org.jfree.util.ShapeUtilities;

import pipe.constants.GUIConstants;
import pipe.controllers.PetriNetController;
import uk.ac.imperial.pipe.models.petrinet.Transition;

import javax.swing.event.MouseInputAdapter;

import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


/**
 * View representation of a transition component
 */
@SuppressWarnings("serial")
public class TransitionView extends ConnectableView<Transition> {
    /**
     * True if the transition view has been selected
     */
    public boolean highlighted;

    public Shape unrotated;
    /**
     * Constructor
     *
     * @param model             underlying transition model
     * @param controller        Petri net controller of the Petri net the transition is housed in
     * @param parent            parent of the view
     * @param transitionHandler mouse listener actions for the transition when in edit mode
     * @param animationHandler  mouse listener actions for the transition when in animation mode
     */
    public TransitionView(Transition model, PetriNetController controller, Container parent,
                          MouseInputAdapter transitionHandler, MouseListener animationHandler) {
        super(model.getId(), model, controller, controller.getTransitionController(model), parent,
                new Rectangle2D.Double(-model.getWidth()/2, -model.getHeight()/2, model.getWidth(), model.getHeight()));
        unrotated = new Rectangle2D.Double(-model.getWidth()/2, -model.getHeight()/2, model.getWidth(), model.getHeight());
        setChangeListener();

        highlighted = false;

        rotate(model.getAngle());
        //TODO: DEBUG WHY CANT CALL THIS IN CONSTRUCTOR
        //        changeToolTipText();

        setMouseListener(transitionHandler, animationHandler);

    }

    /**
     * Listen for property changes of the underlying mode and trigger a repaint
     */
    private void setChangeListener() {
        model.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                String name = propertyChangeEvent.getPropertyName();
                switch (name) {
                    case Transition.PRIORITY_CHANGE_MESSAGE:
                    case Transition.RATE_CHANGE_MESSAGE:
                        repaint();
                        break;
                    case Transition.ANGLE_CHANGE_MESSAGE:
                        int angle = (int) propertyChangeEvent.getNewValue();
                        rotate(angle);
                    case Transition.TIMED_CHANGE_MESSAGE:
                    case Transition.INFINITE_SEVER_CHANGE_MESSAGE:
                        repaint();
                        break;
                    case Transition.ENABLED_CHANGE_MESSAGE:
                    case Transition.DISABLED_CHANGE_MESSAGE:
                        repaint();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * Rotate the transition
     *
     * Currently doesn't work
     * @param angle to rotate
     */
    public final void rotate(int angle) {
        shape = ShapeUtilities.rotateShape(unrotated, Math.toRadians(angle), 0,0);
        Rectangle bounds = shape.getBounds();
        Rectangle newBounds = new Rectangle((int)(model.getCentre().getX() + bounds.getX()), (int)(model.getCentre().getY() + bounds.getY()), (int) bounds.getWidth() + getComponentDrawOffset(), (int)bounds.getHeight() + getComponentDrawOffset()) ;
        setBounds(newBounds);
    }

    /**
     * Register the mouse listeners to this view
     *
     * @param transitionHandler mouse listener actions for the transition when in edit mode
     * @param animationHandler  mouse listener actions for the transition when in animation mode
     */
    private void setMouseListener(MouseInputAdapter transitionHandler, MouseListener animationHandler) {
        addMouseListener(transitionHandler);
        addMouseMotionListener(transitionHandler);
        addMouseWheelListener(transitionHandler);
        addMouseListener(animationHandler);
    }


    /**
     * Paints the transition, if the model is enabled it will paint the transition in red
     * @param g graphics 
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        Rectangle rect = shape.getBounds();
        g2.translate(rect.getWidth()/2, rect.getHeight()/2);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


        if (isSelected() && !ignoreSelection) {
            g2.setColor(GUIConstants.SELECTION_FILL_COLOUR);
        } else {
            g2.setColor(GUIConstants.ELEMENT_FILL_COLOUR);
        }

        if (model.isTimed()) {
            if (model.isInfiniteServer()) {
                for (int i = 2; i >= 1; i--) {
                    g2.translate(2 * i, -2 * i);
                    g2.fill(shape);
                    Paint pen = g2.getPaint();
                    if (highlightView()) {
                        g2.setPaint(GUIConstants.ENABLED_TRANSITION_COLOUR);
                    } else if (isSelected() && !ignoreSelection) {
                        g2.setPaint(GUIConstants.SELECTION_LINE_COLOUR);
                    } else {
                        g2.setPaint(GUIConstants.ELEMENT_LINE_COLOUR);
                    }
                    g2.draw(shape);
                    g2.setPaint(pen);
                    g2.translate(-2 * i, 2 * i);
                }
            }
            g2.fill(shape);
        }

        if (highlightView()) {
            g2.setPaint(GUIConstants.ENABLED_TRANSITION_COLOUR);
        } else if (isSelected() && !ignoreSelection) {
            g2.setPaint(GUIConstants.SELECTION_LINE_COLOUR);
        } else {
            g2.setPaint(GUIConstants.ELEMENT_LINE_COLOUR);
        }

        g2.draw(shape);
        if (!model.isTimed()) {
            if (model.isInfiniteServer()) {
                for (int i = 2; i >= 1; i--) {
                    g2.translate(2 * i, -2 * i);
                    Paint pen = g2.getPaint();
                    g2.setPaint(GUIConstants.ELEMENT_FILL_COLOUR);
                    g2.fill(shape);
                    g2.setPaint(pen);
                    g2.draw(shape);
                    g2.translate(-2 * i, 2 * i);
                }
            }
            g2.draw(shape);
            g2.fill(shape);
        }
        //        changeToolTipText();
    }

    /**
     * @return true if in animate mode and the model is enabled
     */
    private boolean highlightView() {
        return model.isEnabled() && petriNetController.isInAnimationMode();
    }

    /**
     * Adds the name label to the container
     * @param container to add itself to
     */
    @Override
    public void addToContainer(Container container) {
        addLabelToContainer(container);
    }

}
