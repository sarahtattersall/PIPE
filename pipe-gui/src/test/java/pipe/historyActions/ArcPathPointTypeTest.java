package pipe.historyActions;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import pipe.historyActions.arc.ArcPathPointType;
import uk.ac.imperial.pipe.models.petrinet.ArcPoint;

public class ArcPathPointTypeTest {
    ArcPathPointType item;
    ArcPoint mockPoint;

    @Before
    public void setUp() {
        mockPoint  = mock(ArcPoint.class);
        item = new ArcPathPointType(mockPoint);
    }

    @Test
    public void togglesArcPointTypeToStraightOnUndo() {
        when(mockPoint.isCurved()).thenReturn(true);

        item.undo();
        verify(mockPoint).setCurved(false);
    }

    @Test
    public void togglesArcPointTypeToCurvedOnUndo() {
        when(mockPoint.isCurved()).thenReturn(false);

        item.undo();
        verify(mockPoint).setCurved(true);
    }

    @Test
    public void togglesArcPointTypeToStraightOnRedo() {
        item.undo();
        when(mockPoint.isCurved()).thenReturn(true);

        item.redo();
        verify(mockPoint).setCurved(false);
    }

    @Test
    public void togglesArcPointTypeToCurvedOnRedo() {
        item.undo();
        when(mockPoint.isCurved()).thenReturn(false);

        item.redo();
        verify(mockPoint, times(2)).setCurved(true);
    }


}
