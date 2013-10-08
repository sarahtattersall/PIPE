package pipe.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import pipe.views.TransitionView;

/**
 * Action object that can be used to alternate a transition between
 * timed and immediate.
 * @author unknown
 */
class EditServerAction
        extends AbstractAction {
   
   private static final long serialVersionUID = 2001;
   
   private final TransitionView _selected;
   
   
   public EditServerAction(TransitionView component) {
      _selected = component;
   }
   
   
   /** Action for toggling timing on/off */
   public void actionPerformed(ActionEvent e) {
      boolean currentServer = _selected.isInfiniteServer();
      // if currentTimed it true, set it false, if false, set it true
      _selected.setInfiniteServer( ! currentServer );
   }
   
}		// end of class EditServerAction
