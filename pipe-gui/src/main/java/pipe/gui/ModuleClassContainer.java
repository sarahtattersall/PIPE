package pipe.gui;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;


/**
 * ModuleClass encapsulates information about the IModule class and is designed
 * to be used as a userobject in nodes in a JTree, in this case for nodes 
 * representing module classes. This isn't designed for use anywhere else.
 * @author Camilla Clifford
 */
class ModuleClassContainer {
   
   private String displayName;
   private final Class thisClass;
   
   
   /** 
    * Sets up the private fields, includes instantiating an object and calling 
    * the getName method used to set the displayName.
    * @param cl The class that the ModuleClass encapsulates.
    */
   public ModuleClassContainer(Class cl) {
      thisClass = cl;

      try {
         Constructor ct = thisClass.getDeclaredConstructor(new Class[0]);
         Object moduleObj = ct.newInstance();
         
         // invoke the name method for display
         Method meth = thisClass.getMethod("getName", new Class[0]);
         displayName = (String)meth.invoke(moduleObj);
      } catch (Throwable e) {
         System.out.println("Error in ModuleClass instantiation: " + 
                 e.toString());
         displayName = "(Error in module instantiation)";
      }
   }
   
   
   /** 
    * Overides the object method in order to provide the correct display name 
    */
   public String toString() {
      return displayName;
   }
   
   
   /** 
    * Returns the class object that the ModuleClass encapsulates
    * @return
    */
   public Class returnClass() {
      return thisClass;
   }
   
}
