package pipe.naming;

import pipe.models.component.PetriNetComponent;
import pipe.models.petrinet.PetriNet;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * This class listens for add/delete messages for a component coming from the Petri net
 */
public class ComponentNamer extends AbstractUniqueNamer {

    /**
     * Petri net on which to listen for component changes
     */
    protected final PetriNet petriNet;

    /**
     * @param petriNet            Petri net to name components of, this class listens to changes in this petri net for naming
     * @param namePrefix          Value to prefix component names with, e.g. "P" for place
     * @param newChangeMessage    PetriNet message to look out for when adding item, triggers addition of name
     * @param deleteChangeMessage PetriNet message to look out for when deleting item, triggers removal of name
     */
    protected ComponentNamer(PetriNet petriNet, String namePrefix, String newChangeMessage,
                             String deleteChangeMessage) {
        super(namePrefix);
        this.petriNet = petriNet;
        observeChanges(petriNet, newChangeMessage, deleteChangeMessage);
    }

    private void observeChanges(PetriNet petriNet, final String newChangeMessage, final String deleteChangeMessage) {
        PropertyChangeListener listener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                String name = propertyChangeEvent.getPropertyName();
                if (name.equals(newChangeMessage)) {
                    PetriNetComponent component = (PetriNetComponent) propertyChangeEvent.getNewValue();
                    component.addPropertyChangeListener(nameListener);
                    names.add(component.getId());
                } else if (name.equals(deleteChangeMessage)) {
                    PetriNetComponent component = (PetriNetComponent) propertyChangeEvent.getOldValue();
                    component.removePropertyChangeListener(nameListener);
                    names.remove(component.getId());
                }
            }
        };
        petriNet.addPropertyChangeListener(listener);
    }
}
