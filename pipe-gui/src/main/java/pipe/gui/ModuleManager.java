package pipe.gui;

import pipe.io.JarUtilities;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


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

    private final HashSet installedModules;
    private JTree moduleTree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode load_modules;
    private final String loadNodeString = "Find IModule";
    private final Component parent;


    public ModuleManager()
    {
        parent = ApplicationSettings.getApplicationView();
        installedModules = new HashSet();
    }

    /**
     * Returns the directory under which the module properties files
     * will be found. At present this is the bin/cfg directory.
     * <p/>
     * Matthew - modified to access module folder directly
     * @return
     */
    File getModuleDir()
    {
        File modLocation = new File(ExtFileManager.getClassRoot(this.getClass()),
                                    System.getProperty("file.separator") + "pipe" +
                                            System.getProperty("file.separator") + "modules");

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
    Vector getModuleClasses(File rootDir)
    {
        final ExtensionFilter class_files = new ExtensionFilter(Constants.CLASS_FILE_EXTENSION, Constants.CLASS_FILE_DESC);
        Vector classes = new Vector();
        Class aModuleClass;

        //recursively search through files and folders of module directory
        File children[] = rootDir.listFiles();

        // our base case just returns the empty vector
        if(children == null || children.length == 0)
        {
            return classes;
        }
        for(File aChildren : children)
        {
            if((aChildren).isDirectory())
            {
                classes.addAll(getModuleClasses(aChildren));
            }
            else if(class_files.accept(aChildren))
            {
                aModuleClass = ModuleLoader.importModule(aChildren);
                if(aModuleClass != null)
                {
                    classes.addElement(aModuleClass);
                }
            }
        }
        return classes;
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
    private void addClassToTree(Class moduleClass)
    {
        DefaultMutableTreeNode modNode;
        if(installedModules.add(moduleClass))
        {
            modNode = new DefaultMutableTreeNode(new ModuleClassContainer(moduleClass));

            try
            {
                Method tempMethod = moduleClass.getMethod("start");
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
        // get the names of all the classes that are confirmed to be modules
        Vector names = new Vector();
        Vector classes = new Vector();

        URL modulesDirURL = Thread.currentThread().getContextClassLoader().getResource("pipe" + System.getProperty("file.separator") + "modules" + System.getProperty("file.separator"));
        
        if(JarUtilities.isJarFile(modulesDirURL))
        {
            try
            {
                JarFile jarFile = new JarFile(JarUtilities.getJarName(modulesDirURL));
                ArrayList<JarEntry> modules = JarUtilities.getJarEntries(jarFile, "modules");

                for(JarEntry module : modules)
                {
                    if(module.getName().toLowerCase().endsWith(".class"))
                    {
                    	
                        Class aModuleClass = ModuleLoader.importModule(module);
                        if(aModuleClass != null)
                        {
                            classes.add(aModuleClass);
                        }
                    }
                }
            }
            catch(IOException ex)
            {
                ex.printStackTrace();
            }
        }
        else
        {
            File dir = getModuleDir();

            // get the names of all the classes that are confirmed to be modules
            names = getModuleClasses(dir);
            
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

        DefaultMutableTreeNode add_modules = new DefaultMutableTreeNode(loadNodeString);

        // iterate over the class names and create a node for each
        Iterator iterator = classes.iterator();
        while(iterator.hasNext())
        {
            try
            {
                // create each ModuleClass node using an instantiation of the
                // ModuleClass
                addClassToTree((Class) iterator.next());
            }
            catch(Throwable e)
            {
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
    void removeModuleFromTree(DefaultMutableTreeNode newNode)
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


        public void mouseReleased(MouseEvent e)
        {
            if(e.isPopupTrigger())
            {
                showPopupMenu(e);
            }
        }


        public void mousePressed(MouseEvent e)
        {
            if(e.isPopupTrigger())
            {
                showPopupMenu(e);
            }
        }


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
//                        if(ApplicationSettings.getApplicationView().getCurrentPetriNetView() != null)
//                        {
//                            ((ModuleMethod) nodeObj).execute();
//                        }
                    }
                    else if(nodeObj == loadNodeString)
                    {
                        DefaultMutableTreeNode newNode;

                        //Create a file chooser
                        JFileChooser fc = new JFileChooser();
                        fc.setFileFilter(
                                new ExtensionFilter(Constants.PROPERTY_FILE_EXTENSION,
                                                    Constants.PROPERTY_FILE_DESC));
                        //In response to a button click:
                        int returnVal = fc.showOpenDialog(parent);
                        if(returnVal == JFileChooser.APPROVE_OPTION)
                        {
                            File moduleProp = fc.getSelectedFile();
                            Class newModuleClass = ModuleLoader.importModule(moduleProp);

                            if(newModuleClass != null)
                            {
                                addClassToTree(newModuleClass);
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
