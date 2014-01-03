package pipe.models;

import org.junit.Before;
import org.junit.Test;
import pipe.models.interfaces.IObserver;

import static org.mockito.Mockito.*;

/**
 * Created with IntelliJ IDEA.
 * User: st809
 * Date: 18/10/2013
 * Time: 13:00
 * To change this template use File | Settings | File Templates.
 */
public class ObservableTest {
    TestObservable observable;
    IObserver mockObserver;

    @Before
    public void setUp()
    {
        observable = new TestObservable();
        mockObserver = mock(IObserver.class);
    }

    @Test
    public void notifiesObserver() {
        observable.registerObserver(mockObserver);
        observable.notifyObservers();
        verify(mockObserver).update();
    }

    @Test
    public void notifiesObserverOnce() {
        observable.registerObserver(mockObserver);
        observable.registerObserver(mockObserver);
        observable.notifyObservers();
        verify(mockObserver, times(1)).update();
    }

    @Test
    public void isNotNotifiedIfRemoved() {
        observable.registerObserver(mockObserver);
        observable.removeObserver(mockObserver);
        observable.notifyObservers();
        verify(mockObserver, never()).update();
    }

    private static class TestObservable extends Observable {}
}
