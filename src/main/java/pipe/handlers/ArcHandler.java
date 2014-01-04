package pipe.handlers;

import pipe.actions.SplitArcAction;
import pipe.controllers.PetriNetController;
import pipe.historyActions.HistoryManager;
import pipe.models.component.Arc;
import pipe.views.ArcView;
import pipe.views.NormalArcView;
import pipe.views.PlaceView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * Class used to implement methods corresponding to mouse events on arcs.
 */
public class ArcHandler 
        extends PetriNetObjectHandler<Arc, ArcView>
{

   
   public ArcHandler(ArcView view, Container contentpane, Arc obj, PetriNetController controller) {
      super(view, contentpane, obj, controller);
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
      
 
      if (viewComponent instanceof ArcView) {
          menuItem = new JMenuItem("Edit Weight");
          menuItem.addActionListener(new ActionListener(){
             public void actionPerformed(ActionEvent e) {
                viewComponent.showEditor();
             }
          });
          popup.insert(menuItem, popupIndex++);

         menuItem = new JMenuItem(new SplitArcAction(viewComponent,
                                                      e.getPoint()));
         menuItem.setText("Split Arc Segment");
         popup.insert(menuItem, popupIndex++);

         popup.insert(new JPopupMenu.Separator(), popupIndex++);
      } else if (viewComponent instanceof ArcView) {
         if (((NormalArcView) viewComponent).isJoined()){
            NormalArcView PTArc;
            NormalArcView TPArc;

            if (((NormalArcView) viewComponent).getSource() instanceof PlaceView){
               PTArc = (NormalArcView) viewComponent;
               TPArc = ((NormalArcView) viewComponent).getInverse();
            } else {
               PTArc = ((NormalArcView) viewComponent).getInverse();
               TPArc = (NormalArcView) viewComponent;
            }

            menuItem = new JMenuItem("Edit Weight");
            menuItem.addActionListener(new ActionListener(){
               public void actionPerformed(ActionEvent e) {
                  viewComponent.showEditor();
               }
            });
            popup.insert(menuItem, popupIndex++);
            popup.insert(new JPopupMenu.Separator(), popupIndex++);
            popup.insert(new JPopupMenu.Separator(), popupIndex++);

//            menuItem = new JMenuItem(new InsertPointAction(viewComponent,
//                                                         e.getPoint()));
//            menuItem.setText("Insert Point");
//            popup.insert(menuItem, popupIndex++);

            menuItem = new JMenuItem(
                    new SplitArcsAction((NormalArcView) viewComponent, true));
            menuItem.setText("Split Arcs (PT / TP)");
            popup.insert(menuItem, popupIndex++);

            popup.insert(new JPopupMenu.Separator(), popupIndex++);

            menuItem = new JMenuItem(new DeleteInverseArcAction(PTArc));
            menuItem.setText("Delete (PT Arc)");
            popup.insert(menuItem, popupIndex++);

            menuItem = new JMenuItem(new DeleteInverseArcAction(TPArc));
            menuItem.setText("Delete (TP Arc)");
            popup.insert(menuItem, popupIndex++);

         } else {
             menuItem = new JMenuItem("Edit Weight");
             menuItem.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                   viewComponent.showEditor();
                }
             });
             popup.insert(menuItem, popupIndex++);

//            menuItem = new JMenuItem(new SplitArcAction(viewComponent,
//                                                         e.getPoint()));
//            menuItem.setText("Insert Point");
//            popup.insert(menuItem, popupIndex++);

            if (((NormalArcView) viewComponent).hasInverse()){
               menuItem = new JMenuItem(
                        new SplitArcsAction((NormalArcView) viewComponent, false));

               menuItem.setText("Join Arcs (PT / TP)");
               popup.insert(menuItem, popupIndex++);
            }
            popup.insert(new JPopupMenu.Separator(), popupIndex);
         }
      }
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

      final ArcView _arcView;
      final boolean joined;
      
      
      public SplitArcsAction(ArcView _arc, boolean _joined){
         _arcView = _arc;
         joined = _joined;
      }

       //TODO: REIMPLEMENT
      public void actionPerformed(ActionEvent e) {
//         if (joined) {
//            petriNetController.getHistoryManager().addNewEdit(_arcView.split());
//         } else {
//             petriNetController.getHistoryManager().addNewEdit(_arcView.join());
//         }
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
          HistoryManager historyManager = petriNetController.getHistoryManager();
         
         if (switchArcs) {
            historyManager.addNewEdit(_arcView.split());
         } else {
            historyManager.addNewEdit(_inverse.split());
         }
//         historyManager.deleteSelection(_arcView);

         _arcView.delete();
      }
   }   
   
}
