package pipe.controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.historyActions.component.ChangePetriNetComponentName;
import pipe.historyActions.rateparameter.ChangeRateParameterRate;
import pipe.utilities.transformers.Contains;
import uk.ac.imperial.pipe.exceptions.InvalidRateException;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;
import uk.ac.imperial.pipe.models.petrinet.RateParameter;
import uk.ac.imperial.pipe.parsers.FunctionalResults;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEdit;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RateParameterControllerTest {
    @Mock
    RateParameter rateParameter;

    @Mock
    UndoableEditListener listener;

    @Mock
    PetriNet petriNet;

    RateParameterController rateParameterController;
    @Before
    public void setUp() { rateParameterController = new RateParameterController(rateParameter, petriNet, listener);}

    @Test
    public void setRateModifiesRate() throws InvalidRateException {
        String oldRate = "5";
        String newRate = "10";
        when(rateParameter.getExpression()).thenReturn(oldRate);
        when(petriNet.parseExpression(newRate)).thenReturn(new FunctionalResults<>(10.0, new HashSet<String>()));

        rateParameterController.setRate(newRate);
        verify(rateParameter).setExpression(newRate);
    }

    @Test
    public void checksForInvalidRate() throws InvalidRateException {
        String oldRate = "5";
        String newRate = "10";
        when(rateParameter.getExpression()).thenReturn(oldRate);
        when(petriNet.parseExpression(newRate)).thenReturn(new FunctionalResults<>(10.0, new HashSet<String>()));

        rateParameterController.setRate(newRate);
        verify(petriNet).parseExpression(newRate);
    }


    @Test
    public void throwsInvalidRateException() {
        String oldRate = "5";
        String newRate = "fhajsh";
        when(rateParameter.getExpression()).thenReturn(oldRate);
        when(petriNet.parseExpression(newRate)).thenReturn(new FunctionalResults<Double>(-1., Arrays.asList("error"), new HashSet<String>()));
        try {
            rateParameterController.setRate(newRate);
        } catch (InvalidRateException e) {
            return;
        }
        fail("Did not throw InvalidRateException for an invalid rate!");
    }

    @Test
    public void setRateCreatesUndoItem() throws InvalidRateException {
        String oldRate = "5";
        String newRate = "10";
        when(rateParameter.getExpression()).thenReturn(oldRate);
        when(petriNet.parseExpression(newRate)).thenReturn(new FunctionalResults<>(10.0, new HashSet<String>()));
        rateParameterController.setRate(newRate);

        UndoableEdit changed = new ChangeRateParameterRate(rateParameter, oldRate, newRate);
        verify(listener).undoableEditHappened(argThat(Contains.thisAction(changed)));
    }

    @Test
    public void setRateDoesNotModifiyRateIfEqual() throws InvalidRateException {
        String oldRate = "5";
        String newRate = "5";
        when(rateParameter.getExpression()).thenReturn(oldRate);
        rateParameterController.setRate(newRate);
        verify(rateParameter, never()).setExpression(anyString());
    }


    @Test
    public void setRateDoesCreatesUndoItemIfRateEqual() throws InvalidRateException {
        String oldRate = "5";
        String newRate = "5";
        when(rateParameter.getExpression()).thenReturn(oldRate);
        rateParameterController.setRate(newRate);

        verify(listener, never()).undoableEditHappened(any(UndoableEditEvent.class));
    }

    @Test
    public void setIdSetsId() {
        String oldId = "id";
        String newId = "id2";
        when(rateParameter.getId()).thenReturn(oldId);

        rateParameterController.setId(newId);
        verify(rateParameter).setId(newId);
    }

    @Test
    public void setIdDoesNotSetIdIfEqual() {
        String oldId = "id";
        String newId = "id";
        when(rateParameter.getId()).thenReturn(oldId);

        rateParameterController.setId(newId);
        verify(rateParameter, never()).setId(anyString());
    }


    @Test
    public void setRateDoesCreatesUndoItemIfIdEqual() {
        String oldId = "id";
        String newId = "id";
        when(rateParameter.getId()).thenReturn(oldId);

        rateParameterController.setId(newId);

        verify(listener, never()).undoableEditHappened(any(UndoableEditEvent.class));
    }

    @Test
    public void setIdCreatesUndoItem() {
        String oldId = "id";
        String newId = "id2";
        when(rateParameter.getId()).thenReturn(oldId);

        rateParameterController.setId(newId);

        UndoableEdit changed = new ChangePetriNetComponentName(rateParameter, oldId, newId);
        verify(listener).undoableEditHappened(argThat(Contains.thisAction(changed)));
    }
}