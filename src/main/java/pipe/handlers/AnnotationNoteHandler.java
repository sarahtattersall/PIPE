 /*
 * Created on 05-Mar-2004
 * Author is Michael Camacho
 *
 */
 package pipe.handlers;

 import pipe.actions.*;
 import pipe.views.viewComponents.AnnotationNote;

 import javax.swing.*;
 import java.awt.*;
 import java.awt.event.MouseEvent;


 public class AnnotationNoteHandler
        extends NoteHandler
 {
   

   public AnnotationNoteHandler(Container contentpane, AnnotationNote note) {
      super(contentpane, note);
      enablePopup = true;
   }

   
   /** 
    * Creates the popup menu that the user will see when they right click on a 
    * component */
   public JPopupMenu getPopup(MouseEvent e) {
      int popupIndex = 0;
      JPopupMenu popup = super.getPopup(e);
      
      JMenuItem menuItem =
              new JMenuItem(new EditNoteAction((AnnotationNote) my));
      menuItem.setText("Edit text");
      popup.insert(menuItem, popupIndex++);
      
      menuItem = new JMenuItem(
              new EditAnnotationBorderAction((AnnotationNote) my));
      if (((AnnotationNote) my).isShowingBorder()){
         menuItem.setText("Disable Border");
      } else{
         menuItem.setText("Enable Border");
      }
      popup.insert(menuItem, popupIndex++);
      
      menuItem = new JMenuItem(
              new EditAnnotationBackgroundAction((AnnotationNote) my));
      if (((AnnotationNote) my).isFilled()) {
         menuItem.setText("Transparent");
      } else {
         menuItem.setText("Solid Background");
      }
      popup.insert(new JPopupMenu.Separator(), popupIndex++);      
      popup.insert(menuItem, popupIndex);

      return popup;
   }

   
   public void mouseClicked(MouseEvent e) {
      if ((e.getComponent() == my || !e.getComponent().isEnabled()) &&
              (SwingUtilities.isLeftMouseButton(e))) { 
         if (e.getClickCount() == 2){
            ((AnnotationNote) my).enableEditMode();
         }
      }
   }
   
}
