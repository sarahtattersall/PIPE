package pipe.gui;

import pipe.historyActions.AnimationHistory;
import pipe.models.component.Transition;
import pipe.models.interfaces.IObserver;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;


/**
 * Class to represent the history of the net animation
 */
public class AnimationHistoryView
        extends JTextPane implements IObserver {

    private final String initText;
    private final Document doc;
    private Style emph;
    private Style bold;
    private Style regular;
    private final AnimationHistory history;


    public AnimationHistoryView(AnimationHistory history, String text) throws
            javax.swing.text.BadLocationException {
        this.history = history;
        initText = text;
        initStyles();
        doc = getDocument();
        doc.insertString(doc.getLength(), text, bold);
        updateText();
    }


    private void initStyles() {
        Style def = StyleContext.getDefaultStyleContext().getStyle(
                StyleContext.DEFAULT_STYLE);
        regular = addStyle("regular", def);
        StyleConstants.setFontFamily(def, "SansSerif");

        emph = addStyle("currentTransition", regular);
        StyleConstants.setBackground(emph, Color.LIGHT_GRAY);

        bold = addStyle("title", regular);
        StyleConstants.setBold(bold, true);
    }


    /**
     * Method reinserts the text highlighting the currentItem
     */
    private void updateText() {
        int count = 0;
        try {
            doc.remove(initText.length(), doc.getLength() - initText.length());

            int currentPosition = history.getCurrentPosition();
            for (Transition transition : history.getFiringSequence()) {
                String id = transition.getId();
                doc.insertString(doc.getLength(), "\n" + id,
                        (count == currentPosition) ? emph : regular);
                count++;
            }
        } catch (BadLocationException b) {
            System.err.println(b.toString());
        }
    }


    @Override
    public void update() {
        updateText();
    }
}
