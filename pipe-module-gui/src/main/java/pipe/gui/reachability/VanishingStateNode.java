package pipe.gui.reachability;

import net.sourceforge.jpowergraph.defaults.DefaultNode;
import net.sourceforge.jpowergraph.painters.node.ShapeNodePainter;
import net.sourceforge.jpowergraph.swtswinginteraction.color.JPowerGraphColor;

/**
 * Node class used to represent a vanishing state when displaying the reachability
 * graph.
 *
 * Vanishing states are displayed in light blue
 */
public class VanishingStateNode extends DefaultNode {
    /**
     * Label to appear in the node
     */
    private final String label;

    /**
     * Tool tip when hovering over the node
     */
    private String toolTip;

    /**
     * Background color
     */
    private static final JPowerGraphColor BG_COLOR = new JPowerGraphColor(182, 220, 255);

    /**
     * Text color
     */
    private static final JPowerGraphColor TEXT_COLOR = JPowerGraphColor.BLACK;

    /**
     * Shape
     */
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


    /**
     *
     * @return node label
     */
    @Override
    public String getLabel() {
        return label;
    }


    /**
     *
     * @return vanishing state
     */
    @Override
    public String getNodeType(){
        return "Vanishing state";
    }


    /**
     *
     * @return tooltip message
     */
    public String getToolTip(){
        return toolTip;
    }


    /**
     *
     * @return node painter
     */
    public static ShapeNodePainter getShapeNodePainter(){
        return SHAPE_NODE_PAINTER;
    }
}
