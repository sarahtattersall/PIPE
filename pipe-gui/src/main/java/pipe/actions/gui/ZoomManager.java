package pipe.actions.gui;

import java.beans.PropertyChangeListener;

/**
 * PIPE overall application zoom manager
 */
public interface ZoomManager {
    /**
     *
     * @return true if can zoom in any further
     */
    boolean canZoomIn();

    /**
     *
     * @return true if can zoom out any more
     */
    boolean canZoomOut();

    /**
     * Performs the zooming in on the current canvas
     */
    void zoomIn();

    /**
     * Performs the zooming out of the current canvas
     */
    void zoomOut();

    /**
     *
     * @return current zoom percentage
     */
    int getPercentageZoom();

    /**
     *
     * @return current percentage scale
     */
    double getScale();

    /**
     * Add a lister for zooming actions
     * @param listener
     */
    void addPropertyChangeListener(PropertyChangeListener listener);
}
