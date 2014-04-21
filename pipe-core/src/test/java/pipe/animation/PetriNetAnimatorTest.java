package pipe.animation;

import org.junit.Test;
import pipe.dsl.*;
import pipe.exceptions.PetriNetComponentNotFoundException;
import pipe.models.component.Connectable;
import pipe.models.component.arc.Arc;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.PetriNet;
import pipe.visitor.ClonePetriNet;

import java.awt.Color;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class PetriNetAnimatorTest {


    @Test
    public void correctlyIdentifiesEnabledTransition() throws PetriNetComponentNotFoundException {
        int tokenWeight = 1;
        PetriNet petriNet = createSimplePetriNet(tokenWeight);
        Animator animator = new PetriNetAnimator(petriNet);
        Transition transition = petriNet.getComponent("T1", Transition.class);
        Collection<Transition> enabled = animator.getEnabledTransitions();
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

        Animator animator = new PetriNetAnimator(petriNet);
        Collection<Transition> enabled = animator.getEnabledTransitions();
        assertTrue("Petri net did not put transition in enabled collection", enabled.contains(transition));
    }

    @Test
    public void correctlyIdentifiesNotEnabledTransitionDueToEmptyPlace() throws PetriNetComponentNotFoundException {
        int tokenWeight = 4;
        PetriNet petriNet = createSimplePetriNet(tokenWeight);
        Token token = petriNet.getComponent("Default", Token.class);
        Place place = petriNet.getComponent("P1", Place.class);
        Transition transition = petriNet.getComponent("T1", Transition.class);
        place.decrementTokenCount(token);

        Animator animator = new PetriNetAnimator(petriNet);
        Collection<Transition> enabled = animator.getEnabledTransitions();
        assertThat(enabled).doesNotContain(transition);
    }


    @Test
    public void correctlyIdentifiesNotEnabledTransitionDueToNotEnoughTokens()
            throws PetriNetComponentNotFoundException {
        int tokenWeight = 4;
        PetriNet petriNet = createSimplePetriNet(tokenWeight);
        Transition transition = petriNet.getComponent("T1", Transition.class);

        Animator animator = new PetriNetAnimator(petriNet);
        Collection<Transition> enabled = animator.getEnabledTransitions();
        assertThat(enabled).doesNotContain(transition);
    }

    @Test
    public void correctlyIdentifiesNotEnabledTransitionDueToOnePlaceNotEnoughTokens()
            throws PetriNetComponentNotFoundException {
        int tokenWeight = 1;
        PetriNet petriNet = createSimplePetriNetTwoPlacesToTransition(tokenWeight);
        Transition transition = petriNet.getComponent("T1", Transition.class);

        Animator animator = new PetriNetAnimator(petriNet);
        Collection<Transition> enabled = animator.getEnabledTransitions();
        assertFalse("Petri net put transition in enabled collection", enabled.contains(transition));
    }

    @Test
    public void correctlyIdentifiesNotEnabledTransitionDueToArcNeedingTwoDifferentTokens()
            throws PetriNetComponentNotFoundException {
        int tokenWeight = 1;
        PetriNet petriNet = createSimplePetriNet(tokenWeight);

        Token redToken = new Token("red", new Color(255, 0, 0));
        petriNet.addToken(redToken);

        Arc<? extends Connectable, ? extends Connectable> arc = petriNet.getComponent("P1 TO T1", Arc.class);
        arc.getTokenWeights().put(redToken, "1");
        Transition transition = petriNet.getComponent("T1", Transition.class);

        Animator animator = new PetriNetAnimator(petriNet);
        Collection<Transition> enabled = animator.getEnabledTransitions();
        assertThat(enabled).doesNotContain(transition);
    }

    @Test
    public void correctlyIdentifiesEnabledTransitionRequiringTwoTokens() throws PetriNetComponentNotFoundException {
        int tokenWeight = 1;
        PetriNet petriNet = createSimplePetriNet(tokenWeight);

        Token redToken = new Token("red", new Color(255, 0, 0));
        petriNet.addToken(redToken);
        Arc<? extends Connectable, ? extends Connectable> arc = petriNet.getComponent("P1 TO T1", Arc.class);
        arc.getTokenWeights().put(redToken, "1");

        Place place = petriNet.getComponent("P1", Place.class);
        Transition transition = petriNet.getComponent("T1", Transition.class);
        place.incrementTokenCount(redToken);

        Animator animator = new PetriNetAnimator(petriNet);
        Collection<Transition> enabled = animator.getEnabledTransitions();
        assertThat(enabled).contains(transition);
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
    public void onlyEnablesHigherPriorityTransition() {
        PetriNet net = new PetriNet();
        Transition t1 = new Transition("1", "1");
        t1.setPriority(10);
        Transition t2 = new Transition("2", "2");
        t2.setPriority(1);
        net.addTransition(t1);
        net.addTransition(t2);

        Animator animator = new PetriNetAnimator(net);
        Collection<Transition> enabled = animator.getEnabledTransitions();
        assertEquals(1, enabled.size());
        assertThat(enabled).containsExactly(t1);
    }


    @Test
    public void correctlyDoesNotEnableTransitionsIfPlaceCapacityIsFull() throws PetriNetComponentNotFoundException {
        PetriNet petriNet = createSimplePetriNet(2);
        Token token = petriNet.getComponent("Default", Token.class);

        Place p1 = petriNet.getComponent("P1", Place.class);
        p1.setTokenCount(token, 2);
        Place p2 = petriNet.getComponent("P2", Place.class);
        p2.setCapacity(1);

        Transition transition = petriNet.getComponent("T1", Transition.class);

        Animator animator = new PetriNetAnimator(petriNet);
        Collection<Transition> enabled = animator.getEnabledTransitions();
        assertThat(enabled).doesNotContain(transition);
    }


    @Test
    public void correctlyEnablesTransitionIfSelfLoop() throws PetriNetComponentNotFoundException {
        PetriNet petriNet = createSelfLoopPetriNet("1");
        Place place = petriNet.getComponent("P0", Place.class);
        Token token = petriNet.getComponent("Default", Token.class);
        place.setTokenCount(token, 1);
        place.setCapacity(1);

        Animator animator = new PetriNetAnimator(petriNet);
        Transition transition = petriNet.getComponent("T1", Transition.class);
        Collection<Transition> enabled = animator.getEnabledTransitions();
        assertThat(enabled).containsExactly(transition);
    }

    @Test
    public void correctlyIncrementsTokenCountInSelfLoop() throws PetriNetComponentNotFoundException {
        PetriNet petriNet = createSelfLoopPetriNet("1");
        Place place = petriNet.getComponent("P0", Place.class);
        Token token = petriNet.getComponent("Default", Token.class);
        place.setTokenCount(token, 1);
        place.setCapacity(1);

        Animator animator = new PetriNetAnimator(petriNet);
        Transition transition = petriNet.getComponent("T1", Transition.class);
        animator.fireTransition(transition);
        assertEquals(1, place.getTokenCount(token));
    }


    @Test
    public void firingFunctionalTransitionMovesTokens() throws PetriNetComponentNotFoundException {
        PetriNet petriNet = APetriNet.with(AToken.called("Red").withColor(Color.RED)).and(
                AToken.called("Default").withColor(Color.BLACK)).and(
                APlace.withId("P0").containing(5, "Default").tokens()).and(APlace.withId("P1")).and(
                ATransition.withId("T1")).and(
                ANormalArc.withSource("P0").andTarget("T1").with("#(P0)", "Default").tokens()).andFinally(
                ANormalArc.withSource("T1").andTarget("P1").with("#(P0)*2", "Red").tokens());

        Token token = petriNet.getComponent("Default", Token.class);
        Token redToken = petriNet.getComponent("Red", Token.class);
        Place p1 = petriNet.getComponent("P0", Place.class);
        Place p2 = petriNet.getComponent("P1", Place.class);
        Transition transition = petriNet.getComponent("T1", Transition.class);

        Animator animator = new PetriNetAnimator(petriNet);
        animator.fireTransition(transition);

        assertEquals(0, p1.getTokenCount(token));
        assertEquals(10, p2.getTokenCount(redToken));
    }

    @Test
    public void firingTransitionDoesNotDisableTransition() throws PetriNetComponentNotFoundException {
        int tokenWeight = 1;
        PetriNet petriNet = createSimplePetriNet(tokenWeight);
        Place place = petriNet.getComponent("P1", Place.class);
        Token token = petriNet.getComponent("Default", Token.class);
        place.setTokenCount(token, 2);

        Transition transition = petriNet.getComponent("T1", Transition.class);
        Animator animator = new PetriNetAnimator(petriNet);
        animator.fireTransition(transition);


        Collection<Transition> enabled = animator.getEnabledTransitions();
        assertThat(enabled).contains(transition);
    }

    @Test
    public void firingTransitionEnablesNextTransition() throws PetriNetComponentNotFoundException {
        PetriNet petriNet = APetriNet.with(AToken.called("Default").withColor(Color.BLACK)).and(
                APlace.withId("P1").containing(1, "Default").token()).and(APlace.withId("P2")).and(
                ATransition.withId("T1")).and(ATransition.withId("T2")).and(
                ANormalArc.withSource("P1").andTarget("T1").with("1", "Default").token()).and(
                ANormalArc.withSource("T1").andTarget("P2").with("1", "Default").token()).andFinally(
                ANormalArc.withSource("P2").andTarget("T2").with("1", "Default").token());

        Animator animator = new PetriNetAnimator(petriNet);
        Transition transition = petriNet.getComponent("T1", Transition.class);
        animator.fireTransition(transition);

        Transition transition2 = petriNet.getComponent("T2", Transition.class);

        Collection<Transition> enabled = animator.getEnabledTransitions();
        assertThat(enabled).contains(transition2);
    }

    @Test
    public void firingTransitionBackwardMovesTokensBack() throws PetriNetComponentNotFoundException {
        PetriNet petriNet = APetriNet.with(AToken.called("Default").withColor(Color.BLACK)).and(
                APlace.withId("P1").containing(0, "Default").token()).and(ATransition.withId("T1")).andFinally(
                ANormalArc.withSource("P1").andTarget("T1").with("1", "Default").token());


        Transition transition = petriNet.getComponent("T1", Transition.class);
        Animator animator = new PetriNetAnimator(petriNet);
        animator.fireTransitionBackwards(transition);

        Place place = petriNet.getComponent("P1", Place.class);
        Token token = petriNet.getComponent("Default", Token.class);

        assertThat(place.getTokenCount(token)).isEqualTo(1);
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
        Animator animator = new PetriNetAnimator(petriNet);
        Collection<Transition> enabled = animator.getEnabledTransitions();
        assertThat(enabled).contains(transition);
    }



    @Test
    public void correctlyEnablesTransitionEvenAfterFiring() throws PetriNetComponentNotFoundException {
        PetriNet petriNet = createSimpleInhibitorPetriNet(1);
        Transition transition = petriNet.getComponent("T1", Transition.class);

        Animator animator = new PetriNetAnimator(petriNet);
        animator.fireTransition(transition);
        Collection<Transition> enabled = animator.getEnabledTransitions();
        assertThat(enabled).contains(transition);
    }



    @Test
    public void firingTransitionMovesToken() throws PetriNetComponentNotFoundException {
        int tokenWeight = 1;
        PetriNet petriNet = createSimplePetriNet(tokenWeight);

        Transition transition = petriNet.getComponent("T1", Transition.class);

        Animator animator = new PetriNetAnimator(petriNet);
        animator.fireTransition(transition);

        Token token = petriNet.getComponent("Default", Token.class);
        Place p1 = petriNet.getComponent("P1", Place.class);
        Place p2 = petriNet.getComponent("P2", Place.class);
        assertEquals(0, p1.getTokenCount(token));
        assertEquals(1, p2.getTokenCount(token));
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
    public void firingTransitionDisablesTransition() throws PetriNetComponentNotFoundException {
        int tokenWeight = 1;
        PetriNet petriNet = createSimplePetriNet(tokenWeight);

        Transition transition = petriNet.getComponent("T1", Transition.class);
        Animator animator = new PetriNetAnimator(petriNet);
        animator.fireTransition(transition);

        Collection<Transition> enabled = animator.getEnabledTransitions();
        assertThat(enabled).doesNotContain(transition);
    }


    @Test
    public void firingTransitionBackwardEnablesTransition() throws PetriNetComponentNotFoundException {
        PetriNet petriNet =
                APetriNet.with(AToken.called("Default").withColor(Color.BLACK)).and(APlace.withId("P1")).and(
                        APlace.withId("P2").containing(1, "Default").token()).and(ATransition.withId("T1")).andFinally(
                        ANormalArc.withSource("P1").andTarget("T1").with("1", "Default").token());

        Transition transition = petriNet.getComponent("T1", Transition.class);
        Animator animator = new PetriNetAnimator(petriNet);
        animator.fireTransitionBackwards(transition);

        Collection<Transition> enabled = animator.getEnabledTransitions();
        assertThat(enabled).contains(transition);
    }

    @Test
    public void restoresPetriNet() {
        PetriNet petriNet = createSimplePetriNet(1);
        PetriNet copy = ClonePetriNet.clone(petriNet);

        Animator animator = new PetriNetAnimator(petriNet);
        animator.fireTransition(animator.getRandomEnabledTransition());

        animator.reset();
        assertEquals(copy, petriNet);
    }

}
