/*
 * PIPESwingToolTipListener.java
 */

package pipe.gui.reachability;

import net.sourceforge.jpowergraph.Node;
import net.sourceforge.jpowergraph.swing.manipulator.DefaultSwingToolTipListener;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;

/**
 * This class displays information about a node in its tooltip
 * @author Pere Bonet
 */
public class PIPESwingToolTipListener 
        extends DefaultSwingToolTipListener {
   
   @Override
   public boolean addNodeToolTipItems(Node node, JComponent jComponent,
           Color backgroundColor) {
//      // list of outgoing edges
//      String sTo = "";
//      Iterator <Edge> edgesFrom = node.getEdgesFrom().iterator();
//      boolean duplicate = false;
//      while (edgesFrom.hasNext()){
//         edge = edgesFrom.next();
//         if (edge instanceof TextEdge) {
//            sTo += edge.getTo().getLabel() + " (" +
//                    ((TextEdge)edge).getText() + ");  ";
//         } else if (edge instanceof PIPELoopWithTextEdge) {
//            // without this nasty trick, the info of loop edges appears twice
//            if (!duplicate){
//               sTo += edge.getTo().getLabel() + " (" +
//                       ((PIPELoopWithTextEdge)edge).getText() + ");  ";
//               duplicate = true;
//            }
//         } else {
//            sTo += edge.getTo().getLabel() + ";  ";
//         }
//      }
//      if (sTo.length() > 3) {
//         sTo = sTo.substring(0, sTo.length() - 3);
//      } else {
//         sTo = "-";
//      }
//
//      // list of incoming edges
//      String sFrom = "";
//      Iterator <Edge> edgesTo = node.getEdgesTo().iterator();
//      while (edgesTo.hasNext()){
//         edge = edgesTo.next();
//         if (edge instanceof TextEdge){
//            sFrom += edge.getFrom().getLabel() + " (" +
//                    ((TextEdge)edge).getText() + ");  ";
//         } else if (edge instanceof PIPELoopWithTextEdge){
//            sFrom += edge.getFrom().getLabel() + " (" +
//                    ((PIPELoopWithTextEdge)edge).getText() + ");  ";
//         } else {
//            sFrom += edge.getFrom().getLabel() + ";  ";
//         }
//      }
//      if (sFrom.length() > 3) {
//         sFrom = sFrom.substring(0, sFrom.length() - 3);
//      } else {
//         sFrom = "-";
//      }
      
      // the marking of the state
      String marking = "";
      if (node instanceof TangibleStateNode) {
         marking = ((TangibleStateNode)node).getToolTip();
      }
      
      jComponent.setLayout(new BorderLayout());
      JEditorPane editor = new JEditorPane("text/html", "<font size=3><b>" +
              node.getLabel().replaceAll("\n", "<br>") +
              "</b> [" + node.getNodeType() + "]" +
              "</font><hr size=1><font size=3><b>Marking: </b>" + marking +
//              "</font><hr size=1><font size=3><b>Edges From: </b>" + sFrom +
//              "</font><hr size=1><font size=3><b>Edges To: </b>" + sTo +
              "</font>");
      editor.setBackground(new Color(255, 255, 204));
      editor.setEditable(false);
      jComponent.add(editor);
      
      return true;
   }
   
}
