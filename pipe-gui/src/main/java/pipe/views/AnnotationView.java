/*
 * Created on 04-Mar-2004
 * Author is Michael Camacho
 */
package pipe.views;

import pipe.constants.GUIConstants;
import pipe.controllers.PetriNetController;
import uk.ac.imperial.pipe.models.petrinet.Annotation;
import uk.ac.imperial.pipe.models.petrinet.AnnotationImpl;

import javax.swing.event.MouseInputAdapter;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;


/**
 * View representation of the annotation Petri net component
 */
@SuppressWarnings("serial")
public final class AnnotationView extends Note {

    /**
     * The number of points defined that can be dragged on the annotation to increase
     * its size
     */
    public static final int NO_DRAG_POINTS = 8;

    /**
     * Drag point locations on the annotation
     */
    private final List<ResizePoint> dragPoints = new ArrayList<>(NO_DRAG_POINTS);

    /**
     * Affine transform for drawing purposes. This transform is applied
     * to the rectange and can be used to rotate, stretch etc the annotation
     */
    private AffineTransform prova = new AffineTransform();

    /**
     * Constructor
     * @param annotation underlying annotation model
     * @param controller Petri net controller for the Petri net the annotation belongs to
     * @param parent parent container of this view
     * @param handler how the annotation will handle mouse events
     */
    public AnnotationView(Annotation annotation, PetriNetController controller, Container parent, MouseInputAdapter handler) {
        super(annotation, controller, parent);
        addChangeListener(annotation);
        setDragPoints();
        setMouseHandler(handler);
        updateBounds();
    }

    /**
     * Registers the handler to this view
     * @param handler how the annotation will handle mouse events
     */
    private void setMouseHandler(MouseInputAdapter handler) {
        addMouseListener(handler);
        addMouseMotionListener(handler);
        noteText.addMouseListener(handler);
        noteText.addMouseMotionListener(handler);
    }

    /**
     * Update the (x,y) and width height boundary of this view
     *
     * Implemented because the canvas has no layout manager
     */
    @Override
    public void updateBounds() {
        super.updateBounds();
        // TOP-LEFT
        dragPoints.get(0).setLocation(noteRect.getMinX(), noteRect.getMinY());
        // TOP-MIDDLE
        dragPoints.get(1).setLocation(noteRect.getCenterX(), noteRect.getMinY());
        // TOP-RIGHT
        dragPoints.get(2).setLocation(noteRect.getMaxX(), noteRect.getMinY());
        // MIDDLE-RIGHT
        dragPoints.get(3).setLocation(noteRect.getMaxX(), noteRect.getCenterY());
        // BOTTOM-RIGHT
        dragPoints.get(4).setLocation(noteRect.getMaxX(), noteRect.getMaxY());
        // BOTTOM-MIDDLE
        dragPoints.get(5).setLocation(noteRect.getCenterX(), noteRect.getMaxY());
        // BOTTOM-LEFT
        dragPoints.get(6).setLocation(noteRect.getMinX(), noteRect.getMaxY());
        // MIDDLE-LEFT
        dragPoints.get(7).setLocation(noteRect.getMinX(), noteRect.getCenterY());
    }



    /**
     * @param x coordinate
     * @param y coordinate 
     * @return true if (x, y) intersect annotation location
     */
    @Override
    public boolean contains(int x, int y) {
        boolean pointContains = false;

        for (ResizePoint dragPoint : dragPoints) {
            pointContains |= dragPoint.contains(x - dragPoint.getX(), y - dragPoint.getY());
        }

        return super.contains(x, y) || pointContains;
    }

    /**
     * Listens for changes to the annotation model
     *
     * @param annotation model to register changes to
     */
    private void addChangeListener(Annotation annotation) {
        annotation.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                String name = propertyChangeEvent.getPropertyName();
                if (name.equals(AnnotationImpl.TEXT_CHANGE_MESSAGE)) {
                    String text = (String) propertyChangeEvent.getNewValue();
                    setText(text);
                } else if (name.equals(Annotation.X_CHANGE_MESSAGE) || name.equals(Annotation.Y_CHANGE_MESSAGE)) {
                    updateBounds();
                }
            }
        });
    }

    /**
     * Creates drag points for all the corners and half way along
     * each edge
     */
    private void setDragPoints() {
        dragPoints.add(new ResizePoint(ResizePoint.TOP | ResizePoint.LEFT));
        dragPoints.add(new ResizePoint(ResizePoint.TOP));
        dragPoints.add(new ResizePoint(ResizePoint.TOP | ResizePoint.RIGHT));
        dragPoints.add(new ResizePoint(ResizePoint.RIGHT));
        dragPoints.add(new ResizePoint(ResizePoint.BOTTOM | ResizePoint.RIGHT));
        dragPoints.add(new ResizePoint(ResizePoint.BOTTOM));
        dragPoints.add(new ResizePoint(ResizePoint.BOTTOM | ResizePoint.LEFT));
        dragPoints.add(new ResizePoint(ResizePoint.LEFT));

        for (ResizePoint dragPoint : dragPoints) {
            ResizePointHandler handler = new ResizePointHandler(dragPoint);
            dragPoint.addMouseListener(handler);
            dragPoint.addMouseMotionListener(handler);
            add(dragPoint);
        }
    }

    /**
     * Paints the annotation and its text using the graphics
     * @param g graphics 
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        prova = g2.getTransform();

        g2.setStroke(new BasicStroke(1.0f));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);

        if (isSelected() && !ignoreSelection) {
            g2.setPaint(GUIConstants.SELECTION_FILL_COLOUR);
            g2.fill(noteRect);
            if (drawBorder) {
                g2.setPaint(GUIConstants.SELECTION_LINE_COLOUR);
                g2.draw(noteRect);
            }
        } else {
            g2.setPaint(GUIConstants.ELEMENT_FILL_COLOUR);
            /*

     */
            boolean fillNote = true;
            if (fillNote) {
                g2.fill(noteRect);
            }
            if (drawBorder) {
                g2.setPaint(GUIConstants.ELEMENT_LINE_COLOUR);
                g2.draw(noteRect);
            }
        }
        for (ResizePoint dragPoint : dragPoints) {
            dragPoint.paintOnCanvas(g);
        }
    }

    /**
     * Noop
     * @param container to add itself to
     */
    @Override
    public void addToContainer(Container container) {
        //Noop
    }

    /**
     * Noop
     */
    @Override
    public void componentSpecificDelete() {
        //Noop
    }

    /**
     * Deals with resizing of the annotation by handling mouse events
     */
    private class ResizePointHandler extends javax.swing.event.MouseInputAdapter {

        /**
         * Point to perform actions to
         */
        private final ResizePoint point;

        /**
         * Start point of resizing
         */
        private Point start;


        /**
         * Constructor
         * @param point the point to perform the resize to
         */
        public ResizePointHandler(ResizePoint point) {
            this.point = point;
        }

        /**
         * When the mouse is pressed on the point repaint it and set the start location
         * @param e mouse event
         */
        @Override
        public void mousePressed(MouseEvent e) {
            point.isPressed = true;
            point.repaint();
            start = e.getPoint();
        }

        /**
         * When the mouse is pressed on the point repaint it
         * @param e mouse event
         */
        @Override
        public void mouseReleased(MouseEvent e) {
            point.isPressed = false;
            updateBounds();
            point.repaint();
        }

        /**
         * When the mouse is dragged, drag the point using the set start point when the mouse was pressed
         * @param e mouse event
         */
        @Override
        public void mouseDragged(MouseEvent e) {
            point.drag(e.getX() - start.x, e.getY() - start.y);
            updateBounds();
            point.repaint();
        }

    }

    /**
     * Resizable point for changing the size of the annotation
     * These appear on the boarder of the annotation
     */
    public class ResizePoint extends javax.swing.JComponent {

        /**
         * Top horizontal of the annotation box
         */
        private static final int TOP = 1;

        /**
         * Bottom horizontal of the annotation box
         */
        private static final int BOTTOM = 2;

        /**
         * Left vertical of the annotation box
         */
        private static final int LEFT = 4;

        /**
         * Right vertical of the annotation box
         */
        private static final int RIGHT = 8;

        public final int typeMask;

        /**
         * Size of the point
         */
        private static final int SIZE = 3;

        /**
         * Graphical representation of each point
         */
        private Rectangle shape;

        /**
         * True if the point has been pressed
         */
        private boolean isPressed = false;

        /**
         * Constructor
         * @param type of point
         */
        public ResizePoint(int type) {
            setOpaque(false);
            setBounds(-SIZE - 1, -SIZE - 1, 2 * SIZE + GUIConstants.ANNOTATION_SIZE_OFFSET + 1,
                    2 * SIZE + GUIConstants.ANNOTATION_SIZE_OFFSET + 1);
            typeMask = type;
        }

        /**
         * Set the x,y location of the point
         * @param x coordinate 
         * @param y coordinate
         */
        public void setLocation(double x, double y) {
            super.setLocation((int) (x - SIZE), (int) (y - SIZE));
        }

        /**
         * Drag the point by x, y
         * @param x coordinate
         * @param y coordinate 
         */
        private void drag(int x, int y) {
            if ((typeMask & TOP) == TOP) {
                adjustTop(y);
            }
            if ((typeMask & BOTTOM) == BOTTOM) {
                adjustBottom(y);
            }
            if ((typeMask & LEFT) == LEFT) {
                adjustLeft(x);
            }
            if ((typeMask & RIGHT) == RIGHT) {
                adjustRight(x);
            }
        }

        /**
         * Paint the point on the canvas
         * @param g graphics 
         */
        public void paintOnCanvas(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setTransform(prova);
            if (isSelected() && !AbstractPetriNetViewComponent.ignoreSelection) {
                g2.translate(this.getLocation().x, this.getLocation().y);
                shape = new Rectangle(0, 0, 2 * SIZE, 2 * SIZE);
                g2.fill(shape);

                g2.setStroke(new BasicStroke(1.0f));
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isPressed) {
                    g2.setPaint(GUIConstants.RESIZE_POINT_DOWN_COLOUR);
                } else {
                    g2.setPaint(GUIConstants.ELEMENT_FILL_COLOUR);
                }
                g2.fill(shape);
                g2.setPaint(GUIConstants.ELEMENT_LINE_COLOUR);
                g2.draw(shape);
                g2.setTransform(prova);
            }
        }
    }


}
