package pipe.views;

import pipe.controllers.PetriNetController;
import pipe.gui.ApplicationSettings;
import pipe.gui.Constants;
import pipe.gui.PetriNetTab;
import pipe.gui.widgets.ArcWeightEditorPanel;
import pipe.gui.widgets.EscapableDialog;
import pipe.handlers.ArcHandler;
import pipe.views.viewComponents.ArcPath;
import uk.ac.imperial.pipe.models.petrinet.Arc;
import uk.ac.imperial.pipe.models.petrinet.ArcPoint;
import uk.ac.imperial.pipe.models.petrinet.Connectable;

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
     * This is a reference to the petri net tab that this arc is placed on.
     * It is needed to add ArcPoints to the petri net based on the models intermediate
     * points
     */
    protected PetriNetTab tab = null;

    /**
     * true if arc is not hidden when a bidirectional arc is used
     */
    protected boolean inView = true;

    public ArcView(Arc<S, T> model, PetriNetController controller) {
        super(model.getId(), model, controller);
        arcPath = new ArcPath(this, controller);

        updatePath();
        updateBounds();
        registerModelListeners();
        tab = controller.getPetriNetTab();
    }

    /**
     * Registers listeners for the arc model and it's source and target models
     */
    private void registerModelListeners() {
        addArcChangeListener();
        addSourceTargetConnectableListener();
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
                    arcSpecificUpdate();
                }
            }
        };
        model.getSource().addPropertyChangeListener(changeListener);
        model.getTarget().addPropertyChangeListener(changeListener);
    }

    /**
     * Perform any updates specific to the arc type
     * E.g. NormalArc should show weights
     */
    public abstract void arcSpecificUpdate();

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
     * Updates all arc points displayed based on their positions
     * in the model
     */
    private void updateAllPoints() {
        updatePath();
        arcSpecificUpdate();
        updateBounds();
    }

    /**
     * Repopulates the path with the models points
     */
    private void updatePath() {
        addIntermediatePoints();
        arcPath.createPath();
        if (tab != null) {
            arcPath.addPointsToGui(tab);
        }
        repaint();
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
     * Updates the bounding box of the arc component based on the arcs bounds
     */
    public final void updateBounds() {
        bounds = arcPath.getBounds();
        bounds.grow(getComponentDrawOffset() + ZOOM_GROW, getComponentDrawOffset() + ZOOM_GROW);
        setBounds(bounds);
    }

    public PetriNetTab getTab() {
        return tab;
    }

    /**
     * Perform any arc specific addition acitons
     */
    protected abstract void arcSpecificAdd();

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

    @Override
    public void delete() {
        if (!_deleted) {
            arcSpecificDelete();

            arcPath.forceHidePoints();
            super.delete();
            _deleted = true;
        }
    }

    /**
     * Perform any arc specific deletion acitons
     */
    protected abstract void arcSpecificDelete();

    @Override
    public int getLayerOffset() {
        return Constants.ARC_LAYER_OFFSET;
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
    public void translate(int x, int y) {
        // We don't translate an arc, we translate each selected arc point
    }

    @Override
    public void addToPetriNetTab(PetriNetTab tab) {
        this.tab = tab;
        updatePath();
        ArcHandler<S, T> arcHandler = new ArcHandler<>(this, tab, this.model, petriNetController);
        addMouseListener(arcHandler);
        addMouseWheelListener(arcHandler);
        addMouseMotionListener(arcHandler);
    }

    //TODO: DELETE AND REPOINT METHODS AT THE MODEL VERSION
    public ConnectableView<Connectable> getSource() {
        return null;
    }

    //TODO: DELETE
    void setSource(ConnectableView<?> sourceInput) {
        throw new RuntimeException("Should be setting models source");
    }

    //TODO: DELETE AND REPOINT METHODS AT THE MODEL VERSION
    public ConnectableView<Connectable> getTarget() {
        return null;
    }

    //TODO: DELETE
    public void setTarget(ConnectableView<?> targetInput) {
        throw new RuntimeException("Should be setting models target");
    }

    public int getSimpleWeight() {
        return 1;
    }

    public ArcPath getArcPath() {
        return arcPath;
    }

    public abstract String getType();

    public boolean inView() {
        return inView;
    }

    public void removeFromView() {
        if (getParent() != null) {
            arcSpecificDelete();
        }
        arcPath.forceHidePoints();
        removeFromContainer();
    }

    public void showEditor() {
        // Build interface
        EscapableDialog guiDialog = new EscapableDialog(ApplicationSettings.getApplicationView(), "PIPE", true);

        ArcWeightEditorPanel arcWeightEditor = new ArcWeightEditorPanel(guiDialog.getRootPane(), petriNetController,
                petriNetController.getArcController(this.model));

        guiDialog.add(arcWeightEditor);

        guiDialog.getRootPane().setDefaultButton(null);

        guiDialog.setResizable(false);

        // Make window fit contents' preferred size
        guiDialog.pack();

        // Move window to the middle of the screen
        guiDialog.setLocationRelativeTo(null);

        guiDialog.setVisible(true);

        guiDialog.dispose();
    }

    // Accessor function to check whether or not the Arc is tagged
    public boolean isTagged() {
        return false;
    }
}
