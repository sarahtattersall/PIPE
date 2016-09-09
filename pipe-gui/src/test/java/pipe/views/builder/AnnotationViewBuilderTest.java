package pipe.views.builder;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import pipe.actions.gui.PipeApplicationModel;
import pipe.controllers.AnnotationViewBuilder;
import pipe.controllers.PetriNetController;
import pipe.gui.PetriNetTab;
import pipe.views.AnnotationView;
import uk.ac.imperial.pipe.models.petrinet.Annotation;
import uk.ac.imperial.pipe.models.petrinet.AnnotationImpl;

@RunWith(MockitoJUnitRunner.class)
public class AnnotationViewBuilderTest {
    private static final double DOUBLE_DELTA = 0.001;
    Annotation annotation;
    AnnotationViewBuilder builder;
    @Mock
    PetriNetController mockController;
    @Mock
    private PipeApplicationModel model;

    @Mock
    PetriNetTab parent;


    @Before
    public void setUp() {
        annotation = new AnnotationImpl(10, 10, "annotation", 1, 3, true);
        builder = new AnnotationViewBuilder(annotation, mockController);
    }

    @Test
    public void correctlySetsModel()
    {
        AnnotationView annotationView = builder.build(parent, model);
        assertEquals(annotation, annotationView.getModel());
    }
}
