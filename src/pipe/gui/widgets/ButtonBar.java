package pipe.gui.widgets;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRootPane;


/**
 * ButtonBar object
 * Create one with
 * @author Maxim
 */
public class ButtonBar 
        extends JPanel {
   
   
   /**
    * Multiple button constructor
    * @param captions - array of Strings for button captions
    * @param actions - array of ActionListeners for button actions
    */
   public ButtonBar(String[] captions, ActionListener[] actions) {
      super();
      this.setLayout(new FlowLayout());
      for (int i = 0; i < captions.length; i++) {
         JButton b = new JButton(captions[i]);
         b.addActionListener(actions[i]);
         this.add(b);
      }
      Dimension d = this.getPreferredSize();
      this.setMinimumSize(d);
      this.setMaximumSize(d);
   }
   
   
   /**
    * Single button constructor
    * @param caption - String caption
    * @param action - ActionListener for button action
    */
   public ButtonBar(String caption, ActionListener action) {
      this(new String[]{caption}, new ActionListener[]{action});
   }
   
   /**
    * Single button constructor
    * @param caption - String caption
    * @param action - ActionListener for button action
    * @param rootPane
    * @param action - ActionListener for button action
    */
   public ButtonBar(String caption, ActionListener action, JRootPane rootPane) {
      this(new String[]{caption}, new ActionListener[]{action});
      rootPane.setDefaultButton((JButton)(this.getComponents()[0]));
   }   

   
   public void setButtonsEnabled(boolean b) {
      this.getComponents();
      for (int i = 0; i < this.getComponents().length; i++) {
         Component c = this.getComponent(i);
         if (c instanceof JButton) {
            c.setEnabled(b);
         }
         
      }
   }
   
}
