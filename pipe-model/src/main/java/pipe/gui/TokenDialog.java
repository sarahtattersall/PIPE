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

public class TokenDialog extends JDialog implements ActionListener{

	private boolean acceptChanges = false;
    private TokenPanel dialogContent;

    public TokenDialog(Frame owner, String title, boolean modal, TokenPanel dialogContent){
		super(owner, title, modal);
        this.dialogContent = dialogContent;
    }

	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("OK")){
            acceptChanges = ((TokenPanel.TableModel) dialogContent.table.getModel()).isValid();
		}
		else if(e.getActionCommand().equals("Cancel")){
			acceptChanges = false;
		}
        if(acceptChanges) {
		    setVisible(false);
        }
	}
	
	public boolean shouldAcceptChanges(){
		return acceptChanges;
	}

}
