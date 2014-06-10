/*
 * JarUtils.java
 *
 * Created on June 4, 2007, 11:10 AM
 */
package pipe.utilities.io;

import java.io.File;
import java.net.URL;
import java.util.zip.ZipEntry;


/**
 * Jar utilities class
 */
public abstract class JarUtilities {

    private JarUtilities() {}


    /**
     *
     * @param entry
     * @return file associated with the zip entry
     */
    public static File getFile(ZipEntry entry) {
        URL urlJarEntry = Thread.currentThread().getContextClassLoader().
                getResource(entry.getName());
        return new File(urlJarEntry.toString());
    }

}
