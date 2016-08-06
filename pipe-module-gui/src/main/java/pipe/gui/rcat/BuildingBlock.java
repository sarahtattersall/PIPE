package pipe.gui.rcat;

import uk.ac.imperial.pipe.models.petrinet.*;

import java.text.AttributedString;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 * Class for generating Building Blocks - a set of Places and Transitions
 * such that for every input transition there exists an output transition.
 *
 * @author Tanvi Potdar
 */
public class BuildingBlock {
    /**
     * collection of places in the building block
     */
    private Collection<Place> places;
    /**
     * collection of transitions in the building block
     */
    private Collection<Transition> transitions;
    /**
     * symbolic input transition rates stored as strings
     */
    private Map<Transition,String> inputRates;
    /**
     * symbolic output transition rates stored as strings
     */
    private Map<Transition,String> outputRates;


    /**
     * constructor for the building block
     * @param places
     * @param transitions
     */
    public BuildingBlock(Collection<Place> places, Collection<Transition> transitions){
        this.places = places;
        this.transitions = transitions;
    }

    /**
     * gets the places in the building block
     * @return places
     */
    public Collection<Place> getPlaces() {
        return places;
    }

    /**
     * sets the places in the building block to the collection provided
     * @param places
     */
    public void setPlaces(Collection<Place> places) {
        this.places = places;
    }

    /**
     * gets the transitions in the building block
     * @return transitions
     */
    public Collection<Transition> getTransitions() {
        return transitions;
    }

    /**
     * sets the transitions in the building block to the collection provided
     * @param transitions
     */
    public void setTransitions(Collection<Transition> transitions) {
        this.transitions = transitions;
    }

    /**
     * gets all the places and transitions in the building block
     * @return places and transitions
     */
    public Collection<Connectable> getConnectables(){
        Collection<Connectable> connectables = new HashSet<>();
        connectables.addAll(places);
        connectables.addAll(transitions);
        return connectables;

    }

    /**
     * Unknown input rates represented as strings
     * @param petriNet
     * @return input rates of the building block
     */
    public Map<Transition,String> getInputRates(PetriNet petriNet) {
        for(Transition transition: getTransitions()){
            if(petriNet.outboundArcs(transition).size()>0){
                inputRates.keySet().add(transition);
                inputRates.values().add("x_" + transition.getId());
            }
        }
        return inputRates;
    }

    /**
     *Returns the known rates of the output transitions in the building block
     * @param petriNet
     */
    public Map<Transition,String> getOutputRates(PetriNet petriNet) {
        for(Transition transition: getTransitions()){
            if(petriNet.inboundArcs(transition).size()>0){
                outputRates.keySet().add(transition);
                outputRates.values().add(transition.getRateExpr());
            }
        }
        return inputRates;
    }

    /**
     * get input rates in the building block
     * @return input rates
     */
    public void setInputRates(Map<Transition, String> inputRates) {
        this.inputRates = inputRates;
    }

    /**
     * get output rates in the building block
     * @return output rates
     */
    public void setOutputRates(Map<Transition, String> outputRates) {
        this.outputRates = outputRates;
    }
}
