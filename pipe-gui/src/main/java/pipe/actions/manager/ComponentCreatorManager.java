package pipe.actions.manager;

import pipe.actions.gui.GuiAction;
import pipe.actions.gui.create.*;
import pipe.controllers.application.PipeApplicationController;
import pipe.controllers.arcCreator.InhibitorCreator;
import pipe.controllers.arcCreator.NormalCreator;
import pipe.gui.model.PipeApplicationModel;
import pipe.views.InhibitorArcHead;
import pipe.views.NormalHead;
import uk.ac.imperial.pipe.visitor.connectable.arc.InhibitorSourceVisitor;
import uk.ac.imperial.pipe.visitor.connectable.arc.NormalArcSourceVisitor;

import javax.swing.event.UndoableEditListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;

public class ComponentCreatorManager implements ActionManager {


    public CreateAction placeAction;

    public CreateAction transAction;

    public CreateAction timedtransAction;

    public final CreateAction arcAction;

    public final CreateAction inhibarcAction;

    public final SpecifyRateParameterAction rateParameterAction;

    public CreateAction annotationAction;


    public ComponentCreatorManager(UndoableEditListener undoListener, PipeApplicationModel applicationModel,
                                   PipeApplicationController applicationController) {
        placeAction = new PlaceAction(applicationModel);

        transAction = new ImmediateTransitionAction(applicationModel);

        timedtransAction = new TimedTransitionAction(applicationModel);

        annotationAction = new AnnotationAction(applicationModel);

        inhibarcAction =
                new ArcAction("Inhibitor Arc", "Add an inhibitor arc (alt-h)", KeyEvent.VK_H, InputEvent.ALT_DOWN_MASK,
                        new InhibitorSourceVisitor(), new InhibitorCreator(), applicationModel,
                        applicationController, new InhibitorArcHead());
        arcAction = new ArcAction("Arc", "Add an arc (alt-a)", KeyEvent.VK_A, InputEvent.ALT_DOWN_MASK,
                new NormalArcSourceVisitor(), new NormalCreator(applicationController), applicationModel,
                applicationController,
                new NormalHead());

        rateParameterAction = new SpecifyRateParameterAction(applicationController);

        placeAction.addUndoableEditListener(undoListener);
        transAction.addUndoableEditListener(undoListener);
        timedtransAction.addUndoableEditListener(undoListener);
        arcAction.addUndoableEditListener(undoListener);
        inhibarcAction.addUndoableEditListener(undoListener);
        annotationAction.addUndoableEditListener(undoListener);
        rateParameterAction.addUndoableEditListener(undoListener);
    }

    @Override
    public Iterable<GuiAction> getActions() {
        return Arrays.asList(placeAction, transAction, timedtransAction, arcAction, inhibarcAction, annotationAction,
                rateParameterAction);
    }

    @Override
    public void enableActions() {
        for (GuiAction action : getActions()) {
            action.setEnabled(true);
        }
    }

    @Override
    public void disableActions() {
        for (GuiAction action : getActions()) {
            action.setEnabled(false);
        }
    }

}
