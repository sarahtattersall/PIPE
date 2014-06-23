package pipe.views;

import pipe.actions.gui.PipeApplicationModel;
import pipe.constants.GUIConstants;
import pipe.controllers.PetriNetController;
import uk.ac.imperial.pipe.exceptions.PetriNetComponentNotFoundException;
import uk.ac.imperial.pipe.models.petrinet.Arc;
import uk.ac.imperial.pipe.models.petrinet.ArcPoint;
import uk.ac.imperial.pipe.models.petrinet.Connectable;
import uk.ac.imperial.pipe.models.petrinet.Token;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Normal arc view
 * @param <S> source model
 * @param <T> target model
 */
public class NormalArcView<S extends Connectable, T extends Connectable> extends ArcView<S, T> {
    /**
     * Class logger
     */
    private static final Logger LOGGER = Logger.getLogger(NormalArcView.class.getName());

    /**
     * Weight labels to display
     */
    private final Collection<TextLabel> weightLabel = new LinkedList<>();

    /**
     * Listens for changes in intermediate points x, y and updates the weights accordingly
     */
    private final WeightLabelListener weightListener = new WeightLabelListener();

    /**
     * Graphical arc head
     */
    private ArcHead arcHead = new NormalHead();

    /**
     * joined if it displays two directional arcs sharing the opposite source and targets
     */
    @Deprecated
    private boolean joined = false;

    /**
     * Constructor
     * @param model underlying normal arc
     * @param controller Petri ent controller for the Petri net that houses the arc
     * @param parent view parent
     * @param handler mouse event handler
     * @param applicationModel PIPE main appliaction model
     */
    public NormalArcView(Arc<S, T> model, PetriNetController controller, Container parent, MouseInputAdapter handler,
                         PipeApplicationModel applicationModel) {
        super(model, controller, parent, handler, applicationModel);
        addConnectableListener();
        addSourceTargetConnectableListener();
        for (TextLabel label : weightLabel) {
            getParent().add(label);
        }
        for (ArcPoint arcPoint : model.getArcPoints()) {
            arcPoint.addPropertyChangeListener(weightListener);
        }
    }

    /**
     * Listens to the source/target changing position
     */
    private void addSourceTargetConnectableListener() {
        PropertyChangeListener changeListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String name = evt.getPropertyName();
                if (name.equals(Connectable.X_CHANGE_MESSAGE) || name.equals(Connectable.Y_CHANGE_MESSAGE)) {
                    updateWeights();
                }
            }
        };
        model.getSource().addPropertyChangeListener(changeListener);
        model.getTarget().addPropertyChangeListener(changeListener);
    }


    /**
     * Adds a listener to the the arc listening for a change in the arc weights
     */
    private void addConnectableListener() {
        PropertyChangeListener listener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                String name = propertyChangeEvent.getPropertyName();
                if (name.equals(Arc.WEIGHT_CHANGE_MESSAGE)) {
                    updateWeights();
                } if (name.equals(Arc.NEW_INTERMEDIATE_POINT_CHANGE_MESSAGE)) {
                    ArcPoint point = (ArcPoint) propertyChangeEvent.getNewValue();
                    point.addPropertyChangeListener(weightListener);
                    updateWeights();
                } if (name.equals(Arc.DELETE_INTERMEDIATE_POINT_CHANGE_MESSAGE)) {
                    ArcPoint point = (ArcPoint) propertyChangeEvent.getOldValue();
                    point.removePropertyChangeListener(weightListener);
                    updateWeights();
                }
            }
        };
        model.addPropertyChangeListener(listener);
    }

    /**
     * Paints the weight label on the parent
     */
    @Override
    public void componentSpecificDelete() {
        super.componentSpecificDelete();
        for (TextLabel label : weightLabel) {
            removeLabelFromParentContainer(label);
        }
    }

    /**
     * Removes the weight labels from the parent
     * @param label
     */
    private void removeLabelFromParentContainer(TextLabel label) {
        getParent().remove(label);
    }

    /**
     * When added to the container it updates the paths and weights
     * @param container to add itself to
     */
    @Override
    public void addToContainer(Container container) {
        updatePath();
        updateWeights();
    }

    /**
     * Creates and paints the weights in the center of the arc
     */
    private void updateWeights() {
        removeCurrentWeights();
        createWeightLabels();
        setWeightLabelPosition();

        addWeightLabelsToContainer(getParent());
    }

    /**
     * Removes the weights from the arc
     */
    private void removeCurrentWeights() {
        for (TextLabel name : weightLabel) {
            removeLabelFromParentContainer(name);
        }
        weightLabel.clear();
    }

    /**
     * Creates token weight labels
     */
    private void createWeightLabels() {
        Map<String, String> weights = model.getTokenWeights();
        for (Map.Entry<String, String> entry : weights.entrySet()) {
            String weight = entry.getValue();
            String tokenId = entry.getKey();
            TextLabel label = new TextLabel(weight);
            try {
                Token token = petriNetController.getToken(tokenId);
                label.setColor(token.getColor());
            } catch (PetriNetComponentNotFoundException e) {
                LOGGER.log(Level.SEVERE, e.getMessage());
                label.setColor(Color.BLACK);
            }
            label.updateSize();
            weightLabel.add(label);
        }
    }

    /**
     * Set the position of the weight labels to the middle of the arc
     */
    protected void setWeightLabelPosition() {
        int originalX = (int) arcPath.midPoint.x;
        int originalY = (int) arcPath.midPoint.y - 10;
        int x = originalX;
        int y = originalY;
        int yCount = 0;

        for (TextLabel label : weightLabel) {
            if (yCount >= 4) {
                y = originalY;
                x += 17;
                yCount = 0;
            }
            label.setPosition(x + label.getWidth() / 2 - 4, y);
            y += 10;
            yCount++;
        }
    }

    /**
     * Add weight labels to the container
     * @param container
     */
    private void addWeightLabelsToContainer(Container container) {
        for (TextLabel label : weightLabel) {
            container.add(label);
        }
    }


    /**
     * Paints the arc
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

        if (joined) {
            g2.translate(arcPath.getPoint(0).getX(), arcPath.getPoint(0).getY());
            g2.rotate(arcPath.getStartAngle() + Math.PI);
            g2.setTransform(reset);
        }

        g2.setStroke(new BasicStroke(1f));
        g2.draw(arcPath);

        g2.translate(arcPath.getPoint(arcPath.getEndIndex()).getX(), arcPath.getPoint(arcPath.getEndIndex()).getY());

        g2.rotate(model.getEndAngle());
        g2.setColor(java.awt.Color.WHITE);


        if (isSelected() && !ignoreSelection) {
            g2.setPaint(GUIConstants.SELECTION_LINE_COLOUR);
        } else {
            g2.setPaint(GUIConstants.ELEMENT_LINE_COLOUR);
        }

        arcHead.draw(g2);

        g2.transform(reset);
    }

    /**
     * Weight label listener for updating the arc weight label location if arc points change
     */
    private class WeightLabelListener implements PropertyChangeListener {

        /**
         * If the event fired is an arc point (x,y) location change then the
         * weights label location is recalculated
         * @param evt
         */
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String name = evt.getPropertyName();
            if (name.equals(ArcPoint.UPDATE_LOCATION_CHANGE_MESSAGE)) {
                updateWeights();
            }
        }
    }
}
