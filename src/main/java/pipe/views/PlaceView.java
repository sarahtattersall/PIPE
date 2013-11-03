package pipe.views;

import pipe.controllers.PlaceController;
import pipe.gui.*;
import pipe.gui.widgets.EscapableDialog;
import pipe.gui.widgets.PlaceEditorPanel;
import pipe.handlers.LabelHandler;
import pipe.handlers.PlaceHandler;
import pipe.handlers.PlaceTransitionObjectHandler;
import pipe.historyActions.HistoryItem;
import pipe.historyActions.PlaceCapacity;
import pipe.historyActions.PlaceMarking;
import pipe.models.Marking;
import pipe.models.PipeObservable;
import pipe.models.Place;
import pipe.models.Token;
import pipe.utilities.Copier;
import pipe.views.builder.TokenViewBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.List;

// Steve Doubleday (Oct 2013): added as Observer of changes to MarkingViews; refactored to simplify testing
public class PlaceView extends ConnectableView<Place> implements Serializable, Observer {
    //transferred
    private static final long serialVersionUID = 1L;
    //transferred
    private List<MarkingView> _initialMarkingView = new LinkedList<MarkingView>();
    //transferred
    private List<MarkingView> _currentMarkingView = new LinkedList<MarkingView>();

    //transferred
    private Integer totalMarking = 0;

    private Double markingOffsetX = 0d;

    private Double markingOffsetY = 0d;

    //transferred
    private Integer capacity = 0;

    private static final int DIAMETER = 30;


    private static final Ellipse2D.Double place = new Ellipse2D.Double(0, 0, DIAMETER, DIAMETER);
    private static final Shape proximityPlace =
            (new BasicStroke(Constants.PLACE_TRANSITION_PROXIMITY_RADIUS)).createStrokedShape(place);

    //transferred
    private TokenView _activeTokenView;
    private PlaceController _placeController;
    private List<MarkingView> initBackUp;
    private List<MarkingView> currentBackUp;

    public PlaceView() {
        this(0, 0, "", "", 0, 0, new LinkedList<MarkingView>(), 0, 0, 0);
    }

    public PlaceView(double positionXInput, double positionYInput) {
        //MODEL
        super(positionXInput, positionYInput, new Place("", ""));
        _componentWidth = DIAMETER;
        _componentHeight = DIAMETER;

        setCentre((int) _positionX, (int) _positionY);
    }


    public PlaceView(double positionXInput, double positionYInput, String idInput, String nameInput,
            double nameOffsetXInput, double nameOffsetYInput, LinkedList<MarkingView> initialMarkingViewInput,
            double markingOffsetXInput, double markingOffsetYInput, int capacityInput) {
        //MODEL
        super(positionXInput, positionYInput, idInput, nameInput, nameOffsetXInput, nameOffsetYInput,
                new Place(idInput, nameInput));
        _initialMarkingView = Copier.mediumCopy(initialMarkingViewInput);
        _currentMarkingView = Copier.mediumCopy(initialMarkingViewInput);
        addObserver(_currentMarkingView);
        totalMarking = getTotalMarking();
        markingOffsetX = new Double(markingOffsetXInput);
        markingOffsetY = new Double(markingOffsetYInput);
        _nameOffsetX = new Double(nameOffsetXInput);
        _nameOffsetY = new Double(nameOffsetYInput);
        _componentWidth = DIAMETER;
        _componentHeight = DIAMETER;
        capacity = capacityInput;
        setId(_model.getId());
        setCapacity(capacityInput);
        setCentre((int) _positionX, (int) _positionY);
    }

    private void createDisplayTokens() {
        for (Marking marking : _model.getTokens()) {
            Token token = marking.getToken();
            //TODO: IF TOKEN HAS NOT BEEN DECLARED POP UP ERROR MESSAGE!
            if (token != null)
            {
                TokenViewBuilder builder = new TokenViewBuilder(marking.getToken());
                TokenView tokenView = builder.build();
                MarkingView markingView = new MarkingView(tokenView, marking.getCurrentMarking());
                markingView.addObserver(this);
                _currentMarkingView.add(markingView);
            }
        }
    }

    //TODO: This is a temporary method before removing variables above, so that
    // it doesn't break existing code
    public void setModel(Place model) {
        this._model = model;
        createDisplayTokens();
    }


    private void addObserver(List<MarkingView> markingViews) {
        for (MarkingView markingView : markingViews) {
            markingView.addObserver(this);
        }
    }

    public PlaceView(PlaceController placeController, Place model) {
        super(0, 0, model);

        _placeController = placeController;
        _model = model;
        _model.registerObserver(this);
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
        copy._nameOffsetX = this._nameOffsetX;
        copy._nameOffsetY = this._nameOffsetY;
        copy.capacity = this.capacity;
        copy._attributesVisible = this._attributesVisible;
        copy._initialMarkingView = Copier.mediumCopy(this._initialMarkingView);
        copy.totalMarking = this.totalMarking;
        copy.markingOffsetX = this.markingOffsetX;
        copy.markingOffsetY = this.markingOffsetY;
        copy.update();
        return copy;
    }

    public PlaceView copy() {
        PlaceView copy = new PlaceView((double) ZoomController.getUnzoomedValue(this.getX(), _zoomPercentage),
                (double) ZoomController.getUnzoomedValue(this.getY(), _zoomPercentage));
        copy._nameLabel.setName(this.getName());
        copy._nameOffsetX = this._nameOffsetX;
        copy._nameOffsetY = this._nameOffsetY;
        copy.capacity = this.capacity;
        copy._attributesVisible = this._attributesVisible;
        copy._initialMarkingView = Copier.mediumCopy(this._initialMarkingView);
        copy.totalMarking = this.totalMarking;
        copy.markingOffsetX = this.markingOffsetX;
        copy.markingOffsetY = this.markingOffsetY;
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

            canvas2D.fill(PlaceView.place);

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

        PlaceHandler placeHandler = new PlaceHandler(tab, this._model);
        this.addMouseListener(placeHandler);
        this.addMouseWheelListener(placeHandler);
        this.addMouseMotionListener(placeHandler);
    }

    public HistoryItem setCurrentMarking(List<MarkingView> currentMarkingViewInput) {
        if (_initialMarkingView.size() == 0) {
            _initialMarkingView = Copier.mediumCopy(_currentMarkingView);
        }


        int totalMarking = 0;
        for (MarkingView inputtedMarkingView : currentMarkingViewInput) {
            totalMarking += inputtedMarkingView.getCurrentMarking();
        }
        // If total marking exceeds capacity then leave the marking as is
        if (capacity != 0 && totalMarking > capacity) {
            return new PlaceMarking(this, _currentMarkingView, _currentMarkingView);
        }

        List<MarkingView> oldMarkingView = Copier.mediumCopy(_currentMarkingView);

        // if a marking for a specific class that existed before does not exist
        // in
        // the new input then this place must release the lock for that token
        // class
        // to allow it to be edited. Also if a previously positive marking is
        // now
        // 0 then the same should happen.
        for (MarkingView m : _currentMarkingView) {
            int newMarkingPos = getMarkingListPos(m.getToken().getID(), currentMarkingViewInput);
            if ((newMarkingPos == -1) || (currentMarkingViewInput.get(newMarkingPos).getCurrentMarking() == 0 &&
                    m.getCurrentMarking() != 0)) {
                ApplicationSettings.getApplicationView().getCurrentPetriNetView()
                        .unlockTokenClass(m.getToken().getID());
            }
        }
        // if a marking for a specific class that didnt exist before is in
        // the new input then this place must acquire the lock for that token
        // class
        // to avoid it from being edited. Also if a now positive marking was
        // previously
        // 0 then the same should happen.
        for (MarkingView m : currentMarkingViewInput) {
            int oldMarkingPos = getMarkingListPos(m.getToken().getID(), _currentMarkingView);
            if ((oldMarkingPos == -1 && m.getCurrentMarking() > 0) ||
                    (_currentMarkingView.get(oldMarkingPos).getCurrentMarking() == 0 && m.getCurrentMarking() != 0)) {
                ApplicationSettings.getApplicationView().getCurrentPetriNetView().lockTokenClass(m.getToken().getID());
            }
            // Now update the current marking if such a marking exists, otherwise create a new one
            if (oldMarkingPos == -1) {
                m.addObserver(this);
                _currentMarkingView.add(m);
            } else {
                _currentMarkingView.get(oldMarkingPos).setCurrentMarking(m.getCurrentMarking());
            }
        }
        LinkedList<TokenView> tokenViews =
                ApplicationSettings.getApplicationView().getCurrentPetriNetView().getTokenViews();
        for (TokenView tc : tokenViews) {
            if (tc.isEnabled()) {
                if (getMarkingListPos(tc.getID(), _currentMarkingView) == -1) {
                    MarkingView m = new MarkingView(tc, 0);
                    m.addObserver(this);
                    _currentMarkingView.add(m);
                }
            }
        }
        repaint();
        List<MarkingView> newMarkingView = Copier.mediumCopy(_currentMarkingView);
        return new PlaceMarking(this, oldMarkingView, newMarkingView);
    }

    public HistoryItem setCapacity(int newCapacity) {
        int oldCapacity = capacity;

        if (capacity != newCapacity) {
            capacity = newCapacity;
            update();
        }
        return new PlaceCapacity(this, oldCapacity, newCapacity);
    }

    public List<MarkingView> getInitialMarkingView() {
        return _initialMarkingView;
    }

    public List<MarkingView> getCurrentMarkingView() {
        return _currentMarkingView;
    }

    public int getCapacity() {
        return ((capacity == null) ? 0 : capacity.intValue());
    }

    public List<MarkingView> getCurrentMarkingObject() {
        return _currentMarkingView;
    }

    public Double getMarkingOffsetXObject() {
        return markingOffsetX;
    }

    public Double getMarkingOffsetYObject() {
        return markingOffsetY;
    }

    private int getDiameter() {
        return ZoomController.getZoomedValue(DIAMETER, _zoomPercentage);
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
                    if (!PlaceTransitionObjectHandler.isMouseDown()) {
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
            arcView.setSourceLocation(_positionX + (getDiameter() * 0.5), _positionY + (getDiameter() * 0.5));
            double angle = arcView.getArcPath().getStartAngle();
            arcView.setSourceLocation(_positionX + centreOffsetLeft() - (0.5 * getDiameter() * (Math.sin(angle))),
                    _positionY + centreOffsetTop() + (0.5 * getDiameter() * (Math.cos(angle))));
        } else {
            // Make it calculate the angle from the centre of the place rather
            // than the current target point
            arcView.setTargetLocation(_positionX + (getDiameter() * 0.5), _positionY + (getDiameter() * 0.5));
            double angle = arcView.getArcPath().getEndAngle();
            arcView.setTargetLocation(_positionX + centreOffsetLeft() - (0.5 * getDiameter() * (Math.sin(angle))),
                    _positionY + centreOffsetTop() + (0.5 * getDiameter() * (Math.cos(angle))));
        }
    }

    public void toggleAttributesVisible() {
        _attributesVisible = !_attributesVisible;
        update();
    }

    boolean hasCapacity() {
        return capacity > 0;
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
        contentPane.add(new PlaceEditorPanel(guiDialog.getRootPane(), this,
                ApplicationSettings.getApplicationView().getCurrentPetriNetView(),
                ApplicationSettings.getApplicationView().getCurrentTab()));

        guiDialog.setResizable(false);

        // Make window fit contents' preferred size
        guiDialog.pack();

        // Move window to the middle of the screen
        guiDialog.setLocationRelativeTo(null);
        guiDialog.setVisible(true);
    }

    public void update() {
        if (_attributesVisible) {
            _nameLabel.setText("\nk=" + (capacity > 0 ? capacity : "\u221E"));
        } else {
            _nameLabel.setText("");
        }
        _nameLabel.zoomUpdate(_zoomPercentage);
        super.update();
        repaint();
    }

    public void delete() {
        super.delete();
        ApplicationSettings.getApplicationView().getCurrentPetriNetView().deletePlace(this.getId());
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
            this._model  = place;
            setPositionX(_model.getX());
            setPositionY(_model.getY());
            update();
        }
    }
}

