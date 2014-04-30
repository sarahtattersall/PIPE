package pipe.reachability.algorithm.sequential;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.reachability.algorithm.state.StateExplorer;
import pipe.reachability.io.WriterFormatter;
import pipe.reachability.state.State;

import java.io.IOException;
import java.io.ObjectOutputStream;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class SavingStateExplorerTest {

    private SavingStateExplorer explorer;

    @Mock
    WriterFormatter formatter;

    @Mock
    ObjectOutputStream writer;

    @Mock
    State previous;

    @Mock
    State state;

    @Before
    public void setUp() {
        explorer = new SavingStateExplorer(formatter, writer);
    }

    @Test
    public void doesNotWriteIfPreviousIsNull() throws IOException {
        explorer.explore(null, state, 1.0);
        verify(formatter, never()).write(any(State.class), any(State.class), anyDouble(), any(ObjectOutputStream.class));
    }

    @Test
    public void writesToWriterIfPreviousNotNull() throws IOException {
        explorer.explore(previous, state, 1.0);
        verify(formatter).write(previous, state, 1.0, writer);
    }


    @Test
    public void noInteractionsWithMocks() {
        StateExplorer explorer = new NonSavingStateExplorer();
        explorer.explore(previous, state, 1.0);
        verifyNoMoreInteractions(previous, state);
    }


}