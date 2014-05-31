package pipe.gui.reachability;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxStylesheet;
import pipe.reachability.algorithm.CachingExplorerUtilities;
import pipe.reachability.algorithm.ExplorerUtilities;
import pipe.reachability.algorithm.TimelessTrapException;
import pipe.reachability.algorithm.VanishingExplorer;
import pipe.reachability.algorithm.sequential.SequentialStateSpaceExplorer;
import pipe.reachability.algorithm.state.OnTheFlyVanishingExplorer;
import pipe.reachability.algorithm.state.SimpleVanishingExplorer;
import pipe.reachability.algorithm.state.StateSpaceExplorer;
import uk.ac.imperial.io.*;
import uk.ac.imperial.pipe.io.PetriNetIOImpl;
import uk.ac.imperial.pipe.io.PetriNetReader;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;
import uk.ac.imperial.pipe.parsers.UnparsableException;
import uk.ac.imperial.state.ClassifiedState;
import uk.ac.imperial.state.Record;

import javax.swing.*;
import javax.xml.bind.JAXBException;
import java.awt.Container;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ReachabilityGraph {

    /**
     * For loading Petri nets to explore
     */
    private final FileDialog loadDialog;

    /**
     * For saving state space results
     */
    private final FileDialog saveBinaryDialog;

    /**
     * Graphical representation of reachability
     */
    private final TooltipMXGraph graph = new TooltipMXGraph();

    private JPanel panel1;

    /**
     * Used to start state space generation
     */
    private JButton goButton;

    /**
     * Displays the name of the Petri net loaded from file
     */
    private JTextField petriNetNameLabel;

    /**
     * Contains the graph based results of state space exploration
     */
    private JPanel resultsPanel;

    /**
     * Check box to determine if we include vanishing states in the exploration
     */
    private JCheckBox includeVanishingStatesCheckBox;

    /**
     * For saving state space results
     */
    private JButton saveButton;


    /**
     * Radio button, if selected we use existing petri net
     */
    private JRadioButton useExistingPetriNetRadioButton;

    /**
     * Label to show the transition binaries currently loaded
     */
    private JTextField transitionFieldLabel;

    /**
     * Label to show the latest state binaries loaded
     */
    private JTextField stateFieldLabel;

    /**
     * If this radio button is selected we will use the file loaded for state space exploration
     */
    private JRadioButton loadPetriNetFromFileRadioButton;

    /**
     * If this radio button is selected then previous results will be displayed
     */
    private JRadioButton loadFromBinariesRadio;

    private JPanel textResultsPanel;

    private JLabel textResultsLabel;

    /**
     * Temporary transitions file for generating results into
     */
    private Path temporaryTransitions;

    /**
     * Temporary states file for generating results into
     */
    private Path temporaryStates;

    /**
     * Last loaded Petri net via the load dialog
     */
    private PetriNet lastLoadedPetriNet;

    /**
     * Default petri net
     */
    private PetriNet defaultPetriNet;

    /**
     * Binary transitions loaded when binary transitions radio check box is selected
     */
    private Path binaryTransitions;

    /**
     * Binary states loaded when binary transitions radio check box is selected
     */
    private Path binaryStates;

    /**
     * When selecting use current Petri net the petri net used will be
     * @param loadDialog
     * @param saveBinaryDialog
     * @param petriNet
     */

    public ReachabilityGraph(FileDialog loadDialog, FileDialog saveBinaryDialog, PetriNet petriNet) {
        this.saveBinaryDialog = saveBinaryDialog;
        this.loadDialog = loadDialog;
        defaultPetriNet = petriNet;
        setUp();
    }

    /**
     * Constructor deactivates use current petri net radio button since none is supplied.
     * @param loadDialog
     * @param saveBinaryDialog
     */
    public ReachabilityGraph(FileDialog loadDialog, FileDialog saveBinaryDialog) {
        useExistingPetriNetRadioButton.setEnabled(false);
        this.saveBinaryDialog = saveBinaryDialog;
        this.loadDialog = loadDialog;
        setUp();
    }

    private void setUp() {
        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        graphComponent.setToolTips(true);
        graphComponent.setDragEnabled(false);

        setupGraph();

        resultsPanel.add(graphComponent);

        goButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateResults();
            }
        });
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveBinaryFiles();
            }
        });
        loadPetriNetFromFileRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadData();
            }
        });
        loadFromBinariesRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadBinaryFiles();
            }
        });
    }

    /**
     * Loads a petri net into the defaultPetriNet field
     */
    private void loadDefaultPetriNet() {
        try {
            PetriNetReader petriNetIO = new PetriNetIOImpl();
            URL resource = getClass().getResource("/simple_vanishing.xml");
            defaultPetriNet = petriNetIO.read(resource.getPath());
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (UnparsableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets up the graph so that components are not editable
     */
    private void setupGraph() {
        graph.setCellsLocked(true);
        graph.setEdgeLabelsMovable(false);
    }

    /**
     * Calculates the steady state exploration of a Petri net and stores its results
     * in a temporary file.
     * <p/>
     * These results are then read in and turned into a graphical representation using mxGraph
     * which is displayed to the user
     */
    private void calculateResults() {
        try {
            KryoStateIO stateWriter = new KryoStateIO();

            temporaryTransitions = getTransitionsPath();
            temporaryStates = getStatesPath();


            if (!loadFromBinariesRadio.isSelected()) {
                generateStateSpace(stateWriter, temporaryTransitions, temporaryStates);
            }

            processBinaryResults(stateWriter, temporaryTransitions, temporaryStates);


        } catch (TimelessTrapException | IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Copies the temporary files to a permenant loaction
     */
    private void saveBinaryFiles() {
        if (temporaryStates != null && temporaryTransitions != null) {
            copyFile(temporaryTransitions, "Select location for temporary transitions");
            copyFile(temporaryStates, "Select location for temporary states");
        }
    }

    /**
     * Opens the file dialog and saves the selected Petri net into lastLoadedPetriNet
     * for use when calculating the state space exploration
     */
    private void loadData() {
        loadDialog.setTitle("Select petri net");
        loadDialog.setVisible(true);

        File[] files = loadDialog.getFiles();
        if (files.length > 0) {
            File path = files[0];
            try {
                petriNetNameLabel.setText(path.getName());
                PetriNetReader petriNetIO = new PetriNetIOImpl();
                lastLoadedPetriNet = petriNetIO.read(path.getAbsolutePath());
            } catch (JAXBException | UnparsableException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Loads the transition and state binary files into the member variables
     */
    private void loadBinaryFiles() {
        loadDialog.setTitle("Load transitions file");
        loadDialog.setVisible(true);
        File[] files = loadDialog.getFiles();
        if (files.length > 0) {
            File file = files[0];
            binaryTransitions = Paths.get(file.toURI());
            transitionFieldLabel.setText(file.getName());
        }

        loadDialog.setTitle("Load states file");
        loadDialog.setVisible(true);
        File[] statesFiles = loadDialog.getFiles();
        if (statesFiles.length > 0) {
            File file = statesFiles[0];
            binaryStates = Paths.get(file.toURI());
            stateFieldLabel.setText(file.getName());
        }

    }

    /**
     * @return Path for state space transitions
     * @throws IOException
     */
    private Path getTransitionsPath() throws IOException {
        return loadFromBinariesRadio.isSelected() ? binaryTransitions : Files.createTempFile("transitions", ".tmp");
    }

    /**
     * @return Path for state space states
     * @throws IOException
     */
    private Path getStatesPath() throws IOException {
        return loadFromBinariesRadio.isSelected() ? binaryStates : Files.createTempFile("states", ".tmp");
    }

    /**
     * Writes the state space into transitions and states
     * @param stateWriter
     * @param transitions
     * @param states
     * @throws IOException
     * @throws TimelessTrapException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private void generateStateSpace(StateWriter stateWriter, Path transitions, Path states)
            throws IOException, TimelessTrapException, ExecutionException, InterruptedException {
        try (OutputStream transitionStream = Files.newOutputStream(transitions);
             OutputStream stateStream = Files.newOutputStream(states)) {
            try (Output transitionOutput = new Output(transitionStream);
                 Output stateOutput = new Output(stateStream)) {
                writeStateSpace(stateWriter, transitionOutput, stateOutput);
            }
        }
    }

    /**
     * Reads in the state space from transitions and states
     * @param stateReader
     * @param transitions
     * @param states
     * @throws IOException
     */
    private void processBinaryResults(StateReader stateReader, Path transitions, Path states) throws IOException {
        try (InputStream inputStream = Files.newInputStream(transitions);
             InputStream stateInputStream = Files.newInputStream(states);
             Input transitionInput = new Input(inputStream);
             Input stateInput = new Input(stateInputStream)) {
            Collection<Record> records = readResults(stateReader, transitionInput);
            Map<Integer, ClassifiedState> stateMap = readMappings(stateReader, stateInput);
            updateTextResults(records);
            updateGraph(records, stateMap);
        }
    }

    /**
     * Updates the text results with the number of states and transitions
     *
     * Although this method could take the results from generating the state space
     * with the pre-calculated transition count, if we load from disk then this is
     * lost so for now just caluclate the transition count directly from the records
     * @param records State space successor records
     */
    private void updateTextResults(Collection<Record> records) {
        StringBuilder results = new StringBuilder();
        int transitions = numberOfTransitions(records);
        results.append("Results: ")
               .append(records.size()).append(" states and ")
               .append(transitions).append(" transitions");
        textResultsLabel.setText(results.toString());
    }

    /**
     * Calculates the number of transitions from the results records
     * @param records
     * @return total number of transitions
     */
    private int numberOfTransitions(Collection<Record> records) {
        int transitions = 0;
        for (Record record : records) {
            transitions += record.successors.size();
        }
        return transitions;
    }

    /**
     * @param temporary path to copy to new location
     * @param message   displayed message in save file dialog pop up
     */
    private void copyFile(Path temporary, String message) {
        saveBinaryDialog.setTitle(message);
        saveBinaryDialog.setVisible(true);

        File[] files = saveBinaryDialog.getFiles();
        if (files.length > 0) {
            File file = files[0];
            Path path = Paths.get(file.toURI());
            try {
                Files.copy(temporary, path, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Writes the petriNet state space out to a temporary file which is referenced by the objectOutputStream
     *
     * @param stateWriter      format in which to write the results to
     * @param transitionOutput stream to write state space to
     * @param stateOutput      stream to write state integer mappings to
     * @throws TimelessTrapException if the state space cannot be generated due to cyclic vanishing states
     */
    private void writeStateSpace(StateWriter stateWriter, Output transitionOutput, Output stateOutput)
            throws TimelessTrapException, ExecutionException, InterruptedException, IOException {
        PetriNet petriNet = (useExistingPetriNetRadioButton.isSelected() ? defaultPetriNet : lastLoadedPetriNet);
        StateProcessor processor = new StateIOProcessor(stateWriter, transitionOutput, stateOutput);
        ExplorerUtilities explorerUtilites = new CachingExplorerUtilities(petriNet);
        VanishingExplorer vanishingExplorer = getVanishingExplorer(explorerUtilites);
        StateSpaceExplorer stateSpaceExplorer =
                new SequentialStateSpaceExplorer(explorerUtilites, vanishingExplorer, processor);
        stateSpaceExplorer.generate(explorerUtilites.getCurrentState());
    }

    /**
     * Reads results of steady state exploration into a collection of records
     *
     * @param stateReader
     * @param input
     * @return state transitions with rates
     * @throws IOException
     */
    private Collection<Record> readResults(StateReader stateReader, Input input) throws IOException {
        MultiStateReader reader = new EntireStateReader(stateReader);
        return reader.readRecords(input);
    }


    /**
     * Reads results of the mapping of an integer state representation to
     * the Classified State it represents
     *
     * @param stateReader
     * @param input
     * @return state mappings
     * @throws IOException
     */
    private Map<Integer, ClassifiedState> readMappings(StateReader stateReader, Input input) throws IOException {
        MultiStateReader reader = new EntireStateReader(stateReader);
        return reader.readStates(input);
    }

    /**
     * Updates the mxGraph to display the records
     *
     * @param records  state transitions from a processed Petri net
     * @param stateMap
     */
    private void updateGraph(Iterable<Record> records, Map<Integer, ClassifiedState> stateMap) {

        removeCurrentContent();

        Object parent = graph.getDefaultParent();
        graph.getModel().beginUpdate();
        mxStylesheet stylesheet = graph.getStylesheet();
        Map<String, Object> vertexStyles = stylesheet.getDefaultVertexStyle();
        vertexStyles.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);

        mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
        layout.setInterHierarchySpacing(20);
        layout.setInterRankCellSpacing(50);
        Map<Integer, Object> verticies = new HashMap<>();
        try {
            graph.clearSelection();
            for (Record record : records) {
                Object state = getInsertedState(verticies, record.state, stateMap.get(record.state), graph);
                for (Map.Entry<Integer, Double> entry : record.successors.entrySet()) {
                    Integer successorId = entry.getKey();
                    ClassifiedState successorState = stateMap.get(successorId);
                    Object successor = getInsertedState(verticies, successorId, successorState, graph);
                    addEdge(parent, state, successor, entry.getValue());
                }
            }
        } finally {
            graph.getModel().endUpdate();
        }

        layout.execute(graph.getDefaultParent());

    }

    /**
     * Vanishing explorer is either a {@link pipe.reachability.algorithm.state.SimpleVanishingExplorer} if
     * vanishing states are to be included in the graph, else it is {@link pipe.reachability.algorithm.state.OnTheFlyVanishingExplorer}
     *
     * @param explorerUtilities
     */
    private VanishingExplorer getVanishingExplorer(ExplorerUtilities explorerUtilities) {
        if (includeVanishingStatesCheckBox.isSelected()) {
            return new SimpleVanishingExplorer();
        }
        return new OnTheFlyVanishingExplorer(explorerUtilities);
    }

    private void removeCurrentContent() {
        graph.removeCells(graph.getChildVertices(graph.getDefaultParent()));
    }

    /**
     * Creates/retreives a graphical representation of the state. If it does not exist
     * on the canvas already it is created and added to the verticies map
     *
     * @param verticies verticies map representing states already existing on the graph to their
     *                  graphical representation
     * @param stateId   state id that is represented in state space
     * @param state     state that stateid maps to in state space
     * @return graphical vertex representation for the State
     */
    private Object getInsertedState(Map<Integer, Object> verticies, Integer stateId, ClassifiedState state,
                                    TooltipMXGraph graph) {
        if (verticies.containsKey(stateId)) {
            return verticies.get(stateId);
        }
        Object parent = graph.getDefaultParent();
        Object vertexState = graph.insertVertex(parent, null, verticies.size(), 0, 0, 30, 30, getColor(state));
        graph.setTooltipText(vertexState, stateId.toString());
        verticies.put(stateId, vertexState);
        return vertexState;
    }

    private Object addEdge(Object parent, Object state, Object successor, double rate) {

        return graph.insertEdge(parent, null, String.format("%.2f", rate), state, successor);
    }

    /**
     * Return settings string for inserting a vertex into a graph.
     * <p/>
     * Tangible states are blue whilst vanishing states are red.
     *
     * @param state
     * @return settings string
     */
    private String getColor(ClassifiedState state) {
        if (state.isTangible()) {
            return "fillColor=#99CCFF";
        }
        return "fillColor=#FF8566";
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("ReachabilityGraph");
        FileDialog selector = new FileDialog(frame, "Select petri net", FileDialog.LOAD);
        FileDialog saver = new FileDialog(frame, "Save binary transition data", FileDialog.SAVE);
        frame.setContentPane(new ReachabilityGraph(selector, saver).panel1);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }

    private void createUIComponents() {
    }

    public Container getMainPanel() {
        return panel1;
    }
}
