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
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean acceptChanges = false;
    private TokenPanel _dialogContent;

    public TokenDialog(Frame owner, String title, boolean modal, TokenPanel dialogContent){
		super(owner, title, modal);
        _dialogContent = dialogContent;
    }
	public void actionPerformed(ActionEvent e) {
        boolean valid = true;
		if(e.getActionCommand().equals("OK")){
            valid = ((TokenPanel.TableModel) _dialogContent.table.getModel()).isValid();
            if(valid)
			    acceptChanges = true;
            else
                acceptChanges = false;
		}
		else if(e.getActionCommand().equals("Cancel")){
			acceptChanges = false;
		}
        if(valid)
		    setVisible(false);
	}
	
	public boolean shouldAcceptChanges(){
		return acceptChanges;
	}

}
