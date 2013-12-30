package pipe.actions.type;

import pipe.controllers.PetriNetController;
import pipe.gui.ApplicationSettings;
import pipe.models.component.Connectable;
import pipe.models.component.Token;
import pipe.views.PetriNetView;
import pipe.views.PipeApplicationView;

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
        PipeApplicationView view = ApplicationSettings.getApplicationView();
        Token activeToken = petriNetController.getToken(view.getSelectedTokenName());

        petriNetController.startCreatingNormalArc(connectable, activeToken);
    }

    @Override
    public void doAction(Point point, PetriNetController petriNetController) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
