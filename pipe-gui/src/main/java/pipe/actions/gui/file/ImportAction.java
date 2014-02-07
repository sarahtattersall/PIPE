package pipe.actions.gui.file;

import pipe.actions.gui.GuiAction;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class ImportAction extends GuiAction {
    public ImportAction() {
        super("Import", "Import from eDSPN", KeyEvent.VK_I, InputEvent.META_DOWN_MASK);
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
