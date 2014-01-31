package pipe.views;

public interface ZoomManager {
    boolean canZoomIn();
    boolean canZoomOut();
    void zoomIn();
    void zoomOut();
    int getPercentageZoom();
}
