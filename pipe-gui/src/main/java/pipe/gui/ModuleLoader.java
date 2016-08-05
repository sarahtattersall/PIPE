/*
 * Created on 07-Feb-2004
 * Author is Michael Camacho
 */
package pipe.gui;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Utility class that dynamically loads the modules required for PIPE
 */
public final class ModuleLoader {


    /**
     * Imodule location
     */
    private static final String IMODULE_LOCATION = "pipe.modules.interfaces.IModule";

    /**
     * Class logger
     */
    private static final Logger LOGGER = Logger.getLogger(ModuleLoader.class.getName());


    /**
     * Private constructor for the static class
     */
    private ModuleLoader() {
    }

    /**
     * Jar to include as a module
     * @param modFile jar file 
     * @return class of the module 
     */
    public static Class<?> importModule(File modFile) {
        Class<?> modClass = null;

        if (modFile.exists() && modFile.isFile() && modFile.canRead()) {
            String className = getClassName(modFile);

            modFile = modFile.getParentFile();

            File moduleFile = modFile;
            while (!moduleFile.getName().endsWith("pipe")) {
                moduleFile = moduleFile.getParentFile();
            }
            ExtFileManager.addSearchPath(moduleFile);
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
            LOGGER.log(Level.SEVERE, e.getMessage());
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

    /**
     *
     * @param modClass
     * @return true if the class is a module
     */
    private static boolean isModule(Class<?> modClass) {
        for (Class<?> anInterface : modClass.getInterfaces()) {
            if (anInterface.getName().equals(IMODULE_LOCATION)) {
                return true;
            }
        }
        return false;
    }
}
