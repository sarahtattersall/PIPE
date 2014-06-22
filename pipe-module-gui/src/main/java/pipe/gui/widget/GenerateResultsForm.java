package pipe.gui.widget;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GenerateResultsForm {
    /**
     * Maximum number of threads allowed
     */
    private static final int MAX_THREADS = 100;

    /**
     * Error message if processing threads is incorrect
     */
    private static final String THREADS_ERROR_MESSAGE =
            "Error! Please enter a valid number of threads between 1-" + MAX_THREADS;

    /**
     * Action to perform when the go button is pressed
     */
    private final GoAction goAction;

    /**
     * Panel containing number of threads and go button
     */
    private JPanel generatePanel;

    /**
     * Number of threads text
     */
    private JTextField numberOfThreadsText;

    /**
     * Load results button
     */
    private JButton goButton;

    /**
     * Main panel containing the generate Panel
     */
    private JPanel mainPanel;

    public GenerateResultsForm(GoAction goAction) {
        this.goAction = goAction;
        goButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                go();
            }
        });
    }

    /**
     * Attempts to start the specified procedure by gathering the number
     * of threads to use. If it is not between 1 and MAX_THREADS then we display
     * and error and do not perform the action
     */
    private void go() {
        try {
            int threads = Integer.valueOf(numberOfThreadsText.getText());

            if (threads < 1 || threads > MAX_THREADS) {
                displayThreadErrorMessage();
                return;
            }
            goAction.go(threads);
        } catch (NumberFormatException e) {
            displayThreadErrorMessage();
        }
    }

    /**
     * Displays an error message depicting that the number of threads
     * entered does not conform to the expected values
     */
    private void displayThreadErrorMessage() {
        JOptionPane.showMessageDialog(mainPanel, THREADS_ERROR_MESSAGE, "GSPN Analysis Error",
                JOptionPane.ERROR_MESSAGE);
    }


    /**
     * @return panel to add to other GUI's
     */
    public JPanel getPanel() {
        return mainPanel;
    }

    /**
     * Interface used to programmatically decide what happens when the generate
     * button is pressed
     */
    public interface GoAction {
        void go(int threads);
    }

}
