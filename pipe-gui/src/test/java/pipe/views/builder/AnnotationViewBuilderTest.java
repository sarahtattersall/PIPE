package pipe.views.builder;

import org.junit.Before;
import org.junit.Test;
import pipe.controllers.PetriNetController;
import pipe.views.viewComponents.AnnotationView;
import uk.ac.imperial.pipe.models.component.annotation.Annotation;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class AnnotationViewBuilderTest {
    private static final double DOUBLE_DELTA = 0.001;
    Annotation annotation;
    AnnotationViewBuilder builder;
    PetriNetController mockController;

    @Before
    public void setUp() {
        mockController = mock(PetriNetController.class);
        annotation = new Annotation(10, 10, "annotation", 1, 3, true);
        builder = new AnnotationViewBuilder(annotation, mockController);
    }

    @Test
    public void correctlySetsModel()
    {
        AnnotationView annotationView = builder.build();
        assertEquals(annotation, annotationView.getModel());
    }

    @Test
    public void correctlySetsParameters()
    {
        AnnotationView annotationView = builder.build();
        //TODO: Work out waht the view is doing to x & y?
//        assertEquals(annotation.getX(), annotationNote._positionX, DOUBLE_DELTA);
//        assertEquals(annotation.getY(), annotationNote._positionY, DOUBLE_DELTA);
//        assertEquals(annotation.getHeight(), annotationNote.getHeight(), DOUBLE_DELTA);
//        assertEquals(annotation.getWidth(), annotationNote.getWidth(), DOUBLE_DELTA);
        assertEquals(annotation.hasBoarder(), annotationView.isShowingBorder());
        assertEquals(annotation.getText(), annotationView.getNoteText());
    }
}
