package utils;

public class FileUtils {

    public static String fileLocation(String path) {
        return FileUtils.class.getResource(path).getPath();
    }
}
