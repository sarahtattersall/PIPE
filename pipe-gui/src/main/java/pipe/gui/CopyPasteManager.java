package pipe.gui;

import pipe.exceptions.PetriNetComponentException;
import pipe.historyActions.MultipleEdit;
import pipe.historyActions.component.AddPetriNetObject;
import pipe.models.component.Connectable;
import pipe.models.component.PetriNetComponent;
import pipe.models.component.annotation.Annotation;
import pipe.models.component.annotation.AnnotationVisitor;
import pipe.models.component.arc.Arc;
import pipe.models.component.arc.ArcVisitor;
import pipe.models.component.place.Place;
import pipe.models.component.place.PlaceVisitor;
import pipe.models.component.token.Token;
import pipe.models.component.token.TokenVisitor;
import pipe.models.component.transition.Transition;
import pipe.models.component.transition.TransitionVisitor;
import pipe.models.petrinet.PetriNet;
import pipe.naming.MultipleNamer;
import pipe.naming.PetriNetComponentNamer;
import pipe.utilities.gui.GuiUtils;
import pipe.views.PipeApplicationView;
import pipe.visitor.PasteVisitor;
import pipe.visitor.component.PetriNetComponentVisitor;

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
 * Class to handle copy & paste functionality
 */
public class CopyPasteManager extends javax.swing.JComponent
        implements pipe.gui.Zoomable, java.awt.event.MouseListener, java.awt.event.MouseMotionListener,
        java.awt.event.KeyListener {

    /**
     * Colour of rectange displayed when pasting
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
     * Origin of the selected components to paste (top left corner)
     */
    private final Point rectangleOrigin = new Point();

    private final UndoableEditListener listener;

    /**
     * pasteInProgres is true when pasteRectangle is visible (user is doing a
     * paste but still hasn't chosen the position where elements will be pasted).
     */
    private boolean pasteInProgress = false;

    //TODO: DELETE
    private int zoom;

    /**
     * Components to paste when paste is clicked.
     * These are set when copied
     */
    private Collection<PetriNetComponent> pasteComponents = new ArrayList<>();


    public CopyPasteManager(UndoableEditListener listener, PetriNetTab petriNetTab, PetriNet net) {
        this.petriNetTab = petriNetTab;
        petriNet = net;
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
//        zoom = petriNetTab.getZoom();
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
        final Location location = new Location();
        PetriNetComponentVisitor locationVisitor = new PlaceTransitionVisitor() {
            @Override
            public void visit(Place place) {
                adjustLocation(place);
            }

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

            @Override
            public void visit(Transition transition) {
                adjustLocation(transition);
            }
        };

        for (PetriNetComponent component : selectedComponents) {
            try {
                component.accept(locationVisitor);
            } catch (PetriNetComponentException e) {
                GuiUtils.displayErrorMessage(null, e.getMessage());
            }
        }

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

            petriNetTab.setLayer(this, Constants.SELECTION_LAYER_OFFSET);
            repaint();
            pasteInProgress = true;
            updateBounds();
        }
    }

    private void updateSize(Rectangle pasteRectangle, int zoom, int newZoom) {
//        ZoomController zoomController = petriNetTab.getZoomController();
        int realWidth = pasteRectangle.width;
        int realHeight = pasteRectangle.height;

//        pasteRectangle.setSize((int) (realWidth * zoomController.getScaleFactor()),
//                (int) (realHeight * zoomController.getScaleFactor()));
    }

    private void updateBounds() {
        if (pasteInProgress) {
            PipeApplicationView applicationView = ApplicationSettings.getApplicationView();
            setBounds(0, 0, applicationView.getCurrentTab().getWidth(),
                    ApplicationSettings.getApplicationView().getCurrentTab().getHeight());
        }
    }

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

    @Override
    public void zoomUpdate(int newZoom) {
        updateSize(pasteRectangle, zoom, newZoom);
        zoom = newZoom;
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

    @Override
    public void mouseClicked(MouseEvent e) {
        //Not needed
    }

    @Override
    public void mousePressed(MouseEvent e) {
        petriNetTab.updatePreferredSize();
        petriNetTab.setLayer(this, Constants.LOWEST_LAYER_OFFSET);
        repaint();
        if (pasteInProgress) {
            paste(petriNetTab);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // Not needed
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // Not needed
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // Not needed
    }

    /**
     * Paste pastes the new objects into the petriNet specified in consturction.
     * <p/>
     * It first pastes the connectables, and then other components. This ordering is important
     * and will ensure that arcs are created with the right components.
     *
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
     * @return Petri net components that do not inherit from {@link pipe.models.component.Connectable}
     */
    private Collection<PetriNetComponent> getNonConnectablesToPaste() {
        final Collection<PetriNetComponent> components = new LinkedList<>();
        PetriNetComponentVisitor componentVisitor = new NonConnectableVisitor() {
            @Override
            public <T extends Connectable, S extends Connectable> void visit(Arc<S, T> arc) {
                components.add(arc);
            }

            @Override
            public void visit(Token token) {
                components.add(token);
            }

            @Override
            public void visit(Annotation annotation) {
                components.add(annotation);
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

    @Override
    public void keyTyped(KeyEvent e) {
        // Not needed
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Not needed
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            cancelPaste();
        }
    }

    public void cancelPaste() {
        cancelPaste(ApplicationSettings.getApplicationView().getCurrentTab());
    }

    void cancelPaste(PetriNetTab view) {
        pasteInProgress = false;
        view.repaint();
        view.remove(this);
    }

    /**
     * Used for creating anonymous classes that only visit
     * Places and Transition
     */
    private static interface PlaceTransitionVisitor extends PlaceVisitor, TransitionVisitor {

    }


    /**
     * Used for creating anonymous classes that visit non connectable classes
     */
    private static interface NonConnectableVisitor extends AnnotationVisitor, ArcVisitor, TokenVisitor {

    }

    /**
     * Private class used to set the bounds of a selection rectangle
     * Needed to create a class so that the visitor can change the values
     */
    private static class Location {
        double bottom = 0;

        double right = 0;

        double top = Double.MAX_VALUE;

        double left = Double.MAX_VALUE;
    }


}
