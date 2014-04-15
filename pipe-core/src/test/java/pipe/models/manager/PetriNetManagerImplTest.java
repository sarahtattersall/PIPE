package pipe.models.manager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.exceptions.PetriNetComponentNotFoundException;
import pipe.models.component.token.Token;
import pipe.models.petrinet.PetriNet;
import utils.PropertyChangeUtils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PetriNetManagerImplTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private PropertyChangeListener listener;

    private PetriNetManager manager;



    @Before
    public void setUp() {
        manager = new PetriNetManagerImpl();
        manager.addPropertyChangeListener(listener);
    }

    @Test
    public void newPetriNetNotifiesListener() {
        manager.createNewPetriNet();
        verify(listener).propertyChange(argThat(PropertyChangeUtils.hasName(PetriNetManagerImpl.NEW_PETRI_NET_MESSAGE)));
    }

    @Test
    public void newPetriNetHasDefaultToken() throws PetriNetComponentNotFoundException {
        manager.createNewPetriNet();
        PetriNet petriNet = manager.getLastNet();
        assertNotNull(petriNet.getComponent("Default", Token.class));
    }

    @Test
    public void throwsRuntimeExpcetionIfNoPetriNets() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("No Petri nets stored in the manager");
        manager.getLastNet();
    }



}
