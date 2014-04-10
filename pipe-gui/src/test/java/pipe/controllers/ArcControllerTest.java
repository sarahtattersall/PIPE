package pipe.controllers;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.historyActions.*;
import pipe.models.component.arc.Arc;
import pipe.models.component.arc.ArcPoint;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.parsers.FunctionalResults;
import pipe.parsers.UnparsableException;
import pipe.utilities.transformers.Contains;

import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEdit;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.*;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ArcControllerTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Mock
    Arc<Place, Transition> mockArc;

    @Mock
    PetriNetController mockPetriNetController;

    ArcController<Place, Transition> controller;

    @Mock
    UndoableEditListener listener;

    @Before
    public void setUp() {
        controller = new ArcController<>(mockArc, mockPetriNetController, listener);

        FunctionalResults<Double> results = new FunctionalResults<>(1.0, new HashSet<String>());
        when(mockPetriNetController.parseFunctionalExpression(anyString())).thenReturn(results);
    }

    @Test
    public void setWeightCreatesHistoryItem() throws UnparsableException {
        Token defaultToken = new Token("Default", true, 0, new Color(0, 0, 0));
        String oldWeight = "5";
        when(mockArc.getWeightForToken(defaultToken)).thenReturn(oldWeight);


        String newWeight = "51";
        controller.setWeight(defaultToken, newWeight);

        SetArcWeightAction<Place, Transition> weightAction =
                new SetArcWeightAction<>(mockArc, defaultToken, oldWeight, newWeight);

        verify(listener).undoableEditHappened(argThat(Contains.thisAction(weightAction)));
    }

    @Test
    public void setWeightUpdatesArcWeight() throws UnparsableException {
        Token defaultToken = new Token("Default", true, 0, new Color(0, 0, 0));
        String oldWeight = "5";
        when(mockArc.getWeightForToken(defaultToken)).thenReturn(oldWeight);


        String newWeight = "51";
        controller.setWeight(defaultToken, newWeight);
        verify(mockArc).setWeight(defaultToken, newWeight);
    }

    @Test
    public void setWeightsCreatesHistoryItem() throws UnparsableException {
        Map<Token, String> tokenWeights = new HashMap<Token, String>();
        Token defaultToken = new Token("Default", true, 0, new Color(0, 0, 0));
        String oldWeight = "5";
        when(mockArc.getWeightForToken(defaultToken)).thenReturn(oldWeight);


        String newWeight = "51";
        tokenWeights.put(defaultToken, newWeight);
        controller.setWeights(tokenWeights);

        UndoableEdit weightAction = new SetArcWeightAction<>(mockArc, defaultToken, oldWeight, newWeight);
        UndoableEdit edit = new MultipleEdit(Arrays.asList(weightAction));
        verify(listener).undoableEditHappened(argThat(Contains.thisAction(edit)));

    }

    @Test
    public void setWeightsUpdatesArc() throws UnparsableException {
        Map<Token, String> tokenWeights = new HashMap<>();
        Token defaultToken = new Token("Default", true, 0, new Color(0, 0, 0));
        String oldWeight = "5";
        when(mockArc.getWeightForToken(defaultToken)).thenReturn(oldWeight);


        String newWeight = "51";
        tokenWeights.put(defaultToken, newWeight);
        controller.setWeights(tokenWeights);
        verify(mockArc).setWeight(defaultToken, newWeight);
    }

    @Test
    public void throwsUnparsableExceptionForNonIntegerWeight() throws UnparsableException {
        FunctionalResults<Double> result = new FunctionalResults<>(5.2, new HashSet<String>());
        when(mockPetriNetController.parseFunctionalExpression(anyString())).thenReturn(result);
        exception.expect(UnparsableException.class);
        exception.expectMessage("Value is not an integer, please surround expression with floor or ceil");

        Token defaultToken = new Token("Default", true, 0, new Color(0, 0, 0));
        controller.setWeight(defaultToken, "1.2");
    }


    @Test
    public void throwsUnparsableExceptionForNonIntegerWeights() throws UnparsableException {

        Map<Token, String> tokenWeights = new HashMap<>();
        FunctionalResults<Double> result = new FunctionalResults<>(5.2, new HashSet<String>());
        when(mockPetriNetController.parseFunctionalExpression(anyString())).thenReturn(result);
        exception.expect(UnparsableException.class);
        exception.expectMessage("Value is not an integer, please surround expression with floor or ceil");

        Token defaultToken = new Token("Default", true, 0, new Color(0, 0, 0));
        tokenWeights.put(defaultToken, "5.2");
        controller.setWeights(tokenWeights);
    }

    @Test
    public void throwsUnparsableExceptionForErrorInWeights() throws UnparsableException {

        Map<Token, String> tokenWeights = new HashMap<>();
        List<String> errors = new LinkedList<>();
        errors.add("test error");

        FunctionalResults<Double> result = new FunctionalResults<>(5.0, errors, new HashSet<String>());
        when(mockPetriNetController.parseFunctionalExpression(anyString())).thenReturn(result);
        exception.expect(UnparsableException.class);
        exception.expectMessage("test error");

        Token defaultToken = new Token("Default", true, 0, new Color(0, 0, 0));
        tokenWeights.put(defaultToken, "5.2");
        controller.setWeights(tokenWeights);
    }

    @Test
    public void throwsUnparsableExceptionForErrorInWeight() throws UnparsableException {
        List<String> errors = new LinkedList<>();
        errors.add("test error");

        FunctionalResults<Double> result = new FunctionalResults<>(5.0, errors, new HashSet<String>());
        when(mockPetriNetController.parseFunctionalExpression(anyString())).thenReturn(result);
        exception.expect(UnparsableException.class);
        exception.expectMessage("test error");

        Token defaultToken = new Token("Default", true, 0, new Color(0, 0, 0));
        controller.setWeight(defaultToken, "5.0");
    }

    @Test
    public void splittingArcPointSplitsHalfWay() {
        ArcPoint nextPoint = new ArcPoint(new Point2D.Double(10, 10), false);
        ArcPoint splitPoint = new ArcPoint(new Point2D.Double(0, 0), false);

        when(mockArc.getNextPoint(splitPoint)).thenReturn(nextPoint);

        controller.splitArcPoint(splitPoint);
        ArcPoint expected = new ArcPoint(new Point2D.Double(5, 5), false);
        verify(mockArc).addIntermediatePoint(expected);
    }

    @Test
    public void splittingArcPointKeepsArcUncurved() {
        ArcPoint nextPoint = new ArcPoint(new Point2D.Double(10, 10), false);
        ArcPoint splitPoint = new ArcPoint(new Point2D.Double(0, 0), false);

        when(mockArc.getNextPoint(splitPoint)).thenReturn(nextPoint);

        controller.splitArcPoint(splitPoint);
        ArcPoint expected = new ArcPoint(new Point2D.Double(5, 5), false);
        verify(mockArc).addIntermediatePoint(expected);
    }

    @Test
    public void splittingArcPointKeepsArcCurved() {
        ArcPoint nextPoint = new ArcPoint(new Point2D.Double(10, 10), false);
        ArcPoint splitPoint = new ArcPoint(new Point2D.Double(0, 0), true);

        when(mockArc.getNextPoint(splitPoint)).thenReturn(nextPoint);

        controller.splitArcPoint(splitPoint);
        ArcPoint expected = new ArcPoint(new Point2D.Double(5, 5), true);
        verify(mockArc).addIntermediatePoint(expected);
    }

    @Test
    public void splittingCreatesHistoryItem() {
        ArcPoint nextPoint = new ArcPoint(new Point2D.Double(10, 10), false);
        ArcPoint splitPoint = new ArcPoint(new Point2D.Double(0, 0), true);

        when(mockArc.getNextPoint(splitPoint)).thenReturn(nextPoint);

        controller.splitArcPoint(splitPoint);
        ArcPoint expected = new ArcPoint(new Point2D.Double(5, 5), true);
        UndoableEdit addArcPointAction = new AddArcPathPoint<>(mockArc, expected);

        verify(listener).undoableEditHappened(argThat(Contains.thisAction(addArcPointAction)));
    }

    @Test
    public void toggleCreatesHistoryItem() {
        ArcPoint point = new ArcPoint(new Point2D.Double(0, 0), false);
        controller.toggleArcPointType(point);
        ArcPathPointType arcPathPointType = new ArcPathPointType(point);
        verify(listener).undoableEditHappened(argThat(Contains.thisAction(arcPathPointType)));
    }

    @Test
    public void arcPointCreatesAtSpecifiedPoint() {
        Point2D.Double point = new Point2D.Double(4, 1);
        controller.addPoint(point);

        ArcPoint expected = new ArcPoint(point, false);
        verify(mockArc).addIntermediatePoint(expected);
    }

    @Test
    public void addPointCreatesHistoryItem() {
        ArcPoint point = new ArcPoint(new Point2D.Double(0, 0), false);
        controller.addPoint(point.getPoint());
        UndoableEdit addArcPointAction = new AddArcPathPoint<>(mockArc, point);
        verify(listener).undoableEditHappened(argThat(Contains.thisAction(addArcPointAction)));
    }


    @Test
    public void setNameChangesName() {
        String newName = "newName";
        controller.setName(newName);
        verify(mockArc).setId(newName);
        verify(mockArc).setName(newName);
    }


    @Test
    public void setNameCreatesUndoItem() {
        String oldName = "oldName";
        String newName = "newName";
        when(mockArc.getId()).thenReturn(oldName);
        controller.setName(newName);

        UndoableEdit nameEdit = new PetriNetObjectName(mockArc, oldName, newName);
        verify(listener).undoableEditHappened(argThat(Contains.thisAction(nameEdit)));
    }
}
