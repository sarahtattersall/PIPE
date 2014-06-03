/*
 * Created on 07-Feb-2004
 * Author is Michael Camacho
 */
package pipe.gui;

import java.io.File;
import java.io.IOException;


/**
 * Utility class that dynamically loads the modules required for PIPE
 */
public final class ModuleLoader {


    /**
     * Imodule location
     */
    private static final String IMODULE_LOCATION = "pipe.modules.interfaces.IModule";


    private ModuleLoader() {
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
            if (anInterface.getName().equals(IMODULE_LOCATION)) {
                return true;
            }
        }
        return false;
    }
}
