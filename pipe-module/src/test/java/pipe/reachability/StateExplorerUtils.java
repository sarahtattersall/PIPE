package pipe.reachability;

import com.esotericsoftware.kryo.io.Output;
import pipe.reachability.algorithm.ExplorerUtilities;
import pipe.reachability.algorithm.VanishingExplorer;
import uk.ac.imperial.io.StateProcessor;
import uk.ac.imperial.io.StateWriter;

/**
 * Useful class to distinguish in Cucumber integration tests whether
 * tangible only states will be written to the output stream or whether
 * vanishing states will be written too
 */
public interface StateExplorerUtils {

    StateProcessor getTangibleStateExplorer(StateWriter stateWriter, Output outputStream);
    VanishingExplorer getVanishingExplorer(ExplorerUtilities explorerUtilities);

}
