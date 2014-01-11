/*
 * transitionPriorityEdit.java
 */
package pipe.historyActions;

import pipe.views.GroupTransitionView;


/**
 *
 * @author Alex Charalambous
 */
public class GroupTransitionRotation
        extends HistoryItem
{
   
   private final GroupTransitionView groupTransition;
   private final Integer angle;
   
   
   /** Creates a new instance of placePriorityEdit
    * @param _groupTransition
    * @param _angle*/
   public GroupTransitionRotation(GroupTransitionView _groupTransition, Integer _angle) {
      groupTransition = _groupTransition;
      angle = _angle;
   }

   
   /** */
   public void undo() {
      groupTransition.rotate(-angle);
   }

   
   /** */
   public void redo() {
      groupTransition.rotate(angle);
   }
   
}
