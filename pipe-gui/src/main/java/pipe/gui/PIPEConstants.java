package pipe.gui;

import java.io.File;

/**
 * Contains constants used by the PIPE GUI.
 */
public class PIPEConstants {
    public static final String IMAGE_PATH = File.separator + "images" + File.separator;

    /**
     * Path to the directory that the PIPE examples are located in
     */
    public static final String EXAMPLES_PATH = File.separator + "extras" + File.separator + "examples" + File.separator;

    public static String getExamplesDirectoryPath() {
        return "extras" + File.separator + "examples";
    }

}
