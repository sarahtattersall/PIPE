package pipe.gui;

import pipe.gui.plugin.GuiModule;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * ModuleClass encapsulates information about the IModule class and is designed
 * to be used as a userobject in nodes in a JTree, in this case for nodes
 * representing module classes. This isn't designed for use anywhere else.
 *
 * @author Camilla Clifford
 */
class ModuleClassContainer {

    private static final Logger LOGGER = Logger.getLogger(ModuleClassContainer.class.getName());

    private final Class<? extends GuiModule> clazz;

    private String displayName;


    /**
     * Sets up the private fields, includes instantiating an object and calling
     * the getName method used to set the displayName.
     *
     * @param clazz The class that the ModuleClass encapsulates.
     */
    public ModuleClassContainer(Class<? extends GuiModule> clazz) {
        this.clazz = clazz;

        try {
            Constructor<? extends GuiModule> ctr = this.clazz.getDeclaredConstructor(new Class[0]);
            Object moduleObj = ctr.newInstance();

            // invoke the name method for display
            Method meth = this.clazz.getMethod("getName", new Class[0]);
            displayName = (String) meth.invoke(moduleObj);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            displayName = "(Error in module instantiation)";
            LOGGER.log(Level.SEVERE, e.getMessage());
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
