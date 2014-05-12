package pipe.controllers;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.historyActions.MultipleEdit;
import pipe.historyActions.arc.AddArcPathPoint;
import pipe.historyActions.arc.ArcPathPointType;
import pipe.historyActions.arc.SetArcWeightAction;
import pipe.historyActions.component.ChangePetriNetComponentName;
import pipe.utilities.transformers.Contains;
import uk.ac.imperial.pipe.models.component.arc.Arc;
import uk.ac.imperial.pipe.models.component.arc.ArcPoint;
import uk.ac.imperial.pipe.models.component.place.Place;
import uk.ac.imperial.pipe.models.component.transition.Transition;
import uk.ac.imperial.pipe.parsers.FunctionalResults;
import uk.ac.imperial.pipe.parsers.UnparsableException;

import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEdit;
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

    private static final String DEFAULT_TOKEN_ID = "Default";

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
        String oldWeight = "5";
        when(mockArc.getWeightForToken(DEFAULT_TOKEN_ID)).thenReturn(oldWeight);


        String newWeight = "51";
        controller.setWeight(DEFAULT_TOKEN_ID, newWeight);

        SetArcWeightAction<Place, Transition> weightAction =
                new SetArcWeightAction<>(mockArc, DEFAULT_TOKEN_ID, oldWeight, newWeight);

        verify(listener).undoableEditHappened(argThat(Contains.thisAction(weightAction)));
    }

    @Test
    public void setWeightUpdatesArcWeight() throws UnparsableException {
        String oldWeight = "5";
        when(mockArc.getWeightForToken(DEFAULT_TOKEN_ID)).thenReturn(oldWeight);


        String newWeight = "51";
        controller.setWeight(DEFAULT_TOKEN_ID, newWeight);
        verify(mockArc).setWeight(DEFAULT_TOKEN_ID, newWeight);
    }

    @Test
    public void setWeightsCreatesHistoryItem() throws UnparsableException {
        Map<String, String> tokenWeights = new HashMap<>();
        String oldWeight = "5";
        when(mockArc.getWeightForToken(DEFAULT_TOKEN_ID)).thenReturn(oldWeight);


        String newWeight = "51";
        tokenWeights.put(DEFAULT_TOKEN_ID, newWeight);
        controller.setWeights(tokenWeights);

        UndoableEdit weightAction = new SetArcWeightAction<>(mockArc, DEFAULT_TOKEN_ID, oldWeight, newWeight);
        UndoableEdit edit = new MultipleEdit(Arrays.asList(weightAction));
        verify(listener).undoableEditHappened(argThat(Contains.thisAction(edit)));

    }

    @Test
    public void setWeightsUpdatesArc() throws UnparsableException {
        Map<String, String> tokenWeights = new HashMap<>();
        String oldWeight = "5";
        when(mockArc.getWeightForToken(DEFAULT_TOKEN_ID)).thenReturn(oldWeight);


        String newWeight = "51";
        tokenWeights.put(DEFAULT_TOKEN_ID, newWeight);
        controller.setWeights(tokenWeights);
        verify(mockArc).setWeight(DEFAULT_TOKEN_ID, newWeight);
    }

    @Test
    public void throwsUnparsableExceptionForNonIntegerWeight() throws UnparsableException {
        FunctionalResults<Double> result = new FunctionalResults<>(5.2, new HashSet<String>());
        when(mockPetriNetController.parseFunctionalExpression(anyString())).thenReturn(result);
        exception.expect(UnparsableException.class);
        exception.expectMessage("Value is not an integer, please surround expression with floor or ceil");

        controller.setWeight(DEFAULT_TOKEN_ID, "1.2");
    }


    @Test
    public void throwsUnparsableExceptionForNonIntegerWeights() throws UnparsableException {

        Map<String, String> tokenWeights = new HashMap<>();
        FunctionalResults<Double> result = new FunctionalResults<>(5.2, new HashSet<String>());
        when(mockPetriNetController.parseFunctionalExpression(anyString())).thenReturn(result);
        exception.expect(UnparsableException.class);
        exception.expectMessage("Value is not an integer, please surround expression with floor or ceil");

        tokenWeights.put(DEFAULT_TOKEN_ID, "5.2");
        controller.setWeights(tokenWeights);
    }

    @Test
    public void throwsUnparsableExceptionForErrorInWeights() throws UnparsableException {

        Map<String, String> tokenWeights = new HashMap<>();
        List<String> errors = new LinkedList<>();
        errors.add("test error");

        FunctionalResults<Double> result = new FunctionalResults<>(5.0, errors, new HashSet<String>());
        when(mockPetriNetController.parseFunctionalExpression(anyString())).thenReturn(result);
        exception.expect(UnparsableException.class);
        exception.expectMessage("test error");

        tokenWeights.put(DEFAULT_TOKEN_ID, "5.2");
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

        controller.setWeight(DEFAULT_TOKEN_ID, "5.0");
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

        UndoableEdit nameEdit = new ChangePetriNetComponentName(mockArc, oldName, newName);
        verify(listener).undoableEditHappened(argThat(Contains.thisAction(nameEdit)));
    }
}
