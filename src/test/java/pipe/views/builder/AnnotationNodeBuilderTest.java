package pipe.views.builder;

import org.junit.Before;
import org.junit.Test;
import pipe.models.component.Annotation;
import pipe.views.viewComponents.AnnotationNote;

import static org.junit.Assert.assertEquals;

public class AnnotationNodeBuilderTest {
    private static final double DOUBLE_DELTA = 0.001;
    Annotation annotation;
    AnnotationNodeBuilder builder;

    @Before
    public void setUp() {
        annotation = new Annotation(10, 10, "annotation", 1, 3, true);
        builder = new AnnotationNodeBuilder(annotation);
    }

    @Test
    public void correctlySetsModel()
    {
        AnnotationNote annotationNote = builder.build();
        assertEquals(annotation, annotationNote.getModel());
    }

    @Test
    public void correctlySetsParameters()
    {
        AnnotationNote annotationNote = builder.build();
        //TODO: Work out waht the view is doing to x & y?
//        assertEquals(annotation.getX(), annotationNote._positionX, DOUBLE_DELTA);
//        assertEquals(annotation.getY(), annotationNote._positionY, DOUBLE_DELTA);
//        assertEquals(annotation.getHeight(), annotationNote.getHeight(), DOUBLE_DELTA);
//        assertEquals(annotation.getWidth(), annotationNote.getWidth(), DOUBLE_DELTA);
        assertEquals(annotation.hasBoarder(), annotationNote.isShowingBorder());
        assertEquals(annotation.getText(), annotationNote.getNoteText());
    }
}
