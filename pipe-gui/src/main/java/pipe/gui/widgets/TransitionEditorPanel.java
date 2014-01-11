package pipe.gui.widgets;

import net.sourceforge.jeval.EvaluationException;
import parser.ExprEvaluator;
import pipe.controllers.PetriNetController;
import pipe.controllers.TransitionController;
import pipe.gui.ApplicationSettings;
import pipe.models.component.Arc;
import pipe.models.component.Place;
import pipe.models.component.Transition;
import pipe.views.PetriNetView;
import pipe.views.viewComponents.RateParameter;

import javax.swing.*;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.util.Enumeration;

/**
 * @author pere
 * @author yufei wang
 */
public class TransitionEditorPanel extends javax.swing.JPanel {

    private final TransitionController transitionController;
    private final PetriNetController netController;
    private final JRootPane rootPane;
    private final javax.swing.JRadioButton immediateRadioButton =
            new JRadioButton();
    private final javax.swing.JRadioButton infiniteServerRadioButton =
            new JRadioButton();
    private final javax.swing.JTextField nameTextField = new JTextField();
    private final javax.swing.JButton okButton = new JButton();
    private final javax.swing.JLabel priorityLabel = new JLabel();
    private final javax.swing.JPanel priorityPanel = new JPanel();
    private final javax.swing.JSlider prioritySlider = new JSlider();
    private final javax.swing.JTextField priorityTextField = new JTextField();
    private final javax.swing.JComboBox rateComboBox = new JComboBox();
    private final javax.swing.JLabel rateLabel = new JLabel();
    private final javax.swing.JButton functionalratebutton = new JButton();
    private final javax.swing.JTextField rateTextField = new JTextField();
    private final javax.swing.JComboBox rotationComboBox = new JComboBox();
    private final javax.swing.ButtonGroup semanticsButtonGroup =
            new ButtonGroup();
    private final javax.swing.JLabel serverLabel = new JLabel();
    private final javax.swing.JPanel serverPanel = new JPanel();
    private final javax.swing.JRadioButton singleServerRadioButton =
            new JRadioButton();
    private final javax.swing.JRadioButton timedRadioButton =
            new JRadioButton();
    private final CaretListener caretListener =
            new javax.swing.event.CaretListener() {
                public void caretUpdate(javax.swing.event.CaretEvent evt) {
                    JTextField textField = (JTextField) evt.getSource();
                    textField.setBackground(new Color(255, 255, 255));
                }
            };

    /**
     * Creates new form PlaceEditor
     *
     * @param _rootPane
     * @param transitionController
     * @param netController        petriNetController that transitionController belongs to.
     */
    public TransitionEditorPanel(JRootPane _rootPane, TransitionController transitionController,
                                 PetriNetController netController) {
        this.transitionController = transitionController;
        this.netController = netController;
        rootPane = _rootPane;

        initComponents();

        this.serverLabel.setVisible(true);
        this.serverPanel.setVisible(true);

        rootPane.setDefaultButton(okButton);

        if (transitionController.isTimed()) {
            timedTransition();
        } else {
            immediateTransition();
        }

        if (transitionController.isInfiniteServer()) {
            infiniteServerRadioButton.setSelected(true);
        } else {
            singleServerRadioButton.setSelected(true);
        }

        if (transitionController.getRateParameter() != null) {
            for (int i = 1; i < rateComboBox.getItemCount(); i++) {
                if (transitionController.getRateParameter() ==
                        rateComboBox.getItemAt(i)) {
                    rateComboBox.setSelectedIndex(i);
                }
            }
        }
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        ButtonGroup timingButtonGroup = new ButtonGroup();

        JPanel transitionEditorPanel = new JPanel();
        JLabel nameLabel = new JLabel();

        JPanel timingPanel = new JPanel();

        JLabel rotationLabel = new JLabel();

        JLabel timingLabel = new JLabel();

        JPanel buttonPanel = new JPanel();
        JButton cancelButton = new JButton();

        setLayout(new java.awt.GridBagLayout());

        transitionEditorPanel.setBorder(javax.swing.BorderFactory
                .createTitledBorder("Transition Editor"));
        transitionEditorPanel.setLayout(new java.awt.GridBagLayout());

        nameLabel.setText("Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        transitionEditorPanel.add(nameLabel, gridBagConstraints);

        nameTextField.setText(transitionController.getName());
        nameTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                nameTextFieldFocusGained(evt);
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                nameTextFieldFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        transitionEditorPanel.add(nameTextField, gridBagConstraints);

        rateLabel.setText("Constant Rate:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        transitionEditorPanel.add(rateLabel, gridBagConstraints);

        functionalratebutton.setText("Editor");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        transitionEditorPanel.add(functionalratebutton, gridBagConstraints);

        priorityLabel.setText("Priority:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        transitionEditorPanel.add(priorityLabel, gridBagConstraints);

        rateComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rateComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        transitionEditorPanel.add(rateComboBox, gridBagConstraints);

        timingPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        timingPanel.setLayout(new java.awt.GridLayout(1, 0));

        timingButtonGroup.add(timedRadioButton);
        timedRadioButton.setText("Timed");
        timedRadioButton.setBorder(
                javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        timedRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        timedRadioButton.setMaximumSize(new java.awt.Dimension(90, 15));
        timedRadioButton.setMinimumSize(new java.awt.Dimension(90, 15));
        timedRadioButton.setPreferredSize(new java.awt.Dimension(90, 15));
        timedRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timedRadioButtonActionPerformed(evt);
            }
        });
        timingPanel.add(timedRadioButton);

        timingButtonGroup.add(immediateRadioButton);
        immediateRadioButton.setText("Immediate");
        immediateRadioButton.setBorder(
                javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        immediateRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        immediateRadioButton.setMaximumSize(new java.awt.Dimension(90, 15));
        immediateRadioButton.setMinimumSize(new java.awt.Dimension(90, 15));
        immediateRadioButton.setPreferredSize(new java.awt.Dimension(90, 15));
        immediateRadioButton
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(
                            java.awt.event.ActionEvent evt) {
                        immediateRadioButtonActionPerformed(evt);
                    }
                });
        timingPanel.add(immediateRadioButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        transitionEditorPanel.add(timingPanel, gridBagConstraints);

        serverPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        serverPanel.setLayout(new java.awt.GridLayout(1, 0));

        semanticsButtonGroup.add(singleServerRadioButton);
        singleServerRadioButton.setText("Single");
        singleServerRadioButton.setBorder(
                javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        singleServerRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        singleServerRadioButton.setMaximumSize(new java.awt.Dimension(90, 15));
        singleServerRadioButton.setMinimumSize(new java.awt.Dimension(90, 15));
        singleServerRadioButton
                .setPreferredSize(new java.awt.Dimension(90, 15));
        singleServerRadioButton
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(
                            java.awt.event.ActionEvent evt) {
                        serverRadioButtonActionPerformed(evt);
                    }
                });

        serverPanel.add(singleServerRadioButton);

        semanticsButtonGroup.add(infiniteServerRadioButton);
        infiniteServerRadioButton.setSelected(true);
        infiniteServerRadioButton.setText("Infinite");
        infiniteServerRadioButton.setBorder(
                javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        infiniteServerRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        infiniteServerRadioButton
                .setMaximumSize(new java.awt.Dimension(90, 15));
        infiniteServerRadioButton
                .setMinimumSize(new java.awt.Dimension(90, 15));
        infiniteServerRadioButton
                .setPreferredSize(new java.awt.Dimension(90, 15));
        infiniteServerRadioButton
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(
                            java.awt.event.ActionEvent evt) {
                        serverRadioButtonActionPerformed(evt);
                    }
                });
        serverPanel.add(infiniteServerRadioButton);

        serverLabel.setText("Server:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        transitionEditorPanel.add(serverLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        transitionEditorPanel.add(serverPanel, gridBagConstraints);

        rotationLabel.setText("Rotation:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        transitionEditorPanel.add(rotationLabel, gridBagConstraints);

        rotationComboBox.setModel(new javax.swing.DefaultComboBoxModel(
                new String[]{"", "+45\u00B0", "+90\u00B0", "-45\u00B0"}));
        rotationComboBox.setMaximumSize(new java.awt.Dimension(70, 20));
        rotationComboBox.setMinimumSize(new java.awt.Dimension(70, 20));
        rotationComboBox.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        transitionEditorPanel.add(rotationComboBox, gridBagConstraints);

        rateTextField.setMaximumSize(new java.awt.Dimension(40, 19));
        rateTextField.setMinimumSize(new java.awt.Dimension(40, 19));
        rateTextField.setPreferredSize(new java.awt.Dimension(40, 19));
        rateTextField.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                rateTextFieldCaretUpdate(evt);
            }
        });
        rateTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                rateTextFieldFocusGained(evt);
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                rateTextFieldFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        transitionEditorPanel.add(rateTextField, gridBagConstraints);

        timingLabel.setText("Timing:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        transitionEditorPanel.add(timingLabel, gridBagConstraints);

        prioritySlider.setMajorTickSpacing(50);
        prioritySlider.setMaximum(127);
        prioritySlider.setMinimum(1);
        prioritySlider.setMinorTickSpacing(1);
        prioritySlider.setSnapToTicks(true);
        prioritySlider
                .setToolTipText("1: lowest priority; 127: highest priority");
        prioritySlider.setValue(transitionController.getPriority());
        prioritySlider
                .addChangeListener(new javax.swing.event.ChangeListener() {
                    public void stateChanged(
                            javax.swing.event.ChangeEvent evt) {
                        prioritySliderStateChanged(evt);
                    }
                });
        priorityPanel.add(prioritySlider);

        //prova
        priorityTextField.setEditable(false);
        priorityTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        priorityTextField.setText("1");
        priorityTextField.setMaximumSize(new java.awt.Dimension(36, 19));
        priorityTextField.setMinimumSize(new java.awt.Dimension(36, 19));
        priorityTextField.setPreferredSize(new java.awt.Dimension(36, 19));
        priorityTextField.setText("" + transitionController.getPriority());
        priorityPanel.add(priorityTextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        transitionEditorPanel.add(priorityPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(transitionEditorPanel, gridBagConstraints);

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

        functionalratebutton
                .addActionListener(new java.awt.event.ActionListener() {

                    @Override
                    public void actionPerformed(
                            java.awt.event.ActionEvent evt) {
                        createWindow();
                    }

                });

    }

    /**
     * Handles the cancellation by exiting the editor
     *
     * @param evt
     */
    private void cancelButtonHandler(java.awt.event.ActionEvent evt) {
        exit();
    }

    /**
     * Exit the editor panel
     */
    private void exit() {
        rootPane.getParent().setVisible(false);
    }

    /**
     * Handles saving by doing the following (if they've changed):
     * - sets if it's an infinite server
     * - sets if its timed
     * - sets its priority
     * - sets its angle
     * - sets (and evaluates) its rate expression
     *
     * @param evt
     */
    private void okButtonHandler(java.awt.event.ActionEvent evt) {
        if (canSetName() && canSetInfiniteServer() && canSetRate() && canSetAngle() && (canSetTimed() || canSetPriority())) {
            setNameIfChanged();
            setInfiniteServerIfChanged();
            setRateIfChanged();
            setAngleIfChanged();
            if (canSetTimed()) {
                setTimeIfChanged();
            }
            if (canSetPriority()) {
                setPriorityIfChanged();
            }
            exit();
        }
    }

    private void setInfiniteServerIfChanged() {
        if (infiniteServerRadioButton.isSelected() !=
                transitionController.isInfiniteServer()) {
            transitionController.setInfiniteServer(infiniteServerRadioButton.isSelected());
        }
    }

    /**
     * This always returns true since the infinite server option is just a radio box.
     *
     * @return true if the infinite server selection option is valid
     */
    private boolean canSetInfiniteServer() {
        return true;
    }

    /**
     * Sets the transitions name.
     * <p/>
     * PRE: canSetName() should be called first
     */
    private void setNameIfChanged() {
        String newName = nameTextField.getText();
        if (!newName.equals(transitionController.getName())) {
            transitionController.setName(newName);
        }
    }

    /**
     * Checks to see if the name can be set. This depends on whether there are any other transitions with this name.
     *
     * @return true if the name can be set, false otherwise
     */
    private boolean canSetName() {
        String newName = nameTextField.getText();
        if (!newName.equals(transitionController.getName())) {
            //TODO: REIMPLEMENT:
            if(false) {
//            if (.checkTransitionIDAvailability(newName)) {
//                return true;
            } else {
                JOptionPane.showMessageDialog(null,
                        "There is already a transitionController named " + newName,
                        "Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        return true;
    }

    /**
     * Sets the transitions timed value if it has changed
     */
    private void setTimeIfChanged() {
        if (timedRadioButton.isSelected() != transitionController.isTimed()) {
            transitionController.setTimed(timedRadioButton.isSelected());
        }
    }

    /**
     * This method will always return true since the timer is a radio button. It does not rely on any other criterion
     *
     * @return if it is ok for the transitions timed field to change
     */
    private boolean canSetTimed() {
        return true;
    }

    private void setPriorityIfChanged() {
        int newPriority = prioritySlider.getValue();
        if (newPriority != transitionController
                .getPriority() && !transitionController.isTimed()) {
            transitionController.setPriority(prioritySlider.getValue());
        }
    }

    /**
     * @return true if the transition has not been changed to timed/is a timed transition
     */
    private boolean canSetPriority() {
        return !timedRadioButton.isSelected();
    }

    private void okButtonKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            okButtonHandler(new java.awt.event.ActionEvent(this, 0, ""));
        }
    }

    private void prioritySliderStateChanged(javax.swing.event.ChangeEvent evt) {
        priorityTextField.setText("" + prioritySlider.getValue());
    }

    private void immediateRadioButtonActionPerformed(
            java.awt.event.ActionEvent evt) {
        if (immediateRadioButton.isSelected()) {
            immediateTransition();
        } else {
            timedTransition();
        }
    }

    private void serverRadioButtonActionPerformed(
            java.awt.event.ActionEvent evt) {
        if (singleServerRadioButton.isSelected()) {
            functionalratebutton.setEnabled(true);
            singleServerRadioButton.setSelected(true);
            infiniteServerRadioButton.setSelected(false);
            rateTextField.setEditable(true);
            rateTextField.setText(transitionController.getRateExpr());
        } else {
            if (checkIfArcsAreFunctional()) {
                String message =
                        "Infinite server cannot be connect directly to \r\n" +
                                "arcs with functional weights for this version";
                String title = "Error";
                JOptionPane.showMessageDialog(null, message, title,
                        JOptionPane.YES_NO_OPTION);
                functionalratebutton.setEnabled(true);
                singleServerRadioButton.setSelected(true);
                infiniteServerRadioButton.setSelected(false);

                return;
            }
            rateTextField.setEditable(false);
            singleServerRadioButton.setSelected(false);
            infiniteServerRadioButton.setSelected(true);
            functionalratebutton.setEnabled(false);
            rateTextField.setText("ED(" + transitionController.getName() + ")");
        }
    }

    private boolean checkIfArcsAreFunctional() {
        for (Arc<Place, Transition> arc : transitionController.inboundArcs()) {
            if (arc.hasFunctionalWeight()) {
                return true;
            }
        }
        return false;
    }

    private void timedRadioButtonActionPerformed(
            java.awt.event.ActionEvent evt) {
        if (timedRadioButton.isSelected()) {
            timedTransition();
        } else {
            immediateTransition();
        }
    }

    private void rateComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
        int index = rateComboBox.getSelectedIndex();
        if (index > 0) {
            //TODO: IMPLEMENT
//            rateTextField.setText(
//                    _pnmlData.markingRateParameters()[index - 1].getValue()
//                            .toString());
        }
    }

    private void rateTextFieldFocusGained(java.awt.event.FocusEvent evt) {
        focusGained(rateTextField);
    }

    private void nameTextFieldFocusGained(java.awt.event.FocusEvent evt) {
        focusGained(nameTextField);
    }

    private void focusGained(javax.swing.JTextField textField) {
        textField.setCaretPosition(0);
        textField.moveCaretPosition(textField.getText().length());
    }

    private void nameTextFieldFocusLost(java.awt.event.FocusEvent evt) {
        focusLost(nameTextField);
    }

    private void focusLost(javax.swing.JTextField textField) {
        textField.setCaretPosition(0);
    }

    private void rateTextFieldFocusLost(java.awt.event.FocusEvent evt) {
        focusLost(rateTextField);
    }

    private void rateTextFieldCaretUpdate(javax.swing.event.CaretEvent evt) {
        try {
            if ((rateComboBox.getSelectedIndex() > 0) &&
                    (((RateParameter) rateComboBox.getSelectedItem())
                            .getValue() !=
                            Double.parseDouble(rateTextField.getText()))) {
                rateComboBox.setSelectedIndex(0);
            }
        } catch (NumberFormatException nfe) {
            if (!nfe.getMessage().equalsIgnoreCase("empty String")) {
                System.out.println(
                        "NumberFormatException (not Empty String): \n" +
                                nfe.getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    private void createWindow() {
        EscapableDialog guiDialog =
                new EscapableDialog(ApplicationSettings.getApplicationView(),
                        "PIPE2", true);
        TransitionFunctionEditor feditor =
                new TransitionFunctionEditor(this, guiDialog,
                        transitionController, netController.getPetriNet());
        guiDialog.add(feditor);
        guiDialog.setSize(270, 230);
        guiDialog.setLocationRelativeTo(
                ApplicationSettings.getApplicationView());
        guiDialog.setVisible(true);
        guiDialog.dispose();
    }

    private void immediateTransition() {
        immediateRadioButton.setSelected(true);
        rateLabel.setText("Weight:");
        if (transitionController.isInfiniteServer()) {
            rateTextField.setText("ED(" + transitionController.getName() + ")");
            rateTextField.setEditable(false);
        } else {
            rateTextField.setText(transitionController.getRateExpr());
            rateTextField.setEditable(true);
        }
        rateTextField.setEnabled(false);
        functionalratebutton.setEnabled(true);
        functionalratebutton.setText("Weight expression editor");

        prioritySlider.setEnabled(true);
        priorityTextField.setText("" + transitionController.getPriority());

        priorityLabel.setEnabled(true);
        priorityPanel.setEnabled(true);

//        RateParameter[] rates = _pnmlData.markingRateParameters();
//        if (rates.length > 0) {
        if (false) {
//            rateComboBox.addItem("");
//            for (RateParameter rate1 : rates) {
//                rateComboBox.addItem(rate1);
//            }
        } else {
            rateComboBox.setEnabled(false);
        }
    }

    private void timedTransition() {
        timedRadioButton.setSelected(true);
        rateLabel.setText("Rate:");
        if (transitionController.isInfiniteServer()) {
            rateTextField.setText("ED(" + transitionController.getName() + ")");
            rateTextField.setEditable(false);
        } else {
            rateTextField.setText(transitionController.getRateExpr());
            rateTextField.setEditable(true);
        }
        rateTextField.setEnabled(true);
        functionalratebutton.setEnabled(true);
        functionalratebutton.setText("Rate Expression editor");

        prioritySlider.setEnabled(false);
        priorityTextField.setText("0");

        Enumeration buttons = semanticsButtonGroup.getElements();
        while (buttons.hasMoreElements()) {
            ((AbstractButton) buttons.nextElement()).setEnabled(true);
        }

        priorityLabel.setEnabled(false);
        priorityPanel.setEnabled(false);

        //        RateParameter[] rates = _pnmlData.markingRateParameters();
        //        if (rates.length > 0) {
        if (false) {
            //            rateComboBox.addItem("");
            //            for (RateParameter rate1 : rates) {
            //                rateComboBox.addItem(rate1);
            //            }
        } else {
            rateComboBox.setEnabled(false);
        }
    }

    private boolean canSetRate() {
        ExprEvaluator parser = new ExprEvaluator(netController.getPetriNet());
        double rate = 0;
        try {
            rate = parser.parseAndEvalExprForTransition(rateTextField.getText());
        } catch (EvaluationException e1) {
            return false;
        }

        if (rate == -1) {
            String message =
                    " Functional rate expression is invalid. Please check your function.";
            String title = "Error";
            JOptionPane.showMessageDialog(null, message, title,
                    JOptionPane.YES_NO_OPTION);
            return false;
        }

        if (transitionController.getRateExpr().equals("")) {
            String message =
                    "Functional rate expression is empty. Please check.";
            String title = "Error";
            JOptionPane.showMessageDialog(null, message, title,
                    JOptionPane.YES_NO_OPTION);
            return false;
        }
        return true;
    }


    //TODO: REIMPLEMENT
    public void setRateIfChanged() {
//        if (rateComboBox.getSelectedIndex() > 0) {
//            // There's a rate parameter selected
//            RateParameter parameter =
//                    (RateParameter) rateComboBox.getSelectedItem();
//            if (parameter != transitionController.getRateParameter()) {
//
//                if (rParameter != null) {
//                    // The rate parameter has been changed
//                    netController.getHistoryManager().addEdit(transitionController
//                            .changeRateParameter((RateParameter) rateComboBox
//                                    .getSelectedItem()));
//                }
//                else {
//                    // The rate parameter has been changed
//                    petriNetController.getHistoryManager().addEdit(transitionController
//                            .setRateParameter((RateParameter) rateComboBox
//                                    .getSelectedItem()));
//                }
//            }
//        }
//        else {
//
//            // There is no rate parameter selected
//            if (rParameter != null) {
//                // The rate parameter has been changed
//                petriNetController.getHistoryManager()
//                        .addEdit(transitionController.clearRateParameter());
//            }
//            try {
//                if (singleServerRadioButton.isSelected()) {
//                    // Double newRate = Double.parseDouble(rateTextField.getText());
//                    if (!(r == rate)) {
//                        petriNetController.getHistoryManager().addEdit(
//                                transitionController.setRate(rateTextField.getText()));
//                        //  transitionController.setRateType("C");
//                    }
//                }
//            } catch (NumberFormatException nfe) {
//                rateTextField.setBackground(new Color(255, 0, 0));
//                rateTextField.addCaretListener(caretListener);
//                return true;
//            } catch (Exception e) {
//                System.out.println(":" + e);
//            }
//        }
    }

    /**
     *
     * This method will always return true sine the angle depends upon nothing else.
     *
     * @return if the angle is OK to be set
     */
    private boolean canSetAngle() {
        return true;
    }

    private void setAngleIfChanged() {
        int rotationIndex = rotationComboBox.getSelectedIndex();
        if (rotationIndex > 0) {
            int angle = 0;
            switch (rotationIndex) {
                case 1:
                    angle = 45;
                    break;
                case 2:
                    angle = 90;
                    break;
                case 3:
                    angle = 135;
                    break;
                default:
                    break;
            }
            if (angle != transitionController.getAngle()) {
                transitionController.setAngle(angle);
            }
        }
    }

    /**
     * Sets the rateTextField ready for saving out to the model
     *
     * @param func new Rate for the transition
     */
    public void setRate(String func) {
        this.rateTextField.setText(func);
    }

}
