/**
 * This is a placeholder for a class to be added as part of the
 * support of exponential distributions.
 *
 * This will be a module to simulate a Petri Net based on a
 * target "clock" time. It will simulate the net and calculate
 * the virtual clock time of the next firing. When the target
 * clock time is exceeded, it will stop that cycle. 
 *
 * @author David Patterson 
 *
 */

package pipe.modules.iai;

import pipe.gui.ApplicationSettings;

//TO LOAD THIS MODULE UNCOMMENT THE implement
public class SimulationClockWatcher //implements IModule
{
    public SimulationClockWatcher()
    {
        // nothing here.
    } // end of constructor for this class

    public void start()
    {
        ApplicationSettings.getApplicationView().getStatusBar().changeText(
                "ERROR: This simulation module is not implemented yet.");
    }

    public String getName()
    {
        return "Clock Watcher Simulation (Not ready)";
    }

    public void onSimulate()
    {
        // nothing here.
    }


}
