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
@SuppressWarnings("serial")
public class ZoomUI extends LayerUI<JComponent> implements ZoomManager {

    /**
     * Message fired on a zoom out
     */
    public static final String ZOOM_OUT_CHANGE_MESSAGE = "zoomOut";

    /**
     * Message fired on a zoom in
     */
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
     * @param controller    controller 
     */
    public ZoomUI(double startingScale, double zoomAmount, double zoomMax, double zoomMin,
                  PipeApplicationController controller) {
        zoom = startingScale;
        this.zoomAmount = zoomAmount;
        this.zoomMax = zoomMax;
        this.zoomMin = zoomMin;
        this.controller = controller;
    }

    /**
     * Paints the component with the current zoom scale
     * @param g graphics
     * @param c component
     */
    @Override
    public void paint(Graphics g, JComponent c) {
        g.clearRect(c.getX(), c.getY(), c.getWidth(), c.getHeight());
        Graphics2D g2 = (Graphics2D) g;
        g2.scale(zoom, zoom);
        super.paint(g2, c);
    }

    /**
     * Transforms zoomed mouse events to their unzoomed coordinates
     *
     * @param e event 
     * @param l component 
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

    /**
     * Translates the event to a zoomed event point
     * @param e mouse event
     * @param l component 
     */
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

    /**
     * Noop action
     * @param e mouse event
     * @param l component
     */
    @Override
    protected void processMouseWheelEvent(MouseWheelEvent e, JLayer<? extends JComponent> l) {
        //No action needed
    }

    /**
     * Install the UI
     * @param c component
     */
    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        JLayer<? extends JComponent> jlayer = (JLayer<? extends JComponent>) c;
        jlayer.setLayerEventMask(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
    }

    /**
     * Uninstall the UI
     * @param c component 
     */
    @Override
    public void uninstallUI(JComponent c) {
        JLayer<? extends JComponent> jlayer = (JLayer<? extends JComponent>) c;
        jlayer.setLayerEventMask(0);
        super.uninstallUI(c);
    }

    /**
     * Add a listener for zoom updates
     * @param listener to add
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove a listener from the zoom UI
     * @param listener to remove
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    /**
     *
     * @param event mouse event
     * @param l component 
     * @return true if the event is within the component bounds
     */
    private boolean clickNotOutOfBounds(MouseEvent event, JLayer<? extends JComponent> l) {
        return getComponentClickedOn(l, event) != null;
    }

    /**
     * Perform a zoom out of the canvas
     */
    @Override
    public void zoomOut() {
        if (canZoomOut()) {
            double old = zoom;
            zoom -= zoomAmount;
            changeSupport.firePropertyChange(ZOOM_OUT_CHANGE_MESSAGE, old, zoom);
        }
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
     * @param e  mouse event 
     * @param layer component 
     * @return a new event with x y pointing to the coordinate space of the layer
     * rather than the whole application
     */
    private MouseEvent translateToLayerCoordinates(MouseEvent e, JLayer<? extends JComponent> layer) {
        PetriNetTab tab = controller.getActiveTab();
        return SwingUtilities.convertMouseEvent(e.getComponent(), e, tab);
    }

    /**
     *
     * @param component clicked
     * @param mouseEvent mouse event 
     * @return translated mouse click event
     */
    private MouseEvent getNewMouseClickEvent(Component component, MouseEvent mouseEvent) {
        Point coordinates = zoomedXY(mouseEvent);
        return new MouseEvent(component, mouseEvent.getID(), mouseEvent.getWhen(), mouseEvent.getModifiers(),
                coordinates.x, coordinates.y, mouseEvent.getClickCount(), mouseEvent.isPopupTrigger(),
                mouseEvent.getButton());
    }

    /**
     *
     * @return the zoom scale as a percentage e.g. 20%, 100%, 120%
     */
    @Override
    public int getPercentageZoom() {
        return (int) (zoom * 100);
    }


    /**
     *
     * @return the scale of the zoom e.g. 0.2, 1.0, 1.2
     */
    @Override
    public double getScale() {
        return zoom;
    }


    /**
     *
     * @return true if can zoom out any further
     */
    @Override
    public boolean canZoomOut() {
        return zoom - zoomAmount >= zoomMin;
    }


    /**
     * Performs the zoom in on the canvas
     */
    @Override
    public void zoomIn() {
        if (canZoomIn()) {
            double old = zoom;
            zoom += zoomAmount;
            changeSupport.firePropertyChange(ZOOM_IN_CHANGE_MESSAGE, old, zoom);
        }
    }


    /**
     *
     * @return true if can zoom in any further
     */
    @Override
    public boolean canZoomIn() {
        return zoom + zoomAmount <= zoomMax;
    }
}