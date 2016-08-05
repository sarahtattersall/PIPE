/*
 * Created on 16-Feb-2004
 */
package pipe.gui;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * Extension filter for filtering files
 */
public class ExtensionFilter 
        extends FileFilter {

    /**
     * Accepted extension
     */
   private final String extensionString;

    /**
     * Description of the extension
     */
   private final String extensionDescription;


    /**
     * Constructor
     * @param ext extension
     * @param desc description 
     */
   public ExtensionFilter(String ext, String desc) {
      extensionString = ext.toLowerCase();
      extensionDescription = desc;
   }


    /**
     *
     * @param f file 
     * @return true if we will accept the file
     */
   @Override
   public boolean accept(File f) {
       return f.isDirectory() || f.getName().toLowerCase().endsWith(extensionString);
   }


    /**
     *
     * @return a short description about the extension type
     */
   @Override
   public String getDescription() {
      return extensionDescription;
   }

}
