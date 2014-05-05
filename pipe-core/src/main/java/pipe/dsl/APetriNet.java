package pipe.dsl;

import pipe.exceptions.PetriNetComponentException;
import pipe.models.component.PetriNetComponent;
import pipe.models.component.place.Place;
import pipe.models.component.rate.RateParameter;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.PetriNet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


// Create:
// APetriNet.with(aToken("Red")).andAPlace("P0").containing(5, "Red")

/**
 * Usage:
 * APetriNet.with(AToken.withName("Red").andColor(Color.RED))
 *          .and(APlace.withId("P0").containing(5, "Red").tokens())
 *          .and(AToken.withId("T0"))
 *          .andFinally(ANormalArc.withSource("P0").andTarget("T0"))
 *
 * Alternativelty to only add one item to the Petri net:
 * APetriNet.withOnly(AToken.withName("Default").andColor(Color.BLACK));
 */
public class APetriNet {
    private Collection<DSLCreator<? extends PetriNetComponent>> creators = new ArrayList<>();


    /**
     * Entry method for creating a Petri Net
     * @param creator item creator to add to Petri net
     * @param <T> type of PetriNetComponent
     * @return instance of APetriNet class for chaining
     */
    public static <T extends PetriNetComponent> APetriNet with(DSLCreator<T> creator) {
        APetriNet aPetriNet = new APetriNet();
        aPetriNet.and(creator);
        return aPetriNet;
    }

    /**
     *
     * Adds more 'items' to the PetriNet by collecting their creators
     *
     * @param creator item creator to add to Petri net
     * @param <T> type of PetriNetComponent
     * @return instance of APetriNet class for chaining
     */
    public <T extends PetriNetComponent> APetriNet and(DSLCreator<T> creator) {
        creators.add(creator);
        return this;
    }

    /**
     *
     * Adds more 'items' to the PetriNet by collecting their creators
     *
     * @param finalCreator last item creator to add to Petri net
     * @param <T> type of PetriNetComponent
     * @return the created PetriNetcontaining all the items made from the added creators
     */
    public  <T extends PetriNetComponent> PetriNet andFinally(DSLCreator<T> finalCreator) {
        return and(finalCreator).makePetriNet();
    }

    /**
     * Creates a PetriNet with a single item
     * @param creator item creator to add to Petri net
     * @param <T> type of PetriNetComponent
     * @return created petri net containing the item
     */
    public static <T extends PetriNetComponent> PetriNet withOnly(DSLCreator<T> creator) {
        APetriNet aPetriNet = new APetriNet();
        return aPetriNet.andFinally(creator);
    }

    /**
     * Creates a petri net by looping through the creators and calling
     * their create methods
     * @return petri net with components added
     */
    private PetriNet makePetriNet() {
        Map<String, Token> tokens = new HashMap<>();
        Map<String, Place> places = new HashMap<>();
        Map<String, Transition> transitions = new HashMap<>();
        Map<String, RateParameter> rateParameters = new HashMap<>();

        PetriNet petriNet = new PetriNet();
        for (DSLCreator<? extends PetriNetComponent> creator : creators) {
            try {
                petriNet.add(creator.create(tokens, places, transitions, rateParameters));
            } catch (PetriNetComponentException e) {
                e.printStackTrace();
            }
        }
        return petriNet;
    }
}
