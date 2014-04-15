package pipe.gui;

import pipe.historyActions.AnimationHistory;
import pipe.models.component.transition.Transition;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;


/**
 * Class to represent the history of the net animation
 */
public class AnimationHistoryView
        extends JTextPane implements Observer {

    private final String initText;
    private final Document doc;
    private Style emph;
    private Style bold;
    private Style regular;


    public AnimationHistoryView(String text) throws
            javax.swing.text.BadLocationException {
        initText = text;
        initStyles();
        doc = getDocument();
        doc.insertString(doc.getLength(), text, bold);
    }

    /**
     * Method reinserts the text highlighting the currentItem
     */
    private void updateText(int historyPosition, Iterable<Transition> firingSequence) {
        int count = 0;
        try {
            doc.remove(initText.length(), doc.getLength() - initText.length());

            for (Transition transition : firingSequence) {
                String id = transition.getId();
                doc.insertString(doc.getLength(), "\n" + id,
                        (count == historyPosition) ? emph : regular);
                count++;
            }
        } catch (BadLocationException b) {
            System.err.println(b.toString());
        }
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

    @Override
    public void update(Observable observable, Object o) {
        if (Observable.class.equals(AnimationHistory.class)) {
            AnimationHistory history = (AnimationHistory) observable;
            updateText(history.getCurrentPosition(), history.getFiringSequence());
        }
    }
}
