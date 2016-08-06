package pipe.controllers;

import pipe.constants.GUIConstants;
import pipe.controllers.application.PipeApplicationController;
import pipe.gui.PetriNetTab;
import pipe.historyActions.MultipleEdit;
import pipe.historyActions.component.AddPetriNetObject;
import pipe.utilities.gui.GuiUtils;
import uk.ac.imperial.pipe.exceptions.PetriNetComponentException;
import uk.ac.imperial.pipe.models.petrinet.*;
import uk.ac.imperial.pipe.naming.MultipleNamer;
import uk.ac.imperial.pipe.naming.PetriNetComponentNamer;
import uk.ac.imperial.pipe.visitor.PasteVisitor;
import uk.ac.imperial.pipe.visitor.component.PetriNetComponentVisitor;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEdit;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * Class to handle copy and paste functionality
 */
@SuppressWarnings("serial")
public class CopyPasteManager extends javax.swing.JComponent
        implements java.awt.event.MouseListener, java.awt.event.MouseMotionListener, java.awt.event.KeyListener {

    /**
     * Colour of rectangle displayed when pasting
     */
    private static final Paint PASTE_COLOR = new Color(155, 155, 155, 100);

    /**
     * Colour of rectangle outline displayed when pasting
     */
    private static final Color PASTE_COLOR_OUTLINE = new Color(155, 0, 0, 0);

    /**
     * Rectangle displayed which marks the outline of the objects to paste
     */
    private final Rectangle pasteRectangle = new Rectangle(-1, -1);

    /**
     * Petri net tab where pasting takes place
     */
    private final PetriNetTab petriNetTab;

    /**
     * Petri net pasting objects from/to
     */
    private final PetriNet petriNet;

    /**
     * Main PIPE application controller
     */
    private final PipeApplicationController applicationController;

    /**
     * Origin of the selected components to paste (top left corner)
     */
    private final Point rectangleOrigin = new Point();

    /**
     * Listener for undoable events being created
     */
    private final UndoableEditListener listener;

    /**
     * pasteInProgres is true when pasteRectangle is visible (user is doing a
     * paste but still hasn't chosen the position where elements will be pasted).
     */
    private boolean pasteInProgress = false;

    /**
     * Components to paste when paste is clicked.
     * These are set when copied
     */
    private Collection<PetriNetComponent> pasteComponents = new ArrayList<>();


    /**
     * Constructor
     *
     * @param listener              undoable event listener, used to register undo events to
     * @param petriNetTab           current Petri net tab
     * @param net                   underlying Petri net displayed on the Petri net tab
     * @param applicationController main application controller
     */
    public CopyPasteManager(UndoableEditListener listener, PetriNetTab petriNetTab, PetriNet net,
                            PipeApplicationController applicationController) {
        this.petriNetTab = petriNetTab;
        petriNet = net;
        this.applicationController = applicationController;
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        this.listener = listener;

    }

    /**
     * Creates new components for the petri net to copy when pasted
     *
     * @param selectedComponents components to copy
     */
    public void copy(Collection<PetriNetComponent> selectedComponents) {
        pasteComponents.clear();
        pasteComponents.addAll(selectedComponents);
        LocationVisitor locationVisitor = new LocationVisitor();

        for (PetriNetComponent component : selectedComponents) {
            try {
                component.accept(locationVisitor);
            } catch (PetriNetComponentException e) {
                GuiUtils.displayErrorMessage(null, e.getMessage());
            }
        }
        Location location = locationVisitor.location;
        pasteRectangle.setRect(location.left, location.top, location.right - location.left,
                location.bottom - location.top);
        rectangleOrigin.setLocation(location.left, location.top);
    }

    /**
     * Shows the paste rectangle on screen
     */
    public void showPasteRectangle() {
        if (!pasteInProgress) {
            petriNetTab.add(this);
            requestFocusInWindow();
            //            if (zoom != petriNetTab.getZoom()) {
            //                updateSize(pasteRectangle, zoom, petriNetTab.getZoom());
            //                zoom = petriNetTab.getZoom();
            //            }

            petriNetTab.setLayer(this, GUIConstants.SELECTION_LAYER_OFFSET);
            repaint();
            pasteInProgress = true;
            updateBounds();
        }
    }

    /**
     * Update the bounds which this object can be displayed at
     */
    private void updateBounds() {
        if (pasteInProgress) {
            PetriNetTab activeTab = applicationController.getActiveTab();
            setBounds(0, 0, activeTab.getWidth(), activeTab.getHeight());
        }
    }

    /**
     * @return if it is possible to perform a paste action
     */
    public boolean pasteEnabled() {
        return !pasteComponents.isEmpty();
    }

    /**
     * Paints the paste rectangle onto the screen
     *
     * @param g paint graphics
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint(PASTE_COLOR);
        g2d.fill(pasteRectangle);
        g2d.setXORMode(PASTE_COLOR_OUTLINE);
        g2d.draw(pasteRectangle);
    }

    /**
     * Dragging the mouse on the screen updates the location of the
     * paste rectangle
     *
     * @param e mouse drag event
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (pasteInProgress) {
            updateRect(e.getPoint());
        }
    }

    /**
     * Changes the rectangles location to point
     *
     * @param point new top left point for rectangle
     */
    private void updateRect(Point point) {
        pasteRectangle.setLocation(point);
        repaint();
        updateBounds();
    }

    /**
     * Moving the mouse on the screen updates the location of the
     * paste rectangle
     *
     * @param e mouse move event
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        if (pasteInProgress) {
            updateRect(e.getPoint());
        }
    }

    /**
     * Noop action on click
     *
     * @param e mouse click event 
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        //Not needed
    }

    /**
     * Performs the paste action
     *
     * @param e mouse pressed event 
     */
    @Override
    public void mousePressed(MouseEvent e) {
        petriNetTab.updatePreferredSize();
        petriNetTab.setLayer(this, GUIConstants.LOWEST_LAYER_OFFSET);
        repaint();
        if (pasteInProgress) {
            paste(petriNetTab);
        }
    }

    /**
     * Noop action
     *
     * @param e mouse released event 
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        // Not needed
    }

    /**
     * Noop action
     *
     * @param e mouse entered event 
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        // Not needed
    }

    /**
     * Noop action
     *
     * @param e mouse exited event 
     */
    @Override
    public void mouseExited(MouseEvent e) {
        // Not needed
    }

    /**
     * Paste pastes the new objects into the petriNet specified in consturction.
     * <p>
     * It first pastes the connectables, and then other components. This ordering is important
     * and will ensure that arcs are created with the right components.
     * </p>
     * @param petriNetTab petri net tab to paste items to
     */
    private void paste(PetriNetTab petriNetTab) {
        pasteInProgress = false;
        petriNetTab.remove(this);

        if (pasteComponents.isEmpty()) {
            return;
        }

        int despX = pasteRectangle.x - rectangleOrigin.x;
        int despY = pasteRectangle.y - rectangleOrigin.y;

        MultipleNamer multipleNamer = new PetriNetComponentNamer(petriNet);
        PasteVisitor pasteVisitor = new PasteVisitor(petriNet, pasteComponents, multipleNamer, despX, despY);

        try {
            for (Connectable component : getConnectablesToPaste()) {
                component.accept(pasteVisitor);
            }
            for (PetriNetComponent component : getNonConnectablesToPaste()) {
                component.accept(pasteVisitor);
            }
        } catch (PetriNetComponentException e) {

            GuiUtils.displayErrorMessage(null, e.getMessage());
        }

        createPasteHistoryItem(pasteVisitor.getCreatedComponents());
    }

    /**
     * @return a collection of the connectable items to paste
     */
    private Collection<Connectable> getConnectablesToPaste() {
        final Collection<Connectable> connectables = new LinkedList<>();
        PetriNetComponentVisitor connectableVisitor = new PlaceTransitionVisitor() {
            @Override
            public void visit(Place place) {
                connectables.add(place);
            }

            @Override
            public void visit(Transition transition) {
                connectables.add(transition);
            }
        };

        for (PetriNetComponent component : pasteComponents) {
            try {
                component.accept(connectableVisitor);
            } catch (PetriNetComponentException e) {
                GuiUtils.displayErrorMessage(null, e.getMessage());
            }
        }
        return connectables;
    }

    /**
     * @return Petri net components that do not inherit from Connectable
     */
    private Collection<PetriNetComponent> getNonConnectablesToPaste() {
        final Collection<PetriNetComponent> components = new LinkedList<>();
        PetriNetComponentVisitor componentVisitor = new NonConnectableVisitor() {
            @Override
            public void visit(Token token) {
                components.add(token);
            }

            @Override
            public void visit(Annotation annotation) {
                components.add(annotation);
            }

            @Override
            public void visit(InboundArc inboundArc) {
                components.add(inboundArc);
            }

            @Override
            public void visit(OutboundArc outboundArc) {
                components.add(outboundArc);
            }
        };

        for (PetriNetComponent component : pasteComponents) {
            try {
                component.accept(componentVisitor);
            } catch (PetriNetComponentException e) {
                GuiUtils.displayErrorMessage(null, e.getMessage());
            }
        }
        return components;
    }

    /**
     * Creates a history item for the new components added to the petrinet
     *
     * @param createdComponents new components that have been created
     */
    private void createPasteHistoryItem(Iterable<PetriNetComponent> createdComponents) {
        List<UndoableEdit> undoableEditList = new LinkedList<>();
        for (PetriNetComponent component : createdComponents) {
            AddPetriNetObject addAction = new AddPetriNetObject(component, petriNet);
            undoableEditList.add(addAction);
        }

        listener.undoableEditHappened(new UndoableEditEvent(this, new MultipleEdit(undoableEditList)));
    }

    /**
     * Noop action
     *
     * @param e key typed event 
     */
    @Override
    public void keyTyped(KeyEvent e) {
        // Not needed
    }

    /**
     * Noop action
     *
     * @param e key pressed event 
     */
    @Override
    public void keyPressed(KeyEvent e) {
        // Not needed
    }

    /**
     * Noop action
     *
     * @param e key released event 
     */
    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            cancelPaste();
        }
    }

    /**
     * Cancel the paste. This will stop the paste rectangle being displayed but will
     * keep the copied items selected for future pastes
     */
    public void cancelPaste() {
        PetriNetTab tab = applicationController.getActiveTab();
        cancelPaste(tab);
    }

    /**
     * Cancel the paste. This will stop the paste rectangle being displayed but will
     * keep the copied items selected for future pastes
     *
     * @param view tab on which the paste is taking place
     */
    void cancelPaste(PetriNetTab view) {
        pasteInProgress = false;
        view.repaint();
        view.remove(this);
    }

    /**
     * Used for creating anonymous classes that only visit
     * Places and Transition
     */
    private interface PlaceTransitionVisitor extends PlaceVisitor, TransitionVisitor {

    }


    /**
     * Used for creating anonymous classes that visit non connectable classes
     */
    private interface NonConnectableVisitor extends AnnotationVisitor, ArcVisitor, TokenVisitor {

    }

    /**
     * Private class used to set the bounds of a selection rectangle
     * Needed to create a class so that the visitor can change the values
     */
    private static class Location {
        /**
         * Bottom location
         */
        private double bottom = 0;

        /**
         * Right of the rectangle
         */
        private double right = 0;

        /**
         * Top of the rectangle
         */
        private double top = Double.MAX_VALUE;

        /**
         * Left of the rectangle
         */
        private double left = Double.MAX_VALUE;
    }

    /**
     * Used to set the bounds of the rectagle displayed when copy pasting
     */
    private static class LocationVisitor implements PlaceTransitionVisitor {
        /**
         * Location of the rectangle
         */
        private final Location location = new Location();

        /**
         * Adjusts the bounds to include the position of the place
         * @param place
         */
        @Override
        public void visit(Place place) {
            adjustLocation(place);
        }

        /**
         * Changes the bounds of the rectangle to include the connectable
         * @param connectable being bounded 
         * @param <T> type of the connectable
         */
        private <T extends Connectable> void adjustLocation(T connectable) {
            if (connectable.getX() < location.left) {
                location.left = connectable.getX();
            }
            if (connectable.getX() + connectable.getWidth() > location.right) {
                location.right = connectable.getX() + connectable.getWidth();
            }
            if (connectable.getY() < location.top) {
                location.top = connectable.getY();
            }
            if (connectable.getY() + connectable.getHeight() > location.bottom) {
                location.bottom = connectable.getY() + connectable.getHeight();
            }
        }

        /**
         * Adjusts the bounds of the rectangle to include the position of the transition
         * @param transition to be included in the bounds 
         */
        @Override
        public void visit(Transition transition) {
            adjustLocation(transition);
        }

    }


}
