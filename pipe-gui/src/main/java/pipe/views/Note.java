package pipe.views;

import pipe.constants.GUIConstants;
import pipe.controllers.PetriNetController;
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
    protected final JTextArea noteText = new JTextArea();

    /**
     * Rectangular border for the note
     */
    protected final RectangularShape noteRect = new Rectangle();

    protected boolean drawBorder = true;

    protected int originalX;

    protected int originalY;


    Note(Annotation model, PetriNetController controller, Container parent) {
        super(model.getId(), model, controller, parent);
        initialise(model.getX(), model.getY());
        noteText.setText(model.getText());
        noteText.setSize(model.getWidth(), model.getHeight());
    }

    private void initialise(int x, int y) {
        originalX = x;
        originalY = y;

        noteText.setAlignmentX(Component.CENTER_ALIGNMENT);
        noteText.setAlignmentY(Component.CENTER_ALIGNMENT);
        noteText.setOpaque(false);
        noteText.setEditable(false);
        noteText.setEnabled(false);
        noteText.setLineWrap(true);
        noteText.setWrapStyleWord(true);

        // Set minimum size the preferred size for an empty string:
        noteText.setText("");
        noteText.setFont(
                new Font(GUIConstants.ANNOTATION_DEFAULT_FONT, Font.PLAIN, GUIConstants.ANNOTATION_DEFAULT_FONT_SIZE));
        noteText.setSize(noteText.getPreferredSize().width, noteText.getPreferredSize().height);
        noteText.setMinimumSize(noteText.getPreferredSize());
        noteText.setHighlighter(new DefaultHighlighter());
        noteText.setDisabledTextColor(GUIConstants.NOTE_DISABLED_COLOUR);
        noteText.setForeground(GUIConstants.NOTE_EDITING_COLOUR);
        add(noteText);
        setLocation(x - GUIConstants.RESERVED_BORDER / 2, y - GUIConstants.RESERVED_BORDER / 2);
    }

    /**
     * Calculates the BoundsOffsets used for setBounds() method
     */
    public void updateBounds() {
        int newHeight = noteText.getPreferredSize().height;

        if (noteText.getHeight() < newHeight && newHeight >= noteText.getMinimumSize().height) {
            noteText.setSize(noteText.getWidth(), newHeight);
        }

        int rectWidth = noteText.getWidth() + GUIConstants.RESERVED_BORDER;
        int rectHeight = noteText.getHeight() + GUIConstants.RESERVED_BORDER;

        noteRect.setFrame(GUIConstants.RESERVED_BORDER / 2, GUIConstants.RESERVED_BORDER / 2, rectWidth, rectHeight);
        setSize(rectWidth + GUIConstants.ANNOTATION_SIZE_OFFSET, rectHeight + GUIConstants.ANNOTATION_SIZE_OFFSET);

        noteText.setLocation((int) noteRect.getX() + (rectWidth - noteText.getWidth()) / 2,
                (int) noteRect.getY() + (rectHeight - noteText.getHeight()) / 2);

        bounds.setBounds(model.getX() - 20, model.getY() - 20,
                rectWidth + GUIConstants.RESERVED_BORDER + GUIConstants.ANNOTATION_SIZE_OFFSET + 20,
                rectHeight + GUIConstants.RESERVED_BORDER + GUIConstants.ANNOTATION_SIZE_OFFSET + 20);
        setBounds(bounds);
    }


    public boolean isShowingBorder() {
        return drawBorder;
    }

    public JTextArea getNote() {
        return noteText;
    }

    public String getNoteText() {
        return noteText.getText();
    }

    public int getNoteWidth() {
        return noteText.getWidth();
    }

    public int getNoteHeight() {
        return noteText.getHeight();
    }

    public void adjustTop(int dy) {
        if (noteText.getPreferredSize().height <= noteText.getHeight() - dy) {
            noteText.setSize(new Dimension(noteText.getWidth(), noteText.getHeight() - dy));
            setLocation(getX(), getY() + dy);
            originalY += dy;
        }
    }

    public void adjustBottom(int dy) {
        if (noteText.getPreferredSize().height <= noteText.getHeight() + dy) {
            noteText.setSize(new Dimension(noteText.getWidth(), noteText.getHeight() + dy));
        }
    }

    public void adjustLeft(int dx) {
        if (GUIConstants.ANNOTATION_MIN_WIDTH <= noteText.getWidth() - dx) {
            noteText.setSize(new Dimension(noteText.getWidth() - dx, noteText.getHeight()));
            setLocation(getX() + dx, getY());
            originalX += dx;
        }
    }

    public void adjustRight(int dx) {
        if (GUIConstants.ANNOTATION_MIN_WIDTH <= noteText.getWidth() + dx) {
            noteText.setSize(new Dimension(noteText.getWidth() + dx, noteText.getHeight()));
        }
    }

    @Override
    public boolean contains(int x, int y) {
        return noteRect.contains(x, y);
    }

    public String getText() {
        return noteText.getText();
    }

    public void setText(String text) {
        noteText.setText(text);
        noteText.setSize(noteText.getPreferredSize());
    }

    public int getOriginalX() {
        return originalX;
    }

    public int getOriginalY() {
        return originalY;
    }

}
