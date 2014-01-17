package pipe.views;

import pipe.gui.ApplicationSettings;
import pipe.gui.Constants;
import pipe.gui.Grid;
import pipe.gui.PetriNetTab;
import pipe.gui.widgets.EscapableDialog;
import pipe.gui.widgets.GroupTransitionEditorPanel;
import pipe.handlers.GroupTransitionHandler;
import pipe.historyActions.GroupTransitionRotation;
import pipe.historyActions.HistoryItem;
import pipe.historyActions.UngroupTransition;
import pipe.models.component.Connectable;
import pipe.models.component.transition.Transition;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class GroupTransitionView extends ConnectableView<Transition> implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final int TRANSITION_HEIGHT = Constants.PLACE_TRANSITION_HEIGHT;

    private static final int TRANSITION_WIDTH = TRANSITION_HEIGHT / 3;

    private static final double rootThreeOverTwo = 0.5 * Math.sqrt(3);

    private final ArrayList arcAngleList = new ArrayList();

    private final ArrayList<TransitionView> _groupedTransitionViews = new ArrayList<TransitionView>();

    public boolean highlighted = false;

    private GeneralPath transition;

    private Shape proximityTransition;

    private int angle;

    private int enabled = 0;

    private boolean enabledBackwards = false;

    private double delay;

    private boolean delayValid;

    private TransitionView _foldedInto;

    public GroupTransitionView(TransitionView _foldedInto, double positionXInput, double positionYInput) {
        //MODEL
        super(new Transition("", ""));
        this._foldedInto = _foldedInto;
        constructTransition();
        updateBounds();
        updateEndPoints();
    }

    //TODO: DELETE
    void updateEndPoints() {
    }

    private void constructTransition() {
        transition = new GeneralPath();
        transition.append(new Rectangle2D.Double((model.getHeight() - TRANSITION_WIDTH) / 2, 0, TRANSITION_WIDTH,
                TRANSITION_HEIGHT), false);
        outlineTransition();
    }

    private void outlineTransition() {
        proximityTransition =
                (new BasicStroke(Constants.PLACE_TRANSITION_PROXIMITY_RADIUS)).createStrokedShape(transition);
    }

    public GroupTransitionView copy() {
        GroupTransitionView copy = new GroupTransitionView(_foldedInto, this.getX(), this.getY());
        copy.angle = this.angle;
        copy._attributesVisible = this._attributesVisible;
        copy.setOriginal(this);
        return copy;
    }

    public GroupTransitionView paste(double x, double y, boolean fromAnotherView, PetriNetView model) {
        GroupTransitionView copy = new GroupTransitionView(_foldedInto,
                x + this.getX() + Constants.PLACE_TRANSITION_HEIGHT / 2,
               y + this.getY() + Constants.PLACE_TRANSITION_HEIGHT / 2);


        this.newCopy(copy);


        copy.angle = this.angle;

        copy._attributesVisible = this._attributesVisible;
        copy.transition.transform(
                AffineTransform.getRotateInstance(Math.toRadians(copy.angle), GroupTransitionView.TRANSITION_HEIGHT / 2,
                        GroupTransitionView.TRANSITION_HEIGHT / 2));
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

        //if (timed) {
        //	if (infiniteServer) {
        for (int i = 2; i >= 1; i--) {
            g2.translate(2 * i, -2 * i);
            g2.fill(transition);
            Paint pen = g2.getPaint();
            if (highlighted) {
                g2.setPaint(Constants.ENABLED_TRANSITION_COLOUR);
            } else if (isSelected() && !_ignoreSelection) {
                g2.setPaint(Constants.SELECTION_LINE_COLOUR);
            } else {
                g2.setPaint(Constants.ELEMENT_LINE_COLOUR);
            }
            g2.draw(transition);
            g2.setPaint(pen);
            g2.translate(-2 * i, 2 * i);
        }
        //}
        g2.fill(transition);
        //}

        if (highlighted) {
            g2.setPaint(Constants.ENABLED_TRANSITION_COLOUR);
        } else if (isSelected() && !_ignoreSelection) {
            g2.setPaint(Constants.SELECTION_LINE_COLOUR);
        } else {
            g2.setPaint(Constants.ELEMENT_LINE_COLOUR);
        }

        g2.draw(transition);
        /*if (!timed) {
              //if (infiniteServer) {
                  for (int i = 2; i >= 1; i--) {
                      g2.translate(2 * i, -2 * i);
                      Paint pen = g2.getPaint();
                      g2.setPaint(Constants.ELEMENT_FILL_COLOUR);
                      g2.fill(transition);
                      g2.setPaint(pen);
                      g2.draw(transition);
                      g2.translate(-2 * i, 2 * i);
                  }
              //}
              g2.draw(transition);
              g2.fill(transition);
          }*/
    }

    void setCentre(double x, double y) {
        super.setCentre(x, y);
        update();
    }

    public void delete() {
        JOptionPane.showMessageDialog(null, "You cannot delete a Group Transition." +
                " To delete transitions within a Group Transition " +
                "first ungroup the Group Transition");
    }

    public void showEditor() {
        // Build interface
        EscapableDialog guiDialog = new EscapableDialog(ApplicationSettings.getApplicationView(), "PIPE2", true);

        GroupTransitionEditorPanel te = new GroupTransitionEditorPanel(guiDialog.getRootPane(), this,
                ApplicationSettings.getApplicationView().getCurrentPetriNetView(),
                ApplicationSettings.getApplicationView().getCurrentTab());

        guiDialog.add(te);

        guiDialog.getRootPane().setDefaultButton(null);

        guiDialog.setResizable(false);

        // Make window fit contents' preferred size
        guiDialog.pack();

        // Move window to the middle of the screen
        guiDialog.setLocationRelativeTo(null);

        guiDialog.setVisible(true);

        guiDialog.dispose();
    }

    public void addedToGui() {
        super.addedToGui();
        update();
    }

    public void update() {
        this.repaint();
    }

    private String getAttributes() { // NOU-PERE
        return "";
    }

    public void toggleAttributesVisible() {
        _attributesVisible = !_attributesVisible;
    }

    @Override
    public void addToPetriNetTab(PetriNetTab tab) {
        GroupTransitionHandler groupTransitionHandler = new GroupTransitionHandler(this, tab, this, petriNetController);
        addMouseListener(groupTransitionHandler);
        addMouseMotionListener(groupTransitionHandler);
        addMouseWheelListener(groupTransitionHandler);
        addMouseListener(tab.getAnimationHandler());
    }

    /**
     * Rotates the GroupTransition through the specified angle around the midpoint
     *
     * @param angleInc
     * @return
     */
    public HistoryItem rotate(int angleInc) {
        angle = (angle + angleInc) % 360;
        transition.transform(AffineTransform.getRotateInstance(Math.toRadians(angleInc), model.getHeight() / 2,
                model.getHeight() / 2));
        outlineTransition();

        Iterator<?> arcIterator = arcAngleList.iterator();
        while (arcIterator.hasNext()) {
            ((ArcAngleCompare) arcIterator.next()).calcAngle();
        }
        Collections.sort(arcAngleList);

        updateEndPoints();
        repaint();

        return new GroupTransitionRotation(this, angleInc);
    }

    /**
     * Determines whether GroupTransition is enabled
     *
     * @param animationStatus Anamation status
     * @return True if enabled
     */
    public boolean isEnabled(boolean animationStatus) {
        if (animationStatus) {
            if (enabled > 0) {
                highlighted = true;
                return true;
            } else {
                highlighted = false;
            }
        }
        return false;
    }

    /**
     * Determines whether GroupTransition is enabled backwards
     *
     * @return True if enabled
     */
    public boolean isEnabledBackwards() {
        return enabledBackwards;
    }

    /**
     * Sets whether GroupTransition is enabled
     *
     * @param status
     * @return enabled if True
     */
    public void setEnabledBackwards(boolean status) {
        enabledBackwards = status;
    }

    /**
     * Determines whether GroupTransition is enabled
     *
     * @return True if enabled
     */
    public boolean isEnabled() {
        return enabled > 0;
    }

    /**
     * Sets whether GroupTransition is enabled
     *
     * @return enabled if True
     */
    public void setEnabled(boolean status) {
        if (enabled > 0 && !status) { // going from enabled to disabled
            delayValid = false; // mark that delay is not valid
        }
        if (status) {
            enabled++;
        } else if (enabled > 0) {
            enabled--;
        } else {
            highlighted = false;
        }
    }

    public void setHighlighted(boolean status) {
        highlighted = status;
    }

    /**
     * This is a getter for the delay for this transition.
     *
     * @return a double with the amount of delay
     * @author Dave Patterson as part of the Exponential Distribution support
     * for timed transitions.
     */
    public double getDelay() {
        return delay;
    }

    /**
     * This is a setter for the delay for this transition.
     *
     * @param _delay the time until this transition will fire
     * @author Dave Patterson as part of the Exponential Distribution support
     * for timed transitions.
     */
    public void setDelay(double _delay) {
        delay = _delay;
        delayValid = true;
    }

    /**
     * This method is a getter for the boolean indicating if the delay is valid
     * or not.
     *
     * @return the delayValid a boolean that is true if the delay is valid, and
     * false otherwise
     * @author Dave Patterson as part of the Exponential Distribution support
     * for timed transitions.
     */
    public boolean isDelayValid() {
        return delayValid;
    }

    /**
     * This method is used to set a flag to indicate that the delay is valid or
     * invalid. (Mainly it is used to invalidate the delay.)
     *
     * @param _delayValid a boolean that is true if the delay is valid, false otherwise
     * @author Dave Patterson as part of the Exponential Distribution support
     * for timed transitions.
     */
    public void setDelayValid(boolean _delayValid) {
        delayValid = _delayValid;
    }

    /* Called at the end of animation to reset Transitions to false */
    public void setEnabledFalse() {
        enabled = 0;
        highlighted = false;
    }

    int getAngle() {
        return angle;
    }

    public boolean contains(int x, int y) {

        double unZoomedX = (x - getComponentDrawOffset());
        double unZoomedY = (y - getComponentDrawOffset());


        //TODO: FIGURE OUT WHAT THIS DOES
        ArcView someArcView = null; //ApplicationSettings.getApplicationView().getCurrentTab()._createArcView;
        //        if (someArcView != null) { // Must be drawing a new Arc if non-NULL.
        //            if ((proximityTransition.contains((int) unZoomedX, (int) unZoomedY) ||
        //                    transition.contains((int) unZoomedX, (int) unZoomedY)) && areNotSameType(someArcView.getSource())) {
        //                // assume we are only snapping the target...
        //                if (someArcView.getTarget() != this) {
        //                    someArcView.setTarget(this);
        //                }
        //                someArcView.updateArcPosition();
        //                return true;
        //            } else {
        //                if (someArcView.getTarget() == this) {
        //                    someArcView.setTarget(null);
        //                    removeArcCompareObject(someArcView);
        //                    updateConnected();
        //                }
        //                return false;
        //            }
        //        } else {
        return transition.contains((int) unZoomedX, (int) unZoomedY);
        //        }
    }

    void removeArcCompareObject(ArcView a) {
        Iterator<?> arcIterator = arcAngleList.iterator();
        while (arcIterator.hasNext()) {
            if (((ArcAngleCompare) arcIterator.next())._arcView == a) {
                arcIterator.remove();
            }
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see
      * pipe.models.component.Connectable#updateEndPoint(pipe.models.component.arc.Arc)
      */
    public void updateEndPoint(ArcView arcView) {
        boolean match = false;

        Iterator<?> arcIterator = arcAngleList.iterator();
        while (arcIterator.hasNext()) {
            ArcAngleCompare thisArc = (ArcAngleCompare) arcIterator.next();
            if (thisArc._arcView == arcView || !arcView.inView()) {
                thisArc.calcAngle();
                match = true;
                break;
            }
        }

        if (!match) {
            arcAngleList.add(new ArcAngleCompare(arcView, this));
        }

        Collections.sort(arcAngleList);
        updateEndPoints();
    }

    public HistoryItem ungroupTransitions() {
        ungroupTransitionsHelper();
        return new UngroupTransition(this);
    }

    public void ungroupTransitionsHelper() {
        for (TransitionView t : _groupedTransitionViews) {
            t.unhideFromCanvas();
            t.showAssociatedArcs();
            t.ungroupTransition();
        }
        deleteAssociatedArcs();
        setVisible(false);
    }

    public void deleteAssociatedArcs() {
        for (ArcView tempArcView : inboundArcs()) {
            tempArcView.removeFromView();
        }

        for (ArcView tempArcView : outboundArcs()) {
            tempArcView.removeFromView();
        }
    }

    public void hideAssociatedArcs() {
        for (ArcView tempArcView : inboundArcs()) {
            tempArcView.setVisible(false);
        }

        for (ArcView tempArcView : outboundArcs()) {
            tempArcView.setVisible(false);
        }
    }

    public void showAssociatedArcs() {
        for (ArcView tempArcView : this.inboundArcs()) {
            tempArcView.setVisible(true);
        }

        for (ArcView tempArcView : this.outboundArcs()) {
            tempArcView.setVisible(true);
        }
    }

    public void addTransition(TransitionView t) {
        _groupedTransitionViews.add(t);
    }

    public void removeTransition(TransitionView t) {
        _groupedTransitionViews.remove(t);
    }

    public ArrayList<TransitionView> getTransitions() {
        return _groupedTransitionViews;
    }

    public TransitionView getFoldedInto() {
        return _foldedInto;
    }

    public void setFoldedInto(TransitionView t) {
        _foldedInto = t;
    }

    class ArcAngleCompare implements Comparable {

        private final static boolean SOURCE = false;

        private final static boolean TARGET = true;

        private final ArcView<? extends Connectable, ? extends Connectable> _arcView;

        private final GroupTransitionView _transitionView;

        private double angle;

        public ArcAngleCompare(ArcView<? extends Connectable, ? extends Connectable> _arcView,
                               GroupTransitionView _transitionView) {
            this._arcView = _arcView;
            this._transitionView = _transitionView;
            calcAngle();
        }

        private void calcAngle() {
            int index = sourceOrTarget() ? _arcView.getArcPath().getEndIndex() - 1 : 1;
            Point2D.Double p1 = new Point2D.Double(getX() + centreOffsetLeft(), getY() + centreOffsetTop());
            Point2D.Double p2 = new Point2D.Double(_arcView.getArcPath().getPoint(index).getX(),
                    _arcView.getArcPath().getPoint(index).getY());

            if (p1.y <= p2.y) {
                angle = Math.atan((p1.x - p2.x) / (p2.y - p1.y));
            } else {
                angle = Math.atan((p1.x - p2.x) / (p2.y - p1.y)) + Math.PI;
            }

            // This makes sure the angle overlap lies at the intersection
            // between
            // edges of a transition
            // Yes it is a nasty hack (a.k.a. ingeneous solution). But it works!
            if (angle < (Math.toRadians(30 + _transitionView.getAngle()))) {
                angle += (2 * Math.PI);
            }

            // Needed to eliminate an exception on Windows
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
