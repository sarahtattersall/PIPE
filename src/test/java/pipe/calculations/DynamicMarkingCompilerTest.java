package pipe.calculations;

import org.junit.Test;
import pipe.models.interfaces.IDynamicMarking;

import static org.junit.Assert.assertNotNull;

/**
 * Simple test to check that it will compile the file
 * More testing should be done in the future
 */
//TODO: BETTER TEST
public class DynamicMarkingCompilerTest {

    @Test
    public void compiles() {
        DynamicMarkingCompiler d = new DynamicMarkingCompiler();
        IDynamicMarking marking = d.getDynamicMarking("p[0] > 0", "p[1] > 0");
        assertNotNull(marking);
    }

}
