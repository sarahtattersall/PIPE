package pipe.gui.widgets;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import pipe.gui.ExtensionFilter;


/**
 * @author ...
 *
 * Opens a file browser with appropriate settings for the given filetype/extension
 */
public class FileBrowserWithCheckBox
        extends FileBrowser {
   
   private final JCheckBox checkBox;

   public FileBrowserWithCheckBox(String dialogTitle, String filetype, String ext,
           String path, boolean acceptAllFileFilterUsed, JCheckBox checkBox) {
      super(dialogTitle, filetype, ext, path, acceptAllFileFilterUsed);
  
      final JPanel panel = new JPanel(); 
      //panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS)); 
      panel.setLayout(new FlowLayout()); 
      //panel.add(Box.createHorizontalGlue()); 
            
      
      //panel.add(acceptB); 
      //panel.add(Box.createHorizontalStrut(40));
      
      //panel.add(new JCheckBox("Generate xsl from xml[Provisional]",true)); 
      panel.add(checkBox); 
      
      
      //panel.add(cancelB);
      Insets insets = this.getInsets();
      panel.setBorder(BorderFactory.createEmptyBorder( 
              insets.top,
              ((insets.left>=2)?insets.left-2:insets.left),
              insets.bottom,insets.right)); 
      
      // Tricky way to add an extra component to the dialog
      addPropertyChangeListener("ancestor",   new PropertyChangeListener(){ 
         public void propertyChange(PropertyChangeEvent arg0){
            getParent().add(panel,BorderLayout.SOUTH);
         }
      });
      
      this.checkBox = checkBox;
   }
   
   
   public String saveFile() {
      if (showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
         try {
            File f = getSelectedFile();
            if (!f.getName().endsWith("." + ext)) {
               f = new File(f.getCanonicalPath() + "." + ext); // force extension
            }
            if (f.exists() &&
                    JOptionPane.showConfirmDialog(this, f.getCanonicalPath() +
                    "\nDo you want to overwrite this file?") !=
                    JOptionPane.YES_OPTION) {
               return null;
            }
            return f.getCanonicalPath();
         } catch (IOException e) {
            /* gulp */
         }
      }
      return null;
   }
   
   public boolean isChecked(){
      return checkBox.isSelected();
   }
 
}
