/*
 * Created on 10-Feb-2004
 */
package pipe.gui;

import pipe.constants.GUIConstants;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

/**
 * This class represents a grid that will draw itself on an object
 */
public class Grid {

    private static final Color GRID_COLOR = new Color(240, 240, 255);

    private float gridSpacing = GUIConstants.PLACE_TRANSITION_HEIGHT / 2;

    private GeneralPath gridDisplay;

    private boolean enabled = true;

    private int gridHeight;

    private int gridWidth;

    private int gridCount = 1;

    public void increment() {
        gridCount++;
        gridCount %= 4;

        if (gridCount == 3) {
            disableGrid();
        } else {
            enableGrid();
            setGridSpacing(Math.pow(2, gridCount - 2));
        }
    }

    public void disableGrid() {
        if (enabled) {
            enabled = false;
        }
    }

    public void enableGrid() {
        enabled = true;
    }

    private void setGridSpacing(double spacing) {
        gridSpacing = (float) (spacing * GUIConstants.PLACE_TRANSITION_HEIGHT);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void updateSize(Container parent) {
        if (enabled) {
            gridHeight = parent.getHeight();
            gridWidth = parent.getWidth();
            createGrid();
        }
    }

    private void createGrid() {
        gridDisplay = new GeneralPath();

        for (float i = gridSpacing; i <= gridWidth; i += gridSpacing) {
            gridDisplay.moveTo(i, 2);
            gridDisplay.lineTo(i, gridHeight);
        }
        for (float i = gridSpacing; i <= gridHeight; i += gridSpacing) {
            gridDisplay.moveTo(2, i);
            gridDisplay.lineTo(gridWidth, i);
        }
    }

    public void drawGrid(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setPaint(GRID_COLOR);
        g2d.draw(gridDisplay);
    }


    public int getModifiedValue(double value) {
        if (!enabled) {
            return (int) value;
        }
        return (int) (Math.round(value / gridSpacing) * gridSpacing);

    }
}
