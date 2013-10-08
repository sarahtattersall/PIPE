/**
 * QueryStatusBar
 * 
 * - prints status messages to guide users on what to do
 * 
 * @author Tamas Suto
 * @date 15/05/07
 */

package pipe.modules.queryeditor.gui;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import pipe.modules.interfaces.QueryConstants;


public class QueryStatusBar extends JPanel implements QueryConstants {

    private final JLabel label;

  public QueryStatusBar(){
    super();
    label = new JLabel(" ");
    this.setLayout(new BorderLayout(0,0));
    this.add(label);
  }

  void changeText(String newText){
    label.setText(newText);
  }

  public void changeText(int type ){
    switch(type){
      case DRAW:
          String textforDrawing = "Drawing Mode: Click on a button on the Query Builder on the left to start drawing components";
          changeText(textforDrawing);
        break;
      case SELECT:
          String textforMove = "Select Mode: Click/Drag to select objects; drag to move them";
          changeText(textforMove);
          break;
      case NODE:
          String textforNode = "Node Mode: Right click on a node to see menu options";
          changeText(textforNode);
        break;
      case ARC:
          String textforArc = "Arc Mode: Right-Click on an Arc to add weighting";
          changeText(textforArc);
        break;      
      default:
        break;
    }
  }
  
}
