package pipe.models;


import pipe.io.adapters.modelAdapter.PetriNetAdapter;
import pipe.models.petrinet.PetriNet;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * This class just holds all petri nets and forms the base level of PNML
 */
@XmlRootElement(name = "pnml")
public class PetriNetHolder {
    @XmlJavaTypeAdapter(PetriNetAdapter.class)
    @XmlElement(name = "net")
    private final List<PetriNet> nets = new ArrayList<>();

    public void addNet(PetriNet net) {
        nets.add(net);
    }

    public PetriNet getNet(int index) {
        return nets.get(index);
    }

    /**
     *
     * @return the number of Petri nets stored in this holder
     */
    public int size() {
        return nets.size();
    }

    public boolean isEmpty() {
        return nets.isEmpty();
    }

    public void remove(PetriNet petriNet) {
        nets.remove(petriNet);
    }
}
