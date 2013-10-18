package pipe.models;

import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: st809
 * Date: 18/10/2013
 * Time: 12:45
 * To change this template use File | Settings | File Templates.
 */
public class NormalArcTest {

    NormalArc arc;

    @Before
    public void setUp()
    {
        Connectable source = new Place("source", "source");
        Connectable destination = new Place("dest", "dest");
        List<Marking> marking = new LinkedList<Marking>();
        arc = new NormalArc(source, destination, marking);
    }

    @Test
    public void isSelectable()
    {
        assertTrue(arc.isSelectable());
    }
}
