package pipe.historyActions;

import org.junit.Before;
import org.junit.Test;
import pipe.historyActions.arc.ArcPathPointType;
import pipe.models.component.arc.ArcPoint;

import static org.mockito.Mockito.*;

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
