package pipe.gui.widgets;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import pipe.gui.ExtensionFilter;

/**
 * @author Maxim
 *
 * Opens a file browser with appropriate settings for the given filetype/extension
 */
public class FileBrowser
        extends JFileChooser {

   final String ext;

   public FileBrowser(String filetype, String ext, String path) {
      super();
      if (filetype == null) {
         filetype = "file";
      }

      if (path != null) {
         File f = new File(path);
         if (f.exists()) {
            setCurrentDirectory(f);
         }
         if (!f.isDirectory()) {
            setSelectedFile(f);
         }
      }

      this.ext = ext;
      ExtensionFilter filter = new ExtensionFilter(ext, filetype);

      setFileFilter(filter);
   }

   public FileBrowser(String path) {
      this("Petri net", "xml", path); // default parameters
   }

   FileBrowser() {
      this(null);
   }

   FileBrowser(String dialogTitle, String filetype, String ext,
               String path, boolean acceptAllFileFilterUsed) {
      this(filetype, ext, path);
      this.setDialogTitle(dialogTitle);
      this.setAcceptAllFileFilterUsed(acceptAllFileFilterUsed);
   }

   public File openFile() {
      if (showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
         try {
            return getSelectedFile().getCanonicalFile();
         } catch (IOException e) {
            /* gulp */
         }
      }
      return null;
   }

   public String saveFile() {
      while (showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
         try {
            File f = getSelectedFile();
            if (!f.getName().endsWith("." + ext)) {
               f = new File(f.getCanonicalPath() + "." + ext); // force extension
            }
            if (f.exists()) {
               int option = JOptionPane.showConfirmDialog(this, f.getCanonicalPath() +
                       "\nDo you want to overwrite this file?");
               switch (option) {
                  case JOptionPane.YES_OPTION:
                     return f.getCanonicalPath();
                     //break;
                  case JOptionPane.NO_OPTION:
                     break;
                  case JOptionPane.CANCEL_OPTION:
                     return null;
                     //break;
                  default:
                     break;
               }
            } else {
               return f.getCanonicalPath();
            }
         } catch (IOException e) {
            /* gulp */
         }
      }
      return null;
   }
}
