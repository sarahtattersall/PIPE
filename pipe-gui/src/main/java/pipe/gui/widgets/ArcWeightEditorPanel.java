package pipe.gui.widgets;

import pipe.controllers.ArcController;
import pipe.controllers.PetriNetController;
import pipe.utilities.gui.GuiUtils;
import uk.ac.imperial.pipe.models.petrinet.Token;
import uk.ac.imperial.pipe.parsers.UnparsableException;

import javax.swing.*;
import java.awt.Dimension;
import java.awt.Window;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class deals with editing the weight of an arc
 */
public class ArcWeightEditorPanel extends javax.swing.JPanel {

    private final PetriNetController petriNetController;

    private final ArcController<?, ?> arcController;

    private JRootPane rootPane;

    private javax.swing.JButton okButton = new javax.swing.JButton();

    private List<JTextField> inputtedWeights = new LinkedList<>();

    private List<String> inputtedTokenClassNames = new LinkedList<>();

    /**
     * Creates new form Arc Weight Editor
     *
     * @param rootPane
     * @param arcController controller for modifying arc
     */
    public ArcWeightEditorPanel(JRootPane rootPane, PetriNetController petriNetController,
                                ArcController<?, ?> arcController) {
        this.rootPane = rootPane;
        this.petriNetController = petriNetController;
        this.arcController = arcController;
        initComponents();

        this.rootPane.setDefaultButton(okButton);
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        JPanel arcEditorPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        JButton cancelButton = new JButton();

        setLayout(new java.awt.GridBagLayout());

        arcEditorPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Arc Weight Editor"));
        arcEditorPanel.setLayout(new java.awt.GridBagLayout());
        Dimension d = new Dimension();
        d.setSize(300, 340);
        arcEditorPanel.setPreferredSize(d);

        // Now set new dimension used in for loop below
        d = new Dimension();
        d.setSize(50, 19);
        int x = 0;
        int y = 0;

        for (final Token token : petriNetController.getNetTokens()) {
            gridBagConstraints = new java.awt.GridBagConstraints();

            JLabel tokenClassName = new JLabel();
            final JTextField tokenClassWeight = new JTextField();
            tokenClassWeight.setEditable(true);
            tokenClassWeight.setName(token.getId());

            tokenClassWeight.setText(arcController.getWeightForToken(token.getId()));
            inputtedWeights.add(tokenClassWeight);

            tokenClassName.setText(token.getId() + ": ");
            inputtedTokenClassNames.add(token.getId());

            gridBagConstraints.gridx = x;
            gridBagConstraints.gridy = y;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
            gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
            arcEditorPanel.add(tokenClassName, gridBagConstraints);

            tokenClassWeight.setPreferredSize(d);

            tokenClassWeight.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusGained(java.awt.event.FocusEvent evt) {
                    nameTextFieldFocusGained(evt);
                }

                public void focusLost(java.awt.event.FocusEvent evt) {
                    nameTextFieldFocusLost(evt);
                }
            });
            tokenClassWeight.setEnabled(true);

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = x + 3;
            gridBagConstraints.gridy = y;
            gridBagConstraints.gridwidth = 3;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
            arcEditorPanel.add(tokenClassWeight, gridBagConstraints);

            final JButton fweditor = new JButton("Weight expression editor");
            fweditor.setEnabled(true);
            gridBagConstraints.gridx = x + 3;
            gridBagConstraints.gridy = y + 1;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
            gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
            arcEditorPanel.add(fweditor, gridBagConstraints);

            fweditor.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    createEditorWindow(token.getId());
                }
            });

            y += 2;
        }

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(arcEditorPanel, gridBagConstraints);
        buttonPanel.setLayout(new java.awt.GridBagLayout());

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonHandler(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        buttonPanel.add(cancelButton, gridBagConstraints);

        okButton.setText("OK");
        okButton.setMaximumSize(new java.awt.Dimension(75, 25));
        okButton.setMinimumSize(new java.awt.Dimension(75, 25));
        okButton.setPreferredSize(new java.awt.Dimension(75, 25));
        okButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonHandler(evt);
            }
        });
        okButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                okButtonKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        buttonPanel.add(okButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 8, 3);
        add(buttonPanel, gridBagConstraints);
    }

    private void cancelButtonHandler(java.awt.event.ActionEvent evt) {
        // Provisional!
        exit();
    }

    private void exit() {
        rootPane.getParent().setVisible(false);
    }

    /**
     * Handles pressing of OK on Arc weight.
     *
     * @param evt
     */
    private void okButtonHandler(java.awt.event.ActionEvent evt) {
        for (JTextField inputtedWeight : inputtedWeights) {
            String expr = inputtedWeight.getText();
            if (expr.isEmpty()) {
                System.err.println("Error in functional rates expression.");
                String message = " Expression is invalid. Please check your function.";
                String title = "Error";
                JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        Map<String, String> newWeights = new HashMap<>();
        for (int i = 0; i < inputtedWeights.size(); i++) {
            String tokenClassName = inputtedTokenClassNames.get(i);
            String weight = inputtedWeights.get(i).getText();
            newWeights.put(tokenClassName, weight);
        }
        try {
            arcController.setWeights(newWeights);
            exit();
        } catch (UnparsableException e) {
            GuiUtils.displayErrorMessage(null, e.getMessage());
        }
    }

    private void okButtonKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            okButtonHandler(new java.awt.event.ActionEvent(this, 0, ""));
        }
    }

    private void nameTextFieldFocusGained(java.awt.event.FocusEvent evt) {
        // focusGained(nameTextField);
    }

    private void nameTextFieldFocusLost(java.awt.event.FocusEvent evt) {
        // focusLost(nameTextField);
    }

    public void createEditorWindow(String token) {
        Window window = SwingUtilities.getWindowAncestor(rootPane);
        EscapableDialog guiDialog = new EscapableDialog(window, "PIPE2", true);
        ArcFunctionEditor feditor =
                new ArcFunctionEditor(this, guiDialog, petriNetController.getPetriNet(), arcController, token);
        guiDialog.add(feditor);
        guiDialog.setSize(270, 230);
        guiDialog.setVisible(true);
        guiDialog.dispose();
    }

    public void setWeight(String func, String id) {
        for (int i = 0; i < inputtedTokenClassNames.size(); i++) {
            if (inputtedTokenClassNames.get(i).equals(id)) {
                inputtedWeights.get(i).setText(func);
            }
        }

    }

}
