package pipe.gui.widget;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import pipe.reachability.algorithm.ExplorerUtilities;
import pipe.reachability.algorithm.StateSpaceExplorer;
import pipe.reachability.algorithm.TimelessTrapException;
import pipe.reachability.algorithm.VanishingExplorer;
import pipe.reachability.algorithm.sequential.SequentialStateSpaceExplorer;
import uk.ac.imperial.io.*;
import uk.ac.imperial.pipe.exceptions.InvalidRateException;
import uk.ac.imperial.pipe.io.PetriNetIOImpl;
import uk.ac.imperial.pipe.io.PetriNetReader;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;
import uk.ac.imperial.state.ClassifiedState;
import uk.ac.imperial.state.Record;

import javax.swing.*;
import javax.xml.bind.JAXBException;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StateSpaceLoader {
    private static final Logger LOGGER = Logger.getLogger(StateSpaceLoader.class.getName());

    /**
     * For loading Petri nets to explore
     */
    private final FileDialog loadDialog;

    private JRadioButton useExistingPetriNetRadioButton;

    private JTextField petriNetNameLabel;

    private JRadioButton loadPetriNetFromFileRadioButton;

    private JPanel mainPanel;

    private JTextField stateFieldLabel;

    private JTextField transitionFieldLabel;

    private JRadioButton loadFromBinariesRadio;

    /**
     * Default petri net
     */
    private PetriNet defaultPetriNet;

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
     * Binary transitions loaded when binary transitions radio check box is selected
     */
    private Path binaryTransitions;

    /**
     * Binary states loaded when binary transitions radio check box is selected
     */
    private Path binaryStates;

    public StateSpaceLoader(FileDialog loadDialog) {
        this.loadDialog = loadDialog;
        setUp();
    }

    public boolean isBinaryLoadChecked() {
        return loadFromBinariesRadio.isSelected();
    }

    public PetriNet getPetriNet() {
        return useExistingPetriNetRadioButton.isSelected() ? defaultPetriNet : lastLoadedPetriNet;
    }

    private void setUp() {

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
            } catch (JAXBException | FileNotFoundException e) {
                LOGGER.log(Level.SEVERE, e.getMessage());
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
        } else {
            return;
        }

        loadDialog.setTitle("Load states file");
        loadDialog.setVisible(true);
        File[] statesFiles = loadDialog.getFiles();
        if (statesFiles.length > 0) {
            File file = statesFiles[0];
            binaryStates = Paths.get(file.toURI());
            stateFieldLabel.setText(file.getName());
        } else {
            return;
        }

        //        loadResults();
    }


    public JPanel getMainPanel() {
        return mainPanel;
    }
    //
    //    /**
    //     * Loads a reachability graph from binary files
    //     */
    //    private void loadResults() {
    //        try {
    //            KryoStateIO stateWriter = new KryoStateIO();
    //            temporaryTransitions = getTransitionsPath();
    //            temporaryStates = getStatesPath();
    //            processBinaryResults(stateWriter, temporaryTransitions, temporaryStates);
    //        } catch (IOException e) {
    //            LOGGER.log(Level.SEVERE, e.getMessage());
    //        }
    //    }


    /**
     * Calculates the steady state exploration of a Petri net and stores its results
     * in a temporary file.
     * <p/>
     * These results are then read in and turned into a graphical representation using mxGraph
     * which is displayed to the user
     */
    public StateSpaceExplorer.StateSpaceExplorerResults calculateResults(ExplorerCreator creator,
                                                                         VanishingExplorerCreator vanishingCreator)
            throws IOException, InterruptedException, ExecutionException, InvalidRateException, TimelessTrapException {
        if(loadFromBinariesRadio.isSelected()) {
            return loadFromBinaries();
        } else {
            KryoStateIO stateWriter = new KryoStateIO();
            temporaryTransitions = getTransitionsPath();
            temporaryStates = getStatesPath();

            PetriNet petriNet = useExistingPetriNetRadioButton.isSelected() ? defaultPetriNet : lastLoadedPetriNet;
            if (petriNet != null) {
                ExplorerUtilities explorerUtils = creator.create(petriNet);
                VanishingExplorer vanishingExplorer = vanishingCreator.create(explorerUtils);
                return generateStateSpace(stateWriter, temporaryTransitions, temporaryStates, petriNet, explorerUtils,
                        vanishingExplorer);
            }
            return null;
        }
    }

    private StateSpaceExplorer.StateSpaceExplorerResults loadFromBinaries() throws IOException {
        KryoStateIO stateWriter = new KryoStateIO();
        temporaryTransitions = getTransitionsPath();
        temporaryStates = getStatesPath();
        return  processBinaryResults(stateWriter, temporaryTransitions);
    }

    private StateSpaceExplorer.StateSpaceExplorerResults processBinaryResults(StateReader stateReader, Path transitions) throws IOException {
        try (InputStream inputStream = Files.newInputStream(transitions);
             Input transitionInput = new Input(inputStream)) {
            Collection<Record> records = readResults(stateReader, transitionInput);
            int transitionCount = getTransitionCount(records);
            return new StateSpaceExplorer.StateSpaceExplorerResults(transitionCount, records.size());
        }
    }

    private int getTransitionCount(Collection<Record> records) {
        int sum = 0;
        for (Record record : records) {
            sum += record.successors.size();
        }
        return sum;
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
     *
     * @param stateWriter
     * @param transitions
     * @param states
     * @throws IOException
     * @throws TimelessTrapException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private StateSpaceExplorer.StateSpaceExplorerResults generateStateSpace(StateWriter stateWriter, Path transitions,
                                                                            Path states, PetriNet petriNet,
                                                                            ExplorerUtilities explorerUtils,
                                                                            VanishingExplorer vanishingExplorer)
            throws IOException, TimelessTrapException, ExecutionException, InvalidRateException, InterruptedException {
        try (OutputStream transitionStream = Files.newOutputStream(transitions);
             OutputStream stateStream = Files.newOutputStream(states)) {
            try (Output transitionOutput = new Output(transitionStream);
                 Output stateOutput = new Output(stateStream)) {
                return writeStateSpace(stateWriter, transitionOutput, stateOutput, petriNet, explorerUtils,
                        vanishingExplorer);
            }
        }
    }


    /**
     * Writes the petriNet state space out to a temporary file which is referenced by the objectOutputStream
     *
     * @param stateWriter       format in which to write the results to
     * @param transitionOutput  stream to write state space to
     * @param stateOutput       stream to write state integer mappings to
     * @param explorerUtilites
     * @param vanishingExplorer @throws TimelessTrapException if the state space cannot be generated due to cyclic vanishing states
     */
    private StateSpaceExplorer.StateSpaceExplorerResults writeStateSpace(StateWriter stateWriter,
                                                                         Output transitionOutput, Output stateOutput,
                                                                         PetriNet petriNet,
                                                                         ExplorerUtilities explorerUtilites,
                                                                         VanishingExplorer vanishingExplorer)
            throws TimelessTrapException, ExecutionException, InterruptedException, IOException, InvalidRateException {
        StateProcessor processor = new StateIOProcessor(stateWriter, transitionOutput, stateOutput);
        StateSpaceExplorer stateSpaceExplorer =
                new SequentialStateSpaceExplorer(explorerUtilites, vanishingExplorer, processor);
        return stateSpaceExplorer.generate(explorerUtilites.getCurrentState());
    }

    public interface ExplorerCreator {
        ExplorerUtilities create(PetriNet petriNet);
    }

    public interface VanishingExplorerCreator {
        VanishingExplorer create(ExplorerUtilities utils);
    }

    public Results loadStateSpace() throws IOException {
        KryoStateIO stateReader = new KryoStateIO();
        try (InputStream inputStream = Files.newInputStream(temporaryTransitions);
             InputStream stateInputStream = Files.newInputStream(temporaryStates);
             Input transitionInput = new Input(inputStream);
             Input stateInput = new Input(stateInputStream)) {
            Collection<Record> records = readResults(stateReader, transitionInput);
            Map<Integer, ClassifiedState> stateMap = readMappings(stateReader, stateInput);
            return new Results(records, stateMap);
        }

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

    public class Results {
        public final Collection<Record> records;
        public final Map<Integer, ClassifiedState> stateMappings;

        public Results(Collection<Record> records, Map<Integer, ClassifiedState> stateMappings) {
            this.records = records;
            this.stateMappings = stateMappings;
        }
    }
}
