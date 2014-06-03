package pipe.actions.gui;

import pipe.controllers.application.PipeApplicationController;
import pipe.gui.PetriNetTab;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Zoom UI which intercepts mouse presses on a zoomed panel and transforms them to their
 * correct location
 */
public class ZoomUI extends LayerUI<JComponent> implements ZoomManager {

    public static final String ZOOM_OUT_CHANGE_MESSAGE = "zoomOut";

    public static final String ZOOM_IN_CHANGE_MESSAGE = "zoomIn";

    /**
     * Amount to zoom in and out by
     */
    private final double zoomAmount;

    /**
     * Minimum scale allowed to zoom to
     */
    private final double zoomMin;

    /**
     * ApplicationView that this zooming belongs for
     * is used to get petri net tab
     */
    private final PipeApplicationController controller;

    /**
     * Maximum scale allowed to zoom to
     */
    private final double zoomMax;

    /**
     * Change support for firing events when percent is changed.
     */
    protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    /**
     * Zoom transformation 1 = unzoomed
     */
    private double zoom = 1;

    /**
     * @param startingScale initialZoomScale where 1 = unzoomed
     * @param zoomAmount    amount to zoom in/out by
     * @param zoomMax       maximum allowed zoom value
     * @param zoomMin       minimum allowed zoom value
     * @param controller
     */
    public ZoomUI(double startingScale, double zoomAmount, double zoomMax, double zoomMin, PipeApplicationController controller) {
        zoom = startingScale;
        this.zoomAmount = zoomAmount;
        this.zoomMax = zoomMax;
        this.zoomMin = zoomMin;
        this.controller = controller;
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        g.clearRect(c.getX(), c.getY(), c.getWidth(), c.getHeight());
        Graphics2D g2 = (Graphics2D) g;
        g2.scale(zoom, zoom);
        super.paint(g2, c);
    }

    /**
     * Transforms zoomed mouse events to their unzoomed coordinates
     * @param e
     * @param l
     */
    @Override
    protected void processMouseEvent(MouseEvent e, JLayer<? extends JComponent> l) {
        MouseEvent localEvent = translateToLayerCoordinates(e, l);
        if (clickNotOutOfBounds(localEvent, l)) {
            Component component = getComponentClickedOn(l, localEvent);
            if (localEvent.getID() == MouseEvent.MOUSE_PRESSED) {
                for (ActionListener listener : component.getListeners(ActionListener.class)) {
                    ActionEvent actionEvent = new ActionEvent(component, localEvent.getID(), "CLICK");
                    listener.actionPerformed(actionEvent);
                }
                for (MouseListener listener : component.getListeners(MouseListener.class)) {
                    listener.mousePressed(getNewMouseClickEvent(component, localEvent));
                }
            } else if (localEvent.getID() == MouseEvent.MOUSE_RELEASED) {
                for (MouseListener listener : component.getListeners(MouseListener.class)) {
                    listener.mouseReleased(getNewMouseClickEvent(component, localEvent));
                }
            } else if (localEvent.getID() == MouseEvent.MOUSE_CLICKED) {
                for (MouseListener listener : component.getListeners(MouseListener.class)) {
                    listener.mouseClicked(getNewMouseClickEvent(component, localEvent));
                }
            }
            e.consume();
        }
    }

    @Override
    protected void processMouseMotionEvent(MouseEvent e, JLayer<? extends JComponent> l) {
        MouseEvent localEvent = translateToLayerCoordinates(e, l);
        if (clickNotOutOfBounds(localEvent, l)) {
            Component component = getComponentClickedOn(l, localEvent);
            if (localEvent.getID() == MouseEvent.MOUSE_MOVED) {
                for (MouseMotionListener listener : component.getListeners(MouseMotionListener.class)) {
                    listener.mouseMoved(getNewMouseClickEvent(component, localEvent));
                }
            } else if (localEvent.getID() == MouseEvent.MOUSE_DRAGGED) {
                for (MouseMotionListener listener : component.getListeners(MouseMotionListener.class)) {
                    listener.mouseDragged(getNewMouseClickEvent(component, localEvent));
                }
            }
        }
        e.consume();
    }

    @Override
    protected void processMouseWheelEvent(MouseWheelEvent e,JLayer<? extends JComponent> l) {
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        JLayer<? extends JComponent> jlayer = (JLayer<? extends JComponent>) c;
        jlayer.setLayerEventMask(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
    }

    @Override
    public void uninstallUI(JComponent c) {
        JLayer<? extends JComponent> jlayer = (JLayer<? extends JComponent>) c;
        jlayer.setLayerEventMask(0);
        super.uninstallUI(c);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public void zoomOut() {
        if (canZoomOut()) {
            double old = zoom;
            zoom -= zoomAmount;
            changeSupport.firePropertyChange(ZOOM_OUT_CHANGE_MESSAGE, old, zoom);
        }
    }

    private boolean clickNotOutOfBounds(MouseEvent event, JLayer<? extends JComponent> l) {
        return getComponentClickedOn(l, event) != null;
    }

    /**
     * @param l layer clicked
     * @param e mouse event with coordinates releative to l
     * @return component in l clicked on
     */
    private Component getComponentClickedOn(JLayer<? extends JComponent> l, MouseEvent e) {

        PetriNetTab tab = controller.getActiveTab();

        Point coordinates = zoomedXY(e);
        return tab.getComponentAt(coordinates.x, coordinates.y);
    }

    /**
     * @param e mouse click event
     * @return the events x y coordinates zoomed
     */
    private Point zoomedXY(MouseEvent e) {
        int x = e.getX() == 0 ? 0 : (int) (e.getX() / zoom);
        int y = e.getY() == 0 ? 0 : (int) (e.getY() / zoom);
        return new Point(x, y);
    }

    /**
     * @param e
     * @param layer
     * @return a new event with x y pointing to the coordinate space of the layer
     * rather than the whole application
     */
    private MouseEvent translateToLayerCoordinates(MouseEvent e, JLayer<? extends JComponent> layer) {
        PetriNetTab tab = controller.getActiveTab();
        return SwingUtilities.convertMouseEvent(e.getComponent(), e, tab);
    }

    @Override
    public int getPercentageZoom() {
        return (int) (zoom * 100);
    }

    private MouseEvent getNewMouseClickEvent(Component component, MouseEvent mouseEvent) {
        Point coordinates = zoomedXY(mouseEvent);
        return new MouseEvent(component, mouseEvent.getID(), mouseEvent.getWhen(), mouseEvent.getModifiers(),
                coordinates.x, coordinates.y, mouseEvent.getClickCount(), mouseEvent.isPopupTrigger(),
                mouseEvent.getButton());
    }

    @Override
    public double getScale() {
        return zoom;
    }


    @Override
    public boolean canZoomOut() {
        return zoom - zoomAmount >= zoomMin;
    }


    @Override
    public void zoomIn() {
        if (canZoomIn()) {
            double old = zoom;
            zoom += zoomAmount;
            changeSupport.firePropertyChange(ZOOM_IN_CHANGE_MESSAGE, old, zoom);
        }
    }


    @Override
    public boolean canZoomIn() {
        return zoom + zoomAmount <= zoomMax;
    }
}