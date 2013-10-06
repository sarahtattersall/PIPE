package pipe.actions;

import pipe.controllers.PipeApplicationController;
import pipe.gui.ApplicationSettings;
import pipe.gui.Export;
import pipe.gui.PetriNetTab;
import pipe.views.PipeApplicationView;
import pipe.gui.widgets.FileBrowser;
import pipe.models.PipeApplicationModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class FileAction extends GuiAction
{
    private String _userPath;
	private File fileForTesting;

    // constructor
    public FileAction(String name, String tooltip, String keystroke)
    {
        super(name, tooltip, keystroke);
        _userPath = null;
    }

    public void actionPerformed(ActionEvent e)
    {
        PipeApplicationController pipeApplicationController = ApplicationSettings.getApplicationController();
        PipeApplicationView pipeApplicationView = ApplicationSettings.getApplicationView();
        PipeApplicationModel pipeApplicationModel = ApplicationSettings.getApplicationModel();
        if(this == pipeApplicationModel.saveAction)
        {
            pipeApplicationView.saveOperation(false); // code for Save operation
        }
        else if(this == pipeApplicationModel.saveAsAction)
        {
            pipeApplicationView.saveOperation(true); // code for Save As operations
        }
        else if(this == pipeApplicationModel.openAction)
        { // code for Open operation
            File filePath = getFile();
            if((filePath != null) && filePath.exists() && filePath.isFile() && filePath.canRead())
            {
                _userPath = filePath.getParent();
                pipeApplicationView.createNewTab(filePath, false);
            }
            if((filePath != null) && (!filePath.exists()))
            {
                String message = "File \"" + filePath.getName()
                        + "\" does not exist.";
                JOptionPane.showMessageDialog(null, message, "Warning",
                                              JOptionPane.WARNING_MESSAGE);
            }
        }
        else
        {
            PetriNetTab appView = pipeApplicationView.getCurrentTab();
            if(this == pipeApplicationModel.importAction)
            {	
            	 String message = "The eDSPN file for TimeNet is not compatible with PIPE temporarily.\r\n"
            			 +"We will fix it as soon as possible.";
            	JOptionPane.showMessageDialog(null, message, "Not Compatible",
                        JOptionPane.ERROR_MESSAGE);
            	return;
            	
            	//original code
//                File filePath = new FileBrowser(_userPath).openFile();
//                if((filePath != null) && filePath.exists()
//                        && filePath.isFile() && filePath.canRead())
//                {
//                    _userPath = filePath.getParent();
//                    pipeApplicationView.createNewTab(filePath, true);
//                    appView.getSelectionObject().enableSelection();
//                }
            }
            else if(this == pipeApplicationModel.createAction)
            {
                pipeApplicationView.createNewTab(null, false); // Create a new tab
            }
            else if((this == pipeApplicationModel.exitAction) && pipeApplicationView.checkForSaveAll())
            {
                pipeApplicationView.dispose();
                System.exit(0);
            }
            else
            {
                JTabbedPane appTab = pipeApplicationView._frameForPetriNetTabs;
                if((this == pipeApplicationModel.closeAction) && pipeApplicationView.checkForSave())
                {
                	pipeApplicationView.setObjectsNull(appTab.getSelectedIndex());
                	
                	if( (appTab.getTabCount() > 0) ){
                		appTab.remove(appTab.getSelectedIndex());
                	}
                }
                else if(this == pipeApplicationModel.exportPNGAction)
                {
                    Export.exportGuiView(appView, Export.PNG, null);
                }
                else if(this == pipeApplicationModel.exportPSAction)
                {
                    Export.exportGuiView(appView, Export.POSTSCRIPT, null);
                }
                else if(this == pipeApplicationModel.exportTNAction)
                {
                    Export.exportGuiView(appView, Export.TN, pipeApplicationView.getCurrentPetriNetView());
                }
                else if(this == pipeApplicationModel.printAction)
                {
                    Export.exportGuiView(appView, Export.PRINTER, null);
                }
            }
        }
    }

	protected File getFile()
	{
		return (fileForTesting != null) ? fileForTesting : new FileBrowser(_userPath).openFile();
	}
	public void setFileForTesting(File file)
	{
		fileForTesting = file; 
	}
}
