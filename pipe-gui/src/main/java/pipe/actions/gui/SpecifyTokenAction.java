package pipe.actions.gui;

import pipe.controllers.PetriNetController;
import pipe.controllers.application.PipeApplicationController;
import pipe.gui.AbstractDatum;
import pipe.gui.TokenEditorPanel;
import pipe.historyActions.MultipleEdit;
import pipe.historyActions.component.AddPetriNetObject;
import pipe.historyActions.component.ChangePetriNetComponentName;
import pipe.historyActions.component.DeletePetriNetObject;
import pipe.historyActions.token.ChangeTokenColor;
import pipe.utilities.gui.GuiUtils;
import pipe.views.PipeApplicationView;
import uk.ac.imperial.pipe.exceptions.PetriNetComponentException;
import uk.ac.imperial.pipe.exceptions.PetriNetComponentNotFoundException;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;
import uk.ac.imperial.pipe.models.petrinet.PetriNetComponent;
import uk.ac.imperial.pipe.models.petrinet.Token;

import javax.swing.*;
import javax.swing.undo.UndoableEdit;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action used to create, delete and edit tokens in the Petri net
 */
@SuppressWarnings("serial")
public class SpecifyTokenAction extends GuiAction {
    /**
     * Main PIPE application controller
     */
    private final PipeApplicationController pipeApplicationController;

    /**
     * Main PIPE application view
     */
    private final PipeApplicationView applicationView;

    /**
     * Pop up editor panel for the tokens
     */
    private TokenEditorPanel tokenEditorPanel;

    /**
     * Pop up dialog
     */
    private JDialog guiDialog;

    /**
     * Legacy action, I'm not sure what this is
     */
    private ActionEvent forcedAction;

    public SpecifyTokenAction(PipeApplicationController pipeApplicationController,
                              PipeApplicationView applicationView) {
        super("SpecifyTokenClasses", "Specify tokens (ctrl-shift-T)", KeyEvent.VK_T,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | InputEvent.SHIFT_DOWN_MASK);
        this.pipeApplicationController = pipeApplicationController;
        this.applicationView = applicationView;
    }

    /**
     * Pops up to change the petri net tokens if there is an active petri net
     *
     * @param e event 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (pipeApplicationController.getActivePetriNetController() != null) {
            buildTokenGuiClasses();
            finishBuildingGui();
        }
    }

    /**
     * Creates a new token editor with the tokens from the Petri net
     */
    public void buildTokenGuiClasses() {
        tokenEditorPanel = new TokenEditorPanel(pipeApplicationController.getActivePetriNetController());
        guiDialog = new TokenDialog("Tokens", true, tokenEditorPanel);
    }

    /**
     * Provides the set up for what the token editor panel will look like
     */
    public void finishBuildingGui() {
        guiDialog.setSize(600, 200);
        guiDialog.setLocationRelativeTo(null);
        tokenEditorPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tokenEditorPanel.setOpaque(true);

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

        guiDialog.add(tokenEditorPanel, BorderLayout.CENTER);
        guiDialog.add(buttonPane, BorderLayout.PAGE_END);
        tokenEditorPanel.setVisible(true);

        if (forcedAction != null) {
            forceContinue();
        } else {
            guiDialog.setVisible(true);
        }
    }

    /**
     * Legacy code
     */
    private void forceContinue() {
        ((TokenDialog) guiDialog).actionPerformed(forcedAction);
        forcedAction = null;
    }

    /**
     * @author Alex Charalambous, June 2010: ColorDrawer, ColorPicker,
     *         TokenPanel and TokenDialog are four classes used
     *         to display the Token Classes dialog (accessible through the button
     *         toolbar).
     */

    public class TokenDialog<T extends PetriNetComponent> extends JDialog implements ActionListener {

        /**
         * Class logger
         */
        private final Logger logger = Logger.getLogger(TokenDialog.class.getName());

        /**
         * Editor panel
         */
        private TokenEditorPanel tokenEditorPanel;

        /**
         *
         * @param title token dialog title
         * @param modal dialog 
         * @param tokenEditorPanel panel 
         */
        public TokenDialog(String title, boolean modal, TokenEditorPanel tokenEditorPanel) {
            super(applicationView, title, modal);
            this.tokenEditorPanel = tokenEditorPanel;
        }

        /**
         * Processes the changing of Petri net tokens
         * @param e event 
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("OK")) {
                if (tokenEditorPanel.isDataValid()) {
                    updateFromTable(tokenEditorPanel.getTableData());
                    removeDeletedData(tokenEditorPanel.getDeletedData());
                    setVisible(false);
                }
            } else if (e.getActionCommand().equals("Cancel")) {
                setVisible(false);
            }
        }

        //TODO: ONCE PETRINET CAN GET COMPONENT BY ID YOU CAN MAKE THIS WHOLE CLASS ABSTRACT
        //      AND SHARE IT WITH RATE EDITOR

        /**
         * Removes tokens from the Petri net.
         * <p>
         * Creates error message if a token cannot be removed for some reason, for
         * example if places still contain tokens of its type. It will apply all other
         * changes.
         * </p>
         * @param deletedData contains Datum items that were deleted from the table
         */
        private void removeDeletedData(Iterable<TokenEditorPanel.Datum> deletedData) {
            PetriNetController petriNetController = pipeApplicationController.getActivePetriNetController();
            PetriNet petriNet = petriNetController.getPetriNet();
            List<UndoableEdit> undoableEdits = new LinkedList<>();
            for (TokenEditorPanel.Datum datum : deletedData) {
                if (tokenEditorPanel.isExistingDatum(datum)) {
                    try {
                        Token token = petriNet.getComponent(datum.id, Token.class);
                        petriNet.removeToken(token);
                        UndoableEdit historyItem = new DeletePetriNetObject(token, petriNet);
                        undoableEdits.add(historyItem);
                    } catch (PetriNetComponentNotFoundException e) {
                    	logger.log(Level.SEVERE, e.getMessage());
                    } catch (PetriNetComponentException e) {
                        StringBuilder messageBuilder = new StringBuilder();
                        messageBuilder.append(e.getMessage());
                        messageBuilder.append("\n");
                        messageBuilder.append("All other changes will be applied but this token will not be deleted!");
                        GuiUtils.displayErrorMessage(null, messageBuilder.toString());
                    }
                }
            }
            if (undoableEdits.size() > 0) {
                registerUndoEvent(new MultipleEdit(undoableEdits));
            }
        }

        /**
         * Update the Petri net component from the table data item
         * @param data for update 
         */
        private void updateFromTable(Iterable<TokenEditorPanel.Datum> data) {
            PetriNetController petriNetController = pipeApplicationController.getActivePetriNetController();
            List<UndoableEdit> undoableEdits = new LinkedList<>();
            for (TokenEditorPanel.Datum modified : data) {
                if (tokenEditorPanel.isExistingDatum(modified)) {
                    AbstractDatum initial = modified.initial;
                    if (!modified.equals(initial) && modified.hasBeenSet()) {
                        try {
                            Token token = petriNetController.getToken(initial.id);
                            undoableEdits.add(new ChangePetriNetComponentName(token, initial.id, modified.id));
                            undoableEdits.add(new ChangeTokenColor(token, token.getColor(), modified.color));
                            petriNetController.updateToken(initial.id, modified.id, modified.color);
                        } catch (PetriNetComponentNotFoundException petriNetComponentNotFoundException) {
                            GuiUtils.displayErrorMessage(null, petriNetComponentNotFoundException.getMessage());
                        }
                    }
                } else if (modified.hasBeenSet()) {
                    petriNetController.createNewToken(modified.id, modified.color);
                    try {
                        Token token = petriNetController.getToken(modified.id);
                        undoableEdits.add(new AddPetriNetObject(token, petriNetController.getPetriNet()));
                    } catch (PetriNetComponentNotFoundException e) {
                        logger.log(Level.SEVERE, e.getMessage());
                    }

                }
            }
            if (undoableEdits.size() > 0) {
                registerUndoEvent(new MultipleEdit(undoableEdits));
            }
        }


    }
}
