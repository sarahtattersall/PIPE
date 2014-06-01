package pipe.gui.reachability;

import net.sourceforge.jpowergraph.Edge;
import net.sourceforge.jpowergraph.Node;

/**
 * Creates an edge with with text capabilities
 * <p/>
 * To be used instead of {@link net.sourceforge.jpowergraph.defaults.DefaultEdge} and {@link net.sourceforge.jpowergraph.defaults.TextEdge}
 * because they consider two edges equal if:
 * from1 = from2 & to1 = to2 OR
 * from1 = to2 & to1 = from2
 * <p/>
 * The second behaviour is not correct in directed edges
 */
public class DirectedTextEdge implements Edge {
    private final String text;

    /**
     * The node from the edge.
     */
    protected Node from;

    /**
     * The node to the edge.
     */
    protected Node to;

    /**
     * Creates an instance of this class.
     *
     * @param from the node from
     * @param to   the node to
     * @param text the edge text
     */
    public DirectedTextEdge(Node from, Node to, String text) {
        this.from = from;
        this.to = to;
        this.text = text;
    }

    /**
     * Returns the node from which this edge points.
     *
     * @return the node from which this edge points
     */
    @Override
    public Node getFrom() {
        return from;
    }

    /**
     * Returns the node from which to edge points.
     *
     * @return the node to which this edge points
     */
    @Override
    public Node getTo() {
        return to;
    }

    /**
     * Sets the node from which to edge points.
     *
     * @param to node to which this edge points
     */
    public void setTo(Node to) {
        this.to = to;
    }

    /**
     * Returns the label of this node.
     *
     * @return the label of the node
     */
    @Override
    public String getLabels() {
        return toString();
    }

    /**
     * Returns the length of this edge.
     *
     * @return the ledge of the edge
     */
    @Override
    public double getLength() {
        return 40;
    }

    /**
     * Sets the node from which this edge points.
     *
     * @param from node from which this edge points
     */
    public void setFrom(Node from) {
        this.from = from;
    }

    public String getText() {
        return text;
    }
}
