/*
 * Created on 04-Mar-2004
 * Author is Michael Camacho
 */
package pipe.views.viewComponents;

import pipe.controllers.PetriNetController;
import pipe.gui.Constants;
import pipe.gui.PetriNetTab;
import pipe.gui.widgets.AnnotationEditorPanel;
import pipe.gui.widgets.EscapableDialog;
import pipe.handlers.AnnotationNoteHandler;
import pipe.views.AbstractPetriNetViewComponent;
import uk.ac.imperial.pipe.models.petrinet.Annotation;
import uk.ac.imperial.pipe.models.petrinet.AnnotationImpl;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


public class AnnotationView extends Note {
    private static final long serialVersionUID = 1L;

    private final ResizePoint[] dragPoints = new ResizePoint[8];

    private boolean fillNote = true;

    private AffineTransform prova = new AffineTransform();

    public AnnotationView(Annotation annotation, PetriNetController controller) {
        super(annotation, controller);
        addChangeListener(annotation);
        setDragPoints();
        updateBounds();
    }

    @Override
    public final void updateBounds() {
        super.updateBounds();
        // TOP-LEFT
        dragPoints[0].setLocation(noteRect.getMinX(), noteRect.getMinY());
        // TOP-MIDDLE
        dragPoints[1].setLocation(noteRect.getCenterX(), noteRect.getMinY());
        // TOP-RIGHT
        dragPoints[2].setLocation(noteRect.getMaxX(), noteRect.getMinY());
        // MIDDLE-RIGHT
        dragPoints[3].setLocation(noteRect.getMaxX(), noteRect.getCenterY());
        // BOTTOM-RIGHT
        dragPoints[4].setLocation(noteRect.getMaxX(), noteRect.getMaxY());
        // BOTTOM-MIDDLE
        dragPoints[5].setLocation(noteRect.getCenterX(), noteRect.getMaxY());
        // BOTTOM-LEFT
        dragPoints[6].setLocation(noteRect.getMinX(), noteRect.getMaxY());
        // MIDDLE-LEFT
        dragPoints[7].setLocation(noteRect.getMinX(), noteRect.getCenterY());
    }

    @Override
    public void enableEditMode() {
        // Build interface
        EscapableDialog guiDialog = new EscapableDialog(petriNetController.getPetriNetTab().getApplicationView(), "PIPE5", true);

        guiDialog.add(new AnnotationEditorPanel(petriNetController.getAnnotationController(model)));

        // Make window fit contents' preferred size
        guiDialog.pack();

        // Move window to the middle of the screen
        guiDialog.setLocationRelativeTo(null);

        guiDialog.setResizable(false);
        guiDialog.setVisible(true);

        guiDialog.dispose();
    }

    /**
     * @param x
     * @param y
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

    @Override
    public int getLayerOffset() {
        return Constants.NOTE_LAYER_OFFSET;
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
        dragPoints[0] = new ResizePoint(ResizePoint.TOP | ResizePoint.LEFT);
        dragPoints[1] = new ResizePoint(ResizePoint.TOP);
        dragPoints[2] = new ResizePoint(ResizePoint.TOP | ResizePoint.RIGHT);
        dragPoints[3] = new ResizePoint(ResizePoint.RIGHT);
        dragPoints[4] = new ResizePoint(ResizePoint.BOTTOM | ResizePoint.RIGHT);
        dragPoints[5] = new ResizePoint(ResizePoint.BOTTOM);
        dragPoints[6] = new ResizePoint(ResizePoint.BOTTOM | ResizePoint.LEFT);
        dragPoints[7] = new ResizePoint(ResizePoint.LEFT);

        for (ResizePoint dragPoint : dragPoints) {
            ResizePointHandler handler = new ResizePointHandler(dragPoint);
            dragPoint.addMouseListener(handler);
            dragPoint.addMouseMotionListener(handler);
            add(dragPoint);
        }
    }

    @Override
    public Annotation getModel() {
        return model;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        prova = g2.getTransform();

        g2.setStroke(new BasicStroke(1.0f));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);

        if (isSelected() && !_ignoreSelection) {
            g2.setPaint(Constants.SELECTION_FILL_COLOUR);
            g2.fill(noteRect);
            if (drawBorder) {
                g2.setPaint(Constants.SELECTION_LINE_COLOUR);
                g2.draw(noteRect);
            }
        } else {
            g2.setPaint(Constants.ELEMENT_FILL_COLOUR);
            if (fillNote) {
                g2.fill(noteRect);
            }
            if (drawBorder) {
                g2.setPaint(Constants.ELEMENT_LINE_COLOUR);
                g2.draw(noteRect);
            }
        }
        for (ResizePoint dragPoint : dragPoints) {
            dragPoint.paintOnCanvas(g);
        }
    }

    public void changeBackground() {
        fillNote = !fillNote;
        note.setOpaque(fillNote);
    }

    @Override
    public void addToPetriNetTab(PetriNetTab tab) {
        AnnotationNoteHandler noteHandler = new AnnotationNoteHandler(this, tab, this.model, petriNetController);
        addMouseListener(noteHandler);
        addMouseMotionListener(noteHandler);
        note.addMouseListener(noteHandler);
        note.addMouseMotionListener(noteHandler);
    }

    /**
     * Deals with resizing of the annotation by handling mouse events
     */
    private class ResizePointHandler extends javax.swing.event.MouseInputAdapter {

        /**
         * Point to perform actions to
         */
        private final ResizePoint point;

        private Point start;


        public ResizePointHandler(ResizePoint point) {
            this.point = point;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            setDraggable(false);
            point.isPressed = true;
            point.repaint();
            start = e.getPoint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            setDraggable(true);
            point.isPressed = false;
            updateBounds();
            point.repaint();
        }

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

        private static final int TOP = 1;

        private static final int BOTTOM = 2;

        private static final int LEFT = 4;

        private static final int RIGHT = 8;

        public final int typeMask;

        private int SIZE = 3;

        private Rectangle shape;

        private boolean isPressed = false;

        public ResizePoint(int type) {
            setOpaque(false);
            setBounds(-SIZE - 1, -SIZE - 1, 2 * SIZE + Constants.ANNOTATION_SIZE_OFFSET + 1,
                    2 * SIZE + Constants.ANNOTATION_SIZE_OFFSET + 1);
            typeMask = type;
        }

        public void setLocation(double x, double y) {
            super.setLocation((int) (x - SIZE), (int) (y - SIZE));
        }

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

        public void paintOnCanvas(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setTransform(prova);
            if (isSelected() && !AbstractPetriNetViewComponent._ignoreSelection) {
                g2.translate(this.getLocation().x, this.getLocation().y);
                shape = new Rectangle(0, 0, 2 * SIZE, 2 * SIZE);
                g2.fill(shape);

                g2.setStroke(new BasicStroke(1.0f));
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isPressed) {
                    g2.setPaint(Constants.RESIZE_POINT_DOWN_COLOUR);
                } else {
                    g2.setPaint(Constants.ELEMENT_FILL_COLOUR);
                }
                g2.fill(shape);
                g2.setPaint(Constants.ELEMENT_LINE_COLOUR);
                g2.draw(shape);
                g2.setTransform(prova);
            }
        }
    }


}
