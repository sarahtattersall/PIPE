package pipe.actions.gui;

import pipe.controllers.PetriNetController;
import pipe.controllers.RateParameterController;
import pipe.controllers.application.PipeApplicationController;
import pipe.gui.AbstractDatum;
import pipe.gui.RateEditorPanel;
import pipe.historyActions.MultipleEdit;
import pipe.historyActions.component.AddPetriNetObject;
import pipe.historyActions.component.DeletePetriNetObject;
import pipe.utilities.gui.GuiUtils;
import uk.ac.imperial.pipe.exceptions.InvalidRateException;
import uk.ac.imperial.pipe.exceptions.PetriNetComponentException;
import uk.ac.imperial.pipe.exceptions.PetriNetComponentNotFoundException;
import uk.ac.imperial.pipe.models.petrinet.FunctionalRateParameter;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;
import uk.ac.imperial.pipe.models.petrinet.RateParameter;

import javax.swing.*;
import javax.swing.undo.UndoableEdit;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class SpecifyRateParameterAction extends GuiAction {
    private static final Logger LOGGER = Logger.getLogger(SpecifyRateParameterAction.class.getName());

    private final PipeApplicationController pipeApplicationController;

    private RateEditorPanel rateEditorPanel;

    private JDialog guiDialog;

    public SpecifyRateParameterAction(PipeApplicationController pipeApplicationController) {
        super("Rate Parameter", "Specify Rate Parameters (alt-R)", KeyEvent.VK_R, InputEvent.ALT_DOWN_MASK);
        this.pipeApplicationController = pipeApplicationController;
    }

    /**
     * Pops up with an editor for the token rates if there is an active petri net
     *
     * @param e event 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (pipeApplicationController.getActivePetriNetController() != null) {
            buildGuiClasses();
            showGui();
        }

    }

    private void buildGuiClasses() {
        rateEditorPanel = new RateEditorPanel(pipeApplicationController.getActivePetriNetController());
        guiDialog = new RateDialog();

    }

    private void showGui() {
        guiDialog.setSize(600, 200);
        guiDialog.setLocationRelativeTo(null);
        rateEditorPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        rateEditorPanel.setOpaque(true);


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

        guiDialog.add(rateEditorPanel, BorderLayout.CENTER);
        guiDialog.add(buttonPane, BorderLayout.PAGE_END);
        rateEditorPanel.setVisible(true);

        guiDialog.setVisible(true);
    }

    private class RateDialog extends JDialog implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("OK")) {
                if (rateEditorPanel.isDataValid()) {
                    updateFromTable(rateEditorPanel.getTableData());
                    removeDeletedData(rateEditorPanel.getDeletedData());
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
        private void removeDeletedData(Iterable<RateEditorPanel.RateModel.Datum> deletedData) {
            PetriNetController petriNetController = pipeApplicationController.getActivePetriNetController();
            PetriNet petriNet = petriNetController.getPetriNet();
            List<UndoableEdit> undoableEdits = new LinkedList<>();
            for (RateEditorPanel.RateModel.Datum datum : deletedData) {
                if (rateEditorPanel.isExistingRateParameter(datum)) {
                    try {
                        RateParameter rateParameter = petriNet.getComponent(datum.id, RateParameter.class);
                        UndoableEdit historyItem = new DeletePetriNetObject(rateParameter, petriNet);
                        undoableEdits.add(historyItem);
                        petriNet.removeRateParameter(rateParameter);
                    } catch (PetriNetComponentNotFoundException e) {
                        LOGGER.log(Level.SEVERE, e.getMessage());
                    }
                }
            }
            if (undoableEdits.size() > 0) {
                registerUndoEvent(new MultipleEdit(undoableEdits));
            }
        }


        /**
         * Performs an update on the table data items
         *
         * @param data list of data in the table
         */
        private void updateFromTable(Iterable<RateEditorPanel.RateModel.Datum> data) {
            PetriNetController petriNetController = pipeApplicationController.getActivePetriNetController();
            List<UndoableEdit> undoableEdits = new LinkedList<>();
            for (RateEditorPanel.RateModel.Datum modified : data) {
                if (rateEditorPanel.isExistingRateParameter(modified)) {
                    AbstractDatum initial = modified.initial;
                    if (!modified.equals(initial) && modified.hasBeenSet()) {
                        try {
                            RateParameterController rateController =
                                    petriNetController.getRateParameterController(initial.id);
                            rateController.startMultipleEdits();
                            rateController.setId(modified.id);
                            rateController.setRate(modified.expression);
                            rateController.finishMultipleEdits();
                        } catch (PetriNetComponentNotFoundException | InvalidRateException e) {
                            GuiUtils.displayErrorMessage(null, e.getMessage());
                        }
                    }
                } else if (modified.hasBeenSet()) {
                    RateParameter rateParameter =
                            new FunctionalRateParameter(modified.expression, modified.id, modified.id);
                    try {
                        petriNetController.getPetriNet().add(rateParameter);
                        undoableEdits.add(new AddPetriNetObject(rateParameter, petriNetController.getPetriNet()));
                    } catch (PetriNetComponentException e) {
                        GuiUtils.displayErrorMessage(null, e.getMessage());
                    }
                }
            }
            if (undoableEdits.size() > 0) {
                registerUndoEvent(new MultipleEdit(undoableEdits));
            }
        }
    }
}
