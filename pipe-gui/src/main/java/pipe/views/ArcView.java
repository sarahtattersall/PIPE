package pipe.views;

import pipe.controllers.PetriNetController;
import pipe.gui.ApplicationSettings;
import pipe.gui.Constants;
import pipe.gui.PetriNetTab;
import pipe.gui.ZoomController;
import pipe.gui.widgets.ArcWeightEditorPanel;
import pipe.gui.widgets.EscapableDialog;
import pipe.handlers.ArcHandler;
import pipe.historyActions.HistoryItem;
import pipe.models.PipeObservable;
import pipe.models.component.Arc;
import pipe.models.component.ArcPoint;
import pipe.models.component.Connectable;
import pipe.views.viewComponents.ArcPath;
import pipe.views.viewComponents.NameLabel;

import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.*;

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
        extends AbstractPetriNetViewComponent<Arc<S, T>> implements Cloneable, Serializable, Observer {


    public PetriNetTab getTab() {
        return tab;
    }

    final protected ArcPath arcPath;

    // bounds of arc need to be grown in order to avoid clipping problems
    final int zoomGrow = 10;

    /**
     * This is a reference to the petri net tab that this arc is placed on.
     * It is needed to add ArcPoints to the petri net based on the models intermediate
     * points
     */
    protected PetriNetTab tab = null;

    // true if arc is not hidden when a bidirectional arc is used
    boolean inView = true;

    private boolean _noFunctionalWeights = true;

    private ArcPoint sourcePoint;

    private ArcPoint endPoint;

    public ArcView(Arc<S, T> model, PetriNetController controller) {
        super(model.getId(), model.getId(), 0, 0, model, controller);
        arcPath = new ArcPath(this, controller);


        addPathSourceLocation();
        addPathEndLocation();
        updatePath();
        updateBounds();
        addConnectableListener();
    }

    private void addConnectableListener() {
        addArcChangeListener();
        addSourceTargetConnectableListener();

    }

    private void addArcChangeListener() {
        PropertyChangeListener listener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                String name = propertyChangeEvent.getPropertyName();
                if (name.equals("newIntermediatePoint")) {
                    updatePath();
                    arcSpecificUpdate();
                    updateBounds();
                }  else if (name.equals("deleteIntermediatePoint")) {
                    ArcPoint point = (ArcPoint) propertyChangeEvent.getOldValue();
                    arcPath.deletePoint(point);
                    updatePath();
                    arcSpecificUpdate();
                    updateBounds();
                }
            }
        };
        model.addPropertyChangeListener(listener);
    }

    /**
     * Listens to the source/target changing position
     */
    private void addSourceTargetConnectableListener() {
        model.getSource().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                String name = propertyChangeEvent.getPropertyName();
                if (name.equals("x") || name.equals("y")) {
                    sourcePoint.setPoint(model.getStartPoint());
                    arcSpecificUpdate();
                }
            }
        });
        model.getTarget().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                String name = propertyChangeEvent.getPropertyName();
                if (name.equals("x") || name.equals("y")) {
                    endPoint.setPoint(model.getEndPoint());
                    arcSpecificUpdate();
                }
            }
        });
    }

    /**
     * Perform any arc specific addition acitons
     */
    protected abstract void arcSpecificAdd();

    @Override
    public boolean contains(int x, int y) {
        Point2D.Double point = new Point2D.Double(x + arcPath.getBounds().getX() - getComponentDrawOffset() -
                zoomGrow, y + arcPath.getBounds().getY() - getComponentDrawOffset() -
                zoomGrow);
        if (!ApplicationSettings.getApplicationView().getCurrentTab().isInAnimationMode()) {
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
    public void addedToGui() {
        // called by PetriNetTab / State viewer when adding component.
        _deleted = false;
        _markedAsDeleted = false;

        arcPath.addPointsToGui(tab);
        updateArcPosition();
        //addWeightLabelsToContainer(getParent());
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
    public void delete() {
        if (!_deleted) {
            arcSpecificDelete();

            arcPath.forceHidePoints();
            super.delete();
            _deleted = true;
        }
    }

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

    /**
     * Perform any arc specific deletion acitons
     */
    protected abstract void arcSpecificDelete();

    @Override
    public void translate(int x, int y) {
        // We don't translate an arc, we translate each selected arc point
    }

    @Override
    public void zoomUpdate(int percent) {
        _zoomPercentage = percent;
    }

    @Override
    public void addToPetriNetTab(PetriNetTab tab) {
        this.tab = tab;
        ArcHandler<S, T> arcHandler = new ArcHandler<S, T>(this, tab, this.model, petriNetController);
        addMouseListener(arcHandler);
        addMouseWheelListener(arcHandler);
        addMouseMotionListener(arcHandler);
    }

    // Steve Doubleday (Oct 2013): cascading clean up of Marking Views if Token View is disabled
    @Override
    public void update(Observable observable, Object obj) {
        if ((observable instanceof PipeObservable) && (obj == null)) {
            // if multiple cases are added, consider creating specific subclasses of Observable
            Object originalObject = ((PipeObservable) observable).getObservable();
            //            if (originalObject instanceof MarkingView) {
            //                MarkingView viewToDelete = (MarkingView) originalObject;
            //                _weight.remove(viewToDelete);
            //            }
        }
    }

    public HistoryItem setWeight(List<MarkingView> weightInput) {
        //        List<MarkingView> oldWeight = Copier.mediumCopy(_weight);
        //        checkIfFunctionalWeightExists();
        //        _weight = weightInput;
        //        return new ArcWeight(this, oldWeight, _weight);
        return null;
    }

    protected void removeLabelFromParentContainer(NameLabel label) {
        getParent().remove(label);
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

    /**
     * Repopulates the path with the models points
     */
    private void updatePath() {
        addIntermediatePoints();
        arcPath.createPath();
        if (tab != null) {
            arcPath.addPointsToGui(tab);
        }
    }

    /**
     * Loops through points in revese order adding them to the path
     * Since addPointAt inserts to the left of the index to get
     * between and the start we need to always insert left of the
     * end.
     */
    private void addIntermediatePoints() {
        List<ArcPoint> points = new ArrayList<ArcPoint>(model.getIntermediatePoints());
        for (ArcPoint arcPoint : points) {
            if (!arcPath.contains(arcPoint)) {
                arcPath.insertIntermediatePoint(arcPoint);
            }
        }
    }

    private void addPathEndLocation() {
        Point2D.Double targetPoint = model.getEndPoint();
        endPoint = new ArcPoint(zoom(targetPoint), false);
        arcPath.addPoint(endPoint);
    }

    private void addPathSourceLocation() {
        Point2D.Double startPoint = model.getStartPoint();
        sourcePoint = new ArcPoint(zoom(startPoint), false);
        arcPath.addPoint(sourcePoint);
    }

    /**
     * Updates the bounding box of the arc component based on the arcs bounds
     */
    public void updateBounds() {
        bounds = arcPath.getBounds();
        bounds.grow(getComponentDrawOffset() + zoomGrow, getComponentDrawOffset() + zoomGrow);
        setBounds(bounds);
    }

    public ArcPath getArcPath() {
        return arcPath;
    }

    public void setPathToTransitionAngle(int angle) {
        arcPath.set_transitionAngle(angle);
    }

    public HistoryItem split(Point2D.Double mouseposition) {
        //        ArcPathPoint newPoint = arcPath.splitSegment(mouseposition);
        //        return new AddArcPathPoint(getModel(), newPoint);
        return null;
    }

    public abstract String getType();

    public boolean inView() {
        return inView;
    }

    //TODO: DELETE
    public TransitionView getTransition() {
        //        if (getTarget() instanceof TransitionView) {
        //            return (TransitionView) getTarget();
        //        } else {
        //            return (TransitionView) getSource();
        //        }
        return null;
    }

    public void removeFromView() {
        if (getParent() != null) {
            arcSpecificDelete();
        }
        arcPath.forceHidePoints();
        removeFromContainer();
    }

    public void addToView(PetriNetTab view) {
        if (getParent() != null) {
            arcSpecificUpdate();
        }
        arcPath.showPoints();
        view.add(this);
    }

    /**
     * Perform any updates specific to the arc type
     * E.g. NormalArc should show weights
     */
    public abstract void arcSpecificUpdate();

    public Point2D.Double zoom(Point2D.Double point) {
        return new Point2D.Double(ZoomController.getZoomedValue(point.x, _zoomPercentage),
                ZoomController.getZoomedValue(point.y, _zoomPercentage));
    }

    public void setZoom(int percent) {
        _zoomPercentage = percent;
    }

    public void showEditor() {
        // Build interface
        EscapableDialog guiDialog = new EscapableDialog(ApplicationSettings.getApplicationView(), "PIPE2", true);

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

    //TODO: DELETE
    public List<MarkingView> getWeightSimple() {
        return null;
    }

    public boolean isWeightFunctional() {
        return !_noFunctionalWeights;
    }

    //TODO DELETE:
    public List<MarkingView> getWeight() {
        return null;
    }

    protected void updateHistory(
            HistoryItem historyItem) { // Steve Doubleday:  changed from addEdit to avoid NPE when HistoryManager edits is list of nulls

        petriNetController.getHistoryManager().addNewEdit(historyItem);
    }


}
