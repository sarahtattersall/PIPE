package pipe.actions.petrinet;

import pipe.views.ConnectableView;
import uk.ac.imperial.pipe.models.component.Connectable;

import java.awt.event.ActionEvent;


/**
 *
 */
public class  ShowHideInfoAction<T extends Connectable>
        extends javax.swing.AbstractAction {
   
   private final ConnectableView<T> connectableView;
   
   
   public ShowHideInfoAction(ConnectableView<T> component) {
      connectableView = component;
   }
   
   
   /**  */
   @Override
   public void actionPerformed(ActionEvent e) {
      connectableView.toggleAttributesVisible();
   }
   
}
