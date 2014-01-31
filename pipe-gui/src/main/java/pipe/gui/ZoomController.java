package pipe.gui;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
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
            percent -= Constants.ZOOM_DELTA;
            changeSupport.firePropertyChange("zoomOut", old, percent);
        }
    }

    public boolean canZoomOut() {
        int newPercent = percent - Constants.ZOOM_DELTA;
        return newPercent >= Constants.ZOOM_MIN;
    }

    public void zoomIn() {
        if (canZoomIn()) {
            int old = percent;
            percent += Constants.ZOOM_DELTA;
            changeSupport.firePropertyChange("zoomIn", old, percent);
        }
    }

    public boolean canZoomIn() {
        int newPercent = percent + Constants.ZOOM_DELTA;
        return newPercent <= Constants.ZOOM_MAX;
    }

    public int getPercent() {
        return percent;
    }

    private void setPercent(int newPercent) {
        if ((newPercent >= Constants.ZOOM_MIN) && (newPercent <= Constants.ZOOM_MAX)) {
            percent = newPercent;
        }
    }

    public void setZoom(int newPercent) {
        setPercent(newPercent);
    }
}
