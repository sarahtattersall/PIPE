package pipe.views;

import matchers.component.HasModel;
import org.junit.Before;
import org.junit.Test;
import pipe.controllers.PetriNetController;
import pipe.gui.ZoomController;
import pipe.models.petrinet.PetriNet;
import pipe.models.component.place.Place;

import java.util.Observer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class PetriNetViewTest {

    private PetriNetView petriNetView;

    @Before
    public void setUp() throws Exception {
        petriNetView = new PetriNetView(null, new PetriNet());
    }

    @Test
    public void verifyDefaultTokenViewCreatedDuringConstruction() throws Exception {
        assertEquals("Default created", 1, petriNetView.getTokenViews().size());
        assertEquals("Default", petriNetView.getTokenViews().get(0).getID());
    }
}
