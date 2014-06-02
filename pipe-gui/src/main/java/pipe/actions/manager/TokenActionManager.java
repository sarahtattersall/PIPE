package pipe.actions.manager;

import pipe.actions.gui.GuiAction;
import pipe.actions.gui.create.AddTokenAction;
import pipe.actions.gui.create.CreateAction;
import pipe.actions.gui.create.DeleteTokenAction;
import pipe.actions.gui.tokens.SpecifyTokenAction;
import pipe.controllers.application.PipeApplicationController;
import pipe.gui.model.PipeApplicationModel;
import pipe.views.PipeApplicationView;

import javax.swing.event.UndoableEditListener;
import java.util.Arrays;

public class TokenActionManager implements ActionManager {


    public final SpecifyTokenAction specifyTokenClasses;

    public CreateAction tokenAction;

    public CreateAction deleteTokenAction;

    public TokenActionManager(UndoableEditListener undoListener, PipeApplicationModel applicationModel,
                              PipeApplicationController applicationController, PipeApplicationView applicationView) {
        tokenAction = new AddTokenAction(applicationModel);
        deleteTokenAction = new DeleteTokenAction(applicationModel);
        specifyTokenClasses = new SpecifyTokenAction(applicationController, applicationView);
        tokenAction.addUndoableEditListener(undoListener);
        deleteTokenAction.addUndoableEditListener(undoListener);
        specifyTokenClasses.addUndoableEditListener(undoListener);
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
