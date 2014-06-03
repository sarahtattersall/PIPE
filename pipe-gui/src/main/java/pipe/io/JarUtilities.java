/*
 * JarUtils.java
 *
 * Created on June 4, 2007, 11:10 AM
 */
package pipe.io;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;


/**
 * @author Pere Bonet
 */
public abstract class JarUtilities {

   
   public static boolean isJarFile(URL url) {
       return url != null && url.toString().startsWith("jar:file:");

   }

   
   public static String getJarName(URL url) {
      return url.toString().substring(9, url.toString().indexOf("!/"));
   }

   
   public static List<JarEntry> getJarEntries(JarFile jarFile, String directory) {
      Enumeration<JarEntry> enumeration = jarFile.entries();
      List<JarEntry> nets = new ArrayList<>();
      String separatedDirectory = directory + File.separator;
      
      while (enumeration.hasMoreElements()) {
         JarEntry je = (JarEntry)enumeration.nextElement();
         String s = je.toString();
         if ((s.contains(separatedDirectory)) && (s.length() > separatedDirectory.length())){
            nets.add(je);
         }
      }
      return nets;
   }

   
   public static File getFile(ZipEntry entry) {
      URL urlJarEntry  = Thread.currentThread().getContextClassLoader().
                 getResource(entry.getName());
      return new File (urlJarEntry.toString());      
   }
   
}
