package pipe.actions.type;

import pipe.actions.TypeAction;
import pipe.controllers.PetriNetController;
import pipe.models.component.Connectable;

import java.awt.*;

//TODO
public class DragAction extends TypeAction {

    @Override
    public void doAction(Point point, PetriNetController petriNetController) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void doConnectableAction(Connectable connectable, PetriNetController petriNetController) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public DragAction(final String name, final int typeID,
                     final String tooltip, final String keystroke) {
        super(name, typeID, tooltip, keystroke);
    }


}
