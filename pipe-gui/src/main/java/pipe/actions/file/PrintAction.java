package pipe.actions.file;

import pipe.actions.GuiAction;
import pipe.gui.ApplicationSettings;
import pipe.gui.Export;
import pipe.gui.PetriNetTab;
import pipe.views.PipeApplicationView;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class PrintAction extends GuiAction {
    public PrintAction() {
        super("Print", "Print", KeyEvent.VK_P, InputEvent.META_DOWN_MASK);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PipeApplicationView view = ApplicationSettings.getApplicationView();
        PetriNetTab tab = view.getCurrentTab();
        Export.exportGuiView(tab, Export.PRINTER, null);
    }
}
