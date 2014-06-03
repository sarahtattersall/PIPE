package pipe.constants;

import java.awt.*;

public interface GUIConstants {
   
   final String TOOL = "PIPE";
   final String VERSION = "2.5";
   
//  Filesystem Definitions
   final String PROPERTY_FILE_EXTENSION = ".properties";
   final String PROPERTY_FILE_DESC = "PIPE Properties file";
   final String CLASS_FILE_EXTENSION = ".class";
   final String CLASS_FILE_DESC = "Java Class File";   
//	File DEFAULT_DIRECTORY = new File("Petri-Nets");
//	String DEFAULT_FILENAME = "PetriNetViewComponent.xml";
   
//PetriNetViewComponent Object Type Definitions
   final int ANIMATE      = 98;
   final int RANDOM       = 99;
   final int START        = 100;
   final int FIRE         = 101;
   final int STEPFORWARD  = 102;
   final int STEPBACKWARD = 103;
   final int STOP         = 104;
   
   final int PLACE        = 105;
   final int IMMTRANS     = 106;
   final int TIMEDTRANS   = 114;
   final int ADDTOKEN     = 107;
   final int DELTOKEN     = 108;
   final int ANNOTATION   = 109;
   final int SELECT       = 110;
   final int DELETE       = 111;
   final int ARC          = 112;
   final int GRID         = 113;
   final int INHIBARC     = 116;
      
   final int DRAW         = 115;
   
   final int RATE         = 117;
   final int MARKING      = 118;
   
   final int DRAG         = 120;
   
   
   final int FAST_PLACE      = 150;
   final int FAST_TRANSITION = 151;
            
   // Special:  Parsing in a PNML file - creating components
   final int CREATING     = 200;   
   
   final int DEFAULT_ELEMENT_TYPE = SELECT;
   
   final int PLACE_TRANSITION_HEIGHT=30;
   
   final Color ENABLED_TRANSITION_COLOUR = new Color(192,0,0);
   final Color ELEMENT_LINE_COLOUR = Color.BLACK;
   final Color ELEMENT_FILL_COLOUR = Color.WHITE;
   final Color SELECTION_LINE_COLOUR = new Color(0,0,192);
   final Color SELECTION_FILL_COLOUR = new Color(192,192,255);
   
   // For ArcPath:
   final int ARC_CONTROL_POINT_CONSTANT = 3;
   final int ARC_PATH_SELECTION_WIDTH = 6;
   final int ARC_PATH_PROXIMITY_WIDTH = 10;
   
   // For Place/Transition Arc Snap-To behaviour:
   final int PLACE_TRANSITION_PROXIMITY_RADIUS = 25;
   
   // Object layer positions for PetriNetTab:
   final int WHITE_LAYER_OFFSET = 80;
   final int ARC_POINT_LAYER_OFFSET = 50;
   final int ARC_LAYER_OFFSET = 20;
   final int PLACE_TRANSITION_LAYER_OFFSET = 30;
   final int NOTE_LAYER_OFFSET = 10;
   final int SELECTION_LAYER_OFFSET = 90;
   final int LOWEST_LAYER_OFFSET = 0;
   final int ANNOTATION_LAYER_OFFSET = 10;
   
   // For AnnotationNote appearance:
   final int RESERVED_BORDER = 12;
   final int ANNOTATION_SIZE_OFFSET = 4;
   final int ANNOTATION_MIN_WIDTH = 40;
   final Color NOTE_DISABLED_COLOUR = Color.BLACK;
   final Color NOTE_EDITING_COLOUR = Color.BLACK;
   final Color RESIZE_POINT_DOWN_COLOUR = new Color(220,220,255);
   final String ANNOTATION_DEFAULT_FONT = "Helvetica";
   final int ANNOTATION_DEFAULT_FONT_SIZE = 12;
   
   
   final String LABEL_FONT = "Dialog";
   final int    LABEL_DEFAULT_FONT_SIZE = 10;
   
   
   final int DEFAULT_OFFSET_X = -5;
   final int DEFAULT_OFFSET_Y = 35;
   
      /** X-Axis Scale Value */
   final int DISPLAY_SCALE_FACTORX = 7; // Scale factors for loading other Petri-Nets (not yet implemented)
   /** Y-Axis Scale Value */
   final int DISPLAY_SCALE_FACTORY = 7; // Scale factors for loading other Petri-Nets (not yet implemented)
   /** X-Axis Shift Value */
   final int DISPLAY_SHIFT_FACTORX = 270; // Scale factors for loading other Petri-Nets (not yet implemented)
   /** Y-Axis Shift Value */
   final int DISPLAY_SHIFT_FACTORY = 120; // Scale factors for loading other Petri-Nets (not yet implemented)
   
   
   final int NAMELABEL_OFFSET = 12;

   int DEFAULT_BUFFER_SIZE = 50;
   
   boolean JOIN_ARCS = false;

   final int ZOOM_DELTA   = 10;
   final int ZOOM_MAX     = 300;
   final int ZOOM_MIN     = 40;
   final int ZOOM_DEFAULT = 100;

   Color BACKGROUND_COLOR = new Color(255, 255, 255, 200);
//REVISAR-LIMITS
   final int MAX_NODES = 10000;
   // TODO: find a better value for MAX_NODES
   
   //public static final int NUMBER_OF_BUTTONS = MouseInfo.getNumberOfButtons();
   
   
}
