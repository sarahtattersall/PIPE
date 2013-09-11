package pipe.actions;

import java.awt.event.ActionEvent;

import pipe.views.ConnectableView;


/**
 *
 */
public class ShowHideInfoAction 
        extends javax.swing.AbstractAction {
   
   private final ConnectableView _pto;
   
   
   public ShowHideInfoAction(ConnectableView component) {
      _pto = component;
   }
   
   
   /**  */
   public void actionPerformed(ActionEvent e) {    
      _pto.toggleAttributesVisible();
   }
   
}
