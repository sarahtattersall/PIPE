package pipe.gui.reachability;

import pipe.GuiModule;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;

import javax.swing.*;
import java.awt.FileDialog;

public class StateSpaceModule implements GuiModule {
    @Override
    public void start(PetriNet petriNet) {
        JFrame frame = new JFrame("State Space Explorer");
        FileDialog selector = new FileDialog(frame, "Select petri net", FileDialog.LOAD);
        FileDialog saver = new FileDialog(frame, "Save binary transition data", FileDialog.SAVE);
        frame.setContentPane(new ReachabilityGraph(selector, saver).getMainPanel());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }
}
