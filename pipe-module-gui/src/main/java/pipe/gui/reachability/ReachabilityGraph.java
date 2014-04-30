package pipe.gui.reachability;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import pipe.io.PetriNetIOImpl;
import pipe.io.PetriNetReader;
import pipe.models.petrinet.PetriNet;
import pipe.parsers.UnparsableException;
import pipe.reachability.algorithm.CachingExplorerUtilities;
import pipe.reachability.algorithm.ExplorerUtilities;
import pipe.reachability.algorithm.TimelessTrapException;
import pipe.reachability.algorithm.VanishingExplorer;
import pipe.reachability.algorithm.state.SeralizingStateWriter;
import pipe.reachability.algorithm.sequential.SequentialStateSpaceExplorer;
import pipe.reachability.algorithm.state.OnTheFlyVanishingExplorer;
import pipe.reachability.algorithm.state.SimpleVanishingExplorer;
import pipe.reachability.algorithm.state.StateWriter;
import pipe.reachability.algorithm.state.StateSpaceExplorer;
import pipe.reachability.io.*;
import pipe.reachability.io.MultiTransitionStateSpaceExplorationReader;
import pipe.reachability.io.StateSpaceExplorationReader;
import pipe.reachability.state.Record;
import pipe.reachability.state.State;

import javax.swing.*;
import javax.xml.bind.JAXBException;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ReachabilityGraph {

    /**
     * For loading Petri nets to explore
     */
    private final FileDialog fileDialog;

    /**
     * Graphical representation of reachability
     */
    private final mxGraph graph = new mxGraph();

    private JPanel panel1;

    private JButton goButton;

    private JCheckBox useExistingPetriNetCheckBox;

    private JTextField petriNetNameLabel;

    private JButton loadButton;

    private JPanel resultsPanel;

    private JCheckBox includeVanishingStatesCheckBox;

    /**
     * Last loaded Petri net via the load dialog
     */
    private PetriNet lastLoadedPetriNet;

    public ReachabilityGraph(FileDialog fileDialog) {
        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        graphComponent.setPreferredSize(new Dimension(500, 500));
        resultsPanel.add(graphComponent);

        goButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateResults();
            }
        });
        this.fileDialog = fileDialog;
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadPetriNet();
            }
        });
        useExistingPetriNetCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                int state = e.getStateChange();
                if (state == ItemEvent.DESELECTED) {
                    loadButton.setEnabled(true);
                } else if (state == ItemEvent.SELECTED) {
                    loadButton.setEnabled(false);
                }
            }
        });
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
            WriterFormatter formatter = new ByteWriterFormatter();
            Path temporary = Files.createTempFile("rea", ".tmp");
            try (OutputStream stream = Files.newOutputStream(temporary);
                 ObjectOutputStream objectOutputStream = new ObjectOutputStream(stream)) {
                writeStateSpace(formatter, objectOutputStream);

                try (InputStream inputStream = Files.newInputStream(temporary);
                     ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
                    Collection<Record> records = readResults(formatter, objectInputStream);
                    updateGraph(records);
                }
            }
        } catch (TimelessTrapException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the file dialog and saves the selected Petri net into lastLoadedPetriNet
     * for use when calculating the state space exploration
     */
    private void loadPetriNet() {
        fileDialog.setVisible(true);

        File[] files = fileDialog.getFiles();
        if (files.length > 0) {
            File path = files[0];

            try {
                PetriNetReader petriNetIO = new PetriNetIOImpl();
                lastLoadedPetriNet = petriNetIO.read(path.getAbsolutePath());
                petriNetNameLabel.setText(path.getName());
            } catch (JAXBException e) {
                e.printStackTrace();
            } catch (UnparsableException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Writes the petriNet state space out to a temporary file which is referenced by the objectOutputStream
     *
     * @param formatter          format in which to write the results to
     * @param objectOutputStream stream to write state space to
     * @throws TimelessTrapException if the state space cannot be generated due to cyclic vanishing states
     */
    private void writeStateSpace(WriterFormatter formatter, ObjectOutputStream objectOutputStream)
            throws TimelessTrapException {
        PetriNet petriNet = (useExistingPetriNetCheckBox.isSelected() ? null : lastLoadedPetriNet);
        StateWriter tangibleExplorer = new SeralizingStateWriter(formatter, objectOutputStream);
        ExplorerUtilities explorerUtilites = new CachingExplorerUtilities(petriNet);
        VanishingExplorer vanishingExplorer = getVanishingExplorer(explorerUtilites);
        StateSpaceExplorer stateSpaceExplorer =
                new SequentialStateSpaceExplorer(tangibleExplorer, vanishingExplorer, explorerUtilites);
        stateSpaceExplorer.generate();
    }

    /**
     * Reads results of steady state exploration into a collection of records
     *
     * @param formatter
     * @param objectInputStream
     * @return state transitions with rates
     * @throws IOException
     */
    private Collection<Record> readResults(WriterFormatter formatter, ObjectInputStream objectInputStream)
            throws IOException {
        StateSpaceExplorationReader reader = new MultiTransitionStateSpaceExplorationReader(formatter);
        return reader.getRecords(objectInputStream);
    }

    /**
     *
     * Updates the mxGraph to display the records
     *
     * @param records state transitions from a processed Petri net
     */
    private void updateGraph(Iterable<Record> records) {
        graph.removeCells(graph.getChildVertices(graph.getDefaultParent()));
        mxIGraphLayout layout = new mxHierarchicalLayout(graph);

        Object parent = graph.getDefaultParent();
        graph.getModel().beginUpdate();
        mxStylesheet stylesheet = graph.getStylesheet();
        Map<String, Object> vertexStyles = stylesheet.getDefaultVertexStyle();
        vertexStyles.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);


        Map<State, Object> verticies = new HashMap<>();
        try {
            graph.clearSelection();
            for (Record record : records) {
                Object state = getInsertedState(verticies, record.state, graph);
                Object successor = getInsertedState(verticies, record.successor, graph);
                graph.insertEdge(parent, null, "", state, successor);
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

    /**
     * Creates/retreives a graphical representation of the state. If it does not exist
     * on the canvas already it is created and added to the verticies map
     *
     * @param verticies verticies map representing states already existing on the graph to their
     *                  graphical representation
     * @param state     state to represent graphically
     * @return graphical vertex representation for the State
     */
    private Object getInsertedState(Map<State, Object> verticies, State state, mxGraph graph) {
        if (verticies.containsKey(state)) {
            return verticies.get(state);
        }
        Object parent = graph.getDefaultParent();
        Object vertexState = graph.insertVertex(parent, null, verticies.size(), 0, 0, 30, 30, getColor(state));
        verticies.put(state, vertexState);
        return vertexState;
    }

    /**
     * Return settings string for inserting a vertex into a graph.
     * <p/>
     * Tangible states are blue whilst vanishing states are red.
     *
     * @param state
     * @return settings string
     */
    private String getColor(State state) {
        if (state.isTangible()) {
            return "fillColor=#99CCFF";
        }
        return "fillColor=#FF8566";
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("ReachabilityGraph");
        FileDialog selector = new FileDialog(frame, "Select petri net", FileDialog.LOAD);
        frame.setContentPane(new ReachabilityGraph(selector).panel1);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }

    private void createUIComponents() {
    }
}
