package pipe.views;

import pipe.constants.GUIConstants;

import javax.swing.*;
import java.awt.Color;
import java.awt.Font;

/**
 * Text label for displaying component information on the canvas
 * <p/>
 * This can be used for component ids, arc weights and so on
 */
public class TextLabel extends JTextArea {

    /**
     * Label name
     */
    private String name;

    /**
     * Label text, i.e. the components id
     */
    private String text;

    /**
     * x location
     */
    private double positionX;

    /**
     * y location
     */
    private double positionY;

    /**
     * Default constructor setting x, y to (0,0)
     *
     * @param text text for the label
     */
    public TextLabel(String text) {
        this(text, 0, 0);
    }

    /**
     * Constructor
     *
     * @param text        text to display
     * @param nameOffsetX x location
     * @param nameOffsetY y location
     */
    public TextLabel(String text, double nameOffsetX, double nameOffsetY) {
        super(text);
        this.name = text;
        positionX = nameOffsetX;
        positionY = nameOffsetY;
        this.text = "";
        Font font = new Font("Dialog", Font.BOLD, 10);
        setFont(getFont().deriveFont(GUIConstants.LABEL_DEFAULT_FONT_SIZE));
        setFont(font);
        setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
        setEditable(false);
        setFocusable(false);
        setOpaque(false);
        setBackground(GUIConstants.BACKGROUND_COLOR);

    }

    /**
     * @param color color of the text
     */
    public void setColor(Color color) {
        this.setForeground(color);
    }

    /**
     * Set the coordinates of the label
     *
     * @param x
     * @param y
     */
    public void setPosition(double x, double y) {
        positionX = x;
        positionY = y;
        updatePosition();
    }

    /**
     * Update the position of the text label relative to its set x,y
     */
    public void updatePosition() {
        setBounds((int) (positionX - getPreferredSize().width), (int) (positionY - GUIConstants.NAMELABEL_OFFSET),
                (int) getPreferredSize().getWidth(), (int) getPreferredSize().getHeight());
    }

    /**
     * Update the size of the text label
     */
    public void updateSize() {
        // To get around Java bug #4352983 the size had to be expanded a bit
        setSize((int) (getPreferredSize().width * 1.2), (int) (getPreferredSize().height * 1.2));
        updatePosition();
    }

    /**
     * Set the text displayed
     * @param text new text to display
     */
    @Override
    public void setText(String text) {
        this.text = text;
        super.setText(text);

        updateSize();
    }

}
