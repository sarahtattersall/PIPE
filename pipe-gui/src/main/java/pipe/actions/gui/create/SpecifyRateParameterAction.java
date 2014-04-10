package pipe.actions.gui.create;

import pipe.actions.gui.GuiAction;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.exceptions.PetriNetComponentNotFoundException;
import pipe.gui.RatePanel;
import pipe.historyActions.*;
import pipe.models.component.rate.RateParameter;
import pipe.models.petrinet.PetriNet;
import pipe.utilities.gui.GuiUtils;

import javax.swing.*;
import javax.swing.undo.AbstractUndoableEdit;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;

public class SpecifyRateParameterAction extends GuiAction {
    private final PipeApplicationController pipeApplicationController;

    private RatePanel ratePanel;

    private JDialog guiDialog;

    public SpecifyRateParameterAction(PipeApplicationController pipeApplicationController) {
        super("Rate Parameter", "Specify Rate Parameters (alt-R)", KeyEvent.VK_R, InputEvent.ALT_DOWN_MASK);
        this.pipeApplicationController = pipeApplicationController;
    }

    /**
     * Pops up with an editor for the token rates if there is an active petri net
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (pipeApplicationController.getActivePetriNetController() != null) {
            buildGuiClasses();
            showGui();
        }

    }

    private void buildGuiClasses() {
        ratePanel = new RatePanel(pipeApplicationController.getActivePetriNetController());
        guiDialog = new RateDialog();

    }

    private void showGui() {
        guiDialog.setSize(600, 200);
        guiDialog.setLocationRelativeTo(null);
        ratePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        ratePanel.setOpaque(true);


        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());
        JButton ok = new JButton("OK");
        ok.addActionListener((ActionListener) guiDialog);
        buttonPane.add(ok);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener((ActionListener) guiDialog);
        buttonPane.add(cancel);

        guiDialog.add(ratePanel, BorderLayout.CENTER);
        guiDialog.add(buttonPane, BorderLayout.PAGE_END);
        ratePanel.setVisible(true);

        guiDialog.setVisible(true);
    }

    private class RateDialog extends JDialog implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("OK")) {
                if (ratePanel.isDataValid()) {
                    updateFromTable(ratePanel.getTableData());
                    removeDeletedData(ratePanel.getDeletedData());
                    setVisible(false);
                }
            } else if (e.getActionCommand().equals("Cancel")) {
                setVisible(false);
            }
        }
        /**
         * Removes any data deleted from the table if it was in the Petri net
         * when table loaded
         */
        private void removeDeletedData(Iterable<RatePanel.RateModel.Datum> deletedData) {
            PetriNetController petriNetController = pipeApplicationController.getActivePetriNetController();
            PetriNet petriNet = petriNetController.getPetriNet();
            List<AbstractUndoableEdit> undoableEdits = new LinkedList<>();
            for (RatePanel.RateModel.Datum datum : deletedData) {
                if (ratePanel.isExistingRateParameter(datum)) {
                    try {
                        RateParameter rateParameter = petriNet.getRateParameter(datum.id);
                        AbstractUndoableEdit historyItem = new DeletePetriNetObject(rateParameter, petriNet);
                        undoableEdits.add(historyItem);
                        petriNet.removeRateParameter(rateParameter);
                    } catch (PetriNetComponentNotFoundException ignored) {
                    }
                }
            }
            if (undoableEdits.size() > 0) {
                registerUndoEvent(new MultipleEdit(undoableEdits));
            }
        }


        /**
         * @return changes to be applied to the actual model based on the Datum available in the table
         */
        private void updateFromTable(Iterable<RatePanel.RateModel.Datum> data) {
            PetriNetController petriNetController = pipeApplicationController.getActivePetriNetController();
            List<AbstractUndoableEdit> undoableEdits = new LinkedList<>();
            for (RatePanel.RateModel.Datum modified : data) {
                if (ratePanel.isExistingRateParameter(modified)) {
                    RatePanel.RateModel.Datum initial = modified.initial;
                    if (!modified.equals(initial) && modified.hasBeenSet()) {
                        try {
                            RateParameter rateParameter = petriNetController.getPetriNet().getRateParameter(initial.id);
                            ChangeRateParameterId idEdit =
                                    new ChangeRateParameterId(rateParameter, initial.id, modified.id);
                            undoableEdits.add(idEdit);

                            AbstractUndoableEdit expressionEdit = new ChangeRateParameterRate(rateParameter, rateParameter.getExpression(), modified.expression);
                            undoableEdits.add(expressionEdit);
                            rateParameter.setExpression(modified.expression);

                        } catch (PetriNetComponentNotFoundException e) {
                            GuiUtils.displayErrorMessage(null, e.getMessage());
                        }
                    }
                } else if (modified.hasBeenSet()) {
                    RateParameter rateParameter = new RateParameter(modified.expression, modified.id, modified.id);
                    petriNetController.getPetriNet().add(rateParameter);
                    undoableEdits.add(new AddPetriNetObject(rateParameter, petriNetController.getPetriNet()));
                }
            }
            if (undoableEdits.size() > 0) {
                registerUndoEvent(new MultipleEdit(undoableEdits));
            }
        }
    }
}
