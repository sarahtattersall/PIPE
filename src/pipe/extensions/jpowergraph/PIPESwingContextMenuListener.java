/*
 * PIPESwingContextMenuListener.java
 */

package pipe.extensions.jpowergraph;

import javax.swing.JPopupMenu;

import net.sourceforge.jpowergraph.Edge;
import net.sourceforge.jpowergraph.Graph;
import net.sourceforge.jpowergraph.Node;
import net.sourceforge.jpowergraph.lens.LensSet;
import net.sourceforge.jpowergraph.swing.manipulator.DefaultSwingContextMenuListener;


//REMARK: this class extends a jpowergraph's class which is LGPL


/**
 * This class prevents from showing context menus in order to make the graph 
 * "uneditable" (there's no "delete node" or "delete edge")
 * @author Pere Bonet
 */
public class PIPESwingContextMenuListener 
        extends DefaultSwingContextMenuListener {
   
   /** Creates a new instance of NewClass
    * @param theGraph
    * @param theLensSet
    * @param theZoomLevels
    * @param theRotateAngles*/
   public PIPESwingContextMenuListener(Graph theGraph, LensSet theLensSet, 
           Integer[] theZoomLevels, Integer[] theRotateAngles) {
      super(theGraph, theLensSet, theZoomLevels, theRotateAngles);
   }
   
   
   public void fillNodeContextMenu(final Node theNode, JPopupMenu theMenu) {
       //
   }
   
   
   public void fillEdgeContextMenu(final Edge theEdge, JPopupMenu theMenu) {
   }
   
}
