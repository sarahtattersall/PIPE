/*
* Created on 05-Mar-2004
* Author is Michael Camacho
*
*/
package pipe.handlers;

import pipe.actions.EditAnnotationBorderAction;
import pipe.controllers.PetriNetController;
import pipe.views.viewComponents.AnnotationView;
import uk.ac.imperial.pipe.models.petrinet.Annotation;

import javax.swing.*;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;


public class AnnotationNoteHandler extends PetriNetObjectHandler<Annotation, AnnotationView> {


    public AnnotationNoteHandler(AnnotationView view, Container contentpane, Annotation note,
                                 PetriNetController controller) {
        super(view, contentpane, note, controller);
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
                viewComponent.enableEditMode();
            }
        });
        menuItem.setText("Edit text");
        popup.insert(menuItem, popupIndex++);

              menuItem = new JMenuItem(
                      new EditAnnotationBorderAction(component));
              if (viewComponent.isShowingBorder()){
                 menuItem.setText("Disable Border");
              } else{
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
//
//    @Override
//    public void mouseClicked(MouseEvent e) {
//        if (!e.getComponent().isEnabled() && (SwingUtilities.isRightMouseButton(e))) {
//            viewComponent.enableEditMode();
//        }
//    }

}
