package pipe.actions.gui;

import pipe.actions.gui.GuiAction;
import pipe.gui.ApplicationSettings;
import pipe.io.JarUtilities;
import pipe.parsers.UnparsableException;
import pipe.utilities.gui.GuiUtils;
import pipe.views.PipeApplicationView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.jar.JarEntry;

public class ExampleFileAction extends GuiAction
{
    private final File filename;
    private final PipeApplicationView applicationView;

    public ExampleFileAction(File file, PipeApplicationView applicationView)
    {
        super(file.getName(), "Open example file \"" + file.getName() + "\"");
        filename = file;
        this.applicationView = applicationView;
        putValue(SMALL_ICON, new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(ApplicationSettings.getImagePath() + "Net.png")));
    }

    public ExampleFileAction(JarEntry entry, PipeApplicationView applicationView)
    {
        super(entry.getName().substring(1 + entry.getName().indexOf(System.getProperty("file.separator"))), "Open example file \"" + entry.getName() + "\"");
        this.applicationView = applicationView;
        filename = JarUtilities.getFile(entry);
        putValue(SMALL_ICON, new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(ApplicationSettings.getImagePath() + "Net.png")));
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        try {
            ApplicationSettings.getApplicationController().createNewTabFromFile(
                    filename, applicationView);
        } catch (UnparsableException e1) {
            GuiUtils.displayErrorMessage(applicationView, e1.getMessage());
        }
    }

}
