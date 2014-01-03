/*
 * InsertPointAction.java
 */
package pipe.actions;

import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.ApplicationSettings;
import pipe.historyActions.HistoryManager;
import pipe.views.ArcView;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;


/**
 * This class is used to split an arc in two at the
 * point the user clicks the mouse button.
 * @author Pere
 */
public class InsertPointAction 
        extends javax.swing.AbstractAction{
   
   private final ArcView _selected;
   private final Point2D.Float mouseposition;
   
   
   public InsertPointAction(ArcView arcView, Point mousepos) {
      _selected = arcView;
      
      // Mousepos is relative to selected component i.e. the arc
      // Need to convert this into actual coordinates
      Point2D.Float offset = new Point2D.Float(_selected.getX(),
                                               _selected.getY());
      mouseposition = new Point2D.Float(mousepos.x + offset.x, 
                                        mousepos.y + offset.y);
   }
   
   
   public void actionPerformed(ActionEvent arg0) {
       PipeApplicationController controller = ApplicationSettings.getApplicationController();
       PetriNetController petriNetController = controller.getActivePetriNetController();
       HistoryManager historyManager = petriNetController.getHistoryManager();

       historyManager.addNewEdit(_selected.getArcPath().insertPointAt(mouseposition, false));
   }
   
}
