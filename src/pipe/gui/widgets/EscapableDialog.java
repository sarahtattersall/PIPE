/*
 * EscapableDialog.java
 */

package pipe.gui.widgets;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

/**
 *
 * @author Pere Bonet
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
