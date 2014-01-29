package pipe.views;

import pipe.controllers.PetriNetController;
import pipe.gui.ApplicationSettings;
import pipe.gui.Constants;
import pipe.gui.PetriNetTab;
import pipe.gui.ZoomController;
import pipe.historyActions.*;
import pipe.models.component.arc.Arc;
import pipe.models.component.Connectable;
import pipe.models.component.token.Token;
import pipe.views.viewComponents.NameLabel;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;


public class NormalArcView<S extends Connectable, T extends Connectable> extends ArcView<S,T> implements Serializable {
    private final static String type = "normal";
    private final static Polygon head = new Polygon(new int[]{0, 5, 0, -5}, new int[]{0, -10, -7, -10}, 4);
    private final Collection<NameLabel> weightLabel = new LinkedList<NameLabel>();
    private final java.util.List<MarkingView> _weight = new LinkedList<MarkingView>();
    // bidirectional arc?
    private boolean joined = false;
    // Whether or not exists an inverse arc
    private NormalArcView<S, T> _inverse = null;
    private Boolean tagged = false;

    public NormalArcView(Arc<S, T> model,
                         PetriNetController controller) {
        super(model, controller);
        setTagged(model.isTagged());
        addConnectableListener();

    }

    private void addConnectableListener() {
        PropertyChangeListener listener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                String name = propertyChangeEvent.getPropertyName();
                if (name.equals(Arc.WEIGHT_CHANGE_MESSAGE)) {
                    updateWeights();
                }
            }
        };
        model.addPropertyChangeListener(listener);
    }

    /**
     * Updates the weights associated with the arc
     */
    @Override
    public void arcSpecificUpdate() {
        updateWeights();
    }

    @Override
    public void delete() {
        super.delete();
        for (NameLabel label : weightLabel) {
            removeLabelFromParentContainer(label);
        }
    }

    private void removeLabelFromParentContainer(NameLabel label) {
        tab.remove(label);
    }

    @Override
    protected void arcSpecificDelete() {
    }

    @Override
    protected void arcSpecificAdd() {
        for (NameLabel label : weightLabel) {
            getParent().add(label);
        }
    }

    private void updateWeights() {
        removeCurrentWeights();
        createWeightLabels();
        setWeightLabelPosition();

        if (tab != null) {
            addWeightLabelsToContainer(tab);
        }
    }

    protected void setWeightLabelPosition() {
        int originalX = (int) (arcPath.midPoint.x);
        int originalY = (int) (arcPath.midPoint.y) - 10;
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

    private void removeCurrentWeights() {
        for (NameLabel name : weightLabel) {
            removeLabelFromParentContainer(name);
        }
        weightLabel.clear();
    }

    private void createWeightLabels() {
        Map<Token, String> weights = model.getTokenWeights();
        for (Map.Entry<Token, String> entry : weights.entrySet()) {
            Token token = entry.getKey();
            String weight = entry.getValue();

            NameLabel label = new NameLabel();
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

    @Override
    public void addToPetriNetTab(PetriNetTab tab) {
        super.addToPetriNetTab(tab);
        updateWeights();
    }

    @Override public String getType() {
        return type;
    }

    /**
     * Accessor function to check whether or not the Arc is tagged
     */
    @Override public boolean isTagged() {
        return tagged;
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

    void updateWeightLabel() {

        setWeightLabelPosition();

    }

    public void setInView(boolean flag) {
        inView = flag;
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

    public HistoryItem setInverse(NormalArcView<S,T> _inverse, boolean joined) {
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
//            TransitionView transitionView = this.getTransition();
//            transitionView.removeFromArc(_inverse);
//            transitionView.removeArcCompareObject(_inverse);
//            transitionView.updateConnected();
            joined = isJoined;
        }
        updateWeightLabel();
    }

    public boolean isJoined() {
        return joined;
    }

    void setJoined(boolean flag) {
        joined = flag;
    }

    public HistoryItem split() {
       return null;
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

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.translate(getComponentDrawOffset() + ZOOM_GROW - arcPath.getBounds().getX(),
                getComponentDrawOffset() + ZOOM_GROW - arcPath.getBounds().getY());

        AffineTransform reset = g2.getTransform();

        if (isSelected() && !_ignoreSelection) {
            g2.setPaint(Constants.SELECTION_LINE_COLOUR);
        } else {
            g2.setPaint(Constants.ELEMENT_LINE_COLOUR);
        }

        if (joined) {
            g2.translate(arcPath.getPoint(0).getX(), arcPath.getPoint(0).getY());
            g2.rotate(arcPath.getStartAngle() + Math.PI);
            g2.setTransform(reset);
        }

        g2.setStroke(new BasicStroke(1f));
        g2.draw(arcPath);

        g2.translate(arcPath.getPoint(arcPath.getEndIndex()).getX(), arcPath.getPoint(arcPath.getEndIndex()).getY());

        g2.rotate(arcPath.getEndAngle() + Math.PI);
        g2.setColor(java.awt.Color.WHITE);


        if (isSelected() && !_ignoreSelection) {
            g2.setPaint(Constants.SELECTION_LINE_COLOUR);
        } else {
            g2.setPaint(Constants.ELEMENT_LINE_COLOUR);
        }

        g2.setStroke(new BasicStroke(0.8f));
        g2.fillPolygon(head);

        g2.transform(reset);
    }
}
