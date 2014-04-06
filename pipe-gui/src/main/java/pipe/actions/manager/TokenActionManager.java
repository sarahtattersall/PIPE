package pipe.actions.manager;

import pipe.actions.gui.GuiAction;
import pipe.actions.gui.create.AddTokenAction;
import pipe.actions.gui.create.CreateAction;
import pipe.actions.gui.create.DeleteTokenAction;
import pipe.actions.gui.tokens.SpecifyTokenAction;
import pipe.controllers.PipeApplicationController;

import java.util.Arrays;

public class TokenActionManager implements ActionManager {


    public GuiAction tokenAction = new AddTokenAction();

    public GuiAction deleteTokenAction = new DeleteTokenAction();

    public final SpecifyTokenAction specifyTokenClasses;

    public TokenActionManager(PipeApplicationController applicationController) {
        specifyTokenClasses = new SpecifyTokenAction(applicationController);
    }

    @Override
    public Iterable<GuiAction> getActions() {
        return Arrays.asList(tokenAction, deleteTokenAction, specifyTokenClasses);
    }

    @Override
    public void enableActions() {
        tokenAction.setEnabled(true);
        deleteTokenAction.setEnabled(true);

    }

    @Override
    public void disableActions() {
        tokenAction.setEnabled(false);
        deleteTokenAction.setEnabled(false);

    }
}
