/*
 * PetriNetObjectNameEdit.java
 */
package pipe.historyActions;

import pipe.models.PetriNet;
import pipe.models.PetriNetComponent;
import pipe.views.PetriNetViewComponent;


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
    private final PetriNet petriNet;


    public PetriNetObjectName(PetriNetComponent component, PetriNet petriNet, String oldName, String newName) {
        //To change body of created methods use File | Settings | File Templates.
        this.component = component;
        this.petriNet = petriNet;
        this.oldName = oldName;
        this.newName = newName;
    }


    /** */
   public void undo() {
       component.setName(oldName);
       component.setId(oldName);
       petriNet.notifyObservers();
   }

   
   /** */
   public void redo() {
       component.setName(newName);
       component.setId(newName);
       petriNet.notifyObservers();
   }
   
}
