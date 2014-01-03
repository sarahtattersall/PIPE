package pipe.actions.file;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ImportAction extends FileAction {
    public ImportAction() {
        super("Import", "Import from eDSPN", "ctrl I");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
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
//                    pipeApplicationView.createNewTabDELETEME(filePath, true);
//                    appView.getSelectionObject().enableSelection();
//                }
    }
}
