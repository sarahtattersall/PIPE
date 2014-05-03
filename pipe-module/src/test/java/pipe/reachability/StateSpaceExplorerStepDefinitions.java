package pipe.reachability;

import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import pipe.exceptions.PetriNetComponentNotFoundException;
import pipe.models.component.token.Token;
import pipe.models.petrinet.PetriNet;
import pipe.parsers.UnparsableException;
import pipe.reachability.algorithm.CachingExplorerUtilities;
import pipe.reachability.algorithm.ExplorerUtilities;
import pipe.reachability.algorithm.TimelessTrapException;
import pipe.reachability.algorithm.VanishingExplorer;
import pipe.reachability.algorithm.sequential.SequentialStateSpaceExplorer;
import pipe.reachability.algorithm.state.StateWriter;
import pipe.reachability.algorithm.state.StateSpaceExplorer;
import pipe.reachability.io.ByteWriterFormatter;
import pipe.reachability.io.SerializedStateSpaceExplorationReader;
import pipe.reachability.io.StateTransition;
import pipe.reachability.io.WriterFormatter;
import pipe.reachability.state.HashedState;
import pipe.reachability.state.State;
import utils.Utils;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class StateSpaceExplorerStepDefinitions {
    VanishingExplorer vanishingExplorer;

    /**
     * Petri net to perform exploration on
     */
    private PetriNet petriNet;

    /**
     * State space exploration results
     */
    private Map<StateTransition, Double> results = new HashMap<>();

    /**
     * Set to true if timeless trap is thrown
     */
    private boolean timelessTrap = false;

    /**
     * Auxillary state for registering with expected records
     */
    private State state;

    /**
     * Auxillary state for registering with expected records
     */
    private State successor;

    private StateExplorerUtils utils;

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
    public void I_generate_the_exploration_graph() throws IOException {

        WriterFormatter formatter = new ByteWriterFormatter();
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
             ObjectOutputStream outputStream = new ObjectOutputStream(byteStream)) {


            StateWriter tangibleExplorer = utils.getTangibleStateExplorer(formatter, outputStream);
            ExplorerUtilities explorerUtilities = new CachingExplorerUtilities(petriNet);
            VanishingExplorer vanishingExplorer = utils.getVanishingExplorer(explorerUtilities);

            StateSpaceExplorer stateSpaceExplorer =
                    new SequentialStateSpaceExplorer(tangibleExplorer, vanishingExplorer, explorerUtilities);
            stateSpaceExplorer.generate();

            try (ByteArrayInputStream byteInputStream = new ByteArrayInputStream(byteStream.toByteArray());
                 ObjectInputStream inputStream = new ObjectInputStream(byteInputStream)) {
                SerializedStateSpaceExplorationReader reader = new SerializedStateSpaceExplorationReader(formatter);
                results.putAll(reader.getTotalRates(inputStream));
            }
        } catch (TimelessTrapException ignored) {
            timelessTrap = true;
        }
    }

    @Then("^I expect to see (\\d+) state transitions?")
    public void I_expect_transitions(int transitionCount) {
        assertEquals(transitionCount, results.size());
    }

    @And("^I expect a record with state")
    public void I_expect_a_record_with_state(String jsonState) throws IOException, PetriNetComponentNotFoundException {
        state = toState(jsonState);
    }

    private State toState(String json) throws IOException, PetriNetComponentNotFoundException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, HashMap<String, Integer>> map =
                mapper.readValue(json, new TypeReference<HashMap<String, HashMap<String, Integer>>>() {
                });
        Map<String, Map<Token, Integer>> stateMap = new HashMap<>();
        for (Map.Entry<String, HashMap<String, Integer>> entry : map.entrySet()) {
            Map<Token, Integer> tokenCounts = new HashMap<>();
            for (Map.Entry<String, Integer> tokenEntry : entry.getValue().entrySet()) {
                tokenCounts.put(petriNet.getComponent(tokenEntry.getKey(), Token.class), tokenEntry.getValue());
            }
            stateMap.put(entry.getKey(), tokenCounts);
        }
        return HashedState.tangibleState(stateMap);
    }

    @And("^successor")
    public void successor(String jsonState) throws IOException, PetriNetComponentNotFoundException {
        successor = toState(jsonState);
    }

    @And("^rate (\\d+.\\d+)")
    public void rate(double rate) {
        StateTransition transition = new StateTransition(state, successor);
        Double actualRate = results.get(transition);
        assertNotNull("State transition not contained in results", actualRate);
        assertEquals("State transition rate not correct", rate, actualRate, 0.00001);
    }

    @And("^have thrown a TimelessTrapException$")
    public void have_thrown_a_TimelessTrapException() {
        assertTrue(timelessTrap);
    }
}
