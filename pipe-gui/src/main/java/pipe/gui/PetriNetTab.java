package pipe.gui;

import pipe.constants.GUIConstants;
import pipe.controllers.SelectionManager;
import pipe.controllers.ZoomController;
import pipe.views.AbstractPetriNetViewComponent;
import pipe.views.PetriNetViewComponent;
import uk.ac.imperial.pipe.exceptions.PetriNetComponentException;
import uk.ac.imperial.pipe.models.petrinet.*;
import uk.ac.imperial.pipe.visitor.component.PetriNetComponentVisitor;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main canvas that the {@link pipe.views.PetriNetViewComponent}s appear on
 * It is a tab in the main applicaiton
 */
public class PetriNetTab extends JLayeredPane implements Observer, Printable {

    /**
     * Class logger
     */
    private static final Logger LOGGER = Logger.getLogger(PetriNetTab.class.getName());

    /**
     * Map of components in the tab with id -&gt; component
     */
    private final Map<String, PetriNetViewComponent> petriNetComponents = new HashMap<>();

    /**
     * Grid displayed on petri net tab
     */
    private final Grid grid = new Grid();

    /**
     * Legacy file for the saving of the underlying Petri net
     */
    @Deprecated
    public File appFile;

    /**
     * Constructor
     *
     * Sets no layout manager to acheive an (x,y) layout
     */
    public PetriNetTab() {
        setLayout(null);
        setOpaque(true);
        setDoubleBuffered(true);
        setAutoscrolls(true);
        setBackground(GUIConstants.ELEMENT_FILL_COLOUR);

        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    /**
     *
     * Register the zoom listener to the Petri net tab
     *
     * @param zoomController zoom listener
     */
    public void addZoomListener(ZoomController zoomController) {
        zoomController.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                repaint();
            }
        });
    }


    /**
     * Legacy update method
     * @param o observable 
     * @param diffObj object to add
     */
    @Override
    public void update(Observable o, Object diffObj) {
        if (diffObj instanceof AbstractPetriNetViewComponent) {
            AbstractPetriNetViewComponent<?> component = (AbstractPetriNetViewComponent<?>) diffObj;
            addNewPetriNetComponent(component);
        }
    }

    /**
     * Adds the Petri net component to this canvas
     * @param component to add to petri net view
     */
    public void addNewPetriNetComponent(AbstractPetriNetViewComponent<?> component) {
            add(component);
            component.addToContainer(this);
    }

    /**
     * Add the Petri net component to this canvas
     * @param component to add
     */
    public void add(AbstractPetriNetViewComponent<?> component) {
        registerLocationChangeListener(component.getModel());

        setLayer(component, DEFAULT_LAYER);
        super.add(component);
        petriNetComponents.put(component.getId(), component);
        updatePreferredSize();
        //        repaint();
    }

    /**
     * Update the preferred size of the canvas and grid that is displayed on it
     */
    public void updatePreferredSize() {
        Component[] components = getComponents();
        Dimension d = new Dimension(0, 0);
        for (Component component : components) {
            if (component.getClass() == SelectionManager.class) {
                continue;
            }
            Rectangle r = component.getBounds();
            int x = r.x + r.width + 20;
            int y = r.y + r.height + 20;
            if (x > d.width) {
                d.width = x;
            }
            if (y > d.height) {
                d.height = y;
            }
        }
        setPreferredSize(d);
        Container parent = getParent();
        if (parent != null) {
            parent.validate();
        }
    }

    /**
     *
     * Registers a location listener on the Petri net component
     *
     * @param component for which a listener will be registered
     */
    private void registerLocationChangeListener(PetriNetComponent component) {

        PetriNetComponentVisitor changeListener = new ChangeListener();
        try {
            component.accept(changeListener);
        } catch (PetriNetComponentException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

    /**
     * Prints the Petri net tab
     * @param g graphics
     * @param pageFormat page format
     * @param pageIndex index
     * @return printer return code 
     * @throws PrinterException if error in printing 
     */
    @Override
    public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex > 0) {
            return Printable.NO_SUCH_PAGE;
        }
        Graphics2D g2D = (Graphics2D) g;
        g2D.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        g2D.scale(0.5, 0.5);
        print(g2D);
        return Printable.PAGE_EXISTS;
    }

    /**
     * Paints the underlying grid on the canvas
     * @param g graphics 
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (grid.isEnabled()) {
            grid.updateSize(this);
            grid.drawGrid(g);
        }
    }

    /**
     * Set the cursor type. Options are:
     * - arrow
     * - crosshair
     * - move
     * @param type cursor type
     */
    //TODO These should be an enum
    public void setCursorType(String type) {
        if (type.equals("arrow")) {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } else if (type.equals("crosshair")) {
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        } else if (type.equals("move")) {
            setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        }
    }

    /**
     * Set meta down. Since there is no documentation for this the functionality
     * has been deprecated and it no longer does anything
     * @param down flag
     */
    @Deprecated
    public void setMetaDown(boolean down) {
        //TODO: DELETE
    }

    /**
     * Updates the canvas boundary when dragging is taking place
     * @param dragStart start of drag
     * @param dragEnd end of drag
     */
    public void drag(Point dragStart, Point dragEnd) {
        if (dragStart == null) {
            return;
        }
        JViewport viewer = (JViewport) getParent();
        Point offScreen = viewer.getViewPosition();
        if (dragStart.x > dragEnd.x) {
            offScreen.translate(viewer.getWidth(), 0);
        }
        if (dragStart.y > dragEnd.y) {
            offScreen.translate(0, viewer.getHeight());
        }
        offScreen.translate(dragStart.x - dragEnd.x, dragStart.y - dragEnd.y);
        Rectangle r = new Rectangle(offScreen.x, offScreen.y, 1, 1);
        scrollRectToVisible(r);
    }

    /**
     * Remove the component with this id from the canvas
     * @param id to remove 
     */
    public void deletePetriNetComponent(String id) {
        PetriNetViewComponent component = petriNetComponents.get(id);
        if (component != null) {
            component.delete();
            remove((Component) component);
        }
        validate();
        repaint();
    }

    /**
     *
     * @return Grid displayed on the canvas
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     *
     * @param handler specifies how the canvas should behave to mouse events
     */
    public void setMouseHandler(MouseInputAdapter handler) {
        addMouseListener(handler);
        addMouseMotionListener(handler);
        addMouseWheelListener(handler);
    }

    /**
     * Used to set the bounds of the canvas so that it will expand if components go out of bound
     */
    private class ChangeListener implements PlaceVisitor, TransitionVisitor {
        /**
         * Listens to (x,y) changes in components and updates the canvas width
         * if a place/transition goes out of the current bounds
         */
        private PropertyChangeListener updateListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String name = evt.getPropertyName();
                if (name.equals(Connectable.X_CHANGE_MESSAGE)) {
                    int x = (int) evt.getNewValue();
                    if (x > getWidth()) {
                        updatePreferredSize();
                    }

                }
                if (name.equals(Connectable.Y_CHANGE_MESSAGE)) {
                    int y = (int) evt.getNewValue();
                    if (y > getHeight()) {
                        updatePreferredSize();
                    }

                }
            }
        };

        /**
         * Add the update listener to the place
         * @param place for which to add listener
         */
        @Override
        public void visit(Place place) {
            place.addPropertyChangeListener(updateListener);
        }

        /**
         * Add the update listener to the transition
         * @param transition for which to add listener
         */
        @Override
        public void visit(Transition transition) {
            transition.addPropertyChangeListener(updateListener);
        }
    }
}


