package pipe.modules.clientCommon;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;



public class HTMLPane extends JPanel
{
  
	private static final long serialVersionUID = 1L;
	private final JEditorPane results;

  
	public HTMLPane(String title)
	{
		super(new BorderLayout());
	
	    results=new JEditorPane();
	    results.setEditable(false);
	  
	    results.setContentType("text/html");
	
	    JScrollPane scroller=new JScrollPane(results);
	    scroller.setPreferredSize(new Dimension(400,450));
	    scroller.setBorder(new BevelBorder(BevelBorder.LOWERED));
	    this.add(scroller);	    
	    this.setBorder(new TitledBorder(new EtchedBorder(),title));
  }
  
  public void setText(String text)
  {
    results.setText("<html><head><style type=\"text/css\">" +
        "body{font-family:Arial,Helvetica,sans-serif;text-align:center;background:#ffffff}" +
        "td.colhead{font-weight:bold;text-align:center;background:#ffffff}" +
        "td.rowhead{font-weight:bold;background:#ffffff}"+
        "td.cell{text-align:center;padding:5px,0}" +
        "tr.even{background:#a0a0d0}" +
        "tr.odd{background:#c0c0f0}" +
        "td.empty{background:#ffffff}" +
        "</style></head><body>"+text+"</body></html>");
    results.setCaretPosition(0); // scroll to top
  }
  
  public String getText(){
  	return results.getText();
  }
 
} 

