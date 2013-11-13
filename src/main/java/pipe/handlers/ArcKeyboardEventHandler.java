package pipe.handlers;

import pipe.controllers.PetriNetController;
import pipe.gui.ApplicationSettings;
import pipe.gui.Constants;
import pipe.gui.PetriNetTab;
import pipe.views.ArcView;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


/**
* @authors Michael Camacho and Tom Barnwell
*
*/
public class ArcKeyboardEventHandler
        extends KeyAdapter {
   
   private final ArcView _arcViewBeingDrawn;
    private final PetriNetController petriNetController;
   
   
   public ArcKeyboardEventHandler(ArcView anArcView, PetriNetController controller) {
      _arcViewBeingDrawn = anArcView;
       petriNetController = controller;
   }
   

   public void keyPressed(KeyEvent e) {
      switch (e.getKeyCode()) {
         case KeyEvent.VK_META:
         case KeyEvent.VK_WINDOWS:
            // I don't know if it's a java's bug or if I have a configuration 
            // problem with component linux box, but there is an issue with the
            // Windows key under linux, so the space key is used as a provisional
            // solution
         case KeyEvent.VK_SPACE: //provisional
            ((PetriNetTab) _arcViewBeingDrawn.getParent()).setMetaDown(true);
            break;
            
         case KeyEvent.VK_ESCAPE:
         case KeyEvent.VK_DELETE:
            PetriNetTab aView = ((PetriNetTab) _arcViewBeingDrawn.getParent());
            //TODO: CANCEL ARC
             petriNetController.cancelArcCreation();
            //aView._createArcView = null;
            //_arcViewBeingDrawn.delete();
             if ((ApplicationSettings.getApplicationModel().getMode() == Constants.FAST_PLACE) ||
                    (ApplicationSettings.getApplicationModel().getMode() == Constants.FAST_TRANSITION)) {
                 ApplicationSettings.getApplicationModel().resetMode();
            }
            aView.repaint();
            break;
            
         default:
            break;
      }
   }
   
   
   public void keyReleased(KeyEvent e) {   
      switch (e.getKeyCode()) {
         case KeyEvent.VK_META:
         case KeyEvent.VK_WINDOWS:
         case KeyEvent.VK_SPACE: //provisional
            ((PetriNetTab) _arcViewBeingDrawn.getParent()).setMetaDown(false);
            break;
            
         default:
            break;
      }
      e.consume();
   }
   
}
