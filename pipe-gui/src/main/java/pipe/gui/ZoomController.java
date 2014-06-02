package pipe.gui;

import pipe.constants.GUIConstants;

import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

public class ZoomController implements Serializable {

    /**
     * Change support for firing events when percent is changed.
     */
    protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    /**
     * Zoom percentage, 100% = unzoomed
     */
    private int percent;

    public ZoomController(int pct) {
        percent = pct;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    public AffineTransform getTransform() {
        return AffineTransform.getScaleInstance(percent * 0.01, percent * 0.01);
    }

    public double getScaleFactor() {
        return percent * 0.01;
    }

    public void zoomOut() {
        if (canZoomOut()) {
            int old = percent;
            percent -= GUIConstants.ZOOM_DELTA;
            changeSupport.firePropertyChange("zoomOut", old, percent);
        }
    }

    public boolean canZoomOut() {
        int newPercent = percent - GUIConstants.ZOOM_DELTA;
        return newPercent >= GUIConstants.ZOOM_MIN;
    }

    public void zoomIn() {
        if (canZoomIn()) {
            int old = percent;
            percent += GUIConstants.ZOOM_DELTA;
            changeSupport.firePropertyChange("zoomIn", old, percent);
        }
    }

    public boolean canZoomIn() {
        int newPercent = percent + GUIConstants.ZOOM_DELTA;
        return newPercent <= GUIConstants.ZOOM_MAX;
    }

    public int getPercent() {
        return percent;
    }

    private void setPercent(int newPercent) {
        if ((newPercent >= GUIConstants.ZOOM_MIN) && (newPercent <= GUIConstants.ZOOM_MAX)) {
            percent = newPercent;
        }
    }

    public void setZoom(int newPercent) {
        setPercent(newPercent);
    }
}
