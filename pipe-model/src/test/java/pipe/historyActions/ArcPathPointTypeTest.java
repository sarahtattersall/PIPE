package pipe.historyActions;

import org.junit.Before;
import org.junit.Test;
import pipe.models.component.ArcPoint;

import static org.mockito.Mockito.*;

public class ArcPathPointTypeTest {
    ArcPathPointType historyItem;
    ArcPoint mockPoint;

    @Before
    public void setUp() {
        mockPoint  = mock(ArcPoint.class);
        historyItem = new ArcPathPointType(mockPoint);
    }

    @Test
    public void togglesArcPointTypeToStraightOnUndo() {
        when(mockPoint.isCurved()).thenReturn(true);

        historyItem.undo();
        verify(mockPoint).setCurved(false);
    }

    @Test
    public void togglesArcPointTypeToCurvedOnUndo() {
        when(mockPoint.isCurved()).thenReturn(false);

        historyItem.undo();
        verify(mockPoint).setCurved(true);
    }

    @Test
    public void togglesArcPointTypeToStraightOnRedo() {
        when(mockPoint.isCurved()).thenReturn(true);

        historyItem.redo();
        verify(mockPoint).setCurved(false);
    }

    @Test
    public void togglesArcPointTypeToCurvedOnRedo() {
        when(mockPoint.isCurved()).thenReturn(false);

        historyItem.redo();
        verify(mockPoint).setCurved(true);
    }


}
