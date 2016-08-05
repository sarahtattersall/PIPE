package pipe.gui.reachability;

import net.sourceforge.jpowergraph.Edge;
import net.sourceforge.jpowergraph.Node;
import net.sourceforge.jpowergraph.defaults.DefaultGraph;
import net.sourceforge.jpowergraph.layout.Layouter;
import net.sourceforge.jpowergraph.layout.spring.SpringLayoutStrategy;
import net.sourceforge.jpowergraph.lens.*;
import net.sourceforge.jpowergraph.manipulator.dragging.DraggingManipulator;
import net.sourceforge.jpowergraph.manipulator.popup.PopupManipulator;
import net.sourceforge.jpowergraph.swing.SwingJGraphPane;
import net.sourceforge.jpowergraph.swing.SwingJGraphScrollPane;
import net.sourceforge.jpowergraph.swing.manipulator.SwingPopupDisplayer;
import net.sourceforge.jpowergraph.swtswinginteraction.color.JPowerGraphColor;
import pipe.gui.widget.GenerateResultsForm;
import pipe.gui.widget.StateSpaceLoader;
import pipe.gui.widget.StateSpaceLoaderException;
import pipe.reachability.algorithm.*;
import uk.ac.imperial.pipe.exceptions.InvalidRateException;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;
import uk.ac.imperial.state.ClassifiedState;
import uk.ac.imperial.state.Record;

import javax.swing.*;
import java.awt.Container;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * GUI class used to display and run the results of reachability and coverability classes
 */
public class ReachabilityGraph {

    /**
     * Class logger
     */
    private static final Logger LOGGER = Logger.getLogger(ReachabilityGraph.class.getName());

    /**
     * Maximum number of states to graphically display
     */
    private static final int MAX_STATES_TO_DISPLAY = 100;


    private JPanel panel1;

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

    private JLabel textResultsLabel;
    private JPanel textResultsPanel;


    private JRadioButton reachabilityButton;

    private JRadioButton coverabilityButton;

    private JTextField maxStatesField;

    private JPanel stateLoadingPanel;

    private JPanel generatePanel;

    private DefaultGraph graph = new DefaultGraph();

    private StateSpaceLoader stateSpaceLoader;


    /**
     * When selecting use current Petri net the petri net used will be
     *
     * @param loadDialog dialog
     * @param petriNet   current petri net
     */

    public ReachabilityGraph(FileDialog loadDialog, PetriNet petriNet) {
        stateSpaceLoader = new StateSpaceLoader(petriNet, loadDialog);
        setUp();
    }

    /**
     * Set up action listeners
     */
    private void setUp() {
        JPanel pane = setupGraph();
        resultsPanel.add(pane);
        stateLoadingPanel.add(stateSpaceLoader.getMainPanel(), 0);

        ActionListener disableListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reachabilityButton.setEnabled(false);
                coverabilityButton.setEnabled(false);
                includeVanishingStatesCheckBox.setEnabled(false);
            }
        };

        ActionListener enableListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reachabilityButton.setEnabled(true);
                coverabilityButton.setEnabled(true);
                includeVanishingStatesCheckBox.setEnabled(true);
            }
        };
//
        stateSpaceLoader.addPetriNetRadioListener(enableListener);
        stateSpaceLoader.addBinariesListener(disableListener);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveBinaryFiles();
            }
        });
        GenerateResultsForm resultsForm = new GenerateResultsForm(new GenerateResultsForm.GoAction() {
            @Override
            public void go(int threads) {
                calculateResults(threads);
            }
        });
        generatePanel.add(resultsForm.getPanel());
    }

    /**
     * Sets up the graph and returns the JPanel to add to
     * the resultsPanel
     * @return panel
     */
    private JPanel setupGraph() {
        SwingJGraphPane pane = new SwingJGraphPane(graph);
        LensSet lensSet = new LensSet();
        lensSet.addLens(new RotateLens());
        lensSet.addLens(new TranslateLens());
        lensSet.addLens(new ZoomLens());
        CursorLens draggingLens = new CursorLens();
        lensSet.addLens(draggingLens);
        lensSet.addLens(new TooltipLens());
        lensSet.addLens(new LegendLens());
        lensSet.addLens(new NodeSizeLens());
        pane.setLens(lensSet);

        pane.addManipulator(new DraggingManipulator(draggingLens, -1));
        pane.addManipulator(new PopupManipulator(pane, (TooltipLens) lensSet.getFirstLensOfType(TooltipLens.class)));


        pane.setNodePainter(TangibleStateNode.class, TangibleStateNode.getShapeNodePainter());
        pane.setNodePainter(VanishingStateNode.class, VanishingStateNode.getShapeNodePainter());


        pane.setEdgePainter(DirectedTextEdge.class,
                new PIPELineWithTextEdgePainter(JPowerGraphColor.BLACK, JPowerGraphColor.GRAY, false));

        pane.setAntialias(true);

        pane.setPopupDisplayer(new SwingPopupDisplayer(new PIPESwingToolTipListener(),
                new PIPESwingContextMenuListener(graph, new LensSet(), new Integer[]{}, new Integer[]{})));

        return new SwingJGraphScrollPane(pane, lensSet);
    }

    /**
     * Calculates the steady state exploration of a Petri net and stores its results
     * in a temporary file.
     * <p>
     * These results are then read in and turned into a graphical representation using mxGraph
     * which is displayed to the user
     * </p>
     * @param threads number of threads to use to explore the state space
     */
    private void calculateResults(int threads) {
        try {
            StateSpaceExplorer.StateSpaceExplorerResults results =
                    stateSpaceLoader.calculateResults(new StateSpaceLoader.ExplorerCreator() {
                                                          @Override
                                                          public ExplorerUtilities create(PetriNet petriNet) {
                                                              return getExplorerUtilities(petriNet);
                                                          }
                                                      }, new StateSpaceLoader.VanishingExplorerCreator() {
                                                          @Override
                                                          public VanishingExplorer create(ExplorerUtilities utils) {
                                                              return getVanishingExplorer(utils);
                                                          }
                                                      }, threads
                    );
            updateTextResults(results.numberOfStates, results.processedTransitions);
            if (results.numberOfStates <= MAX_STATES_TO_DISPLAY) {
                StateSpaceLoader.Results stateSpace = stateSpaceLoader.loadStateSpace();
                updateGraph(stateSpace.records, stateSpace.stateMappings);
            }

        } catch (InvalidRateException | TimelessTrapException | IOException | InterruptedException | ExecutionException e) {
            LOGGER.log(Level.SEVERE, e.toString());
        } catch (StateSpaceLoaderException e) {
            JOptionPane.showMessageDialog(panel1, e.getMessage(), "GSPN Analysis Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Copies the temporary files to a permanent loaction
     */
    private void saveBinaryFiles() {
        stateSpaceLoader.saveBinaryFiles();
    }

    /**
     * Creates the explorer utilities based upon whether the coverability or reachability graph
     * is being generate
     *
     * @param petriNet to be displayed
     * @return explorer utilities for generating state space
     */
    private ExplorerUtilities getExplorerUtilities(PetriNet petriNet) {
        if (coverabilityButton.isSelected()) {
            return new CoverabilityExplorerUtilities(new UnboundedExplorerUtilities(petriNet));
        }

        return new BoundedExplorerUtilities(petriNet, Integer.valueOf(maxStatesField.getText()));

    }

    /**
     * Vanishing explorer is either a {@link pipe.reachability.algorithm.SimpleVanishingExplorer} if
     * vanishing states are to be included in the graph, else it is {@link pipe.reachability.algorithm.OnTheFlyVanishingExplorer}
     *
     * @param explorerUtilities utilities for analysis 
     * @return vanishing explorer
     */
    private VanishingExplorer getVanishingExplorer(ExplorerUtilities explorerUtilities) {
        if (includeVanishingStatesCheckBox.isSelected()) {
            return new SimpleVanishingExplorer();
        }
        return new OnTheFlyVanishingExplorer(explorerUtilities);
    }

    /**
     * Updates the text results with the number of states and transitions
     *
     * @param states      number of states
     * @param transitions number of transitions
     */
    private void updateTextResults(int states, int transitions) {
        StringBuilder results = new StringBuilder();
        results.append("Results: ").append(states).append(" states and ").append(transitions).append(" transitions");
        textResultsLabel.setText(results.toString());
    }

    /**
     * Updates the mxGraph to display the records
     *
     * @param records  state transitions from a processed Petri net
     * @param stateMap map of stated
     */
    private void updateGraph(Iterable<Record> records, Map<Integer, ClassifiedState> stateMap) {
        graph.clear();
        Map<Integer, Node> nodes = getNodes(stateMap);
        Collection<Edge> edges = getEdges(records, nodes);
        graph.addElements(nodes.values(), edges);
        layoutGraph();
    }

    /**
     * @param stateMap map of states
     * @return All nodes to be added to the graph
     */
    private Map<Integer, Node> getNodes(Map<Integer, ClassifiedState> stateMap) {
        Map<Integer, Node> nodes = new HashMap<>(stateMap.size());
        for (Map.Entry<Integer, ClassifiedState> entry : stateMap.entrySet()) {
            ClassifiedState state = entry.getValue();
            int id = entry.getKey();
            nodes.put(id, createNode(state, id));
        }
        return nodes;
    }

    /**
     * All edges to be added to the graph
     *
     * @param records to add
     * @param nodes to add
     * @return edges 
     */
    private Collection<Edge> getEdges(Iterable<Record> records, Map<Integer, Node> nodes) {
        Collection<Edge> edges = new ArrayList<>();
        for (Record record : records) {
            int state = record.state;
            for (Map.Entry<Integer, Double> entry : record.successors.entrySet()) {
                int succ = entry.getKey();
                edges.add(new DirectedTextEdge(nodes.get(state), nodes.get(succ),
                        String.format("%.2f", entry.getValue())));
            }
        }
        return edges;
    }

    /**
     * Performs laying out of items on the graph
     */
    private void layoutGraph() {
        Layouter layouter = new Layouter(new SpringLayoutStrategy(graph));
        layouter.start();
    }

    /**
     * @param state classified state to be turned into a graph node
     * @param id    state integer id
     * @return Tangible or Vanishing state node corresponding to the state and its integer id representation
     */
    private Node createNode(ClassifiedState state, int id) {
        String label = Integer.toString(id);
        String toolTip = state.toString();
        if (state.isTangible()) {
            return new TangibleStateNode(label, toolTip);
        }
        return new VanishingStateNode(label, toolTip);
    }

    /**
     * Constructor deactivates use current petri net radio button since none is supplied.
     *
     * @param loadDialog file dialog
     */
    public ReachabilityGraph(FileDialog loadDialog) {
        stateSpaceLoader = new StateSpaceLoader(loadDialog);
        setUp();
    }

    /**
     * Main method for running this externally without PIPE
     *
     * @param args command line arguments 
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("ReachabilityGraph");
        FileDialog selector = new FileDialog(frame, "Select petri net", FileDialog.LOAD);
        frame.setContentPane(new ReachabilityGraph(selector).panel1);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * @return main panel of the GUI
     */
    public Container getMainPanel() {
        return panel1;
    }
}
