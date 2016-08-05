/*
 * EscapableDialog.java
 */

package pipe.gui.widgets;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Dialog that closes itself on escape key pressed
 */
@SuppressWarnings("serial")
public class EscapableDialog 
        extends JDialog{


    /** Creates a new instance of EscapableDialog
     * @param window dialog window
     * @param title of dialog
     * @param modal true if modal 
     * */
    public EscapableDialog(Window window, String title, boolean modal) {
        super(window, title);
    }

    /**
     *
     * Creates a root pane and allows it to close on exit
     *
     * @return root pane
     */
   @Override
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
