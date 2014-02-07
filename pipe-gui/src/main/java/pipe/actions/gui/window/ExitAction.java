package pipe.actions.gui.window;

import pipe.actions.gui.GuiAction;
import pipe.controllers.PipeApplicationController;
import pipe.views.PipeApplicationView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

public class ExitAction extends GuiAction {

    PipeApplicationView view;

    private final PipeApplicationController pipeApplicationController;

    public ExitAction(PipeApplicationView view, PipeApplicationController pipeApplicationController) {
        super("Exit", "Close the program", KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        this.view = view;
        this.pipeApplicationController = pipeApplicationController;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        tryToExit();
    }

    public void tryToExit() {
        tryToExit(!pipeApplicationController.anyNetsChanged());
    }

    public void tryToExit(boolean shouldExit) {
        if (shouldExit) {
            view.dispose();
            System.exit(0);
        } else {
            List<String> changedNames = pipeApplicationController.getNetsChanged();
            String message = changedNamesMessage(changedNames);
            int result = JOptionPane.showConfirmDialog(view,  message,
                    "Confirm Exit", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            switch (result) {
                case JOptionPane.YES_OPTION:
                    tryToExit(true);
                    break;
                case JOptionPane.CLOSED_OPTION:
                case JOptionPane.CANCEL_OPTION:
                    break;
            }
        }
    }

    public String changedNamesMessage(Iterable<String> changedNames) {
        StringBuilder buffer = new StringBuilder("The following Petri nets have changed. Do you still want to exit?");
         for (String name : changedNames) {
             buffer.append("\n");
             buffer.append(name);
         }
        return buffer.toString();
    }
}
