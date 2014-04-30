package pipe.reachability;

import pipe.reachability.algorithm.ExplorerUtilities;
import pipe.reachability.algorithm.state.StateWriter;
import pipe.reachability.algorithm.VanishingExplorer;
import pipe.reachability.io.WriterFormatter;

import java.io.ObjectOutputStream;

/**
 * Useful class to distinguish in Cucumber integration tests whether
 * tangible only states will be written to the output stream or whether
 * vanishing states will be written too
 */
public interface StateExplorerUtils {

    StateWriter getTangibleStateExplorer(WriterFormatter formatter, ObjectOutputStream outputStream);
    VanishingExplorer getVanishingExplorer(ExplorerUtilities explorerUtilities);

}
