package pipe.gui.widgets;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

/**
 * Class that handles the creation of a new JFrame to display the GraphPanel widget and
 * a save button
 *
 * @author Oliver Haggarty - August 2007
 *
 */
class CartesianGraphFrame
        extends JFrame {
   
   private final Clipboard clipboard=this.getToolkit().getSystemClipboard();
    private ArrayList<Double> xlist, ylist;
   
   /**
    * Creates the GraphPanel and initialises with xlist and ylist. Create the save button.
    * @param xlist Data for x axis
    * @param ylist Data for y axis
    */
   public void constructCartesianGraphFrame(ArrayList<Double> xlist, ArrayList<Double> ylist) {
      
      //this.setIconImage(new ImageIcon(Pipe._imgPath + "icon.png").getImage());
      
      setSize(600,600);
      setLocation(100,100);
      
      addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent wev) {
            Window w = wev.getWindow();
            w.setVisible(false);
            w.dispose();
         }
      });
      
      this.xlist = xlist;
      this.ylist = ylist;
      Container contentPane = this.getContentPane();
      contentPane.setLayout(new BoxLayout(contentPane,BoxLayout.PAGE_AXIS));
       GraphPanel graph = new GraphPanel(xlist, ylist);
      add(graph);
      this.add(new ButtonBar(new String[]{"Save as CSV"},
              new ActionListener[]{SaveHandler}),BorderLayout.PAGE_END);
      setVisible(true);
   }
   
   
   /**
    * Not called at the moment
    */
   private ActionListener CopyHandler=new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
         StringSelection data=new StringSelection(xlist.toString());
         try {
            clipboard.setContents(data,data);
         } catch (IllegalStateException e) {
            System.out.println("Error copying to clipboard, seems it's busy?");
         }
      }
   };
   
   
   /**
    * Code for when the save button is pressed. Converts the data to a Comma 
    * Seperated Variable file of format t value, probability, etc. 
    * Creates JFileChooser so user can select file name
    */
   private final ActionListener SaveHandler=new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
         FileWriter fw = null;
         StringBuffer content = new StringBuffer();
         
         File saveFile;
         
         JFileChooser fc = new JFileChooser();
         int returnVal = fc.showSaveDialog(CartesianGraphFrame.this);
         if (returnVal == JFileChooser.APPROVE_OPTION) {
            saveFile = fc.getSelectedFile();
         } else {
            return;
         }
         
         try {
            fw = new FileWriter(saveFile);
         } catch (IOException e) {
            e.printStackTrace();
         }
         for (int i = 0; i < xlist.size(); i++) {
            content.append(xlist.get(i));
            content.append(",");
            content.append(ylist.get(i));
            content.append(",\n");
         }
         String content1 = content.toString();
         
         try {
            fw.write(content1);
            fw.close();
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   };
   
}
