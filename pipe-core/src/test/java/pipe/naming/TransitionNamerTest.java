package pipe.naming;

import org.junit.Before;
import org.junit.Test;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.PetriNet;

import static org.junit.Assert.*;

public class TransitionNamerTest {
    PetriNet petriNet;

    TransitionNamer transitionNamer;

    @Before
    public void setUp() {
        petriNet = new PetriNet();
        transitionNamer = new TransitionNamer(petriNet);
    }

    @Test
    public void firstPlaceIsZero() {
        String actual = transitionNamer.getName();
        assertEquals("T0", actual);
    }

    @Test
    public void returnP0IfPlaceNotCreated() {
        String first = transitionNamer.getName();
        assertEquals("T0", first);
        String second = transitionNamer.getName();
        assertEquals("T0", second);
    }

    @Test
    public void returnP0IfPlacesDontConflict() {
        Transition transition = new Transition("T1", "T1");
        petriNet.addTransition(transition);
        String actual = transitionNamer.getName();
        assertEquals("T0", actual);
    }

    @Test
    public void returnNextValueIfTwoPlacesExist() {
        addNConsecutiveTransitions(2);
        String actual = transitionNamer.getName();
        assertEquals("T2", actual);
    }

    private void addNConsecutiveTransitions(int n) {
        for (int i = 0; i < n; i++) {
            String id = "T" + i;
            Transition transition = new Transition(id, id);
            petriNet.addTransition(transition);
        }
    }

    @Test
    public void returnNextValueIfFourPlacesExist() {
        addNConsecutiveTransitions(4);
        String actual = transitionNamer.getName();
        assertEquals("T4", actual);
    }

    @Test
    public void returnMiddleValue() {
        Transition transition = new Transition("T0", "T0");
        Transition transition2 = new Transition("T2", "T2");
        petriNet.addTransition(transition);
        petriNet.addTransition(transition2);

        String actual = transitionNamer.getName();
        assertEquals("T1", actual);
    }

    @Test
    public void reUseDeletedValue() {
        Transition transition = new Transition("T0", "T0");
        petriNet.addTransition(transition);

        String actual = transitionNamer.getName();
        assertEquals("T1", actual);
        petriNet.removeTransition(transition);

        String actual2 = transitionNamer.getName();
        assertEquals("T0", actual2);
    }

    /**
     * Since the TransitionNamer works via listening for change events
     * we need to make sure if Transition exist in the petrinet on construction
     * it still is aware of their names.
     */
    @Test
    public void returnCorrectValueAfterConsturctor() {
        Transition transition = new Transition("T0", "T0");
        petriNet.addTransition(transition);
        petriNet.addTransition(transition);

        TransitionNamer newNamer = new TransitionNamer(petriNet);

        String actual = newNamer.getName();
        assertEquals("T1", actual);
    }

    @Test
    public void identifiesNonUniqueName() {
        String name = "Transition 0";
        Transition transition = new Transition(name, name);
        petriNet.addTransition(transition);
        TransitionNamer newNamer = new TransitionNamer(petriNet);
        assertFalse(newNamer.isUniqueName(name));
    }


    @Test
    public void identifiesUniqueName() {
        String name = "Transition 0";
        Transition transition = new Transition(name, name);
        petriNet.addTransition(transition);
        TransitionNamer newNamer = new TransitionNamer(petriNet);
        assertTrue(newNamer.isUniqueName("Transition 1"));
    }

    @Test
    public void observesTransitionNameChanges() {
        String orignalId = "Transition 0";
        Transition transition = new Transition(orignalId, orignalId);
        petriNet.addTransition(transition);
        UniqueNamer newNamer = new TransitionNamer(petriNet);
        String newId = "Transition 1";
        transition.setId(newId);
        assertFalse(newNamer.isUniqueName(newId));
        assertTrue(newNamer.isUniqueName(orignalId));
    }
}
