package pipe.views.builder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.controllers.NormalArcViewBuilder;
import pipe.controllers.PetriNetController;
import pipe.gui.PetriNetTab;
import pipe.actions.gui.PipeApplicationModel;
import pipe.views.ArcView;
import uk.ac.imperial.pipe.models.petrinet.Connectable;
import uk.ac.imperial.pipe.models.petrinet.InboundArc;
import uk.ac.imperial.pipe.models.petrinet.InboundNormalArc;
import uk.ac.imperial.pipe.models.petrinet.DiscretePlace;
import uk.ac.imperial.pipe.models.petrinet.Place;
import uk.ac.imperial.pipe.models.petrinet.DiscreteTransition;
import uk.ac.imperial.pipe.models.petrinet.Transition;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ArcViewBuilderTest {
    InboundArc arc;

    NormalArcViewBuilder builder;
    @Mock
    PetriNetTab parent;

    @Mock
    private PipeApplicationModel model;

    @Mock
    private PetriNetController mockController;

    @Before
    public void setUp() {
        Place source = new DiscretePlace("source", "source");
        Transition transition = new DiscreteTransition("id", "name");

        arc = new InboundNormalArc(source, transition, new HashMap<String, String>());
        arc.setId("id");
        builder = new NormalArcViewBuilder(arc, mockController);
    }

    @Test
    public void setsCorrectModel() {
        ArcView<Connectable, Connectable> view = builder.build(parent, model);
        assertEquals(arc, view.getModel());
    }

    @Test
    public void setsCorrectAttributes() {
        ArcView<Connectable, Connectable> view = builder.build(parent, model);
        assertEquals(arc.getId(), view.getId());
        assertEquals(arc.isTagged(), view.isTagged());
    }
}
