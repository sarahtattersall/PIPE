package pipe.reachability.algorithm.parallel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.reachability.algorithm.ExplorerUtilities;
import pipe.reachability.algorithm.StateRateRecord;
import pipe.reachability.algorithm.TimelessTrapException;
import pipe.reachability.algorithm.VanishingExplorer;
import pipe.reachability.state.State;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ParallelStateExplorerTest {

    @Mock
    CountDownLatch latch;

    @Mock
    ExplorerUtilities explorerUtilities;

    @Mock
    VanishingExplorer vanishingExplorer;

    @Mock
    State state;

    private ParallelStateExplorer explorer;

    /**
     * Successors for explorerUtilities and state
     */
    private Collection<State> successors = new LinkedList<>();

    @Before
    public void setUp() {
        explorer = new ParallelStateExplorer(latch, state, explorerUtilities, vanishingExplorer);
        when(explorerUtilities.getSuccessors(state)).thenReturn(successors);
    }

    @Test
    public void decrementsLatch() throws TimelessTrapException {
        explorer.call();
        verify(latch, times(1)).countDown();
    }

    @Test
    public void tangibleRatesAdded() throws TimelessTrapException {
        State successor = mock(State.class);
        when(successor.isTangible()).thenReturn(true);
        successors.add(successor);

        when(explorerUtilities.rate(state, successor)).thenReturn(5.0);

        Map<State, Double> results = explorer.call();

        assertEquals(1, results.size());
        assertEquals(5.0, results.get(successor), 0.001);
    }


    @Test
    public void vanishingRatesAdded() throws TimelessTrapException {
        State successor = mock(State.class);
        when(successor.isTangible()).thenReturn(false);
        successors.add(successor);

        when(explorerUtilities.rate(state, successor)).thenReturn(5.0);

        State vanishingSuccessor = mock(State.class);
        StateRateRecord rateRecord = new StateRateRecord(vanishingSuccessor, 2.5);
        when(vanishingExplorer.explore(successor, 5.0)).thenReturn(Arrays.asList(rateRecord));

        Map<State, Double> results = explorer.call();

        assertEquals(1, results.size());
        assertEquals(2.5, results.get(vanishingSuccessor), 0.001);
    }



    @Test
    public void sumsVanishingRatesForSameState() throws TimelessTrapException {
        State successor = mock(State.class);
        when(successor.isTangible()).thenReturn(false);
        successors.add(successor);

        when(explorerUtilities.rate(state, successor)).thenReturn(5.0);

        State vanishingSuccessor = mock(State.class);
        StateRateRecord rateRecord = new StateRateRecord(vanishingSuccessor, 2.5);
        StateRateRecord duplicateRateRecord = new StateRateRecord(vanishingSuccessor, 9.5);
        when(vanishingExplorer.explore(successor, 5.0)).thenReturn(Arrays.asList(rateRecord, duplicateRateRecord));

        Map<State, Double> results = explorer.call();

        assertEquals(1, results.size());
        assertEquals(12.0, results.get(vanishingSuccessor), 0.001);
    }
}