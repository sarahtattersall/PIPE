package pipe.views;

import pipe.gui.*;
import pipe.historyActions.GroupTransitionRotation;
import pipe.historyActions.HistoryItem;
import pipe.historyActions.UngroupTransition;
import pipe.gui.widgets.EscapableDialog;
import pipe.gui.widgets.GroupTransitionEditorPanel;
import pipe.models.Transition;

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

public class GroupTransitionView extends ConnectableView implements Serializable
{
    private static final long serialVersionUID = 1L;
    private GeneralPath transition;
    private Shape proximityTransition;
    private static final int TRANSITION_HEIGHT = Constants.PLACE_TRANSITION_HEIGHT;
    private static final int TRANSITION_WIDTH = TRANSITION_HEIGHT / 3;
    private int angle;
    private int enabled = 0;
    private boolean enabledBackwards = false;
    public boolean highlighted = false;
    private double delay;
    private boolean delayValid;
    private static final double rootThreeOverTwo = 0.5 * Math.sqrt(3);
    private final ArrayList arcAngleList = new ArrayList();
    private final ArrayList<TransitionView> _groupedTransitionViews = new ArrayList<TransitionView>();
    private TransitionView _foldedInto;

    public GroupTransitionView(TransitionView _foldedInto, double positionXInput, double positionYInput)
    {
        //MODEL
        super(positionXInput, positionYInput, new Transition("",""));
        this._foldedInto = _foldedInto;
        _componentWidth = TRANSITION_HEIGHT;
        _componentHeight = TRANSITION_HEIGHT;
        constructTransition();
        setCentre((int) _positionX, (int) _positionY);
        updateBounds();
        updateEndPoints();
    }

    public GroupTransitionView paste(double x, double y, boolean fromAnotherView, PetriNetView model)
    {
        GroupTransitionView copy = new GroupTransitionView(_foldedInto, Grid.getModifiedX(x + this.getX() + Constants.PLACE_TRANSITION_HEIGHT / 2), Grid.getModifiedY(y + this.getY() + Constants.PLACE_TRANSITION_HEIGHT / 2));

        String newName = this._nameLabel.getName() + "(" + this.getCopyNumber() + ")";
        boolean properName = false;

        while(!properName)
        {
            if(model.checkTransitionIDAvailability(newName))
            {
                copy._nameLabel.setName(newName);
                properName = true;
            }
            else
            {
                newName = newName + "'";
            }
        }

        this.newCopy(copy);

        copy._nameOffsetX = this._nameOffsetX;
        copy._nameOffsetY = this._nameOffsetY;

        copy.angle = this.angle;

        copy._attributesVisible = this._attributesVisible;
        copy.transition.transform(AffineTransform.getRotateInstance(Math
                                                                            .toRadians(copy.angle), GroupTransitionView.TRANSITION_HEIGHT / 2,
                                                                    GroupTransitionView.TRANSITION_HEIGHT / 2));
        return copy;
    }

    public GroupTransitionView copy()
    {
        GroupTransitionView copy = new GroupTransitionView(_foldedInto, ZoomController.getUnzoomedValue(this.getX(),
                                                                                               _zoomPercentage), ZoomController.getUnzoomedValue(this.getY(), _zoomPercentage));
        copy._nameLabel.setName(this.getName());
        copy._nameOffsetX = this._nameOffsetX;
        copy._nameOffsetY = this._nameOffsetY;
        copy.angle = this.angle;
        copy._attributesVisible = this._attributesVisible;
        copy.setOriginal(this);
        return copy;
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        if(_selected && !_ignoreSelection)
        {
            g2.setColor(Constants.SELECTION_FILL_COLOUR);
        }
        else
        {
            g2.setColor(Constants.ELEMENT_FILL_COLOUR);
        }

        //if (timed) {
        //	if (infiniteServer) {
        for(int i = 2; i >= 1; i--)
        {
            g2.translate(2 * i, -2 * i);
            g2.fill(transition);
            Paint pen = g2.getPaint();
            if(highlighted)
            {
                g2.setPaint(Constants.ENABLED_TRANSITION_COLOUR);
            }
            else if(_selected && !_ignoreSelection)
            {
                g2.setPaint(Constants.SELECTION_LINE_COLOUR);
            }
            else
            {
                g2.setPaint(Constants.ELEMENT_LINE_COLOUR);
            }
            g2.draw(transition);
            g2.setPaint(pen);
            g2.translate(-2 * i, 2 * i);
        }
        //}
        g2.fill(transition);
        //}

        if(highlighted)
        {
            g2.setPaint(Constants.ENABLED_TRANSITION_COLOUR);
        }
        else if(_selected && !_ignoreSelection)
        {
            g2.setPaint(Constants.SELECTION_LINE_COLOUR);
        }
        else
        {
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

    /**
     * Rotates the GroupTransition through the specified angle around the midpoint
     * @param angleInc
     * @return
     */
    public HistoryItem rotate(int angleInc)
    {
        angle = (angle + angleInc) % 360;
        transition.transform(AffineTransform.getRotateInstance(Math
                                                                       .toRadians(angleInc), _componentWidth / 2, _componentHeight / 2));
        outlineTransition();

        Iterator<?> arcIterator = arcAngleList.iterator();
        while(arcIterator.hasNext())
        {
            ((ArcAngleCompare) arcIterator.next()).calcAngle();
        }
        Collections.sort(arcAngleList);

        updateEndPoints();
        repaint();

        return new GroupTransitionRotation(this, angleInc);
    }

    private void outlineTransition()
    {
        proximityTransition = (new BasicStroke(
                Constants.PLACE_TRANSITION_PROXIMITY_RADIUS))
                .createStrokedShape(transition);
    }

    /**
     * Determines whether GroupTransition is enabled
     *
     * @param animationStatus Anamation status
     * @return True if enabled
     */
    public boolean isEnabled(boolean animationStatus)
    {
        if(animationStatus)
        {
            if(enabled > 0)
            {
                highlighted = true;
                return true;
            }
            else
            {
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
    public boolean isEnabledBackwards()
    {
        return enabledBackwards;
    }

    /**
     * Determines whether GroupTransition is enabled
     *
     * @return True if enabled
     */
    public boolean isEnabled()
    {
        return enabled > 0;
    }

    public void setHighlighted(boolean status)
    {
        highlighted = status;
    }

    /**
     * Sets whether GroupTransition is enabled
     *
     * @return enabled if True
     */
    public void setEnabled(boolean status)
    {
        if(enabled > 0 && !status)
        { // going from enabled to disabled
            delayValid = false; // mark that delay is not valid
        }
        if(status)
        {
            enabled++;
        }
        else if(enabled > 0)
        {
            enabled--;
        }
        else
        {
            highlighted = false;
        }
    }

    /**
     * This is a setter for the delay for this transition.
     *
     * @param _delay the time until this transition will fire
     * @author Dave Patterson as part of the Exponential Distribution support
     * for timed transitions.
     */
    public void setDelay(double _delay)
    {
        delay = _delay;
        delayValid = true;
    }

    /**
     * This is a getter for the delay for this transition.
     *
     * @return a double with the amount of delay
     * @author Dave Patterson as part of the Exponential Distribution support
     * for timed transitions.
     */
    public double getDelay()
    {
        return delay;
    }

    /**
     * This method is a getter for the boolean indicating if the delay is valid
     * or not.
     *
     * @return the delayValid a boolean that is true if the delay is valid, and
     *         false otherwise
     * @author Dave Patterson as part of the Exponential Distribution support
     * for timed transitions.
     */
    public boolean isDelayValid()
    {
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
    public void setDelayValid(boolean _delayValid)
    {
        delayValid = _delayValid;
    }

    /**
     * Sets whether GroupTransition is enabled
     *
     * @param status
     * @return enabled if True
     */
    public void setEnabledBackwards(boolean status)
    {
        enabledBackwards = status;
    }

    /* Called at the end of animation to reset Transitions to false */
    public void setEnabledFalse()
    {
        enabled = 0;
        highlighted = false;
    }

    int getAngle()
    {
        return angle;
    }

    private void constructTransition()
    {
        transition = new GeneralPath();
        transition.append(new Rectangle2D.Double(
                (_componentWidth - TRANSITION_WIDTH) / 2, 0, TRANSITION_WIDTH,
                TRANSITION_HEIGHT), false);
        outlineTransition();
    }

    public boolean contains(int x, int y)
    {
        int zoomPercentage = _zoomPercentage;

        double unZoomedX = (x - getComponentDrawOffset())
                / (zoomPercentage / 100.0);
        double unZoomedY = (y - getComponentDrawOffset())
                / (zoomPercentage / 100.0);

        ArcView someArcView = ApplicationSettings.getApplicationView().getCurrentTab()._createArcView;
        if(someArcView != null)
        { // Must be drawing a new Arc if non-NULL.
            if((proximityTransition.contains((int) unZoomedX, (int) unZoomedY) || transition
                    .contains((int) unZoomedX, (int) unZoomedY))
                    && areNotSameType(someArcView.getSource()))
            {
                // assume we are only snapping the target...
                if(someArcView.getTarget() != this)
                {
                    someArcView.setTarget(this);
                }
                someArcView.updateArcPosition();
                return true;
            }
            else
            {
                if(someArcView.getTarget() == this)
                {
                    someArcView.setTarget(null);
                    removeArcCompareObject(someArcView);
                    updateConnected();
                }
                return false;
            }
        }
        else
        {
            return transition.contains((int) unZoomedX, (int) unZoomedY);
        }
    }

    void removeArcCompareObject(ArcView a)
    {
        Iterator<?> arcIterator = arcAngleList.iterator();
        while(arcIterator.hasNext())
        {
            if(((ArcAngleCompare) arcIterator.next())._arcView == a)
            {
                arcIterator.remove();
            }
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see
      * pipe.models.Connectable#updateEndPoint(pipe.models.Arc)
      */
    public void updateEndPoint(ArcView arcView)
    {
        boolean match = false;

        Iterator<?> arcIterator = arcAngleList.iterator();
        while(arcIterator.hasNext())
        {
            ArcAngleCompare thisArc = (ArcAngleCompare) arcIterator.next();
            if(thisArc._arcView == arcView || !arcView.inView())
            {
                thisArc.calcAngle();
                match = true;
                break;
            }
        }

        if(!match)
        {
            arcAngleList.add(new ArcAngleCompare(arcView, this));
        }

        Collections.sort(arcAngleList);
        updateEndPoints();
    }

    void updateEndPoints()
    {
        ArrayList<ArcAngleCompare> top = new ArrayList<ArcAngleCompare>();
        ArrayList<ArcAngleCompare> bottom = new ArrayList<ArcAngleCompare>();
        ArrayList<ArcAngleCompare> left = new ArrayList<ArcAngleCompare>();
        ArrayList<ArcAngleCompare> right = new ArrayList<ArcAngleCompare>();

        Iterator<?> arcIterator = arcAngleList.iterator();
        while(arcIterator.hasNext())
        {
            ArcAngleCompare thisArc = (ArcAngleCompare) arcIterator.next();
            double thisAngle = thisArc.angle - Math.toRadians(angle);
            if(Math.cos(thisAngle) > (rootThreeOverTwo))
            {
                top.add(thisArc);
                thisArc._arcView.setPathToTransitionAngle(angle + 90);
            }
            else if(Math.cos(thisAngle) < -rootThreeOverTwo)
            {
                bottom.add(thisArc);
                thisArc._arcView.setPathToTransitionAngle(angle + 270);
            }
            else if(Math.sin(thisAngle) > 0)
            {
                left.add(thisArc);
                thisArc._arcView.setPathToTransitionAngle(angle + 180);
            }
            else
            {
                right.add(thisArc);
                thisArc._arcView.setPathToTransitionAngle(angle);
            }
        }

        AffineTransform transform = AffineTransform.getRotateInstance(Math
                                                                              .toRadians(angle + Math.PI));
        Point2D.Double transformed = new Point2D.Double();

        transform.concatenate(ZoomController.getTransform(_zoomPercentage));

        arcIterator = top.iterator();
        transform.transform(new Point2D.Double(1, 0.5 * TRANSITION_HEIGHT),
                            transformed); // +1 due to rounding making it off by 1
        while(arcIterator.hasNext())
        {
            ArcAngleCompare thisArc = (ArcAngleCompare) arcIterator.next();
            if(thisArc.sourceOrTarget())
            {
                thisArc._arcView.setTargetLocation(_positionX + centreOffsetLeft()
                                                      + transformed.x, _positionY + centreOffsetTop()
                        + transformed.y);
            }
            else
            {
                thisArc._arcView.setSourceLocation(_positionX + centreOffsetLeft()
                                                      + transformed.x, _positionY + centreOffsetTop()
                        + transformed.y);
            }
        }

        arcIterator = bottom.iterator();
        transform.transform(new Point2D.Double(0, -0.5 * TRANSITION_HEIGHT),
                            transformed);
        while(arcIterator.hasNext())
        {
            ArcAngleCompare thisArc = (ArcAngleCompare) arcIterator.next();
            if(thisArc.sourceOrTarget())
            {
                thisArc._arcView.setTargetLocation(_positionX + centreOffsetLeft()
                                                      + transformed.x, _positionY + centreOffsetTop()
                        + transformed.y);
            }
            else
            {
                thisArc._arcView.setSourceLocation(_positionX + centreOffsetLeft()
                                                      + transformed.x, _positionY + centreOffsetTop()
                        + transformed.y);
            }
        }

        arcIterator = left.iterator();
        double inc = TRANSITION_HEIGHT / (left.size() + 1);
        double current = TRANSITION_HEIGHT / 2 - inc;
        while(arcIterator.hasNext())
        {
            ArcAngleCompare thisArc = (ArcAngleCompare) arcIterator.next();
            transform.transform(new Point2D.Double(-0.5 * TRANSITION_WIDTH,
                                                   current + 1), transformed); // +1 due to rounding making it
            // off by 1
            if(thisArc.sourceOrTarget())
            {
                thisArc._arcView.setTargetLocation(_positionX + centreOffsetLeft()
                                                      + transformed.x, _positionY + centreOffsetTop()
                        + transformed.y);
            }
            else
            {
                thisArc._arcView.setSourceLocation(_positionX + centreOffsetLeft()
                                                      + transformed.x, _positionY + centreOffsetTop()
                        + transformed.y);
            }
            current -= inc;
        }

        inc = TRANSITION_HEIGHT / (right.size() + 1);
        current = -TRANSITION_HEIGHT / 2 + inc;
        arcIterator = right.iterator();
        while(arcIterator.hasNext())
        {
            ArcAngleCompare thisArc = (ArcAngleCompare) arcIterator.next();
            transform.transform(new Point2D.Double(+0.5 * TRANSITION_WIDTH,
                                                   current), transformed);
            if(thisArc.sourceOrTarget())
            {
                thisArc._arcView.setTargetLocation(_positionX + centreOffsetLeft()
                                                      + transformed.x, _positionY + centreOffsetTop()
                        + transformed.y);
            }
            else
            {
                thisArc._arcView.setSourceLocation(_positionX + centreOffsetLeft()
                                                      + transformed.x, _positionY + centreOffsetTop()
                        + transformed.y);
            }
            current += inc;
        }
    }

    public void addedToGui()
    {
        super.addedToGui();
        update();
    }

    private String getAttributes()
    { // NOU-PERE
        return "";
    }

    void setCentre(double x, double y)
    {
        super.setCentre(x, y);
        update();
    }

    public void toggleAttributesVisible()
    {
        _attributesVisible = !_attributesVisible;
        _nameLabel.setText(getAttributes());
    }

    public void showEditor()
    {
        // Build interface
        EscapableDialog guiDialog = new EscapableDialog(ApplicationSettings.getApplicationView(),
                                                        "PIPE2", true);

        GroupTransitionEditorPanel te = new GroupTransitionEditorPanel(guiDialog
                                                                               .getRootPane(), this, ApplicationSettings.getApplicationView().getCurrentPetriNetView(), ApplicationSettings.getApplicationView().getCurrentTab());

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


    public void update()
    {
        _nameLabel.setText(getAttributes());
        _nameLabel.zoomUpdate(_zoomPercentage);
        super.update();
        this.repaint();
    }

    public void delete()
    {
        JOptionPane.showMessageDialog(null, "You cannot delete a Group Transition." +
                " To delete transitions within a Group Transition " +
                "first ungroup the Group Transition");
    }

    class ArcAngleCompare implements Comparable
    {

        private final static boolean SOURCE = false;
        private final static boolean TARGET = true;
        private final ArcView _arcView;
        private final GroupTransitionView _transitionView;
        private double angle;

        public ArcAngleCompare(ArcView _arcView, GroupTransitionView _transitionView)
        {
            this._arcView = _arcView;
            this._transitionView = _transitionView;
            calcAngle();
        }

        public int compareTo(Object arg0)
        {
            double angle2 = ((ArcAngleCompare) arg0).angle;

            return (angle < angle2 ? -1 : (angle == angle2 ? 0 : 1));
        }

        private void calcAngle()
        {
            int index = sourceOrTarget() ? _arcView.getArcPath().getEndIndex() - 1
                    : 1;
            Point2D.Double p1 = new Point2D.Double(_positionX
                                                           + centreOffsetLeft(), _positionY + centreOffsetTop());
            Point2D.Double p2 = new Point2D.Double(_arcView.getArcPath().getPoint(
                    index).x, _arcView.getArcPath().getPoint(index).y);

            if(p1.y <= p2.y)
            {
                angle = Math.atan((p1.x - p2.x) / (p2.y - p1.y));
            }
            else
            {
                angle = Math.atan((p1.x - p2.x) / (p2.y - p1.y)) + Math.PI;
            }

            // This makes sure the angle overlap lies at the intersection
            // between
            // edges of a transition
            // Yes it is a nasty hack (a.k.a. ingeneous solution). But it works!
            if(angle < (Math.toRadians(30 + _transitionView.getAngle())))
            {
                angle += (2 * Math.PI);
            }

            // Needed to eliminate an exception on Windows
            if(p1.equals(p2))
            {
                angle = 0;
            }

        }

        private boolean sourceOrTarget()
        {
            return (_arcView.getSource() == _transitionView ? SOURCE : TARGET);
        }

    }

    public HistoryItem ungroupTransitions()
    {
        ungroupTransitionsHelper();
        return new UngroupTransition(this);
    }

    public void ungroupTransitionsHelper()
    {
        for(TransitionView t : _groupedTransitionViews)
        {
            t.unhideFromCanvas();
            t.showAssociatedArcs();
            t.ungroupTransition();
        }
        deleteAssociatedArcs();
        setVisible(false);
        getNameLabel().setVisible(false);
    }

    public void deleteAssociatedArcs()
    {
        for(ArcView tempArcView : inboundArcs())
            tempArcView.removeFromView();

        for(ArcView tempArcView : outboundArcs())
            tempArcView.removeFromView();
    }

    public void hideAssociatedArcs()
    {
        for(ArcView tempArcView : inboundArcs())
            tempArcView.setVisible(false);

        for (ArcView tempArcView : outboundArcs())
            tempArcView.setVisible(false);
    }

    public void showAssociatedArcs()
    {
        for (ArcView tempArcView : this.inboundArcs())
            tempArcView.setVisible(true);

        for (ArcView tempArcView : this.outboundArcs())
            tempArcView.setVisible(true);
    }

    public void addTransition(TransitionView t)
    {
        _groupedTransitionViews.add(t);
    }

    public void removeTransition(TransitionView t)
    {
        _groupedTransitionViews.remove(t);
    }

    public ArrayList<TransitionView> getTransitions()
    {
        return _groupedTransitionViews;
    }

    public void setFoldedInto(TransitionView t)
    {
        _foldedInto = t;
    }

    public TransitionView getFoldedInto()
    {
        return _foldedInto;
    }

}
