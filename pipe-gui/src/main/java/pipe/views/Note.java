package pipe.views;

import pipe.constants.GUIConstants;
import pipe.controllers.PetriNetController;
import pipe.historyActions.AnnotationBorder;
import pipe.historyActions.HistoryItem;
import uk.ac.imperial.pipe.models.petrinet.Annotation;

import javax.swing.*;
import javax.swing.text.DefaultHighlighter;
import java.awt.*;
import java.awt.geom.RectangularShape;


/**
 * This abstract class is the base class for AnnotationNote class and for
 * Parameter class
 */
public abstract class Note extends AbstractPetriNetViewComponent<Annotation> {
    /**
     * Text note to appear on screen
     */
    final JTextArea note = new JTextArea();

    /**
     * Rectangular border for the note
     */
    final RectangularShape noteRect = new Rectangle();

    boolean drawBorder = true;

    private int originalX;

    private int originalY;


    Note(Annotation model, PetriNetController controller, Container parent) {
        super(model.getId(), model, controller, parent);
        initialise(model.getX(), model.getY());
        note.setText(model.getText());
        note.setSize(model.getWidth(), model.getHeight());
    }

    private void initialise(int x, int y) {
        originalX = x;
        originalY = y;

        note.setAlignmentX(Component.CENTER_ALIGNMENT);
        note.setAlignmentY(Component.CENTER_ALIGNMENT);
        note.setOpaque(false);
        note.setEditable(false);
        note.setEnabled(false);
        note.setLineWrap(true);
        note.setWrapStyleWord(true);

        // Set minimum size the preferred size for an empty string:
        note.setText("");
        note.setFont(new Font(GUIConstants.ANNOTATION_DEFAULT_FONT, Font.PLAIN, GUIConstants.ANNOTATION_DEFAULT_FONT_SIZE));
        note.setSize(note.getPreferredSize().width, note.getPreferredSize().height);
        note.setMinimumSize(note.getPreferredSize());
        note.setHighlighter(new DefaultHighlighter());
        note.setDisabledTextColor(GUIConstants.NOTE_DISABLED_COLOUR);
        note.setForeground(GUIConstants.NOTE_EDITING_COLOUR);
        add(note);
        setLocation(x - GUIConstants.RESERVED_BORDER / 2, y - GUIConstants.RESERVED_BORDER / 2);
    }

    /**
     * Calculates the BoundsOffsets used for setBounds() method
     */
    public void updateBounds() {
        int newHeight = note.getPreferredSize().height;

        if ((note.getHeight() < newHeight) && (newHeight >= note.getMinimumSize().height)) {
            note.setSize(note.getWidth(), newHeight);
        }

        int rectWidth = note.getWidth() + GUIConstants.RESERVED_BORDER;
        int rectHeight = note.getHeight() + GUIConstants.RESERVED_BORDER;

        noteRect.setFrame(GUIConstants.RESERVED_BORDER / 2, GUIConstants.RESERVED_BORDER / 2, rectWidth, rectHeight);
        setSize(rectWidth + GUIConstants.ANNOTATION_SIZE_OFFSET, rectHeight + GUIConstants.ANNOTATION_SIZE_OFFSET);

        note.setLocation((int) noteRect.getX() + (rectWidth - note.getWidth()) / 2,
                (int) noteRect.getY() + (rectHeight - note.getHeight()) / 2);

        bounds.setBounds(model.getX() - 20, model.getY() - 20,
                (rectWidth + GUIConstants.RESERVED_BORDER + GUIConstants.ANNOTATION_SIZE_OFFSET) + 20,
                (rectHeight + GUIConstants.RESERVED_BORDER +
                        +GUIConstants.ANNOTATION_SIZE_OFFSET) + 20);
        setBounds(bounds);
    }


    public boolean isShowingBorder() {
        return drawBorder;
    }

    public HistoryItem showBorder(boolean show) {
        drawBorder = show;
        repaint();
        return new AnnotationBorder(this);
    }

    public JTextArea getNote() {
        return note;
    }

    public String getNoteText() {
        return note.getText();
    }

    public int getNoteWidth() {
        return note.getWidth();
    }

    public int getNoteHeight() {
        return note.getHeight();
    }

    void adjustTop(int dy) {
        if (note.getPreferredSize().height <= (note.getHeight() - dy)) {
            note.setSize(new Dimension(note.getWidth(), note.getHeight() - dy));
            setLocation(getX(), getY() + dy);
            originalY += dy;
        }
    }

    void adjustBottom(int dy) {
        if (note.getPreferredSize().height <= (note.getHeight() + dy)) {
            note.setSize(new Dimension(note.getWidth(), note.getHeight() + dy));
        }
    }

    void adjustLeft(int dx) {
        if (GUIConstants.ANNOTATION_MIN_WIDTH <= (note.getWidth() - dx)) {
            note.setSize(new Dimension(note.getWidth() - dx, note.getHeight()));
            setLocation(getX() + dx, getY());
            originalX += dx;
        }
    }

    void adjustRight(int dx) {
        if (GUIConstants.ANNOTATION_MIN_WIDTH <= (note.getWidth() + dx)) {
            note.setSize(new Dimension(note.getWidth() + dx, note.getHeight()));
        }
    }

    @Override
    public boolean contains(int x, int y) {
        return noteRect.contains(x, y);
    }

    public String getText() {
        return note.getText();
    }

    public void setText(String text) {
        note.setText(text);
        note.setSize(note.getPreferredSize());
    }

    public int getOriginalX() {
        return originalX;
    }

    public int getOriginalY() {
        return originalY;
    }

}
