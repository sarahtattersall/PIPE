/*
 * AddPetriNetObjectEdit.java
 */

package pipe.historyActions;

import pipe.gui.PetriNetTab;
import pipe.views.PetriNetView;
import pipe.views.PetriNetViewComponent;

/**
 *
 * @author corveau
 */
public class AddPetriNetObject
        extends HistoryItem
{
   
   private final PetriNetViewComponent pn;
   private final PetriNetView _model;
   private final PetriNetTab _view;
   
   
   /** Creates a new instance of placeWeightEdit
    * @param _pn
    * @param _view
    * @param _model*/
   public AddPetriNetObject(PetriNetViewComponent _pn,
                            PetriNetTab _view, PetriNetView _model) {
      pn = _pn;
      this._view = _view;
      this._model = _model;
   }

   
   /** */
   public void undo() {
      pn.delete();
   }

   
   /** */
   public void redo() {
      pn.undelete(_model, _view);
   }
   
   
   public String toString(){
      return super.toString() + " \"" + pn.getName() + "\"";
   }
   
}
