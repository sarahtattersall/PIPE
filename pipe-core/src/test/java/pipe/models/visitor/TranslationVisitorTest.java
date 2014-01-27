package pipe.models.visitor;

import org.junit.Before;
import org.junit.Test;
import pipe.models.component.PetriNetComponent;
import pipe.models.component.arc.Arc;
import pipe.models.component.arc.ArcPoint;
import pipe.models.component.place.Place;
import pipe.models.component.transition.Transition;
import pipe.visitor.TranslationVisitor;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.*;

public class TranslationVisitorTest {
    private TranslationVisitor translationVisitor;

    private Point2D translateAmount;

    private Collection<PetriNetComponent> selected;

    @Before
    public void setUp() {
        translateAmount = new Point2D.Double(10, 60);
        selected = new HashSet<PetriNetComponent>();
        translationVisitor = new TranslationVisitor(translateAmount, selected);
    }

    @Test
    public void translatesIntermediateArcPointsIfBothSourceAndTargetInSelected() {
        Place place = mock(Place.class);
        Transition transition = mock(Transition.class);
        Arc<Place, Transition> arc = mock(Arc.class);
        when(arc.getSource()).thenReturn(place);
        when(arc.getTarget()).thenReturn(transition);

        List<ArcPoint> points = new LinkedList<ArcPoint>();
        ArcPoint arcPoint = mock(ArcPoint.class);
        Point2D point = new Point2D.Double(10, 5);
        when(arcPoint.getPoint()).thenReturn(point);
        points.add(arcPoint);
        when(arc.getIntermediatePoints()).thenReturn(points);

        selected.add(place);
        selected.add(transition);


        translationVisitor.visit(arc);
        Point2D.Double expected =
                new Point2D.Double(point.getX() + translateAmount.getX(), point.getY() + translateAmount.getY());
        verify(arcPoint).setPoint(expected);
    }

    @Test
    public void doesNotTranslateIfSourceIsOnlySelected() {
        Place place = mock(Place.class);
        Transition transition = mock(Transition.class);
        Arc<Place, Transition> arc = mock(Arc.class);
        when(arc.getSource()).thenReturn(place);
        when(arc.getTarget()).thenReturn(transition);

        List<ArcPoint> points = new LinkedList<ArcPoint>();
        ArcPoint arcPoint = mock(ArcPoint.class);
        Point2D point = new Point2D.Double(10, 5);
        when(arcPoint.getPoint()).thenReturn(point);
        points.add(arcPoint);
        when(arc.getIntermediatePoints()).thenReturn(points);

        selected.add(place);

        translationVisitor.visit(arc);
        Point2D.Double expected =
                new Point2D.Double(point.getX() + translateAmount.getX(), point.getY() + translateAmount.getY());
        verify(arcPoint, never()).setPoint(expected);
    }


    @Test
    public void doesNotTranslateIfTargetsOnlySelected() {
        Place place = mock(Place.class);
        Transition transition = mock(Transition.class);
        Arc<Place, Transition> arc = mock(Arc.class);
        when(arc.getSource()).thenReturn(place);
        when(arc.getTarget()).thenReturn(transition);

        List<ArcPoint> points = new LinkedList<ArcPoint>();
        ArcPoint arcPoint = mock(ArcPoint.class);
        Point2D point = new Point2D.Double(10, 5);
        when(arcPoint.getPoint()).thenReturn(point);
        points.add(arcPoint);
        when(arc.getIntermediatePoints()).thenReturn(points);

        selected.add(transition);

        translationVisitor.visit(arc);
        Point2D.Double expected =
                new Point2D.Double(point.getX() + translateAmount.getX(), point.getY() + translateAmount.getY());
        verify(arcPoint, never()).setPoint(expected);
    }

    @Test
    public void translatesPlaceCorrectly() {
        Place place = mock(Place.class);
        int x_y_value = 40;
        when(place.getX()).thenReturn(x_y_value);
        when(place.getY()).thenReturn(x_y_value);


        translationVisitor.visit(place);

        double expectedXValue = x_y_value + translateAmount.getX();
        double expectedYValue = x_y_value + translateAmount.getY();
        verify(place).setX(expectedXValue);
        verify(place).setY(expectedYValue);
    }

    @Test
    public void translatesTransitionCorrectly() {
        Transition transition = mock(Transition.class);


        int x_y_value = 40;
        when(transition.getX()).thenReturn(x_y_value);
        when(transition.getY()).thenReturn(x_y_value);


        translationVisitor.visit(transition);

        double expectedXValue = x_y_value + translateAmount.getX();
        double expectedYValue = x_y_value + translateAmount.getY();
        verify(transition).setX(expectedXValue);
        verify(transition).setY(expectedYValue);
    }
}
