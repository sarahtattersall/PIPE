package pipe.models.visitor;

import org.junit.Before;
import org.junit.Test;
import pipe.controllers.PetriNetController;
import pipe.models.component.Arc;
import pipe.models.component.ArcPoint;
import pipe.models.component.Place;
import pipe.models.component.Transition;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.*;

public class TranslationVisitorTest {
    private TranslationVisitor translationVisitor;
    private Point2D translateAmount;
    private PetriNetController mockController;

    @Before
    public void setUp() {
        translateAmount = new Point2D.Double(10, 60);
        mockController = mock(PetriNetController.class);
        translationVisitor = new TranslationVisitor(translateAmount, mockController);
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
        Point2D point = new Point2D.Double(10,5);
        when(arcPoint.getPoint()).thenReturn(point);
        points.add(arcPoint);
        when(arc.getPoints()).thenReturn(points);

        when(mockController.isSelected(place)).thenReturn(true);
        when(mockController.isSelected(transition)).thenReturn(true);


        translationVisitor.visit(arc);
        Point2D.Double expected = new Point2D.Double(point.getX() + translateAmount.getX(), point.getY() + translateAmount.getY());
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
        Point2D point = new Point2D.Double(10,5);
        when(arcPoint.getPoint()).thenReturn(point);
        points.add(arcPoint);
        when(arc.getPoints()).thenReturn(points);

        when(mockController.isSelected(place)).thenReturn(true);
        when(mockController.isSelected(transition)).thenReturn(false);


        translationVisitor.visit(arc);
        Point2D.Double expected = new Point2D.Double(point.getX() + translateAmount.getX(), point.getY() + translateAmount.getY());
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
        Point2D point = new Point2D.Double(10,5);
        when(arcPoint.getPoint()).thenReturn(point);
        points.add(arcPoint);
        when(arc.getPoints()).thenReturn(points);

        when(mockController.isSelected(place)).thenReturn(false);
        when(mockController.isSelected(transition)).thenReturn(true);


        translationVisitor.visit(arc);
        Point2D.Double expected = new Point2D.Double(point.getX() + translateAmount.getX(), point.getY() + translateAmount.getY());
        verify(arcPoint, never()).setPoint(expected);
    }

    @Test
    public void translatesPlaceCorrectly() {
        Place place = mock(Place.class);
        double x_y_value = 40.0;
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


        double x_y_value = 40.0;
        when(transition.getX()).thenReturn(x_y_value);
        when(transition.getY()).thenReturn(x_y_value);


        translationVisitor.visit(transition);

        double expectedXValue = x_y_value + translateAmount.getX();
        double expectedYValue = x_y_value + translateAmount.getY();
        verify(transition).setX(expectedXValue);
        verify(transition).setY(expectedYValue);
    }
}
