package pipe.models;

import org.junit.Before;
import org.junit.Test;
import pipe.models.component.Arc;
import pipe.models.component.Connectable;
import pipe.models.interfaces.IObserver;
import pipe.models.visitor.connectable.ConnectableVisitor;
import pipe.models.visitor.PetriNetComponentVisitor;

import java.awt.geom.Point2D;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ConnectableTest {
    private static final double DOUBLE_DELTA = 0.001;
    private Connectable connectable;
    //TODO: This should really be a dummy, but it's hard to construct.
    private Arc mockArc;

    @Before
    public void setUp()
    {
        connectable = new DummyConnectable("test", "test");
        mockArc = mock(Arc.class);
    }

//    TODO: This cant be easily tested. Dont like this method anyway so it will get deleted
    @Test
    public void addInBoundOrOutboundCorrectlyChoses()
    {

    }

    @Test
    public void notifiesObserversOnXChange()
    {
        IObserver mockObserver = mock(IObserver.class);
        connectable.registerObserver(mockObserver);
        connectable.setX(10);
        verify(mockObserver).update();
    }

    @Test
    public void defaultNameOffsetValues()
    {
        assertEquals(-5, connectable.getNameXOffset(), DOUBLE_DELTA);
        assertEquals(35, connectable.getNameYOffset(), DOUBLE_DELTA);
    }

    @Test
    public void notifiesObserversOnNameChange()
    {
        IObserver mockObserver = mock(IObserver.class);
        connectable.registerObserver(mockObserver);
        connectable.setName("");
        verify(mockObserver).update();
    }

    @Test
    public void notifiesObserversOnIdChange()
    {
        IObserver mockObserver = mock(IObserver.class);
        connectable.registerObserver(mockObserver);
        connectable.setId("");
        verify(mockObserver).update();
    }

    private class DummyConnectable extends Connectable {

        DummyConnectable(String id, String name) {
            super(id, name);
        }

        @Override
        public int getHeight() {
            return 0;
        }

        @Override
        public int getWidth() {
            return 0;
        }

        @Override
        public Point2D.Double getCentre() {
            return new Point2D.Double(0,0);
        }

        @Override
        public Point2D.Double getArcEdgePoint(double angle) {
            return new Point2D.Double(0,0);
        }

        @Override
        public boolean isEndPoint() {
            return true;
        }

        @Override
        public void accept(final ConnectableVisitor visitor) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public boolean isSelectable() {
            return false;
        }

        @Override
        public boolean isDraggable() {
            return false;
        }

        @Override
        public void accept(PetriNetComponentVisitor visitor) {

        }
    }


}
