/*
 * Created on 04-Mar-2004
 * Author is Michael Camacho
 */
package pipe.views.viewComponents;

import pipe.gui.ApplicationSettings;
import pipe.gui.Constants;
import pipe.gui.Grid;
import pipe.gui.ZoomController;
import pipe.gui.widgets.AnnotationPanel;
import pipe.gui.widgets.EscapableDialog;
import pipe.historyActions.AnnotationText;
import pipe.views.PetriNetView;
import pipe.views.PetriNetViewComponent;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;


public class AnnotationNote extends Note
{
    private static final long serialVersionUID = 1L;
    private boolean fillNote = true;
    private final ResizePoint[] dragPoints = new ResizePoint[8];
    private AffineTransform prova = new AffineTransform();

    public AnnotationNote(int x, int y)
    {
        super(x, y);
        setDragPoints();
        if(ApplicationSettings.getApplicationView() != null)
        {
            this.addZoomController(ApplicationSettings.getApplicationView().getCurrentTab().getZoomController());
        }
    }


    public AnnotationNote(String id, String text, int x, int y)
    {
        super(id, text, x, y);
        setDragPoints();
        if(ApplicationSettings.getApplicationView() != null)
        {
            this.addZoomController(ApplicationSettings.getApplicationView().getCurrentTab().getZoomController());
        }
    }


    public AnnotationNote(String text, int x, int y, int w, int h, boolean border)
    {
        super(text, x, y, w, h, border);
        setDragPoints();
        if(ApplicationSettings.getApplicationView() != null)
        {
            this.addZoomController(ApplicationSettings.getApplicationView().getCurrentTab().getZoomController());
        }
    }


    private void setDragPoints()
    {
        dragPoints[0] = new ResizePoint(this, ResizePoint.TOP |
                ResizePoint.LEFT);
        dragPoints[1] = new ResizePoint(this, ResizePoint.TOP);
        dragPoints[2] = new ResizePoint(this, ResizePoint.TOP |
                ResizePoint.RIGHT);
        dragPoints[3] = new ResizePoint(this, ResizePoint.RIGHT);
        dragPoints[4] = new ResizePoint(this, ResizePoint.BOTTOM |
                ResizePoint.RIGHT);
        dragPoints[5] = new ResizePoint(this, ResizePoint.BOTTOM);
        dragPoints[6] = new ResizePoint(this, ResizePoint.BOTTOM |
                ResizePoint.LEFT);
        dragPoints[7] = new ResizePoint(this, ResizePoint.LEFT);

        for(int i = 0; i < 8; i++)
        {
            ResizePointHandler handler = new ResizePointHandler(dragPoints[i]);
            dragPoints[i].addMouseListener(handler);
            dragPoints[i].addMouseMotionListener(handler);
            add(dragPoints[i]);
        }
    }


    public void updateBounds()
    {
        super.updateBounds();
        if(dragPoints != null)
        {
            // TOP-LEFT
            dragPoints[0].setLocation(
                    ZoomController.getZoomedValue(noteRect.getMinX(), _zoomPercentage),
                    ZoomController.getZoomedValue(noteRect.getMinY(), _zoomPercentage));
            dragPoints[0].setZoom(_zoomPercentage);
            // TOP-MIDDLE
            dragPoints[1].setLocation(
                    ZoomController.getZoomedValue(noteRect.getCenterX(), _zoomPercentage),
                    ZoomController.getZoomedValue(noteRect.getMinY(), _zoomPercentage));
            dragPoints[1].setZoom(_zoomPercentage);
            // TOP-RIGHT
            dragPoints[2].setLocation(
                    ZoomController.getZoomedValue(noteRect.getMaxX(), _zoomPercentage),
                    ZoomController.getZoomedValue(noteRect.getMinY(), _zoomPercentage));
            dragPoints[2].setZoom(_zoomPercentage);
            // MIDDLE-RIGHT
            dragPoints[3].setLocation(
                    ZoomController.getZoomedValue(noteRect.getMaxX(), _zoomPercentage),
                    ZoomController.getZoomedValue(noteRect.getCenterY(), _zoomPercentage));
            dragPoints[3].setZoom(_zoomPercentage);
            // BOTTOM-RIGHT
            dragPoints[4].setLocation(
                    ZoomController.getZoomedValue(noteRect.getMaxX(), _zoomPercentage),
                    ZoomController.getZoomedValue(noteRect.getMaxY(), _zoomPercentage));
            dragPoints[4].setZoom(_zoomPercentage);
            // BOTTOM-MIDDLE
            dragPoints[5].setLocation(
                    ZoomController.getZoomedValue(noteRect.getCenterX(), _zoomPercentage),
                    ZoomController.getZoomedValue(noteRect.getMaxY(), _zoomPercentage));
            dragPoints[5].setZoom(_zoomPercentage);
            // BOTTOM-LEFT
            dragPoints[6].setLocation(
                    ZoomController.getZoomedValue(noteRect.getMinX(), _zoomPercentage),
                    ZoomController.getZoomedValue(noteRect.getMaxY(), _zoomPercentage));
            dragPoints[6].setZoom(_zoomPercentage);
            // MIDDLE-LEFT
            dragPoints[7].setLocation(
                    ZoomController.getZoomedValue(noteRect.getMinX(), _zoomPercentage),
                    ZoomController.getZoomedValue(noteRect.getCenterY(), _zoomPercentage));
            dragPoints[7].setZoom(_zoomPercentage);
        }
    }


    public boolean contains(int x, int y)
    {
        boolean pointContains = false;

        for(int i = 0; i < 8; i++)
        {
            pointContains |= dragPoints[i].contains(x - dragPoints[i].getX(),
                                                    y - dragPoints[i].getY());
        }

        return super.contains(x, y) || pointContains;
    }


    public void enableEditMode()
    {
        String oldText = note.getText();

        // Build interface
        EscapableDialog guiDialog =
                new EscapableDialog(ApplicationSettings.getApplicationView(), "PIPE2", true);

        guiDialog.add(new AnnotationPanel(this));

        // Make window fit contents' preferred size
        guiDialog.pack();

        // Move window to the middle of the screen
        guiDialog.setLocationRelativeTo(null);

        guiDialog.setResizable(false);
        guiDialog.setVisible(true);

        guiDialog.dispose();

        String newText = note.getText();
        if(oldText != null && !newText.equals(oldText))
        {
            // Text has been changed
            ApplicationSettings.getApplicationView().getCurrentTab().getHistoryManager().addNewEdit(
                    new AnnotationText(this, oldText, newText));
            updateBounds();
        }
    }


    public AnnotationNote paste(double x, double y, boolean toAnotherView, PetriNetView model)
    {
        return new AnnotationNote(this.note.getText(),
                                  Grid.getModifiedX(x + this.getX()),
                                  Grid.getModifiedY(y + this.getY()),
                                  this.note.getWidth(),
                                  this.note.getHeight(),
                                  this.isShowingBorder());
    }


    public AnnotationNote copy()
    {
        return new AnnotationNote(this.note.getText(),
                                  ZoomController.getUnzoomedValue(this.getX(), _zoomPercentage),
                                  ZoomController.getUnzoomedValue(this.getY(), _zoomPercentage),
                                  this.note.getWidth(),
                                  this.note.getHeight(),
                                  this.isShowingBorder());
    }


    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        prova = g2.getTransform();

        g2.setStroke(new BasicStroke(1.0f));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                            RenderingHints.VALUE_STROKE_NORMALIZE);

        g2.transform(ZoomController.getTransform(_zoomPercentage));
        if(_selected && !_ignoreSelection)
        {
            g2.setPaint(Constants.SELECTION_FILL_COLOUR);
            g2.fill(noteRect);
            if(drawBorder)
            {
                g2.setPaint(Constants.SELECTION_LINE_COLOUR);
                g2.draw(noteRect);
            }
        }
        else
        {
            g2.setPaint(Constants.ELEMENT_FILL_COLOUR);
            if(fillNote)
            {
                g2.fill(noteRect);
            }
            if(drawBorder)
            {
                g2.setPaint(Constants.ELEMENT_LINE_COLOUR);
                g2.draw(noteRect);
            }
        }
        for(int i = 0; i < 8; i++)
        {
            dragPoints[i].myPaintComponent(g);
        }

        g2.transform(ZoomController.getTransform(_zoomPercentage));
    }


    public int getLayerOffset()
    {
        return Constants.NOTE_LAYER_OFFSET;
    }


    public boolean isFilled()
    {
        return fillNote;
    }


    public void changeBackground()
    {
        fillNote = !fillNote;
        note.setOpaque(fillNote);
    }


    private class ResizePointHandler
            extends javax.swing.event.MouseInputAdapter
    {

        private final ResizePoint myPoint;
        private Point start;


        public ResizePointHandler(ResizePoint point)
        {
            myPoint = point;
        }


        public void mousePressed(MouseEvent e)
        {
            myPoint.myNote.setDraggable(false);
            myPoint.isPressed = true;
            myPoint.repaint();
            start = e.getPoint();
        }


        public void mouseDragged(MouseEvent e)
        {
            myPoint.drag(Grid.getModifiedX(e.getX() - start.x),
                         Grid.getModifiedY(e.getY() - start.y));
            myPoint.myNote.updateBounds();
            myPoint.repaint();
        }


        public void mouseReleased(MouseEvent e)
        {
            myPoint.myNote.setDraggable(true);
            myPoint.isPressed = false;
            myPoint.myNote.updateBounds();
            myPoint.repaint();
        }

    }

    public class ResizePoint  extends javax.swing.JComponent
        {

            /**
             *
             */
            private static final long serialVersionUID = 1L;
            private int SIZE = 3;
            private static final int TOP = 1;
            private static final int BOTTOM = 2;
            private static final int LEFT = 4;
            private static final int RIGHT = 8;

            private Rectangle shape;
            private boolean isPressed = false;

            public Note getMyNote()
            {
                return myNote;
            }

            private final Note myNote;
            public final int typeMask;


            public ResizePoint(Note obj, int type)
            {
                myNote = obj;
                setOpaque(false);
                setBounds(-SIZE - 1,
                          -SIZE - 1,
                          2 * SIZE + Constants.ANNOTATION_SIZE_OFFSET + 1,
                          2 * SIZE + Constants.ANNOTATION_SIZE_OFFSET + 1);
                typeMask = type;
            }


            public void setLocation(double x, double y)
            {
                super.setLocation((int) (x - SIZE), (int) (y - SIZE));
            }


            private void drag(int x, int y)
            {
                if((typeMask & TOP) == TOP)
                {
                    myNote.adjustTop(ZoomController.getUnzoomedValue(y, _zoomPercentage));
                }
                if((typeMask & BOTTOM) == BOTTOM)
                {
                    myNote.adjustBottom(ZoomController.getUnzoomedValue(y, _zoomPercentage));
                }
                if((typeMask & LEFT) == LEFT)
                {
                    myNote.adjustLeft(ZoomController.getUnzoomedValue(x, _zoomPercentage));
                }
                if((typeMask & RIGHT) == RIGHT)
                {
                    myNote.adjustRight(ZoomController.getUnzoomedValue(x, _zoomPercentage));
                }
                ApplicationSettings.getApplicationView().getCurrentTab().setNetChanged(true);
            }


            public void myPaintComponent(Graphics g)
            {
                Graphics2D g2 = (Graphics2D) g;
                g2.setTransform(prova);
                if(myNote.getSelected() && !PetriNetViewComponent._ignoreSelection)
                {
                    g2.translate(this.getLocation().x, this.getLocation().y);
                    shape = new Rectangle(0, 0, 2 * SIZE, 2 * SIZE);
                    g2.fill(shape);

                    g2.setStroke(new BasicStroke(1.0f));
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                        RenderingHints.VALUE_ANTIALIAS_ON);
                    if(isPressed)
                    {
                        g2.setPaint(Constants.RESIZE_POINT_DOWN_COLOUR);
                    }
                    else
                    {
                        g2.setPaint(Constants.ELEMENT_FILL_COLOUR);
                    }
                    g2.fill(shape);
                    g2.setPaint(Constants.ELEMENT_LINE_COLOUR);
                    g2.draw(shape);
                    g2.setTransform(prova);
                }
            }


            // change ResizePoint's size a little bit acording to the zoom percent
            private void setZoom(int percent)
            {
                if(_zoomPercentage >= 220)
                {
                    SIZE = 5;
                }
                else if(_zoomPercentage >= 120)
                {
                    SIZE = 4;
                }
                else if(_zoomPercentage >= 60)
                {
                    SIZE = 3;
                }
            }
        }



}
