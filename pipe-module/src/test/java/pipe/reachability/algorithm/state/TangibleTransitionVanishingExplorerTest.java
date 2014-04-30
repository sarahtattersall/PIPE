package pipe.reachability.algorithm.state;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.models.component.transition.Transition;
import pipe.reachability.algorithm.ExplorerUtilities;
import pipe.reachability.algorithm.TimelessTrapException;
import pipe.reachability.state.State;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TangibleTransitionVanishingExplorerTest {
    @Mock
    StateExplorer tangible;

    @Mock
    StateExplorer vanishing;


    @Mock
    ExplorerUtilities utilities;


    @Mock
    State tangibleState;

    @Mock
    State vanishingState;

    private State setupSuccessor() {
        State successor = mock(State.class);
        Map<State, Collection<Transition>> successors = new HashMap<>();
        successors.put(successor, Arrays.asList(mock(Transition.class)));
        when(utilities.getSuccessors(vanishingState)).thenReturn(successors);
        return successor;
    }

    @Test
    public void exploresVanishingToVanishingState() throws TimelessTrapException {
        State successor = setupSuccessor();
        when(successor.isTangible()).thenReturn(false);

        TangibleTransitionVanishingExplorer explorer = new TangibleTransitionVanishingExplorer(tangible, vanishing, utilities);
        explorer.explore(tangibleState, vanishingState, 1.0);
        verify(vanishing).explore(vanishingState, successor, 1.0);
    }


    @Test
    public void exploresVanishingToTangible() throws TimelessTrapException {
        State successor = setupSuccessor();
        when(successor.isTangible()).thenReturn(true);

        TangibleTransitionVanishingExplorer explorer = new TangibleTransitionVanishingExplorer(tangible, vanishing, utilities);
        explorer.explore(tangibleState, vanishingState, 1.0);
        verify(tangible).explore(tangibleState, successor, 1.0);
    }
}