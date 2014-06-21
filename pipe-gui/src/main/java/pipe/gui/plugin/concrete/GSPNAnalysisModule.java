package pipe.gui.plugin.concrete;

import pipe.gui.analysis.GSPNAnalysis;
import pipe.gui.plugin.GuiModule;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;

import javax.swing.*;
import java.awt.FileDialog;

public class GSPNAnalysisModule implements GuiModule {
    /**
     * Starts the GSPN analysis module
     * @param petriNet current Petri net to use
     */
    @Override
    public void start(PetriNet petriNet) {
        JFrame frame = new JFrame("GSPN analysis");
        FileDialog selector = new FileDialog(frame, "Select petri net", FileDialog.LOAD);

        frame.setContentPane(new GSPNAnalysis(petriNet, selector).getMainPanel());
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     *
     * @return GSPN Analysis
     */
    @Override
    public String getName() {
        return "GSPN Analysis";
    }
}
