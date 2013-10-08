/*
 * GraphPanelPane.java
 *
 * Created on 10-Mar-2004
 */
package pipe.gui.widgets;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import pipe.gui.ApplicationSettings;
import pipe.gui.Export;


/**
 * @author mjg103
 */
public class GraphPanelPane 
        extends JPanel {
   
   private final GraphPanel graph;
   
   
   public GraphPanelPane() {
      super(new BorderLayout());
      setBorder(new TitledBorder(new EtchedBorder(), "Results graph"));
      add(graph = new GraphPanel(), BorderLayout.CENTER);
       ActionListener saveButtonClick = new ActionListener()
       {
           public void actionPerformed(ActionEvent e)
           {
               String filename = null;
               if(ApplicationSettings.getApplicationView().getFile() != null)
               {
                   filename = ApplicationSettings.getApplicationView().getFile() + " DNAmaca output.png";
               }
               filename = new FileBrowser("PNG image", "png", filename).saveFile();
               if(filename != null)
               {
                   try
                   {
                       graph.setIgnoreRepaint(true);
                       Border b = graph.getBorder();
                       graph.setBorder(null);
                       // Export uses preferred size for exported file dimensions
                       Dimension d = graph.getPreferredSize();
                       graph.setSize(640, 480);
                       graph.setPreferredSize(graph.getSize());
                       Export.toPNG(graph, filename);
                       graph.setPreferredSize(d);
                       graph.setBorder(b);
                       graph.setIgnoreRepaint(false);
                   }
                   catch(IOException ex)
                   {
                   }
               }
           }
       };
       add(new ButtonBar("Save", saveButtonClick), BorderLayout.PAGE_END);
   }


    public synchronized GraphPanel getGraph() {
      return graph;
   }
   
}
