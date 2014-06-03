package pipe.handlers;

import pipe.actions.petrinet.SplitArcAction;
import pipe.controllers.PetriNetController;
import pipe.actions.gui.PipeApplicationModel;
import pipe.gui.widgets.ArcWeightEditorPanel;
import pipe.gui.widgets.EscapableDialog;
import uk.ac.imperial.pipe.models.petrinet.Arc;
import uk.ac.imperial.pipe.models.petrinet.ArcType;
import uk.ac.imperial.pipe.models.petrinet.Connectable;

import javax.swing.*;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * Class used to implement methods corresponding to mouse events on arcs.
 */
public class ArcHandler<S extends Connectable, T extends Connectable>
        extends PetriNetObjectHandler<Arc<S, T>> {


    public ArcHandler(Container contentPane, Arc<S, T> component, PetriNetController controller,  PipeApplicationModel applicationModel) {
        super(contentPane, component, controller, applicationModel);
        enablePopup = true;
    }

    /**
     * Creates the popup menu that the user will see when they right click on a
     * component
     */
    @Override
    public JPopupMenu getPopup(MouseEvent e) {
        int popupIndex = 0;
        JMenuItem menuItem;
        JPopupMenu popup = super.getPopup(e);


        menuItem = new JMenuItem(new SplitArcAction(petriNetController.getArcController(component),
                e.getPoint()));
        menuItem.setText("Split Arc Segment");
        popup.insert(menuItem, popupIndex++);

        popup.insert(new JPopupMenu.Separator(), popupIndex++);

        if (component.getType().equals(ArcType.NORMAL)) {
            menuItem = new JMenuItem("Edit Weight");
            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showEditor();
                }
            });
            popup.insert(menuItem, popupIndex++);

            popup.insert(new JPopupMenu.Separator(), popupIndex);
        }
        return popup;
    }


    @Override
    public void mouseDragged(MouseEvent e) {
        //Do nothing on mouse drag
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        //Do nothing on mouse wheel move
    }

    public void showEditor() {
        // Build interface
        Window owner = SwingUtilities.getWindowAncestor(contentPane);

        EscapableDialog guiDialog = new EscapableDialog(owner, "PIPE", true);

        ArcWeightEditorPanel arcWeightEditor = new ArcWeightEditorPanel(guiDialog.getRootPane(), petriNetController,
                petriNetController.getArcController(component));

        guiDialog.add(arcWeightEditor);

        guiDialog.getRootPane().setDefaultButton(null);

        guiDialog.setResizable(false);

        // Make window fit contents' preferred size
        guiDialog.pack();

        // Move window to the middle of the screen
        guiDialog.setLocationRelativeTo(null);

        guiDialog.setVisible(true);

        guiDialog.dispose();
    }
}
