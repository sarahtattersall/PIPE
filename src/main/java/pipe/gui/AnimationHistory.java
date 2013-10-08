package pipe.gui;

import java.awt.Color;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;


/** Class to represent the history of the net animation
 *
 * @author Pere Bonet changed and added a number of new functions
 * to fix the unexpected behaviour observed during animation playback.
 * Previously, under certain circumstances, it is possible to step back to state(s)
 * before the initial state and step forward to state(s) after the last recorded
 * transitions in the animation history. 
 * These actions are no longer allowed after the fix.
 * */
public class AnimationHistory
        extends JTextPane {
   
   /** Holds all the transitions in the sequence */
   private Vector fSeq;
   private final String initText;
   private final Document doc;
   private Style emph;
   private Style bold;
   private Style regular;
   private int currentItem;
   
   
   public AnimationHistory(String text) throws 
           javax.swing.text.BadLocationException {
      super();
      initText = text;
      initStyles();
      doc = getDocument();
      doc.insertString(doc.getLength(),text,bold);
      fSeq = new Vector();
      fSeq.add("Initial Marking");
      currentItem = 1;
      updateText();
   }
   
   
   private void initStyles() {
      Style def = StyleContext.getDefaultStyleContext().getStyle(
              StyleContext.DEFAULT_STYLE);
      regular = addStyle("regular", def);
      StyleConstants.setFontFamily(def, "SansSerif");
      
      emph = addStyle("currentTransition",regular);
      StyleConstants.setBackground(emph,Color.LIGHT_GRAY);
      
      bold = addStyle("title",regular);
      StyleConstants.setBold(bold,true);
   }
   
   
   public void addHistoryItem(String transitionName) {
      fSeq.add(transitionName);
      currentItem = fSeq.size();
      updateText();
   }
   
   
   public void clearStepsForward() {
      fSeq.setSize(currentItem);
   }
   
   
   /** Method reinserts the text highlighting the currentItem */
   private void updateText() {
      String newS;
      int count=1;
      Style currentStyle = regular;
      
      Enumeration e = fSeq.elements();
      try {
         doc.remove(initText.length(),doc.getLength()-initText.length());
         
         while (e.hasMoreElements()) {
            newS = (String)e.nextElement();
            doc.insertString(doc.getLength(), newS+"\n",
                    (count ==currentItem) ?emph :regular);
            count++;
         }
      } catch (BadLocationException b) {
         System.err.println(b.toString());
      }
   }
   
   
   public void stepForward() {
      if (isStepForwardAllowed()) {
         currentItem++;
      }
      updateText();
   }
   
   
   public void stepBackwards() {
      if (isStepBackAllowed()){
         currentItem--;
      }
      updateText();
   }
   
   
   public boolean isStepForwardAllowed(){
      return currentItem < fSeq.size();
   }
   
   
   public boolean isStepBackAllowed(){
      return currentItem > 1;
   }
   
}
