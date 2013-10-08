/*
 * PIPESwingToolTipListener.java
 */

package pipe.extensions.jpowergraph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JEditorPane;

import net.sourceforge.jpowergraph.Edge;
import net.sourceforge.jpowergraph.Node;
import net.sourceforge.jpowergraph.defaults.TextEdge;
import net.sourceforge.jpowergraph.swing.manipulator.DefaultSwingToolTipListener;


//REMARK: this class extends a jpowergraph's class which is LGPL

/**
 * This class displays information about a node in its tooltip
 * @author Pere Bonet
 */
public class PIPESwingToolTipListener 
        extends DefaultSwingToolTipListener {
   
   public boolean addNodeToolTipItems(Node theNode, JComponent theJComponent,
           Color backgroundColor) {
      Edge edge;
      
      // list of outgoing edges
      String sTo = "";
      Iterator <Edge> edgesFrom = theNode.getEdgesFrom().iterator();
      boolean duplicate = false;
      while (edgesFrom.hasNext()){
         edge = edgesFrom.next();
         if (edge instanceof TextEdge) {
            sTo += edge.getTo().getLabel() + " (" +
                    ((TextEdge)edge).getText() + ");  ";
         } else if (edge instanceof PIPELoopWithTextEdge) {
            // without this nasty trick, the info of loop edges appears twice
            if (!duplicate){
               sTo += edge.getTo().getLabel() + " (" +
                       ((PIPELoopWithTextEdge)edge).getText() + ");  ";
               duplicate = true;
            }
         } else {
            sTo += edge.getTo().getLabel() + ";  ";
         }
      }
      if (sTo.length() > 3) {
         sTo = sTo.substring(0, sTo.length() - 3);
      } else {
         sTo = "-";
      }
      
      // list of incoming edges
      String sFrom = "";
      Iterator <Edge> edgesTo = theNode.getEdgesTo().iterator();
      while (edgesTo.hasNext()){
         edge = edgesTo.next();
         if (edge instanceof TextEdge){
            sFrom += edge.getFrom().getLabel() + " (" +
                    ((TextEdge)edge).getText() + ");  ";
         } else if (edge instanceof PIPELoopWithTextEdge){
            sFrom += edge.getFrom().getLabel() + " (" +
                    ((PIPELoopWithTextEdge)edge).getText() + ");  ";
         } else {
            sFrom += edge.getFrom().getLabel() + ";  ";
         }
      }
      if (sFrom.length() > 3) {
         sFrom = sFrom.substring(0, sFrom.length() - 3);
      } else {
         sFrom = "-";
      }
      
      // the marking of the state
      String marking = "";
      if (theNode instanceof PIPENode) {
         marking = ((PIPENode)theNode).getMarking();
      }
      
      theJComponent.setLayout(new BorderLayout());
      JEditorPane editor = new JEditorPane("text/html", "<font size=3><b>" +
              theNode.getLabel().replaceAll("\n", "<br>") +
              "</b> [" + theNode.getNodeType() + "]" +
              "</font><hr size=1><font size=3><b>Marking: </b>" + marking +
              "</font><hr size=1><font size=3><b>Edges From: </b>" + sFrom +
              "</font><hr size=1><font size=3><b>Edges To: </b>" + sTo +
              "</font>");
      editor.setBackground(new Color(255, 255, 204));
      editor.setEditable(false);
      theJComponent.add(editor);
      
      return true;
   }
   
}
