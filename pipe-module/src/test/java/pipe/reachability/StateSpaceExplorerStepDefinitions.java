package pipe.reachability;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import pipe.exceptions.PetriNetComponentNotFoundException;
import pipe.models.petrinet.PetriNet;
import pipe.parsers.UnparsableException;
import pipe.reachability.algorithm.CachingExplorerUtilities;
import pipe.reachability.algorithm.ExplorerUtilities;
import pipe.reachability.algorithm.TimelessTrapException;
import pipe.reachability.algorithm.VanishingExplorer;
import pipe.reachability.algorithm.sequential.SequentialStateSpaceExplorer;
import pipe.reachability.algorithm.state.StateSpaceExplorer;
import uk.ac.imperial.io.EntireStateReader;
import uk.ac.imperial.io.KryoStateIO;
import uk.ac.imperial.io.MultiStateReader;
import uk.ac.imperial.io.StateProcessor;
import uk.ac.imperial.state.ClassifiedState;
import uk.ac.imperial.state.Record;
import uk.ac.imperial.utils.StateUtils;
import utils.Utils;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

public class StateSpaceExplorerStepDefinitions {
    /**
     * Petri net to perform exploration on
     */
    private PetriNet petriNet;

    /**
     * State space exploration results
     */
    private Map<ClassifiedState, Map<ClassifiedState, Double>> results = new HashMap<>();

    /**
     * Set to true if timeless trap is thrown
     */
    private boolean timelessTrap = false;

    /**
     * Auxillary state for registering with expected records
     */
    private ClassifiedState state;

    /**
     * Auxillary state for registering with expected records
     */
    private ClassifiedState successor;

    private StateExplorerUtils utils;

    private int processedTransitons = 0;

    @Before("@tangibleOnly")
    public void beforeTangibleScenario() {
        utils = new TangibleOnlyUtils();
    }

    @Before("@tangibleAndVanishing")
    public void beforeTanigbleAndVanishingScenario() {
        utils = new TangibleAndVanishingUtils();
    }


    @Given("^I use the Petri net located at (/[\\w/]+.xml)$")
    public void I_Use_the_Petri_net_located_at(String path) throws JAXBException, UnparsableException {
        petriNet = Utils.readPetriNet(path);
    }

    @When("^I generate the exploration graph$")
    public void I_generate_the_exploration_graph() throws IOException, ExecutionException, InterruptedException {

        KryoStateIO kryoIo = new KryoStateIO();
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream()) {
            try (Output outputStream = new Output(byteStream)) {


                StateProcessor processor = utils.getTangibleStateExplorer(kryoIo, outputStream);
                ExplorerUtilities explorerUtilities = new CachingExplorerUtilities(petriNet);
                VanishingExplorer vanishingExplorer = utils.getVanishingExplorer(explorerUtilities);

                StateSpaceExplorer stateSpaceExplorer =
                        new SequentialStateSpaceExplorer(explorerUtilities, vanishingExplorer, processor);
                processedTransitons = stateSpaceExplorer.generate(explorerUtilities.getCurrentState());
            } catch (TimelessTrapException e) {
                timelessTrap = true;
            }
            try (ByteArrayInputStream byteInputStream = new ByteArrayInputStream(byteStream.toByteArray());
                 Input inputStream = new Input(byteInputStream)) {
                MultiStateReader reader = new EntireStateReader(kryoIo);
                for (Record record : reader.readRecords(inputStream)) {
                    results.put(record.state, record.successors);
                }
            }
        }

    }

    @Then("^I expect to see (\\d+) state transitions?")
    public void I_expect_transitions(int transitionCount) {
        assertEquals(transitionCount, processedTransitons);
    }

    @And("^I expect a record with state")
    public void I_expect_a_record_with_state(String jsonState) throws IOException, PetriNetComponentNotFoundException {
        state = StateUtils.tangibleStateFromJson(jsonState);
    }

    @And("^successor")
    public void successor(String jsonState) throws IOException, PetriNetComponentNotFoundException {
        successor = StateUtils.tangibleStateFromJson(jsonState);
    }

    @And("^rate (\\d+.\\d+)")
    public void rate(double rate) {
        Map<ClassifiedState, Double> successors = results.get(state);
        Double actualRate = successors.get(successor);
        assertNotNull("State transition not contained in results", actualRate);
        assertEquals("State transition rate not correct", rate, actualRate, 0.00001);
    }

    @And("^have thrown a TimelessTrapException$")
    public void have_thrown_a_TimelessTrapException() {
        assertTrue(timelessTrap);
    }
}
