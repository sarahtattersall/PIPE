package pipe.modules.rta;


import pipe.gui.ApplicationSettings;
import pipe.gui.widgets.*;
import pipe.modules.interfaces.IModule;
import pipe.views.PetriNetView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;


/**
 * @author Oliver Haggarty July 2007
 *         <p/>
 *         PIPE2 module that will calculate the response time of GSPN generated in PIPE2.
 *         The class presents the user with a dialog box, from which they can select start and
 *         target states for the passage time they wish to calculate. The response time can either
 *         be caluclated on the local machine, or as a parallel map reduce job. Please note this
 *         option is only available to those running PIPE2 on a Linux platform with a running
 *         Hadoop platform. In this case, PIPE2 must be run with the Hadoop installation in its
 *         classpath.Results are presented ingraphical format and can be saved as a CSV file
 *         which can be opened by some spreadsheet packages.
 */
public class RTA implements IModule
{

    private static final String MODULE_NAME = "Response Time Analysis";
    private ButtonBar btnBar;
    private PetriNetChooserPanel sourceFilePanel;
    private PetriNetView _pnmlData;
    private EnterOptionsPane options;
    private JDialog guiDialog;
    protected GraphPanel testgraph;
    private Thread thread;
    private ResultsHTMLPane results;

    public RTA()
    {
    }


    /**
     * Required method for running module. Creates RTA dialog box, consisting of
     * PetriNetChooserPanel, EnterOptionsPane, ButtonBar.
     *
     */
    public void start()
    {
        // Build interface
        guiDialog = new JDialog(ApplicationSettings.getApplicationView(), MODULE_NAME, true);
        _pnmlData = ApplicationSettings.getApplicationView().getCurrentPetriNetView();

        // 1 Set layout
        Container contentPane = guiDialog.getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));

        // 2 Add file browser
        contentPane.add(sourceFilePanel = new PetriNetChooserPanel("Source net", _pnmlData));
        results = new ResultsHTMLPane(_pnmlData.getPNMLName());
        // 3 Add the EnterOptionsPane
        //Check if PIPE2 is being run on a Linux platform with Hadoop installed
        //If so, Run as Mapreduce job is checked as default, if not it is unchecked
        if(isHadoopReady())
            contentPane.add(options = new EnterOptionsPane(true));
        else
            contentPane.add(options = new EnterOptionsPane(false));

        // 3.1 Initialise settings on EnterOptionsPane
        loadSavedOptions();

        //3.2 Add window listener to check when window closes and save settings
        guiDialog.addWindowListener(new WinList());
        // 4 Add button's
        btnBar = new ButtonBar("Calculate Response Time", analyseHandler);
        contentPane.add(btnBar);

        // 5 Make window fit contents' preferred size
        guiDialog.pack();

        // 6 Move window to the middle of the screen
        guiDialog.setLocationRelativeTo(null);

        //7 Necessary for when creating CartesianGraphFrame (for some reason)
        guiDialog.setModal(false);
        guiDialog.setVisible(false);
        guiDialog.setVisible(true);

    }

    /**
     * Contains code that turns the enter options dialog into a results dialog and then
     * runs the AnalyseResponse class (in a new thread)
     */
    private final ActionListener analyseHandler = new ActionListener()
    {
        public void actionPerformed(ActionEvent evnt)
        {

            //Set results pane on JDialog to display info as we go:
            Container contentPane = guiDialog.getContentPane();
            contentPane.removeAll();
            contentPane.add(results);
            ButtonBar resultsBtnBar;
            contentPane.add(resultsBtnBar = new ButtonBar(
                    new String[]{"Back", "Cancel"},
                    new ActionListener[]{backHandler, cancelHandler}));
            contentPane.validate();
            contentPane.repaint();
            //Now run analyse class in new thread
            thread = new Thread(new AnalyseResponse(options, _pnmlData, resultsBtnBar, results, RTA.this));
            try
            {
                thread.start();
            }
            catch(Exception e)
            {
                //do nothing its because we cancelled
            }
            System.gc();
        }
    };

    /**
     * Calls changeTOEnterOptions() when the back button is clicked
     */
    private final ActionListener backHandler = new ActionListener()
    {
        public void actionPerformed(ActionEvent ev)
        {
            changeToEnterOptions();
        }
    };

    /**
     * Cancels the current job being executed in the other thread and changes the
     * dialog box back to the enter options view
     */
    private final ActionListener cancelHandler = new ActionListener()
    {
        public void actionPerformed(ActionEvent ev)
        {
            //Nasty way of cancelling job - but it works
            thread.stop();
            Component anBtn = btnBar.getComponent(0);
            anBtn.setEnabled(true);
            options.setErrorMessage("Analysis Cancelled");
            RTA.this.changeToEnterOptions();
        }
    };

    /**
     * Class that listens for when the dialog box is closed. When it is calls a method to
     * save the user's options to reloading next time
     *
     * @author Oliver Haggarty - 08/2007
     */
    private class WinList implements WindowListener
    {
        public void windowClosed(WindowEvent arg0)
        {
        }

        public void windowActivated(WindowEvent arg0)
        {
        }

        /**
         * On closing, saves user's options
         */
        public void windowClosing(WindowEvent arg0)
        {
            saveOptions();
        }

        public void windowDeactivated(WindowEvent arg0)
        {
        }

        public void windowDeiconified(WindowEvent arg0)
        {
        }

        public void windowIconified(WindowEvent arg0)
        {
        }

        public void windowOpened(WindowEvent arg0)
        {
        }
    }

    /**
     * Function that changes the results dialog back to the enter options dialog
     */
    public void changeToEnterOptions()
    {
        Container contentPane = guiDialog.getContentPane();
        contentPane.removeAll();
        contentPane.add(sourceFilePanel);
        contentPane.add(options);
        contentPane.add(btnBar);
        contentPane.validate();
        contentPane.repaint();
    }

    /**
     * Detects if PIPE2 is running on Linux with hadoop installed
     *
     * @return true if running on Linux with hadoop installed
     */
    private boolean isHadoopReady()
    {
        return (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") == -1) &&
                System.getenv("HADOOP_INSTALL") != null;
    }

    /**
     * Loads saved options from file and enter them into relevent GUI components. Uses
     * default options if file is not found
     */
    private void loadSavedOptions()
    {
        String savedOpt = "savedOptions";
        File savedOptFile = new File(savedOpt);
        if(savedOptFile.exists())
        {
            //Read in options and set them
            BufferedReader in = null;
            try
            {
                in = new BufferedReader(new FileReader(savedOptFile));
            }
            catch(FileNotFoundException e)
            {
                e.printStackTrace();
            }
            String s;
            try
            {
                s = in.readLine();
                options.setStartStates(s);
                s = in.readLine();
                options.setTargetStates(s);
                s = in.readLine();
                options.setTStart(s);
                s = in.readLine();
                options.setTStop(s);
                s = in.readLine();
                options.setStepSize(s);
                s = in.readLine();
                options.setRunAsMap(Boolean.parseBoolean(s));
                s = in.readLine();
                options.setNumMaps(s);
                s = in.readLine();
                options.setRT(Boolean.parseBoolean(s));
                s = in.readLine();
                options.setCDF(Boolean.parseBoolean(s));
                s = in.readLine();
                options.setBufferSize(s);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            options.setStartStates("#(P0) > 0");
            options.setTargetStates("#(P1) > 0");
            options.setTStart("1.0");
            options.setTStop("10.0");
            options.setStepSize("0.1");
            options.setRunAsMap(false);
            options.setNumMaps("128");
            options.setRT(true);
            options.setCDF(true);
            options.setBufferSize("100");
        }
    }

    /**
     * Saves current options to a file, checking each one is valid
     */
    private void saveOptions()
    {
        String savedOpt = "savedOptions";
        File savedOptFile = new File(savedOpt);
        BufferedWriter out = null;
        Double TStart, TStop, stepSize;
        Integer numMaps, bufferSize;
        try
        {
            out = new BufferedWriter(new FileWriter(savedOptFile));
            try
            {
                TStart = options.getTStart();
            }
            catch(NumberFormatException e)
            {
                TStart = 1.0;
            }
            try
            {
                TStop = options.getTStop();
            }
            catch(NumberFormatException e)
            {
                TStop = 10.0;
            }
            try
            {
                stepSize = options.getStepSize();
            }
            catch(NumberFormatException e)
            {
                stepSize = 1.0;
            }
            try
            {
                numMaps = options.getNumMaps();
            }
            catch(NumberFormatException e)
            {
                numMaps = 128;
            }
            try
            {
                bufferSize = options.getBufferSize();
            }
            catch(NumberFormatException e)
            {
                bufferSize = 100;
            }
            try
            {
                out.write(options.getStartStates() + "\n");
                out.write(options.getTargetStates() + "\n");
                out.write((TStart).toString() + "\n");
                out.write((TStop).toString() + "\n");
                out.write((stepSize).toString() + "\n");
                out.write(((Boolean) options.isMapRedJob()).toString() + "\n");
                out.write((numMaps).toString() + "\n");
                out.write(((Boolean) options.isRT()).toString() + "\n");
                out.write(((Boolean) options.isCDF()).toString() + "\n");
                out.write((bufferSize).toString() + "\n");
            }

            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                out.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }

    }


    /**
     * @return Name of module
     */
    public String getName()
    {
        return MODULE_NAME;
    }
}
