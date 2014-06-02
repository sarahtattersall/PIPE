package pipe.actions.gui;

import java.beans.PropertyChangeListener;

public interface ZoomManager {
    boolean canZoomIn();
    boolean canZoomOut();
    void zoomIn();
    void zoomOut();
    int getPercentageZoom();
    double getScale();
    void addPropertyChangeListener(PropertyChangeListener listener);
}
