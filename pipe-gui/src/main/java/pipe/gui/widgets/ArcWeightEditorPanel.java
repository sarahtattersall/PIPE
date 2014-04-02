package pipe.gui.widgets;

import pipe.controllers.ArcController;
import pipe.controllers.PetriNetController;
import pipe.exceptions.PetriNetComponentNotFoundException;
import pipe.gui.ApplicationSettings;
import pipe.models.component.Connectable;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.ExprEvaluator;
import pipe.utilities.gui.GuiUtils;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

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
            if (token.isEnabled()) {
                gridBagConstraints = new java.awt.GridBagConstraints();

                JLabel tokenClassName = new JLabel();
                final JTextField tokenClassWeight = new JTextField();
                tokenClassWeight.setEditable(true);
                tokenClassWeight.setName(token.getId());

                tokenClassWeight.setText(arcController.getWeightForToken(token));
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
                        createEditorWindow(token);
                    }
                });

                y += 2;
            }
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

    public void createEditorWindow(Token token) {
        EscapableDialog guiDialog = new EscapableDialog(ApplicationSettings.getApplicationView(), "PIPE2", true);
        ArcFunctionEditor feditor =
                new ArcFunctionEditor(this, guiDialog, petriNetController.getPetriNet(), arcController, token);
        guiDialog.add(feditor);
        guiDialog.setSize(270, 230);
        guiDialog.setLocationRelativeTo(ApplicationSettings.getApplicationView());
        guiDialog.setVisible(true);
        guiDialog.dispose();
    }

    private void nameTextFieldFocusLost(java.awt.event.FocusEvent evt) {
        // focusLost(nameTextField);
    }

    private void nameTextFieldFocusGained(java.awt.event.FocusEvent evt) {
        // focusGained(nameTextField);
    }

    private void okButtonKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            okButtonHandler(new java.awt.event.ActionEvent(this, 0, ""));
        }
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

                //                String tokenClassName = inputtedTokenClassNames.get(i);
                //                Token token = petriNetController.getToken(tokenClassName);
                //
                //                if (parser.parseAndEvalExpr(expr, tokenClassName) != -1) {
                //                    arcController.setWeight(token, expr);
                //                } else {
                //                    if (parser.parseAndEvalExpr(expr, tokenClassName) == -2) {
                //                        JOptionPane.showMessageDialog(null, "Please make sure division and floating numbers are "
                //                                + "surrounded by ceil() or floor()");
                //                        return;
                //                    } else {
                //                        System.err.println("Error in functional rates expression.");
                //                        String message = " Expression is invalid. Please check your function.";
                //                        String title = "Error";
                //                        JOptionPane.showMessageDialog(null, message, title, JOptionPane.YES_NO_OPTION);
                //                        return;
                //                    }
                //                }
                //            }
                //            //            catch (MarkingDividedByNumberException e) {
                //            //                JOptionPane.showMessageDialog(null,
                //            //                        "Marking-dependent arc weight divided by number not supported.\r\n" +
                //            //                                "Since this may cause non-integer arc weight.");
                //            //                return;
                //            //            }
//            } catch (Exception e) {
//                System.err.println("Error in functional rates expression.");
//                String message = " Expression is invalid. Please check your function.";
//                String title = "Error";
//                JOptionPane.showMessageDialog(null, message, title, JOptionPane.YES_NO_OPTION);
//                return;
//            }
        }

        //TODO: PUSH THIS METHOD DOWN
        if (arcController.hasFunctionalWeight()) {
            Connectable target = arcController.getTarget();
            if (target instanceof Transition) {
                Transition transition = (Transition) target;
                if (transition.isInfiniteServer()) {
                    String message = "This arc points to an infinite server transition. \r\n"
                            + "Thus this arc could not have functional weights at the moment";
                    String title = "Error";
                    JOptionPane.showMessageDialog(null, message, title, JOptionPane.YES_NO_OPTION);
                    return;
                }
            }
        }

        Map<Token, String> newWeights = new HashMap<>();
        for (int i = 0; i < inputtedWeights.size(); i++) {
            String tokenClassName = inputtedTokenClassNames.get(i);
            try {
                Token token = petriNetController.getToken(tokenClassName);
                String weight = inputtedWeights.get(i).getText();
                newWeights.put(token, weight);
//                int evaluatedWeight = parser.parseAndEvalExpr(weight, tokenClassName);
//                if (evaluatedWeight == -1) {
//                    GuiUtils.displayErrorMessage(null,
//                            "Error in weight expression. Please make sure\r\n it is an integer");
//                    return;
//                }
//                if (evaluatedWeight == -2) {
//                    GuiUtils.displayErrorMessage(null,
//                            "Please make sure division and floating numbers are " + "surrounded by ceil() or floor()");
//                    return;
//                }
//                if (evaluatedWeight < 0) {
//                    GuiUtils.displayErrorMessage(null, "Weighting cannot be less than 0. Please re-enter");
//                    return;
//                }

            } catch (PetriNetComponentNotFoundException petriNetComponentNotFoundException) {
                GuiUtils.displayErrorMessage(null, petriNetComponentNotFoundException.getMessage());
                return;
            }
        }
        arcController.setWeights(newWeights);
        exit();
    }

    private void cancelButtonHandler(java.awt.event.ActionEvent evt) {
        // Provisional!
        exit();
    }

    private void exit() {
        rootPane.getParent().setVisible(false);
    }

    public void setWeight(String func, String id) {
        for (int i = 0; i < inputtedTokenClassNames.size(); i++) {
            if (inputtedTokenClassNames.get(i).equals(id)) {
                inputtedWeights.get(i).setText(func);
            }
        }

    }

}
