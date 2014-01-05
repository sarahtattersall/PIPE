package pipe.controllers;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pipe.gui.Animator;
import pipe.gui.ApplicationSettings;
import pipe.gui.PetriNetTab;
import pipe.historyActions.DeletePetriNetObject;
import pipe.historyActions.HistoryManager;
import pipe.models.PetriNet;
import pipe.models.component.*;
import pipe.models.interfaces.IObserver;
import pipe.models.visitor.PetriNetComponentVisitor;
import pipe.models.visitor.TranslationVisitor;
import pipe.views.PipeApplicationView;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Collection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PetriNetControllerTest {
    private PetriNetController controller;

    private PetriNet net;

    private HistoryManager mockHistoryManager;
    private Animator mockAnimator;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() {
        net = new PetriNet();
        mockHistoryManager = mock(HistoryManager.class);
        mockAnimator = mock(Animator.class);
        controller = new PetriNetController(net, mockHistoryManager, mockAnimator);

        //TODO: Remove this when you can get reid of ApplicationSettings
        // nasty staticness means that some views persist between tests.
        PipeApplicationView nullView = null;
        ApplicationSettings.register(nullView);
    }

    @Test
    public void returnsUniqueNumberForPetriNet()
    {
        assertEquals(0, controller.getUniquePlaceNumber());
        assertEquals(1, controller.getUniquePlaceNumber());
    }

    @Test
    public void containsSelected() {
        PetriNetComponent dummyComponent = new DummyPetriNetComponent();
        controller.select(dummyComponent);
        assertTrue(controller.isSelected(dummyComponent));
    }


    @Test
    public void doesNotContainDeselected() {
        PetriNetComponent dummyComponent = new DummyPetriNetComponent();
        controller.select(dummyComponent);
        controller.deselect(dummyComponent);
        assertFalse(controller.isSelected(dummyComponent));
    }

    @Test
    public void deselectAllEmptiesSelected() {
        PetriNetComponent dummyComponent = new DummyPetriNetComponent();
        controller.select(dummyComponent);
        controller.deselectAll();
        assertFalse(controller.isSelected(dummyComponent));
    }

    @Test
    public void deletesSelectedRemovesFromNet() {
        Place place = new Place("", "");
        net.addPlace(place);

        controller.select(place);
        controller.deleteSelection();
        assertFalse(net.getPlaces().contains(place));
    }

    @Test
    public void deletingSelectionAddsToHistoryManager() {
        Place place = new Place("", "");
        net.addPlace(place);

        controller.select(place);
        controller.deleteSelection();

        DeletePetriNetObject deleteAction = new DeletePetriNetObject(place, net);

        verify(mockHistoryManager).newEdit();
        verify(mockHistoryManager).addEdit(deleteAction);
    }


    @Test
    public void deleteComponentRemovesFromPetriNet() {
        Place place = new Place("", "");
        net.addPlace(place);

        controller.delete(place);

        assertFalse("Petrinet contains place after deletion", net.getPlaces().contains(place));
    }


    @Test
    public void deletingComponentAddsToHistoryManager() {
        Place place = new Place("", "");
        net.addPlace(place);

        controller.delete(place);

        DeletePetriNetObject deleteAction = new DeletePetriNetObject(place, net);

        verify(mockHistoryManager).newEdit();
        verify(mockHistoryManager).addEdit(deleteAction);
    }

    @Test
    public void deletesSelectedNotifiesObserver() {
        Place place = new Place("", "");
        net.addPlace(place);

        IObserver mockObserver = mock(IObserver.class);
        net.registerObserver(mockObserver);

        controller.select(place);
        controller.deleteSelection();
        verify(mockObserver, atLeastOnce()).update();
    }

    @Test
    public void selectsItemLocatedWithinSelectionArea() {
        Place place = mock(Place.class);
        when(place.getX()).thenReturn(5.0);
        when(place.getY()).thenReturn(10.0);
        when(place.getWidth()).thenReturn(5);
        when(place.getHeight()).thenReturn(20);

        net.addPlace(place);

        Rectangle selectionRectangle = new Rectangle(5, 10, 40, 40);
        controller.select(selectionRectangle);

        assertTrue(controller.isSelected(place));
    }

    /**
     * Even if top left x, y is out of the selection area if the height and width are in then select item
     */
    @Test
    public void selectsItemWithWidthAndHeightWithinSelectionArea() {
        Place place = mock(Place.class);
        when(place.getX()).thenReturn(0.0);
        when(place.getY()).thenReturn(0.0);
        when(place.getWidth()).thenReturn(10);
        when(place.getHeight()).thenReturn(10);

        net.addPlace(place);

        Rectangle selectionRectangle = new Rectangle(5, 5, 40, 40);
        controller.select(selectionRectangle);

        assertTrue(controller.isSelected(place));
    }

    @Test
    public void selectsArcIfIntersects() {
        Arc arc = mock(Arc.class);
        Point2D.Double start = new Point2D.Double(0,0);
        when(arc.getStartPoint()).thenReturn(start);

        Point2D.Double end = new Point2D.Double(10,10);
        when(arc.getEndPoint()).thenReturn(end);

        net.addArc(arc);

        Rectangle selectionRectangle = new Rectangle(0, 0, 2, 2);
        controller.select(selectionRectangle);
        assertTrue(controller.isSelected(arc));
    }


    @Test
    public void doesNotSelectArcIfDoesntIntersect() {
        Arc arc = mock(Arc.class);
        Point2D.Double start = new Point2D.Double(0,0);
        when(arc.getStartPoint()).thenReturn(start);

        Point2D.Double end = new Point2D.Double(10,10);
        when(arc.getEndPoint()).thenReturn(end);

        net.addArc(arc);

        Rectangle selectionRectangle = new Rectangle(30, 30, 40, 40);
        controller.select(selectionRectangle);
        assertFalse(controller.isSelected(arc));
    }

    @Test
    public void translatesSelectedItemsCorrectly() {
        Transition transition = mock(Transition.class);
        Place place = mock(Place.class);
        net.addPlace(place);
        net.addTransition(transition);

        controller.select(place);
        controller.select(transition);

        double x_y_value = 40.0;
        when(place.getX()).thenReturn(x_y_value);
        when(place.getY()).thenReturn(x_y_value);
        when(transition.getX()).thenReturn(x_y_value);
        when(transition.getY()).thenReturn(x_y_value);

        double translate_value = 50;
        controller.translateSelected(new Point2D.Double(translate_value, translate_value));

        double expected_value = x_y_value + translate_value;
        verify(place).accept(any(TranslationVisitor.class));
        verify(transition).accept(any(TranslationVisitor.class));
    }

    private PetriNetTab createMockTab()
    {
        PipeApplicationView mockView = mock(PipeApplicationView.class);
        ApplicationSettings.register(mockView);
        PetriNetTab mockTab = mock(PetriNetTab.class);
        when(mockView.getCurrentTab()).thenReturn(mockTab);
        return mockTab;
    }

    @Test
    public void creatingArcAddsItToPetriNet()
    {
        PetriNet net = setupPetriNet();
        Place source = createFakePlace();
        Token token = mock(Token.class);
        controller.startCreatingNormalArc(source, token);
        assertEquals(1, net.getArcs().size());
    }

    @Test
    public void addPointCreatesIntermediateStraightPoint()
    {
        PetriNet net = setupPetriNet();
        Place source = createFakePlace();
        Token token = mock(Token.class);
        controller.startCreatingNormalArc(source, token);
        Arc arc = net.getArcs().iterator().next();

        Point2D point = new Point2D.Double(10, 5);
        ArcPoint arcPoint = new ArcPoint(point, false);
        controller.addPoint(point, false);

        assertEquals(1, arc.getIntermediatePoints().size());
        assertEquals(arcPoint, arc.getIntermediatePoints().iterator().next());
    }

    @Test
    public void addPointCreatesIntermediateCurvedPoint()
    {
        PetriNet net = setupPetriNet();
        Place source = createFakePlace();
        Token token = mock(Token.class);
        controller.startCreatingNormalArc(source, token);
        Arc arc = net.getArcs().iterator().next();

        Point2D point = new Point2D.Double(10, 5);
        ArcPoint arcPoint = new ArcPoint(point, true);
        controller.addPoint(point, true);

        assertEquals(1, arc.getIntermediatePoints().size());
        assertEquals(arcPoint, arc.getIntermediatePoints().iterator().next());
    }

    @Test
    public void finishingArcReturnsFalseIfNotCreating() {
        Connectable place = mock(Place.class);
        assertFalse(controller.finishCreatingArc(place));
    }

    @Test
    public void finishingArcReturnsFalseIfNotValidEndPoint() {
        Place source = createFakePlace();
        Token token = mock(Token.class);
        controller.startCreatingNormalArc(source, token);

        Connectable place = mock(Place.class);
        when(place.isEndPoint()).thenReturn(true);
        assertFalse(controller.finishCreatingArc(place));
    }

    @Test
    public void finishingArcReturnsTrueIfNotValidEndPoint() {
        Place source = createFakePlace();
        Token token = mock(Token.class);
        controller.startCreatingNormalArc(source, token);

        Connectable transition = mock(Transition.class);
        when(transition.isEndPoint()).thenReturn(true);
        assertTrue(controller.finishCreatingArc(transition));
    }


    @Test
    public void finishingArcSetsCreatingToFalse() {
        Place source = createFakePlace();
        Token token = mock(Token.class);
        controller.startCreatingNormalArc(source, token);

        Connectable transition = mock(Transition.class);
        when(transition.isEndPoint()).thenReturn(true);
        controller.finishCreatingArc(transition);
        assertFalse(controller.isCurrentlyCreatingArc());
    }

    private Place createFakePlace() {
        Place source = mock(Place.class);
        when(source.getX()).thenReturn(0.);
        when(source.getY()).thenReturn(0.);
        when(source.getArcEdgePoint(anyDouble())).thenReturn(new Point2D.Double());
        return source;
    }

    private PetriNet setupPetriNet() {
        createMockTab();
        assertEquals(0, net.getArcs().size());
        return net;
    }

    @Test
    public void cancellingArcRemovesItFromPetriNet()
    {
        PetriNet net = setupPetriNet();
        Place source = createFakePlace();
        Token token = mock(Token.class);
        controller.startCreatingNormalArc(source, token);
        controller.cancelArcCreation();
        assertEquals(0, net.getArcs().size());
    }

    @Test
    public void createNewToken() {
        String name = "testToken";
        boolean enabled = true;
        Color color = new Color(160, 92, 240);

        controller.createNewToken(name, enabled, color);
        Collection<Token> tokens = net.getTokens();
        assertEquals(1, tokens.size());
        Token token  = tokens.iterator().next();
        assertEquals(name, token.getId());
        assertEquals(enabled, token.isEnabled());
        assertEquals(color, token.getColor());
    }

    private class DummyPetriNetComponent extends AbstractPetriNetComponent {
        @Override
        public boolean isSelectable() {
            return false;
        }

        @Override
        public boolean isDraggable() {
            return false;
        }

        @Override
        public void accept(PetriNetComponentVisitor visitor) {

        }

        @Override
        public String getId() {
            return "";
        }

        @Override
        public void setId(String id) {
        }

        @Override
        public void setName(String name) {
        }
    }
}
