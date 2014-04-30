package pipe.reachability;

import pipe.reachability.algorithm.ExplorerUtilities;
import pipe.reachability.algorithm.state.StateExplorer;
import pipe.reachability.algorithm.state.TangibleTransitionVanishingExplorer;
import pipe.reachability.algorithm.VanishingExplorer;
import pipe.reachability.algorithm.sequential.NonSavingStateExplorer;
import pipe.reachability.algorithm.sequential.SavingStateExplorer;
import pipe.reachability.io.WriterFormatter;

import java.io.ObjectOutputStream;

public class TangibleOnlyUtils implements StateExplorerUtils {
    @Override
    public StateExplorer getTangibleStateExplorer(WriterFormatter formatter, ObjectOutputStream outputStream) {
        return new SavingStateExplorer(formatter, outputStream);
    }

    @Override
    public StateExplorer getVanishingStateExplorer(WriterFormatter formatter, ObjectOutputStream outputStream) {
        return new NonSavingStateExplorer();
    }

    @Override
    public VanishingExplorer getVanishingExplorer(StateExplorer tangible, StateExplorer vanishing, ExplorerUtilities explorerUtilities) {
        return new TangibleTransitionVanishingExplorer(tangible, vanishing, explorerUtilities);
    }
}
