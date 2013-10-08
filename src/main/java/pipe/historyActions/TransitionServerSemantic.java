/*
 * TransitionServerSemanticEdit.java
 */
package pipe.historyActions;

import pipe.views.TransitionView;


/**
 *
 * @author corveau
 */
public class TransitionServerSemantic
        extends HistoryItem
{
   
   private final TransitionView _transitionView;
   
   
   /** Creates a new instance of TransitionServerSemanticEdit
    * @param _transitionView*/
   public TransitionServerSemantic(TransitionView _transitionView) {
      this._transitionView = _transitionView;
   }

   
   /** */
   public void undo() {
      _transitionView.setInfiniteServer(!_transitionView.isInfiniteServer());
   }

   
   /** */
   public void redo() {
      _transitionView.setInfiniteServer(!_transitionView.isInfiniteServer());
   }
   
}
