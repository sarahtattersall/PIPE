package pipe.gui.reachability;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import pipe.io.PetriNetIOImpl;
import pipe.io.PetriNetReader;
import pipe.models.petrinet.PetriNet;
import pipe.parsers.UnparsableException;
import pipe.reachability.algorithm.sequential.SequentialStateSpaceExplorer;
import pipe.reachability.algorithm.StateSpaceExplorer;
import pipe.reachability.algorithm.TimelessTrapException;
import pipe.reachability.io.ByteWriterFormatter;
import pipe.reachability.io.MultiTransitionReachabilityReader;
import pipe.reachability.io.ReachabilityReader;
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

    private final FileDialog fileDialog;

    private JPanel panel1;

    private JButton goButton;

    private JCheckBox useExistingPetriNetCheckBox;

    private JTextField petriNetNameLabel;

    private JButton loadButton;

    private JPanel resultsPanel;

    private final mxGraph graph = new mxGraph();

    /**
     * Last loaded Petri net
     */
    private PetriNet lastLoadedPetriNet;

    public ReachabilityGraph(FileDialog fileDialog) {
        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        graphComponent.setPreferredSize(new Dimension(500,500));
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

    private void calculateResults() {
        try {
            PetriNet petriNet = (useExistingPetriNetCheckBox.isSelected() ? null : lastLoadedPetriNet);
            ByteWriterFormatter formatter = new ByteWriterFormatter();
            StateSpaceExplorer stateSpaceExplorer = new SequentialStateSpaceExplorer(tangibleExplorer,
                    vanishingExplorer, explorerUtilites);

            Path temporary = Files.createTempFile("rea", ".tmp");
            try (OutputStream stream = Files.newOutputStream(temporary);
                 ObjectOutputStream objectOutputStream = new ObjectOutputStream(stream)) {
                stateSpaceExplorer.generate(objectOutputStream);
                try (InputStream inputStream = Files.newInputStream(temporary);
                     ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
                    ReachabilityReader reader = new MultiTransitionReachabilityReader(formatter);
                    Collection<Record> records = reader.getRecords(objectInputStream);
                    updateGraph(records);
                }
            }
        } catch (TimelessTrapException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("ReachabilityGraph");
        FileDialog selector = new FileDialog(frame, "Select petri net", FileDialog.LOAD);
        frame.setContentPane(new ReachabilityGraph(selector).panel1);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }

    private void createUIComponents() {}

    private void updateGraph(Collection<Record> records) {
        graph.removeCells(graph.getChildVertices(graph.getDefaultParent()));
        mxGraphLayout layout = new mxHierarchicalLayout(graph);

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
     *
     * Return settings string for inserting a vertex into a graph.
     *
     * Tangible states are blue whilst vanishing states are red.
     * @param state
     * @return settings string
     */
    private String getColor(State state) {
        if (state.isTangible()) {
            return "fillColor=#99CCFF";
        }
        return "fillColor=#FF8566";
    }
}
