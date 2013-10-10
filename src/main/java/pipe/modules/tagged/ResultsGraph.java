package pipe.modules.tagged;

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
import pipe.gui.widgets.ButtonBar;
import pipe.gui.widgets.FileBrowser;
import pipe.gui.widgets.GraphPanel;

/**
 * This class is based on the one found in pipe.gui.widgets but is customised
 * for the Passage time analysis usage 
 * 
 * @author Barry Kearns
 */
public class ResultsGraph extends JPanel
{

	private static final long serialVersionUID = 1L;

	private final GraphPanel graph;
  
	public ResultsGraph()
	{
		
		super(new BorderLayout());
		setBorder(new TitledBorder(new EtchedBorder(),"Results"));
    
		graph=new GraphPanel();
		graph.setPreferredSize(new Dimension(400,450));
		
		add(graph,BorderLayout.CENTER);

        ActionListener saveButtonClick = new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                String filename = null;
                if(ApplicationSettings.getApplicationView().getFile() != null)
                    filename = ApplicationSettings.getApplicationView().getFile() + " graph.png";
                filename = new FileBrowser("PNG image", "png", filename).saveFile();
                if(filename != null) try
                {
                    graph.setIgnoreRepaint(true);
                    Border b = graph.getBorder();
                    graph.setBorder(null);
                    Dimension d = graph.getPreferredSize(); // Export uses preferred size for exported file dimensions
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
        };
        add(new ButtonBar("Save", saveButtonClick),BorderLayout.PAGE_END);
  }

    public synchronized GraphPanel getGraph() {
    return graph;
  }
}
