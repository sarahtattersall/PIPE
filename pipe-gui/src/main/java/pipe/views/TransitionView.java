package pipe.views;

import net.sourceforge.jeval.EvaluationException;
import parser.ExprEvaluator;
import pipe.controllers.PetriNetController;
import pipe.controllers.TransitionController;
import pipe.gui.ApplicationSettings;
import pipe.gui.Constants;
import pipe.gui.PetriNetTab;
import pipe.gui.widgets.EscapableDialog;
import pipe.gui.widgets.TransitionEditorPanel;
import pipe.handlers.LabelHandler;
import pipe.handlers.TransitionHandler;
import pipe.historyActions.*;
import pipe.models.PetriNet;
import pipe.models.component.Connectable;
import pipe.models.component.Transition;
import pipe.views.viewComponents.RateParameter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;


public class
        TransitionView extends ConnectableView<Transition> implements Serializable {
    public boolean _highlighted;
    private GeneralPath shape;
    private boolean _enabled;
    private boolean _enabledBackwards;
    private double _delay;
    private boolean _delayValid;
    private RateParameter _rateParameter;
    private GroupTransitionView _groupTransitionView;
    private int _delayForShowingWarnings;

    public TransitionView() {
        this("", "", Constants.DEFAULT_OFFSET_X, Constants.DEFAULT_OFFSET_Y, false,
                false, 0, new Transition("", "", "1", 1), null);
    }


    public TransitionView(String id, String name, double nameOffsetX,
                          double nameOffsetY, boolean timed, boolean infServer, int angleInput, Transition model,
                          PetriNetController controller) {
        super(id, name, model.getX() + model.getNameXOffset(), model.getY() + model.getNameYOffset(), model,
                controller);
        constructTransition();
        setChangeListener();

        _enabled = false;
        _enabledBackwards = false;
        _highlighted = false;
        _delayForShowingWarnings = 10000;

        rotate(model.getAngle());
        updateBounds();
        //TODO: DEBUG WHY CANT CALL THIS IN CONSTRUCTOR
        //        changeToolTipText();

    }

    private void constructTransition() {
        shape = new GeneralPath();
        //TODO: CHANGE THIS BACK! _componentWidth = TRANSITION_HEIGHT
        shape.append(new Rectangle2D.Double((model.getHeight() - model.getWidth()) / 2, 0, model.getWidth(),
                model.getHeight()), false);
    }

    public void rotate(int angleInc) {
                shape.transform(AffineTransform.getRotateInstance(Math.toRadians(angleInc), model.getHeight() / 2,
                        model.getHeight() / 2));
    }

    private void setChangeListener() {
        model.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                String name = propertyChangeEvent.getPropertyName();
                if (name.equals("priority") || name.equals("rate")) {
                    repaint();
                } else if (name.equals("angle") || name.equals("timed") || name.equals("infiniteServer")) {
                    repaint();
                }
            }
        });
    }

    public TransitionView(TransitionController transitionController, Transition model) {
        super(model);
        model = model;
        //        model.registerObserver(this);
    }

    @Override
    public boolean isShowing() {
        return super.isShowing();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public boolean isEnabled() {
        return _enabled;
    }

    public void setEnabled(boolean status) {
        if (_enabled && !status) {
            _delayValid = false;
        }
        if (_groupTransitionView != null) {
            _groupTransitionView.setEnabled(status);
        }
        _enabled = status;

    }

    public void setDelayForShowingWarnings(int delayForShowingWarnings) {
        _delayForShowingWarnings = delayForShowingWarnings;
    }

    public TransitionView copy() {
        TransitionView copy = new TransitionView();
        copy._nameLabel.setName(this.getName());
        copy.model.setRateExpr(getRate());
        copy._attributesVisible = this._attributesVisible;
        copy.model.setPriority(model.getPriority());
        copy.setOriginal(this);
        copy._rateParameter = this._rateParameter;
        return copy;
    }

    public double getRate() {
        if (isInfiniteServer()) {
            PetriNet petriNet = petriNetController.getPetriNet();
            return petriNet.getEnablingDegree(model);
        }

        if (model.getRateExpr() == null) {
            return -1;
        }
        try {
            return Double.parseDouble(model.getRateExpr());
        } catch (Exception e) {
            ExprEvaluator parser = new ExprEvaluator(petriNetController.getPetriNet());
            try {
                return parser.parseAndEvalExprForTransition(model.getRateExpr());
            } catch (EvaluationException ee) {
                showErrorMessage();
                return 1.0;
            }

        }
    }

    private void showErrorMessage() {
        String message =
                "Errors in marking-dependent transition rate expression." + "\r\n The computation should be aborted";
        String title = "Error";
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.YES_NO_OPTION);
    }

    public boolean isInfiniteServer() {
        return model.isInfiniteServer();
    }

    public TransitionView paste(double x, double y, boolean fromAnotherView, PetriNetView model) {
        TransitionView copy =
                new TransitionView();
        String newName = this._nameLabel.getName() + "(" + this.getCopyNumber() + ")";
        boolean properName = false;

        while (!properName) {
            if (model.checkTransitionIDAvailability(newName)) {
                copy._nameLabel.setName(newName);
                properName = true;
            } else {
                newName = newName + "'";
            }
        }

        this.newCopy(copy);

        copy.model.setRateExpr(this.model.getRateExpr());

        copy._attributesVisible = this._attributesVisible;
        copy.model.setPriority(this.model.getPriority());
        copy.shape.transform(
                AffineTransform.getRotateInstance(Math.toRadians(this.model.getAngle()), this.model.getHeight() / 2,
                        this.model.getHeight() / 2));
        copy._rateParameter = null;


        return copy;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (isSelected() && !_ignoreSelection) {
            g2.setColor(Constants.SELECTION_FILL_COLOUR);
        } else {
            g2.setColor(Constants.ELEMENT_FILL_COLOUR);
        }

        if (model.isTimed()) {
            if (model.isInfiniteServer()) {
                for (int i = 2; i >= 1; i--) {
                    g2.translate(2 * i, -2 * i);
                    g2.fill(shape);
                    Paint pen = g2.getPaint();
                    if (highlightView()) {
                        g2.setPaint(Constants.ENABLED_TRANSITION_COLOUR);
                    } else if (isSelected() && !_ignoreSelection) {
                        g2.setPaint(Constants.SELECTION_LINE_COLOUR);
                    } else {
                        g2.setPaint(Constants.ELEMENT_LINE_COLOUR);
                    }
                    g2.draw(shape);
                    g2.setPaint(pen);
                    g2.translate(-2 * i, 2 * i);
                }
            }
            g2.fill(shape);
        }

        if (highlightView()) {
            g2.setPaint(Constants.ENABLED_TRANSITION_COLOUR);
        } else if (isSelected() && !_ignoreSelection) {
            g2.setPaint(Constants.SELECTION_LINE_COLOUR);
        } else {
            g2.setPaint(Constants.ELEMENT_LINE_COLOUR);
        }

        g2.draw(shape);
        if (!model.isTimed()) {
            if (model.isInfiniteServer()) {
                for (int i = 2; i >= 1; i--) {
                    g2.translate(2 * i, -2 * i);
                    Paint pen = g2.getPaint();
                    g2.setPaint(Constants.ELEMENT_FILL_COLOUR);
                    g2.fill(shape);
                    g2.setPaint(pen);
                    g2.draw(shape);
                    g2.translate(-2 * i, 2 * i);
                }
            }
            g2.draw(shape);
            g2.fill(shape);
        }
        changeToolTipText();


    }

    void setCentre(double x, double y) {
        super.setCentre(x, y);
        update();
    }

    public void delete() {
        if (_rateParameter != null) {
            _rateParameter.remove(this);
            _rateParameter = null;
        }
        super.delete();
    }

    public void addedToGui() {
        super.addedToGui();
        update();
    }

    public void update() {
        _nameLabel.setText(getAttributes());
        _nameLabel.zoomUpdate(_zoomPercentage);
        //        super.update();
        this.repaint();
    }

    private String getAttributes() {
        if (_attributesVisible) {
            try {
                Double.parseDouble(getRateExpr());
                if (isInfiniteServer()) {
                    return "\nr=" + "ED(" + this.getId() + ",M)=" + getRate();
                }
                if (isTimed()) {
                    if (_rateParameter != null) {
                        return "\nr=" + _rateParameter.getName();
                    } else {
                        return "\nr=" + getRate();
                    }
                } else {
                    if (_rateParameter != null) {
                        return "\n" + '\u03c0' + "=" + model.getPriority() + "\nw=" + _rateParameter.getName();
                    } else {
                        return "\n" + '\u03c0' + "=" + model.getPriority() + "\nw=" + getRate();
                    }
                }
            } catch (Exception e) {
                if (isInfiniteServer()) {
                    return "\nr=" + "ED(" + this.getId() + ",M)=" + getRate();
                }
                if (isTimed()) {
                    if (_rateParameter != null) {
                        return "\nr=" + _rateParameter.getName();
                    } else {
                        return "\nr=" + getRateExpr() + "=" + getRate();
                    }
                } else {
                    if (_rateParameter != null) {
                        return "\n" + '\u03c0' + "=" + model.getPriority() + "\nw=" + _rateParameter.getName();
                    } else {
                        return "\n" + '\u03c0' + "=" + model.getPriority() + "\nw=" + getRateExpr() + "=" + getRate();
                    }
                }
            }
        }
        return "";
    }

    public void showEditor() {
        EscapableDialog guiDialog = new EscapableDialog(ApplicationSettings.getApplicationView(), "PIPE2", true);
        TransitionEditorPanel te = new TransitionEditorPanel(guiDialog.getRootPane(),
                petriNetController.getTransitionController(this.model),
                petriNetController);
        guiDialog.add(te);
        guiDialog.getRootPane().setDefaultButton(null);
        guiDialog.setResizable(false);
        guiDialog.pack();
        guiDialog.setLocationRelativeTo(null);
        guiDialog.setVisible(true);
        guiDialog.dispose();
    }

    public void toggleAttributesVisible() {
        _attributesVisible = !_attributesVisible;
        _nameLabel.setText(getAttributes());
    }

    private void changeToolTipText() {
        try {
            Double.parseDouble(getRateExpr());
            if (this.isTimed()) {
                setToolTipText("r = " + this.getRate());
            } else {
                setToolTipText("\u03c0 = " + this.getPriority() + "; w = " + this.getRate());
            }
        } catch (Exception e) {
            if (this.isTimed()) {
                setToolTipText("r = " + this.getRateExpr() + " = " + this.getRate());
            } else {
                setToolTipText(
                        "\u03c0 = " + this.getPriority() + "; w = " + this.getRateExpr() + " = " + this.getRate());
            }
        }
    }

    public String getRateExpr() {
        return model.getRateExpr();
    }

    public boolean isTimed() {
        return model.isTimed();
    }

    public int getPriority() {
        return model.
                getPriority();
    }

    /**
     * @return true if in animate mode and the model is enabled
     */
    private boolean highlightView() {
        //TODO: GET THIS IN A BETTER WAY
        PipeApplicationView view = ApplicationSettings.getApplicationView();
        PetriNetTab tab = view.getCurrentTab();

        return model.isEnabled() && tab.isInAnimationMode();
    }

    @Override
    public void addToPetriNetTab(PetriNetTab tab) {
        LabelHandler labelHandler = new LabelHandler(_nameLabel, this);
        _nameLabel.addMouseListener(labelHandler);
        _nameLabel.addMouseMotionListener(labelHandler);
        _nameLabel.addMouseWheelListener(labelHandler);

        TransitionHandler transitionHandler = new TransitionHandler(this, tab, this.model, petriNetController);
        addMouseListener(transitionHandler);
        addMouseMotionListener(transitionHandler);
        addMouseWheelListener(transitionHandler);
        addMouseListener(tab.getAnimationHandler());
    }

    public boolean isEnabled(boolean animationStatus) {
        if (_groupTransitionView != null) {
            _groupTransitionView.isEnabled(animationStatus);
        }
        if (animationStatus) {
            if (_enabled) {
                _highlighted = true;
                return true;
            } else {
                _highlighted = false;
            }
        }
        return false;
    }

    public boolean isEnabledBackwards() {
        return _enabledBackwards;
    }

    public void setEnabledBackwards(boolean status) {
        _enabledBackwards = status;
        if (_groupTransitionView != null) {
            _groupTransitionView.setEnabledBackwards(status);
        }
    }

    public void setHighlighted(boolean status) {
        if (_groupTransitionView != null) {
            _groupTransitionView.setHighlighted(status);
        }
        _highlighted = status;
    }

    //TODO: RE-IMPLEMENT
    public HistoryItem setInfiniteServer(boolean status) {
        throw new RuntimeException("THIS SHOULD BE IMPLEMENTED IN CONTROLLER");
        //        _infiniteServer = status;
        //        repaint();
        //        return new TransitionInfiniteServer(this);
    }

    public void setEnabledFalse() {
        _enabled = false;
        _highlighted = false;
        if (_groupTransitionView != null) {
            _groupTransitionView.setEnabled(false);
        }
    }

    public int getAngle() {
        return model.getAngle();
    }

    public HistoryItem setPriority(int newPriority) {
        //        int oldPriority = getPriority();
        //
        //        model.setPriority(newPriority);
        //        _nameLabel.setText(getAttributes());
        //        repaint();
        //        return new TransitionPriority(this, oldPriority, model.getPriority());
        return null;
    }

    public double getDelay() {
        return _delay;
    }

    public void setDelay(double _delay) {
        this._delay = _delay;
        _delayValid = true;
    }

    public boolean isDelayValid() {
        return _delayValid;
    }

    public void setDelayValid(boolean _delayValid) {
        this._delayValid = _delayValid;
    }

    public boolean contains(int x, int y) {
        int zoomPercentage = _zoomPercentage;

        double unZoomedX = (x - getComponentDrawOffset()) / (zoomPercentage / 100.0);
        double unZoomedY = (y - getComponentDrawOffset()) / (zoomPercentage / 100.0);

        //TODO: WORK OUT WHAT THIS DOES AND REMOVE DUPLICATED CODE BETWEEN THIS AND PLACE
        ArcView someArcView = null; //ApplicationSettings.getApplicationView().getCurrentTab()._createArcView;
        //        if (someArcView != null) {
        //            if ((proximityTransition.contains((int) unZoomedX, (int) unZoomedY) ||
        //                    shape.contains((int) unZoomedX, (int) unZoomedY)) && areNotSameType(someArcView.getSource())) {
        //                if (someArcView.getTarget() != this) {
        //                    someArcView.setTarget(this);
        //                }
        //                someArcView.updateArcPosition();
        //                return true;
        //            } else {
        //                if (someArcView.getTarget() == this) {
        //                    if (!ConnectableHandler.isMouseDown()) {
        //                        someArcView.setTarget(null);
        //                        removeArcCompareObject(someArcView);
        //                        updateConnected();
        //                    }
        //                }
        //                return false;
        //            }
        //        } else {
        return shape.contains((int) unZoomedX, (int) unZoomedY);
        //        }
    }

    public void removeArcCompareObject(ArcView arcView) {
    }

    public RateParameter getRateParameter() {
        return _rateParameter;
    }

    public HistoryItem setRateParameter(RateParameter rateParameter) {
        double oldRate = getRate();
        this._rateParameter = rateParameter;
        this._rateParameter.add(this);
        model.setRateExpr(rateParameter.getValue());
        update();
        return new SetRateParameter(this, oldRate, this._rateParameter);
    }

    public HistoryItem clearRateParameter() {
        RateParameter oldRateParameter = _rateParameter;
        _rateParameter.remove(this);
        _rateParameter = null;
        update();
        return new ClearRateParameter(this, oldRateParameter);
    }

    public HistoryItem changeRateParameter(RateParameter rateParameter) {
        RateParameter oldRateParameter = this._rateParameter;
        this._rateParameter.remove(this);
        this._rateParameter = rateParameter;
        this._rateParameter.add(this);
        model.setRateExpr(rateParameter.getValue());
        update();
        return new ChangeRateParameter(this, oldRateParameter, this._rateParameter);
    }

    public void setModel(Transition model) {
        this.model = model;
    }

    public void bindToGroup(GroupTransitionView groupTransitionView) {
        this._groupTransitionView = groupTransitionView;
    }

    public boolean isGrouped() {
        return _groupTransitionView != null;
    }

    public GroupTransitionView getGroup() {
        return _groupTransitionView;
    }

    public void ungroupTransition() {
        _groupTransitionView = null;
    }

    private void showWarningDialog(String message) {
        JOptionPane pane = new JOptionPane(message, JOptionPane.ERROR_MESSAGE);
        final JDialog dialog = pane.createDialog(ApplicationSettings.getApplicationView(), "Invalid selection");
        ActionListener exiter = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        };
        new Timer(_delayForShowingWarnings, exiter).start();
        dialog.show();
    }

    public int confirmOrTimeout(String message, String title) throws HeadlessException {
        JOptionPane pane = new JOptionPane(message, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION);
        return showWithTimeout(pane, null, title);
    }

    private int showWithTimeout(JOptionPane pane, Component parent, String title) {
        final JDialog dialog = pane.createDialog(parent, title);
        Thread timeoutThread = new Thread() {
            public void run() {
                try {
                    sleep(2000);
                } catch (InterruptedException ex) {
                }
                javax.swing.SwingUtilities.invokeLater(new Runnable() //   from the event dispatch
                {
                    public void run() {
                        dialog.hide();
                    }
                });            //   thread
            }
        };
        timeoutThread.start();
        dialog.show();
        Object selection = pane.getValue();                      // We get to this point when
        int result = JOptionPane.CLOSED_OPTION;                   // (1) The user makes a selection
        if (selection != null && selection instanceof Integer)        // or (2) the timeout thread closes
        {
            result = ((Integer) selection).intValue();             // the dialog.
        }
        return result;
    }

    public HistoryItem groupTransitions() {
        ArrayList<TransitionView> transitionsToHide = groupTransitionsValidation();
        GroupTransitionView newGroupTransitionView =
                new GroupTransitionView(this, model.getX(), model.getY());
        groupTransitionsHelper(transitionsToHide, newGroupTransitionView);
        return new GroupTransition(newGroupTransitionView);
    }

    public void groupTransitionsHelper(ArrayList<TransitionView> transitionsToHide,
                                       GroupTransitionView newGroupTransitionView) {
        //        if (transitionsToHide == null) {
        //            return;
        //        }
        //
        //        PetriNetTab view = ApplicationSettings.getApplicationView().getCurrentTab();
        //        PetriNetView model = ApplicationSettings.getApplicationView().getCurrentPetriNetView();
        //
        //        int i = 0;
        //        for (TransitionView transitionViewToGroup : transitionsToHide) {
        //            transitionViewToGroup.hideFromCanvas();
        //            transitionViewToGroup.hideAssociatedArcs();
        //            transitionViewToGroup.bindToGroup(newGroupTransitionView);
        //            newGroupTransitionView.addTransition(transitionViewToGroup);
        //            if (i == 0) {
        //                newGroupTransitionView.setName(transitionViewToGroup.getName());
        //            } else {
        //                newGroupTransitionView
        //                        .setName(newGroupTransitionView.getName() + "_" + transitionViewToGroup.getName());
        //            }
        //            i++;
        //        }
        //
        //        for (ArcView tempArcView : inboundArcs()) {
        //            ArcView newArcView = new NormalArcView(tempArcView.getStartPositionX(), tempArcView.getStartPositionY(),
        //                    tempArcView.getArcPath().getPoint(1).getX(), tempArcView.getArcPath().getPoint(1).getY(),
        //                    tempArcView.getSource(), newGroupTransitionView, new LinkedList<MarkingView>(), "", false,
        //                    new NormalArc(tempArcView.getSource().getModel(), newGroupTransitionView.getModel(),
        //                            new HashMap<Token, String>()), petriNetController);
        //            newGroupTransitionView.addInbound(newArcView);
        //            tempArcView.getSource().addOutbound(newArcView);
        //            newArcView.addToView(view);
        //        }
        //        for (ArcView tempArcView : outboundArcs()) {
        //            ArcView newArcView = new NormalArcView(tempArcView.getStartPositionX(), tempArcView.getStartPositionY(),
        //                    tempArcView.getArcPath().getPoint(1).getX(), tempArcView.getArcPath().getPoint(1).getY(),
        //                    newGroupTransitionView, tempArcView.getTarget(), new LinkedList<MarkingView>(), "", false,
        //                    new NormalArc(newGroupTransitionView.getModel(), tempArcView.getSource().getModel(),
        //                            new HashMap<Token, String>()), petriNetController);
        //            newGroupTransitionView.addOutbound(newArcView);
        //            tempArcView.getTarget().addInbound(newArcView);
        //            newArcView.addToView(view);
        //        }
        //        newGroupTransitionView.setVisible(true);
        //        newGroupTransitionView.getNameLabel().setVisible(true);
        //        view.addNewPetriNetObject(newGroupTransitionView);
        //        model.addPetriNetObject(newGroupTransitionView);
        //        newGroupTransitionView.repaint();
    }

    private ArrayList<TransitionView> groupTransitionsValidation() {
        //        if (!this.isSelected()) {
        //            JOptionPane.showMessageDialog(null, "You can only choose this option on selected transitions",
        //                    "Invalid selection", JOptionPane.ERROR_MESSAGE);
        //            return null;
        //        }
        //        PetriNetTab view = ApplicationSettings.getApplicationView().getCurrentTab();
        //        ArrayList<PetriNetViewComponent> pns = view.getPNObjects();
        ArrayList<TransitionView> transitionsToHide = new ArrayList<TransitionView>();
        //
        //        ArrayList<PlaceView> thisOutputPlaceViews = new ArrayList<PlaceView>();
        //        for (ArcView tempArcView : outboundArcs()) {
        //            thisOutputPlaceViews.add((PlaceView) (tempArcView.getTarget()));
        //        }
        //
        //        ArrayList<PlaceView> thisInputPlaceViews = new ArrayList<PlaceView>();
        //        for (ArcView tempArcView : inboundArcs()) {
        //            thisInputPlaceViews.add((PlaceView) (tempArcView.getSource()));
        //        }
        //
        //        ArrayList<PlaceView> currentOutputPlaceViews;
        //        ArrayList<PlaceView> currentInputPlaceViews;
        //
        //        for (PetriNetViewComponent pn : pns) {
        //            if (pn.isSelected()) {
        //                //TODO: HOW TO DESELECT?
        ////                pn.deselect();
        //                if (pn instanceof TransitionView) {
        //                    if (this != pn) {
        //                        currentOutputPlaceViews = new ArrayList<PlaceView>();
        //
        //                        LinkedList<ArcView> outboundArcViews = ((TransitionView) pn).outboundArcs();
        //                        for (ArcView tempArcView : outboundArcViews) {
        //                            currentOutputPlaceViews.add((PlaceView) (tempArcView.getTarget()));
        //                        }
        //
        //                        if (!thisOutputPlaceViews.equals(currentOutputPlaceViews)) {
        //                            showWarningDialog(
        //                                    "In order to be grouped, selected transitions must have the same output places");
        //                            return null;
        //                        }
        //
        //                        currentInputPlaceViews = new ArrayList<PlaceView>();
        //
        //                        LinkedList<ArcView> inboundArcViews = ((TransitionView) pn).inboundArcs();
        //                        for (ArcView tempArcView : inboundArcViews) {
        //                            currentInputPlaceViews.add((PlaceView) (tempArcView.getSource()));
        //                        }
        //
        //                        if (!thisInputPlaceViews.equals(currentInputPlaceViews)) {
        //                            showWarningDialog(
        //                                    "In order to be grouped, selected transitions must have the same input places");
        //                            return null;
        //                        }
        //                    }
        //                    transitionsToHide.add(((TransitionView) pn));
        //                }
        //            }
        //        }
        //
        //        if (transitionsToHide.size() < 2) {
        //            JOptionPane.showMessageDialog(null, "Please select 2 or more transitions to group", "Invalid selection",
        //                    JOptionPane.ERROR_MESSAGE);
        //            return null;
        //        }
        return transitionsToHide;
    }

    public void hideFromCanvas() {
        this.setVisible(false);
        this.getNameLabel().setVisible(false);
    }

    public void unhideFromCanvas() {
        this.setVisible(true);
        this.getNameLabel().setVisible(true);
    }

    public void hideAssociatedArcs() {
        for (ArcView tempArcView : outboundArcs()) {
            tempArcView.removeFromView();
        }

        for (ArcView tempArcView : inboundArcs()) {
            tempArcView.removeFromView();
        }
    }

    public void showAssociatedArcs() {
        PetriNetTab view = ApplicationSettings.getApplicationView().getCurrentTab();
        for (ArcView tempArcView : outboundArcs()) {
            tempArcView.addToView(view);
        }
        for (ArcView tempArcView : inboundArcs()) {
            tempArcView.addToView(view);
        }
    }

    public boolean isConst() {
        try {
            Double.parseDouble(model.getRateExpr());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public HistoryItem setRate(double rate) {
        String oldRate = model.getRateExpr();
        model.setRateExpr(rate + "");
        _nameLabel.setText(getAttributes());
        repaint();
        return new TransitionRate(this, oldRate, model.getRateExpr());
    }

    public HistoryItem setRate(String rate) {
        String oldRate = model.getRateExpr();
        model.setRateExpr(rate + "");
        _nameLabel.setText(getAttributes());
        repaint();
        return new TransitionRate(this, oldRate, model.getRateExpr());
    }

    class ArcAngleCompare implements Comparable {

        private final static boolean SOURCE = false;
        private final static boolean TARGET = true;
        private final ArcView<? extends Connectable, ? extends Connectable> _arcView;
        private final TransitionView _transitionView;
        private double angle;

        public ArcAngleCompare(ArcView<? extends Connectable, ? extends Connectable> arcView,
                               TransitionView transitionView) {
            this._arcView = arcView;
            this._transitionView = transitionView;
            calcAngle();
        }

        private void calcAngle() {
            int index = sourceOrTarget() ? _arcView.getArcPath().getEndIndex() - 1 : 1;
            Point2D.Double p1 = new Point2D.Double(model.getX() + centreOffsetLeft(), model.getY() + centreOffsetTop());
            Point2D.Double p2 = new Point2D.Double(_arcView.getArcPath().getPoint(index).x,
                    _arcView.getArcPath().getPoint(index).y);

            if (p1.y <= p2.y) {
                angle = Math.atan((p1.x - p2.x) / (p2.y - p1.y));
            } else {
                angle = Math.atan((p1.x - p2.x) / (p2.y - p1.y)) + Math.PI;
            }

            if (angle < (Math.toRadians(30 + _transitionView.getAngle()))) {
                angle += (2 * Math.PI);
            }

            if (p1.equals(p2)) {
                angle = 0;
            }
        }

        private boolean sourceOrTarget() {
            return (_arcView.getModel().getSource() instanceof Transition ? SOURCE : TARGET);
        }

        public int compareTo(Object arg0) {
            double angle2 = ((ArcAngleCompare) arg0).angle;
            return (angle < angle2 ? -1 : (angle == angle2 ? 0 : 1));
        }

    }
}
