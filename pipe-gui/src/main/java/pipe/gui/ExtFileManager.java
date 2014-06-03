/*
 * Created on 07-Feb-2004
 * Author is Michael Camacho
 */
package pipe.gui;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;


final class ExtFileManager {

    private static URLClassLoader cLoader = null;


    private static final Logger LOGGER = Logger.getLogger(ExtFileManager.class.getName());
    private ExtFileManager() {
    }


    public static Class<?> loadExtClass(String className) {
        Class<?> c = null;

        try {
            c = cLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        return c;
    }


    public static Class<?> loadExtClass(File classFile) {
        Class<?> myClass = null;
        File path = classFile.getParentFile();
        String name = classFile.getName();

        addSearchPath(path);
        if (name.endsWith(".class")) {
            name = name.substring(0, name.length() - 6);
            try {
                myClass = cLoader.loadClass(name);
            } catch (ClassNotFoundException e) {
                LOGGER.log(Level.SEVERE, e.getMessage());
            }
        }
        return myClass;
    }


    public static void addSearchPath(File p) {
        if (p.exists() && p.isDirectory()) {
            try {
                URL[] pathURLs = {p.getCanonicalFile().toURI().toURL()};
                addSearchPath(pathURLs);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to add path: URI.toURL generated an error." + e.getMessage());
            }
        }
    }


    public static void addSearchPath(URL[] urls) {
        if (cLoader == null) {
            cLoader = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());
        } else {
            cLoader = new URLClassLoader(urls, cLoader);
        }
    }


    public static File getClassRoot(Class someClass) {
        File dataDir;

        URL url = Thread.currentThread().getContextClassLoader().getResource("images");

        try {
            URI sourceURI = new URI(url.toString());
            dataDir = new File(sourceURI).getParentFile();
        } catch (URISyntaxException e) {
            return null;
        } catch (IllegalArgumentException e) {
            return null;
        }

        if (!dataDir.isDirectory()) {
            dataDir = dataDir.getParentFile();
        }
        return dataDir;
    }

}
