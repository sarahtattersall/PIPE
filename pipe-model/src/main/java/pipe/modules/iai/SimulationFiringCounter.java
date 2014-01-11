/**
 * This is a placeholder for a class to be added as part of the
 * support of exponential distributions.
 *
 * This will be a module to simulate a Petri Net based on a
 * counting firings (much like the original "Simulation" module
 * did. 
 *
 * @author David Patterson 
 *
 */

package pipe.modules.iai;

import pipe.gui.ApplicationSettings;

//TO LOAD THIS MODULE UNCOMMENT THE implement
public class SimulationFiringCounter //implements IModule

{
    private static final String MODULE_NAME =
            "IAI - Simulation: Firing Counter";

    public SimulationFiringCounter()
    {
        // Nothing here.
    } // end of constructor for this class

    public void start()
    {
        ApplicationSettings.getApplicationView().getStatusBar().changeText(
                "ERROR: This simulation module is not implemented yet.");
    }

    public String getName()
    {
        return "Firing Counter Simulation (Not ready)";
    }
}
