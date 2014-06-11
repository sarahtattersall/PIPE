package pipe.views.builder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.actions.gui.PipeApplicationModel;
import pipe.controllers.PetriNetController;
import pipe.controllers.TransitionViewBuilder;
import pipe.gui.PetriNetTab;
import pipe.views.TransitionView;
import uk.ac.imperial.pipe.models.petrinet.DiscreteTransition;
import uk.ac.imperial.pipe.models.petrinet.Transition;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class TransitionViewBuilderTest {
    private static final double DOUBLE_DELTA = 0.001;
    Transition transition;
    TransitionViewBuilder builder;

    @Mock
    PetriNetTab parent;


    @Mock
    private PetriNetController mockController;


    @Mock
    private PipeApplicationModel model;

    @Before
    public void setUp()
    {
        transition = new DiscreteTransition("id", "name");
        builder = new TransitionViewBuilder(transition, mockController);
    }

    @Test
    public void correctlySetsModel()
    {
        TransitionView transitionView = builder.build(parent, model);
        assertEquals(transition, transitionView.getModel());
    }

}
