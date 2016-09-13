package pipe.actions.gui;

import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.BoxLayout;
import javax.swing.SwingUtilities;

import pipe.controllers.application.PipeApplicationController;
import pipe.gui.widgets.EscapableDialog;
import pipe.gui.widgets.IncludeHierarchyPanel;
import pipe.gui.widgets.PlaceEditorPanel;
import pipe.views.PipeApplicationView;

@SuppressWarnings("serial")
public class IncludesAction extends GuiAction {

	
	
	private PipeApplicationController controller;
	private PipeApplicationView view;

	public IncludesAction(PipeApplicationView view, PipeApplicationController controller) {
        super("Includes", "Maintain include hierarchy");  
        this.view = view; 
        this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		showInterfacePanel(); 
	}

	private void showInterfacePanel() {
        Window window = SwingUtilities.getWindowAncestor(view);
        EscapableDialog guiDialog = new EscapableDialog(window, "PIPE5", true);

        Container contentPane = guiDialog.getContentPane();

        // 1 Set layout
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));

        // 2 Add Place editor
        contentPane.add(
                new IncludeHierarchyPanel(guiDialog.getRootPane(), controller));

        guiDialog.setResizable(false);

        // Make window fit contents' preferred size
        guiDialog.pack();

        // Move window to the middle of the screen
        guiDialog.setLocationRelativeTo(null);
        guiDialog.setVisible(true);

	}

}
