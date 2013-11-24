package pipe.handlers;

import pipe.actions.ShowHideInfoAction;
import pipe.controllers.PetriNetController;
import pipe.gui.ApplicationSettings;
import pipe.gui.Constants;
import pipe.gui.ZoomController;
import pipe.historyActions.HistoryItem;
import pipe.models.PipeApplicationModel;
import pipe.models.Transition;
import pipe.views.TransitionView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * Class used to implement methods corresponding to mouse events on transitions.
 */
public class TransitionHandler 
        extends PlaceTransitionObjectHandler<Transition, TransitionView>
{
  
   
   public TransitionHandler(TransitionView view, Container contentpane, Transition obj, PetriNetController controller) {
      super(view, contentpane, obj, controller);
   }

   
   public void mouseWheelMoved (MouseWheelEvent e) {
//
//       if (!ApplicationSettings.getApplicationModel().isEditionAllowed() ||
//              e.isControlDown()) {
//         return;
//      }
//
//      if (e.isShiftDown()) {
//          ApplicationSettings.getApplicationView().getCurrentTab().getHistoryManager().addNewEdit(
//                 ((TransitionView) component).setTimed(
//                 !((TransitionView) component).isTimed()));
//      } else {
//         int rotation = 0;
//         if (e.getWheelRotation() < 0) {
//            rotation = -e.getWheelRotation() * 135;
//         } else {
//            rotation = e.getWheelRotation() * 45;
//         }
//          ApplicationSettings.getApplicationView().getCurrentTab().getHistoryManager().addNewEdit(
//                 ((TransitionView) component).rotate(rotation));
//      }
   }   
   
   
   /** 
    * Creates the popup menu that the user will see when they right click on a 
    * component 
    */
   protected JPopupMenu getPopup(MouseEvent e) {
      int index = 0;
      JPopupMenu popup = super.getPopup(e);
      JMenuItem menuItem = new JMenuItem("Edit Transition");
      menuItem.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e) {
            viewComponent.showEditor();
         }
      });
      popup.insert(menuItem, index++);

      popup.insert(new JPopupMenu.Separator(), index);
      menuItem = new JMenuItem("Group Transitions");
      menuItem.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e) {
        	 HistoryItem edit = viewComponent.groupTransitions();
        	 if(edit != null){
//                 ApplicationSettings.getApplicationView().getCurrentTab().getHistoryManager().addNewEdit(edit);
        	 }
         }
      });
      popup.insert(menuItem, index++);

      return popup;
   }
   
   
   public void mouseClicked(MouseEvent e) {
          PipeApplicationModel applicationModel = ApplicationSettings.getApplicationModel();
//      if (SwingUtilities.isLeftMouseButton(e)){
//          if (e.getClickCount() == 2 &&
//                 applicationModel.isEditionAllowed() &&
//                 (applicationModel.getMode() == Constants.TIMEDTRANS ||
//                 applicationModel.getMode() == Constants.IMMTRANS ||
//                 applicationModel.getMode() == Constants.SELECT)) {
//            ((TransitionView) component).showEditor();
//         }
//      }  else if (SwingUtilities.isRightMouseButton(e)){
//          if (applicationModel.isEditionAllowed() && enablePopup) {
//            JPopupMenu m = getPopup(e);
//            if (m != null) {
//               int x = ZoomController.getZoomedValue(
//                       component.getNameXOffset(),
//                       component.getZoomPercentage());
//               int y = ZoomController.getZoomedValue(
//                       component.getNameYOffset(),
//                       component.getZoomPercentage());
//               m.show(component, x, y);
//            }
//         }
//      }
   }
   
}
