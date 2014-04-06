package pipe.actions.gui.tokens;

import pipe.actions.gui.GuiAction;
import pipe.controllers.PipeApplicationController;
import pipe.gui.TokenDialog;
import pipe.gui.TokenPanel;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class SpecifyTokenAction extends GuiAction {
    private final PipeApplicationController pipeApplicationController;

    private TokenPanel dialogContent;

    private JDialog guiDialog;

    private ActionEvent forcedAction;

    public SpecifyTokenAction(PipeApplicationController pipeApplicationController) {
        super("SpecifyTokenClasses", "Specify tokens (ctrl-shift-T)", KeyEvent.VK_T,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | InputEvent.SHIFT_DOWN_MASK);
        this.pipeApplicationController = pipeApplicationController;
    }

    /**
     * Pops up to change the petri net tokens if there is an active petri net
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (pipeApplicationController.getActivePetriNetController() != null) {
            buildTokenGuiClasses();
            finishBuildingGui();
        }
    }

    public void buildTokenGuiClasses() {
        dialogContent = new TokenPanel(pipeApplicationController.getActivePetriNetController());
        guiDialog = new TokenDialog("Tokens", true, dialogContent);
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
