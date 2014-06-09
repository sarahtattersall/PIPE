package pipe.actions.manager;

import pipe.actions.gui.GuiAction;
import pipe.actions.gui.AddTokenAction;
import pipe.actions.gui.CreateAction;
import pipe.actions.gui.DeleteTokenAction;
import pipe.actions.gui.SpecifyTokenAction;
import pipe.controllers.application.PipeApplicationController;
import pipe.actions.gui.PipeApplicationModel;
import pipe.views.PipeApplicationView;

import javax.swing.event.UndoableEditListener;
import java.util.Arrays;

/**
 * Houses the actions that are responsible for editing tokens
 */
public class TokenActionManager implements ActionManager {


    /**
     * Pop up for editing tokens
     */
    public final SpecifyTokenAction specifyTokenClasses;

    /**
     * Action to add a token to a place
     */
    public CreateAction tokenAction;

    /**
     * Action to delete a token from a place
     */
    public CreateAction deleteTokenAction;

    /**
     * Constructor
     * @param undoListener undo listener
     * @param applicationModel PIPE application model
     * @param applicationController PIPE application controller
     * @param applicationView PIPE application view
     */
    public TokenActionManager(UndoableEditListener undoListener, PipeApplicationModel applicationModel,
                              PipeApplicationController applicationController, PipeApplicationView applicationView) {
        tokenAction = new AddTokenAction(applicationModel);
        deleteTokenAction = new DeleteTokenAction(applicationModel);
        specifyTokenClasses = new SpecifyTokenAction(applicationController, applicationView);
        tokenAction.addUndoableEditListener(undoListener);
        deleteTokenAction.addUndoableEditListener(undoListener);
        specifyTokenClasses.addUndoableEditListener(undoListener);
    }

    /**
     *
     * @return all token actions housed
     */
    @Override
    public Iterable<GuiAction> getActions() {
        return Arrays.asList(tokenAction, deleteTokenAction, specifyTokenClasses);
    }

    /**
     * Enables the actions that set the number of tokens in a place
     */
    @Override
    public void enableActions() {
        tokenAction.setEnabled(true);
        deleteTokenAction.setEnabled(true);

    }

    /**
     * Disables the actions that set the number of tokens in a place
     */
    @Override
    public void disableActions() {
        tokenAction.setEnabled(false);
        deleteTokenAction.setEnabled(false);

    }
}
