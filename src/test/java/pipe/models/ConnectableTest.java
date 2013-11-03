package pipe.models;

import org.junit.Before;
import org.junit.Test;
import pipe.models.interfaces.IObserver;
import pipe.views.ArcView;

import java.awt.geom.Point2D;
import java.util.LinkedList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created with IntelliJ IDEA.
 * User: st809
 * Date: 18/10/2013
 * Time: 12:23
 * To change this template use File | Settings | File Templates.
 */
public class ConnectableTest {
    private Connectable connectable;
    //TODO: This should really be a dummy, but it's hard to construct.
    private ArcView mockView;

    @Before
    public void setUp()
    {
        connectable = new DummyConnectable("test", "test");
        mockView = mock(ArcView.class);
    }

    @Test
    public void addOutBoundArcCorrectlyReturns()
    {
        connectable.addOutbound(mockView);
        LinkedList<ArcView> outBoundArcs = connectable.outboundArcs();
        assertTrue(outBoundArcs.contains(mockView));
    }

    @Test
    public void addInBoundArcCorrectlyReturns()
    {
        connectable.addInbound(mockView);
        LinkedList<ArcView> inboundArcs = connectable.inboundArcs();
        assertTrue(inboundArcs.contains(mockView));
    }

//    TODO: This cant be easily tested. Dont like this method anyway so it will get deleted
    @Test
    public void addInBoundOrOutboundCorrectlyChoses()
    {

    }

    @Test
    public void removeFromCorrectlyRemovesItem()
    {
        connectable.addOutbound(mockView);
        connectable.removeFromArcs(mockView);
        LinkedList<ArcView> outBoundArcs = connectable.outboundArcs();
        assertFalse(outBoundArcs.contains(mockView));
    }

    @Test
    public void removeToCorrectlyRemovesItem()
    {
        connectable.addInbound(mockView);
        connectable.removeToArc(mockView);
        LinkedList<ArcView> inBoundArcs = connectable.inboundArcs();
        assertFalse(inBoundArcs.contains(mockView));
    }

    @Test
    public void notifiesObserversOnXChange()
    {
        IObserver mockObserver = mock(IObserver.class);
        connectable.registerObserver(mockObserver);
        connectable.setX(10);
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
        public double getCentreX() {
            return 0;
        }

        @Override
        public double getCentreY() {
            return 0;
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
        public boolean isSelectable() {
            return false;
        }

        @Override
        public boolean isDraggable() {
            return false;
        }
    }


}
