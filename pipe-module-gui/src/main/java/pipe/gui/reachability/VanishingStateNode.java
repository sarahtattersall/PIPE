package pipe.gui.reachability;

import net.sourceforge.jpowergraph.defaults.DefaultNode;
import net.sourceforge.jpowergraph.painters.node.ShapeNodePainter;
import net.sourceforge.jpowergraph.swtswinginteraction.color.JPowerGraphColor;

public class VanishingStateNode extends DefaultNode {
    private final String label;
    private String toolTip;

    private static final JPowerGraphColor BG_COLOR = new JPowerGraphColor(182, 220, 255);
    private static final JPowerGraphColor TEXT_COLOR = JPowerGraphColor.BLACK;
    private static final ShapeNodePainter SHAPE_NODE_PAINTER = new ShapeNodePainter(
            ShapeNodePainter.ELLIPSE, BG_COLOR, BG_COLOR, TEXT_COLOR);

    /**
     * Creates a new node instance.
     * @param label    the node id.
     * @param tooltip  the state tooltip text
     */
    VanishingStateNode(String label, String tooltip){
        this.label = label;
        this.toolTip = tooltip;
    }


    @Override
    public String getLabel() {
        return label;
    }


    @Override
    public String getNodeType(){
        return "Vanishing state";
    }


    public String getToolTip(){
        return toolTip;
    }


    public static ShapeNodePainter getShapeNodePainter(){
        return SHAPE_NODE_PAINTER;
    }
}
