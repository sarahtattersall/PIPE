package pipe.views;

import pipe.controllers.PetriNetController;
import pipe.gui.*;
import pipe.gui.widgets.EscapableDialog;
import pipe.gui.widgets.PlaceEditorPanel;
import pipe.handlers.ConnectableHandler;
import pipe.handlers.LabelHandler;
import pipe.handlers.PlaceHandler;
import pipe.historyActions.HistoryItem;
import pipe.models.PipeObservable;
import pipe.models.component.Place;
import pipe.models.component.Token;
import pipe.utilities.Copier;
import pipe.views.builder.TokenViewBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.io.Serializable;
import java.util.*;
import java.util.List;

// Steve Doubleday (Oct 2013): added as Observer of changes to MarkingViews; refactored to simplify testing
public class PlaceView extends ConnectableView<Place> implements Serializable, Observer {

    //transferred
    private List<MarkingView> _initialMarkingView = new LinkedList<MarkingView>();
    //transferred
    private List<MarkingView> _currentMarkingView = new LinkedList<MarkingView>();

    //transferred
    private Integer totalMarking = 0;

    private final Ellipse2D.Double place;
    private final Shape proximityPlace;

    //transferred
    private TokenView _activeTokenView;
    private List<MarkingView> initBackUp;
    private List<MarkingView> currentBackUp;

    public PlaceView(double positionXInput, double positionYInput) {
        //MODEL
        super(new Place("", ""));
        place = new Ellipse2D.Double(0, 0, model.getWidth(), model.getWidth());
        proximityPlace =
                (new BasicStroke(Constants.PLACE_TRANSITION_PROXIMITY_RADIUS)).createStrokedShape(place);
    }


    public PlaceView(String idInput, String nameInput,
            LinkedList<MarkingView> initialMarkingViewInput, Place model, PetriNetController controller) {
        //MODEL
        super(idInput, nameInput, model.getX() + model.getNameXOffset(), model.getY() + model.getNameYOffset(),
               model, controller);
        _initialMarkingView = Copier.mediumCopy(initialMarkingViewInput);
        _currentMarkingView = Copier.mediumCopy(initialMarkingViewInput);
        totalMarking = getTotalMarking();
        setId(model.getId());
        place = new Ellipse2D.Double(0, 0, model.getWidth(), model.getWidth());
        proximityPlace =
                (new BasicStroke(Constants.PLACE_TRANSITION_PROXIMITY_RADIUS)).createStrokedShape(place);
        updateDisplayTokens();
    }

    private void updateDisplayTokens() {
        removeExistingTokens();

        Map<Token, Integer> tokenCounts = model.getTokenCounts();
        for (Map.Entry<Token, Integer> entry : tokenCounts.entrySet()) {
            Token token = entry.getKey();
            Integer count = entry.getValue();

            TokenViewBuilder builder = new TokenViewBuilder(token);
            TokenView tokenView = builder.build();
            MarkingView markingView = new MarkingView(tokenView, count);
            markingView.addObserver(this);
            _currentMarkingView.add(markingView);
        }
    }

    private void removeExistingTokens() {
        for (MarkingView view : _currentMarkingView) {
            //TODO: THIS NEEDS TO BE DONE IN A LESS HACKY WAY
            this.getParent().remove(view);
        }
        _currentMarkingView.clear();
    }

    /**
     * Create Petri-Net Place object returns the position of the element in the
     * list "markings" where the token class with this ID is.
     *
     * @param id
     * @param markingViews
     * @return
     */
//transferred
    private int getMarkingListPos(String id, List<MarkingView> markingViews) {
        int size = markingViews.size();
        for (int i = 0; i < size; i++) {
            if (markingViews.get(i).getToken().getID().equals(id)) {
                return i;
            }
        }
        // marking with such an ID does not exist
        return -1;
    }

    /**
     * Used to quickly calculate the total marking of this place and sums up the
     * marking of each token class.
     *
     * @return
     */
    public int getTotalMarking() {

        int size = _currentMarkingView.size();
        int totalMarking = 0;
        for (MarkingView a_currentMarkingView : _currentMarkingView) {
            totalMarking += a_currentMarkingView.getCurrentMarking();
        }
        return totalMarking;
    }

    public TokenView getActiveTokenView() {
        return _activeTokenView;
    }


    public void setActiveTokenView(TokenView tokenView) {
        this._activeTokenView = tokenView;
        int markingListPos = getMarkingListPos(tokenView.getID(), _currentMarkingView);
        if (markingListPos == -1) {
            MarkingView m = new MarkingView(tokenView, 0);
            m.addObserver(this);
            //_currentMarkingView.add(m);
        }
    }

    public PlaceView paste(double x, double y, boolean fromAnotherView, PetriNetView model) {
        PlaceView copy =
                new PlaceView((double) Grid.getModifiedX(x + this.getX() + Constants.PLACE_TRANSITION_HEIGHT / 2),
                        (double) Grid.getModifiedY(y + this.getY() + Constants.PLACE_TRANSITION_HEIGHT / 2));

        String newName = this._nameLabel.getName() + "(" + this.getCopyNumber() + ")";
        boolean properName = false;

        while (!properName) {
            if (model.checkPlaceIDAvailability(newName)) {
                copy._nameLabel.setName(newName);
                properName = true;
            } else {
                newName = newName + "'";
            }
        }
        this.newCopy(copy);
        copy._attributesVisible = this._attributesVisible;
        copy._initialMarkingView = Copier.mediumCopy(this._initialMarkingView);
        copy.totalMarking = this.totalMarking;
        copy.update();
        return copy;
    }

    public PlaceView copy() {
        PlaceView copy = new PlaceView((double) ZoomController.getUnzoomedValue(this.getX(), _zoomPercentage),
                (double) ZoomController.getUnzoomedValue(this.getY(), _zoomPercentage));
        copy._nameLabel.setName(this.getName());
        copy._attributesVisible = this._attributesVisible;
        copy._initialMarkingView = Copier.mediumCopy(this._initialMarkingView);
        copy.totalMarking = this.totalMarking;
        copy.setOriginal(this);
        return copy;
    }

    /**
     * Paints the Place component taking into account the n q12[umber of tokens from
     * the storeCurrentMarking
     *
     * @param canvas The Graphics object onto which the Place is drawn.
     */
    public void paintComponent(Graphics canvas) {
        super.paintComponent(canvas);
        Graphics2D canvas2D = (Graphics2D) canvas;

        Insets insets = getInsets();

        if (hasCapacity()) {
            canvas2D.setStroke(new BasicStroke(2.0f));
            setToolTipText("k = " + this.getCapacity());
        } else {
            canvas2D.setStroke(new BasicStroke(1.0f));
            setToolTipText("k = \u221E");
        }
        canvas2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (_selected && !_ignoreSelection) {
            canvas2D.setColor(Constants.SELECTION_FILL_COLOUR);
        } else {
            canvas2D.setColor(Constants.ELEMENT_FILL_COLOUR);
        }
        canvas2D.fill(place);

        if (_selected && !_ignoreSelection) {
            canvas2D.setPaint(Constants.SELECTION_LINE_COLOUR);
        } else {
            canvas2D.setPaint(Constants.ELEMENT_LINE_COLOUR);
        }
        canvas2D.draw(place);

        canvas2D.setStroke(new BasicStroke(1.0f));

        // Paints border round a tagged place - paint component is called after any action on the place, so this bit
        // of code doesn't have to be called specially

        if (this.isTagged()) {
            final AffineTransform oldTransform = canvas2D.getTransform();

            final AffineTransform scaleTransform = new AffineTransform();
            scaleTransform.setToScale(1.2, 1.2);

            canvas2D.transform(scaleTransform);

            canvas2D.translate(-2, -2);

            canvas2D.fill(place);

            canvas2D.translate(2, 2);

            canvas2D.setTransform(oldTransform);


        }


        int tempTotalMarking = getTotalMarking();

        if (tempTotalMarking > 5) {
            int offset = 0;
            for (MarkingView m : _currentMarkingView) {
                m.update(canvas, insets, offset, tempTotalMarking);
                if (m.getCurrentMarking() != 0) {
                    offset += 10;
                }
            }
        } else {
            for (MarkingView m : _currentMarkingView) {
                m.update(canvas, insets, 0, tempTotalMarking);
                tempTotalMarking = tempTotalMarking - m.getCurrentMarking();
            }
        }
    }

    @Override
    public void addToPetriNetTab(PetriNetTab tab) {
        LabelHandler labelHandler = new LabelHandler(_nameLabel, this);
        _nameLabel.addMouseListener(labelHandler);
        _nameLabel.addMouseMotionListener(labelHandler);
        _nameLabel.addMouseWheelListener(labelHandler);

        PlaceHandler placeHandler = new PlaceHandler(this, tab, this.model, petriNetController);
        this.addMouseListener(placeHandler);
        this.addMouseWheelListener(placeHandler);
        this.addMouseMotionListener(placeHandler);
    }

    public HistoryItem setCurrentMarking(List<MarkingView> currentMarkingViewInput) {
//        if (_initialMarkingView.size() == 0) {
//            _initialMarkingView = Copier.mediumCopy(_currentMarkingView);
//        }
//
//
//        int totalMarking = 0;
//        for (MarkingView inputtedMarkingView : currentMarkingViewInput) {
//            totalMarking += inputtedMarkingView.getCurrentMarking();
//        }
//        // If total marking exceeds capacity then leave the marking as is
//        if (model.getCapacity() != 0 && totalMarking > model.getCapacity()) {
//            return new PlaceMarking(this, _currentMarkingView, _currentMarkingView);
//        }
//
//        List<MarkingView> oldMarkingView = Copier.mediumCopy(_currentMarkingView);
//
//        // if a marking for a specific class that existed before does not exist
//        // in
//        // the new input then this place must release the lock for that token
//        // class
//        // to allow it to be edited. Also if a previously positive marking is
//        // now
//        // 0 then the same should happen.
//        for (MarkingView m : _currentMarkingView) {
//            int newMarkingPos = getMarkingListPos(m.getToken().getID(), currentMarkingViewInput);
//            if ((newMarkingPos == -1) || (currentMarkingViewInput.get(newMarkingPos).getCurrentMarking() == 0 &&
//                    m.getCurrentMarking() != 0)) {
//                ApplicationSettings.getApplicationView().getCurrentPetriNetView()
//                        .unlockTokenClass(m.getToken().getID());
//            }
//        }
//        // if a marking for a specific class that didnt exist before is in
//        // the new input then this place must acquire the lock for that token
//        // class
//        // to avoid it from being edited. Also if a now positive marking was
//        // previously
//        // 0 then the same should happen.
//        for (MarkingView m : currentMarkingViewInput) {
//            int oldMarkingPos = getMarkingListPos(m.getToken().getID(), _currentMarkingView);
//            if ((oldMarkingPos == -1 && m.getCurrentMarking() > 0) ||
//                    (_currentMarkingView.get(oldMarkingPos).getCurrentMarking() == 0 && m.getCurrentMarking() != 0)) {
//                ApplicationSettings.getApplicationView().getCurrentPetriNetView().lockTokenClass(m.getToken().getID());
//            }
//            // Now update the current marking if such a marking exists, otherwise create a new one
//            if (oldMarkingPos == -1) {
//                m.addObserver(this);
//                _currentMarkingView.add(m);
//            } else {
//                _currentMarkingView.get(oldMarkingPos).setCurrentMarking(m.getCurrentMarking());
//            }
//        }
//        LinkedList<TokenView> tokenViews =
//                ApplicationSettings.getApplicationView().getCurrentPetriNetView().getTokenViews();
//        for (TokenView tc : tokenViews) {
//            if (tc.isEnabled()) {
//                if (getMarkingListPos(tc.getID(), _currentMarkingView) == -1) {
//                    MarkingView m = new MarkingView(tc, 0);
//                    m.addObserver(this);
//                    _currentMarkingView.add(m);
//                }
//            }
//        }
//        repaint();
//        List<MarkingView> newMarkingView = Copier.mediumCopy(_currentMarkingView);
//        return new PlaceMarking(this, oldMarkingView, newMarkingView);
        return null;
    }

    public HistoryItem setCapacity(int newCapacity) {

        throw new RuntimeException("NEED TO EXECUTE THIS IN CONTROLLER");
//        int oldCapacity = (int)_model.getCapacity();
//
//        if (capacity != newCapacity) {
//            capacity = newCapacity;
//            update();
//        }
//        return new PlaceCapacity(this, oldCapacity, newCapacity);
    }

    public List<MarkingView> getInitialMarkingView() {
        return _initialMarkingView;
    }

    public List<MarkingView> getCurrentMarkingView() {
        return _currentMarkingView;
    }

    public int getCapacity() {
        return (int) model.getCapacity();
    }

    public List<MarkingView> getCurrentMarkingObject() {
        return _currentMarkingView;
    }

    public Double getMarkingOffsetXObject() {
        return model.getMarkingXOffset();
    }

    public Double getMarkingOffsetYObject() {
        return model.getMarkingYOffset();
    }

    private int getDiameter() {
        return ZoomController.getZoomedValue(model.getWidth(), _zoomPercentage);
    }

    public boolean contains(int x, int y) {
        double unZoomedX = ZoomController.getUnzoomedValue(x - getComponentDrawOffset(), _zoomPercentage);
        double unZoomedY = ZoomController.getUnzoomedValue(y - getComponentDrawOffset(), _zoomPercentage);

        //TODO: WORK OUT WHAT THIS DOES
        ArcView someArcView = null;//ApplicationSettings.getApplicationView().getCurrentTab()._createArcView;
        if (someArcView != null) { // Must be drawing a new Arc if non-NULL.
            if ((proximityPlace.contains((int) unZoomedX, (int) unZoomedY) ||
                    place.contains((int) unZoomedX, (int) unZoomedY)) && areNotSameType(someArcView.getSource())) {
                // assume we are only snapping the target...
                if (someArcView.getTarget() != this) {
                    someArcView.setTarget(this);
                }
                someArcView.updateArcPosition();
                return true;
            } else {
                if (someArcView.getTarget() == this) {
                    if (!ConnectableHandler.isMouseDown()) {
                        someArcView.setTarget(null);
                        updateConnected();
                    }
                }
                return false;
            }
        } else {
            return place.contains((int) unZoomedX, (int) unZoomedY);
        }
    }

    public void updateEndPoint(ArcView arcView) {
        if (arcView.getSource() == this) {
            arcView.setSourceLocation(model.getX() + (getDiameter() * 0.5), model.getY() + (getDiameter() * 0.5));
            double angle = arcView.getArcPath().getStartAngle();
            arcView.setSourceLocation(model.getX() + centreOffsetLeft() - (0.5 * getDiameter() * (Math.sin(angle))),
                    model.getY() + centreOffsetTop() + (0.5 * getDiameter() * (Math.cos(angle))));
        } else {
            // Make it calculate the angle from the centre of the place rather
            // than the current target point
            arcView.setTargetLocation(model.getX() + (getDiameter() * 0.5), model.getY() + (getDiameter() * 0.5));
            double angle = arcView.getArcPath().getEndAngle();
            arcView.setTargetLocation(model.getX() + centreOffsetLeft() - (0.5 * getDiameter() * (Math.sin(angle))),
                    model.getY() + centreOffsetTop() + (0.5 * getDiameter() * (Math.cos(angle))));
        }
    }

    public void toggleAttributesVisible() {
        _attributesVisible = !_attributesVisible;
        update();
    }

    boolean hasCapacity() {
        return model.getCapacity() > 0;
    }

    public void addedToGui() {
        super.addedToGui();
        update();
    }

    public void showEditor() {
        // Build interface
        EscapableDialog guiDialog = new EscapableDialog(ApplicationSettings.getApplicationView(), "PIPE2", true);

        Container contentPane = guiDialog.getContentPane();

        // 1 Set layout
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));

        // 2 Add Place editor
        contentPane.add(new PlaceEditorPanel(guiDialog.getRootPane(), petriNetController.getPlaceController(this.getModel()),
                ApplicationSettings.getApplicationView().getCurrentPetriNetView()));

        guiDialog.setResizable(false);

        // Make window fit contents' preferred size
        guiDialog.pack();

        // Move window to the middle of the screen
        guiDialog.setLocationRelativeTo(null);
        guiDialog.setVisible(true);
    }

    public void update() {
        if (_attributesVisible) {
            _nameLabel.setText("\nk=" + (model.getCapacity() > 0 ? model.getCapacity() : "\u221E"));
        } else {
            _nameLabel.setText("");
        }
        _nameLabel.zoomUpdate(_zoomPercentage);
        _nameLabel.setName(model.getName());

        updateDisplayTokens();

        super.update();
        repaint();
    }

    public void delete() {
        super.delete();
        //ApplicationSettings.getApplicationView().getCurrentPetriNetView().deletePlace(this.getId());
    }

    private boolean isTagged() {
        return false;
    }

    public void backUpMarking() {
        initBackUp = Copier.mediumCopy(_initialMarkingView);
        currentBackUp = Copier.mediumCopy(_currentMarkingView);

    }

    public void restoreMarking() {
        _initialMarkingView = initBackUp;
        _currentMarkingView = currentBackUp;
        update();
    }

    @Override
    public void update(Observable observable, Object obj) {
        if ((observable instanceof PipeObservable) && (obj == null)) {
            // if multiple cases are added, consider creating specific subclasses of Observable
            Object originalObject = ((PipeObservable) observable).getObservable();
            if (originalObject instanceof MarkingView) {
                MarkingView viewToDelete = (MarkingView) originalObject;
                _currentMarkingView.remove(viewToDelete);
            }
        }
        if (obj instanceof Place) {
            Place place = (Place) obj;
            this.model  = place;
//            setPositionX(_model.getX());
//            setPositionY(_model.getY());
            update();
        }
    }

}

