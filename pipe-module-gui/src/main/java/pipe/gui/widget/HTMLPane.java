package pipe.gui.widget;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;

/**
 * A HTML pane to display results of analysis on.
 * <p/>
 * Largely taken from PIPE 4
 */
public class HTMLPane extends JPanel {

    /**
     * Pane on which to display results
     */
    private JEditorPane resultsPane = new JEditorPane();

    public HTMLPane() {
        super(new BorderLayout());
        setupResults();
    }

    private void setupResults() {
        resultsPane.setEditable(false);
        resultsPane.setMargin(new Insets(5, 5, 5, 5));
        resultsPane.setContentType("text/html");

        JScrollPane scrollPane = new JScrollPane(resultsPane);
        scrollPane.setPreferredSize(new Dimension(600, 600));
        scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
        this.add(scrollPane);
    }

    public void setText(String html) {
        resultsPane.setText(html);
        resultsPane.setCaretPosition(0); // scroll to top
    }
}
