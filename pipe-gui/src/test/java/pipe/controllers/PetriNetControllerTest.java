package pipe.controllers;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pipe.exceptions.TokenLockedException;
import pipe.gui.*;
import pipe.historyActions.DeletePetriNetObject;
import pipe.historyActions.HistoryManager;
import pipe.models.PetriNet;
import pipe.models.component.*;
import pipe.models.visitor.PetriNetComponentVisitor;
import pipe.models.visitor.TranslationVisitor;
import pipe.views.PipeApplicationView;

import java.awt.*;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PetriNetControllerTest {
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();
    private PetriNetController controller;
    private PetriNet net;
    private HistoryManager mockHistoryManager;
    private Animator mockAnimator;

    @Before
    public void setUp() {
        net = new PetriNet();
        mockHistoryManager = mock(HistoryManager.class);
        mockAnimator = mock(Animator.class);
        CopyPasteManager copyPasteManager = mock(CopyPasteManager.class);
        ZoomController zoomController = mock(ZoomController.class);

        controller = new PetriNetController(net, mockHistoryManager, mockAnimator, copyPasteManager, zoomController);

        //TODO: Remove this when you can get reid of ApplicationSettings
        // nasty staticness means that some views persist between tests.
        PipeApplicationView nullView = null;
        ApplicationSettings.register(nullView);
    }

    @Test
    public void returnsUniqueNumberForPetriNet() {
        assertEquals(0, controller.getUniquePlaceName());
        assertEquals(1, controller.getUniquePlaceName());
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

        PropertyChangeListener mockListener = mock(PropertyChangeListener.class);
        net.addPropertyChangeListener(mockListener);

        controller.select(place);
        controller.deleteSelection();
        verify(mockListener, atLeastOnce()).propertyChange(any(PropertyChangeEvent.class));
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
        Point2D.Double start = new Point2D.Double(0, 0);
        when(arc.getStartPoint()).thenReturn(start);

        Point2D.Double end = new Point2D.Double(10, 10);
        when(arc.getEndPoint()).thenReturn(end);

        net.addArc(arc);

        Rectangle selectionRectangle = new Rectangle(0, 0, 2, 2);
        controller.select(selectionRectangle);
        assertTrue(controller.isSelected(arc));
    }

    @Test
    public void doesNotSelectArcIfDoesntIntersect() {
        Arc arc = mock(Arc.class);
        Point2D.Double start = new Point2D.Double(0, 0);
        when(arc.getStartPoint()).thenReturn(start);

        Point2D.Double end = new Point2D.Double(10, 10);
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

    @Test
    public void updateNameIfChanged() {
        Token token = mock(Token.class);
        String id = "id";
        when(token.getId()).thenReturn(id);

        boolean enabled = true;
        when(token.isEnabled()).thenReturn(enabled);

        Color color = new Color(255, 0, 0);
        when(token.getColor()).thenReturn(color);
        net.addToken(token);

        controller.updateToken(id, "new", enabled, color);
        verify(token).setId("new");
    }

    @Test
    public void doesNotUpdateNameIfNotChanged() {
        Token token = mock(Token.class);
        String id = "id";
        when(token.getId()).thenReturn(id);

        boolean enabled = true;
        when(token.isEnabled()).thenReturn(enabled);

        Color color = new Color(255, 0, 0);
        when(token.getColor()).thenReturn(color);
        net.addToken(token);

        controller.updateToken(id, id, enabled, color);
        verify(token, never()).setId(anyString());
    }

    @Test
    public void updateEnabledIfChanged() throws TokenLockedException {
        Token token = mock(Token.class);
        String id = "id";
        when(token.getId()).thenReturn(id);

        boolean enabled = true;
        when(token.isEnabled()).thenReturn(enabled);

        Color color = new Color(255, 0, 0);
        when(token.getColor()).thenReturn(color);
        net.addToken(token);

        controller.updateToken(id, id, !enabled, color);
        verify(token).setEnabled(!enabled);
    }

    @Test
    public void doesNotUpdateEnabledIfNotChanged() throws TokenLockedException {
        Token token = mock(Token.class);
        String id = "id";
        when(token.getId()).thenReturn(id);

        boolean enabled = true;
        when(token.isEnabled()).thenReturn(enabled);

        Color color = new Color(255, 0, 0);
        when(token.getColor()).thenReturn(color);
        net.addToken(token);

        controller.updateToken(id, id, enabled, color);
        verify(token, never()).setEnabled(anyBoolean());
    }

    @Test
    public void updateTokenColorIfChanged() {
        Token token = mock(Token.class);
        String id = "id";
        when(token.getId()).thenReturn(id);

        boolean enabled = true;
        when(token.isEnabled()).thenReturn(enabled);

        Color color = new Color(255, 0, 0);
        when(token.getColor()).thenReturn(color);
        net.addToken(token);

        Color newColor = new Color(0, 0, 0);
        controller.updateToken(id, id, enabled, newColor);
        verify(token).setColor(newColor);
    }

    @Test
    public void doesNotUpdateTokenColorIfNotChanged() {
        Token token = mock(Token.class);
        String id = "id";
        when(token.getId()).thenReturn(id);

        boolean enabled = true;
        when(token.isEnabled()).thenReturn(enabled);

        Color color = new Color(255, 0, 0);
        when(token.getColor()).thenReturn(color);
        net.addToken(token);

        controller.updateToken(id, id, enabled, color);
        verify(token, never()).setColor(any(Color.class));
    }

    private PetriNet setupPetriNet() {
        createMockTab();
        assertEquals(0, net.getArcs().size());
        return net;
    }

    private PetriNetTab createMockTab() {
        PipeApplicationView mockView = mock(PipeApplicationView.class);
        ApplicationSettings.register(mockView);
        PetriNetTab mockTab = mock(PetriNetTab.class);
        when(mockView.getCurrentTab()).thenReturn(mockTab);
        return mockTab;
    }

    @Test
    public void createNewToken() {
        String name = "testToken";
        boolean enabled = true;
        Color color = new Color(160, 92, 240);

        controller.createNewToken(name, enabled, color);
        Collection<Token> tokens = net.getTokens();
        assertEquals(1, tokens.size());
        Token token = tokens.iterator().next();
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
