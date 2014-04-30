package pipe.reachability.algorithm;

import pipe.reachability.state.State;

/**
 * On implementing calls to the {@link pipe.reachability.algorithm.StateExplorer}'s explore
 * method this class uses the last state whether it be vanishing or tangible
 */
public class VanishingTransitionVanishingExplorer extends AbstractVanishingExplorer {

    private final StateExplorer tangibleExplorer;
    protected VanishingTransitionVanishingExplorer(StateExplorer vanishingExplorer, ExplorerUtilites explorerUtilities,
                                                   StateExplorer tangibleExplorer) {
        super(vanishingExplorer, explorerUtilities);
        this.tangibleExplorer = tangibleExplorer;
    }

    @Override
    protected void registerTangible(State lastTangible, State previous, State successor, double rate) {
        tangibleExplorer.explore(previous, successor, rate);
    }
}
