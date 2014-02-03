package pipe.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Alex Charalambous, June 2010: ColorDrawer, ColorPicker, 
 * TokenPanel and TokenDialog are four classes used
 * to display the Token Classes dialog (accessible through the button 
 * toolbar).
 */

public class TokenDialog extends JDialog implements ActionListener {

    private TokenPanel dialogContent;

    public TokenDialog(Frame owner, String title, boolean modal, TokenPanel dialogContent){
		super(owner, title, modal);
        this.dialogContent = dialogContent;
    }

	@Override
    public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("OK")){
            if (dialogContent.isDataValid()) {
                dialogContent.setChanges();
                setVisible(false);
            }
		} else if (e.getActionCommand().equals("Cancel")) {
            setVisible(false);
        }
	}


}
