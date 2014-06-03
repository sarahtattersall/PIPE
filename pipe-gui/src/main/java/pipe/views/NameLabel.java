package pipe.views;

import pipe.constants.GUIConstants;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

public class NameLabel extends JTextArea {

    private String _name;
    private String _text;
    private double _positionX;
    private double _positionY;

    public NameLabel() {
        this("", 0, 0);
    }

    public NameLabel(String name, double nameOffsetX, double nameOffsetY) {
        super(name);
        _name = name;
        _positionX = nameOffsetX;
        _positionY = nameOffsetY;
        _text = "";
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
        _positionX = x;
        _positionY = y;
        updatePosition();
    }


    public void updateSize() {
        // To get around Java bug #4352983 the size had to be expanded a bit
        setSize((int) (getPreferredSize().width * 1.2), (int) (getPreferredSize().height * 1.2));
        updatePosition();
    }

    public void updatePosition() {
        Dimension dimension = getPreferredSize();
        setBounds((int) (_positionX - getPreferredSize().width), (int) (_positionY - GUIConstants.NAMELABEL_OFFSET), (int) getPreferredSize().getWidth(), (int) getPreferredSize().getHeight());
    }


    public double getPositionX() {
        return _positionX;
    }

    public double getPositionY() {
        return _positionY;
    }

    public void setName(String nameInput) {
        _name = nameInput;
        setText(_text);
        updateSize();
    }

    @Override
    public void setText(String s) {
        _text = s;
        if (_name != null) {
            super.setText(_name + s);
        } else {
            super.setText(s);
        }
        updateSize();
    }

}
