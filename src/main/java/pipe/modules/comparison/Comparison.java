/**
 * Comparison IModule
 * @author James D Bloom 2003-03-12
 * @author Maxim 2004 (better UI and code cleanup)
 * @author Pere Bonet (added inhibitor arcs, place's capacity and transition's priority)
 */
package pipe.modules.comparison;

import pipe.gui.ApplicationSettings;
import pipe.gui.widgets.ButtonBar;
import pipe.gui.widgets.EscapableDialog;
import pipe.gui.widgets.PetriNetChooserPanel;
import pipe.gui.widgets.ResultsHTMLPane;
import pipe.modules.interfaces.IModule;
import pipe.utilities.Expander;
import pipe.utilities.writers.PNMLWriter;
import pipe.views.ArcView;
import pipe.views.PetriNetView;
import pipe.views.PlaceView;
import pipe.views.TransitionView;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Comparison
        implements IModule
{

    // Main Frame
    private static final String MODULE_NAME = "Comparison";
    private PetriNetChooserPanel sourceFilePanel;
    private PetriNetChooserPanel comparisonFilePanel;
    private ResultsHTMLPane results;
    private JCheckBox comparePlaceID;
    private JCheckBox comparePlaceName;
    private JCheckBox comparePlaceMarking;
    private JCheckBox comparePlaceCapacity;
    private JCheckBox compareTransitionID;
    private JCheckBox compareTransitionName;
    private JCheckBox compareTransitionRate;
    private JCheckBox compareTransitionPriority;
    private JCheckBox compareArcID;
    private JCheckBox compareArcName;
    private JCheckBox compareArcWeighting;
    private JCheckBox compareInhibitorID;
    private JCheckBox compareInhibitorName;
    private JCheckBox compareInhibitorWeighting;

    public void start()
    {
        PetriNetView pnmlData = ApplicationSettings.getApplicationView().getCurrentPetriNetView();
        // Check if this net is a CGSPN. If it is, then this
        // module won't work with it and we must convert it.
        if(pnmlData.getTokenViews().size() > 1)
        {
            Expander expander = new Expander(pnmlData);
            pnmlData = expander.unfold();
        }
        EscapableDialog guiDialog =
                new EscapableDialog(ApplicationSettings.getApplicationView(), MODULE_NAME, true);

        // 1 Set layout
        Container contentPane = guiDialog.getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));

        // 2 Add file browser
        sourceFilePanel = new PetriNetChooserPanel("Source net", pnmlData);
        comparisonFilePanel = new PetriNetChooserPanel("Comparison net", null);
        contentPane.add(sourceFilePanel);
        contentPane.add(comparisonFilePanel);

        // 2.5 Add check boxes
        JPanel placePanel = new JPanel();
        placePanel.setLayout(new BoxLayout(placePanel, BoxLayout.LINE_AXIS));
        placePanel.setBorder(new TitledBorder(new EtchedBorder(), "Places"));
        placePanel.add(comparePlaceID = new JCheckBox("ID", true));
        placePanel.add(comparePlaceName = new JCheckBox("Name", true));
        placePanel.add(comparePlaceMarking = new JCheckBox("Marking", true));
        placePanel.add(comparePlaceCapacity = new JCheckBox("Capacity", true));

        JPanel transPanel = new JPanel();
        transPanel.setLayout(new BoxLayout(transPanel, BoxLayout.LINE_AXIS));
        transPanel.setBorder(new TitledBorder(new EtchedBorder(), "Transitions"));
        transPanel.add(compareTransitionID = new JCheckBox("ID", true));
        transPanel.add(compareTransitionName = new JCheckBox("Name", true));
        transPanel.add(compareTransitionRate = new JCheckBox("Rate/Weight", true));
        transPanel.add(compareTransitionPriority = new JCheckBox("Priority", true));

        JPanel arcPanel = new JPanel();
        arcPanel.setLayout(new BoxLayout(arcPanel, BoxLayout.LINE_AXIS));
        arcPanel.setBorder(new TitledBorder(new EtchedBorder(), "Arcs"));
        arcPanel.add(compareArcID = new JCheckBox("ID", true));
        arcPanel.add(compareArcName = new JCheckBox("Name", true));
        arcPanel.add(compareArcWeighting = new JCheckBox("Weighting", true));

        JPanel inhibitorPanel = new JPanel();
        inhibitorPanel.setLayout(new BoxLayout(inhibitorPanel, BoxLayout.LINE_AXIS));
        inhibitorPanel.setBorder(new TitledBorder(new EtchedBorder(), "Inhibitor Arcs"));
        inhibitorPanel.add(compareInhibitorID = new JCheckBox("ID", true));
        inhibitorPanel.add(compareInhibitorName = new JCheckBox("Name", true));
        inhibitorPanel.add(compareInhibitorWeighting = new JCheckBox("Weighting", true));

        JPanel options = new JPanel();
        options.setBorder(new TitledBorder(new EtchedBorder(), "Comparison options"));
        options.setLayout(new BoxLayout(options, BoxLayout.LINE_AXIS));
        options.add(placePanel);
        options.add(transPanel);
        options.add(arcPanel);
        options.add(inhibitorPanel);
        contentPane.add(options);

        // 3 Add results pane
        results = new ResultsHTMLPane(pnmlData.getPNMLName());
        contentPane.add(results);

        // 4 Add button
        contentPane.add(new ButtonBar("Compare", compareButtonClick,
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
     * Compare button click handler
     */
    private final ActionListener compareButtonClick = new ActionListener()
    {

        public void actionPerformed(ActionEvent arg0)
        {
            PetriNetView sourceDataLayer = sourceFilePanel.getDataLayer();
            if(sourceDataLayer == null)
            {
                JOptionPane.showMessageDialog(null, "Please, choose a source net",
                                              "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            PNMLWriter.saveTemporaryFile(sourceDataLayer,
                                         this.getClass().getName());

            PetriNetView comparisonDataLayer = comparisonFilePanel.getDataLayer();
            if(comparisonDataLayer == null)
            {
                JOptionPane.showMessageDialog(null, "Please, choose a comparison net",
                                              "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String s = "<h2>Petri net comparison results</h2>";

            try
            {
                if(!sourceDataLayer.hasPlaceTransitionObjects())
                {
                    s += "No Petri net objects defined!";
                }
                else
                {
                    if(comparePlaceID.isSelected() || comparePlaceName.isSelected() || comparePlaceMarking.isSelected())
                    {
                        s += comparePlaces(sourceDataLayer.places(),
                                           comparisonDataLayer.places(),
                                           comparePlaceID.isSelected(),
                                           comparePlaceName.isSelected(),
                                           comparePlaceMarking.isSelected(),
                                           comparePlaceCapacity.isSelected());
                    }
                }
                if(compareTransitionID.isSelected() ||
                        compareTransitionName.isSelected() ||
                        compareTransitionRate.isSelected())
                {
                    s += compareTransitions(sourceDataLayer.getTransitionViews(),
                                            comparisonDataLayer.getTransitionViews(),
                                            compareTransitionID.isSelected(),
                                            compareTransitionName.isSelected(),
                                            compareTransitionRate.isSelected(),
                                            compareTransitionPriority.isSelected());
                }
                if(compareArcID.isSelected() || compareArcName.isSelected() ||
                        compareArcWeighting.isSelected())
                {
                    s += compareArcs(sourceDataLayer.arcs(),
                                     comparisonDataLayer.arcs(),
                                     compareArcID.isSelected(),
                                     compareArcName.isSelected(),
                                     compareArcWeighting.isSelected(),
                                     false);
                }
                if(compareInhibitorID.isSelected() || compareInhibitorName.isSelected() ||
                        compareInhibitorWeighting.isSelected())
                {
                    s += compareArcs(sourceDataLayer.inhibitors(),
                                     comparisonDataLayer.inhibitors(),
                                     compareArcID.isSelected(),
                                     compareArcName.isSelected(),
                                     compareArcWeighting.isSelected(),
                                     true);
                }
                results.setEnabled(true);
                results.setText(s);

            }
            catch(OutOfMemoryError oome)
            {
                System.gc();
                results.setText("");
                s = "Memory error: " + oome.getMessage();

                s += "<br>Not enough memory. Please use a larger heap size." + "<br>" + "<br>Note:" + "<br>The Java heap size can be specified with the -Xmx option." + "<br>E.g., to use 512MB as heap size, the command line looks like this:" + "<br>java -Xmx512m -classpath ...\n";
                results.setText(s);
            }
            catch(Exception e)
            {
                e.printStackTrace();
                s = "<br>Error" + e.getMessage();
                results.setText(s);
            }
        }
    };

    public String comparePlaces(PlaceView[] source, PlaceView[] comparison,
                                boolean compareID, boolean compareName, boolean compareMarking,
                                boolean compareCapacity)
    {
        int j = 0, k = -1;
        String s;
        ArrayList results = new ArrayList(); // arraylist for creating the table

        results.add("Source place name");
        results.add("Comparison place name");
        results.add("Comparison");

        for(int i = 0; i < source.length; i++)
        {
            k = -1;
            // Find matching Place with a match for either id or name
            for(j = 0; j < comparison.length; j++)
            {
                if(comparison[j] != null &&
                        source[i] != null &&
                        source[i].getId() != null &&
                        comparison[j].getId() != null &&
                        source[i].getName() != null &&
                        comparison[j].getName() != null)
                {
                    if(source[i].getId().equals(comparison[j].getId()) ||
                            source[i].getName().equals(comparison[j].getName()))
                    {
                        k = j;
                    }
                }
            }
            j = k;

            if(j != -1 && source[i] != null && comparison[j] != null)
            {
                results.add(source[i].getName());
                results.add(comparison[j].getName());
                if((!compareID || source[i].getId().equals(comparison[j].getId())) &&
                        (!compareName || source[i].getName().equals(comparison[j].getName())) &&
                        (!compareMarking || source[i].getCurrentMarkingView().get(0).getCurrentMarking()
                                == comparison[j].getCurrentMarkingView().get(0).getCurrentMarking()) &&
                        (!compareCapacity || source[i].getCapacity() == comparison[j].getCapacity()))
                {
                    s = "Identical";
                }
                else
                {
                    s = "";
                    if(compareID)
                    {
                        s += "Id";
                        if(source[i].getId().equals(comparison[j].getId()))
                        {
                            s += " (match)";
                        }
                        s += ": Source = \"" + source[i].getId() +
                                "\"  Comparison  = \"" + comparison[j].getId() +
                                "\"<br>";
                    }
                    if(compareName)
                    {
                        s += "Name";
                        if(source[i].getName().equals(comparison[j].getName()))
                        {
                            s += " (match)";
                        }
                        s += ": Source = \"" + source[i].getName() +
                                "\"  Comparison  = \"" + comparison[j].getName() +
                                "\"<br>";
                    }
                    if(compareMarking)
                    {
                        s += "Marking";
                        if(source[i].getCurrentMarkingView().get(0).getCurrentMarking()
                                == comparison[j].getCurrentMarkingView().get(0).getCurrentMarking())
                        {
                            s += " (match)";
                        }
                        s += ": Source = \"" + source[i].getCurrentMarkingView().get(0).getCurrentMarking() +
                                "\"  Comparison  = \"" +
                                comparison[j].getCurrentMarkingView().get(0).getCurrentMarking() +
                                "\"<br>";
                    }

                    if(compareCapacity)
                    {
                        s += "Capacity";
                        if(source[i].getCapacity() == comparison[j].getCapacity())
                        {
                            s += " (match)";
                        }
                        s += ": Source = \"" + source[i].getCapacity() +
                                "\"  Comparison  = \"" + comparison[j].getCapacity() +
                                "\"";
                    }
                }
                source[i] = null;       // null ones that have been done
                comparison[j] = null;
                results.add(s);
            }
        }

        // Fill in unmatched source places
        for(PlaceView aSource : source)
        {
            if(aSource != null)
            {
                results.add(aSource.getName());
                results.add("");
                s = "Not found in comparison Petri net";
                if(compareID)
                {
                    s += "<br>Id = \"" + aSource.getId() + "\"";
                }
                if(compareName)
                {
                    s += "<br>Name = \"" + aSource.getName() + "\"";
                }
                if(compareMarking)
                {
                    s += "<br>Marking = \"" + aSource.getCurrentMarkingView().get(0).getCurrentMarking() + "\"";
                }
                if(compareCapacity)
                {
                    s += "<br>Capacity = \"" + aSource.getCapacity() + "\"";
                }
                results.add(s);
            }
        }

        // Fill in unmatched comparison places
        for(PlaceView aComparison : comparison)
        {
            if(aComparison != null)
            {
                results.add("");
                results.add(aComparison.getName());
                s = "Not found in source Petri net";
                if(compareID)
                {
                    s += "<br>Id = \"" + aComparison.getId() + "\"";
                }
                if(compareName)
                {
                    s += "<br>Name = \"" + aComparison.getName() + "\"";
                }
                if(compareMarking)
                {
                    s += "<br>Marking = \"" + aComparison.getCurrentMarkingView().get(0).getCurrentMarking() + "\"";
                }
                if(compareCapacity)
                {
                    s += "<br>Capacity = \"" + aComparison.getCapacity() + "\"";
                }
                results.add(s);
            }
        }
        if(results.size() <= 3)
        {
            return "<h2>Places</h2>" +
                    "Neither source Petri Net nor comparison Petri Net contain Places";
        }
        else
        {
            return "<h2>Places</h2>" + ResultsHTMLPane.makeTable(
                    results.toArray(), 3, false, true, true, false);
        }
    }

    private String compareTransitions(TransitionView[] source, TransitionView[] comparison,
                                      boolean compareID, boolean compareName, boolean compareRate,
                                      boolean comparePriority)
    {
        int j = 0, k = -1;
        String s;
        ArrayList results = new ArrayList(); // arraylist for creating the table

        results.add("Source transition name");
        results.add("Comparison transition name");
        results.add("Comparison");

        for(int i = 0; i < source.length; i++)
        {
            k = -1;
            // Find matching Transition with a match for either id or name
            for(j = 0; j < comparison.length; j++)
            {
                if(comparison[j] != null && source[i] != null &&
                        source[i].getId() != null &&
                        comparison[j].getId() != null &&
                        source[i].getName() != null &&
                        comparison[j].getName() != null)
                {
                    if(source[i].getId().equals(comparison[j].getId()) ||
                            source[i].getName().equals(comparison[j].getName()))
                    {
                        k = j;
                    }
                }
            }
            j = k;

            if(j != -1 && source[i] != null && comparison[j] != null)
            {
                results.add(source[i].getName());
                results.add(comparison[j].getName());
                if((!compareID || source[i].getId().equals(comparison[j].getId())) &&
                        (!compareName || source[i].getName().equals(comparison[j].getName())) &&
                      //  (!compareRate || source[i].getRateExpr() == comparison[j].getRate()) &&
                        (!compareRate || source[i].getRateExpr().equals(comparison[j].getRateExpr()))&&
                        (!comparePriority || source[i].getPriority() == comparison[j].getPriority()))
                {
                    s = "Identical";
                }
                else
                {
                    s = "";
                    if(compareID)
                    {
                        s += "Id";
                        if(source[i].getId().equals(comparison[j].getId()))
                        {
                            s += " (match)";
                        }
                        s += ": Source = \"" + source[i].getId() +
                                "\"  Comparison  = \"" + comparison[j].getId() +
                                "\"<br>";
                    }
                    if(compareName)
                    {
                        s += "Name";
                        if(source[i].getName().equals(comparison[j].getName()))
                        {
                            s += " (match)";
                        }
                        s += ": Source = \"" + source[i].getName() +
                                "\"  Comparison  = \"" + comparison[j].getName() +
                                "\"<br>";
                    }
                    if(compareRate)
                    {
                        s += "Rate/Weight";
                        if(source[i].getRateExpr().equals(comparison[j].getRateExpr()))
                        {
                            s += " (match)";
                        }
                        s += ": Source = \"" + source[i].getRateExpr() +
                                "\"  Comparison  = \"" + comparison[j].getRateExpr() +
                                "\"<br>";
                    }
                    if(comparePriority)
                    {
                        s += "Priority";
                        if(source[i].getPriority() == comparison[j].getPriority())
                        {
                            s += " (match)";
                        }
                        s += ": Source = \"" + source[i].getPriority() +
                                "\"  Comparison  = \"" + comparison[j].getPriority() + "\"";
                    }
                }
                source[i] = null;       // null ones that have been done
                comparison[j] = null;
                results.add(s);
            }
        }

        // Fill in unmatched source transitions
        for(TransitionView aSource : source)
        {
            if(aSource != null)
            {
                results.add(aSource.getName());
                results.add("");
                s = "Not found in comparison Petri net";
                if(compareID)
                {
                    s += "<br>Id= \"" + aSource.getId() + "\"";
                }
                if(compareName)
                {
                    s += "<br>Name= \"" + aSource.getName() + "\"";
                }
                if(compareRate)
                {
                    s += "<br>Rate/Weight= \"" + aSource.getRateExpr() + "\""; //Nou-pere
                }
                if(comparePriority)
                {
                    s += "<br>Priority = \"" + aSource.getPriority() + "\"";
                }
                results.add(s);
            }
        }

        // Fill in unmatched comparison transitions
        for(TransitionView aComparison : comparison)
        {
            if(aComparison != null)
            {
                results.add("");
                results.add(aComparison.getName());
                s = "Not found in source Petri net";
                if(compareID)
                {
                    s += "<br>Id = \"" + aComparison.getId() + "\"";
                }
                if(compareName)
                {
                    s += "<br>Name = \"" + aComparison.getName() + "\"";
                }
                if(compareRate)
                {
                    s += "<br>Rate/Weight = \"" + aComparison.getRateExpr() + "\"";//Nou-pere
                }
                if(comparePriority)
                {
                    s += "<br>Priority = \"" + aComparison.getPriority() + "\"";
                }
                results.add(s);
            }
        }
        if(results.size() <= 3)
        {
            return "<h2>Transitions</h2>" +
                    "Neither source Petri Net nor comparison Petri Net contain" +
                    " Transitions";
        }
        else
        {
            return "<h2>Transitions</h2>" + ResultsHTMLPane.makeTable(
                    results.toArray(), 3, false, true, true, false);
        }
    }

    private String compareArcs(ArcView[] source, ArcView[] comparison, boolean compareName,
                               boolean compareID, boolean compareWeighting, boolean inhibitorArcs)
    {
        int j = 0, k = -1;
        String s;
        ArrayList results = new ArrayList(); // arraylist for creating the table

        results.add("Source arc");
        results.add("Comparison arc");
        results.add("Comparison");

        for(int i = 0; i < source.length; i++)
        {
            k = -1;
            // Find matching Arc with a match for both source and dest
            // this is pretty horrible
            for(j = 0; j < comparison.length; j++)
            {
                if(comparison[j] != null && source[i] != null &&
                        source[i].getSource().getId() != null &&
                        comparison[j].getSource().getId() != null &&
                        source[i].getSource().getName() != null &&
                        comparison[j].getSource().getName() != null &&
                        source[i].getTarget().getId() != null &&
                        comparison[j].getTarget().getId() != null &&
                        source[i].getTarget().getName() != null &&
                        comparison[j].getTarget().getName() != null)
                {
                    if((source[i].getSource().getId().equals(comparison[j].getSource().getId()) && source[i].getTarget().getId().equals(comparison[j].getTarget().getId())) || (source[i].getSource().getName().equals(comparison[j].getSource().getName()) && source[i].getTarget().getName().equals(comparison[j].getTarget().getName())))
                    {
                        k = j;
                    }
                }
            }
            j = k;

            if(j != -1 && source[i] != null && comparison[j] != null)
            {
                results.add(source[i].getSource().getName() + "->" +
                                    source[i].getTarget().getName());
                results.add(comparison[i].getSource().getName() + "->" +
                                    comparison[i].getTarget().getName());
                if((!compareID || (source[i].getSource().getId().equals(comparison[j].getSource().getId()) && source[i].getTarget().getId().equals(comparison[j].getTarget().getId()))) && (!compareName || (source[i].getSource().getName().equals(comparison[j].getSource().getName()) && source[i].getTarget().getName().equals(comparison[j].getTarget().getName()))) && (!compareWeighting || (source[i].getWeight().get(0).getCurrentFunctionalMarking().equals(comparison[j].getWeight().get(0).getCurrentFunctionalMarking()))))
                {
                    s = "Identical";
                }
                else
                {
                    s = "";
                    if(compareID)
                    {
                        s += "Id";
                        if(source[i].getSource().getId().equals(comparison[j].getSource().getId()) &&
                                source[i].getTarget().getId().equals(comparison[j].getTarget().getId()))
                        {
                            s += " (match)";
                        }
                        s += ": Source = \"" + source[i].getSource().getId() + "\"->\"" +
                                source[i].getTarget().getId() + "\"" + "Comparison  = \"" +
                                comparison[i].getSource().getId() + "\"->\"" +
                                comparison[i].getTarget().getId() + "\"" + "<br>";
                    }
                    if(compareName)
                    {
                        s += "Name";
                        if(source[i].getSource().getName().equals(comparison[j].getSource().getName()) &&
                                source[i].getTarget().getName().equals(comparison[j].getTarget().getName()))
                        {
                            s += " (match)";
                        }
                        s += ": Source = \"" + source[i].getSource().getName() + "\"->\"" +
                                source[i].getTarget().getName() + "\"" + "Comparison  = \"" +
                                comparison[i].getSource().getName() + "\"->\"" +
                                comparison[i].getTarget().getName() + "\"" + "<br>";
                    }
                    if(compareWeighting)
                    {
                        s += "Weighting";
                        if(source[i].getWeight().get(0).getCurrentFunctionalMarking().equals(comparison[j].getWeight().get(0).getCurrentFunctionalMarking()))
                        {
                            s += " (match)";
                        }
                        s += ": Source = \"" + source[i].getWeight().get(0).getCurrentFunctionalMarking() + "\"  Comparison  = \"" +
                                comparison[j].getWeight().get(0).getCurrentFunctionalMarking() + "\"";
                    }
                }
                source[i] = null;       // null ones that have been done
                comparison[j] = null;
                results.add(s);
            }
        }

        // Fill in unmatched source arcs
        for(ArcView aSource : source)
        {
            if(aSource != null)
            {
                results.add(aSource.getSource().getName() + "->" +
                                    aSource.getTarget().getName());
                results.add("");
                s = "Not found in comparison Petri net";
                if(compareID)
                {
                    s += "<br>Id= \"" + aSource.getSource().getId() + "\"->\"" +
                            aSource.getTarget().getId() + "\"";
                }
                if(compareName)
                {
                    s += "<br>Name= \"" + aSource.getSource().getName() + "\"->\"" +
                            aSource.getTarget().getName() + "\"";
                }
                if(compareWeighting)
                {
                    s += "<br>Weighting= \"" + aSource.getWeight().get(0).getCurrentFunctionalMarking() + "\"";
                }
                results.add(s);
            }
        }

        // Fill in unmatched comparison arcs
        for(ArcView aComparison : comparison)
        {
            if(aComparison != null)
            {
                results.add("");
                results.add(aComparison.getSource().getName() + "->" +
                                    aComparison.getTarget().getName());
                s = "Not found in source Petri net";
                if(compareID)
                {
                    s += "<br>Id= \"" + aComparison.getSource().getId() + "\"->\"" +
                            aComparison.getTarget().getId() + "\"";
                }
                if(compareName)
                {
                    s += "<br>Name= \"" + aComparison.getSource().getName() +
                            "\"->\"" + aComparison.getTarget().getName() + "\"";
                }
                if(compareWeighting)
                {
                    s += "<br>Weighting= \"" + aComparison.getWeight().get(0).getCurrentFunctionalMarking() + "\"";
                }
                results.add(s);
            }
        }
        if(inhibitorArcs)
        {
            if(results.size() <= 3)
            {
                return "<h2>Inhibitor Arcs</h2>" +
                        "Neither source Petri Net nor comparison Petri Net contain " +
                        "Inhibitor Arcs";
            }
            else
            {
                return "<h2>Inhibitor Arcs</h2>" + ResultsHTMLPane.makeTable(
                        results.toArray(), 3, false, true, true, false);
            }
        }
        else
        {
            if(results.size() <= 3)
            {
                return "<h2>Arcs</h2>" +
                        "Neither source Petri Net nor comparison Petri Net contain " +
                        "Arcs";
            }
            else
            {
                return "<h2>Arcs</h2>" + ResultsHTMLPane.makeTable(
                        results.toArray(), 3, false, true, true, false);
            }
        }
    }
}
