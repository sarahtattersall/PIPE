package pipe.views;

import pipe.actions.gui.PipeApplicationModel;
import pipe.constants.GUIConstants;
import pipe.controllers.PetriNetController;
import uk.ac.imperial.pipe.exceptions.PetriNetComponentNotFoundException;
import uk.ac.imperial.pipe.models.petrinet.Arc;
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


public class NormalArcView<S extends Connectable, T extends Connectable> extends ArcView<S, T> {
    private static final Logger LOGGER = Logger.getLogger(NormalArcView.class.getName());

    private static final String TYPE = "normal";

    private final Collection<NameLabel> weightLabel = new LinkedList<NameLabel>();

    private ArcHead arcHead = new NormalHead();

    // bidirectional arc?
    private boolean joined = false;

    public NormalArcView(Arc<S, T> model, PetriNetController controller, Container parent, MouseInputAdapter handler,
                         PipeApplicationModel applicationModel) {
        super(model, controller, parent, handler, applicationModel);
        addConnectableListener();
        addSourceTargetConnectableListener();
        for (NameLabel label : weightLabel) {
            getParent().add(label);
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


    private void addConnectableListener() {
        PropertyChangeListener listener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                String name = propertyChangeEvent.getPropertyName();
                if (name.equals(Arc.WEIGHT_CHANGE_MESSAGE)) {
                    updateWeights();
                }
            }
        };
        model.addPropertyChangeListener(listener);
    }

    @Override
    public void componentSpecificDelete() {
        for (NameLabel label : weightLabel) {
            removeLabelFromParentContainer(label);
        }
    }

    private void removeLabelFromParentContainer(NameLabel label) {
        getParent().remove(label);
    }

    @Override
    public void addToContainer(Container container) {
        updatePath();
        updateWeights();
    }

    private void updateWeights() {
        removeCurrentWeights();
        createWeightLabels();
        setWeightLabelPosition();

        addWeightLabelsToContainer(getParent());
    }

    private void removeCurrentWeights() {
        for (NameLabel name : weightLabel) {
            removeLabelFromParentContainer(name);
        }
        weightLabel.clear();
    }

    private void createWeightLabels() {
        Map<String, String> weights = model.getTokenWeights();
        for (Map.Entry<String, String> entry : weights.entrySet()) {
            String weight = entry.getValue();
            String tokenId = entry.getKey();
            NameLabel label = new NameLabel();
            label.setText(weight);
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

    protected void setWeightLabelPosition() {
        int originalX = (int) arcPath.midPoint.x;
        int originalY = (int) arcPath.midPoint.y - 10;
        int x = originalX;
        int y = originalY;
        int yCount = 0;

        for (NameLabel label : weightLabel) {
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

    private void addWeightLabelsToContainer(Container container) {
        for (NameLabel label : weightLabel) {
            container.add(label);
        }
    }

    @Override
    public String getType() {
        return TYPE;
    }


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
}
