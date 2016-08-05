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
@SuppressWarnings("serial")
public class SelectionManager extends javax.swing.JComponent
        implements java.awt.event.MouseListener, java.awt.event.MouseWheelListener, java.awt.event.MouseMotionListener {

    /**
     * Color to paint selection highlighter
     */
    private static final Color SELECTION_COLOR = new Color(0, 0, 255, 24);

    /**
     * Outline of the selection handeller
     */
    private static final Color SELECTION_COLOR_OUTLINE = new Color(0, 0, 100);

    /**
     * Area of selection
     */
    private final Rectangle selectionRectangle = new Rectangle(-1, -1);

    /**
     * Tab that selection is taking place on
     */
    private final PetriNetTab petriNetTab;

    /**
     * Petri net controller for the underlying Petri net
     */
    private final PetriNetController petriNetController;

    /**
     * Start point of selection
     */
    private Point startPoint;

    /**
     * true if currently selecting items
     */
    private boolean isSelecting;

    /**
     * Legacy enabled
     */
    private boolean enabled = true;

    /**
     * Constructor
     * @param controller Petri net controller
     */
    public SelectionManager(PetriNetController controller) {
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        this.petriNetTab = controller.getPetriNetTab();
        this.petriNetController = controller;
    }

    /**
     * Enable the selection
     */
    public void enableSelection() {
        if (!enabled) {
            petriNetTab.add(this);
            enabled = true;
            updateBounds();
        }
    }

    /**
     * Update the displayed selection rectangle bounds
     *
     * Used because there is no layout manager for the canvas
     */
    public void updateBounds() {
        if (enabled) {
            setBounds(0, 0, petriNetTab.getWidth(), petriNetTab.getHeight());
        }
    }

    /**
     * Disable the selected items
     */
    public void disableSelection() {
        if (enabled) {
            petriNetTab.remove(this);
            enabled = false;
        }
    }

    /**
     * Paint a blue rectangle over the selected area
     * @param g graphics
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint(SELECTION_COLOR);
        g2d.fill(selectionRectangle);
        g2d.setPaint(SELECTION_COLOR_OUTLINE);
        g2d.draw(selectionRectangle);
    }

    /**
     * Update the selected area
     * @param e mouse event 
     */
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

    /**
     * Select the items that interact with the selection area
     * @param e mouse event 
     */
    private void processSelection(MouseEvent e) {
        if (!e.isShiftDown()) {
            clearSelection();
        }

        petriNetController.select(selectionRectangle);
    }

    /**
     * Deselect all components
     */
    public void clearSelection() {
        petriNetController.deselectAll();
    }


    /**
     * Noop
     * @param e mouse event 
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
            petriNetController.getDragManager().drag(e.getPoint());
        }
    }

    /**
     * Noop
     * @param e mouse event 
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        // Not used
    }

    /**
     * Noop
     * @param e mouse event 
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        // Not needed
    }

    /**
     * Select all items falling within the selected area
     * @param e mouse event 
     */
    @Override
    public void mousePressed(MouseEvent e) {
        startPoint = e.getPoint();
        if (e.getButton() == MouseEvent.BUTTON1 && !e.isControlDown()) {
            isSelecting = true;
            petriNetTab.setLayer(this, GUIConstants.SELECTION_LAYER_OFFSET);
            selectionRectangle.setRect(startPoint.getX(), startPoint.getY(), 0, 0);
            // Select anything that intersects with the rectangle.
            processSelection(e);
            repaint();
        }
    }

    /**
     *
     * Reset the selection area
     *
     * @param e mouse event 
     */
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

    /**
     * Noop
     * @param e mouse event 
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        // Not needed
    }

    /**
     * Noop
     * @param e mouse event 
     */
    @Override
    public void mouseExited(MouseEvent e) {
        // Not needed
    }

}
