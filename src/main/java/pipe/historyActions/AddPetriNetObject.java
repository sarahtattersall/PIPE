/*
 * AddPetriNetObjectEdit.java
 */

package pipe.historyActions;

import pipe.models.PetriNet;
import pipe.models.PetriNetComponent;

/**
 *
 * @author corveau
 */
public class AddPetriNetObject
        extends HistoryItem
{
   
   private final PetriNetComponent component;
   private final PetriNet petriNet;
   
   
   /** Creates a new instance of placeWeightEdit
    * @param component
    * @param petriNet*/
   public AddPetriNetObject(PetriNetComponent component, PetriNet petriNet) {
      this.component = component;
      this.petriNet = petriNet;
   }

   
   /** */
   public void undo() {
      petriNet.remove(component);
   }

   
   /** */
   public void redo() {
       petriNet.add(component);
   }
   
   
   public String toString(){
      return super.toString() + " \"" + component + "\"";
   }
   
}
