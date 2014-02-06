package pipe.gui.widgets;

import net.sourceforge.jeval.EvaluationException;
import pipe.controllers.PetriNetController;
import pipe.controllers.TransitionController;
import pipe.exceptions.PetriNetComponentNotFoundException;
import pipe.gui.ApplicationSettings;
import pipe.models.component.PetriNetComponent;
import pipe.models.component.arc.Arc;
import pipe.models.component.place.Place;
import pipe.models.component.rate.NormalRate;
import pipe.models.component.rate.Rate;
import pipe.models.component.rate.RateParameter;
import pipe.models.component.rate.RateType;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.ExprEvaluator;
import pipe.utilities.gui.GuiUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.Enumeration;

/**
 * @author pere
 * @author yufei wang
 */
public class TransitionEditorPanel extends javax.swing.JPanel {

    private static final String NO_RATE_PARAMETER = "No Rate Parameter";

    private final TransitionController transitionController;

    private final PetriNetController netController;

    private final JRootPane rootPane;

    private final JRadioButton immediateRadioButton = new JRadioButton();

    private final JRadioButton infiniteServerRadioButton = new JRadioButton();

    private final JTextField nameTextField = new JTextField();

    private final JButton okButton = new JButton();

    private final JLabel priorityLabel = new JLabel();

    private final JPanel priorityPanel = new JPanel();

    private final JSlider prioritySlider = new JSlider();

    private final JTextField priorityTextField = new JTextField();

    private final JComboBox<String> rateComboBox = new JComboBox<>();

    private final JLabel rateLabel = new JLabel();

    private final JButton functionalratebutton = new JButton();

    private final JTextField rateTextField = new JTextField();

    private final JComboBox<String> rotationComboBox = new JComboBox<>();

    private final ButtonGroup semanticsButtonGroup = new ButtonGroup();

    private final JLabel serverLabel = new JLabel();

    private final JPanel serverPanel = new JPanel();

    private final JRadioButton singleServerRadioButton = new JRadioButton();

    private final JRadioButton timedRadioButton = new JRadioButton();

    /**
     * Creates new form Transition editor
     *
     * @param rootPane             parent
     * @param transitionController controller for the transition to edit
     * @param netController        petriNetController that transitionController belongs to.
     */
    public TransitionEditorPanel(JRootPane rootPane, TransitionController transitionController,
                                 PetriNetController netController) {
        this.transitionController = transitionController;
        this.netController = netController;
        this.rootPane = rootPane;

        initComponents();


        this.serverLabel.setVisible(true);
        this.serverPanel.setVisible(true);

        this.rootPane.setDefaultButton(okButton);

        if (transitionController.isTimed()) {
            setUpTimedTransition();
        } else {
            setUpImmediateTransition();
        }

        if (transitionController.isInfiniteServer()) {
            infiniteServerRadioButton.setSelected(true);
        } else {
            singleServerRadioButton.setSelected(true);
        }


        initRates();
        setRateBasedOnTransition();
    }

    /**
     * Initialises the rate parameter combo dropdown
     * The first field is No rate parameter and means the user can input their
     * own value
     * <p/>
     * It then ses the rate to the transitions existing weight
     */
    private void initRates() {
        Collection<RateParameter> rateParameters = netController.getRateParameters();
        String[] ids = new String[rateParameters.size() + 1];

        ids[0] = NO_RATE_PARAMETER;
        int index = 1;
        for (RateParameter rateParameter : rateParameters) {
            ids[index] = rateParameter.getId();
            index++;
        }

        rateComboBox.setModel(new DefaultComboBoxModel<>(ids));
        if (transitionController.isTimed()) {
            rateComboBox.setEnabled(true);
        }
    }

    /**
     * Initialises the rate field based on the transitions value
     * It will find the rate parameter and select it
     */
    private void setRateBasedOnTransition() {
        Rate rate = transitionController.getRate();
        if (rate.getRateType().equals(RateType.RATE_PARAMETER)) {
            PetriNetComponent rateParameter = (RateParameter) rate;
            int index = getIndexOfRate(rateParameter.getId());
            rateComboBox.setSelectedIndex(index);
            if (index > 0) {
                rateTextField.setEnabled(false);
            }
        }
    }

    /**
     * Looks through the rateComboBox to find the index of the rateParameterId
     *
     * @param rateParameterId rate parameter id to search for in the rateComboBox
     * @return index of rateParameterId in the rateComboBox
     */
    private int getIndexOfRate(String rateParameterId) {
        for (int index = 0; index <= rateComboBox.getItemCount(); index++) {
            String id = rateComboBox.getModel().getElementAt(index);
            if (id.equals(rateParameterId)) {
                return index;
            }
        }
        throw new RuntimeException("No such element " + rateParameterId + " in petri net rate parameters");
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

        transitionEditorPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Transition Editor"));
        transitionEditorPanel.setLayout(new java.awt.GridBagLayout());

        nameLabel.setText("NameDetails:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        transitionEditorPanel.add(nameLabel, gridBagConstraints);

        nameTextField.setText(transitionController.getName());
        nameTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                nameTextFieldFocusGained();
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                nameTextFieldFocusLost();
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
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rateComboBoxActionPerformed();
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
        timedRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        timedRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        timedRadioButton.setMaximumSize(new java.awt.Dimension(90, 15));
        timedRadioButton.setMinimumSize(new java.awt.Dimension(90, 15));
        timedRadioButton.setPreferredSize(new java.awt.Dimension(90, 15));
        timedRadioButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timedRadioButtonActionPerformed(evt);
            }
        });
        timingPanel.add(timedRadioButton);

        timingButtonGroup.add(immediateRadioButton);
        immediateRadioButton.setText("Immediate");
        immediateRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        immediateRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        immediateRadioButton.setMaximumSize(new java.awt.Dimension(90, 15));
        immediateRadioButton.setMinimumSize(new java.awt.Dimension(90, 15));
        immediateRadioButton.setPreferredSize(new java.awt.Dimension(90, 15));
        immediateRadioButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
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
        singleServerRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        singleServerRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        singleServerRadioButton.setMaximumSize(new java.awt.Dimension(90, 15));
        singleServerRadioButton.setMinimumSize(new java.awt.Dimension(90, 15));
        singleServerRadioButton.setPreferredSize(new java.awt.Dimension(90, 15));
        singleServerRadioButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                serverRadioButtonActionPerformed(evt);
            }
        });

        serverPanel.add(singleServerRadioButton);

        semanticsButtonGroup.add(infiniteServerRadioButton);
        infiniteServerRadioButton.setSelected(true);
        infiniteServerRadioButton.setText("Infinite");
        infiniteServerRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        infiniteServerRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        infiniteServerRadioButton.setMaximumSize(new java.awt.Dimension(90, 15));
        infiniteServerRadioButton.setMinimumSize(new java.awt.Dimension(90, 15));
        infiniteServerRadioButton.setPreferredSize(new java.awt.Dimension(90, 15));
        infiniteServerRadioButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
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

        rotationComboBox.setModel(
                new javax.swing.DefaultComboBoxModel<>(new String[]{"", "+45\u00B0", "+90\u00B0", "-45\u00B0"}));
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
        rateTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                rateTextFieldFocusGained();
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                rateTextFieldFocusLost();
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
        prioritySlider.setToolTipText("1: lowest priority; 127: highest priority");
        prioritySlider.setValue(transitionController.getPriority());
        prioritySlider.addChangeListener(new javax.swing.event.ChangeListener() {
            @Override
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
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
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonHandler();
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
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                okButtonHandler();
            }
        });
        okButton.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
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

        functionalratebutton.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createWindow();
            }

        });

    }

    /**
     * Handles the cancellation by exiting the editor
     *
     * It does not perform any of the changes, the transition remains as is
     */
    private void cancelButtonHandler() {
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
     */
    private void okButtonHandler() {
        if (canSetName() && canSetInfiniteServer() && canSetRate() && canSetAngle() && (canSetTimed()
                || canSetPriority())) {
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
        if (infiniteServerRadioButton.isSelected() != transitionController.isInfiniteServer()) {
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
            if (!netController.isUniqueName(newName)) {
                showErrorMessage("There is already a transition named " + newName);
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
        if (newPriority != transitionController.getPriority() && !transitionController.isTimed()) {
            transitionController.setPriority(prioritySlider.getValue());
        }
    }

    /**
     * @return true if the transition has not been changed to timed/is a timed transition
     */
    private boolean canSetPriority() {
        return !timedRadioButton.isSelected();
    }

    private boolean canSetRate() {
        ExprEvaluator parser = new ExprEvaluator(netController.getPetriNet());
        double rate;
        try {
            rate = parser.parseAndEvalExprForTransition(rateTextField.getText());
        } catch (EvaluationException ignored) {
            showErrorMessage("Functional rate expression is invalid. Please check your function.");
            return false;
        }

        if (rate == -1) {
            showErrorMessage("Functional rate expression is invalid. Please check your function.");
            return false;
        }

        if (transitionController.getRateExpr().equals("")) {
            showErrorMessage("Functional rate expression is empty. Please check.");
            return false;
        }
        return true;
    }

    /**
     * Sets the models rate based upon the settings of the editor panel
     */
    public void setRateIfChanged() {
        if (rateComboBox.getSelectedIndex() > 0) {
            String rateParameterId = (String) rateComboBox.getSelectedItem();
            RateParameter rateParameter = null;
            try {
                rateParameter = netController.getPetriNet().getRateParameter(rateParameterId);
            } catch (PetriNetComponentNotFoundException petriNetComponentNotFoundException) {
                showErrorMessage(petriNetComponentNotFoundException.getMessage());
            }
            transitionController.setRate(rateParameter);
        } else {
            String rateExpression = rateTextField.getText();
            Rate rate = new NormalRate(rateExpression);
            transitionController.setRate(rate);
        }
    }

    /**
     *
     * Pops up a dialog with error message
     *
     * @param message message to be displayed on error
     */
    private void showErrorMessage(String message) {
        GuiUtils.displayErrorMessage(null, message);
    }

    /**
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

    private void okButtonKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            okButtonHandler();
        }
    }

    private void prioritySliderStateChanged(javax.swing.event.ChangeEvent evt) {
        priorityTextField.setText("" + prioritySlider.getValue());
    }

    private void immediateRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (immediateRadioButton.isSelected()) {
            setUpImmediateTransition();
        } else {
            setUpTimedTransition();
        }
    }

    private void serverRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (singleServerRadioButton.isSelected()) {
            functionalratebutton.setEnabled(true);
            singleServerRadioButton.setSelected(true);
            infiniteServerRadioButton.setSelected(false);
            rateTextField.setEditable(true);
            rateTextField.setText(transitionController.getRateExpr());
        } else {
            if (checkIfArcsAreFunctional()) {
                String message = "Infinite server cannot be connect directly to \r\n"
                        + "arcs with functional weights for this version";
                showErrorMessage(message);
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

    private void timedRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (timedRadioButton.isSelected()) {
            setUpTimedTransition();
        } else {
            setUpImmediateTransition();
        }
    }

    /**
     * Updates the text field to the value of the rate parameter
     * If no rate parameter is selected allow the user to edit the text field
     * otherwise disable the text field and show the value
     */
    private void rateComboBoxActionPerformed() {
        String selected = (String) rateComboBox.getModel().getSelectedItem();
        if (!selected.equals(NO_RATE_PARAMETER)) {
            RateParameter rateParameter = null;
            try {
                rateParameter = netController.getPetriNet().getRateParameter(selected);
            } catch (PetriNetComponentNotFoundException petriNetComponentNotFoundException) {
                showErrorMessage(petriNetComponentNotFoundException.getMessage());
            }
            rateTextField.setEnabled(false);
            rateTextField.setText(rateParameter.getExpression());
        } else {
            rateTextField.setEnabled(true);
        }
    }

    private void rateTextFieldFocusGained() {
        focusGained(rateTextField);
    }

    private void nameTextFieldFocusGained() {
        focusGained(nameTextField);
    }

    private void focusGained(JTextField textField) {
        textField.setCaretPosition(0);
        textField.moveCaretPosition(textField.getText().length());
    }

    private void nameTextFieldFocusLost() {
        focusLost(nameTextField);
    }

    private void focusLost(javax.swing.JTextField textField) {
        textField.setCaretPosition(0);
    }

    private void rateTextFieldFocusLost() {
        focusLost(rateTextField);
    }

    private void createWindow() {
        EscapableDialog guiDialog = new EscapableDialog(ApplicationSettings.getApplicationView(), "PIPE2", true);
        TransitionFunctionEditor feditor =
                new TransitionFunctionEditor(this, guiDialog, transitionController, netController.getPetriNet());
        guiDialog.add(feditor);
        guiDialog.setSize(270, 230);
        guiDialog.setLocationRelativeTo(ApplicationSettings.getApplicationView());
        guiDialog.setVisible(true);
        guiDialog.dispose();
    }

    private void setUpImmediateTransition() {
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
        rateComboBox.setEnabled(false);
    }

    private void setUpTimedTransition() {
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

        Enumeration<AbstractButton> buttons = semanticsButtonGroup.getElements();
        while (buttons.hasMoreElements()) {
            buttons.nextElement().setEnabled(true);
        }

        priorityLabel.setEnabled(false);
        priorityPanel.setEnabled(false);

        rateComboBox.setEnabled(netController.getRateParameters().size() > 0);
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
