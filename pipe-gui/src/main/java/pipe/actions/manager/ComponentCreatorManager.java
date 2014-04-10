package pipe.actions.manager;

import pipe.actions.gui.GuiAction;
import pipe.actions.gui.create.*;
import pipe.controllers.PipeApplicationController;
import pipe.controllers.arcCreator.InhibitorCreator;
import pipe.controllers.arcCreator.NormalCreator;
import pipe.views.arc.InhibitorArcHead;
import pipe.views.arc.NormalHead;
import pipe.visitor.connectable.arc.InhibitorSourceVisitor;
import pipe.visitor.connectable.arc.NormalArcSourceVisitor;

import javax.swing.event.UndoableEditListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;

public class ComponentCreatorManager implements ActionManager {


    public CreateAction placeAction = new PlaceAction();

    public CreateAction transAction = new ImmediateTransitionAction();

    public CreateAction timedtransAction = new TimedTransitionAction();

    public final CreateAction arcAction;

    public final CreateAction inhibarcAction;

    public final SpecifyRateParameterAction rateParameterAction;

    public CreateAction annotationAction = new AnnotationAction();


    public ComponentCreatorManager(UndoableEditListener undoListener,
                                   PipeApplicationController applicationController) {
        inhibarcAction =
                new ArcAction("Inhibitor Arc", "Add an inhibitor arc (alt-h)", KeyEvent.VK_H, InputEvent.ALT_DOWN_MASK,
                        new InhibitorSourceVisitor(), new InhibitorCreator(applicationController),
                        applicationController, new InhibitorArcHead());
        arcAction = new ArcAction("Arc", "Add an arc (alt-a)", KeyEvent.VK_A, InputEvent.ALT_DOWN_MASK,
                new NormalArcSourceVisitor(), new NormalCreator(applicationController), applicationController,
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
