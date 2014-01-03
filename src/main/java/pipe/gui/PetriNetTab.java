package pipe.gui;

import pipe.controllers.PetriNetController;
import pipe.handlers.AnimationHandler;
import pipe.views.PetriNetView;
import pipe.views.PetriNetViewComponent;
import pipe.views.PipeApplicationView;

import javax.swing.*;
import java.awt.*;
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
    //public ArcView _createArcView;
    private final AnimationHandler animationHandler = new AnimationHandler();
    private boolean metaDown = false;
    private final SelectionManager selection;
//    private final HistoryManager _historyManager;
    private final PipeApplicationView _pipeApplicationView;
    private final ArrayList<PetriNetViewComponent> petriNetComponents = new ArrayList<PetriNetViewComponent>();
    private final ZoomController zoomControl;
    public boolean _wasNewPertiNetComponentCreated = false;
    private boolean _zoomCalled = true;
    private final Point viewPosition = new Point(0, 0);
    private final PetriNetController petriNetController;
    private final AnimationHistoryView animationHistoryView;


    public PetriNetTab(PetriNetView petriNetView, PetriNetController controller, AnimationHistoryView animationHistoryView)
    {
        _petriNetView = petriNetView;
        petriNetController = controller;
        this.animationHistoryView = animationHistoryView;

        _pipeApplicationView = ApplicationSettings.getApplicationView();
        setLayout(null);
        setOpaque(true);
        setDoubleBuffered(true);
        setAutoscrolls(true);
        setBackground(Constants.ELEMENT_FILL_COLOUR);
        zoomControl = new ZoomController(100);

        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        selection = new SelectionManager(this, controller);
//        _historyManager = new HistoryManager(this, _petriNetView);
    }

    public PetriNetController getPetriNetController() {
        return petriNetController;
    }


    public void addNewPetriNetObject(PetriNetViewComponent newPetriNetViewComponent)
    {
        if(newPetriNetViewComponent != null)
        {
            if(newPetriNetViewComponent.getMouseListeners().length == 0)
            {
                newPetriNetViewComponent.addToPetriNetTab(this);
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
            PetriNetViewComponent component = (PetriNetViewComponent) diffObj;
            addNewPetriNetObject(component);
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

//    public HistoryManager getHistoryManager()
//    {
//        return _historyManager;
//    }

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
        selection.setZoom(zoomControl.getPercent());
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

    public boolean isMetaDown() {
        return metaDown;
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

    public AnimationHistoryView getAnimationView() {
        return animationHistoryView;
    }
}


