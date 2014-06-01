/*
 * Created on 07-Feb-2004
 * Author is Michael Camacho
 */
package pipe.gui;

import pipe.io.JarUtilities;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.JarEntry;


/**
 * @author Matthew Worthington - simplification and refactoring (Jan,2007)
 * @author Pere Bonet - changes (JarUtilities)
 */
class ModuleLoader {


    public ModuleLoader() {
    }

    public static Class<?> importModule(File modFile) {
        Class<?> modClass = null;

        if (modFile.exists() && modFile.isFile() && modFile.canRead()) {
            String className = getClassName(modFile);

            modFile = modFile.getParentFile();

            while (!modFile.getName().endsWith("pipe")) {
                modFile = modFile.getParentFile();
            }
            ExtFileManager.addSearchPath(modFile);
            modClass = ExtFileManager.loadExtClass(className);
            if (!isModule(modClass)) {
                return null;
            }
        }
        return modClass;
    }

    private static String getClassName(File moduleFile) {
        String filename;

        try {
            filename = moduleFile.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        String seperator = System.getProperty("file.separator");
        filename = filename.replace(seperator.charAt(0), '.');
        filename = filename.substring(0, filename.length() - 6);
        int position = filename.lastIndexOf("pipe");
        if (position != -1) {
            filename = filename.substring(position);
        } else {
            filename = filename.substring(filename.lastIndexOf(".") + 1);
        }
        return filename;
    }

    private static boolean isModule(Class<?> modClass) {
        Class<?> interfaces[] = modClass.getInterfaces();

        for (Class<?> anInterface : interfaces) {
            if (anInterface.getName().equals("pipe.modules.interfaces.IModule")) {
                return true;
            }
        }
        return false;
    }

    public static Class<?> importModule(JarEntry entry) {
        Class<?> modClass = null;
        File file = new File(JarUtilities.getFile(entry).getPath());

        String className = getClassName(file);
        try {
            URL[] pathURLs = {file.toURI().toURL()};
            ExtFileManager.addSearchPath(pathURLs);

            modClass = ExtFileManager.loadExtClass(className);
            if (!isModule(modClass)) {
                return null;
            }
        } catch (MalformedURLException ignored) {
        }
        return modClass;
    }

   
   /*
   public static Class importModule(File modFile) {
      Class modClass = null;
      
      if (pFileName.endsWith(Pipe.PROPERTY_FILE_EXTENSION) && 
               modFile.exists() && modFile.isFile() && modFile.canRead()) {         
         try {
            prop.load(new FileInputStream(modFile));
         } catch (Exception e) {
            System.out.println("Error loading " + pFileName);
            return null;
         }
         
         ExtFileManager.addSearchPath(modFile.getParentFile());
         String moduleClassName = (String)prop.get("module.class");
         
         try {
            modClass = ExtFileManager.loadExtClass(moduleClassName);
            if (!isModule(modClass)) {
               System.out.println(moduleClassName + " is not a valid module Class");
               return null;
            }
         } catch (Exception e) {
            ;
         }
      }
      return modClass;
   }
*/
   /*
   public static Class importExternalModule(File modFile) {
      Class modClass = null;
      
      if (modFile.exists() && modFile.isFile() && modFile.canRead()) {
         ExtFileManager.addSearchPath(modFile.getParentFile());
         try {
            modClass = ExtFileManager.loadExtClass(modFile);
            if (!isModule(modClass)) {
               System.out.println(modFile.getName() + " is not a valid module Class");
               return null;
            }
         } catch (Exception e) {
            ;
         }
      }
      return modClass;
   }   
   */

}
