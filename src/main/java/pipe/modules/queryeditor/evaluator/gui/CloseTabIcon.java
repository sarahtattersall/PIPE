/**
 * 
 */
package pipe.modules.queryeditor.evaluator.gui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.Icon;

class CloseTabIcon implements Icon
{
	private int			x_pos;
	private int			y_pos;
	private final int	width;
	private final int	height;
	private final Icon	fileIcon;

	public CloseTabIcon(final Icon fileIcon) {
		this.fileIcon = fileIcon;
		this.width = fileIcon.getIconWidth();
		this.height = fileIcon.getIconHeight();
	}

	public Rectangle getBounds()
	{
		return new Rectangle(this.x_pos, this.y_pos, this.width, this.height);
	}

	public int getIconHeight()
	{
		return this.height;
	}

	public int getIconWidth()
	{
		return this.width;
	}

	public void paintIcon(final Component c, final Graphics g, final int x, final int y)
	{
		this.x_pos = x;
		this.y_pos = y;

		if (this.fileIcon != null)
		{
			this.fileIcon.paintIcon(c, g, x, y + 2);
		}
	}
}
