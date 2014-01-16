/*
 * PetriNetObjectNameEdit.java
 */
package pipe.historyActions;

import pipe.models.component.PetriNetComponent;

/**
 *
 * @author corveau
 */
public class PetriNetObjectName
        extends HistoryItem
{

    private final String oldName;
    private final String newName;
    private final PetriNetComponent component;


    public PetriNetObjectName(PetriNetComponent component, String oldName, String newName) {
        this.component = component;
        this.oldName = oldName;
        this.newName = newName;
    }


    /** */
   public void undo() {
       component.setName(oldName);
       component.setId(oldName);
   }

   
   /** */
   public void redo() {
       component.setName(newName);
       component.setId(newName);
   }
   
}
