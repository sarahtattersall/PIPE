package pipe.views;

import pipe.constants.GUIConstants;

import javax.swing.*;
import java.awt.Color;
import java.awt.Font;

public class NameLabel extends JTextArea {

    private String name;
    private String text;
    private double positionX;
    private double positionY;

    public NameLabel() {
        this("", 0, 0);
    }

    public NameLabel(String name, double nameOffsetX, double nameOffsetY) {
        super(name);
        this.name = name;
        positionX = nameOffsetX;
        positionY = nameOffsetY;
        text = "";
        Font font = new Font("Dialog", Font.BOLD, 10);
        setFont(getFont()
                .deriveFont(GUIConstants.LABEL_DEFAULT_FONT_SIZE));
        setFont(font);
        setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
        setEditable(false);
        setFocusable(false);
        setOpaque(false);
        setBackground(GUIConstants.BACKGROUND_COLOR);

    }

    public void setColor(Color c) {
        this.setForeground(c);
    }

    public void setPosition(double x, double y) {
        positionX = x;
        positionY = y;
        updatePosition();
    }


    public void updateSize() {
        // To get around Java bug #4352983 the size had to be expanded a bit
        setSize((int) (getPreferredSize().width * 1.2), (int) (getPreferredSize().height * 1.2));
        updatePosition();
    }

    public void updatePosition() {
        setBounds((int) (positionX - getPreferredSize().width), (int) (positionY - GUIConstants.NAMELABEL_OFFSET), (int) getPreferredSize().getWidth(), (int) getPreferredSize().getHeight());
    }


    public double getPositionX() {
        return positionX;
    }

    public double getPositionY() {
        return positionY;
    }

    public void setName(String nameInput) {
        name = nameInput;
        setText(text);
        updateSize();
    }

    @Override
    public void setText(String s) {
        text = s;
        if (name != null) {
            super.setText(name + s);
        } else {
            super.setText(s);
        }
        updateSize();
    }

}
