/**
 * Incidence and Marking module
 * @author James D Bloom 2003-03-12
 * @author Maxim 2004 (better GUI, cleaned up code)
 */
package pipe.modules.matrices;

import pipe.gui.widgets.PetriNetChooserPanel;
import pipe.gui.widgets.ResultsHTMLPane;
import pipe.modules.interfaces.IModule;
import pipe.views.MarkingView;
import pipe.views.PetriNetView;
import pipe.views.PlaceView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class Matrices implements IModule {

    private static final String MODULE_NAME = "Incidence & Marking";

    /**
     * Calculate button click handler
     */
    private final ActionListener calculateButtonClick = new ActionListener() {

        public void actionPerformed(ActionEvent arg0) {
            PetriNetView data = sourceFilePanel.getDataLayer();
            String s = "<h2>Petri net incidence and marking</h2>";
            if (data == null) {
                return;
            }
            if (!data.hasPlaceTransitionObjects()) {
                s += "No Petri net objects defined!";
            } else {
                try {

                    //                    PNMLWriter.saveTemporaryFile(data, this.getClass().getName());
                    //
                    //                    s += ResultsHTMLPane.makeTable(new String[]{
                    //                            "Forwards incidence matrix <i>I<sup>+</sup></i>",
                    //                            renderMatrix(data, data.getActiveTokenView().getForwardsIncidenceMatrix(
                    //                                    data.getModel().getArcs(), data.getModel().getTransitions(),
                    //                                    data.getModel().getPlaces()))
                    //                    }, 1, false, false, true, false);
                    //                    s += ResultsHTMLPane.makeTable(new String[]{
                    //                            "Backwards incidence matrix <i>I<sup>-</sup></i>",
                    //                            renderMatrix(data, data.getActiveTokenView().getBackwardsIncidenceMatrix(
                    //                                    data.getModel().getArcs(), data.getModel().getTransitions(),
                    //                                    data.getModel().getPlaces()))
                    //                    }, 1, false, false, true, false);
                    //                    s += ResultsHTMLPane.makeTable(new String[]{
                    //                            "Combined incidence matrix <i>I</i>",
                    //                            renderMatrix(data, data.getActiveTokenView().getIncidenceMatrix(
                    //                                    data.getModel().getArcs(), data.getModel().getTransitions(),
                    //                                    data.getModel().getPlaces()))
                    //                    }, 1, false, false, true, false);
                    //                    s += ResultsHTMLPane.makeTable(new String[]{
                    //                            "Inhibition matrix <i>H</i>",
                    //                            renderMatrix(data, data.getActiveTokenView().getInhibitionMatrix(
                    //                                    data.getInhibitorsArrayList(), data.getTransitionsArrayList(),
                    //                                    data.getPlacesArrayList()))
                    //                    }, 1, false, false, true, false);
                    //                    s += ResultsHTMLPane.makeTable(new String[]{
                    //                            "Marking",
                    //                            renderMarkingMatrices(data)
                    //                    }, 1, false, false, true, false);
                    //                    s += ResultsHTMLPane.makeTable(new String[]{
                    //                            "Enabled transitions",
                    //                            renderTransitionStates(data)
                    //                    }, 1, false, false, true, false);
                } catch (OutOfMemoryError oome) {
                    System.gc();
                    results.setText("");
                    s = "Memory error: " + oome.getMessage();

                    s += "<br>Not enough memory. Please use a larger heap size." + "<br>" + "<br>Note:"
                            + "<br>The Java heap size can be specified with the -Xmx option."
                            + "<br>E.g., to use 512MB as heap size, the command line looks like this:"
                            + "<br>java -Xmx512m -classpath ...\n";
                    results.setText(s);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    s = "<br>Error" + e.getMessage();
                    results.setText(s);
                    return;
                }
            }
            results.setEnabled(true);
            results.setText(s);
        }
    };

    private PetriNetChooserPanel sourceFilePanel;

    private ResultsHTMLPane results;

    public String getName() {
        return MODULE_NAME;
    }

    public void start() {
        //        // Check if this net is a CGSPN. If it is, then this
        //        // module won't work with it and we must convert it.
        //        PetriNetView pnmlData = ApplicationSettings.getApplicationView().getCurrentPetriNetView();
        //
        //        if(pnmlData.getEnabledTokenClassNumber() > 1){
        //			Expander expander = new Expander(pnmlData);
        //			pnmlData = expander.unfoldOld();
        //			JOptionPane.showMessageDialog(null, "This is CGSPN. The analysis will only apply to default color (black)",
        //					"Information", JOptionPane.INFORMATION_MESSAGE);
        //		}
        //
        ////        if(pnmlData.getTokenViews().size() > 1)
        ////        {
        ////            Expander expander = new Expander(pnmlData);
        ////            pnmlData = expander.unfoldOld();
        ////        }
        //        // Build interface
        //        EscapableDialog guiDialog =
        //                new EscapableDialog(ApplicationSettings.getApplicationView(), MODULE_NAME, true);
        //
        //        // 1 Set layout
        //        Container contentPane = guiDialog.getContentPane();
        //        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
        //
        //        // 2 Add file browser
        //        sourceFilePanel = new PetriNetChooserPanel("Source net", pnmlData);
        //        contentPane.add(sourceFilePanel);
        //
        //        // 3 Add results pane
        //        results = new ResultsHTMLPane(pnmlData.getPNMLName());
        //        contentPane.add(results);
        //
        //        // 4 Add button
        //        contentPane.add(new ButtonBar("Calculate", calculateButtonClick,
        //                                      guiDialog.getRootPane()));
        //
        //        // 5 Make window fit contents' preferred size
        //        guiDialog.pack();
        //
        //        // 6 Move window to the middle of the screen
        //        guiDialog.setLocationRelativeTo(null);
        //
        //        guiDialog.setVisible(true);
    }

    public String renderMatrix(PetriNetView data, int[][] matrix) {
        if ((matrix.length == 0) || (matrix[0].length == 0)) {
            return "n/a";
        }

        ArrayList result = new ArrayList();
        // add headers to table
        result.add("");
        for (int i = 0; i < matrix[0].length; i++) {
            result.add(data.getTransition(i).getName());
        }

        for (int i = 0; i < matrix.length; i++) {
            result.add(data.getPlace(i).getName());
            for (int j = 0; j < matrix[i].length; j++) {
                result.add(Integer.toString(matrix[i][j]));
            }
        }

        return ResultsHTMLPane.makeTable(result.toArray(), matrix[0].length + 1, false, true, true, true);
    }

    private String renderMarkingMatrices(PetriNetView data) {
        PlaceView[] placeViews = data.places();
        if (placeViews.length == 0) {
            return "n/a";
        }

        List<MarkingView>[] markings = data.getInitialMarkingVector();
        int[] initial = new int[markings.length];
        for (int i = 0; i < markings.length; i++) {
            if (markings[i].size() == 0) {
                initial[i] = 0;
            } else {
                initial[i] = markings[i].get(0).getCurrentMarking();
            }
        }

        markings = data.getCurrentMarkingVector();
        int[] current = new int[markings.length];
        for (int i = 0; i < markings.length; i++) {
            current[i] = markings[i].get(0).getCurrentMarking();
        }

        ArrayList result = new ArrayList();
        // add headers t o table
        result.add("");
        for (PlaceView placeView : placeViews) {
            result.add(placeView.getName());
        }

        result.add("Initial");
        for (int anInitial : initial) {
            result.add(Integer.toString(anInitial));
        }
        result.add("Current");
        for (int aCurrent : current) {
            result.add(Integer.toString(aCurrent));
        }

        return ResultsHTMLPane.makeTable(result.toArray(), placeViews.length + 1, false, true, true, true);
    }

    private String renderTransitionStates(PetriNetView data) throws Exception {
        //        TransitionView[] transitionViews = data.getTransitionViews();
        //        if(transitionViews.length == 0)
        //        {
        //            return "n/a";
        //        }
        //
        //        ArrayList result = new ArrayList();
        //        data.setEnabledTransitions();
        //        for(TransitionView transitionView1 : transitionViews)
        //        {
        //            result.add(transitionView1.getName());
        //        }
        //        for(TransitionView transitionView : transitionViews)
        //        {
        //            result.add((transitionView.isEnabled() ? "yes" : "no"));
        //        }
        //        data.resetEnabledTransitions();
        //
        //        return ResultsHTMLPane.makeTable(
        //                result.toArray(), transitionViews.length, false, true, true, false);
        return "";
    }
}
