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

    /**
     * Color for the grid
     */
    private static final Color GRID_COLOR = new Color(240, 240, 255);

    /**
     * Spacing between grid items
     */
    private float gridSpacing = GUIConstants.PLACE_TRANSITION_HEIGHT / 2;

    /**
     * Graphical grid display
     */
    private GeneralPath gridDisplay;

    /**
     * True if the grid should be displayed
     */
    private boolean enabled = true;

    /**
     * Height of the overall grid
     */
    private int gridHeight;

    /**
     * Width of the overall gird
     */
    private int gridWidth;

    /**
     * Grid count
     */
    private int gridCount = 1;

    /**
     * Increment the grid size
     */
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

    /**
     * Disable the grid from showing
     */
    public void disableGrid() {
        if (enabled) {
            enabled = false;
        }
    }

    /**
     * Set the grid to be displayed
     */
    public void enableGrid() {
        enabled = true;
    }

    /**
     * Set the spacing between the grid items
     * @param spacing
     */
    private void setGridSpacing(double spacing) {
        gridSpacing = (float) (spacing * GUIConstants.PLACE_TRANSITION_HEIGHT);
    }

    /**
     *
     * @return true if the grid should be displayed
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Set the size to that of the parent boundaries
     * @param parent
     */
    public void updateSize(Container parent) {
        if (enabled) {
            gridHeight = parent.getHeight();
            gridWidth = parent.getWidth();
            createGrid();
        }
    }

    /**
     * Create the path of the grid
     */
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

    /**
     * Draw the grid on the parent using the graphics
     * @param g
     */
    public void drawGrid(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setPaint(GRID_COLOR);
        g2d.draw(gridDisplay);
    }


}
