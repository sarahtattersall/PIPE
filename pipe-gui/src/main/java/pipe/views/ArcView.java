package pipe.views;

import pipe.actions.gui.PipeApplicationModel;
import pipe.controllers.PetriNetController;
import uk.ac.imperial.pipe.models.petrinet.Arc;
import uk.ac.imperial.pipe.models.petrinet.ArcPoint;
import uk.ac.imperial.pipe.models.petrinet.Connectable;

import javax.swing.event.MouseInputAdapter;
import java.awt.Container;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * This class contains the common methods for different arc types.
 * <p/>
 * At present when arc points are modified the whole arc is redrawn. At some point
 * in the future it would be good if they're more dynamic than this.
 *
 * @param <S> Model source type
 * @param <T> Model target type
 */
public abstract class ArcView<S extends Connectable, T extends Connectable>
        extends AbstractPetriNetViewComponent<Arc<S, T>> implements Cloneable {
    /**
     * Bounds of arc need to be grown in order to avoid clipping problems.
     * This value achieves it.
     */
    protected static final int ZOOM_GROW = 10;

    /**
     * Actual visible path
     */
    final protected ArcPath arcPath;

    /**
     * true if arc is not hidden when a bidirectional arc is used
     */
    protected boolean inView = true;

    public ArcView(Arc<S, T> model, PetriNetController controller, Container parent,
                   MouseInputAdapter arcHandler,
                   PipeApplicationModel applicationModel) {
        super(model.getId(), model, controller, parent);
        arcPath = new ArcPath(this, controller, applicationModel);

        updatePath();
        updateBounds();
        registerModelListeners();
        setMouseListener(arcHandler);
    }

    /**
     * Repopulates the path with the models points
     */
    private void updatePath() {
        addIntermediatePoints();
        arcPath.createPath();
        arcPath.addPointsToGui(getParent());
        repaint();
    }

    /**
     * Updates the bounding box of the arc component based on the arcs bounds
     */
    public final void updateBounds() {
        bounds = arcPath.getBounds();
        bounds.grow(getComponentDrawOffset() + ZOOM_GROW, getComponentDrawOffset() + ZOOM_GROW);
        setBounds(bounds);
    }

    /**
     * Registers listeners for the arc model and it's source and target models
     */
    private void registerModelListeners() {
        addArcChangeListener();
        addSourceTargetConnectableListener();
    }

    public void setMouseListener(MouseInputAdapter arcHandler) {
        addMouseListener(arcHandler);
        addMouseWheelListener(arcHandler);
        addMouseMotionListener(arcHandler);
    }

    /**
     * Loops through points adding them to the path if they don't already
     * exist
     */
    private void addIntermediatePoints() {
        int index = 0;
        for (ArcPoint arcPoint : model.getArcPoints()) {
            if (!arcPath.contains(arcPoint)) {
                arcPath.insertIntermediatePoint(arcPoint, index);
            }
            index++;
        }
    }

    /**
     * Listens for intermediate points being added/deleted
     * Will call a redraw of the existing points
     */
    private void addArcChangeListener() {
        PropertyChangeListener listener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                String name = propertyChangeEvent.getPropertyName();
                if (name.equals(Arc.NEW_INTERMEDIATE_POINT_CHANGE_MESSAGE)) {
                    updateAllPoints();
                } else if (name.equals(Arc.DELETE_INTERMEDIATE_POINT_CHANGE_MESSAGE)) {
                    ArcPoint point = (ArcPoint) propertyChangeEvent.getOldValue();
                    arcPath.deletePoint(point);
                    updateAllPoints();
                }
            }
        };
        model.addPropertyChangeListener(listener);
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
                }
            }
        };
        model.getSource().addPropertyChangeListener(changeListener);
        model.getTarget().addPropertyChangeListener(changeListener);
    }

    /**
     * Updates all arc points displayed based on their positions
     * in the model
     */
    private void updateAllPoints() {
        updatePath();
        updateBounds();
    }


    @Override
    public boolean contains(int x, int y) {
        Point2D.Double point = new Point2D.Double(x + arcPath.getBounds().getX() - getComponentDrawOffset() -
                ZOOM_GROW, y + arcPath.getBounds().getY() - getComponentDrawOffset() -
                ZOOM_GROW);
        if (!petriNetController.isInAnimationMode()) {
            if (arcPath.proximityContains(point) || isSelected()) {
                // show also if Arc itself selected
                arcPath.showPoints();
            } else {
                arcPath.hidePoints();
            }
        }

        return arcPath.contains(point);
    }

    @Override
    public String getName() {
        return getId();
    }

    @Override
    public String getId() {
        return model.getId();
    }

    /**
     * Method to clone an Arc object
     */
    @Override
    public AbstractPetriNetViewComponent clone() {
        return super.clone();
    }

    //TODO: DELETE
    public void updateArcPosition() {
        //Pair<Point2D.Double, Point2D.Double> points = getArcStartAndEnd();
        //        addPathSourceLocation(points.first.x, points.first.y);
        //        setTargetLocation(points.second.x, points.second.y);
        //        if (_source != null) {
        //            _source.updateEndPoint(this);
        //        }
        //        if (_target != null) {
        //            _target.updateEndPoint(this);
        //        }
        //        arcPath.createPath();
    }

    @Override
    public void addToContainer(Container container) {
        updatePath();
    }

    public ArcPath getArcPath() {
        return arcPath;
    }

    public abstract String getType();

    public void removeFromView() {
        arcPath.forceHidePoints();
        removeFromContainer();
    }


    // Accessor function to check whether or not the Arc is tagged
    public boolean isTagged() {
        return false;
    }
}
