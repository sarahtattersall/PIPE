package pipe.actions;

import pipe.gui.ApplicationSettings;
import pipe.io.JarUtilities;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.jar.JarEntry;

public class ExampleFileAction extends GuiAction
{
    private final File filename;

    public ExampleFileAction(File file, String keyStroke)
    {
        super(file.getName(), "Open example file \"" + file.getName() + "\"", keyStroke);
        filename = file;
        putValue(SMALL_ICON, new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(ApplicationSettings.getImagePath() + "Net.png")));
    }

    public ExampleFileAction(JarEntry entry, String keyStroke)
    {
        super(entry.getName().substring(1 + entry.getName().indexOf(System.getProperty("file.separator"))), "Open example file \"" + entry.getName() + "\"", keyStroke);
        filename = JarUtilities.getFile(entry);
        putValue(SMALL_ICON, new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(ApplicationSettings.getImagePath() + "Net.png")));
    }

    public void actionPerformed(ActionEvent e)
    {
        ApplicationSettings.getApplicationView().createNewTab(filename, false);
    }

}
