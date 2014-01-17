package pipe.gui;

import pipe.controllers.PetriNetController;
import pipe.handlers.AnimationHandler;
import pipe.views.AbstractPetriNetViewComponent;
import pipe.views.PetriNetView;
import pipe.views.PetriNetViewComponent;
import pipe.views.PipeApplicationView;

import javax.swing.*;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class PetriNetTab extends JLayeredPane implements Observer, Printable {

    //public ArcView _createArcView;
    private final AnimationHandler animationHandler = new AnimationHandler();
//    private final SelectionManager selection;
    //    private final HistoryManager _historyManager;
    private final PipeApplicationView _pipeApplicationView;
    /**
     * Map of components in the tab with id -> component
     */
    private final Map<String, PetriNetViewComponent> petriNetComponents = new HashMap<String, PetriNetViewComponent>();
    private final Point viewPosition = new Point(0, 0);
    private final ZoomController zoomController;
    private final AnimationHistoryView animationHistoryView;
    public File _appFile;
    public boolean netChanged = false;
    public boolean _wasNewPertiNetComponentCreated = false;
    private boolean animationmode = false;
    private boolean metaDown = false;
    private boolean _zoomCalled = true;


    public PetriNetTab(ZoomController controller,
                       AnimationHistoryView animationHistoryView) {
        zoomController = controller;
        this.animationHistoryView = animationHistoryView;

        _pipeApplicationView = ApplicationSettings.getApplicationView();
        setLayout(null);
        setOpaque(true);
        setDoubleBuffered(true);
        setAutoscrolls(true);
        setBackground(Constants.ELEMENT_FILL_COLOUR);

        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        //        _historyManager = new HistoryManager(this, _petriNetView);
    }

    @Override
    public void update(Observable o, Object diffObj) {
        if ((diffObj instanceof AbstractPetriNetViewComponent) && (diffObj != null)) {
            AbstractPetriNetViewComponent<?> component = (AbstractPetriNetViewComponent<?>) diffObj;
            addNewPetriNetObject(component);
        }
    }

    /**
     * Add new component to the petrinet view
     *
     * @param component
     */
    public void addNewPetriNetObject(AbstractPetriNetViewComponent<?> component) {
        if (component.getMouseListeners().length == 0) {
            add(component);
            component.addToPetriNetTab(this);
            component.zoomUpdate(getZoom());
        }
    }

    public void add(AbstractPetriNetViewComponent<?> component) {
        setLayer(component, DEFAULT_LAYER + component.getLayerOffset());
        super.add(component);
        component.addedToGui();
        petriNetComponents.put(component.getId(), component);
    }

    public int getZoom() {
        return zoomController.getPercent();
    }

    @Override
    public int print(Graphics g, PageFormat pageFormat, int pageIndex)
            throws PrinterException {
        if (pageIndex > 0) {
            return Printable.NO_SUCH_PAGE;
        }
        Graphics2D g2D = (Graphics2D) g;
        g2D.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        g2D.scale(0.5, 0.5);
        print(g2D);
        return Printable.PAGE_EXISTS;
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        double scale = ZoomController.getScaleFactor(zoomController.getPercent());
        g2.scale(scale, scale);
        super.paintComponent(g);
        if (Grid.isEnabled()) {
            Grid.updateSize(this);
            Grid.drawGrid(g);
        }
    }

    public void changeAnimationMode(boolean status) {
        animationmode = status;
    }

    public void setCursorType(String type) {
        if (type.equals("arrow")) {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } else if (type.equals("crosshair")) {
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        } else if (type.equals("move")) {
            setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        }
    }

//    public SelectionManager getSelectionObject() {
//        return selection;
//    }

    public boolean isMetaDown() {
        return metaDown;
    }

    public void setMetaDown(boolean down) {
        metaDown = down;
    }

    public AnimationHandler getAnimationHandler() {
        return animationHandler;
    }

    public boolean isInAnimationMode() {
        return animationmode;
    }

    public boolean getNetChanged() {
        return netChanged;
    }

    public void setNetChanged(boolean _netChanged) {
        netChanged = _netChanged;
    }

    public void drag(Point dragStart, Point dragEnd) {
        if (dragStart == null) {
            return;
        }
        JViewport viewer = (JViewport) getParent();
        Point offScreen = viewer.getViewPosition();
        if (dragStart.x > dragEnd.x) {
            offScreen.translate(viewer.getWidth(), 0);
        }
        if (dragStart.y > dragEnd.y) {
            offScreen.translate(0, viewer.getHeight());
        }
        offScreen.translate(dragStart.x - dragEnd.x, dragStart.y - dragEnd.y);
        Rectangle r = new Rectangle(offScreen.x, offScreen.y, 1, 1);
        scrollRectToVisible(r);
    }

    public void zoomIn() {
        int zoom = zoomController.getPercent();
        if (zoomController.zoomIn()) {
            zoomTo(midpoint(zoom));
        }
    }

    private Point midpoint(int zoom) {
        JViewport viewport = (JViewport) getParent();
        double midpointX = ZoomController.getUnzoomedValue(
                viewport.getViewPosition().x + (viewport.getWidth() * 0.5), zoom);
        double midpointY = ZoomController.getUnzoomedValue(
                viewport.getViewPosition().y + (viewport.getHeight() * 0.5), zoom);
        return (new java.awt.Point((int) midpointX, (int) midpointY));
    }

    public void zoomTo(Point point) {

        int zoom = zoomController.getPercent();
        JViewport viewport = (JViewport) getParent();
        double newZoomedX = ZoomController.getZoomedValue(point.x, zoom);
        double newZoomedY = ZoomController.getZoomedValue(point.y, zoom);

        int newViewX = (int) (newZoomedX - (viewport.getWidth() * 0.5));
        if (newViewX < 0) {
            newViewX = 0;
        }

        int newViewY = (int) (newZoomedY - (viewport.getHeight() * 0.5));
        if (newViewY < 0) {
            newViewY = 0;
        }

        viewPosition.setLocation(newViewX, newViewY);
        viewport.setViewPosition(viewPosition);
        zoom();

        updatePreferredSize();
    }

    public void updatePreferredSize() {
        Component[] components = getComponents();
        Dimension d = new Dimension(0, 0);
        for (Component component : components) {
            if (component.getClass() == SelectionManager.class) {
                continue;
            }
            Rectangle r = component.getBounds();
            int x = r.x + r.width + 20;
            int y = r.y + r.height + 20;
            if (x > d.width) {
                d.width = x;
            }
            if (y > d.height) {
                d.height = y;
            }
        }
        setPreferredSize(d);
        Container parent = getParent();
        if (parent != null) {
            parent.validate();
        }
    }

    private void zoom() {
        Component[] children = getComponents();

        for (Component child : children) {
            if (child instanceof Zoomable) {
                ((Zoomable) child).zoomUpdate(zoomController.getPercent());
            }
        }
        _zoomCalled = true;
//        selection.setZoom(zoomController.getPercent());
    }

    public void zoomOut() {
        int zoom = zoomController.getPercent();
        if (zoomController.zoomOut()) {
            zoomTo(midpoint(zoom));
        }
    }

    public AnimationHistoryView getAnimationView() {
        return animationHistoryView;
    }

    public void deletePetriNetComponent(String id) {
        PetriNetViewComponent component = petriNetComponents.get(id);
        if (component != null) {
            component.delete();
            remove((Component) component);
        }
        validate();
        repaint();
    }
}


