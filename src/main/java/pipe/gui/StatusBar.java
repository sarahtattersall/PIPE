package pipe.gui;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

/* Status Bar to let users know what to do*/
public class StatusBar 
        extends JPanel{
   
   /* Provides the appropriate text for the mode that the user is in */
   public final String textforDrawing =
           "Drawing Mode: Click on a button to start adding components to the "
           + "Editor";
    public final String textforAnimation =
           "Animation Mode: Red transitions are enabled, click a transition to "
           + "fire it";

    private final JLabel label;
   
   
   public StatusBar(){
      super();
      label = new JLabel(textforDrawing); // got to put something in there
      this.setLayout(new BorderLayout(0,0));
      this.add(label);
   }
   
   
   public void changeText(String newText){
      label.setText(newText);
   }
   
   
   public void changeText(int type ){
      switch(type){
         case Constants.PLACE:
             String textforPlace = "Place Mode: Right click on a Place to see menu options " +
                     "[Mouse wheel -> marking; Shift + Mouse wheel -> capacity]";
             changeText(textforPlace);
            break;
            
         case Constants.IMMTRANS:
             String textforTrans = "Immediate Transition Mode: Right click on a Transition to see menu " +
                     "options [Mouse wheel -> rotate]";
             changeText(textforTrans);
            break;
            
         case Constants.TIMEDTRANS:
             String textforTimedTrans = "Timed Transition Mode: Right click on a Transition to see menu " +
                     "options [Mouse wheel -> rotate]";
             changeText(textforTimedTrans);
            break;
            
         case Constants.ARC:
             String textforArc = "Arc Mode: Right-Click on an Arc to see menu options " +
                     "[Mouse wheel -> weight]";
             changeText(textforArc);
            break;
            
         case Constants.INHIBARC:
             String textforInhibArc = "Inhibitor Mode: Right-Click on an Arc to see menu options " +
                     "[Mouse wheel -> weight]";
             changeText(textforInhibArc);
            break;            
            
         case Constants.ADDTOKEN:
             String textforAddtoken = "Add Token Mode: Click on a Place to add a Token";
             changeText(textforAddtoken);
            break;
            
         case Constants.DELTOKEN:
             String textforDeltoken = "Delete Token Mode: Click on a Place to delete a Token ";
             changeText(textforDeltoken);
            break;
            
         case Constants.SELECT:
             String textforMove = "Select Mode: Click/drag to select objects; drag to move them";
             changeText(textforMove);
            break;
            
         case Constants.DRAW:
            changeText(textforDrawing);
            break;
            
         case Constants.ANNOTATION:
             String textforAnnotation = "Annotation Mode: Right-Click on an Annotation to see menu options; " +
                     "Double click to edit";
             changeText(textforAnnotation);
            break;
            
         case Constants.DRAG:
             String textforDrag = "Drag Mode";
             changeText(textforDrag);
            break;            
            
         case Constants.MARKING:
             String textforMarking = "Add a marking parameter";
             changeText(textforMarking);
            break;

         case Constants.RATE:
             String textforRate = "Add a rate parameter";
             changeText(textforRate);
            break;            
            
         default:
            changeText("To-do (textfor" + type);
            break;
      }
   }

}