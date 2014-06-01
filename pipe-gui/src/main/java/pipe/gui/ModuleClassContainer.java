package pipe.gui;

import pipe.gui.plugin.GuiModule;

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
   private final Class<? extends GuiModule> clazz;
   
   
   /** 
    * Sets up the private fields, includes instantiating an object and calling 
    * the getName method used to set the displayName.
    * @param clazz The class that the ModuleClass encapsulates.
    */
   public ModuleClassContainer(Class<? extends GuiModule> clazz) {
      this.clazz = clazz;

      try {
         Constructor<? extends GuiModule> ctr = this.clazz.getDeclaredConstructor(new Class[0]);
         Object moduleObj = ctr.newInstance();
         
         // invoke the name method for display
         Method meth = this.clazz.getMethod("getName", new Class[0]);
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
    * @return the class object that the ModuleClass encapsulates
    */
   public Class<? extends GuiModule> returnClass() {
      return clazz;
   }
   
}
