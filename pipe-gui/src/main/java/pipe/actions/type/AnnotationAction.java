package pipe.actions.type;

import pipe.actions.TypeAction;
import pipe.controllers.PetriNetController;
import pipe.models.component.Connectable;

import java.awt.event.MouseEvent;

public class AnnotationAction extends TypeAction {

    @Override
    public void doAction(MouseEvent event, PetriNetController petriNetController) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void doConnectableAction(Connectable connectable, PetriNetController petriNetController) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public AnnotationAction(final String name, final int typeID,
                            final String tooltip, final String keystroke) {
        super(name, typeID, tooltip, keystroke);
    }


}
