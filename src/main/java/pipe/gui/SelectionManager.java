/*
 * Created on 08-Feb-2004
 */
package pipe.gui;

import pipe.controllers.PetriNetController;
import pipe.views.ArcView;
import pipe.views.viewComponents.ArcPath;
import pipe.views.ConnectableView;
import pipe.views.PetriNetViewComponent;
import pipe.views.PlaceView;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Peter Kyme, Michael Camacho
 * Class to handle selection rectangle functionality
 */
public class SelectionManager 
        extends javax.swing.JComponent
        implements java.awt.event.MouseListener, 
                   java.awt.event.MouseWheelListener,
                   java.awt.event.MouseMotionListener {

   private Point startPoint;
   private final Rectangle selectionRectangle = new Rectangle(-1,-1);
   private boolean isSelecting;
   private static final Color selectionColor = new Color(0, 0, 255, 24);
   private static final Color selectionColorOutline = new Color(0, 0, 100);
   private final PetriNetTab _view;
   private boolean enabled = true;
   private final PetriNetController petriNetController;

    /**
     * Zoom as a percentage e.g. 100%
     */
   private int zoom = 100;

   public SelectionManager(PetriNetTab _view, PetriNetController controller) {
      addMouseListener(this);
      addMouseMotionListener(this);
      addMouseWheelListener(this);
      this._view = _view;
       this.petriNetController = controller;
   }
   
   public void setZoom(int zoom) {
       this.zoom = zoom;
   }


   public void updateBounds() {
      if (enabled) {
         setBounds(0,0, _view.getWidth(), _view.getHeight());
      }
   }

   
   public void enableSelection() {
      if (!enabled) {
         _view.add(this);
         enabled = true;
         updateBounds();
      }
   }

   
   public void disableSelection() {
       if (enabled) {
         _view.remove(this);
         enabled = false;
      }
   }

   
   private void processSelection(MouseEvent e) {
      if (!e.isShiftDown()){
         clearSelection();
      }
      
      // Get all the objects in the current window
      ArrayList <PetriNetViewComponent> pns = _view.getPNObjects();
      for (PetriNetViewComponent pn : pns) {
         pn.select(selectionRectangle);
      }

       Rectangle unzoomedRectangle = calculateUnzoomedSelection();
       System.out.println("UNZOOMED " + unzoomedRectangle);
       petriNetController.select(unzoomedRectangle);
   }


    /**
     * uses zoom and the ZoomController to calculate what the
     * unzoomed selection rectangle would be
     */
    private Rectangle calculateUnzoomedSelection() {
        int x = ZoomController.getUnzoomedValue((int) selectionRectangle.getX(), zoom);
        int y = ZoomController.getUnzoomedValue((int) selectionRectangle.getY(), zoom);
        int height = ZoomController.getUnzoomedValue((int) selectionRectangle.getHeight(), zoom);
        int width = ZoomController.getUnzoomedValue((int) selectionRectangle.getWidth(), zoom);
        return new Rectangle(x, y, width, height);
    }

   
   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D) g;
      g2d.setPaint(selectionColor);
      g2d.fill(selectionRectangle);
      g2d.setPaint(selectionColorOutline);
      g2d.draw(selectionRectangle);
   }

   
   public void deleteSelection() {
      // Get all the objects in the current window
//      ArrayList <PetriNetViewComponent> pns = _view.getPNObjects();
//       for(PetriNetViewComponent pn : pns)
//       {
//           if(pn.isSelected())
//           {
//               pn.delete();
//           }
//       }
//      _view.updatePreferredSize();
       petriNetController.deleteSelection();
   }

   
   public void clearSelection() {
      // Get all the objects in the current window
      ArrayList <PetriNetViewComponent> pns = _view.getPNObjects();
      for (PetriNetViewComponent pn : pns) {
         if (pn.isSelectable()) {
            pn.deselect();
         }
      }
       petriNetController.deselectAll();
   }

   
   public void translateSelection(int transX, int transY) {

      if (transX == 0 && transY == 0) {
         return;
      }

      // First see if translation will put anything at a negative location
      Point topleft = null;

      // Get all the objects in the current window
      List<PetriNetViewComponent> pns = _view.getPNObjects();
      for (PetriNetViewComponent pn : pns) {
         if (pn.isSelected()){
            Point point = pn.getLocation();
            if (topleft == null) {
               topleft = point;
            } else {
               if (point.x < topleft.x) {
                  topleft.x = point.x;
               }
               if (point.y < topleft.y) {
                  topleft.y = point.y;
               }
            }
         }
      }
      
      if (topleft != null) {
         topleft.translate(transX, transY);
         if (topleft.x < 0){
            transX -= topleft.x;
         }
         if (topleft.y < 0){
            transY -= topleft.y;
         }
         if (transX == 0 && transY == 0){
            return;
         }
      }

       petriNetController.translateSelected(new Point2D.Double(transX, transY));

//      for (PetriNetViewComponent pn : pns) {
//         if (pn.isSelected()) {
//            pn.translate(transX, transY);
//         }
//      }
      _view.updatePreferredSize();
   }

   
   public ArrayList getSelection() {
      ArrayList selection = new ArrayList();

      // Get all the objects in the current window
      ArrayList <PetriNetViewComponent> pns = _view.getPNObjects();
      for (PetriNetViewComponent pn : pns) {
         if (pn.isSelected()){
//        	 if(pn instanceof ArcView)
//        		 System.out.println("arc found");
        	 selection.add(pn);
         }
      }
      return selection;
   }

   
   /* (non-Javadoc)
    * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
    */
   public void mousePressed(MouseEvent e) {
      if (e.getButton() == MouseEvent.BUTTON1 && !(e.isControlDown())) {
         isSelecting = true;
         _view.setLayer(this, Constants.SELECTION_LAYER_OFFSET);
         startPoint = e.getPoint();
         selectionRectangle.setRect(startPoint.getX(), startPoint.getY(), 0, 0);
         // Select anything that intersects with the rectangle.
         processSelection(e);
         repaint();
      } else {
         startPoint = e.getPoint();
      }
   }

   
   /* (non-Javadoc)
    * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
    */
   public void mouseReleased(MouseEvent e) {
      if (isSelecting) {
         // Select anything that intersects with the rectangle.
         processSelection(e);
         isSelecting = false;
         _view.setLayer(this, Constants.LOWEST_LAYER_OFFSET);
         selectionRectangle.setRect(-1, -1, 0, 0);
         repaint();
      }
   }

   
   /* (non-Javadoc)
    * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
    */
   public void mouseDragged(MouseEvent e) {
      if (isSelecting) {
         selectionRectangle.setSize(
                  (int)Math.abs(e.getX() - startPoint.getX()),
                  (int)Math.abs(e.getY() - startPoint.getY()));
         selectionRectangle.setLocation(
                  (int)Math.min(startPoint.getX(), e.getX()),
                  (int)Math.min(startPoint.getY(), e.getY()));
         // Select anything that intersects with the rectangle.
         processSelection(e);
         repaint();
      } else {   
         _view.drag(startPoint, e.getPoint());
      }
   }


   public void mouseWheelMoved(MouseWheelEvent e) {
      if (e.isControlDown()) {
         if (e.getWheelRotation()> 0) {
            _view.zoomIn();
         } else {
            _view.zoomOut();
         }
      }
   }   
   
   public void mouseClicked(MouseEvent e) {
       // Not needed
   }

   
   public void mouseEntered(MouseEvent e) {
       // Not needed
   }

   
   public void mouseExited(MouseEvent e) {
       // Not needed
   }

   
   /* (non-Javadoc)
    * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
    */
   public void mouseMoved(MouseEvent e) {
       // Not needed
   }   
   
   
   public int getSelectionCount() {
      Component netObj[] = _view.getComponents();
      int selectionCount = 0;
      // Get all the objects in the current window
       for(Component aNetObj : netObj)
       {
           // Handle Arcs and Arc Points
           if((aNetObj instanceof ArcView) && ((PetriNetViewComponent) aNetObj).isSelectable())
           {
               ArcView thisArcView = (ArcView) aNetObj;
               ArcPath thisArcPath = thisArcView.getArcPath();
               for(int j = 1; j < thisArcPath.getEndIndex(); j++)
               {
                   if(thisArcPath.isPointSelected(j))
                   {
                       selectionCount++;
                   }
               }
           }

           // Handle PlaceTransition Objects
           if((aNetObj instanceof ConnectableView) &&
                   ((PetriNetViewComponent) aNetObj).isSelectable())
           {
               if(((ConnectableView) aNetObj).isSelected())
               {
                   selectionCount++;
               }
           }
       }
      return selectionCount;
   }

}
