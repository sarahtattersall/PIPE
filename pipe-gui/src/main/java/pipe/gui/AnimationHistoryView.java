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
@SuppressWarnings("serial")
public final class AnimationHistoryView
        extends JTextPane implements Observer {
    /**
     * Class logger
     */
    private static final Logger LOGGER = Logger.getLogger(AnimationHistoryImpl.class.getName());

    /**
     * Text to display initially
     */
    private final String initText;

    /**
     * History document
     */
    private final Document doc;

    /**
     * Emphasis
     */
    private Style emph;

    /**
     * Bold
     */
    private Style bold;

    /**
     * Regular
     */
    private Style regular;


    /**
     * Constructor
     * @param text initally displayed text
     * @throws javax.swing.text.BadLocationException invalid location 
     */
    public AnimationHistoryView(String text) throws
            javax.swing.text.BadLocationException {
        initText = text;
        initStyles();
        doc = getDocument();
        doc.insertString(doc.getLength(), text, bold);
    }

    /**
     * Method reinserts the text highlighting the currentItem
     * @param historyPosition position 
     * @param firingSequence firing sequence 
     */
    private void updateText(int historyPosition, Iterable<Transition> firingSequence) {
        int count = 0;
        try {
            doc.remove(initText.length(), doc.getLength() - initText.length());

            for (Transition transition : firingSequence) {
                String id = transition.getId();
                doc.insertString(doc.getLength(), "\n" + id,
                        count == historyPosition ? emph : regular);
                count++;
            }
        } catch (BadLocationException b) {
            LOGGER.log(Level.SEVERE, b.getMessage());
        }
    }

    /**
     * Initialise bold, emph and regular styles
     */
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
     * Listen for animation actions and update the history accordingly
     * @param observable component 
     * @param o associated object 
     */
    @Override
    public void update(Observable observable, Object o) {
        if (observable.getClass().equals(AnimationHistoryImpl.class)) {
            AnimationHistory history = (AnimationHistory) observable;
            updateText(history.getCurrentPosition(), history.getFiringSequence());
        }
    }
}
