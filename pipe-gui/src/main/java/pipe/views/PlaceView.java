package pipe.views;

import pipe.controllers.PetriNetController;
import pipe.gui.ApplicationSettings;
import pipe.gui.Constants;
import pipe.gui.PetriNetTab;
import pipe.gui.widgets.EscapableDialog;
import pipe.gui.widgets.PlaceEditorPanel;
import pipe.handlers.PlaceHandler;
import pipe.historyActions.HistoryItem;
import pipe.models.PipeObservable;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;
import pipe.utilities.Copier;
import pipe.views.builder.TokenViewBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.*;
import java.util.List;

// Steve Doubleday (Oct 2013): added as Observer of changes to MarkingViews; refactored to simplify testing
public class PlaceView extends ConnectableView<Place> implements Serializable, Observer {

    private final Ellipse2D.Double place;

    private List<MarkingView> _initialMarkingView = new LinkedList<MarkingView>();

    private List<MarkingView> _currentMarkingView = new LinkedList<MarkingView>();

    //transferred
    private TokenView _activeTokenView;

    private List<MarkingView> initBackUp;

    private List<MarkingView> currentBackUp;

    public PlaceView(double positionXInput, double positionYInput) {
        //MODEL
        super(new Place("", ""));
        place = new Ellipse2D.Double(0, 0, model.getWidth(), model.getWidth());
    }


    public PlaceView(String idInput, String nameInput, LinkedList<MarkingView> initialMarkingViewInput, Place model, PetriNetController controller) {
        //MODEL
        super(idInput, model, controller);
        _initialMarkingView = Copier.mediumCopy(initialMarkingViewInput);
        _currentMarkingView = Copier.mediumCopy(initialMarkingViewInput);
        setId(model.getId());
        place = new Ellipse2D.Double(0, 0, model.getWidth(), model.getWidth());
        updateDisplayTokens();
        setChangeListener();
    }

    private void setChangeListener() {
        model.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                String name = propertyChangeEvent.getPropertyName();
                if (name.equals(Place.TOKEN_CHANGE_MESSAGE)) {
                    updateDisplayTokens();
                }
                repaint();
            }
        });
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
            _currentMarkingView.add(markingView);
        }
        repaint();
    }

    private void removeExistingTokens() {
        for (MarkingView view : _currentMarkingView) {
            //TODO: THIS NEEDS TO BE DONE IN A LESS HACKY WAY
            this.getParent().remove(view);
        }
        _currentMarkingView.clear();
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
     * Paints the Place component taking into account the n q12[umber of tokens from
     * the storeCurrentMarking
     *
     * @param g The PositionGraphics object onto which the Place is drawn.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        g2.translate(getComponentDrawOffset(), getComponentDrawOffset());
        AffineTransform transform = g2.getTransform();
        Insets insets = getInsets();

        if (hasCapacity()) {
            g2.setStroke(new BasicStroke(2.0f));
            setToolTipText("k = " + this.getCapacity());
        } else {
            g2.setStroke(new BasicStroke(1.0f));
            setToolTipText("k = \u221E");
        }
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (isSelected() && !_ignoreSelection) {
            g2.setColor(Constants.SELECTION_FILL_COLOUR);
        } else {
            g2.setColor(Constants.ELEMENT_FILL_COLOUR);
        }
        g2.fill(place);

        if (isSelected() && !_ignoreSelection) {
            g2.setPaint(Constants.SELECTION_LINE_COLOUR);
        } else {
            g2.setPaint(Constants.ELEMENT_LINE_COLOUR);
        }
        g2.draw(place);

        g2.setStroke(new BasicStroke(1.0f));

        // Paints border round a tagged place - paint component is called after any action on the place, so this bit
        // of code doesn't have to be called specially

        if (this.isTagged()) {
            AffineTransform oldTransform = g2.getTransform();

            AffineTransform scaleTransform = new AffineTransform();
            scaleTransform.setToScale(1.2, 1.2);

            g2.transform(scaleTransform);

            g2.translate(-2, -2);

            g2.fill(place);

            g2.translate(2, 2);

            g2.setTransform(oldTransform);


        }


        int tempTotalMarking = getTotalMarking();

        if (tempTotalMarking > 5) {
            int offset = 0;
            for (MarkingView m : _currentMarkingView) {
                m.update(g, insets, offset, tempTotalMarking);
                if (m.getCurrentMarking() != 0) {
                    offset += 10;
                }
            }
        } else {
            for (MarkingView m : _currentMarkingView) {
                m.update(g, insets, 0, tempTotalMarking);
                tempTotalMarking = tempTotalMarking - m.getCurrentMarking();
            }
        }
        g2.dispose();
    }

    public int getCapacity() {
        return model.getCapacity();
    }

    boolean hasCapacity() {
        return model.getCapacity() > 0;
    }

    @Override
    public void delete() {
        super.delete();
        //ApplicationSettings.getApplicationView().getCurrentPetriNetView().deletePlace(this.getId());
    }

    @Override
    public void showEditor() {
        // Build interface
        EscapableDialog guiDialog = new EscapableDialog(ApplicationSettings.getApplicationView(), "PIPE2", true);

        Container contentPane = guiDialog.getContentPane();

        // 1 Set layout
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));

        // 2 Add Place editor
        contentPane.add(new PlaceEditorPanel(guiDialog.getRootPane(), petriNetController.getPlaceController(this.getModel()), ApplicationSettings.getApplicationView().getCurrentPetriNetView()));

        guiDialog.setResizable(false);

        // Make window fit contents' preferred size
        guiDialog.pack();

        // Move window to the middle of the screen
        guiDialog.setLocationRelativeTo(null);
        guiDialog.setVisible(true);
    }

    @Override
    public void addedToGui() {
        super.addedToGui();
    }

    @Override
    public void toggleAttributesVisible() {
        _attributesVisible = !_attributesVisible;
    }

    private boolean isTagged() {
        return false;
    }

    @Override
    public void addToPetriNetTab(PetriNetTab tab) {
        addLabelToContainer(tab);


        PlaceHandler placeHandler = new PlaceHandler(this, tab, this.model, petriNetController);
        this.addMouseListener(placeHandler);
        this.addMouseWheelListener(placeHandler);
        this.addMouseMotionListener(placeHandler);
    }

    public HistoryItem setCurrentMarking(List<MarkingView> currentMarkingViewInput) {
        return null;
    }

    public HistoryItem setCapacity(int newCapacity) {
        return null;
    }

    public List<MarkingView> getInitialMarkingView() {
        return _initialMarkingView;
    }

    public List<MarkingView> getCurrentMarkingView() {
        return _currentMarkingView;
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

    @Override
    public boolean contains(int x, int y) {

        //TODO: WORK OUT WHAT THIS DOES
        ArcView someArcView = null;//ApplicationSettings.getApplicationView().getCurrentTab()._createArcView;
        //        if (someArcView != null) { // Must be drawing a new Arc if non-NULL.
        //            if ((proximityPlace.contains((int) unZoomedX, (int) unZoomedY) ||
        //                    place.contains((int) unZoomedX, (int) unZoomedY)) && areNotSameType(someArcView.getSource())) {
        //                // assume we are only snapping the target...
        //                if (someArcView.getTarget() != this) {
        //                    someArcView.setTarget(this);
        //                }
        //                someArcView.updateArcPosition();
        //                return true;
        //            } else {
        //                if (someArcView.getTarget() == this) {
        //                    if (!ConnectableHandler.isMouseDown()) {
        //                        someArcView.setTarget(null);
        //                        updateConnected();
        //                    }
        //                }
        //                return false;
        //            }
        //        } else {
        return place.contains(x, y);
        //        }
    }

    public void backUpMarking() {
        initBackUp = Copier.mediumCopy(_initialMarkingView);
        currentBackUp = Copier.mediumCopy(_currentMarkingView);

    }

    public void restoreMarking() {
        _initialMarkingView = initBackUp;
        _currentMarkingView = currentBackUp;
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
            this.model = place;
            //            setPositionX(_model.getX());
            //            setPositionY(_model.getY());
        }
    }

}

