package pipe.gui;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import pipe.constants.GUIConstants;
import pipe.controllers.application.PipeApplicationController;
import pipe.gui.plugin.GuiModule;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;



/**
 * The ModuleManager class contains methods to create swing components to allow
 * the user to load modules and execute methods within them. To use, instantiate
 * a ModuleManager object and use the methods to return the required components.
 *
 * @author Camilla Clifford
 * @author David Patterson -- minor changes 24 Nov 2006
 * @author Matthew Worthington -- changed the ModuleManger to dynamically load
 *         all class files in the module directory without the need to update the cfg
 *         files and provide path properties. Modules can now be dropped into the module
 *         folder and automatically loaded on all subsequent executions of pipe.
 *         Also refactored to reduce number of methods loaded with reflection into the
 *         Jtree which were subsequently never used. Now only loading the run method of
 *         each of the modules. (Jan,2007)
 * @author Pere Bonet - JAR May 2007
 */
public class ModuleManager
{

    private final Set<Class<?>> installedModules;

    private final PipeApplicationController controller;

    private JTree moduleTree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode load_modules;
    private final String loadNodeString = "Find IModule";
    private final Component parent;


    public ModuleManager(Component view, PipeApplicationController controller)
    {
        this.controller = controller;

        parent = view;
        installedModules = new HashSet<>();
    }

    /**
     * Returns the directory under which the module properties files
     * will be found. At present this is the bin/cfg directory.
     * <p/>
     * Matthew - modified to access module folder directly
     * @return
     */
    private File getModuleDir()
    {
        File modLocation = new File(ExtFileManager.getClassRoot(this.getClass()),
                                    File.pathSeparator + "pipe" +
                                            File.pathSeparator + "plugin");

        if(!modLocation.exists())
        {
            System.out.println("Unable to find Module directory: " +
                                       modLocation.getPath());
        }
        return modLocation;
    }


    /**
     * Finds all the fully qualified (ie: full package names) module classnames
     * by recursively searching the rootDirectories
     *
     * @param rootDir The root directory to start searching from
     *                <p/>
     *                Matthew - created class filters and now cycle through the module directory
     *                dynamically loading all compliant pipe module class files.
     * @return
     */
    //only load attempt to add .class files
    private Collection<Class<? extends GuiModule>> getModuleClasses(File rootDir)
    {
        Collection<Class<? extends GuiModule>> results = new ArrayList<>();
        try {
            ClassPath classPath = ClassPath.from(this.getClass().getClassLoader());
            ImmutableSet<ClassPath.ClassInfo> set = classPath.getTopLevelClasses("pipe.gui.plugin.concrete");
            for (ClassPath.ClassInfo classInfo : set) {
                Class<?> clazz = classInfo.load();
                if (GuiModule.class.isAssignableFrom(clazz)) {
                    results.add((Class<? extends GuiModule>) clazz);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }


    /**
     * Method creates and returns a IModule management tree.
     * This consists of two nodes, one resposible for listing all the available
     * modules from the module directory, and another for admin options such as
     * list refreshing.
     * Each node of the tree has it's own user object, for class nodes this will
     * be ModuleClass, for method nodes ModuleMethod, and another one yet to be
     * implemented for other options.
     * When the user clicks on a method node the method is invoked.
     * <p/>
     * Matthew - modified to reduce unnecessary reflection, now only loading
     * the run method of each module class into the tree
     * @param moduleClass
     */
    private void addClassToTree(Class<? extends GuiModule> moduleClass)
    {
        if(installedModules.add(moduleClass))
        {
            DefaultMutableTreeNode modNode = new DefaultMutableTreeNode(new ModuleClassContainer(moduleClass));

            try
            {
                Method tempMethod = moduleClass.getMethod("start", PetriNet.class);
                ModuleMethod m = new ModuleMethod(moduleClass, tempMethod);
                m.setName(modNode.getUserObject().toString());
                modNode.add(new DefaultMutableTreeNode(m));
            }
            catch(SecurityException e)
            {
                e.printStackTrace();
            }
            catch(NoSuchMethodException e)
            {
                e.printStackTrace();
            }

            if(modNode.getChildCount() == 1)
            {
                Object m = ((DefaultMutableTreeNode) modNode.getFirstChild()).
                        getUserObject();
                load_modules.add(new DefaultMutableTreeNode(m));
            }
            else load_modules.add(modNode);
        }
    }


    public JTree getModuleTree()
    {
        URL modulesDirURL = Thread.currentThread().getContextClassLoader().getResource("pipe" + System.getProperty("file.separator") + "modules" + System.getProperty("file.separator"));

        Collection<Class<? extends GuiModule>> classes = new ArrayList<>();
//        if(JarUtilities.isJarFile(modulesDirURL))
//        {
//            try
//            {
//                JarFile jarFile = new JarFile(JarUtilities.getJarName(modulesDirURL));
//                ArrayList<JarEntry> modules = JarUtilities.getJarEntries(jarFile, "modules");
//
//                for(JarEntry module : modules)
//                {
//                    if(module.getName().toLowerCase().endsWith(".class"))
//                    {
//
//                        Class<?> pluginClass = ModuleLoader.importModule(module);
//                        if(pluginClass != null)
//                        {
//                            classes.add(pluginClass);
//                        }
//                    }
//                }
//            }
//            catch(IOException ex)
//            {
//                ex.printStackTrace();
//            }
//        }
//        else
        if(true)
        {
            File dir = getModuleDir();

            // get the names of all the classes that are confirmed to be modules
            Collection<Class<? extends GuiModule>> names = getModuleClasses(dir);

            /*
             * temporarily get rid of the modules that needs web servers for calculation
             */
//            try {
//				names.remove(Class.forName("pipe.modules.steadyStateCloud.SteadyState"));
//				names.remove(Class.forName("pipe.modules.passage.Passage"));
//				names.remove(Class.forName("pipe.modules.passageTimeForTaggedNet.Passage"));
//			} catch (ClassNotFoundException e) {
//				TODO Auto-generated catch block
//				e.printStackTrace();
//			}
            classes.addAll(names);
        }

        // create the root node
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Analysis Module Manager");

        // create root children
        load_modules = new DefaultMutableTreeNode("Available Modules");

        MutableTreeNode add_modules = new DefaultMutableTreeNode(loadNodeString);

        // iterate over the class names and create a node for each
        for (Class<? extends GuiModule> clazz : classes) {
            try {
                // create each ModuleClass node using an instantiation of the
                // ModuleClass
                addClassToTree(clazz);
            } catch (Throwable ignored) {
                System.out.println("Error in creating class node");
            }
        }

        root.add(load_modules);
        root.add(add_modules);

        treeModel = new DefaultTreeModel(root);

        moduleTree = new JTree(treeModel);
        moduleTree.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION);

        moduleTree.addMouseListener(new TreeHandler());

        moduleTree.setFocusable(false);

        // expand the modules path
        moduleTree.expandPath(moduleTree.getPathForRow(1));
        return moduleTree;
    }


    /**
     * Removes a node from the IModule subtree
     *
     * @param newNode The node to be removed.
     */
    void removeModuleFromTree(MutableTreeNode newNode)
    {
        treeModel.removeNodeFromParent(newNode);
        treeModel.reload();
    }


    /**
     * Action object that can be used to remove a module from the ModuleTree
     */
    class RemoveModuleAction extends AbstractAction
    {
        final DefaultMutableTreeNode removeNode;

        RemoveModuleAction(TreePath path)
        {
            removeNode = (DefaultMutableTreeNode) path.getLastPathComponent();
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            Object o = removeNode.getUserObject();

            if(o instanceof ModuleMethod)
            {
                installedModules.remove(((ModuleMethod) o).getModClass());
            }
            else if(o instanceof ModuleClassContainer)
            {
                installedModules.remove(((ModuleClassContainer) o).returnClass());
            }
            else
            {
                System.err.println("Don't know how to delete class for "
                                           + o.getClass());
            }
            removeModuleFromTree(removeNode);
            moduleTree.expandPath(moduleTree.getPathForRow(1));
        }
    }


    // now add in the action listener to enable module method loading.
    public class TreeHandler extends MouseAdapter
    {

        private void showPopupMenu(MouseEvent e)
        {
            TreePath selPath = moduleTree.getPathForLocation(e.getX(), e.getY());

            if(selPath != null)
            {
                DefaultMutableTreeNode node =
                        (DefaultMutableTreeNode) selPath.getLastPathComponent();
                Object nodeObj = node.getUserObject();

                if((nodeObj instanceof ModuleClassContainer) ||
                        (nodeObj instanceof ModuleMethod))
                {
                    JPopupMenu popup = new JPopupMenu();
                    TreePath removePath = moduleTree.getPathForLocation(e.getX(),
                                                                        e.getY());
                    JMenuItem menuItem =
                            new JMenuItem(new RemoveModuleAction(removePath));
                    menuItem.setText("Remove Module");
                    popup.add(menuItem);
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        }


        @Override
        public void mouseReleased(MouseEvent e)
        {
            if(e.isPopupTrigger())
            {
                showPopupMenu(e);
            }
        }


        @Override
        public void mousePressed(MouseEvent e)
        {
            if(e.isPopupTrigger())
            {
                showPopupMenu(e);
            }
        }


        @Override
        public void mouseClicked(MouseEvent e)
        {
            int selRow = moduleTree.getRowForLocation(e.getX(), e.getY());
            TreePath selPath = moduleTree.getPathForLocation(e.getX(), e.getY());

            if(selRow != -1)
            {
                DefaultMutableTreeNode node =
                        (DefaultMutableTreeNode) selPath.getLastPathComponent();
                Object nodeObj = node.getUserObject();

                if(e.getClickCount() == 2)
                {
                    if(nodeObj instanceof ModuleMethod)
                    {

                        PetriNet petriNet = controller.getActivePetriNetController().getPetriNet();
                        ((ModuleMethod) nodeObj).execute(petriNet);
                    }
                    else if(nodeObj == loadNodeString)
                    {
                        DefaultMutableTreeNode newNode;

                        //Create a file chooser
                        JFileChooser fc = new JFileChooser();
                        fc.setFileFilter(
                                new ExtensionFilter(GUIConstants.PROPERTY_FILE_EXTENSION,
                                                    GUIConstants.PROPERTY_FILE_DESC));
                        //In response to a button click:
                        int returnVal = fc.showOpenDialog(parent);
                        if(returnVal == JFileChooser.APPROVE_OPTION)
                        {
                            File moduleProp = fc.getSelectedFile();
                            Class<?> newModuleClass = ModuleLoader.importModule(moduleProp);

                            if(newModuleClass != null)
                            {
                                //TODO
//                                addClassToTree(newModuleClass);
                                treeModel.reload();
                                moduleTree.expandPath(moduleTree.getPathForRow(1));
                            }
                            else
                            {
                                JOptionPane.showMessageDialog(
                                        parent,
                                        "Invalid file selected.\n Please ensure the "
                                                + "class implements the IModule interface and is"
                                                + " on the CLASSPATH.",
                                        "File Selection Error",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                }
            }
        }
    }

}
