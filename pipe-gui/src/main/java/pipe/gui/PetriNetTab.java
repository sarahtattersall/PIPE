package pipe.gui;

import pipe.handlers.AnimationHandler;
import pipe.models.component.Connectable;
import pipe.models.component.PetriNetComponent;
import pipe.models.component.annotation.Annotation;
import pipe.models.component.arc.Arc;
import pipe.models.component.place.Place;
import pipe.models.component.place.PlaceVisitor;
import pipe.models.component.rate.Rate;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.models.component.transition.TransitionVisitor;
import pipe.views.AbstractPetriNetViewComponent;
import pipe.views.PetriNetViewComponent;
import pipe.visitor.AllComponentVisitor;
import pipe.visitor.component.PetriNetComponentVisitor;

import javax.swing.*;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class PetriNetTab extends JLayeredPane implements Observer, Printable {

    private final AnimationHandler animationHandler = new AnimationHandler();

    /**
     * Map of components in the tab with id -> component
     */
    private final Map<String, PetriNetViewComponent> petriNetComponents = new HashMap<String, PetriNetViewComponent>();

    public ZoomController getZoomController() {
        return zoomController;
    }

    private final ZoomController zoomController;

    private final AnimationHistoryView animationHistoryView;

    public File _appFile;

    private boolean netChanged = false;

    private boolean animationmode = false;

    private boolean metaDown = false;

    /**
     * Grid displayed on petri net tab
     */
    private final Grid grid = new Grid();

    public PetriNetTab(ZoomController controller, AnimationHistoryView animationHistoryView) {
        zoomController = controller;
        addZoomListener(zoomController);
        this.animationHistoryView = animationHistoryView;

        setLayout(null);
        setOpaque(true);
        setDoubleBuffered(true);
        setAutoscrolls(true);
        setBackground(Constants.ELEMENT_FILL_COLOUR);

        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    private void addZoomListener(ZoomController zoomController) {
        zoomController.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                repaint();
            }
        });
    }

    @Override
    public void update(Observable o, Object diffObj) {
        if ((diffObj instanceof AbstractPetriNetViewComponent) && (diffObj != null)) {
            AbstractPetriNetViewComponent<?> component = (AbstractPetriNetViewComponent<?>) diffObj;
            addNewPetriNetComponent(component);
        }
    }

    /**
     * @param component to add to petri net view
     */
    public void addNewPetriNetComponent(AbstractPetriNetViewComponent<?> component) {
        if (component.getMouseListeners().length == 0) {
            add(component);
            component.addToPetriNetTab(this);
        }
    }

    public void add(AbstractPetriNetViewComponent<?> component) {
        registerForLocationChange(component.getModel());

        setLayer(component, DEFAULT_LAYER + component.getLayerOffset());
        super.add(component);
        component.addedToGui();
        petriNetComponents.put(component.getId(), component);
        updatePreferredSize();
//        repaint();
    }

    private void registerForLocationChange(PetriNetComponent component) {

        PetriNetComponentVisitor changeListener = new ChangeListener();
        component.accept(changeListener);
    }

    public int getZoom() {
        return zoomController.getPercent();
    }

    @Override
    public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {
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
        super.paintComponent(g);
        if (grid.isEnabled()) {
            grid.updateSize(this);
            grid.drawGrid(g);
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

    public Grid getGrid() {
        return grid;
    }

    /**
     * Listen to changes in x/y
     */
    private class ChangeListener implements PlaceVisitor, TransitionVisitor {
        PropertyChangeListener updateListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String name = evt.getPropertyName();
                if (name.equals(Connectable.X_CHANGE_MESSAGE)) {
                    double x = (double) evt.getNewValue();
                    if (x > getWidth()) {
                        updatePreferredSize();
                    }

                }
                if (name.equals(Connectable.Y_CHANGE_MESSAGE)) {
                    double y = (double) evt.getNewValue();
                    if (y > getHeight()) {
                        updatePreferredSize();
                    }

                }
            }
        };

        @Override
        public void visit(Place place) {
              place.addPropertyChangeListener(updateListener);
        }

        @Override
        public void visit(Transition transition) {
            transition.addPropertyChangeListener(updateListener);
        }
    }
}


