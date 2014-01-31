/*
 * CopyPasteManager.java
 */
package pipe.gui;

import pipe.controllers.PetriNetController;
import pipe.historyActions.AddPetriNetObject;
import pipe.historyActions.HistoryManager;
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
import pipe.naming.UniqueNamer;
import pipe.views.PipeApplicationView;
import pipe.visitor.PasteVisitor;
import pipe.visitor.foo.PetriNetComponentVisitor;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;


/**
 * Class to handle paste & paste functionality
 *
 * @author Pere Bonet
 */
public class CopyPasteManager extends javax.swing.JComponent
        implements pipe.gui.Zoomable, java.awt.event.MouseListener, java.awt.event.MouseMotionListener,
        java.awt.event.KeyListener {

    private static final Color PASTE_COLOR = new Color(155, 155, 155, 100);

    private static final Color PASTE_COLOR_OUTLINE = new Color(155, 0, 0, 0);

    private final Rectangle pasteRectangle = new Rectangle(-1, -1);

    private final PetriNetTab petriNetTab;

    private final PetriNet petriNet;

    private final ArrayList<ArrayList> objectsToPaste = new ArrayList();

    private final Point rectangleOrigin = new Point();

    // pasteInProgres is true when pasteRectangle is visible (user is doing a
    // paste but still hasn't chosen the position where elements will be pasted).
    private boolean pasteInProgress = false;

    private PetriNetTab _sourceView;

    private int zoom;

    private Collection<PetriNetComponent> pasteComponents = new ArrayList<PetriNetComponent>();


    public CopyPasteManager(PetriNetTab petriNetTab, PetriNet net) {
        this.petriNetTab = petriNetTab;
        petriNet = net;
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        zoom = petriNetTab.getZoom();

    }

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
            component.accept(locationVisitor);
        }

        pasteRectangle.setRect(location.left, location.top, location.right - location.left,
                location.bottom - location.top);
        rectangleOrigin.setLocation(location.left, location.top);
    }

    public void showPasteRectangle() {
        if (!pasteInProgress) {
            petriNetTab.add(this);
            requestFocusInWindow();
            if (zoom != petriNetTab.getZoom()) {
                updateSize(pasteRectangle, zoom, petriNetTab.getZoom());
                zoom = petriNetTab.getZoom();
            }

            petriNetTab.setLayer(this, Constants.SELECTION_LAYER_OFFSET);
            repaint();
            pasteInProgress = true;
            updateBounds();
        }
    }

    private void updateBounds() {
        if (pasteInProgress) {
            PipeApplicationView applicationView = ApplicationSettings.getApplicationView();
            setBounds(0, 0, applicationView.getCurrentTab().getWidth(),
                    ApplicationSettings.getApplicationView().getCurrentTab().getHeight());
        }
    }

    private void updateSize(Rectangle pasteRectangle, int zoom, int newZoom) {
        ZoomController zoomController = petriNetTab.getZoomController();
        int realWidth = pasteRectangle.width;
        int realHeight = pasteRectangle.height;

        pasteRectangle.setSize((int) (realWidth * zoomController.getScaleFactor()),
                (int) (realHeight * zoomController.getScaleFactor()));
    }

    public boolean pasteInProgress() {
        return pasteInProgress;
    }

    public boolean pasteEnabled() {
        return !objectsToPaste.isEmpty();
    }

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

    /* (non-Javadoc)
     * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (pasteInProgress) {
            updateRect(e.getPoint());
        }
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        if (pasteInProgress) {
            updateRect(e.getPoint());
        }
    }

    private void updateRect(Point point) {
        pasteRectangle.setLocation(point);
        //view.updatePreferredSize();
        repaint();
        updateBounds();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        petriNetTab.updatePreferredSize();
        petriNetTab.setLayer(this, Constants.LOWEST_LAYER_OFFSET);
        repaint();
        //now, we have the position of the pasted objects so we can show them.
        paste(petriNetTab);
    }

    /**
     * Paste pastes the new objects into the petriNet specified in consturction.
     * <p/>
     * It first pastes the connectables, and then other components. This ordering is important
     * and will ensure that arcs are created with the right components.
     *
     * @param petriNetTab
     */
    private void paste(PetriNetTab petriNetTab) {
        pasteInProgress = false;
        petriNetTab.remove(this);

        if (pasteComponents.isEmpty()) {
            return;
        }

        ZoomController zoomController = petriNetTab.getZoomController();
        double despX = pasteRectangle.getX() - rectangleOrigin.getX();
        double despY = pasteRectangle.getY() - rectangleOrigin.getY();

        MultipleNamer multipleNamer = new UniqueNamer(petriNet);
        PasteVisitor pasteVisitor = new PasteVisitor(petriNet, pasteComponents, multipleNamer, despX, despY);

        for (Connectable component : getConnectablesToPaste()) {
            component.accept(pasteVisitor);
        }
        for (PetriNetComponent component : getNonConnectablesToPaste()) {
            component.accept(pasteVisitor);
        }

        createPasteHistoryItem(pasteVisitor.getCreatedComponents());
    }

    /**
     * Creates a history item for the new components added to the petrinet
     *
     * @param createdComponents
     */
    private void createPasteHistoryItem(Iterable<PetriNetComponent> createdComponents) {
        PetriNetController controller = ApplicationSettings.getApplicationController().getActivePetriNetController();
        HistoryManager historyManager = controller.getHistoryManager();
        historyManager.newEdit();

        for (PetriNetComponent component : createdComponents) {
            AddPetriNetObject addAction = new AddPetriNetObject(component, petriNet);
            historyManager.addEdit(addAction);
        }
    }

    private Collection<Connectable> getConnectablesToPaste() {
        final Collection<Connectable> connectables = new LinkedList<Connectable>();
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
            component.accept(connectableVisitor);
        }
        return connectables;
    }

    private Collection<PetriNetComponent> getNonConnectablesToPaste() {
        final Collection<PetriNetComponent> components = new LinkedList<PetriNetComponent>();
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
            component.accept(componentVisitor);
        }
        return components;
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(MouseEvent e) {
        // Not needed
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
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
     * Private class used to set the bounds of a selectionn rectangle
     * Needed to create a class so that the visitor can change the values
     */
    private static class Location {
        double bottom = 0;

        double right = 0;

        double top = Double.MAX_VALUE;

        double left = Double.MAX_VALUE;
    }


}
