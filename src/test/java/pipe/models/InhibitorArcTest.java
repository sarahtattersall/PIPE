package pipe.models;

import org.junit.Before;
import org.junit.Test;
import pipe.models.component.NormalArc;
import pipe.models.component.Token;
import pipe.models.component.Connectable;
import pipe.models.component.Place;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;


public class InhibitorArcTest {

    NormalArc arc;

    @Before
    public void setUp()
    {
        Connectable source = new Place("source", "source");
        Connectable destination = new Place("dest", "dest");
        Map<Token, String> weights = new HashMap<Token, String>();
        arc = new NormalArc(source, destination, weights);
    }

    @Test
    public void isSelectable()
    {
        assertTrue(arc.isSelectable());
    }
}
