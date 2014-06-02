package pipe.handlers;

import pipe.controllers.PetriNetController;
import pipe.gui.model.PipeApplicationModel;
import pipe.gui.widgets.EscapableDialog;
import pipe.gui.widgets.PlaceEditorPanel;
import uk.ac.imperial.pipe.models.petrinet.Place;

import javax.swing.*;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;


/**
 * Handles place actions
 */
public class PlaceHandler
        extends ConnectableHandler<Place> {


    public PlaceHandler(Container contentpane, Place place, PetriNetController controller,  PipeApplicationModel applicationModel) {
        super(contentpane, place, controller, applicationModel);
    }

    @Override
    protected JPopupMenu getPopup(MouseEvent e) {
        int index = 0;
        JPopupMenu popup = super.getPopup(e);

        JMenuItem menuItem = new JMenuItem("Edit Place");
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showEditor();
            }
        };
        menuItem.addActionListener(actionListener);
        popup.insert(menuItem, index++);
        popup.insert(new JPopupMenu.Separator(), index);

        return popup;
    }

    public void showEditor() {
        // Build interface
        Window window = SwingUtilities.getWindowAncestor(contentPane);
        EscapableDialog guiDialog = new EscapableDialog(window, "PIPE2", true);

        Container contentPane = guiDialog.getContentPane();

        // 1 Set layout
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));

        // 2 Add Place editor
        contentPane.add(
                new PlaceEditorPanel(guiDialog.getRootPane(), petriNetController.getPlaceController(component),
                        petriNetController));

        guiDialog.setResizable(false);

        // Make window fit contents' preferred size
        guiDialog.pack();

        // Move window to the middle of the screen
        guiDialog.setLocationRelativeTo(null);
        guiDialog.setVisible(true);
    }
}
