package pipe.actions.gui.file;

import pipe.actions.gui.GuiAction;
import pipe.gui.ApplicationSettings;
import pipe.gui.PetriNetTab;
import pipe.views.PipeApplicationView;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class ExportPSAction extends GuiAction {
    /**
     * Sets shortcut to ctrl T
     */
    public ExportPSAction() {
        super("PostScript", "Export the net to PostScript format", KeyEvent.VK_T, InputEvent.META_DOWN_MASK);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PipeApplicationView view = ApplicationSettings.getApplicationView();
        PetriNetTab tab = view.getCurrentTab();
//        Export.exportGuiView(tab, Export.POSTSCRIPT, view.getCurrentPetriNetView());
    }

}
