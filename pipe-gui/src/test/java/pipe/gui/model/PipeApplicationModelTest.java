package pipe.gui.model;

import matchers.component.PropertyChangeUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.actions.gui.create.CreateAction;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PipeApplicationModelTest {

    PipeApplicationModel model;

    @Mock
    PropertyChangeListener listener;

    @Before
    public void setUp() {
        model = new PipeApplicationModel("5");
        model.addPropertyChangeListener(listener);
    }

    @Test
    public void initiallyNotInAnimationMode() {
        assertFalse(model.isInAnimationMode());
    }

    @Test
    public void changeAnimationModeNotifiesListener() {
        model.setInAnimationMode(true);
        verify(listener).propertyChange(argThat(PropertyChangeUtils.hasValues(PipeApplicationModel.TOGGLE_ANIMATION_MODE, false, true)));
    }

    @Test
    public void doesNotFireChangeListenerIfAnimValuesAreTheSame() {
        model.setInAnimationMode(false);
        verify(listener, never()).propertyChange(any(PropertyChangeEvent.class));
    }

    @Test
    public void changeActionNotifiesListener() {
        CreateAction mockAction = mock(CreateAction.class);
        model.selectTypeAction(mockAction);
        verify(listener).propertyChange(argThat(PropertyChangeUtils.hasValues(PipeApplicationModel.TYPE_ACTION_CHANGE_MESSAGE, null, mockAction)));
    }


}
