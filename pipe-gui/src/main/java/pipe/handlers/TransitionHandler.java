package pipe.handlers;

import pipe.actions.gui.PipeApplicationModel;
import pipe.controllers.PetriNetController;
import pipe.gui.widgets.EscapableDialog;
import pipe.gui.widgets.TransitionEditorPanel;
import uk.ac.imperial.pipe.models.petrinet.Transition;

import javax.swing.*;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * Class used to implement methods corresponding to mouse events on transitions.
 */
public class TransitionHandler extends ConnectableHandler<Transition> {


    /**
     * Constructor
     * @param contentpane parent pane of the transition view
     * @param transition underlying transition model
     * @param controller Petri net controller for the Petri net the transition is housed in
     * @param applicationModel main PIPE application model
     */
    public TransitionHandler(Container contentpane, Transition transition, PetriNetController controller,
                             PipeApplicationModel applicationModel) {
        super(contentpane, transition, controller, applicationModel);
    }


    /**
     * Noop action
     * @param e
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        //No action needed
    }


    /**
     * Creates the popup menu that the user will see when they right click on a
     * component
     */
    @Override
    protected JPopupMenu getPopup(MouseEvent e) {
        int index = 0;
        JPopupMenu popup = super.getPopup(e);
        JMenuItem menuItem = new JMenuItem("Edit Transition");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showEditor();
            }
        });
        popup.insert(menuItem, index++);

        popup.insert(new JPopupMenu.Separator(), index);
        return popup;
    }

    /**
     * Displays the editor dialog for the transition
     */
    public void showEditor() {
        Window window = SwingUtilities.getWindowAncestor(contentPane);
        EscapableDialog guiDialog = new EscapableDialog(window, "PIPE", true);
        TransitionEditorPanel te = new TransitionEditorPanel(guiDialog.getRootPane(),
                petriNetController.getTransitionController(component), petriNetController);
        guiDialog.add(te);
        guiDialog.getRootPane().setDefaultButton(null);
        guiDialog.setResizable(false);
        guiDialog.pack();
        guiDialog.setLocationRelativeTo(null);
        guiDialog.setVisible(true);
    }


}
