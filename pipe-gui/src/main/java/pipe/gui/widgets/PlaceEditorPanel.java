package pipe.gui.widgets;

import pipe.controllers.PetriNetController;
import pipe.controllers.PlaceController;
import pipe.gui.ApplicationSettings;
import uk.ac.imperial.pipe.models.component.token.Token;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author pere
 */
public class PlaceEditorPanel extends javax.swing.JPanel {
    private final PetriNetController netController;

    private final PlaceController placeController;

    private final JRootPane rootPane;

    private final ChangeListener changeListener = new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent evt) {
            JSpinner spinner = (JSpinner) evt.getSource();
            JSpinner.NumberEditor numberEditor = ((JSpinner.NumberEditor) spinner.getEditor());
            numberEditor.getTextField().setBackground(new Color(255, 255, 255));
            spinner.removeChangeListener(this);
        }
    };

    private javax.swing.JLabel capacity0Label = new javax.swing.JLabel();

    private javax.swing.JSpinner capacitySpinner = new javax.swing.JSpinner();

    private javax.swing.JTextField nameTextField = new javax.swing.JTextField();

    private javax.swing.JButton okButton = new javax.swing.JButton();

    private List<JSpinner> inputtedMarkings = new LinkedList<>();

    private List<String> inputtedTokenClassNames = new LinkedList<>();


    /**
     * Creates new form PlaceEditor
     */
    public PlaceEditorPanel(JRootPane rootPane, PlaceController placeController,
                            PetriNetController petriNetController) {

        this.rootPane = rootPane;
        this.rootPane.setDefaultButton(okButton);
        this.placeController = placeController;

        netController = petriNetController;
        initComponents();
    }

    private void initComponents() {
        setLayout(new java.awt.GridBagLayout());
        JPanel placeEditorPanel = createPlaceEditorPanel();

        // Now set new dimension used in for loop below
        int col = 0;
        int row = 2;

        PetriNet net = ApplicationSettings.getApplicationController().getActivePetriNetController().getPetriNet();
        for (Token token : net.getTokens()) {
                JLabel tokenClassName = new JLabel();
                JSpinner tokenClassMarking = new JSpinner();
                inputtedMarkings.add(tokenClassMarking);

                tokenClassName.setText(token.getId() + ": ");
                inputtedTokenClassNames.add(token.getId());
                GridBagConstraints tokenNameConstraints = new java.awt.GridBagConstraints();
                tokenNameConstraints.gridx = 0;
                tokenNameConstraints.gridy = row;
                tokenNameConstraints.anchor = java.awt.GridBagConstraints.EAST;
                tokenNameConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
                placeEditorPanel.add(tokenClassName, tokenNameConstraints);

                tokenClassMarking.setValue(placeController.getTokenCount(token.getId()));
                tokenClassMarking.setMinimumSize(new java.awt.Dimension(50, 20));
                tokenClassMarking.setPreferredSize(new java.awt.Dimension(50, 20));
                tokenClassMarking.addChangeListener(new javax.swing.event.ChangeListener() {
                    public void stateChanged(javax.swing.event.ChangeEvent evt) {
                        markingSpinnerStateChanged(evt, inputtedMarkings.size() - 1);
                    }
                });

                GridBagConstraints tokenValueConstraints = new java.awt.GridBagConstraints();
                tokenValueConstraints.gridx = col + 1;
                tokenValueConstraints.gridy = row;
                tokenValueConstraints.gridwidth = 3;
                tokenValueConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
                tokenValueConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
                placeEditorPanel.add(tokenClassMarking, tokenValueConstraints);
                row++;
            }

        initializeCapacityLabel(placeEditorPanel, row);
        initializeCapacitySpinner(placeEditorPanel, row);
        initializeCapacity0Label(placeEditorPanel, row);

        GridBagConstraints gridBagConstraints;

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new java.awt.GridBagLayout());
        initializeOkButton(buttonPanel);
        initializeCancelButton(buttonPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(buttonPanel, gridBagConstraints);

    }

    /**
     * Sets the no capacity restriction label visible if the capacity is
     * zero
     *
     * @param capacity
     */
    private void setCapacityVisible(double capacity) {
        if (capacity == 0) {
            capacity0Label.setVisible(true);
        } else {
            capacity0Label.setVisible(false);
        }

    }

    private void initializeCapacity0Label(JPanel placeEditorPanel, int row) {
        capacity0Label.setText("(no capacity restriction)    ");
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = row;
        gridBagConstraints.insets = new Insets(3, 3, 3, 3);
        placeEditorPanel.add(capacity0Label, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new Insets(5, 8, 5, 8);
        add(placeEditorPanel, gridBagConstraints);
        setCapacityVisible(placeController.getCapacity());
    }

    private void initializeCapacitySpinner(JPanel placeEditorPanel, int row) {
        capacitySpinner.setModel(new SpinnerNumberModel(placeController.getCapacity(), 0, Integer.MAX_VALUE, 1));
        capacitySpinner.setMinimumSize(new Dimension(50, 20));
        capacitySpinner.setPreferredSize(new Dimension(50, 20));
        capacitySpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                capacitySpinnerStateChanged(evt);
            }
        });

        GridBagConstraints capacityConstraints = new GridBagConstraints();
        capacityConstraints.gridx = 1;
        capacityConstraints.gridy = row;
        capacityConstraints.fill = GridBagConstraints.HORIZONTAL;
        capacityConstraints.insets = new Insets(3, 3, 3, 3);
        placeEditorPanel.add(capacitySpinner, capacityConstraints);
    }

    private JPanel createPlaceEditorPanel() {
        GridBagConstraints gridBagConstraints;
        JPanel placeEditorPanel = new JPanel();
        placeEditorPanel.setLayout(new GridBagLayout());

        placeEditorPanel.setBorder(BorderFactory.createTitledBorder("Place Editor"));

        JLabel nameLabel = new JLabel();
        nameLabel.setText("NameDetails:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new Insets(3, 3, 3, 3);
        placeEditorPanel.add(nameLabel, gridBagConstraints);

        initializeNameTextField(placeEditorPanel);
        return placeEditorPanel;
    }

    private void initializeCapacityLabel(JPanel placeEditorPanel, int row) {
        GridBagConstraints gridBagConstraints;
        JLabel capacityLabel = new JLabel();
        capacityLabel.setText("Capacity:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = row;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new Insets(3, 3, 3, 3);
        placeEditorPanel.add(capacityLabel, gridBagConstraints);
    }

    private void initializeCancelButton(JPanel buttonPanel) {
        GridBagConstraints gridBagConstraints;
        JButton cancelButton = new JButton();
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonHandler(evt);
            }
        });

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(8, 0, 8, 10);
        buttonPanel.add(cancelButton, gridBagConstraints);
    }

    private void initializeNameTextField(JPanel placeEditorPanel) {
        nameTextField.setText(placeController.getName());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(3, 3, 3, 3);
        placeEditorPanel.add(nameTextField, gridBagConstraints);
    }

    private void initializeOkButton(JPanel buttonPanel) {
        okButton.setText("OK");
        okButton.setMaximumSize(new Dimension(75, 25));
        okButton.setMinimumSize(new Dimension(75, 25));
        okButton.setPreferredSize(new Dimension(75, 25));
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

        GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 9);
        buttonPanel.add(okButton, gridBagConstraints);
    }

    private void markingSpinnerStateChanged(javax.swing.event.ChangeEvent evt,
                                            int posInList) {//GEN-FIRST:event_markingSpinnerStateChanged
/*      Integer capacity = (Integer)capacitySpinner.getValue();
      int totalMarkings = 0;
      for(JSpinner inputtedMarking:inputtedMarkings){
    	  totalMarkings += (Integer)inputtedMarking.getValue();
      }
      int markingOfCurrentSpinner = (Integer)inputtedMarkings.get(posInList).getValue();
      if (capacity > 0) {
         if (totalMarkings > capacity) {
        	 int overMarkingLimit = totalMarkings - capacity;
        	 inputtedMarkings.get(posInList).setValue(markingOfCurrentSpinner - overMarkingLimit);
         }
      }*/
    }//GEN-LAST:event_markingSpinnerStateChanged

    private void okButtonKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            doOK();
        }
    }

    private void doOK() {

        Map<String, Integer> newTokenValues = getNewTokenValues();
        if (canSetCapacity() && canSetNameValue()) {

            placeController.startMultipleEdits();
            int newCapacity = (Integer) capacitySpinner.getValue();
            placeController.setCapacity(newCapacity);
            String newName = nameTextField.getText();
            placeController.setName(newName);
            placeController.setTokenCounts(newTokenValues);
            placeController.finishMultipleEdits();
            exit();
        }
    }

    private boolean canSetCapacity() {
        return true;
    }

    private Double getCapacitySpinnerValue() {
        return (Double) capacitySpinner.getValue();
    }

    /**
     * @return return false if could not set name
     */
    private boolean canSetNameValue() {
        String newName = nameTextField.getText();
        if (newName.equals(placeController.getName())) {
            return true;
        }
        if (!netController.isUniqueName(newName)) {
            JOptionPane.showMessageDialog(null, "There is already a component named " + newName, "Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Sets the token values on the place
     *
     * @return new token counts, empty if could not change
     */
    private  Map<String, Integer> getNewTokenValues() {
        Map<String, Integer> newTokenCounts = new HashMap<>();
        int totalCount = calculateTokenCount();
        if (placeController.hasCapacityRestriction() && totalCount > getCapacitySpinnerValue()) {
            JOptionPane.showMessageDialog(null,
                    "Token counts exceed the capacity of place. Please alter capacity or tokens");
            return newTokenCounts;
        }

        for (int tokenIndex = 0; tokenIndex < inputtedMarkings.size(); tokenIndex++) {
            String tokenName = inputtedTokenClassNames.get(tokenIndex);
            int newTokenCount = (Integer) inputtedMarkings.get(tokenIndex).getValue();

            try {
                if (newTokenCount < 0) {
                    JOptionPane.showMessageDialog(null, "Marking cannot be less than 0. Please re-enter");
                    newTokenCounts.clear();
                    return newTokenCounts;
                }

                if (placeController.getTokenCount(tokenName) != newTokenCount) {
                    newTokenCounts.put(tokenName, newTokenCount);
                }

            } catch (NumberFormatException ignored) {
                JOptionPane.showMessageDialog(null, "Please enter a positive integer greater or equal to 0.",
                        "Invalid entry", JOptionPane.ERROR_MESSAGE);
                newTokenCounts.clear();
                return newTokenCounts;
            } catch (HeadlessException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Please enter a positive integer greater or equal to 0.",
                        "Invalid entry", JOptionPane.ERROR_MESSAGE);
                newTokenCounts.clear();
                return newTokenCounts;
            }
        }

        return newTokenCounts;
    }

    /**
     * @return the total number of tokens declared for the place
     */
    private int calculateTokenCount() {
        int sum = 0;
        for (JSpinner inputtedMarking : inputtedMarkings) {
            Object value = inputtedMarking.getValue();
            sum += (Integer) value;
        }
        return sum;
    }

    private void okButtonHandler(java.awt.event.ActionEvent evt) {
        doOK();
    }

    private void exit() {
        rootPane.getParent().setVisible(false);
    }

    private void cancelButtonHandler(java.awt.event.ActionEvent evt) {
        exit();
    }

    private void capacitySpinnerStateChanged(javax.swing.event.ChangeEvent evt) {
        Double capacity = (Double) capacitySpinner.getValue();
        setCapacityVisible(capacity);
    }

}
