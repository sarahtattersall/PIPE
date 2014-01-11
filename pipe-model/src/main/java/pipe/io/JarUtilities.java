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
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * @author Pere Bonet
 */
public abstract class JarUtilities {

   
   public static boolean isJarFile(URL url) {
       if(url ==null)
           return false;

      return url.toString().startsWith("jar:file:");
   }

   
   public static String getJarName(URL url) {
      return url.toString().substring(9, url.toString().indexOf("!/"));
   }

   
   public static ArrayList getJarEntries(JarFile jarFile, String directory) {
      Enumeration enumeration = jarFile.entries();
      ArrayList <JarEntry> nets = new ArrayList();
      directory = directory + System.getProperty("file.separator");
      
      while (enumeration.hasMoreElements()) {
         JarEntry je = (JarEntry)enumeration.nextElement();
         String s = je.toString();
         if ((s.indexOf(directory) > -1) && (s.length() > directory.length())){
            nets.add(je);
         }
      }
      return nets;
   }

   
   public static File getFile(JarEntry entry) {
      URL urlJarEntry  = Thread.currentThread().getContextClassLoader().
                 getResource(entry.getName());
      return new File (urlJarEntry.toString());      
   }
   
}
