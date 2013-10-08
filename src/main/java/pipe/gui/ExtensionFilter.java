/*
 * Created on 16-Feb-2004
 */
package pipe.gui;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class ExtensionFilter 
        extends FileFilter {

   private final String myExtensionString;  // The extension we will accept
   private final String myExtensionDesc;    // A description of its meaning

   
   public ExtensionFilter(String ext, String desc) {
      myExtensionString = ext.toLowerCase();
      myExtensionDesc = desc;
   }

   
   public boolean accept(File f) {
      return f.isDirectory() 
             || f.getName().toLowerCase().endsWith(myExtensionString);
   }	// Don't want directories, especially ones which end with the desired extension!

   
   public String getDescription() {
      return myExtensionDesc;
   }

}
