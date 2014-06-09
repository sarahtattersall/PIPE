package pipe.constants;

import java.awt.Color;

/**
 * Utility class declaring constants for the GUI
 */
public final class GUIConstants {


    //  Filesystem Definitions
    public static final String PROPERTY_FILE_EXTENSION = ".properties";

    public static final String PROPERTY_FILE_DESC = "PIPE Properties file";

    /**
     * Integer for place action
     */
    @Deprecated
    public static final int PLACE = 105;

    /**
     * Integer for immediate transition action
     */
    @Deprecated
    public static final int IMMTRANS = 106;

    /**
     * Integer for timed transition action
     */
    @Deprecated
    public static final int TIMEDTRANS = 114;

    /**
     * Integer for add token action
     */
    @Deprecated
    public static final int ADDTOKEN = 107;

    /**
     * Integer for delete token action
     */
    @Deprecated
    public static final int DELTOKEN = 108;

    /**
     * Integer for adding annotations action
     */
    @Deprecated
    public static final int ANNOTATION = 109;

    /**
     * Integer for select action
     */
    @Deprecated
    public static final int SELECT = 110;

    /**
     * Integer for normal arc action
     */
    @Deprecated
    public static final int ARC = 112;

    /**
     * Integer for inhibitor action
     */
    @Deprecated
    public static final int INHIBARC = 116;

    /**
     * Integer for draw action
     */
    @Deprecated
    public static final int DRAW = 115;

    /**
     * Integer for rate action
     */
    @Deprecated
    public static final int RATE = 117;

    /**
     * Integer for marking action
     */
    @Deprecated
    public static final int MARKING = 118;

    /**
     * Integer for drag action
     */
    @Deprecated
    public static final int DRAG = 120;

    /**
     * Default place/transition height
     */
    @Deprecated
    public static final int PLACE_TRANSITION_HEIGHT = 30;

    /**
     * Enabled transition color
     */
    @Deprecated
    public static final Color ENABLED_TRANSITION_COLOUR = new Color(192, 0, 0);

    /**
     * Petri net component line color
     */
    public static final Color ELEMENT_LINE_COLOUR = Color.BLACK;

    /**
     * Petri net component fill color
     */
    public static final Color ELEMENT_FILL_COLOUR = Color.WHITE;

    /**
     * Selected Petri net component  line color
     */
    public static final Color SELECTION_LINE_COLOUR = new Color(0, 0, 192);

    /**
     * Selected Petri net component fill color
     */
    public static final Color SELECTION_FILL_COLOUR = new Color(192, 192, 255);

    /**
     * Arc path control point constant
     */
    @Deprecated
    public static final int ARC_CONTROL_POINT_CONSTANT = 3;

    /**
     * Arc path point width
     */
    @Deprecated
    public static final int ARC_PATH_SELECTION_WIDTH = 6;

    /**
     * Arc path point width
     */
    @Deprecated
    public static final int ARC_PATH_PROXIMITY_WIDTH = 10;

    /**
     * Selection layer offset for components on the canvas
     */
    @Deprecated
    public static final int SELECTION_LAYER_OFFSET = 90;

    /**
     * Lowest layer allowed for components on the canvas
     */
    @Deprecated
    public static final int LOWEST_LAYER_OFFSET = 0;


    // For AnnotationNote appearance:

    /**
     * Reserved border
     */
    @Deprecated
    public static final int RESERVED_BORDER = 12;

    /**
     * Annotation size offset
     */
    @Deprecated
    public static final int ANNOTATION_SIZE_OFFSET = 4;

    /**
     * Minimum annotation width
     */
    @Deprecated
    public static final int ANNOTATION_MIN_WIDTH = 40;

    /**
     * Disabled annotation color
     */
    @Deprecated
    public static final Color NOTE_DISABLED_COLOUR = Color.BLACK;

    /**
     * Annotation editing color
     */
    @Deprecated
    public static final Color NOTE_EDITING_COLOUR = Color.BLACK;

    /**
     * Resize point down color
     */
    @Deprecated
    public static final Color RESIZE_POINT_DOWN_COLOUR = new Color(220, 220, 255);

    /**
     * Annotation default font (helvetica)
     */
    @Deprecated
    public static final String ANNOTATION_DEFAULT_FONT = "Helvetica";

    /**
     * Annotation font size
     */
    @Deprecated
    public static final int ANNOTATION_DEFAULT_FONT_SIZE = 12;

    /**
     * Label default font size
     */
    @Deprecated
    public static final int LABEL_DEFAULT_FONT_SIZE = 10;

    /**
     * Default name label offset
     */
    @Deprecated
    public static final int NAMELABEL_OFFSET = 12;

    /**
     * Amount to zoom in and out by
     */
    @Deprecated
    public static final int ZOOM_DELTA = 10;

    /**
     * Maximum allowed zoom
     */
    @Deprecated
    public static final int ZOOM_MAX = 300;

    /**
     * Minimum allowed zoom
     */
    @Deprecated
    public static final int ZOOM_MIN = 40;

    /**
     * Canvas background color
     */
    @Deprecated
    public static final Color BACKGROUND_COLOR = new Color(255, 255, 255, 200);

    /**
     * Empty constructor for utility class
     */
    private GUIConstants() {
    }

}
