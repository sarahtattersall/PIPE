/*
 * SplitArcPointAction.java
 *
 * Created on 21-Jun-2005
 */
package pipe.actions;

import pipe.controllers.ArcController;
import pipe.models.component.arc.ArcPoint;

import java.awt.event.ActionEvent;

/**
 * @author Nadeem
 *
 * This class is used to split a point on an arc into two to  allow the arc to 
 * be manipulated further.
 */
public class SplitArcPointAction 
        extends javax.swing.AbstractAction {
   
    private final ArcPoint arcPoint;
    private final ArcController arcController;


    public SplitArcPointAction(ArcPoint arcPoint, ArcController arcController) {
        this.arcPoint = arcPoint;
        this.arcController = arcController;
   }
   
   
   public void actionPerformed(ActionEvent e) {
       arcController.splitArcPoint(arcPoint);
   }
   
}
