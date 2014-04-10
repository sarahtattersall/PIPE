package pipe.actions.manager;

import pipe.actions.gui.GuiAction;
import pipe.actions.gui.create.AddTokenAction;
import pipe.actions.gui.create.CreateAction;
import pipe.actions.gui.create.DeleteTokenAction;
import pipe.actions.gui.tokens.SpecifyTokenAction;
import pipe.controllers.PipeApplicationController;

import javax.swing.event.UndoableEditListener;
import java.util.Arrays;

public class TokenActionManager implements ActionManager {


    public CreateAction tokenAction = new AddTokenAction();

    public CreateAction deleteTokenAction = new DeleteTokenAction();

    public final SpecifyTokenAction specifyTokenClasses;

    public TokenActionManager(UndoableEditListener undoListener, PipeApplicationController applicationController) {
        specifyTokenClasses = new SpecifyTokenAction(applicationController);
        tokenAction.addUndoableEditListener(undoListener);
        deleteTokenAction.addUndoableEditListener(undoListener);
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
