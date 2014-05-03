package pipe.gui.reachability;

import com.mxgraph.view.mxGraph;

import java.util.HashMap;
import java.util.Map;

/**
 * This class extends mxGraph in order to easily be able to set tool tips. There appears no better way to do this since
 * JavaX documentation is a bit sparse in this area.
 *
 * Java Swing 's tool tip tutorial suggests: "An alternative that works for all JComponents is creating a subclass of
 * the component and overriding its getToolTipText(MouseEvent) method."
 */
public class TooltipMXGraph extends mxGraph {

    /**
     * Map that maps an objects cell to its tooltip
     */
    private final Map<Object, String> tooltipText = new HashMap<>();

    @Override
    public String getToolTipForCell(Object cell) {
        return tooltipText.get(cell);
    }

    /**
     *
     * Depicts what text will be shown for the object
     *
     * @param cell
     * @param text
     */
    public void setTooltipText(Object cell, String text) {
        tooltipText.put(cell, text);
    }
}
