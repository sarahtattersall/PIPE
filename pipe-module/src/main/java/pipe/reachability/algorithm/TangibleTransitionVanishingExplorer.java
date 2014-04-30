package pipe.reachability.algorithm;

import pipe.reachability.state.State;

/**
 * On implementing calls to the {@link pipe.reachability.algorithm.StateExplorer}'s explore
 * method this class uses the last known tangible state
 */
public class TangibleTransitionVanishingExplorer extends AbstractVanishingExplorer {

    private final StateExplorer tangibleExplorer;

    public TangibleTransitionVanishingExplorer(StateExplorer tangibleExplorer, StateExplorer vanishingExplorer,
                                               ExplorerUtilites explorerUtilities) {
        super(vanishingExplorer, explorerUtilities);
        this.tangibleExplorer = tangibleExplorer;
    }

    @Override
    protected void registerTangible(State lastTangible, State previous, State successor, double rate) {
        tangibleExplorer.explore(lastTangible, successor, rate);
    }
}
