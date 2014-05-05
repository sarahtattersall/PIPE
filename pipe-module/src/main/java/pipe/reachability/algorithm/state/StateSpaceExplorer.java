package pipe.reachability.algorithm.state;

import pipe.reachability.algorithm.TimelessTrapException;

import java.util.concurrent.ExecutionException;

/**
 * Performs state space exploration searching for the states that can be
 * reached from the given Petri net set up.
 *
 * There are numerous options for exploring the state space. One of which is
 * whether tangible states will be written out to the writer or if
 * vanishing states will be written too.
 *
 * Implementations of this interface should deal as flexibly as possible with different
 * scenarios for generating the state space.
 */
public interface StateSpaceExplorer {

    /**
     * Performs steady state exploration
     *
     * @throws pipe.reachability.algorithm.TimelessTrapException
     */
    void generate() throws TimelessTrapException, InterruptedException, ExecutionException;
}
