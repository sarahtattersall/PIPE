package pipe.actions.type;

import pipe.actions.TypeAction;
import pipe.controllers.PetriNetController;
import pipe.models.component.Connectable;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

//TODO: IS THIS REALLY A TYPEACTION?
public class SelectAction extends TypeAction {
    public SelectAction() {
        super("Select", "Select components (alt-S)", KeyEvent.VK_S, InputEvent.ALT_DOWN_MASK);
    }

    @Override
    public void doAction(MouseEvent event, PetriNetController petriNetController) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void doConnectableAction(Connectable connectable, PetriNetController petriNetController) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}
