package pipe.animation;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.dsl.*;
import pipe.exceptions.PetriNetComponentNotFoundException;
import pipe.models.component.arc.InboundArc;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.PetriNet;

import java.awt.Color;
import java.util.Collection;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class PetriNetAnimationLogicTest {


    @Test
    public void multiColorArcsCanFire() throws PetriNetComponentNotFoundException {
        PetriNet petriNet = APetriNet.with(AToken.called("Default").withColor(Color.BLACK)).and(
                AToken.called("Red").withColor(Color.RED)).and(
                APlace.withId("P0").containing(1, "Default").token().and(1, "Red").token()).and(
                APlace.withId("P1")).and(ATransition.withId("T0")).and(ATransition.withId("T1")).and(
                ANormalArc.withSource("P0").andTarget("T0").with("1", "Default").token()).and(
                ANormalArc.withSource("P0").andTarget("T1").with("1", "Red").token()).and(
                ANormalArc.withSource("T0").andTarget("P1").with("1", "Default").token()).andFinally(
                ANormalArc.withSource("T1").andTarget("P1").with("1", "Red").token());

        Transition t0 = petriNet.getComponent("T0", Transition.class);
        Transition t1 = petriNet.getComponent("T1", Transition.class);

        AnimationLogic animator = new PetriNetAnimationLogic(petriNet);
        Collection<Transition> transitions = animator.getEnabledTransitions(getState(petriNet));
        assertEquals("Both transitions were not enabled", 2, transitions.size());
        assertThat(transitions).contains(t0, t1);
    }

    /**
     * Creates a new state containing the token counts for the
     * current Petri net.
     *
     * @return current state of the Petri net
     */
    private State getState(PetriNet petriNet) {
        Multimap<String, TokenCount> tokenCounts = HashMultimap.create();
        for (Place place : petriNet.getPlaces()) {
            for (Token token : petriNet.getTokens()) {
                tokenCounts.put(place.getId(), new TokenCount(token.getId(), place.getTokenCount(token.getId())));
            }
        }
        return  new HashedState(tokenCounts);
    }

    @Test
    public void multiColorArcsCanFireWithZeroWeighting() throws PetriNetComponentNotFoundException {
        PetriNet petriNet = APetriNet.with(AToken.called("Default").withColor(Color.BLACK)).and(
                AToken.called("Red").withColor(Color.RED)).and(
                APlace.withId("P0").containing(1, "Default").token().and(1, "Red").token()).and(
                APlace.withId("P1")).and(ATransition.withId("T0")).and(ATransition.withId("T1")).and(
                ANormalArc.withSource("P0").andTarget("T0").with("1", "Default").token().and("0", "Red").tokens()).and(
                ANormalArc.withSource("P0").andTarget("T1").with("0", "Default").tokens().and("1", "Red").token()).and(
                ANormalArc.withSource("T0").andTarget("P1").with("1", "Default").token().and("0",
                        "Red").tokens()).andFinally(
                ANormalArc.withSource("T1").andTarget("P1").with("0", "Default").tokens().and("1", "Red").token());

        Transition t0 = petriNet.getComponent("T0", Transition.class);
        Transition t1 = petriNet.getComponent("T1", Transition.class);

        AnimationLogic animator = new PetriNetAnimationLogic(petriNet);
        Collection<Transition> transitions = animator.getEnabledTransitions(getState(petriNet));
        assertEquals("Both transitions were not enabled", 2, transitions.size());
        assertThat(transitions).contains(t0, t1);
    }

    @Test
    public void correctlyIdentifiesEnabledTransition() throws PetriNetComponentNotFoundException {
        int tokenWeight = 1;
        PetriNet petriNet = createSimplePetriNet(tokenWeight);
        Transition transition = petriNet.getComponent("T1", Transition.class);
        AnimationLogic animator = new PetriNetAnimationLogic(petriNet);
        Collection<Transition> enabled = animator.getEnabledTransitions(getState(petriNet));
        assertTrue("Petri net did not put transition in enabled collection", enabled.contains(transition));
    }

    /**
     * Create simple petrinet with P1 -> T1 -> P2
     * Initialises a token in P1 and gives arcs A1 and A2 a weight of tokenWeight to a default token
     *
     * @param tokenWeight
     * @return
     */
    public PetriNet createSimplePetriNet(int tokenWeight) {
        String arcWeight = Integer.toString(tokenWeight);
        return APetriNet.with(AToken.called("Default").withColor(Color.BLACK)).and(
                APlace.withId("P1").containing(1, "Default").token()).and(APlace.withId("P2")).and(
                ATransition.withId("T1")).and(
                ANormalArc.withSource("P1").andTarget("T1").with(arcWeight, "Default").tokens()).andFinally(
                ANormalArc.withSource("T1").andTarget("P2").with(arcWeight, "Default").tokens());
    }

    @Test
    public void correctlyIdentifiesEnabledWithNoSecondColourToken() throws PetriNetComponentNotFoundException {
        PetriNet petriNet = APetriNet.with(AToken.called("Default").withColor(Color.BLACK)).and(
                AToken.called("Red").withColor(Color.RED)).and(
                APlace.withId("P1").containing(1, "Red").token().and(1, "Default").token()).and(
                APlace.withId("P2")).and(ATransition.withId("T1")).andFinally(
                ANormalArc.withSource("P1").andTarget("T1").with("1", "Default").token().and("0", "Red").tokens());

        Transition transition = petriNet.getComponent("T1", Transition.class);

        AnimationLogic animator = new PetriNetAnimationLogic(petriNet);
        Collection<Transition> enabled = animator.getEnabledTransitions(getState(petriNet));
        assertTrue("Petri net did not put transition in enabled collection", enabled.contains(transition));
    }

    @Test
    public void correctlyIdentifiesNotEnabledTransitionDueToEmptyPlace() throws PetriNetComponentNotFoundException {
        int tokenWeight = 4;
        PetriNet petriNet = createSimplePetriNet(tokenWeight);
        Place place = petriNet.getComponent("P1", Place.class);
        Transition transition = petriNet.getComponent("T1", Transition.class);
        place.decrementTokenCount("Default");

        AnimationLogic animator = new PetriNetAnimationLogic(petriNet);
        Collection<Transition> enabled = animator.getEnabledTransitions(getState(petriNet));
        assertThat(enabled).doesNotContain(transition);
    }

    @Test
    public void correctlyIdentifiesNotEnabledTransitionDueToNotEnoughTokens()
            throws PetriNetComponentNotFoundException {
        int tokenWeight = 4;
        PetriNet petriNet = createSimplePetriNet(tokenWeight);
        Transition transition = petriNet.getComponent("T1", Transition.class);

        AnimationLogic animator = new PetriNetAnimationLogic(petriNet);
        Collection<Transition> enabled = animator.getEnabledTransitions(getState(petriNet));
        assertThat(enabled).doesNotContain(transition);
    }

    @Test
    public void correctlyIdentifiesNotEnabledTransitionDueToOnePlaceNotEnoughTokens()
            throws PetriNetComponentNotFoundException {
        int tokenWeight = 1;
        PetriNet petriNet = createSimplePetriNetTwoPlacesToTransition(tokenWeight);
        Transition transition = petriNet.getComponent("T1", Transition.class);

        AnimationLogic animator = new PetriNetAnimationLogic(petriNet);
        Collection<Transition> enabled = animator.getEnabledTransitions(getState(petriNet));
        assertFalse("Petri net put transition in enabled collection", enabled.contains(transition));
    }

    /**
     * Create simple petrinet with P1 -> T1 and P2 -> T1
     * Initialises a token in P1 and gives arcs A1 and A2 a weight of tokenWeight to a default token
     *
     * @param tokenWeight
     * @return
     */
    public PetriNet createSimplePetriNetTwoPlacesToTransition(int tokenWeight) {
        String weight = Integer.toString(tokenWeight);
        return APetriNet.with(AToken.called("Default").withColor(Color.BLACK)).and(APlace.withId("P1")).and(
                APlace.withId("P2")).and(ATransition.withId("T1")).and(
                ANormalArc.withSource("P1").andTarget("T1").with(weight, "Default").tokens()).andFinally(
                ANormalArc.withSource("P2").andTarget("T1").with(weight, "Default").tokens());
    }

    @Test
    public void correctlyIdentifiesNotEnabledTransitionDueToArcNeedingTwoDifferentTokens()
            throws PetriNetComponentNotFoundException {
        int tokenWeight = 1;
        PetriNet petriNet = createSimplePetriNet(tokenWeight);

        Token redToken = new Token("red", new Color(255, 0, 0));
        petriNet.addToken(redToken);

        InboundArc arc = petriNet.getComponent("P1 TO T1", InboundArc.class);
        arc.getTokenWeights().put(redToken.getId(), "1");
        Transition transition = petriNet.getComponent("T1", Transition.class);

        AnimationLogic animator = new PetriNetAnimationLogic(petriNet);
        Collection<Transition> enabled = animator.getEnabledTransitions(getState(petriNet));
        assertThat(enabled).doesNotContain(transition);
    }

    @Test
    public void correctlyIdentifiesEnabledTransitionRequiringTwoTokens() throws PetriNetComponentNotFoundException {
        int tokenWeight = 1;
        PetriNet petriNet = createSimplePetriNet(tokenWeight);

        Token redToken = new Token("red", new Color(255, 0, 0));
        petriNet.addToken(redToken);
        InboundArc arc = petriNet.getComponent("P1 TO T1", InboundArc.class);
        arc.getTokenWeights().put(redToken.getId(), "1");

        Place place = petriNet.getComponent("P1", Place.class);
        Transition transition = petriNet.getComponent("T1", Transition.class);
        place.incrementTokenCount(redToken.getId());

        AnimationLogic animator = new PetriNetAnimationLogic(petriNet);
        Collection<Transition> enabled = animator.getEnabledTransitions(getState(petriNet));
        assertThat(enabled).contains(transition);
    }

    @Test
    public void onlyEnablesHigherPriorityTransition() {
        PetriNet petriNet = new PetriNet();
        Transition t1 = new Transition("1", "1");
        t1.setPriority(10);
        Transition t2 = new Transition("2", "2");
        t2.setPriority(1);
        petriNet.addTransition(t1);
        petriNet.addTransition(t2);

        AnimationLogic animator = new PetriNetAnimationLogic(petriNet);
        Collection<Transition> enabled = animator.getEnabledTransitions(getState(petriNet));
        assertEquals(1, enabled.size());
        assertThat(enabled).containsExactly(t1);
    }

    @Test
    public void correctlyDoesNotEnableTransitionsIfPlaceCapacityIsFull() throws PetriNetComponentNotFoundException {
        PetriNet petriNet = createSimplePetriNet(2);
        Token token = petriNet.getComponent("Default", Token.class);

        Place p1 = petriNet.getComponent("P1", Place.class);
        p1.setTokenCount(token.getId(), 2);
        Place p2 = petriNet.getComponent("P2", Place.class);
        p2.setCapacity(1);

        Transition transition = petriNet.getComponent("T1", Transition.class);

        AnimationLogic animator = new PetriNetAnimationLogic(petriNet);
        Collection<Transition> enabled = animator.getEnabledTransitions(getState(petriNet));
        assertThat(enabled).doesNotContain(transition);
    }

    @Test
    public void correctlyEnablesTransitionIfSelfLoop() throws PetriNetComponentNotFoundException {
        PetriNet petriNet = createSelfLoopPetriNet("1");
        Place place = petriNet.getComponent("P0", Place.class);
        Token token = petriNet.getComponent("Default", Token.class);
        place.setTokenCount(token.getId(), 1);
        place.setCapacity(1);

        Transition transition = petriNet.getComponent("T1", Transition.class);

        AnimationLogic animator = new PetriNetAnimationLogic(petriNet);
        Collection<Transition> enabled = animator.getEnabledTransitions(getState(petriNet));
        assertThat(enabled).containsExactly(transition);
    }

    private PetriNet createSelfLoopPetriNet(String tokenWeight) {
        return APetriNet.with(AToken.called("Default").withColor(Color.BLACK)).and(APlace.withId("P0")).and(
                ATransition.withId("T1")).and(
                ANormalArc.withSource("T1").andTarget("P0").with(tokenWeight, "Default").tokens()).andFinally(
                ANormalArc.withSource("P0").andTarget("T1").with(tokenWeight, "Default").tokens());
    }

    @Test
    public void correctlyMarksInhibitorArcEnabledTransition() throws PetriNetComponentNotFoundException {
        PetriNet petriNet = createSimpleInhibitorPetriNet(1);
        Transition transition = petriNet.getComponent("T1", Transition.class);
        AnimationLogic animator = new PetriNetAnimationLogic(petriNet);
        Collection<Transition> enabled = animator.getEnabledTransitions(getState(petriNet));
        assertThat(enabled).contains(transition);
    }

    /**
     * Create simple petrinet with P1 -o T1 -> P2
     * Initialises a token in P1 and gives arcs A1 and A2 a weight of tokenWeight to a default token
     *
     * @param tokenWeight
     * @return
     */
    public PetriNet createSimpleInhibitorPetriNet(int tokenWeight) {
        return APetriNet.with(AToken.called("Default").withColor(Color.BLACK)).and(APlace.withId("P1")).and(
                APlace.withId("P2")).and(ATransition.withId("T1")).and(
                AnInhibitorArc.withSource("P1").andTarget("T1")).andFinally(
                ANormalArc.withSource("T1").andTarget("P2").with(Integer.toString(tokenWeight), "Default").tokens());
    }

    @Test
    public void calculatesSimpleSuccessorStates() {
        PetriNet petriNet = createSimplePetriNet(1);
        State state = getState(petriNet);
        AnimationLogic animator = new PetriNetAnimationLogic(petriNet);
        Map<State, Collection<Transition>> successors = animator.getSuccessors(state);

        assertEquals(1, successors.size());
        State successor = successors.keySet().iterator().next();

        Collection<TokenCount> actualP1 =   successor.getTokens("P1");
        assertThat(actualP1).contains(new TokenCount("Default", 0));


        Collection<TokenCount> actualP2 =   successor.getTokens("P2");
        assertThat(actualP2).contains(new TokenCount("Default", 1));
    }


}