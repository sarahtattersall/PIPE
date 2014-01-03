/*
 * CopyPasteManager.java
 */
package pipe.gui;

import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.historyActions.HistoryItem;
import pipe.historyActions.HistoryManager;
import pipe.views.*;
import pipe.views.viewComponents.RateParameter;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * Class to handle paste & paste functionality
 *
 * @author Pere Bonet
 */
public class CopyPasteManager
        extends javax.swing.JComponent
        implements pipe.gui.Zoomable, java.awt.event.MouseListener,
        java.awt.event.MouseMotionListener,
        java.awt.event.KeyListener {

    private static final Color PASTE_COLOR = new Color(155, 155, 155, 100);
    private static final Color PASTE_COLOR_OUTLINE = new Color(155, 0, 0, 0);

    private final Rectangle pasteRectangle = new Rectangle(-1, -1);

    // pasteInProgres is true when pasteRectangle is visible (user is doing a
    // paste but still hasn't chosen the position where elements will be pasted).
    private boolean pasteInProgress = false;

    private final ArrayList<ArrayList> objectsToPaste = new ArrayList();

    private final Point origin = new Point();

    private PetriNetTab _sourceView;

    private int zoom;


    public CopyPasteManager() {
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
    }


    private void updateBounds() {
        if (pasteInProgress) {
            PipeApplicationView applicationView = ApplicationSettings.getApplicationView();
            setBounds(0, 0,
                    applicationView.getCurrentTab().getWidth(),
                    ApplicationSettings.getApplicationView().getCurrentTab().getHeight());
        }
    }


    public void doCopy(ArrayList<PetriNetViewComponent> toCopy, PetriNetTab _sourceView) {

        this._sourceView = _sourceView;
        zoom = this._sourceView.getZoom();

        int bottom = 0;
        int right = 0;
        int top = Integer.MAX_VALUE;
        int left = Integer.MAX_VALUE;

        ArrayList<ArcView> arcsToPaste = new ArrayList();
        ArrayList ptaToPaste = new ArrayList();

        for (PetriNetViewComponent pn : toCopy) {
            if (pn.isCopyPasteable()) {
                if (pn instanceof ArcView) {
                    arcsToPaste.add((ArcView) pn.copy());
                    if (pn instanceof NormalArcView) {
                        if (((NormalArcView) pn).hasInvisibleInverse()) {
                            arcsToPaste.add(((NormalArcView) pn).getInverse().copy());
                        }
                    }
                } else {
                    if (pn.getX() < left) {
                        left = pn.getX();
                    }
                    if (pn.getX() + pn.getWidth() > right) {
                        right = pn.getX() + pn.getWidth();
                    }
                    if (pn.getY() < top) {
                        top = pn.getY();
                    }
                    if (pn.getY() + pn.getHeight() > bottom) {
                        bottom = pn.getY() + pn.getHeight();
                    }
                    ptaToPaste.add(pn.copy());
                }
            }
        }

        if (!ptaToPaste.isEmpty()) {
            objectsToPaste.clear();
            pasteRectangle.setRect(left, top, right - left, bottom - top);
            origin.setLocation(ZoomController.getUnzoomedValue(left, zoom),
                    ZoomController.getUnzoomedValue(top, zoom));
            objectsToPaste.add(ptaToPaste);
            objectsToPaste.add(arcsToPaste);
        }
    }


    public void showPasteRectangle(PetriNetTab view) {
        if (!pasteInProgress) {
            view.add(this);
            requestFocusInWindow();
            try {
                if (zoom != view.getZoom()) {
                    updateSize(pasteRectangle, zoom, view.getZoom());
                    zoom = view.getZoom();
                }
                pasteRectangle.setLocation(view.getMousePosition());
            } catch (java.lang.NullPointerException npe) {
                System.out.println(npe);
            }
            view.setLayer(this, Constants.SELECTION_LAYER_OFFSET);
            repaint();
            pasteInProgress = true;
            updateBounds();
        }
    }


    private void doPaste(PetriNetTab view) {
        ArrayList<HistoryItem> undo = new ArrayList();

        pasteInProgress = false;
        view.remove(this);

        double despX = Grid.getModifiedX(
                ZoomController.getUnzoomedValue(pasteRectangle.getX(), zoom) - origin.getX());
        double despY = Grid.getModifiedY(
                ZoomController.getUnzoomedValue(pasteRectangle.getY(), zoom) - origin.getY());

        if (objectsToPaste.isEmpty()) {
            return;
        }

        //TODO: DONT USE STATIC METHOD, PASS IT IN
        PipeApplicationController controller = ApplicationSettings.getApplicationController();
        PetriNetController petriNetController = controller.getActivePetriNetController();
        HistoryManager historyManager = petriNetController.getHistoryManager();
        PetriNetView model = ApplicationSettings.getApplicationView().getCurrentPetriNetView();

        //First, we deal with Places, Transitions & Annotations
        ArrayList<PetriNetViewComponent> ptaToPaste = objectsToPaste.get(0);
        for (PetriNetViewComponent aPtaToPaste : ptaToPaste) {
            PetriNetViewComponent pn = aPtaToPaste.paste(despX, despY, _sourceView != view, model);

            if (pn != null) {
                if ((pn instanceof TransitionView) && (_sourceView != view)) {
                    RateParameter rateParameter =
                            ((TransitionView) pn).getRateParameter();
                    if (rateParameter != null) {
                        ((TransitionView) pn).clearRateParameter();
                    }
                }
                model.addPetriNetObject(pn);
                view.addNewPetriNetObject(pn);
                view.updatePreferredSize();
                //TODO: ADD THIS BACK IN
//                pn.select();
//	               undo.add(new AddPetriNetObject(pn, view, model));
            }
        }

        //Now, we deal with Arcs
        ArrayList<ArcView> arcsToPaste = objectsToPaste.get(1);

        for (ArcView anArcsToPaste : arcsToPaste) {
            if (!(anArcsToPaste instanceof ArcView)) {
                break;
            }
            ArcView arcView = (ArcView) anArcsToPaste.paste(
                    despX, despY, _sourceView != view, model);

            if (arcView != null) {
                model.addPetriNetObject(arcView);
                view.addNewPetriNetObject(arcView);
                view.updatePreferredSize();
                arcView.updateArcPosition();
                //TODO: ADD THIS BACK IN
//                arcView.select();
//	               undo.add(new AddPetriNetObject(arcView, view, model));
            }
        }

        // Now, we find inverse arcs
        ptaToPaste = objectsToPaste.get(0);
        for (PetriNetViewComponent pno : ptaToPaste) {
            if ((pno instanceof ConnectableView)) {
                ConnectableView pt =
                        ((ConnectableView) pno).getOriginal().getLastCopy();

                Iterator<ArcView> pnoConnectedFromIterator =
                        pt.getConnectFromIterator();
                while (pnoConnectedFromIterator.hasNext()) {
                    ArcView arc1;
                    try {
                        arc1 = pnoConnectedFromIterator.next();
                    } catch (java.util.ConcurrentModificationException cme) {
                        System.out.println("cme:" + cme);
                        break;
                    }
                    Iterator<ArcView> pnoConnectedToIterator =
                            pt.getConnectToIterator();
                    while (pnoConnectedToIterator.hasNext()) {
                        ArcView arc2 = pnoConnectedToIterator.next();

                        if (arc2 instanceof NormalArcView) {
                            if (((NormalArcView) arc2).hasInverse()) {
                                break;
                            }
                        }
                        if (arc1.getSource().equals(arc2.getTarget()) &&
                                arc1.getTarget().equals(arc2.getSource())) {
                            if (((NormalArcView) arc1).isJoined()) {
                                ((NormalArcView) arc1).setInverse((NormalArcView) arc2, true);

                            } else if (((NormalArcView) arc2).isJoined()) {
                                ((NormalArcView) arc2).setInverse((NormalArcView) arc1, true);

                            } else {
                                ((NormalArcView) arc1).setInverse((NormalArcView) arc2, false);
                            }
                        }
                    }
                }
            }
        }

        // Clear copies
        ptaToPaste = objectsToPaste.get(0);
        for (PetriNetViewComponent pno : ptaToPaste) {
            if (pno instanceof ConnectableView) {
                if (((ConnectableView) pno).getOriginal() != null) {
                    //the Place/Transition is a copy of another Object, so we have to
                    // nullify the reference to the original Object
                    ((ConnectableView) pno).getOriginal().resetLastCopy();
                } else {
                    ((ConnectableView) pno).resetLastCopy();
                }
            }
        }

        // Add undo edits
        historyManager.newEdit(); // new "transaction""

        Iterator<HistoryItem> undoIterator = undo.iterator();
        while (undoIterator.hasNext()) {
            historyManager.addEdit(undoIterator.next());
        }

        view.zoom(); //
    }


    public void cancelPaste() {
        cancelPaste(ApplicationSettings.getApplicationView().getCurrentTab());
    }


    void cancelPaste(PetriNetTab view) {
        pasteInProgress = false;
        view.repaint();
        view.remove(this);
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


    private void updateSize(Rectangle pasteRectangle, int zoom, int newZoom) {
        int realWidth = ZoomController.getUnzoomedValue(pasteRectangle.width, zoom);
        int realHeight = ZoomController.getUnzoomedValue(pasteRectangle.height, zoom);

        pasteRectangle.setSize((int) (realWidth * ZoomController.getScaleFactor(newZoom)),
                (int) (realHeight * ZoomController.getScaleFactor(newZoom)));
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
        PetriNetTab view = ApplicationSettings.getApplicationView().getCurrentTab();

        view.updatePreferredSize();
        view.setLayer(this, Constants.LOWEST_LAYER_OFFSET);
        repaint();
        //now, we have the position of the pasted objects so we can show them.
        doPaste(view);
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

}
