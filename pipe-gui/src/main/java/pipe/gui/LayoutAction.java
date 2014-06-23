package pipe.gui;

import pipe.actions.gui.GuiAction;
import pipe.controllers.PetriNetController;
import pipe.controllers.application.PipeApplicationController;
import pipe.gui.widgets.EscapableDialog;
import pipe.views.PipeApplicationView;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;

import javax.swing.*;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class LayoutAction extends GuiAction {


    private final PipeApplicationController pipeApplicationController;

    private final PipeApplicationView applicationView;

    public LayoutAction(PipeApplicationController pipeApplicationController, PipeApplicationView applicationView) {
        super("Layout", "Layout", KeyEvent.VK_P, InputEvent.ALT_DOWN_MASK);
        this.pipeApplicationController = pipeApplicationController;
        this.applicationView = applicationView;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PetriNetController petriNetController = pipeApplicationController.getActivePetriNetController();
        PetriNet petriNet = petriNetController.getPetriNet();
        showLayoutEditor(petriNet);
    }

    public void showLayoutEditor(PetriNet petriNet) {
        EscapableDialog guiDialog = new EscapableDialog(applicationView, "PIPE2", true);
        Container contentPane = guiDialog.getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
        LayoutForm formLayout = new LayoutForm(petriNet);
        contentPane.add(formLayout.getMainPanel());
        guiDialog.setResizable(false);
        guiDialog.pack();
        guiDialog.setLocationRelativeTo(null);
        guiDialog.setVisible(true);
    }
}
