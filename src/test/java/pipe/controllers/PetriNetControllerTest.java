package pipe.controllers;

import org.junit.Before;
import org.junit.Test;
import pipe.models.PetriNet;

import static org.junit.Assert.assertEquals;

public class PetriNetControllerTest {
    PetriNetController controller;

    @Before
    public void setUp() {
        controller = new PetriNetController();
    }

    @Test
    public void returnsUniqueNumberForDifferentTabs()
    {
        PetriNet net1 = new PetriNet();
        controller.addPetriNet(net1);
        assertEquals(0, controller.getUniquePlaceNumber());
        assertEquals(1, controller.getUniquePlaceNumber());

        PetriNet net2 = new PetriNet();
        controller.addPetriNet(net2);
        assertEquals(0, controller.getUniquePlaceNumber());
    }
}
