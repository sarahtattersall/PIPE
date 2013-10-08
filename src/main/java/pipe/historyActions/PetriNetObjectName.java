/*
 * PetriNetObjectNameEdit.java
 */
package pipe.historyActions;

import pipe.views.PetriNetViewComponent;


/**
 *
 * @author corveau
 */
public class PetriNetObjectName
        extends HistoryItem
{
   
   private final PetriNetViewComponent pno;
   private final String oldName;
   private final String newName;
   
   
   /** Creates a new instance of placeNameEdit
    * @param _pno
    * @param _oldName
    * @param _newName*/
   public PetriNetObjectName(PetriNetViewComponent _pno,
                             String _oldName, String _newName) {
      pno = _pno;
      oldName = _oldName;      
      newName = _newName;
   }

   
   /** */
   public void undo() {
      pno.setName(oldName);
      pno.setId(oldName);
   }

   
   /** */
   public void redo() {
      pno.setName(newName);
      pno.setId(newName);
   }
   
}
