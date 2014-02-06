package pipe.actions.file;

import pipe.views.PipeApplicationView;

import java.awt.event.ActionEvent;

public class CloseAction extends FileAction {
    private final PipeApplicationView pipeApplicationView;

    public CloseAction(PipeApplicationView pipeApplicationView) {
        super("Close", "Close the current tab", "ctrl W");
        this.pipeApplicationView = pipeApplicationView;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        pipeApplicationView.removeCurrentTab();
    }
}
