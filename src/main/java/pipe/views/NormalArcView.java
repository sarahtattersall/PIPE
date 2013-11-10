package pipe.views;

import pipe.controllers.ArcController;
import pipe.gui.ApplicationSettings;
import pipe.gui.Constants;
import pipe.gui.ZoomController;
import pipe.historyActions.*;
import pipe.models.NormalArc;
import pipe.utilities.Copier;
import pipe.views.viewComponents.NameLabel;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


public class NormalArcView extends ArcView<NormalArc> implements Serializable {
    private final static String type = "normal";
    private final static Polygon head = new Polygon(new int[]{0, 5, 0, -5}, new int[]{0, -10, -7, -10}, 4);

    // bidirectional arc?
    private boolean joined = false;

    // Whether or not exists an inverse arc
    private NormalArcView _inverse = null;

    private Boolean tagged = false;
    private ArcController _controller;

    public NormalArcView(double startPositionXInput, double startPositionYInput, double endPositionXInput,
            double endPositionYInput, ConnectableView sourceInput, ConnectableView targetInput,
            List<MarkingView> weightInput, String idInput, boolean taggedInput, NormalArc model) {

        super(startPositionXInput, startPositionYInput, endPositionXInput, endPositionYInput, sourceInput, targetInput,
                weightInput, idInput, model);
        setTagged(taggedInput);
    }

    /**
     * Create Petri-Net Arc object
     *
     * @param newSource
     */
    public NormalArcView(ConnectableView newSource) {
        super(newSource);
    }

    public NormalArcView(NormalArcView arc) {

        for (int i = 0; i <= arc.myPath.getEndIndex(); i++) {
            this.myPath
                    .addPoint(arc.myPath.getPoint(i).getX(), arc.myPath.getPoint(i).getY(), arc.myPath.getPointType(i));
        }
        this.myPath.createPath();
        this.updateBounds();
        this._id = arc._id;
        this.setSource(arc.getSource());
        this.setTarget(arc.getTarget());
        this.setWeight(Copier.mediumCopy(arc.getWeight()));
        this.inView = arc.inView;
        this.joined = arc.joined;
    }

    public NormalArcView(ArcController arcController, NormalArc model) {
        _controller = arcController;
        this.model = model;
        this.model.registerObserver(this);
    }

    public NormalArcView paste(double despX, double despY, boolean toAnotherView, PetriNetView model) {
        ConnectableView source = this.getSource().getLastCopy();
        ConnectableView target = this.getTarget().getLastCopy();

        if (source == null && target == null) {
            // don't paste an arc with neither source nor target
            return null;
        }

        if (source == null) {
            if (toAnotherView) {
                // if the source belongs to another Petri Net, the arc can't be
                // pasted
                return null;
            } else {
                source = this.getSource();
            }
        }

        if (target == null) {
            if (toAnotherView) {
                // if the target belongs to another Petri Net, the arc can't be
                // pasted
                return null;
            } else {
                target = this.getTarget();
            }
        }

        NormalArcView copy =
                new NormalArcView((double) 0, (double) 0, (double) 0, (double) 0, source, target, getWeight(),
                        source.getId() + " to " +
                                target.getId(), false,
                        new NormalArc(source.getModel(), target.getModel(), this.model.getWeight()));

        copy.myPath.delete();
        for (int i = 0; i <= this.myPath.getEndIndex(); i++) {
            copy.myPath.addPoint(this.myPath.getPoint(i).getX() + despX, this.myPath.getPoint(i).getY() + despY,
                    this.myPath.getPointType(i));
            copy.myPath.selectPoint(i);
        }

        source.addOutbound(copy);
        target.addInbound(copy);

        copy.inView = this.inView;
        copy.joined = this.joined;

        return copy;
    }

    public NormalArcView copy() {
        return new NormalArcView(this);
    }

    public String getType() {
        return type;
    }

    /**
     * Accessor function to set whether or not the Arc is tagged
     *
     * @param flag
     */
    public void setTagged(boolean flag) {
        /** Set the timed transition attribute (for GSPNs) */

        tagged = flag;

        // If it becomes tagged we must remove any existing weight....
        // ...and thus we can reuse the weightLabel to display that it's
        // tagged!!!
        // Because remember that a tagged arc must have a weight of 1...
        /*
		 * if (tagged) { //weight = 1; weightLabel.setText("TAG");
		 * setWeightLabelPosition(); weightLabel.updateSize(); } else {
		 * weightLabel.setText((weight > 1)?Integer.toString(weight) : ""); }
		 */
        repaint();
    }

    /**
     * Accessor function to check whether or not the Arc is tagged
     */
    public boolean isTagged() {
        return tagged;
    }

    void updateWeightLabel() {

        setWeightLabelPosition();

    }

    public void setInView(boolean flag) {
        inView = flag;
    }

    void setJoined(boolean flag) {
        joined = flag;
    }

    public HistoryItem clearInverse() {
        NormalArcView oldInverse = _inverse;

        _inverse.inView = true;
        inView = true;

        _inverse.joined = false;
        joined = false;

        _inverse.updateWeightLabel();
        updateWeightLabel();

        _inverse._inverse = null;
        _inverse = null;

        return new ClearInverseArc(this, oldInverse, false);
    }

    public boolean hasInverse() {
        return _inverse != null;
    }

    public NormalArcView getInverse() {
        return _inverse;
    }

    public HistoryItem setInverse(NormalArcView _inverse, boolean joined) {
        this._inverse = _inverse;
        this._inverse._inverse = this;
        updateArc(joined);
        return new SetInverseArc(this, this._inverse, joined);
    }

    private void updateArc(boolean isJoined) {
        inView = true;
        _inverse.inView = !isJoined;

        if (isJoined) {
            _inverse.removeFromView();
            TransitionView transitionView = this.getTransition();
            transitionView.removeFromArc(_inverse);
            transitionView.removeArcCompareObject(_inverse);
            transitionView.updateConnected();
            joined = isJoined;
        }
        updateWeightLabel();
    }

    public boolean isJoined() {
        return joined;
    }

    public HistoryItem split() {
        //
        if (!this._inverse.inView) {
            ApplicationSettings.getApplicationView().getCurrentTab().add(_inverse);
            _inverse.getSource().addOutbound(_inverse);
            _inverse.getTarget().addInbound(_inverse);
        }
        if (!this.inView) {
            ApplicationSettings.getApplicationView().getCurrentTab().add(this);
            this.getSource().addOutbound(this);
            this.getTarget().addInbound(this);
        }

        //
        _inverse.inView = true;
        this.inView = true;
        this.joined = false;
        _inverse.joined = false;

        this.updateWeightLabel();
        _inverse.updateWeightLabel();

        this.updateArcPosition();
        _inverse.updateArcPosition();

        return new SplitInverseArc(this);
    }

    public HistoryItem join() {
        this.updateArc(true);
        // ((NormalArc)arc.getInverse()).setInView(false);
        // arc.getParent().remove(arc.getInverse());
        _inverse.removeFromView();
        this.setJoined(true);
        if (this.getParent() != null) {
            this.getParent().repaint();
        }

        return new JoinInverseArc(this);
    }

    public boolean hasInvisibleInverse() {
        return ((this._inverse != null) && !(this._inverse.inView()));
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.translate(getComponentDrawOffset() + zoomGrow - myPath.getBounds().getX(),
                getComponentDrawOffset() + zoomGrow - myPath.getBounds().getY());

        AffineTransform reset = g2.getTransform();

        if (_selected && !_ignoreSelection) {
            g2.setPaint(Constants.SELECTION_LINE_COLOUR);
        } else {
            g2.setPaint(Constants.ELEMENT_LINE_COLOUR);
        }

        if (joined) {
            g2.translate(myPath.getPoint(0).getX(), myPath.getPoint(0).getY());
            g2.rotate(myPath.getStartAngle() + Math.PI);
            g2.transform(ZoomController.getTransform(_zoomPercentage));
            g2.fillPolygon(head);
            g2.setTransform(reset);
        }

        g2.setStroke(new BasicStroke(0.01f * _zoomPercentage));
        g2.draw(myPath);

        g2.translate(myPath.getPoint(myPath.getEndIndex()).getX(), myPath.getPoint(myPath.getEndIndex()).getY());

        g2.rotate(myPath.getEndAngle() + Math.PI);
        g2.setColor(java.awt.Color.WHITE);

        g2.transform(ZoomController.getTransform(_zoomPercentage));
        g2.setPaint(Constants.ELEMENT_LINE_COLOUR);

        if (_selected && !_ignoreSelection) {
            g2.setPaint(Constants.SELECTION_LINE_COLOUR);
        } else {
            g2.setPaint(Constants.ELEMENT_LINE_COLOUR);
        }

        g2.setStroke(new BasicStroke(0.8f));
        g2.fillPolygon(head);

        g2.transform(reset);
    }
}
