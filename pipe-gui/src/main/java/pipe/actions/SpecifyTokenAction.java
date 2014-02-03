package pipe.actions;

import pipe.controllers.PipeApplicationController;
import pipe.gui.TokenDialog;
import pipe.gui.TokenPanel;
import pipe.views.PipeApplicationView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SpecifyTokenAction extends GuiAction {
    private final PipeApplicationView pipeApplicationView;

    private final PipeApplicationController pipeApplicationController;

    private TokenPanel dialogContent;

    private JDialog guiDialog;

    private ActionEvent forcedAction;

    public SpecifyTokenAction(PipeApplicationView applicationView,
                              PipeApplicationController pipeApplicationController) {
        super("SpecifyTokenClasses", "Specify tokens", "shift ctrl T");
        this.pipeApplicationView = applicationView;
        this.pipeApplicationController = pipeApplicationController;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        buildTokenGuiClasses();
        finishBuildingGui();
    }

    public void buildTokenGuiClasses() {
        dialogContent = new TokenPanel(pipeApplicationController.getActivePetriNetController());
        guiDialog = new TokenDialog(pipeApplicationView, "Tokens", true, dialogContent);
    }

    public void finishBuildingGui() {
        guiDialog.setSize(600, 200);
        guiDialog.setLocationRelativeTo(null);
        dialogContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dialogContent.setOpaque(true);

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

        guiDialog.add(dialogContent, BorderLayout.CENTER);
        guiDialog.add(buttonPane, BorderLayout.PAGE_END);
        dialogContent.setVisible(true);

        if (forcedAction != null) {
            forceContinue();
        } else {
            guiDialog.setVisible(true);
        }
    }

    private void forceContinue() {
        ((TokenDialog) guiDialog).actionPerformed(forcedAction);
        forcedAction = null;
    }
}
