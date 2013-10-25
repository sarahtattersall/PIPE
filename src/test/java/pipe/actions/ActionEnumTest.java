package pipe.actions;

import org.junit.Before;
import org.junit.Test;
import pipe.models.PipeApplicationModel;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class ActionEnumTest {

    PipeApplicationModel mockModel;

    @Before
    public void setUp()
    {
        mockModel = mock(PipeApplicationModel.class);
    }

    @Test
    public void testCreateActionGet() throws Exception {
        ActionEnum createEnum = ActionEnum.CREATE;
        createEnum.get(mockModel);

        verify(mockModel).getCreateAction();
    }

    @Test
    public void testOpenActionGet() throws Exception {
        ActionEnum createEnum = ActionEnum.OPEN;
        createEnum.get(mockModel);

        verify(mockModel).getOpenAction();
    }
}
