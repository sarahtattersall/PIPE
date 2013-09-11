/**
 * Simulation IModule
 * @author James D Bloom (UI)
 * @author Clare Clark (Maths)
 * @author Maxim (replacement UI and cleanup)
 *
 * @author Davd Patterson (handle null return from fireRandomTransition)
 *
 */
package pipe.modules.simulation;

import pipe.gui.ApplicationSettings;
import pipe.gui.widgets.ButtonBar;
import pipe.gui.widgets.EscapableDialog;
import pipe.gui.widgets.PetriNetChooserPanel;
import pipe.gui.widgets.ResultsHTMLPane;
import pipe.modules.interfaces.IModule;
import pipe.utilities.writers.PNMLWriter;
import pipe.views.MarkingView;
import pipe.views.PetriNetView;
import pipe.views.TransitionView;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;


public class Simulation extends SwingWorker
        implements IModule
{

    private static final String MODULE_NAME = "Simulation";

    private PetriNetChooserPanel sourceFilePanel;
    private ResultsHTMLPane results;

    private JTextField jtfFirings, jtfCycles;

    public void start()
    {
        PetriNetView pnmlData = ApplicationSettings.getApplicationView().getCurrentPetriNetView();
        EscapableDialog guiDialog =
                new EscapableDialog(ApplicationSettings.getApplicationView(), MODULE_NAME, true);

        // 1 Set layout
        Container contentPane = guiDialog.getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));

        // 2 Add file browser
        sourceFilePanel = new PetriNetChooserPanel("Source net", pnmlData);
        contentPane.add(sourceFilePanel);

        // 2.5 Add edit boxes
        JPanel settings = new JPanel();
        settings.setLayout(new BoxLayout(settings, BoxLayout.LINE_AXIS));
        settings.add(new JLabel("Firings:"));
        settings.add(Box.createHorizontalStrut(5));
        settings.add(jtfFirings = new JTextField("100", 5));
        settings.add(Box.createHorizontalStrut(10));
        settings.add(new JLabel("Replications:"));
        settings.add(Box.createHorizontalStrut(5));
        settings.add(jtfCycles = new JTextField("5", 5));
        settings.setBorder(new TitledBorder(new EtchedBorder(),
                                            "Simulation parameters"));
        settings.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                                              settings.getPreferredSize().height));
        contentPane.add(settings);

        // 3 Add results pane
        results = new ResultsHTMLPane(pnmlData.getPNMLName());
        contentPane.add(results);

        // 4 Add button
        contentPane.add(new ButtonBar("Simulate", simulateButtonClick,
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
    //if (!sourceDataLayer.getPetriNetObjects().hasNext()) {

    /**
     * Simulate button click handler
     */
    private final ActionListener simulateButtonClick = new ActionListener()
    {

        public void actionPerformed(ActionEvent arg0)
        {
            PetriNetView sourceDataLayer = sourceFilePanel.getDataLayer();
            String s = "<h2>Petri net simulation results</h2>";
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
            else
            {
                try
                {

                    PNMLWriter.saveTemporaryFile(sourceDataLayer,
                                                 this.getClass().getName());

                    int firings = Integer.parseInt(jtfFirings.getText());
                    int cycles = Integer.parseInt(jtfCycles.getText());
                    s += simulate(sourceDataLayer, cycles, firings);
                    results.setEnabled(true);
                }
                catch(NumberFormatException e)
                {
                    s += "Invalid parameter!";
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
            results.setText(s);
        }
    };


    public String simulate(PetriNetView data, int cycles, int firings)
    {
        data.storeCurrentMarking();

        LinkedList<MarkingView>[] markings = data.getInitialMarkingVector();
        if(markings == null)
            return "No markings present. Try to add coloured tokens.";
        int length = markings.length;
        int[] marking = new int[length];

        for(int i = 0; i < length; i++)
        {
            if(markings[i]!= null && markings[i].size() > 0)
            {
                MarkingView first = markings[i].getFirst();
                if(first != null)
                    marking[i] = first.getCurrentMarking();
            }
        }
        double averageTokens[] = new double[length];
        int totalTokens[] = new int[length];
        double avgResult[] = new double[length];
        double errorResult[] = new double[length];

        double overallAverages[][] = new double[cycles][length];

        int i, j;

        // Initialise arrays
        for(i = 0; i < length; i++)
        {
            averageTokens[i] = 0;
            totalTokens[i] = 0;
            avgResult[i] = 0;
            errorResult[i] = 0;
        }

        //Initialise matrices
        for(i = 0; i < cycles; i++)
        {
            for(j = 0; j < length; j++)
            {
                overallAverages[i][j] = 0;
            }
        }

        for(i = 0; i < cycles; i++)
        {
            //Need to initialise the transition count again
            int transCount = 0;

            //Get initial marking
            markings = data.getInitialMarkingVector();
            marking = new int[length];
            for(int k = 0; k < length; k++)
            {
                if(markings[k]!= null && markings[k].size() > 0)
                {
                    MarkingView first = markings[k].getFirst();
                    if(first!=null)
                        marking[k] = first.getCurrentMarking();
                }
            }
            if(ApplicationSettings.getApplicationView() != null) data.restorePreviousMarking();

            //Initialise matrices for each new cycle
            for(j = 0; j < length; j++)
            {
                averageTokens[j] = 0;
                totalTokens[j] = 0;
                avgResult[j] = 0;
            }

            //Add initial marking to the total
            addTotal(marking, totalTokens);

            // Fire as many transitions as required and evaluate averages
            // Changed by Davd Patterson April 24, 2007
            // Handle a null return from fireRandomTransition if no transition
            // can be found.
            for(j = 0; j < firings; j++)
            {
                System.out.println("Firing " + j + " now");
                //Fire a random transition
                TransitionView fired = data.getRandomTransition();
                if(fired == null)
                {
                    ApplicationSettings.getApplicationView().getStatusBar().changeText(
                            "ERROR: No transitions to fire after " + j + " firings");
                    break;        // no point to keep trying to find a transition
                }
                else
                {
                    //data.createCurrentMarkingVector();
                    data.fireTransition(fired); //NOU-PERE
                    //Get the new marking from the _dataLayer object
                    markings = data.getCurrentMarkingVector();
                    marking = new int[length];
                    for(int k = 0; k < length; k++)
                    {
                        if(markings[k]!= null && markings[k].size() > 0)
                        {
                            MarkingView first = markings[k].getFirst();
                            if(first != null)
                                marking[k] = first.getCurrentMarking();
                        }
                    }

                    /*     for (int k=0; k<marking.length; k++)
                    System.out.print("" + marking[k] + ",");
                    System.out.println("");*/

                    //Add to the totalTokens array
                    addTotal(marking, totalTokens);
                    //Increment the transition count
                    transCount++;
                }
            }

            //Evaluate averages
            for(j = 0; j < length; j++)
            {
                //Divide by transCount + 1 as total number of markings
                //considered includes the original marking which is outside
                //the loop which counts the number of randomly fired transitions.
                averageTokens[j] = (totalTokens[j] / (transCount + 1.0));

                //add appropriate to appropriate row of overall averages for each cycle
                overallAverages[i][j] = averageTokens[j];
            }
        }

        //Add up averages for each cycle and divide by number of cycles
        //Perform evaluation on the overallAverages matrix.
        //for each column
        for(i = 0; i < length; i++)
        {
            //for each row
            for(j = 0; j < cycles; j++)
            {
                avgResult[i] = avgResult[i] + overallAverages[j][i];
            }
            avgResult[i] = (avgResult[i] / cycles);
        }


        //Generate the 95% confidence interval for the table of results

        //Find standard deviation and mulitply by 1.95996 assuming approx
        //to gaussian distribution

        //For each column in result array
        for(i = 0; i < length; i++)
        {
            //Find variance
            for(j = 0; j < cycles; j++)
            {
                //Sum of squares
                errorResult[i] = errorResult[i] +
                        ((overallAverages[j][i] - avgResult[i]) *
                                (overallAverages[j][i] - avgResult[i]));
            }

            //Divide by number of cycles
            //Find standard deviation by taking square root
            //Multiply by 1.95996 to give 95% confidence interval
            errorResult[i] = 1.95996 * Math.sqrt(errorResult[i] / cycles);
        }

        ArrayList results = new ArrayList();
        DecimalFormat f = new DecimalFormat();
        f.setMaximumFractionDigits(5);

        if(averageTokens != null && errorResult != null
                && averageTokens.length > 0 && errorResult.length > 0)
        {
            // Write table of results
            results.add("Place");
            results.add("Average number of tokens");
            results.add("95% confidence interval (+/-)");
            for(i = 0; i < averageTokens.length; i++)
            {
                results.add(data.getPlace(i).getName());
                results.add(f.format(averageTokens[i]));
                results.add(f.format(errorResult[i]));
            }
        }
        if(ApplicationSettings.getApplicationView() != null) data.restorePreviousMarking();
        return ResultsHTMLPane.makeTable(results.toArray(), 3, false, true, true, true);
    }

    private void addTotal(int array[], int dest[])
    {
        if(array.length == dest.length)
        {
            for(int i = 0; i < dest.length; i++)
            {
                dest[i] += array[i];
            }
        }
    }

    @Override
    protected Object doInBackground() throws Exception
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
