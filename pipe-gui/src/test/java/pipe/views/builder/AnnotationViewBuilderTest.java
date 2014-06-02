package pipe.views.builder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.controllers.AnnotationViewBuilder;
import pipe.controllers.PetriNetController;
import pipe.gui.PetriNetTab;
import pipe.gui.model.PipeApplicationModel;
import pipe.views.AnnotationView;
import uk.ac.imperial.pipe.models.petrinet.Annotation;
import uk.ac.imperial.pipe.models.petrinet.AnnotationImpl;

import static org.junit.Assert.assertEquals;

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

    @Test
    public void correctlySetsParameters()
    {
        AnnotationView annotationView = builder.build(parent, model);
        //TODO: Work out waht the view is doing to x & y?
//        assertEquals(annotation.getX(), annotationNote._positionX, DOUBLE_DELTA);
//        assertEquals(annotation.getY(), annotationNote._positionY, DOUBLE_DELTA);
//        assertEquals(annotation.getHeight(), annotationNote.getHeight(), DOUBLE_DELTA);
//        assertEquals(annotation.getWidth(), annotationNote.getWidth(), DOUBLE_DELTA);
        assertEquals(annotation.hasBorder(), annotationView.isShowingBorder());
        assertEquals(annotation.getText(), annotationView.getNoteText());
    }
}
