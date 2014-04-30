package pipe.reachability;

import pipe.reachability.algorithm.ExplorerUtilities;
import pipe.reachability.algorithm.VanishingExplorer;
import pipe.reachability.algorithm.state.SeralizingStateWriter;
import pipe.reachability.algorithm.state.SimpleVanishingExplorer;
import pipe.reachability.algorithm.state.StateWriter;
import pipe.reachability.io.WriterFormatter;

import java.io.ObjectOutputStream;

public class TangibleAndVanishingUtils implements StateExplorerUtils {
    @Override
    public StateWriter getTangibleStateExplorer(WriterFormatter formatter, ObjectOutputStream outputStream) {
        return new SeralizingStateWriter(formatter, outputStream);
    }

    @Override
    public VanishingExplorer getVanishingExplorer(ExplorerUtilities explorerUtilities) {
        return new SimpleVanishingExplorer();
    }
}
