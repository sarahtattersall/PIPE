package pipe.gui.plugin.concrete;

import pipe.gui.reachability.ReachabilityGraph;
import pipe.gui.plugin.GuiModule;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;

import javax.swing.*;
import java.awt.FileDialog;

/**
 * State Space module that is dynamically loaded into the GUI
 */
public class StateSpaceModule implements GuiModule {
    @Override
    public void start(PetriNet petriNet) {
        JFrame frame = new JFrame("State Space Explorer");
        FileDialog selector = new FileDialog(frame, "Select petri net", FileDialog.LOAD);
        FileDialog saver = new FileDialog(frame, "Save binary transition data", FileDialog.SAVE);
        frame.setContentPane(new ReachabilityGraph(selector, petriNet).getMainPanel());
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public String getName() {
        return "State space exploration";
    }
}
