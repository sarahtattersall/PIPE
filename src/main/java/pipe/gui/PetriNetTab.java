package pipe.gui;

import pipe.handlers.*;
import pipe.historyActions.AddPetriNetObject;
import pipe.historyActions.HistoryManager;
import pipe.models.PipeApplicationModel;
import pipe.views.*;
import pipe.views.viewComponents.*;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class PetriNetTab extends JLayeredPane implements Observer, Printable
{

    public final PetriNetView _petriNetView;
    public File _appFile;

    public boolean netChanged = false;
    private boolean animationmode = false;
    public ArcView _createArcView;
    private final AnimationHandler animationHandler = new AnimationHandler();
    private boolean metaDown = false;
    private final SelectionManager selection;
    private final HistoryManager _historyManager;
    private final PipeApplicationView _pipeApplicationView;
    private final ArrayList<PetriNetViewComponent> petriNetComponents = new ArrayList();
    private final ZoomController zoomControl;
    public boolean _wasNewPertiNetComponentCreated = false;
    private boolean _zoomCalled = true;
    private final Point viewPosition = new Point(0, 0);


    public PetriNetTab(PetriNetView petriNetView)
    {
        _petriNetView = petriNetView;
        _pipeApplicationView = ApplicationSettings.getApplicationView();
        setLayout(null);
        setOpaque(true);
        setDoubleBuffered(true);
        setAutoscrolls(true);
        setBackground(Constants.ELEMENT_FILL_COLOUR);
        zoomControl = new ZoomController(100);
        MouseHandler handler = new MouseHandler(this, _petriNetView);
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        addMouseListener(handler);
        addMouseMotionListener(handler);
        try
        {
            addMouseWheelListener(handler);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        selection = new SelectionManager(this);
        _historyManager = new HistoryManager(this, _petriNetView);
    }

    public void addNewPetriNetObject(PetriNetViewComponent newPetriNetViewComponent)
    {
        if(newPetriNetViewComponent != null)
        {
            if(newPetriNetViewComponent.getMouseListeners().length == 0)
            {
                NameLabel nameLabel = newPetriNetViewComponent.getNameLabel();
                if(newPetriNetViewComponent instanceof PlaceView)
                {
                    LabelHandler labelHandler = new LabelHandler(nameLabel,(PlaceView) newPetriNetViewComponent);
                    nameLabel.addMouseListener(labelHandler);
                    nameLabel.addMouseMotionListener(labelHandler);
                    nameLabel.addMouseWheelListener(labelHandler);

                    PlaceHandler placeHandler = new PlaceHandler(this, (PlaceView) newPetriNetViewComponent);
                    newPetriNetViewComponent.addMouseListener(placeHandler);
                    newPetriNetViewComponent.addMouseWheelListener(placeHandler);
                    newPetriNetViewComponent.addMouseMotionListener(placeHandler);
                }
                else if(newPetriNetViewComponent instanceof TransitionView)
                {
                    LabelHandler labelHandler = new LabelHandler(nameLabel,(TransitionView) newPetriNetViewComponent);
                    nameLabel.addMouseListener(labelHandler);
                    nameLabel.addMouseMotionListener(labelHandler);
                    nameLabel.addMouseWheelListener(labelHandler);

                    TransitionHandler transitionHandler = new TransitionHandler(this, (TransitionView) newPetriNetViewComponent);
                    newPetriNetViewComponent.addMouseListener(transitionHandler);
                    newPetriNetViewComponent.addMouseMotionListener(transitionHandler);
                    newPetriNetViewComponent.addMouseWheelListener(transitionHandler);
                    newPetriNetViewComponent.addMouseListener(animationHandler);
                }
                else if(newPetriNetViewComponent instanceof GroupTransitionView)
                {
                    GroupTransitionHandler groupTransitionHandler = new GroupTransitionHandler(this, (GroupTransitionView) newPetriNetViewComponent);
                    newPetriNetViewComponent.addMouseListener(groupTransitionHandler);
                    newPetriNetViewComponent.addMouseMotionListener(groupTransitionHandler);
                    newPetriNetViewComponent.addMouseWheelListener(groupTransitionHandler);
                    newPetriNetViewComponent.addMouseListener(animationHandler);
                }
                else if(newPetriNetViewComponent instanceof ArcView)
                {
                    ArcHandler arcHandler = new ArcHandler(this, (ArcView) newPetriNetViewComponent);
                    newPetriNetViewComponent.addMouseListener(arcHandler);
                    newPetriNetViewComponent.addMouseWheelListener(arcHandler);
                    newPetriNetViewComponent.addMouseMotionListener(arcHandler);
                }
                else if(newPetriNetViewComponent instanceof AnnotationNote)
                {
                    AnnotationNoteHandler noteHandler = new AnnotationNoteHandler(this, (AnnotationNote) newPetriNetViewComponent);
                    newPetriNetViewComponent.addMouseListener(noteHandler);
                    newPetriNetViewComponent.addMouseMotionListener(noteHandler);
                    ((Note) newPetriNetViewComponent).getNote().addMouseListener(noteHandler);
                    ((Note) newPetriNetViewComponent).getNote().addMouseMotionListener(noteHandler);
                }
                else if(newPetriNetViewComponent instanceof Parameter)
                {
                    ParameterHandler parameterHandler = new ParameterHandler(this, (Parameter) newPetriNetViewComponent);
                    newPetriNetViewComponent.addMouseListener(parameterHandler);
                    newPetriNetViewComponent.addMouseMotionListener(parameterHandler);
                    ((Parameter) newPetriNetViewComponent).getNote().addMouseListener(parameterHandler);
                    ((Parameter) newPetriNetViewComponent).getNote().addMouseMotionListener(parameterHandler);
                }
                add(newPetriNetViewComponent);
                newPetriNetViewComponent.zoomUpdate(getZoom());
            }
        }
        validate();
        repaint();
    }


    public void update(Observable o, Object diffObj)
    {
        if((diffObj instanceof PetriNetViewComponent) && (diffObj != null))
        {
            if(ApplicationSettings.getApplicationModel().getMode() == Constants.CREATING)
                addNewPetriNetObject((PetriNetViewComponent) diffObj);
            repaint();
        }
    }


    public int print(Graphics g, PageFormat pageFormat, int pageIndex)
            throws PrinterException
    {
        if(pageIndex > 0)
            return Printable.NO_SUCH_PAGE;
        Graphics2D g2D = (Graphics2D) g;
        g2D.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        g2D.scale(0.5, 0.5);
        print(g2D);
        return Printable.PAGE_EXISTS;
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if(Grid.isEnabled())
        {
            Grid.updateSize(this);
            Grid.drawGrid(g);
        }

        selection.updateBounds();

        if(_zoomCalled)
        {
            ((JViewport) getParent()).setViewPosition(viewPosition);
            _pipeApplicationView.validate();
            _zoomCalled = false;
        }
    }


    public void updatePreferredSize()
    {
        Component[] components = getComponents();
        Dimension d = new Dimension(0, 0);
        for(Component component : components)
        {
            if(component.getClass() == SelectionManager.class)
            {
                continue;
            }
            Rectangle r = component.getBounds();
            int x = r.x + r.width + 20;
            int y = r.y + r.height + 20;
            if(x > d.width)
            {
                d.width = x;
            }
            if(y > d.height)
            {
                d.height = y;
            }
        }
        setPreferredSize(d);
        Container parent = getParent();
        if(parent != null)
        {
            parent.validate();
        }
    }


    public void changeAnimationMode(boolean status)
    {
        animationmode = status;
    }

    public void setCursorType(String type)
    {
        if(type.equals("arrow"))
        {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
        else if(type.equals("crosshair"))
        {
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        }
        else if(type.equals("move"))
        {
            setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        }
    }

    public SelectionManager getSelectionObject()
    {
        return selection;
    }

    public HistoryManager getHistoryManager()
    {
        return _historyManager;
    }

    public ZoomController getZoomController()
    {
        return zoomControl;
    }

    public void zoom()
    {
        Component[] children = getComponents();

        for(Component aChildren : children)
        {
            if(aChildren instanceof Zoomable)
                ((Zoomable) aChildren).zoomUpdate(zoomControl.getPercent());
        }
        _zoomCalled = true;
    }


    public void add(PetriNetViewComponent pn)
    {
        setLayer(pn, DEFAULT_LAYER.intValue() + pn.getLayerOffset());
        super.add(pn);
        pn.addedToGui();
        petriNetComponents.add(pn);
    }


    public void setMetaDown(boolean down)
    {
        metaDown = down;
    }

    public AnimationHandler getAnimationHandler()
    {
        return animationHandler;
    }

    public boolean isInAnimationMode()
    {
        return animationmode;
    }

    public boolean getNetChanged()
    {
        return netChanged;
    }

    public void setNetChanged(boolean _netChanged)
    {
        netChanged = _netChanged;
    }

    public ArrayList<PetriNetViewComponent> getPNObjects()
    {
        return petriNetComponents;
    }

    public void remove(Component comp)
    {
        petriNetComponents.remove(comp);
        super.remove(comp);
    }

    public void drag(Point dragStart, Point dragEnd)
    {
        if(dragStart == null)
        {
            return;
        }
        JViewport viewer = (JViewport) getParent();
        Point offScreen = viewer.getViewPosition();
        if(dragStart.x > dragEnd.x)
        {
            offScreen.translate(viewer.getWidth(), 0);
        }
        if(dragStart.y > dragEnd.y)
        {
            offScreen.translate(0, viewer.getHeight());
        }
        offScreen.translate(dragStart.x - dragEnd.x, dragStart.y - dragEnd.y);
        Rectangle r = new Rectangle(offScreen.x, offScreen.y, 1, 1);
        scrollRectToVisible(r);
    }

    private Point midpoint(int zoom)
    {
        JViewport viewport = (JViewport) getParent();
        double midpointX = ZoomController.getUnzoomedValue(
                viewport.getViewPosition().x + (viewport.getWidth() * 0.5), zoom);
        double midpointY = ZoomController.getUnzoomedValue(
                viewport.getViewPosition().y + (viewport.getHeight() * 0.5), zoom);
        return (new java.awt.Point((int) midpointX, (int) midpointY));
    }

    public void zoomIn()
    {
        int zoom = zoomControl.getPercent();
        if(zoomControl.zoomIn())
        {
            zoomTo(midpoint(zoom));
        }
    }

    public void zoomOut()
    {
        int zoom = zoomControl.getPercent();
        if(zoomControl.zoomOut())
        {
            zoomTo(midpoint(zoom));
        }
    }

    public void zoomTo(Point point)
    {
        int zoom = zoomControl.getPercent();
        JViewport viewport = (JViewport) getParent();
        double currentXNoZoom = ZoomController.getUnzoomedValue(viewport.getViewPosition().x + (viewport.getWidth() * 0.5), zoom);
        double newZoomedX = ZoomController.getZoomedValue(point.x, zoom);
        double newZoomedY = ZoomController.getZoomedValue(point.y, zoom);

        int newViewX = (int) (newZoomedX - (viewport.getWidth() * 0.5));
        if(newViewX < 0)
            newViewX = 0;

        int newViewY = (int) (newZoomedY - (viewport.getHeight() * 0.5));
        if(newViewY < 0)
            newViewY = 0;

        viewPosition.setLocation(newViewX, newViewY);
        viewport.setViewPosition(viewPosition);
        zoom();
        _pipeApplicationView.hideNet(true);
        updatePreferredSize();
    }


    public int getZoom()
    {
        return zoomControl.getPercent();
    }

    class Handler extends MouseInputAdapter
    {
    }

    public class MouseHandler extends MouseInputAdapter
    {
        private PetriNetViewComponent pn;
        private final PetriNetTab _petriNetTab;
        private final PetriNetView _model;
        private Point dragStart;

        public MouseHandler(PetriNetTab petriNetTab, PetriNetView _model)
        {
            super();
            this._petriNetTab = petriNetTab;
            this._model = _model;
        }


        private Point adjustPoint(Point p, int zoom)
        {
            int offset = (int) (ZoomController.getScaleFactor(zoom) * Constants.PLACE_TRANSITION_HEIGHT / 2);

            int x = ZoomController.getUnzoomedValue(p.x - offset, zoom);
            int y = ZoomController.getUnzoomedValue(p.y - offset, zoom);

            p.setLocation(x, y);
            return p;
        }


        private ConnectableView newPlace(Point p)
        {
            p = adjustPoint(p, _petriNetTab.getZoom());

            pn = new PlaceView((double) Grid.getModifiedX(p.x), (double) Grid.getModifiedY(p.y));
            _model.addPetriNetObject(pn);
            _petriNetTab.addNewPetriNetObject(pn);
            return (ConnectableView) pn;
        }


        private ConnectableView newTransition(Point p, boolean timed)
        {
            p = adjustPoint(p, _petriNetTab.getZoom());

            pn = new TransitionView((double) Grid.getModifiedX(p.x), (double) Grid.getModifiedY(p.y));
            ((TransitionView) pn).setTimed(timed);
            _model.addPetriNetObject(pn);
            _petriNetTab.addNewPetriNetObject(pn);
            return (ConnectableView) pn;
        }


        public void mousePressed(MouseEvent e)
        {
            PipeApplicationModel applicationModel = ApplicationSettings.getApplicationModel();
            Point start = e.getPoint();
            Point p;

            if(SwingUtilities.isLeftMouseButton(e))
            {
                int mode = applicationModel.getMode();
                switch(mode)
                {
                    case Constants.PLACE:
                        ConnectableView pto = newPlace(e.getPoint());
                        _petriNetTab.getHistoryManager().addNewEdit(new AddPetriNetObject(pto, _petriNetTab, _model));
                        if(e.isControlDown())
                        {
                            applicationModel.enterFastMode(Constants.FAST_TRANSITION);
                            pn.dispatchEvent(e);
                        }
                        break;

                    case Constants.IMMTRANS:
                    case Constants.TIMEDTRANS:
                        boolean timed = (mode == Constants.TIMEDTRANS);
                        pto = newTransition(e.getPoint(), timed);
                        _petriNetTab.getHistoryManager().addNewEdit(new AddPetriNetObject(pto, _petriNetTab, _model));
                        if(e.isControlDown())
                        {
                            applicationModel.enterFastMode(Constants.FAST_PLACE);
                            pn.dispatchEvent(e);
                        }
                        break;

                    case Constants.ARC:
                    case Constants.INHIBARC:
                        if(_createArcView != null)
                            addPoint(_createArcView, e);
                        break;

                    case Constants.ANNOTATION:
                        p = adjustPoint(e.getPoint(), _petriNetTab.getZoom());
                        pn = new AnnotationNote(p.x, p.y);
                        _model.addPetriNetObject(pn);
                        _petriNetTab.addNewPetriNetObject(pn);
                        _petriNetTab.getHistoryManager().addNewEdit(new AddPetriNetObject(pn, _petriNetTab, _model));
                        ((AnnotationNote) pn).enableEditMode();
                        break;

                    case Constants.RATE:
                        try
                        {
                            String label = JOptionPane.showInputDialog("Rate Parameter Label:", "");
                            if(label == null)
                                break;

                            if(label.length() == 0)
                                throw new Exception("label Incorrect");
                            else if(_model.existsRateParameter(label))
                                throw new Exception("label Already Defined");

                            String value = JOptionPane.showInputDialog("Rate Parameter Value:", "");

                            p = adjustPoint(e.getPoint(), _petriNetTab.getZoom());

                            pn = new RateParameter(label,Double.parseDouble(value), p.x, p.y);
                            _model.addPetriNetObject(pn);
                            _petriNetTab.addNewPetriNetObject(pn);
                            _petriNetTab.getHistoryManager().addNewEdit(new AddPetriNetObject(pn, _petriNetTab, _model));
                        }
                        catch(java.lang.NumberFormatException nfe)
                        {
                            JOptionPane.showMessageDialog(null, "Enter a rate","Invalid entry", JOptionPane.ERROR_MESSAGE);
                        }
                        catch(Exception exc)
                        {
                            String message = exc.getMessage();
                            if(message == null)
                            {
                                message = "Unknown Error!";
                            }
                            JOptionPane.showMessageDialog(null, message,"Invalid entry", JOptionPane.ERROR_MESSAGE);
                        }
                        break;

                    case Constants.FAST_PLACE:
                        ConnectableView createPTO;
                        if(e.isMetaDown() || metaDown)
                        {
                            if(_createArcView != null)
                            {
                                addPoint(_createArcView, e);
                            }
                        }
                        else
                        {
                            if(_createArcView == null)
                                break;
                            _petriNetTab._wasNewPertiNetComponentCreated = true;
                            createPTO = newPlace(e.getPoint());
                            _petriNetTab.getHistoryManager().addNewEdit(new AddPetriNetObject(createPTO, _petriNetTab, _model));
                            pn.getMouseListeners()[0].mouseReleased(e);
                            if(e.isControlDown())
                            {
                                applicationModel.setMode(Constants.FAST_TRANSITION);
                                pn.getMouseListeners()[0].mousePressed(e);
                            }
                            else
                                applicationModel.resetMode();
                        }
                        break;

                    case Constants.FAST_TRANSITION:
                        if(e.isMetaDown() || metaDown)
                        {
                            if(_createArcView != null)
                                addPoint(_createArcView, e);
                        }
                        else
                        {
                            if(_createArcView == null)
                                break;
                            _petriNetTab._wasNewPertiNetComponentCreated = true;
                            timed = e.isAltDown();
                            if(applicationModel.getOldMode() == Constants.TIMEDTRANS)
                                timed = !timed;
                            createPTO = newTransition(e.getPoint(), timed);
                            _petriNetTab.getHistoryManager().addNewEdit(new AddPetriNetObject(createPTO, _petriNetTab, _model));
                            pn.getMouseListeners()[0].mouseReleased(e);
                            if(e.isControlDown())
                            {
                                applicationModel.setMode(Constants.FAST_PLACE);
                                pn.getMouseListeners()[0].mousePressed(e);
                            }
                            else
                                applicationModel.resetMode();
                        }
                        break;

                    case Constants.DRAG:
                        dragStart = new Point(start);
                        break;

                    default:
                        break;
                }
            }
            else
            {
                _petriNetTab.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                dragStart = new Point(start);
            }
            _petriNetTab.updatePreferredSize();
        }


        private void addPoint(final ArcView createArcView, final MouseEvent e)
        {
            int x = Grid.getModifiedX(e.getX());
            int y = Grid.getModifiedY(e.getY());
            boolean shiftDown = e.isShiftDown();
            createArcView.setEndPoint(x, y, shiftDown);
            createArcView.getArcPath().addPoint(x, y, shiftDown);
        }


        public void mouseReleased(MouseEvent e)
        {
            _petriNetTab.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        }


        public void mouseMoved(MouseEvent e)
        {
            if(_createArcView != null)
                _createArcView.setEndPoint(Grid.getModifiedX(e.getX()), Grid.getModifiedY(e.getY()), e.isShiftDown());
        }


        public void mouseDragged(MouseEvent e)
        {
            _petriNetTab.drag(dragStart, e.getPoint());
        }


        public void mouseWheelMoved(MouseWheelEvent e)
        {
            if(!e.isControlDown())
            {
            }
            else
            {
                if(e.getWheelRotation() > 0)
                    _petriNetTab.zoomIn();
                else
                    _petriNetTab.zoomOut();
            }
        }

    }
}


