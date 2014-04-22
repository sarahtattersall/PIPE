/**
 * QueryAction
 * 
 * - Handles loading icon based on action name and setting up other stuff
 * 
 * @author Tamas Suto
 * @date 14/05/07
 */

package pipe.modules.queryeditor.gui;


import pipe.modules.queryeditor.QueryManager;

import javax.swing.*;
import java.net.URL;


public abstract class QueryAction extends AbstractAction {
	
	public QueryAction (String name, String tooltip, String keystroke) {
		super(name);
		URL iconURL = Thread.currentThread().getContextClassLoader().getResource(QueryManager.imgPath + name + ".png");
		URL selectedIconURL = Thread.currentThread().getContextClassLoader().getResource(QueryManager.imgPath + name + "-selected.png");		
		if (iconURL != null){
			putValue(SMALL_ICON, new ImageIcon(iconURL));
		}	
		if(tooltip != null)
			putValue(SHORT_DESCRIPTION, tooltip);
		if(keystroke != null)
			putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(keystroke));
		
		putValue("selected", Boolean.FALSE);
		putValue("selectedIconURL",selectedIconURL);	
	}	
	
	
	public boolean isSelected(){
		return ((Boolean)getValue("selected")).booleanValue();
	}
	
	public void setSelected(boolean selected){
		putValue("selected", Boolean.valueOf(selected));
	}
}
