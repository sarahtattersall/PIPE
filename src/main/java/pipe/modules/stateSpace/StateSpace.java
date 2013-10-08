package pipe.modules.stateSpace;

import pipe.gui.ApplicationSettings;
import pipe.modules.interfaces.IModule;
import pipe.utilities.writers.PNMLWriter;
import pipe.exceptions.TreeTooBigException;
import pipe.views.MarkingView;
import pipe.calculations.myTree;
import pipe.gui.widgets.ButtonBar;
import pipe.gui.widgets.EscapableDialog;
import pipe.gui.widgets.PetriNetChooserPanel;
import pipe.gui.widgets.ResultsHTMLPane;
import pipe.exceptions.EmptyNetException;
import pipe.views.PetriNetView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;


public class StateSpace
        implements IModule
{

    // Main Frame
    private static final String MODULE_NAME = "State Space Analysis";

    private PetriNetChooserPanel sourceFilePanel;
    private ResultsHTMLPane results;

    public void start()
    {
        // Build interface
        PetriNetView pnmlData = ApplicationSettings.getApplicationView().getCurrentPetriNetView();
        EscapableDialog guiDialog =
                new EscapableDialog(ApplicationSettings.getApplicationView(), MODULE_NAME, true);

        // 1 Set layout
        Container contentPane = guiDialog.getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));

        // 2 Add file browser
        sourceFilePanel = new PetriNetChooserPanel("Source net", pnmlData);
        contentPane.add(sourceFilePanel);

        // 3 Add results pane
        contentPane.add(results = new ResultsHTMLPane(pnmlData.getPNMLName()));

        // 4 Add button
        contentPane.add(new ButtonBar("Analyse", analyseButtonClick,
                                      guiDialog.getRootPane()));

        // 5 Make window fit contents' preferred size
        guiDialog.pack();

        // 6 Move window to the middle of the screen
        guiDialog.setLocationRelativeTo(null);

        guiDialog.setVisible(true);
    }


    public String getName()
    {
        return MODULE_NAME;
    }


    /**
     * Analyse button click handler
     */
    private final ActionListener analyseButtonClick = new ActionListener()
    {

        public void actionPerformed(ActionEvent arg0)
        {
            PetriNetView sourceDataLayer = sourceFilePanel.getDataLayer();

            //Get the new marking from the _dataLayer object
            LinkedList<MarkingView>[] markings = sourceDataLayer.getCurrentMarkingVector();
            int[] markup = new int[markings.length];
            for(int k = 0; k < markings.length; k++)
            {
                markup[k] = markings[k].getFirst().getCurrentMarking();
            }


            myTree tree = null;

            String s = "<h2>Petri net state space analysis results</h2>";
            if(sourceDataLayer == null)
            {
                JOptionPane.showMessageDialog(null, "Please, choose a source net",
                                              "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(!sourceDataLayer.hasPlaceTransitionObjects())
            {
                s += "No Petri net objects defined!";
            }
            else if(markup != null)
            {
                try
                {

                    PNMLWriter.saveTemporaryFile(sourceDataLayer,
                                                 this.getClass().getName());

                    tree = new myTree(sourceDataLayer, markup);
                    boolean bounded = !tree.foundAnOmega;
                    boolean safe = !tree.moreThanOneToken;
                    boolean deadlock = tree.noEnabledTransitions;
                    if(tree.tooBig)
                    {
                        s += "<div class=warning> State space tree expansion aborted " +
                                "because it grew too large. Results will be " +
                                "incomplete.</div>";
                    }

                    s += ResultsHTMLPane.makeTable(
                            new String[]{"Bounded", "" + bounded,
                                    "Safe", "" + safe,
                                    "Deadlock", "" + deadlock},
                            2, false, true, false, true);

                    if(deadlock)
                    {
                        s += "<b>Shortest path to deadlock:</b> ";
                        if(tree.pathToDeadlock.length == 0)
                        {
                            s += "Initial state is deadlocked";
                        }
                        else
                        {
                            for(int i = 0; i < tree.pathToDeadlock.length; i++)
                            {
                                int j = tree.pathToDeadlock[i];
                                if(sourceDataLayer.getTransition(j) != null &&
                                        sourceDataLayer.getTransition(j).getName() != null)
                                {
                                    s += sourceDataLayer.getTransition(j).getName() + " ";
                                }
                            }
                        }
                    }
                    results.setEnabled(true);
                }
                catch(TreeTooBigException e)
                {
                    s += e.getMessage();
                }
                catch(OutOfMemoryError oome)
                {
                    System.gc();
                    results.setText("");
                    s = "Memory error: " + oome.getMessage();

                    s += "<br>Not enough memory. Please use a larger heap size."
                            + "<br>"
                            + "<br>Note:"
                            + "<br>The Java heap size can be specified with the -Xmx option."
                            + "<br>E.g., to use 512MB as heap size, the command line looks like this:"
                            + "<br>java -Xmx512m -classpath ...\n";
                    results.setText(s);
                    return;
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    s = "<br>Error" + e.getMessage();
                    results.setText(s);
                    return;
                }
            }
            else
            {
                s += "Error performing analysis";
            }
            results.setText(s);
        }
    };


    //<Marc>
    public boolean[] getStateSpace(PetriNetView sourceDataLayer) throws EmptyNetException, TreeTooBigException
    {
        boolean[] result = new boolean[3];

        //Get the new marking from the _dataLayer object
        LinkedList<MarkingView>[] markings = sourceDataLayer.getCurrentMarkingVector();
        int[] markup = new int[markings.length];
        for(int k = 0; k < markings.length; k++)
        {
            markup[k] = markings[k].getFirst().getCurrentMarking();
        }


        myTree tree = null;

        if(!sourceDataLayer.hasPlaceTransitionObjects())
        {
            throw new EmptyNetException();
        }
        else if(markup != null)
        {
            tree = new myTree(sourceDataLayer, markup);
            result[0] = !tree.foundAnOmega;
            result[1] = !tree.moreThanOneToken;
            result[2] = tree.noEnabledTransitions;
            if(tree.tooBig)
            {

            }
        }

        return result;
    }
    //</Marc>


    public boolean[] getStateSpace()
    {

        return new boolean[3];
    }

}
