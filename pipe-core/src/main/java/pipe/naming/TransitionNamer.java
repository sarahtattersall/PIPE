package pipe.naming;

import pipe.models.component.transition.Transition;
import pipe.models.petrinet.PetriNet;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashSet;

public class TransitionNamer implements PetriNetComponentNamer {

    private final PetriNet petriNet;

    private final Collection<String> transitionNames = new HashSet<>();

    private final PropertyChangeListener transitionNameListener =  new NameChangeListener(transitionNames);

    public TransitionNamer(PetriNet petriNet) {

        this.petriNet = petriNet;
        observeChanges(petriNet);
        initialiseTransitionNames();
    }

    private void observeChanges(PetriNet petriNet) {
        PropertyChangeListener listener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                String name = propertyChangeEvent.getPropertyName();
                if (name.equals(PetriNet.NEW_TRANSITION_CHANGE_MESSAGE)) {
                    Transition transition = (Transition) propertyChangeEvent.getNewValue();
                    transition.addPropertyChangeListener(transitionNameListener);
                    transitionNames.add(transition.getId());
                } else if (name.equals(PetriNet.DELETE_TRANSITION_CHANGE_MESSAGE)) {
                    Transition transition = (Transition) propertyChangeEvent.getOldValue();
                    transition.removePropertyChangeListener(transitionNameListener);
                    transitionNames.remove(transition.getId());
                }
            }
        };
        petriNet.addPropertyChangeListener(listener);
    }

    private void initialiseTransitionNames() {
        for (Transition transition : petriNet.getTransitions()) {
            transitionNames.add(transition.getId());
            transition.addPropertyChangeListener(transitionNameListener);
        }
    }

    @Override
    public String getName() {
        int transitionNumber = 0;
        String name = "T" + transitionNumber;
        while (transitionNames.contains(name)) {
            transitionNumber++;
            name = "T" + transitionNumber;
        }
        return name;
    }

    @Override
    public boolean isUniqueName(String name) {
        return !transitionNames.contains(name);
    }

}
