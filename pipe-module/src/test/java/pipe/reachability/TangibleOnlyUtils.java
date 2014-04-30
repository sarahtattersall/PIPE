package pipe.reachability;

import pipe.reachability.algorithm.ExplorerUtilities;
import pipe.reachability.algorithm.VanishingExplorer;
import pipe.reachability.algorithm.sequential.SavingStateExplorer;
import pipe.reachability.algorithm.state.OnTheFlyVanishingExplorer;
import pipe.reachability.algorithm.state.StateExplorer;
import pipe.reachability.io.WriterFormatter;

import java.io.ObjectOutputStream;

public class TangibleOnlyUtils implements StateExplorerUtils {
    @Override
    public StateExplorer getTangibleStateExplorer(WriterFormatter formatter, ObjectOutputStream outputStream) {
        return new SavingStateExplorer(formatter, outputStream);
    }


    @Override
    public VanishingExplorer getVanishingExplorer(ExplorerUtilities explorerUtilities) {
        return new OnTheFlyVanishingExplorer(explorerUtilities);
    }
}
