package pipe.constants;

import java.awt.*;

public interface GUIConstants {
   
   String TOOL = "PIPE";
   String VERSION = "2.5";
   
//  Filesystem Definitions
   String PROPERTY_FILE_EXTENSION = ".properties";
   String PROPERTY_FILE_DESC = "PIPE Properties file";
   String CLASS_FILE_EXTENSION = ".class";
   String CLASS_FILE_DESC = "Java Class File";
//	File DEFAULT_DIRECTORY = new File("Petri-Nets");
//	String DEFAULT_FILENAME = "PetriNetViewComponent.xml";
   
//PetriNetViewComponent Object Type Definitions
   int ANIMATE      = 98;
   int RANDOM       = 99;
   int START        = 100;
   int FIRE         = 101;
   int STEPFORWARD  = 102;
   int STEPBACKWARD = 103;
   int STOP         = 104;

   int PLACE        = 105;
   int IMMTRANS     = 106;
   int TIMEDTRANS   = 114;
   int ADDTOKEN     = 107;
   int DELTOKEN     = 108;
   int ANNOTATION   = 109;
   int SELECT       = 110;
   int DELETE       = 111;
   int ARC          = 112;
   int GRID         = 113;
   int INHIBARC     = 116;
      
   int DRAW         = 115;
   
   int RATE         = 117;
   int MARKING      = 118;
   
   int DRAG         = 120;
   
   
   int FAST_PLACE      = 150;
   int FAST_TRANSITION = 151;
            
   // Special:  Parsing in a PNML file - creating components
   int CREATING     = 200;
   
   int DEFAULT_ELEMENT_TYPE = SELECT;
   
   int PLACE_TRANSITION_HEIGHT=30;
   
   Color ENABLED_TRANSITION_COLOUR = new Color(192,0,0);
   Color ELEMENT_LINE_COLOUR = Color.BLACK;
   Color ELEMENT_FILL_COLOUR = Color.WHITE;
   Color SELECTION_LINE_COLOUR = new Color(0,0,192);
   Color SELECTION_FILL_COLOUR = new Color(192,192,255);
   
   // For ArcPath:
   int ARC_CONTROL_POINT_CONSTANT = 3;
   int ARC_PATH_SELECTION_WIDTH = 6;
   int ARC_PATH_PROXIMITY_WIDTH = 10;
   
   // For Place/Transition Arc Snap-To behaviour:
   int PLACE_TRANSITION_PROXIMITY_RADIUS = 25;
   
   // Object layer positions for PetriNetTab:
   int WHITE_LAYER_OFFSET = 80;
   int ARC_POINT_LAYER_OFFSET = 50;
   int ARC_LAYER_OFFSET = 20;
   int PLACE_TRANSITION_LAYER_OFFSET = 30;
   int NOTE_LAYER_OFFSET = 10;
   int SELECTION_LAYER_OFFSET = 90;
   int LOWEST_LAYER_OFFSET = 0;
   int ANNOTATION_LAYER_OFFSET = 10;
   
   // For AnnotationNote appearance:
   int RESERVED_BORDER = 12;
   int ANNOTATION_SIZE_OFFSET = 4;
   int ANNOTATION_MIN_WIDTH = 40;
   Color NOTE_DISABLED_COLOUR = Color.BLACK;
   Color NOTE_EDITING_COLOUR = Color.BLACK;
   Color RESIZE_POINT_DOWN_COLOUR = new Color(220,220,255);
   String ANNOTATION_DEFAULT_FONT = "Helvetica";
   int ANNOTATION_DEFAULT_FONT_SIZE = 12;
   
   
   String LABEL_FONT = "Dialog";
   int    LABEL_DEFAULT_FONT_SIZE = 10;
   
   
   int DEFAULT_OFFSET_X = -5;
   int DEFAULT_OFFSET_Y = 35;
   
      /** X-Axis Scale Value */
   int DISPLAY_SCALE_FACTORX = 7; // Scale factors for loading other Petri-Nets (not yet implemented)
   /** Y-Axis Scale Value */
   int DISPLAY_SCALE_FACTORY = 7; // Scale factors for loading other Petri-Nets (not yet implemented)
   /** X-Axis Shift Value */
   int DISPLAY_SHIFT_FACTORX = 270; // Scale factors for loading other Petri-Nets (not yet implemented)
   /** Y-Axis Shift Value */
   int DISPLAY_SHIFT_FACTORY = 120; // Scale factors for loading other Petri-Nets (not yet implemented)
   
   
   int NAMELABEL_OFFSET = 12;

   int DEFAULT_BUFFER_SIZE = 50;
   
   boolean JOIN_ARCS = false;

   int ZOOM_DELTA   = 10;
   int ZOOM_MAX     = 300;
   int ZOOM_MIN     = 40;
   int ZOOM_DEFAULT = 100;

   Color BACKGROUND_COLOR = new Color(255, 255, 255, 200);
//REVISAR-LIMITS
   final int MAX_NODES = 10000;
   // TODO: find a better value for MAX_NODES
   
   //public static final int NUMBER_OF_BUTTONS = MouseInfo.getNumberOfButtons();
   
   
}
