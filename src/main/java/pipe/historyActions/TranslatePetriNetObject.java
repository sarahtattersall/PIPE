/*
 * TranslatePetriNetObjectEdit.java
 */

package pipe.historyActions;

import pipe.views.PetriNetViewComponent;


/**
 *
 * @author Pere Bonet
 */
public class TranslatePetriNetObject
        extends HistoryItem
{
   
   private final PetriNetViewComponent pn;
   private final Integer transX;
   private final Integer transY;
   
   
   /** Creates a new instance of
    * @param _pn
    * @param _transX
    * @param _transY*/
   public TranslatePetriNetObject(PetriNetViewComponent _pn,
                                  Integer _transX, Integer _transY) {
      pn = _pn;
      transX = _transX;
      transY = _transY;
   }

   
   /** */
   public void undo() {
      pn.translate(-transX, -transY);
   }

   
   /** */
   public void redo() {
      pn.translate(transX, transY);
   }

   
   public String toString(){
      return super.toString()  + " " + pn.getName() +
              " (" + transX + "," + transY + ")";
   }
   
}
