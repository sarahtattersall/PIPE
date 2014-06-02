/*
 * EscapableDialog.java
 */

package pipe.gui.widgets;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 *
 * @author Pere Bonet
 * Dialog that closes itself on escape key pressed
 */
public class EscapableDialog 
        extends JDialog{
   
   /** Creates a new instance of EscapableDialog
    * @param frame
    * @param string
    * @param modal*/
   public EscapableDialog(Frame frame, String string, boolean modal) {
      super(frame, string, modal);
   }


    /** Creates a new instance of EscapableDialog
     * @param window
     * @param title
     * @param modal*/
    public EscapableDialog(Window window, String title, boolean modal) {
        super(window, title);
    }

   protected JRootPane createRootPane() {
      JRootPane rootPane = new JRootPane();
      KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
      Action actionListener = new AbstractAction() {
         public void actionPerformed(ActionEvent actionEvent) {
            setVisible(false);
         }
      };
      InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
      inputMap.put(stroke, "ESCAPE");
      rootPane.getActionMap().put("ESCAPE", actionListener);
      return rootPane;
   }
   
   
}
