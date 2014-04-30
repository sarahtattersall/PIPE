package pipe.reachability.algorithm.sequential;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.reachability.algorithm.StateExplorer;
import pipe.reachability.state.State;

import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class NonSavingStateExplorerTest {


    @Mock
    State previous;

    @Mock
    State state;

    @Test
    public void noInteractionsWithMocks() {
        StateExplorer explorer = new NonSavingStateExplorer();
        explorer.explore(previous, state, 1.0);
        verifyNoMoreInteractions(previous, state);
    }

}