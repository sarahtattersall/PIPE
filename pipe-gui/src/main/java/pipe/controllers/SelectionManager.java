/*
 * Created on 08-Feb-2004
 */
package pipe.controllers;

import pipe.constants.GUIConstants;
import pipe.gui.PetriNetTab;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;


/**
 * @author Peter Kyme, Michael Camacho
 *         Class to handle selection rectangle functionality
 */
public class SelectionManager extends javax.swing.JComponent
        implements java.awt.event.MouseListener, java.awt.event.MouseWheelListener, java.awt.event.MouseMotionListener {

    private static final Color SELECTION_COLOR = new Color(0, 0, 255, 24);

    private static final Color SELECTION_COLOR_OUTLINE = new Color(0, 0, 100);

    private final Rectangle selectionRectangle = new Rectangle(-1, -1);

    private final PetriNetTab petriNetTab;

    private final PetriNetController petriNetController;

    private final DragManager dragManager;

    private Point startPoint;

    private boolean isSelecting;

    private boolean enabled = true;

    public SelectionManager(PetriNetController controller) {
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        this.petriNetTab = controller.getPetriNetTab();
        this.petriNetController = controller;
        dragManager = controller.getDragManager();
    }

    public void enableSelection() {
        if (!enabled) {
            petriNetTab.add(this);
            enabled = true;
            updateBounds();
        }
    }

    public void updateBounds() {
        if (enabled) {
            setBounds(0, 0, petriNetTab.getWidth(), petriNetTab.getHeight());
        }
    }

    public void disableSelection() {
        if (enabled) {
            petriNetTab.remove(this);
            enabled = false;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint(SELECTION_COLOR);
        g2d.fill(selectionRectangle);
        g2d.setPaint(SELECTION_COLOR_OUTLINE);
        g2d.draw(selectionRectangle);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (isSelecting) {
            Point point = e.getPoint();
            selectionRectangle.setSize((int) Math.abs(point.getX() - startPoint.getX()),
                    (int) Math.abs(point.getY() - startPoint.getY()));
            selectionRectangle.setLocation((int) Math.min(startPoint.getX(), point.getX()),
                    (int) Math.min(startPoint.getY(), point.getY()));
            // Select anything that intersects with the rectangle.
            processSelection(e);
            repaint();
        } else {
            handleOutOfBoundsDrag(e);
        }
    }

    private void processSelection(MouseEvent e) {
        if (!e.isShiftDown()) {
            clearSelection();
        }

        petriNetController.select(selectionRectangle);
    }

    public void clearSelection() {
        petriNetController.deselectAll();
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        // Not needed
    }

    /**
     * Out of bounds drags are when the mouse has moved fast enough that they no longer
     * contained in the object they are dragging. Since the SelectionManager spans the whole
     * screen when in selection mode, it defaults back to this class with a call to drag
     *
     * @param e mouse drag event
     */
    private void handleOutOfBoundsDrag(MouseEvent e) {
        if (!e.isConsumed()) {
            dragManager.drag(e.getPoint());
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        // Not used
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Not needed
    }

    @Override
    public void mousePressed(MouseEvent e) {
        startPoint = e.getPoint();
        if (e.getButton() == MouseEvent.BUTTON1 && !(e.isControlDown())) {
            isSelecting = true;
            petriNetTab.setLayer(this, GUIConstants.SELECTION_LAYER_OFFSET);
            selectionRectangle.setRect(startPoint.getX(), startPoint.getY(), 0, 0);
            // Select anything that intersects with the rectangle.
            processSelection(e);
            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (isSelecting) {
            // Select anything that intersects with the rectangle.
            processSelection(e);
            isSelecting = false;
            petriNetTab.setLayer(this, GUIConstants.LOWEST_LAYER_OFFSET);
            selectionRectangle.setRect(-1, -1, 0, 0);
            repaint();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // Not needed
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // Not needed
    }

}
