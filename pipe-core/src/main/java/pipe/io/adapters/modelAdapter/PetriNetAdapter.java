package pipe.io.adapters.modelAdapter;

import pipe.exceptions.PetriNetComponentException;
import pipe.io.adapters.model.AdaptedPetriNet;
import pipe.models.component.PetriNetComponent;
import pipe.models.petrinet.PetriNet;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class PetriNetAdapter extends XmlAdapter<AdaptedPetriNet, PetriNet> {
    @Override
    public PetriNet unmarshal(AdaptedPetriNet v) throws PetriNetComponentException {
        PetriNet petriNet = new PetriNet();
        addToPetriNet(v.tokens, petriNet);
        addToPetriNet(v.annotations, petriNet);
        addToPetriNet(v.rateParameters, petriNet);
        addToPetriNet(v.places, petriNet);
        addToPetriNet(v.transitions, petriNet);
        addToPetriNet(v.arcs, petriNet);
        return petriNet;
    }

    @Override
    public AdaptedPetriNet marshal(PetriNet v) {
        AdaptedPetriNet petriNet = new AdaptedPetriNet();
        petriNet.tokens = v.getTokens();
        petriNet.annotations = v.getAnnotations();
        petriNet.rateParameters = v.getRateParameters();
        petriNet.places = v.getPlaces();
        petriNet.transitions = v.getTransitions();
        petriNet.arcs = v.getArcs();
        return petriNet;
    }

    private void addToPetriNet(Iterable<? extends PetriNetComponent> components, PetriNet petriNet)
            throws PetriNetComponentException {
        if (components != null) {
            for (PetriNetComponent component : components) {
                petriNet.add(component);
            }
        }
    }
}
