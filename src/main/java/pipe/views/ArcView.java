package pipe.views;

import pipe.controllers.PetriNetController;
import pipe.gui.ApplicationSettings;
import pipe.gui.Constants;
import pipe.gui.PetriNetTab;
import pipe.gui.ZoomController;
import pipe.gui.widgets.ArcWeightEditorPanel;
import pipe.gui.widgets.EscapableDialog;
import pipe.handlers.ArcHandler;
import pipe.historyActions.AddArcPathPoint;
import pipe.historyActions.HistoryItem;
import pipe.models.PetriNet;
import pipe.models.PipeObservable;
import pipe.models.component.Arc;
import pipe.models.component.ArcPoint;
import pipe.models.interfaces.IObserver;
import pipe.views.viewComponents.ArcPath;
import pipe.views.viewComponents.ArcPathPoint;
import pipe.views.viewComponents.NameLabel;

import javax.swing.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public abstract class ArcView<T extends Arc> extends PetriNetViewComponent<T>
        implements Cloneable, IObserver, Serializable, Observer {


    final protected Path2D path = new Path2D.Double();
    final protected ArcPath arcPath = new ArcPath(this);

    // true if arc is not hidden when a bidirectional arc is used
    boolean inView = true;

    // bounds of arc need to be grown in order to avoid clipping problems
    final int zoomGrow = 10;
    private boolean _noFunctionalWeights = true;

    public ArcView(T model,
            PetriNetController controller) {
        super(model.getId(), model.getId(), 0, 0, model, controller);


        updatePath();
        updateBounds();
    }




    ArcView(ConnectableView newSource) {
        arcPath.addPoint();
        arcPath.addPoint();
        arcPath.createPath();
    }

    ArcView() {
        super();
    }

    /**
     * Perform any updates specific to the arc type
     * E.g. NormalArc should show weights
     */
    protected abstract void arcSpecificUpdate();


    /**
     * Perform any arc specific deletion acitons
     */
    protected abstract void arcSpecificDelete();

    /**
     * Perform any arc specific addition acitons
     */
    protected abstract void arcSpecificAdd();

    @Override
    public boolean contains(int x, int y) {
        Point2D.Double point = new Point2D.Double(
                x + arcPath.getBounds().getX() - getComponentDrawOffset() -
                        zoomGrow,
                y + arcPath.getBounds().getY() - getComponentDrawOffset() -
                        zoomGrow);
        if (!ApplicationSettings.getApplicationView().getCurrentTab()
                .isInAnimationMode()) {
            if (arcPath.proximityContains(point) || isSelected()) {
                // show also if Arc itself selected
                arcPath.showPoints();
            } else {
                //TODO: HIDEPOINTS
//                arcPath.hidePoints();
            }
        }

        return arcPath.contains(point);
    }

    @Override
    public void addedToGui() {
        // called by PetriNetTab / State viewer when adding component.
        _deleted = false;
        _markedAsDeleted = false;

        if (getParent() instanceof PetriNetTab) {
            arcPath.addPointsToGui((PetriNetTab) getParent());
        } else {
            arcPath.addPointsToGui((JLayeredPane) getParent());
        }
        updateArcPosition();
        update();
        //addWeightLabelsToContainer(getParent());
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
    public String getId() {
        return model.getId();
    }

    @Override
    public String getName() {
        return getId();
    }


    @Override
    public int getLayerOffset() {
        return Constants.ARC_LAYER_OFFSET;
    }

    @Override
    public void translate(int x, int y) {
        // We don't translate an arc, we translate each selected arc point
    }

    @Override
    public void zoomUpdate(int percent) {
        _zoomPercentage = percent;
        update();
    }

    /**
     * Method to clone an Arc object
     */
    @Override
    public PetriNetViewComponent clone() {
        return super.clone();
    }


    @Override
    public void addToPetriNetTab(PetriNetTab tab) {
        ArcHandler arcHandler =
                new ArcHandler(this, tab, this.model, petriNetController);
        addMouseListener(arcHandler);
        addMouseWheelListener(arcHandler);
        addMouseMotionListener(arcHandler);
    }

    @Override
    public void update() {

        updatePath();
        arcSpecificUpdate();
        updateBounds();
        repaint();
    }

    // Steve Doubleday (Oct 2013): cascading clean up of Marking Views if Token View is disabled
    @Override
    public void update(Observable observable, Object obj) {
        if ((observable instanceof PipeObservable) && (obj == null)) {
            // if multiple cases are added, consider creating specific subclasses of Observable
            Object originalObject =
                    ((PipeObservable) observable).getObservable();
//            if (originalObject instanceof MarkingView) {
//                MarkingView viewToDelete = (MarkingView) originalObject;
//                _weight.remove(viewToDelete);
//            }
        }
    }



    //TODO: DELETE
    void setSource(ConnectableView sourceInput) {
        throw new RuntimeException("Should be setting models source");
    }

    //TODO: DELETE
    public void setTarget(ConnectableView targetInput) {
        throw new RuntimeException("Should be setting models target");
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
    public ConnectableView getSource() {
        return null;
    }


    //TODO: DELETE AND REPOINT METHODS AT THE MODEL VERSION
    public ConnectableView getTarget() {
        return null;
    }

    public int getSimpleWeight() {
        return 1;
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

    /**
     * Repopulates the path with the models points
     */
    private void updatePath() {
        arcPath.clear();
        addPathSourceLocation();
        addIntermediatePoints();
        addPathEndLocation();
        arcPath.createPath();

        //TODO: PASS IN INSTEAD
        PipeApplicationView view = ApplicationSettings.getApplicationView();
        PetriNetTab tab = view.getCurrentTab();
        arcPath.addPointsToGui(tab);
    }

    private void addIntermediatePoints() {
        List<ArcPoint> points = model.getPoints();
        for (ArcPoint arcPoint : points) {
            arcPath.addPoint(arcPoint);
        }
    }

    private void addPathEndLocation() {
        Point2D.Double endPoint = model.getEndPoint();
        arcPath.addPoint(new ArcPoint(zoom(endPoint), false));
    }

    private void addPathSourceLocation() {
        Point2D.Double startPoint = model.getStartPoint();
        arcPath.addPoint(new ArcPoint(zoom(startPoint), false));
    }

    /**
     * Updates the bounding box of the arc component based on the arcs bounds
     */
    void updateBounds() {
        bounds = arcPath.getBounds();
        bounds.grow(getComponentDrawOffset() + zoomGrow,
                getComponentDrawOffset() + zoomGrow);
        setBounds(bounds);
    }

    public ArcPath getArcPath() {
        return arcPath;
    }


    public void setPathToTransitionAngle(int angle) {
        arcPath.set_transitionAngle(angle);
    }

    public HistoryItem split(Point2D.Double mouseposition) {
        ArcPathPoint newPoint = arcPath.splitSegment(mouseposition);
        return new AddArcPathPoint(this, newPoint);
    }

    public abstract String getType();

    public boolean inView() {
        return inView;
    }

    public TransitionView getTransition() {
        if (getTarget() instanceof TransitionView) {
            return (TransitionView) getTarget();
        } else {
            return (TransitionView) getSource();
        }
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



    public Point2D.Double zoom(Point2D.Double point) {
        return new Point2D.Double(ZoomController.getZoomedValue(point.x, _zoomPercentage),
                ZoomController.getZoomedValue(point.y, _zoomPercentage));
    }

    public void setZoom(int percent) {
        _zoomPercentage = percent;
    }

    public void undelete(PetriNetView model, PetriNetTab view) {
        if (this.isDeleted()) {
            model.addPetriNetObject(this);
            view.add(this);
            getSource().addOutbound(this);
            getTarget().addInbound(this);
        }
    }



    public void showEditor() {
        // Build interface
        EscapableDialog guiDialog =
                new EscapableDialog(ApplicationSettings.getApplicationView(),
                        "PIPE2", true);

        ArcWeightEditorPanel arcWeightEditor =
                new ArcWeightEditorPanel(guiDialog.getRootPane(),
                        petriNetController, petriNetController.getArcController(this.model));

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
