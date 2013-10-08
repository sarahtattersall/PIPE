package pipe.views.viewComponents;

import pipe.gui.Constants;
import pipe.gui.Translatable;
import pipe.gui.ZoomController;
import pipe.gui.Zoomable;

import javax.swing.*;
import java.awt.*;

public class NameLabel extends JTextArea implements Cloneable, Translatable, Zoomable
{

    private String _name;
    private String _text;
    private double _positionX;
    private double _positionY;

    public NameLabel(int zoom)
    {
        this("", zoom, 0, 0);
    }

    public NameLabel(String name, int zoomPercentage, double nameOffsetX, double nameOffsetY)
    {
        super(name);
        _name = name;
        _positionX = nameOffsetX;
        _positionY = nameOffsetY;
        _text = "";
        Font font = new Font("Dialog", Font.BOLD, 10);
        setFont(getFont().deriveFont(ZoomController.getZoomedValue((float) Constants.LABEL_DEFAULT_FONT_SIZE, zoomPercentage)));
        setFont(font);
        setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
        setEditable(false);
        setFocusable(false);
        setOpaque(false);
        setBackground(Constants.BACKGROUND_COLOR);

    }

    public void setColor(Color c)
    {
        this.setForeground(c);
    }

    public void setPosition(double x, double y)
    {
        _positionX = x;
        _positionY = y;
        updatePosition();
    }


    public void updateSize()
    {
        // To get around Java bug #4352983 the size had to be expanded a bit
        setSize((int) (getPreferredSize().width * 1.2), (int) (getPreferredSize().height * 1.2));
        updatePosition();
    }

    void updatePosition()
    {
        setLocation((int)(_positionX - getPreferredSize().width), (int)(_positionY - Constants.NAMELABEL_OFFSET));
    }


    public void translate(int x, int y)
    {
        setPosition(_positionX + x, _positionY + y);
    }

    public double getPositionX()
    {
        return _positionX;
    }

    public double getPositionY()
    {
        return _positionY;
    }


    public void setName(String nameInput)
    {
        _name = nameInput;
        setText(_text);
        updateSize();
    }


    public void setText(String s)
    {
        _text = s;
        if(_name != null)
            super.setText(_name + s);
        else
            super.setText(s);
        updateSize();
    }


    public String getName()
    {
        return _name;
    }


    public String getText()
    {
        return _text;
    }


    public void zoomUpdate(int value)
    {
        setFont(getFont().deriveFont(ZoomController.getZoomedValue((float) Constants.LABEL_DEFAULT_FONT_SIZE, value)));
        updateSize();
    }


    public Object clone()
    {
        try
        {
            return super.clone();
        }
        catch(CloneNotSupportedException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
