/**
 * This is a placeholder for a class to be added as part of the
 * support of exponential distributions.
 *
 * This will be a module to simulate a Petri Net based on 
 * watching a selected Place for a maximum token count. It works
 * best when special Place objects are added to a net that only
 * have inbound arcs. They can be thought of as a bucket into which
 * tokens are placed, for example at the end of processing
 * a unit of work. When the target count is reached, the cycle
 * is neded.  
 *
 * @author David Patterson 
 *
 */

package pipe.modules.iai;

import pipe.gui.ApplicationSettings;

//TO LOAD THIS MODULE UNCOMMENT THE implement
public class SimulationPlaceWatcher //implements IModule
{

    public SimulationPlaceWatcher()
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
        return "Place Watcher Simulation (Not ready)";
    }

    public void onSimulate()
    {
        // nothing here.
    }


}
