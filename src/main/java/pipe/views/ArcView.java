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
import pipe.models.PipeObservable;
import pipe.models.component.Arc;
import pipe.models.component.Token;
import pipe.models.interfaces.IObserver;
import pipe.views.viewComponents.ArcPath;
import pipe.views.viewComponents.ArcPathPoint;
import pipe.views.viewComponents.NameLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public abstract class ArcView<T extends Arc> extends PetriNetViewComponent<T>
        implements Cloneable, IObserver, Serializable, Observer {

    List<NameLabel> weightLabel = new LinkedList<NameLabel>();
    List<MarkingView> _weight = new LinkedList<MarkingView>();

    final ArcPath myPath = new ArcPath(this);

    // true if arc is not hidden when a bidirectional arc is used
    boolean inView = true;

    // bounds of arc need to be grown in order to avoid clipping problems
    final int zoomGrow = 10;
    private boolean _noFunctionalWeights = true;

    ArcView(T model,
            PetriNetController controller) {
        super(model.getId(), model.getId(), 0, 0, model, controller);

        Point2D.Double startPoint = model.getStartPoint();
        myPath.addPoint(startPoint.getX(), startPoint.getY(),
                ArcPathPoint.STRAIGHT);

        Point2D.Double endPoint = model.getEndPoint();
        myPath.addPoint(endPoint.getX(), endPoint.getY(),
                ArcPathPoint.STRAIGHT);
        myPath.createPath();

        updateBounds();
    }

    private double zoom(double x) {
        return ZoomController.getZoomedValue(x, _zoomPercentage);
    }

    protected Point2D.Double zoomPoint(Point2D.Double point) {
        double x = zoom(point.x);
        double y = zoom(point.y);
        return new Point2D.Double(x, y);
    }

    ArcView(ConnectableView newSource) {
        myPath.addPoint();
        myPath.addPoint();
        myPath.createPath();
    }

    ArcView() {
        super();
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

    public void setWeightLabelPosition() {
        int originalX = (int) (myPath.midPoint.x);
        int originalY = (int) (myPath.midPoint.y) - 10;
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

    protected void removeLabelFromParentContainer(NameLabel label) {
        getParent().remove(label);
    }

    public String getId() {
        return model.getId();
    }

    public String getName() {
        return getId();
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
        //        setSourceLocation(points.first.x, points.first.y);
        //        setTargetLocation(points.second.x, points.second.y);
//        if (_source != null) {
//            _source.updateEndPoint(this);
//        }
//        if (_target != null) {
//            _target.updateEndPoint(this);
//        }
//        myPath.createPath();
    }


    public void setEndPoint(boolean type) {
        Point2D.Double endPoint = model.getEndPoint();
        myPath.setPointLocation(myPath.getEndIndex(), endPoint);
        myPath.setPointType(myPath.getEndIndex(), type);
        updateArcPosition();
    }


    //TODO: DELETE
    public void setTargetLocation() {
        Point2D.Double endPoint = model.getEndPoint();
        myPath.setPointLocation(myPath.getEndIndex(), endPoint);
        myPath.createPath();
        updateBounds();
        repaint();
    }

    public void setSourceLocation() {
        Point2D.Double startPoint = model.getStartPoint();
        myPath.setPointLocation(0, startPoint);
        myPath.createPath();
        updateBounds();
        repaint();
    }

    /**
     * Updates the bounding box of the arc component based on the arcs bounds
     */
    void updateBounds() {
        bounds = myPath.getBounds();
        bounds.grow(getComponentDrawOffset() + zoomGrow,
                getComponentDrawOffset() + zoomGrow);
        setBounds(bounds);
    }

    public ArcPath getArcPath() {
        return myPath;
    }

    public boolean contains(int x, int y) {
        Point2D.Double point = new Point2D.Double(
                x + myPath.getBounds().getX() - getComponentDrawOffset() -
                        zoomGrow,
                y + myPath.getBounds().getY() - getComponentDrawOffset() -
                        zoomGrow);
        if (!ApplicationSettings.getApplicationView().getCurrentTab()
                .isInAnimationMode()) {
            if (myPath.proximityContains(point) || isSelected()) {
                // show also if Arc itself selected
                myPath.showPoints();
            }
            else {
                //TODO: HIDEPOINTS
//                myPath.hidePoints();
            }
        }

        return myPath.contains(point);
    }

    public void addedToGui() {
        // called by PetriNetTab / State viewer when adding component.
        _deleted = false;
        _markedAsDeleted = false;

        if (getParent() instanceof PetriNetTab) {
            myPath.addPointsToGui((PetriNetTab) getParent());
        }
        else {
            myPath.addPointsToGui((JLayeredPane) getParent());
        }
        updateArcPosition();
        update();
        //addWeightLabelsToContainer(getParent());
    }

    public void delete() {
        if (!_deleted) {
            for (NameLabel label : weightLabel) {
                removeLabelFromParentContainer(label);
            }
            myPath.forceHidePoints();
            super.delete();
            _deleted = true;
        }
    }

    public void setPathToTransitionAngle(int angle) {
        myPath.set_transitionAngle(angle);
    }

    public HistoryItem split(Point2D.Float mouseposition) {
        ArcPathPoint newPoint = myPath.splitSegment(mouseposition);
        return new AddArcPathPoint(this, newPoint);
    }

    public abstract String getType();

    public boolean inView() {
        return inView;
    }

    public TransitionView getTransition() {
        if (getTarget() instanceof TransitionView) {
            return (TransitionView) getTarget();
        }
        else {
            return (TransitionView) getSource();
        }
    }

    public void removeFromView() {
        if (getParent() != null) {
            for (NameLabel label : weightLabel) {
                removeLabelFromParentContainer(label);
            }
        }
        myPath.forceHidePoints();
        removeFromContainer();
    }

    public void addToView(PetriNetTab view) {
        if (getParent() != null) {
            for (NameLabel label : weightLabel) {
                getParent().add(label);
            }
        }
        myPath.showPoints();
        view.add(this);
    }

    public int getLayerOffset() {
        return Constants.ARC_LAYER_OFFSET;
    }

    public void translate(int x, int y) {
        // We don't translate an arc, we translate each selected arc point
    }

    public void zoomUpdate(int percent) {
        _zoomPercentage = percent;
        update();
        for (NameLabel label : weightLabel) {
            label.zoomUpdate(percent);
            label.updateSize();
        }
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

    /**
     * Method to clone an Arc object
     */
    public PetriNetViewComponent clone() {
        return super.clone();
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

    public List<MarkingView> getWeightSimple() {
        return _weight;
    }

    public boolean isWeightFunctional() {
        return !_noFunctionalWeights;
    }

    public List<MarkingView> getConstantWeight() {
        if (_noFunctionalWeights) {
            return _weight;
        }
        for (int i = 0; i < _weight.size(); i++) {
            _weight.get(i)
                    .setCurrentMarking(_weight.get(i).getCurrentMarking() + "");
        }
        return _weight;
    }

    public List<MarkingView> getWeight() {
        return _weight;
    }

    public int getWeightOfTokenClass(String id) {
        if (_weight != null) {
            for (MarkingView m : _weight) {
                if (m.getToken().getID().equals(id)) {
                    if (m.getCurrentMarking() == -1) {
                        JOptionPane.showMessageDialog(null,
                                "Error in weight expression. Please make sure\r\n it is an integer");
                    }
                    if (m.getCurrentMarking() == 0) {
                        return 1;
                    }
                    return m.getCurrentMarking();
                }
            }
        }

        return 0;
    }

    public String getWeightFunctionOfTokenClass(String id) {
        if (_weight != null) {
            for (MarkingView m : _weight) {
                if (m.getToken().getID().equals(id)) {
                    return m.getCurrentFunctionalMarking();
                }
            }
        }
        return "";
    }

    public void setWeightFunctionByID(String id, String func) {
        if (_weight != null) {
            for (MarkingView m : _weight) {
                if (m.getToken().getID().equals(id)) {
                    m.setCurrentMarking(func);
                }
            }
        }
        checkIfFunctionalWeightExists();
    }

    public void checkIfFunctionalWeightExists() {
        if (_weight != null) {
            for (MarkingView m : _weight) {
                try {
                    Integer.parseInt(m.getCurrentFunctionalMarking());
                    _noFunctionalWeights = true;
                } catch (Exception e) {
                    _noFunctionalWeights = false;
                    return;
                }

            }
        }
    }

    //TODO determine which lists really need to be updated, and remove the argument.
    public void addThisAsObserverToWeight(List<MarkingView> weights) {
        for (MarkingView markingView : weights) {
            markingView.addObserver(this);
        }
    }

    // Steve Doubleday (Oct 2013): cascading clean up of Marking Views if Token View is disabled
    @Override
    public void update(Observable observable, Object obj) {
        if ((observable instanceof PipeObservable) && (obj == null)) {
            // if multiple cases are added, consider creating specific subclasses of Observable
            Object originalObject =
                    ((PipeObservable) observable).getObservable();
            if (originalObject instanceof MarkingView) {
                MarkingView viewToDelete = (MarkingView) originalObject;
                _weight.remove(viewToDelete);
                HistoryItem historyItem = this.setWeight(_weight);
                updateHistory(historyItem);
            }
        }
    }

    protected void updateHistory(
            HistoryItem historyItem) { // Steve Doubleday:  changed from addEdit to avoid NPE when HistoryManager edits is list of nulls

        petriNetController.getHistoryManager().addNewEdit(historyItem);
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
        setSourceLocation();
        //TODO: THIS FALSE IS MEANT TO BE 'SHIFT UP'
        setEndPoint(false);
        updateWeights();
        updateBounds();
        repaint();
    }

    private void updateWeights() {

        removeCurrentWeights();
        createWeightLabels();
        setWeightLabelPosition();

        Container parent = getParent();
        if (parent != null) {
            addWeightLabelsToContainer(parent);
        }
    }

    private void removeCurrentWeights() {
        for (NameLabel name : weightLabel) {
            removeLabelFromParentContainer(name);
        }
        weightLabel.clear();
    }

    private void createWeightLabels() {
        final Map<Token, String> weights = model.getTokenWeights();
        for (Map.Entry<Token, String> entry : weights.entrySet()) {
            Token token = entry.getKey();
            String weight = entry.getValue();

            NameLabel label = new NameLabel(_zoomPercentage);
            label.setText(weight);
            label.setColor(token.getColor());
            label.updateSize();
            weightLabel.add(label);
        }
    }

    private void addWeightLabelsToContainer(Container container) {
        for (NameLabel label : weightLabel) {
            container.add(label);
        }
    }
}
