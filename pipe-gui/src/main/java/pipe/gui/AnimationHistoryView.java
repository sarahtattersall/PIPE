package pipe.gui;

import pipe.historyActions.AnimationHistory;
import pipe.historyActions.AnimationHistoryImpl;
import uk.ac.imperial.pipe.models.petrinet.Transition;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.Color;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Class to represent the history of the net animation
 */
public final class AnimationHistoryView
        extends JTextPane implements Observer {
    private static final Logger LOGGER = Logger.getLogger(AnimationHistoryImpl.class.getName());

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
            LOGGER.log(Level.SEVERE, b.getMessage());
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
        if (observable.getClass().equals(AnimationHistoryImpl.class)) {
            AnimationHistory history = (AnimationHistory) observable;
            updateText(history.getCurrentPosition(), history.getFiringSequence());
        }
    }
}
