/*
 * ParameterNameEdit.java
 */

package pipe.historyActions;


import pipe.views.viewComponents.Parameter;

/**
 *
 * @author corveau
 */
public class ParameterName
        extends HistoryItem
{
   
   private final Parameter parameter;
   private final String newName;
   private final String oldName;
   
   
   /** Creates a new instance of placeCapacityEdit
    * @param _parameter
    * @param _oldName
    * @param _newName*/
   public ParameterName(Parameter _parameter,
                        String _oldName, String _newName) {
      parameter = _parameter;
      oldName = _oldName;      
      newName = _newName;
   }

   
   /** */
   public void undo() {
      parameter.setParameterName(oldName);
      parameter.update();
   }

   
   /** */
   public void redo() {
      parameter.setParameterName(newName);
      parameter.update();
   }
   
}
