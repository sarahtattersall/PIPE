package pipe.gui.rcat;

import uk.ac.imperial.pipe.models.petrinet.Place;
import uk.ac.imperial.pipe.models.petrinet.Transition;

import java.util.Collection;
import java.util.HashSet;

/**
 * Class for generating Building Blocks - a set of Places and Transitions
 * such that for every input transition there exists an output transition.
 *
 * @author Tanvi Potdar
 */
public class BuildingBlock {
    private Collection<Place> places;
    private Collection<Transition> transitions;

    public BuildingBlock(Collection<Place> places, Collection<Transition> transitions){
        this.places = places;
        this.transitions = transitions;
    }

    public Collection<Place> getPlaces() {
        return places;
    }

    public void setPlaces(Collection<Place> places) {
        this.places = places;
    }

    public Collection<Transition> getTransitions() {
        return transitions;
    }

    public void setTransitions(Collection<Transition> transitions) {
        this.transitions = transitions;
    }

}
