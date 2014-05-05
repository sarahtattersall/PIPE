package pipe.reachability.algorithm.sequential;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.reachability.algorithm.state.SerializingStateWriter;
import pipe.reachability.io.WriterFormatter;
import pipe.reachability.state.ExplorerState;

import java.io.IOException;
import java.io.ObjectOutputStream;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SerializingExplorerStateWriterTest {

    private SerializingStateWriter explorer;

    @Mock
    WriterFormatter formatter;

    @Mock
    ObjectOutputStream writer;

    @Mock
    ExplorerState previous;

    @Mock
    ExplorerState state;

    @Before
    public void setUp() {
        explorer = new SerializingStateWriter(formatter, writer);
    }

    @Test
    public void doesNotWriteIfPreviousIsNull() throws IOException {
        explorer.transition(null, state, 1.0);
        verify(formatter, never()).write(any(ExplorerState.class), any(ExplorerState.class), anyDouble(), any(ObjectOutputStream.class));
    }

    @Test
    public void writesToWriterIfPreviousNotNull() throws IOException {
        explorer.transition(previous, state, 1.0);
        verify(formatter).write(previous, state, 1.0, writer);
    }


    @Test
    public void noInteractionsWithMocks() {
        explorer.transition(previous, state, 1.0);
        verifyNoMoreInteractions(previous, state);
    }


}