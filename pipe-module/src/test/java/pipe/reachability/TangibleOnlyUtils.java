package pipe.reachability;

import pipe.reachability.algorithm.ExplorerUtilities;
import pipe.reachability.algorithm.VanishingExplorer;
import pipe.reachability.algorithm.state.SerializingStateWriter;
import pipe.reachability.algorithm.state.OnTheFlyVanishingExplorer;
import pipe.reachability.algorithm.state.StateWriter;
import pipe.reachability.io.WriterFormatter;

import java.io.ObjectOutputStream;


public class TangibleOnlyUtils implements StateExplorerUtils {
    @Override
    public StateWriter getTangibleStateExplorer(WriterFormatter formatter, ObjectOutputStream outputStream) {
        return new SerializingStateWriter(formatter, outputStream);
    }


    @Override
    public VanishingExplorer getVanishingExplorer(ExplorerUtilities explorerUtilities) {
        return new OnTheFlyVanishingExplorer(explorerUtilities);
    }
}
