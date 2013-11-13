package pipe.handlers;

import pipe.controllers.PetriNetController;
import pipe.gui.ApplicationSettings;
import pipe.gui.Constants;
import pipe.gui.Grid;
import pipe.gui.PetriNetTab;
import pipe.actions.*;
import pipe.historyActions.HistoryManager;
import pipe.models.Arc;
import pipe.views.ArcView;
import pipe.views.InhibitorArcView;
import pipe.views.NormalArcView;
import pipe.views.PlaceView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;


/**
 * Class used to implement methods corresponding to mouse events on arcs.
 */
public class ArcHandler 
        extends PetriNetObjectHandler<Arc>
{

   
   public ArcHandler(Container contentpane, Arc obj, PetriNetController controller) {
      super(contentpane, obj, controller);
      enablePopup = true;
   }

   
   /** 
    * Creates the popup menu that the user will see when they right click on a 
    * component 
    */
   public JPopupMenu getPopup(MouseEvent e) {
      int popupIndex = 0;
      JMenuItem menuItem;
      JPopupMenu popup = super.getPopup(e);
      
 
//      if (component instanceof InhibitorArcView) {
//          menuItem = new JMenuItem("Edit Weight");
//          menuItem.addActionListener(new ActionListener(){
//             public void actionPerformed(ActionEvent e) {
//                ((ArcView) component).showEditor();
//             }
//          });
//          popup.insert(menuItem, popupIndex++);
//
//         menuItem = new JMenuItem(new SplitArcAction((ArcView) component,
//                                                      e.getPoint()));
//         menuItem.setText("Split Arc Segment");
//         popup.insert(menuItem, popupIndex++);
//
//         popup.insert(new JPopupMenu.Separator(), popupIndex++);
//      } else if (component instanceof NormalArcView) {
//         if (((NormalArcView) component).isJoined()){
//            NormalArcView PTArc;
//            NormalArcView TPArc;
//
//            if (((NormalArcView) component).getSource() instanceof PlaceView){
//               PTArc = (NormalArcView) component;
//               TPArc = ((NormalArcView) component).getInverse();
//            } else {
//               PTArc = ((NormalArcView) component).getInverse();
//               TPArc = (NormalArcView) component;
//            }
//
////            if (!PTArc.isTagged()) { //pendentnou
//            menuItem = new JMenuItem("Edit Weight");
//            menuItem.addActionListener(new ActionListener(){
//               public void actionPerformed(ActionEvent e) {
//                  ((ArcView) component).showEditor();
//               }
//            });
//            popup.insert(menuItem, popupIndex++);
////               menuItem = new JMenuItem(
////                       new EditTaggedAction(contentPane, PTArc));
////               menuItem.setText("Make Tagged (PT Arc)");
////               popup.insert(menuItem, popupIndex++);
////            } else {
////               menuItem = new JMenuItem(
////                       new EditTaggedAction(contentPane, PTArc));
////               menuItem.setText("Make Non-Tagged (PT Arc)");
////               popup.insert(menuItem, popupIndex++);
////            }
//            popup.insert(new JPopupMenu.Separator(), popupIndex++);
//
////            if (!TPArc.isTagged()) {
////               menuItem = new JMenuItem(
////                       new EditTaggedAction(contentPane, TPArc));
////               menuItem.setText("Make Tagged (TP Arc)");
////               popup.insert(menuItem, popupIndex++);
////            } else {
////               menuItem = new JMenuItem(
////                       new EditTaggedAction(contentPane, TPArc));
////               menuItem.setText("Make Non-Tagged (TP Arc)");
////               popup.insert(menuItem, popupIndex++);
////            }
//
//            popup.insert(new JPopupMenu.Separator(), popupIndex++);
//
//            menuItem = new JMenuItem(new InsertPointAction((ArcView) component,
//                                                         e.getPoint()));
//            menuItem.setText("Insert Point");
//            /*
//            menuItem = new JMenuItem(new SplitArcAction((Arc)component,
//                                                         e.getPoint()));
//            menuItem.setText("Split Arc Segment");
//             */
//            popup.insert(menuItem, popupIndex++);
//
//            menuItem = new JMenuItem(
//                    new SplitArcsAction((NormalArcView) component, true));
//            menuItem.setText("Split Arcs (PT / TP)");
//            popup.insert(menuItem, popupIndex++);
//
//            popup.insert(new JPopupMenu.Separator(), popupIndex++);
//
//            menuItem = new JMenuItem(new DeleteInverseArcAction(PTArc));
//            menuItem.setText("Delete (PT Arc)");
//            popup.insert(menuItem, popupIndex++);
//
//            menuItem = new JMenuItem(new DeleteInverseArcAction(TPArc));
//            menuItem.setText("Delete (TP Arc)");
//            popup.insert(menuItem, popupIndex++);
//            /*
//            menuItem = new JMenuItem(new DeleteBothAction((NormalArc)component));
//            menuItem.setText("Delete Both");
//            popup.insert(menuItem, 8);
//            */
//         } else {
////            if(!((NormalArc)component).isTagged()) {
//             menuItem = new JMenuItem("Edit Weight");
//             menuItem.addActionListener(new ActionListener(){
//                public void actionPerformed(ActionEvent e) {
//                   ((ArcView) component).showEditor();
//                }
//             });
//             popup.insert(menuItem, popupIndex++);
////            }
//
////            menuItem = new JMenuItem(
////                    new EditTaggedAction(contentPane,(NormalArc)component));
////            if (((NormalArc)component).isTagged()) {
////               menuItem.setText("Make Non-Tagged");
////            } else {
////               menuItem.setText("Make Tagged");
////            }
////            popup.insert(menuItem, popupIndex++);
//
//            //menuItem = new JMenuItem(new SplitArcAction((Arc)component,
//            //                                             e.getPoint()));
//            //menuItem.setText("Split Arc Segment");
//
//
//            menuItem = new JMenuItem(new SplitArcAction((ArcView) component,
//                                                         e.getPoint()));
//            menuItem.setText("Insert Point");
//            popup.insert(menuItem, popupIndex++);
//
//            if (((NormalArcView) component).hasInverse()){
//               menuItem = new JMenuItem(
//                        new SplitArcsAction((NormalArcView) component, false));
//
//               menuItem.setText("Join Arcs (PT / TP)");
//               popup.insert(menuItem, popupIndex++);
//            }
//            popup.insert(new JPopupMenu.Separator(), popupIndex);
//         }
//      }
      return popup;
   }

   
   public void mousePressed(MouseEvent e) {
      super.mousePressed(e);
//       if (!ApplicationSettings.getApplicationModel().isEditionAllowed()){
//         return;
//      }
//      if (e.getClickCount() == 2){
//         ArcView arcView = (ArcView) component;
//         if (e.isControlDown()){
//             ApplicationSettings.getApplicationView().getCurrentTab().getHistoryManager().addNewEdit(
//                    arcView.getArcPath().insertPointAt(
//                            new Point2D.Float(arcView.getX() + e.getX(),
//                            arcView.getY() + e.getY()), e.isAltDown()));
//         } else {
//            arcView.getSource().select();
//            arcView.getTarget().select();
//            justSelected = true;
//         }
//      }
   }

   
   public void mouseDragged(MouseEvent e) {
//       switch (ApplicationSettings.getApplicationModel().getMode()) {
//         case Constants.SELECT:
//            if (!isDragging){
//               break;
//            }
//            ArcView currentObject = (ArcView) component;
//            Point oldLocation = currentObject.getLocation();
//            // Calculate translation in mouse
//            int transX = Grid.getModifiedX(e.getX() - dragInit.x);
//            int transY = Grid.getModifiedY(e.getY() - dragInit.y);
//            ((PetriNetTab)contentPane).getSelectionObject().translateSelection(
//                     transX, transY);
//            dragInit.translate(
//                     -(currentObject.getLocation().x - oldLocation.x - transX),
//                     -(currentObject.getLocation().y - oldLocation.y - transY));
//      }
   }

   // Alex Charalambous: No longer does anything since you can't simply increment
   // the weight of the arc because multiple weights for multiple colours exist
   public void mouseWheelMoved (MouseWheelEvent e) {
      
   }
   

   
   class SplitArcsAction extends AbstractAction {

      final NormalArcView _arcView;
      final boolean joined;
      
      
      public SplitArcsAction(NormalArcView _arc, boolean _joined){
         _arcView = _arc;
         joined = _joined;
      }
      
      public void actionPerformed(ActionEvent e) {
         if (joined) {
             ApplicationSettings.getApplicationView().getCurrentTab().getHistoryManager().addNewEdit(
                    _arcView.split());
         } else {
             ApplicationSettings.getApplicationView().getCurrentTab().getHistoryManager().addNewEdit(
                    _arcView.join());
         }
      }
      
   }
      

   
   class DeleteInverseArcAction extends AbstractAction {
      
      final NormalArcView _arcView;
       final NormalArcView _inverse;
      final boolean switchArcs;
      
      
      public DeleteInverseArcAction(NormalArcView _arc){
         _arcView = _arc;
         _inverse = _arcView.getInverse();
         switchArcs = _arcView.inView();
      }
      
      
      public void actionPerformed(ActionEvent e) {
          HistoryManager historyManager = ApplicationSettings.getApplicationView().getCurrentTab().getHistoryManager();
         
         if (switchArcs) {
            historyManager.addNewEdit(_arcView.split());
         } else {
            historyManager.addNewEdit(_inverse.split());
         }
         historyManager.deleteSelection(_arcView);

         _arcView.delete();
      }
   }   
   
}
