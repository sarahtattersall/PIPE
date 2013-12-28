package pipe.actions.type;

import pipe.controllers.PetriNetController;
import pipe.models.component.Connectable;

import java.awt.*;

public class NormalArcAction extends ArcAction {
    public NormalArcAction(String name, int typeID, String tooltip, String keystroke) {
        super(name, typeID, tooltip, keystroke);
    }

    @Override
    protected boolean canCreateArcHere(Connectable connectable) {
        return true;
    }

    @Override
    protected void createArc(Connectable connectable, PetriNetController petriNetController) {
        petriNetController.startCreatingNormalArc(connectable);
    }

    @Override
    public void doAction(Point point, PetriNetController petriNetController) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
