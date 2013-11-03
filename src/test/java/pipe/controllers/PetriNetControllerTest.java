package pipe.controllers;

import org.junit.Before;
import org.junit.Test;
import pipe.gui.ApplicationSettings;
import pipe.gui.PetriNetTab;
import pipe.models.*;
import pipe.models.interfaces.IObservable;
import pipe.models.interfaces.IObserver;
import pipe.views.PipeApplicationView;

import java.awt.*;
import java.awt.geom.Point2D;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.*;

public class PetriNetControllerTest {
    PetriNetController controller;

    @Before
    public void setUp() {
        controller = new PetriNetController();

        //TODO: Remove this when you can get reid of ApplicationSettings
        // nasty staticness means that some views persist between tests.
        PipeApplicationView nullView = null;
        ApplicationSettings.register(nullView);
    }

    @Test
    public void returnsUniqueNumberForDifferentTabs()
    {
        PetriNet net1 = new PetriNet();
        controller.addPetriNet(net1);
        assertEquals(0, controller.getUniquePlaceNumber());
        assertEquals(1, controller.getUniquePlaceNumber());

        PetriNet net2 = new PetriNet();
        controller.addPetriNet(net2);
        assertEquals(0, controller.getUniquePlaceNumber());
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
    public void selectsItemLocatedWithinSelectionArea() {
        Place place = mock(Place.class);
        when(place.getX()).thenReturn(5.0);
        when(place.getY()).thenReturn(10.0);
        when(place.getWidth()).thenReturn(5);
        when(place.getHeight()).thenReturn(20);


        PetriNet net = new PetriNet();
        net.addPlace(place);

        controller.addPetriNet(net);

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


        PetriNet net = new PetriNet();
        net.addPlace(place);

        controller.addPetriNet(net);

        Rectangle selectionRectangle = new Rectangle(5, 5, 40, 40);
        controller.select(selectionRectangle);

        assertTrue(controller.isSelected(place));
    }

    @Test
    public void translatesSelectedItemsCorrectly() {
        Transition transition = mock(Transition.class);
        Place place = mock(Place.class);
        PetriNet net = new PetriNet();
        net.addPlace(place);
        net.addTransition(transition);

        controller.addPetriNet(net);
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
        verify(place).setX(expected_value);
        verify(place).setY(expected_value);
        verify(transition).setX(expected_value);
        verify(transition).setY(expected_value);
    }

    private PetriNetTab createMocKTab()
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
        createMocKTab();

        PetriNet net = new PetriNet();
        controller.addPetriNet(net);
        assertEquals(0, net.getArcs().size());

        Connectable source = mock(Connectable.class);
        when(source.getX()).thenReturn(0.);
        when(source.getY()).thenReturn(0.);
        when(source.getArcEdgePoint(anyDouble())).thenReturn(new Point2D.Double());

        controller.startCreatingArc(source);
        assertEquals(1, net.getArcs().size());
    }

    @Test
    public void notifiesObserversAfterTranslation() {

        Place place = mock(Place.class);
        PetriNet net = new PetriNet();
        net.addPlace(place);

        controller.addPetriNet(net);

        IObserver mockObserver = mock(IObserver.class);
        net.registerObserver(mockObserver);

        controller.translateSelected(new Point2D.Double(5,5));
        verify(mockObserver).update();
    }

    private class DummyPetriNetComponent implements PetriNetComponent {
        @Override
        public boolean isSelectable() {
            return false;
        }

        @Override
        public boolean isDraggable() {
            return false;
        }
    }
}
