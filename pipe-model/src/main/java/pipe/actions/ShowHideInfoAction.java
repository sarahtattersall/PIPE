package pipe.actions;

import pipe.views.ConnectableView;

import java.awt.event.ActionEvent;


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
