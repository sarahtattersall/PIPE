package pipe.gui;

import pipe.constants.GUIConstants;

import javax.swing.*;

import java.awt.BorderLayout;

/**
 * Status Bar to let users know what to do
 * */
@SuppressWarnings("serial")
public class StatusBar extends JPanel {

    /**
     *  Provides the appropriate text for the mode that the user is in
     */
    public static final String TEXT_FOR_DRAWING =
            "Drawing Mode: Click on a button to start adding components to the " + "Editor";

    /**
     * Provides the text for animation
     */
    public static final String TEXT_FOR_ANIMATION =
            "Animation Mode: Red transitions are enabled, click a transition to " + "fire it";

    /**
     * Label on which text is displayed
     */
    private final JLabel label;


    /**
     * Default constructor, performs the set up
     */
    public StatusBar() {
        super();
        label = new JLabel(TEXT_FOR_DRAWING);
        this.setLayout(new BorderLayout(0, 0));
        this.add(label);
    }

    /**
     * Deprecated
     * @param type of change 
     */
    @Deprecated
    public void changeText(int type) {
        switch (type) {
            case GUIConstants.PLACE:
                String textforPlace = "Place Mode: Right click on a Place to see menu options "
                        + "[Mouse wheel -> marking; Shift + Mouse wheel -> capacity]";
                changeText(textforPlace);
                break;

            case GUIConstants.IMMTRANS:
                String textforTrans = "Immediate Transition Mode: Right click on a Transition to see menu "
                        + "options [Mouse wheel -> rotate]";
                changeText(textforTrans);
                break;

            case GUIConstants.TIMEDTRANS:
                String textforTimedTrans = "Timed Transition Mode: Right click on a Transition to see menu "
                        + "options [Mouse wheel -> rotate]";
                changeText(textforTimedTrans);
                break;

            case GUIConstants.ARC:
                String textforArc = "Arc Mode: Right-Click on an Arc to see menu options " + "[Mouse wheel -> weight]";
                changeText(textforArc);
                break;

            case GUIConstants.INHIBARC:
                String textforInhibArc =
                        "Inhibitor Mode: Right-Click on an Arc to see menu options " + "[Mouse wheel -> weight]";
                changeText(textforInhibArc);
                break;

            case GUIConstants.ADDTOKEN:
                String textforAddtoken = "Add Token Mode: Click on a Place to add a Token";
                changeText(textforAddtoken);
                break;

            case GUIConstants.DELTOKEN:
                String textforDeltoken = "Delete Token Mode: Click on a Place to delete a Token ";
                changeText(textforDeltoken);
                break;

            case GUIConstants.SELECT:
                String textforMove = "Select Mode: Click/drag to select objects; drag to move them";
                changeText(textforMove);
                break;

            case GUIConstants.DRAW:
                changeText(TEXT_FOR_DRAWING);
                break;

            case GUIConstants.ANNOTATION:
                String textforAnnotation =
                        "Annotation Mode: Right-Click on an Annotation to see menu options; " + "Double click to edit";
                changeText(textforAnnotation);
                break;

            case GUIConstants.DRAG:
                String textforDrag = "Drag Mode";
                changeText(textforDrag);
                break;

            case GUIConstants.MARKING:
                String textforMarking = "Add a marking parameter";
                changeText(textforMarking);
                break;

            case GUIConstants.RATE:
                String textforRate = "Add a rate parameter";
                changeText(textforRate);
                break;

            default:
                changeText("To-do (textfor" + type);
                break;
        }
    }

    /**
     * Change the text on the status bar
     * @param newText new text 
     */
    public void changeText(String newText) {
        label.setText(newText);
    }

}