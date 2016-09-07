package pipe.actions.gui;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.net.URI;

@SuppressWarnings("serial")
public class InterfaceHelpAction extends GuiAction {

	private static final String INTERFACE_HELP_URL = "https://github.com/sjdayday/PIPECore/wiki";

	public InterfaceHelpAction() {
		super("Help", "Help on interfaces");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
	    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
	    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
	        try {
	            desktop.browse(new URI(INTERFACE_HELP_URL));
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
	    }
	}

}
