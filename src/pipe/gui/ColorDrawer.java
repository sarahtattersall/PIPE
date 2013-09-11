package pipe.gui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.table.TableCellRenderer;
import javax.swing.JTable;

/**
 * @author Alex Charalambous, June 2010: ColorDrawer, ColorPicker, 
 * TokenPanel and TokenDialog are four classes used
 * to display the Token Classes dialog (accessible through the button 
 * toolbar).
 */
public class ColorDrawer extends JLabel implements TableCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Border selectedBorder = null;
	private Border unselectedBorder = null;
	private boolean bordered = true;

	public ColorDrawer(boolean isBordered) {
		this.bordered = isBordered;
		setOpaque(true);
	}

	public Component getTableCellRendererComponent(JTable table, Object color,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Color newColor = (Color) color;
		setBackground(newColor);

		if (bordered) {
			if (isSelected) {
				if (selectedBorder == null) {
					selectedBorder = BorderFactory.createMatteBorder(2, 5, 2,
							5, table.getSelectionBackground());
				}
				setBorder(selectedBorder);
			} else {
				if (unselectedBorder == null) {
					unselectedBorder = BorderFactory.createMatteBorder(2, 5, 2,
							5, table.getBackground());
				}
				setBorder(unselectedBorder);
			}
		}
		setToolTipText("RGB value: " + newColor.getRed() + ", "
				+ newColor.getGreen() + ", " + newColor.getBlue());
		return this;
	}
}