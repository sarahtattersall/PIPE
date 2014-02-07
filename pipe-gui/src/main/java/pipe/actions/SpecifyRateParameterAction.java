package pipe.actions;

import pipe.controllers.PipeApplicationController;
import pipe.gui.RatePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SpecifyRateParameterAction extends GuiAction {
    private final PipeApplicationController pipeApplicationController;

    private RatePanel ratePanel;
    private JDialog guiDialog;

    public SpecifyRateParameterAction(PipeApplicationController pipeApplicationController) {
        super("Rate Parameter", "Specify Rate Parameters", "shift ctrl R");
        this.pipeApplicationController = pipeApplicationController;
    }

    /**
     * Pops up with an editor for the token rates if there is an active petri net
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (pipeApplicationController.getActivePetriNetController() != null) {
            buildGuiClasses();
            showGui();
        }

    }

    private void buildGuiClasses() {
        ratePanel = new RatePanel(pipeApplicationController.getActivePetriNetController());
        guiDialog = new RateDialog();

    }

    private void showGui() {
        guiDialog.setSize(600,200);
        guiDialog.setLocationRelativeTo(null);
        ratePanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        ratePanel.setOpaque(true);


        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());
        JButton ok = new JButton("OK");
        ok.addActionListener((ActionListener) guiDialog);
        buttonPane.add(ok);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener((ActionListener) guiDialog);
        buttonPane.add(cancel);

        guiDialog.add(ratePanel, BorderLayout.CENTER);
        guiDialog.add(buttonPane, BorderLayout.PAGE_END);
        ratePanel.setVisible(true);

        guiDialog.setVisible(true);
    }

    private class RateDialog extends JDialog implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("OK")) {
                if (ratePanel.isDataValid()) {
                    ratePanel.setChanges();
                    setVisible(false);
                }
            } else if (e.getActionCommand().equals("Cancel")) {
                setVisible(false);
            }
        }
    }
}
