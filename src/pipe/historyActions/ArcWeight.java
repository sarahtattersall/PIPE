/*
 * ArcWeightEdit.java
 */

package pipe.historyActions;

import pipe.views.ArcView;
import pipe.views.MarkingView;

import java.util.LinkedList;

/**
 *
 * @author Alex Charalambous
 */
public class ArcWeight
        extends HistoryItem
{
   
   private final ArcView arc;
   private final LinkedList<MarkingView> newWeight;
   private final LinkedList<MarkingView> oldWeight;
   
   
   /** Creates a new instance of arcWeightEdit
    * @param _arc
    * @param _oldWeight
    * @param _newWeight*/
   public ArcWeight(ArcView _arc, LinkedList<MarkingView> _oldWeight, LinkedList<MarkingView> _newWeight) {
      arc = _arc;
      oldWeight = _oldWeight;      
      newWeight = _newWeight;
   }

   
   /** */
   public void undo() {
      arc.setWeight(oldWeight);
   }

   
   /** */
   public void redo() {
      arc.setWeight(newWeight);
   }
   
   
   public String toString(){
      return super.toString() + " " + arc.getName() + 
              "oldWeight: " + oldWeight + "newWeight: " + newWeight;
   }   
   
}
