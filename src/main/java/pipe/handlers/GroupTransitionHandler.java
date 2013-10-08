package pipe.handlers;

import pipe.gui.ApplicationSettings;
import pipe.gui.Constants;
import pipe.gui.ZoomController;
import pipe.historyActions.HistoryItem;
import pipe.views.GroupTransitionView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * Class used to implement methods corresponding to mouse events on transitions.
 */
public class GroupTransitionHandler 
        extends PlaceTransitionObjectHandler
{
        //implements java.awt.event.MouseWheelListener {  //NOU-PERE
  
   
   public GroupTransitionHandler(Container contentpane, GroupTransitionView obj) {
      super(contentpane, obj);
   }

   
   public void mouseWheelMoved (MouseWheelEvent e) {

       if (!ApplicationSettings.getApplicationModel().isEditionAllowed() ||
              e.isControlDown()) {
         return;
      }
      
         int rotation = 0;
         if (e.getWheelRotation() < 0) {
            rotation = -e.getWheelRotation() * 135;
         } else {
            rotation = e.getWheelRotation() * 45;
         }
       ApplicationSettings.getApplicationView().getCurrentTab().getHistoryManager().addNewEdit(
                 ((GroupTransitionView) my).rotate(rotation));
      
   }   
   
   
   /** 
    * Creates the popup menu that the user will see when they right click on a 
    * component 
    */
   JPopupMenu getPopup(MouseEvent e) {
      int index = 0;
      JPopupMenu popup = super.getPopup(e);
      
      JMenuItem menuItem = new JMenuItem("Edit Transition");      
      menuItem.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e) {
            ((GroupTransitionView) my).showEditor();
         }
      });       
      popup.insert(menuItem, index++);             
           
      popup.insert(new JPopupMenu.Separator(), index);
      menuItem = new JMenuItem("Ungroup Transitions");      
      menuItem.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e) {
        	HistoryItem edit = ((GroupTransitionView) my).ungroupTransitions();
             ApplicationSettings.getApplicationView().getCurrentTab().getHistoryManager().addNewEdit(edit);
/*    		PetriNet model = Pipe.getCurrentPetriNetView();
    		model.removePetriNetObject(((GroupTransition)my));
    		PetriNetTab view = Pipe.getCurrentTab();
    		view.remove(((GroupTransition)my));*/
         }
      });       
      popup.insert(menuItem, index++);           
            
      return popup;
   }
   
   
   public void mouseClicked(MouseEvent e) {   
      if (SwingUtilities.isLeftMouseButton(e)){
          if (e.getClickCount() == 2 &&
                 ApplicationSettings.getApplicationModel().isEditionAllowed() &&
                 (ApplicationSettings.getApplicationModel().getMode() == Constants.TIMEDTRANS ||
                 ApplicationSettings.getApplicationModel().getMode() == Constants.IMMTRANS ||
                 ApplicationSettings.getApplicationModel().getMode() == Constants.SELECT)) {
            ((GroupTransitionView) my).showEditor();
         } 
      }  else if (SwingUtilities.isRightMouseButton(e)){
          if (ApplicationSettings.getApplicationModel().isEditionAllowed() && enablePopup) {
            JPopupMenu m = getPopup(e);
            if (m != null) {           
               int x = ZoomController.getZoomedValue(
                       my.getNameOffsetXObject().intValue(),
                       my.getZoomPercentage());
               int y = ZoomController.getZoomedValue(
                       my.getNameOffsetYObject().intValue(),
                       my.getZoomPercentage());
               m.show(my, x, y);
            }
         }
      }
   }
   
}
