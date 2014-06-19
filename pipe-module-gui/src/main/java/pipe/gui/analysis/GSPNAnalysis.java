package pipe.gui.analysis;

import org.rendersnake.HtmlCanvas;
import pipe.gui.widget.HTMLPane;
import pipe.gui.widget.StateSpaceLoader;
import pipe.gui.widget.StateSpaceLoaderException;
import pipe.reachability.algorithm.*;
import pipe.steadystate.algorithm.ParallelGaussSeidel;
import pipe.steadystate.algorithm.SteadyStateSolver;
import pipe.steadystate.metrics.TokenMetrics;
import pipe.steadystate.metrics.TransitionMetrics;
import uk.ac.imperial.pipe.exceptions.InvalidRateException;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;
import uk.ac.imperial.state.ClassifiedState;
import uk.ac.imperial.state.Record;

import javax.swing.*;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.rendersnake.HtmlAttributesFactory.class_;
import static org.rendersnake.HtmlAttributesFactory.type;

/**
 * Performs the exploration and steady state analysis of a Petri net.
 * Displays useful performance analysis metrics
 */
public class GSPNAnalysis {

    public static final String HTML_STYLE = "body{font-family:Arial,Helvetica,sans-serif;text-align:center;" +
            "background:#ffffff}" +
            "td.colhead{font-weight:bold;text-align:center;" +
            "background:#ffffff}" +
            "td.rowhead{font-weight:bold;background:#ffffff}" +
            "td.cell{text-align:center;padding:5px,0}" +
            "tr.even{background:#a0a0d0}" +
            "tr.odd{background:#c0c0f0}" +
            "td.empty{background:#ffffff}";

    /**
     * If the state space is larger than this then we cannot display the results as tables.
     */
    private static final int MAX_DISPLAY_STATES = 200;

    /**
     * Results HTML pane for displaying info
     */
    HTMLPane resultsPane = new HTMLPane();

    private JLabel textResultsLabel;

    private JButton goButton;

    private JPanel mainPanel;

    /**
     * Results panel houses HTML results
     */
    private JPanel resultsPanel;

    private JPanel loadPanel;

    private StateSpaceLoader stateSpaceLoader;

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public GSPNAnalysis(FileDialog fileDialog) {
        stateSpaceLoader = new StateSpaceLoader(fileDialog);
        setUp();
    }

    public GSPNAnalysis(PetriNet petriNet, FileDialog fileDialog) {
        stateSpaceLoader = new StateSpaceLoader(petriNet, fileDialog);
        setUp();
    }


    /**
     * Sets up the UI
     */
    private void setUp() {

        loadPanel.add(stateSpaceLoader.getMainPanel(), 0);
        resultsPanel.add(resultsPane);
        goButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSteadyState();
            }
        });
    }


    /**
     * Loads the steady state and if the number of states is < MAX_DISPLAY_STATES we display steady state information
     */
    private void showSteadyState() {
        try {
            StateSpaceExplorer.StateSpaceExplorerResults results =
                    stateSpaceLoader.calculateResults(new StateSpaceLoader.ExplorerCreator() {
                                                          @Override
                                                          public ExplorerUtilities create(PetriNet petriNet) {
                                                              return new BoundedExplorerUtilities(petriNet, 1000000);
                                                          }
                                                      }, new StateSpaceLoader.VanishingExplorerCreator() {
                                                          @Override
                                                          public VanishingExplorer create(ExplorerUtilities utils) {
                                                              return new OnTheFlyVanishingExplorer(utils);
                                                          }
                                                      }
                    );

            HtmlCanvas html = new HtmlCanvas();
            html.html().head();
            html.style(type("text/css").media("screen")).content(HTML_STYLE);
            html._head();
            html.body();


            if (results.numberOfStates < MAX_DISPLAY_STATES) {
                StateSpaceLoader.Results stateSpace = stateSpaceLoader.loadStateSpace();
                solveSteadyState(stateSpace.records, stateSpace.stateMappings, html);
            } else {
                html.write("State space is too large to show tabular results");
                html.br();
                html.write("Number of states: " + results.numberOfStates);
                html.br();
                html.write("Number of transitions: " + results.processedTransitions);
            }

            html._body()._html();
            resultsPane.setText(html.toHtml());


        } catch (IOException | InterruptedException | ExecutionException | InvalidRateException | TimelessTrapException e) {
            e.printStackTrace();
        } catch (StateSpaceLoaderException e) {
            JOptionPane.showMessageDialog(mainPanel, e.getMessage(), "GSPN Analysis Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Solves the steady state and adds the results to the html canvas
     * @param records
     * @param stateMappings
     * @param html
     */
    private void solveSteadyState(Collection<Record> records, Map<Integer, ClassifiedState> stateMappings,
                                  HtmlCanvas html) {

        ExecutorService executorService = Executors.newFixedThreadPool(8);

        try {
            displayStates(html, stateMappings);
            SteadyStateSolver steadyStateSolver = new ParallelGaussSeidel(8, executorService, 10);
            Map<Integer, Double> steadyState = steadyStateSolver.solve(new ArrayList<>(records));

            displaySteadyState(html, steadyState);
            displayMetrics(html, steadyState, stateMappings);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdownNow();
        }
    }

    /**
     * Displays the state mappings for each token
     * @param html
     * @param stateMappings
     * @throws IOException
     */
    private void displayStates(HtmlCanvas html, Map<Integer, ClassifiedState> stateMappings) throws IOException {
        Collection<String> tokens = getTokens(stateMappings.values());
        for (String token : tokens) {
            buildTokenTable(html, stateMappings, token);
        }
    }

    /**
     * Displays the steady state information in a table
     * @param html
     * @param steadyState
     * @throws IOException
     */
    private void displaySteadyState(HtmlCanvas html, Map<Integer, Double> steadyState) throws IOException {
        List<TableRow> rows = new LinkedList<>();
        for (Map.Entry<Integer, Double> entry : steadyState.entrySet()) {
            rows.add(new TableRow(entry.getKey().toString(), doubleToString(entry.getValue())));
        }
        addTable(html, rows, Arrays.asList("State", "Distribution"), "Steady state distribution");
    }

    /**
     * Display Performance analysis metrics for the steady state
     *
     * Displays:
     *  - the average number of tokens on each place
     *  - the average transition  throughput if loaded from a Petri net
     *
     * @param html
     * @param steadyState
     * @param stateMappings
     * @throws IOException
     */
    private void displayMetrics(HtmlCanvas html, Map<Integer, Double> steadyState,
                                Map<Integer, ClassifiedState> stateMappings) throws IOException {
        Map<String, Map<String, Double>> averageTokens = TokenMetrics.averageTokensOnPlace(stateMappings, steadyState);
        buildAverageMetrics(averageTokens, html);
        if (!stateSpaceLoader.isBinaryLoadChecked()) {
            PetriNet petriNet = stateSpaceLoader.getPetriNet();
            Map<String, Double> throughputs =
                    TransitionMetrics.getTransitionThroughput(stateMappings, steadyState, petriNet);
            displayThroughputs(throughputs, html);
        }


    }

    /**
     *
     * @param states
     * @return a sorted list of tokens contained within the states
     */
    private Collection<String> getTokens(Collection<ClassifiedState> states) {
        Collection<String> tokens = new ArrayList<>();
        if (!states.isEmpty()) {
            ClassifiedState state = states.iterator().next();
            Collection<String> places = state.getPlaces();
            if (!places.isEmpty()) {
                String place = places.iterator().next();
                tokens.addAll(state.getTokens(place).keySet());
            }
        }
        return tokens;
    }

    /**
     *
     * @param html
     * @param stateMappings
     * @param token
     * @throws IOException
     */
    private void buildTokenTable(HtmlCanvas html, Map<Integer, ClassifiedState> stateMappings, String token)
            throws IOException {
        List<Integer> ids = new ArrayList<>(stateMappings.keySet());
        Collections.sort(ids);
        List<String> places = getPlaces(stateMappings);

        List<TableRow> rows = new ArrayList<>();
        for (Integer id : ids) {
            ClassifiedState state = stateMappings.get(id);
            TableRow row = new TableRow(id.toString());
            for (String place : places) {
                Integer count = state.getTokens(place).get(token);
                row.addCell(count.toString());
            }
            rows.add(row);
        }
        places.add(0, "State");
        addTable(html, rows, places, "State markings for " + token + " token");
    }

    /**
     *
     * @param value
     * @return string representation rounded to 3 decimal places
     */
    private String doubleToString(Double value) {
        return String.format("%.3f", value);
    }

    /**
     * Add the table to the HTML canvas ready for rendering
     *
     * @param html      html canvas
     * @param tableRows table rows, these should all be the same lenght
     * @param headers   table headers
     * @param title     itle of the table
     * @throws IOException
     */
    public void addTable(HtmlCanvas html, List<TableRow> tableRows, List<String> headers, String title)
            throws IOException {
        html.h2().content(title);
        html.table();

        html.tr();
        for (String header : headers) {
            html.th().content(header);
        }
        html._tr();

        int i = 0;
        for (TableRow row : tableRows) {
            String clazz = (i % 2 == 0) ? "even" : "odd";
            html.tr(class_(clazz));
            for (String column : row.getCells()) {
                html.td().content(column);
            }
            html._tr();
            i++;
        }
        html._table();
    }

    /**
     * Creates and adds to the html canvas a table for each token colour
     * containing the average number of tokens in the place
     * @param averageTokens
     * @param html
     * @throws IOException
     */
    private void buildAverageMetrics(Map<String, Map<String, Double>> averageTokens, HtmlCanvas html)
            throws IOException {
        List<String> places = new ArrayList<>(averageTokens.keySet());
        Collections.sort(places);
        List<String> tokens = new ArrayList<>(averageTokens.get(places.get(0)).keySet());
        Collections.sort(tokens);

        List<TableRow> rows = new ArrayList<>();
        for (String place : places) {
            Map<String, Double> average = averageTokens.get(place);
            TableRow row = new TableRow(place);
            for (String token : tokens) {
                Double count = average.get(token);
                row.addCell(doubleToString(count));
            }
            rows.add(row);
        }
        tokens.add(0, "Place");
        addTable(html, rows, tokens, "Average token counts");
    }

    /**
     * Creates and displays a table for the given throughputs
     * @param throughputs
     * @param html
     * @throws IOException
     */
    private void displayThroughputs(Map<String, Double> throughputs, HtmlCanvas html) throws IOException {
        List<String> transitions = new ArrayList<>(throughputs.keySet());
        Collections.sort(transitions);

        List<TableRow> rows = new ArrayList<>();
        for (String transition : transitions) {
            Double average = throughputs.get(transition);
            rows.add(new TableRow(transition, doubleToString(average)));
        }
        addTable(html, rows, Arrays.asList("Transition", "Throughput"), "Average transition throughputs");
    }

    /**
     *
     * @param stateMappings
     * @return a list of places in the state mappings
     */
    private List<String> getPlaces(Map<Integer, ClassifiedState> stateMappings) {
        List<String> places = new ArrayList<>();
        places.addAll(stateMappings.values().iterator().next().getPlaces());
        Collections.sort(places);
        return places;
    }

    /**
     * Main method for running this externally without PIPE
     *
     * @param args
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Steady state results");
        FileDialog selector = new FileDialog(frame, "Select petri net", FileDialog.LOAD);

        frame.setContentPane(new GSPNAnalysis(selector).mainPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Useful class for creating fors for HTML tables displayed in the output
     */
    public static class TableRow {
        /**
         * Table row cell values
         */
        List<String> cells = new ArrayList<>();

        /**
         * @param cells
         */
        public TableRow(String... cells) {
            this.cells.addAll(Arrays.asList(cells));
        }

        /**
         * Default constructor with no cells
         */
        public TableRow() {
        }

        /**
         * @return cells
         */
        public List<String> getCells() {
            return cells;
        }

        /**
         * Append this value onto the cells, that is this value will become the last column
         *
         * @param cell
         */
        public void addCell(String cell) {
            cells.add(cell);
        }

    }
}
