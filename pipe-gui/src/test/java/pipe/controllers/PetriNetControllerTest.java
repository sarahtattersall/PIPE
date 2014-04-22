package pipe.controllers;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.exceptions.PetriNetComponentException;
import pipe.exceptions.PetriNetComponentNotFoundException;
import pipe.gui.*;
import pipe.historyActions.component.DeletePetriNetObject;
import pipe.models.component.AbstractPetriNetComponent;
import pipe.models.component.Connectable;
import pipe.models.component.PetriNetComponent;
import pipe.models.component.arc.Arc;
import pipe.models.component.arc.ArcPoint;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.PetriNet;
import pipe.views.PipeApplicationView;
import pipe.visitor.TranslationVisitor;
import pipe.visitor.component.PetriNetComponentVisitor;

import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEdit;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PetriNetControllerTest {
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    private PetriNetController controller;

    private PetriNet net;

    @Mock
    private Animator mockAnimator;

    @Mock
    private PetriNetTab mocKTab;

    @Mock
    UndoableEditListener undoListener;

    @Before
    public void setUp() {
        net = new PetriNet();
        CopyPasteManager copyPasteManager = mock(CopyPasteManager.class);
        ZoomController zoomController = mock(ZoomController.class);

        controller = new PetriNetController(net, undoListener, mockAnimator, copyPasteManager, zoomController, mocKTab);

        //TODO: Remove this when you can get reid of ApplicationSettings
        // nasty staticness means that some views persist between tests.
        PipeApplicationView nullView = null;
        ApplicationSettings.register(nullView);
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
    public void deletesSelectedRemovesFromNet() throws PetriNetComponentException {
        Place place = new Place("", "");
        net.addPlace(place);

        controller.select(place);
        controller.deleteSelection();
        assertFalse(net.getPlaces().contains(place));
    }

    @Test
    public void deletingSelectionReturnsListOfAbstractUndoEdits() throws PetriNetComponentException {
        Place place = new Place("", "");
        net.addPlace(place);

        controller.select(place);
        List<UndoableEdit> edits = controller.deleteSelection();
        DeletePetriNetObject deleteAction = new DeletePetriNetObject(place, net);
        assertEquals(1, edits.size());
        assertTrue(edits.contains(deleteAction));
    }

    @Test
    public void deleteComponentRemovesFromPetriNet() throws PetriNetComponentException {
        Place place = new Place("", "");
        net.addPlace(place);

        controller.delete(place);

        assertFalse("Petrinet contains place after deletion", net.getPlaces().contains(place));
    }

    @Test
    public void deletingComponentAddsToHistoryManager() throws PetriNetComponentException {
        Place place = new Place("", "");
        net.addPlace(place);

        UndoableEdit edit = controller.delete(place);

        DeletePetriNetObject deleteAction = new DeletePetriNetObject(place, net);
        assertEquals(edit, deleteAction);
    }

    @Test
    public void deletesSelectedNotifiesObserver() throws PetriNetComponentException {
        Place place = new Place("", "");
        net.addPlace(place);

        PropertyChangeListener mockListener = mock(PropertyChangeListener.class);
        net.addPropertyChangeListener(mockListener);

        controller.select(place);
        controller.deleteSelection();
        verify(mockListener, atLeastOnce()).propertyChange(any(PropertyChangeEvent.class));
    }

    @Test
    public void selectsItemLocatedWithinSelectionArea() {
        Place place = mock(Place.class);
        when(place.getX()).thenReturn(5);
        when(place.getY()).thenReturn(10);
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
        when(place.getX()).thenReturn(0);
        when(place.getY()).thenReturn(0);
        when(place.getWidth()).thenReturn(10);
        when(place.getHeight()).thenReturn(10);

        net.addPlace(place);

        Rectangle selectionRectangle = new Rectangle(5, 5, 40, 40);
        controller.select(selectionRectangle);

        assertTrue(controller.isSelected(place));
    }

    @Test
    public void selectsArcIfIntersects() {
        Arc<? extends Connectable, ? extends Connectable> arc = mock(Arc.class);
        Point2D.Double start = new Point2D.Double(0, 0);
        Point2D.Double end = new Point2D.Double(10, 10);
        when(arc.getArcPoints()).thenReturn(Arrays.asList(new ArcPoint(start, false), new ArcPoint(end, false)));

        net.addArc(arc);

        Rectangle selectionRectangle = new Rectangle(0, 0, 2, 2);
        controller.select(selectionRectangle);
        assertTrue(controller.isSelected(arc));
    }

    @Test
    public void doesNotSelectArcIfDoesntIntersect() {
        Arc<? extends Connectable, ? extends Connectable> arc = mock(Arc.class);
        Point2D.Double start = new Point2D.Double(0, 0);
        Point2D.Double end = new Point2D.Double(10, 10);
        when(arc.getArcPoints()).thenReturn(Arrays.asList(new ArcPoint(start, false), new ArcPoint(end, false)));

        net.addArc(arc);

        Rectangle selectionRectangle = new Rectangle(30, 30, 40, 40);
        controller.select(selectionRectangle);
        assertFalse(controller.isSelected(arc));
    }

    @Test
    public void translatesSelectedItemsCorrectly() throws PetriNetComponentException {
        Transition transition = mock(Transition.class);
        when(transition.isDraggable()).thenReturn(true);
        Place place = mock(Place.class);
        when(place.isDraggable()).thenReturn(true);
        net.addPlace(place);
        net.addTransition(transition);

        controller.select(place);
        controller.select(transition);

        int x_y_value = 40;
        when(place.getX()).thenReturn(x_y_value);
        when(place.getY()).thenReturn(x_y_value);
        when(transition.getX()).thenReturn(x_y_value);
        when(transition.getY()).thenReturn(x_y_value);

        int translate_value = 50;
        controller.translateSelected(new Point(translate_value, translate_value));

        double expected_value = x_y_value + translate_value;
        verify(place).accept(any(TranslationVisitor.class));
        verify(transition).accept(any(TranslationVisitor.class));
    }

    @Test
    public void doesNotTranslateNonDraggableItems() throws PetriNetComponentException {
        PetriNetComponent petriNetComponent = mock(PetriNetComponent.class);
        when(petriNetComponent.isDraggable()).thenReturn(false);
        controller.select(petriNetComponent);
        controller.translateSelected(new Point(23, 58));
        verify(petriNetComponent, never()).accept(any(TranslationVisitor.class));
    }

    @Test
    public void updateNameIfChanged() throws PetriNetComponentNotFoundException {
        Token token = mock(Token.class);
        String id = "id";
        when(token.getId()).thenReturn(id);

        boolean enabled = true;

        Color color = Color.RED;
        when(token.getColor()).thenReturn(color);
        net.addToken(token);

        controller.updateToken(id, "new", color);
        verify(token).setId("new");
    }

    @Test
    public void doesNotUpdateNameIfNotChanged() throws PetriNetComponentNotFoundException {
        Token token = mock(Token.class);
        String id = "id";
        when(token.getId()).thenReturn(id);

        boolean enabled = true;

        Color color = new Color(255, 0, 0);
        when(token.getColor()).thenReturn(color);
        net.addToken(token);

        controller.updateToken(id, id, color);
        verify(token, never()).setId(anyString());
    }

    @Test
    public void updateTokenColorIfChanged() throws PetriNetComponentNotFoundException {
        Token token = mock(Token.class);
        String id = "id";
        when(token.getId()).thenReturn(id);

        boolean enabled = true;

        Color color = new Color(255, 0, 0);
        when(token.getColor()).thenReturn(color);
        net.addToken(token);

        Color newColor = new Color(0, 0, 0);
        controller.updateToken(id, id, newColor);
        verify(token).setColor(newColor);
    }

    @Test
    public void doesNotUpdateTokenColorIfNotChanged() throws PetriNetComponentNotFoundException {
        Token token = mock(Token.class);
        String id = "id";
        when(token.getId()).thenReturn(id);

        boolean enabled = true;

        Color color = new Color(255, 0, 0);
        when(token.getColor()).thenReturn(color);
        net.addToken(token);

        controller.updateToken(id, id, color);
        verify(token, never()).setColor(any(Color.class));
    }

    @Test
    public void createNewToken() {
        String name = "testToken";
        boolean enabled = true;
        Color color = new Color(160, 92, 240);

        controller.createNewToken(name, color);
        Collection<Token> tokens = net.getTokens();
        assertEquals(1, tokens.size());
        Token token = tokens.iterator().next();
        assertEquals(name, token.getId());
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
