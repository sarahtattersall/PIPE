/**
 * ButtonBar
 * 
 * Creates a bar with multiple buttons aligned.
 * 
 * @author Tamas Suto
 * @date 22/07/07
 * 
 */

package pipe.modules.queryeditor.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;


public class ButtonBar extends JPanel {

	private ButtonBar(String[] captions, ActionListener[] actions) {
		super();
		this.setLayout(new FlowLayout());
		for(int i=0; i<captions.length; i++) {
			JButton b = new JButton(captions[i]);
			b.addActionListener(actions[i]);
			this.add(b);  		
		}
		Dimension d = this.getPreferredSize();
		this.setMinimumSize(d);
		this.setMaximumSize(d);
	}
	
	public ButtonBar(String caption, ActionListener action) {
		this(new String[]{caption},new ActionListener[]{action});
	}  
} 
