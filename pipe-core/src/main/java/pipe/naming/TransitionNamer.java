package pipe.naming;

import pipe.models.component.transition.Transition;
import pipe.models.petrinet.PetriNet;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashSet;

public class TransitionNamer implements PetriNetComponentNamer {
    private final PetriNet petriNet;

    private final  Collection<String> transitionNames = new HashSet<String>();

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
                if (name.equals("newTransition")) {
                    Transition transition = (Transition) propertyChangeEvent.getNewValue();
                    transitionNames.add(transition.getId());
                } else if (name.equals("deleteTransition")) {
                    Transition transition = (Transition) propertyChangeEvent.getOldValue();
                    transitionNames.remove(transition.getId());
                }
            }
        };
        petriNet.addPropertyChangeListener(listener);
    }

    private void initialiseTransitionNames() {
        for (Transition transition : petriNet.getTransitions()) {
            transitionNames.add(transition.getId());
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
}
