package pipe.controllers;

import pipe.constants.GUIConstants;

import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

/**
 * Zoom controller repsonsible for zooming in and out of a Petri net tab
 */
public class ZoomController implements Serializable {

    /**
     * Change support for firing events when percent is changed.
     */
    protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    /**
     * Zoom percentage, 100% = unzoomed
     */
    private int percent;

    /**
     * Constructor
     * @param pct initial percentage to start with
     */
    public ZoomController(int pct) {
        percent = pct;
    }

    /**
     *
     * Add a listener to be triggered when zooming in and out
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Removes the listener from this controller
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    /**
     *
     * @return the affine transform matrix for the zoom level
     */
    public AffineTransform getTransform() {
        return AffineTransform.getScaleInstance(percent * 0.01, percent * 0.01);
    }

    /**
     *
     * @return the scale factor e.g. 0.1, 0.5, 1.0 etc.
     */
    public double getScaleFactor() {
        return percent * 0.01;
    }

    /**
     * zooms out
     */
    public void zoomOut() {
        if (canZoomOut()) {
            int old = percent;
            percent -= GUIConstants.ZOOM_DELTA;
            changeSupport.firePropertyChange("zoomOut", old, percent);
        }
    }

    /**
     *
     * @return true if it can zoom out
     */
    public boolean canZoomOut() {
        int newPercent = percent - GUIConstants.ZOOM_DELTA;
        return newPercent >= GUIConstants.ZOOM_MIN;
    }

    /**
     * Zooms in
     */
    public void zoomIn() {
        if (canZoomIn()) {
            int old = percent;
            percent += GUIConstants.ZOOM_DELTA;
            changeSupport.firePropertyChange("zoomIn", old, percent);
        }
    }

    /**
     *
     * @return true if it can zoom in
     */
    public boolean canZoomIn() {
        int newPercent = percent + GUIConstants.ZOOM_DELTA;
        return newPercent <= GUIConstants.ZOOM_MAX;
    }

    /**
     *
     * @return zoom percentage e.g. 10%, 20%, 100%, 120%
     */
    public int getPercent() {
        return percent;
    }

    private void setPercent(int newPercent) {
        if (newPercent >= GUIConstants.ZOOM_MIN && newPercent <= GUIConstants.ZOOM_MAX) {
            percent = newPercent;
        }
    }

    /**
     *
     * @param newPercent the new zoom percentage
     */
    public void setZoom(int newPercent) {
        setPercent(newPercent);
    }
}
