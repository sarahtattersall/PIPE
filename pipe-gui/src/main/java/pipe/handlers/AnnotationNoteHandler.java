/*
* Created on 05-Mar-2004
* Author is Michael Camacho
*
*/
package pipe.handlers;

import pipe.actions.EditAnnotationBorderAction;
import pipe.controllers.PetriNetController;
import pipe.actions.gui.PipeApplicationModel;
import pipe.gui.widgets.AnnotationEditorPanel;
import pipe.gui.widgets.EscapableDialog;
import uk.ac.imperial.pipe.models.petrinet.Annotation;

import javax.swing.*;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;


/**
 * Mouse handler for an annotation
 */
public final class AnnotationNoteHandler extends PetriNetObjectHandler<Annotation> {


    /**
     * Constructor
     * @param contentpane
     * @param note
     * @param controller
     * @param applicationModel
     */
    public AnnotationNoteHandler(Container contentpane, Annotation note, PetriNetController controller,  PipeApplicationModel applicationModel) {
        super(contentpane, note, controller, applicationModel);
        enablePopup = true;
    }

    /**
     * Creates the popup menu that the user will see when they right click on a
     * component
     */
    @Override
    public JPopupMenu getPopup(MouseEvent e) {
        int popupIndex = 0;
        JPopupMenu popup = super.getPopup(e);

        JMenuItem menuItem = new JMenuItem(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                showEditor();
            }
        });
        menuItem.setText("Edit text");
        popup.insert(menuItem, popupIndex++);

        menuItem = new JMenuItem(new EditAnnotationBorderAction(component));
        if (component.hasBorder()) {
            menuItem.setText("Disable Border");
        } else {
            menuItem.setText("Enable Border");
        }
        popup.insert(menuItem, popupIndex++);

        //      menuItem = new JMenuItem(
        //              new EditAnnotationBackgroundAction((AnnotationNote) component));
        //      if (((AnnotationNote) component).isFilled()) {
        //         menuItem.setText("Transparent");
        //      } else {
        //         menuItem.setText("Solid Background");
        //      }
        //      popup.insert(new JPopupMenu.Separator(), popupIndex++);
        //      popup.insert(menuItem, popupIndex);

        return popup;
    }


    /**
     * Shows the editor when right clicking on an annotation
     */
    private void showEditor() {
        // Build interface
        Window window = SwingUtilities.getWindowAncestor(contentPane);
        EscapableDialog guiDialog =
                new EscapableDialog(window, "PIPE5", true);

        guiDialog.add(new AnnotationEditorPanel(petriNetController.getAnnotationController(component)));

        // Make window fit contents' preferred size
        guiDialog.pack();

        // Move window to the middle of the screen
        guiDialog.setLocationRelativeTo(null);

        guiDialog.setResizable(false);
        guiDialog.setVisible(true);

        guiDialog.dispose();
    }

}
