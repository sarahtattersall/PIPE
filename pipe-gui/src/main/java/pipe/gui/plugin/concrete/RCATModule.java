package pipe.gui.plugin.concrete;

import pipe.gui.widget.RCATForm;
import pipe.gui.plugin.GuiModule;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;

import javax.swing.*;
import java.awt.*;

/**
 * RCAT Module that is dynamically loaded into the GUI
 */
public class RCATModule implements GuiModule{
    /**
     * Starts the RCAT module
     * @param petriNet current Petri net to use
     */
    @Override
    public void start(PetriNet petriNet) {
        JFrame frame = new JFrame("RCAT for Stochastic Petri Nets");
        FileDialog selector = new FileDialog(frame, "Select petri net", FileDialog.LOAD);
        frame.setContentPane(new RCATForm(petriNet, selector).getPrimPanel());
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     *
     * @return  RCAT Module
     */
    @Override
    public String getName() {
        return "RCAT for Stochastic Petri Nets";
    }


}
