/*
 * Created on 07-Mar-2004
 */
package pipe.gui;

import pipe.actions.GuiAction;
import pipe.gui.widgets.ButtonBar;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;


/**
 * @author Maxim
 */
public class HelpBox
        extends GuiAction
        implements HyperlinkListener
{

    private JFrame dialog;
    private JEditorPane content;
    private final LinkedList history = new LinkedList();
    private final String filename;


    public HelpBox(String name, String tooltip, String keystroke, String filename)
    {
        super(name, tooltip, keystroke);
        this.filename = filename;
    }


    /**
     * Sets the page to the given non-absolute filename assumed to be in the
     * Docs directory
     * @param filename
     */
    private void setPage(String filename)
    {
        if(dialog == null)
        {
            dialog = new JFrame("PIPE help");
            Container contentPane = dialog.getContentPane();
            contentPane.setLayout(new BorderLayout(5, 5));

            content = new JEditorPane();
            content.setEditable(false);
            content.setMargin(new Insets(5, 5, 5, 5));
            content.setContentType("text/html");
            content.addHyperlinkListener(this);
            JScrollPane scroller = new JScrollPane(content);
            scroller.setBorder(new BevelBorder(BevelBorder.LOWERED));

            dialog.setIconImage(((ImageIcon) this.getValue(SMALL_ICON)).getImage());

            scroller.setPreferredSize(new Dimension(400, 400));
            contentPane.add(scroller, BorderLayout.CENTER);
            contentPane.add(new ButtonBar(new String[]{"Index", "Back"}, new ActionListener[]{this, this}), BorderLayout.PAGE_START);

            dialog.pack();
        }
        dialog.setLocationRelativeTo(ApplicationSettings.getApplicationView());
        dialog.setVisible(true);
        try
        {
            String urlString = "file:///" + (new File("documentation" + System.getProperty("file.separator") + filename).getAbsolutePath());
            setPage(new URL(urlString), true);
        }
        catch(MalformedURLException e)
        {
            System.err.println(e.getMessage());
            System.err.println("Error setting page to " + filename);
        }
    }


    private void setPage(URL url, boolean addHistory)
    {

        try
        {
            content.setPage(url);
            if(addHistory)
            {
                history.add(url);
            }
        }
        catch(IOException e)
        {
            System.err.println("Error setting page to " + url);
        }
    }


    public void actionPerformed(ActionEvent e)
    {
        String s = e.getActionCommand();

        if(s!= null && (s.equals("Back")) && (history.size() > 1))
        {
            history.removeLast();
            setPage((URL) (history.getLast()), false);
        }
        else
        {
            // default and index
            setPage(filename);
        }
    }


    public void hyperlinkUpdate(HyperlinkEvent e)
    {
        if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
        {
            setPage(e.getURL(), true);
        }
    }

}
