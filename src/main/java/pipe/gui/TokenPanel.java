package pipe.gui;

import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.models.Token;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.Collection;
import java.util.Random;

/**
 * @author Alex Charalambous, June 2010: ColorDrawer, ColorPicker,
 *         TokenPanel and TokenDialog are four classes used to display
 *         the Token Classes dialog (accessible through the button toolbar).
 */

public class TokenPanel extends JPanel {
    public JTable table; // Steve Doubleday changed from final to simplify testing
    private final PetriNetController petriNetController;

    public TokenPanel() {
        PipeApplicationController controller = ApplicationSettings.getApplicationController();
        petriNetController = controller.getActivePetriNetController();

        table = new JTable(new TableModel());
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);
        table.setDefaultRenderer(Color.class, new ColorDrawer(true));
        table.setDefaultEditor(Color.class, new ColorPicker());

        add(scrollPane);
    }
    private static void displayGUI() {
        JFrame frame = new JFrame("Tokens");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel tablePane = new TokenPanel();
        tablePane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tablePane.setOpaque(true);

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(new JButton(""));
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(new JButton());

        // frame.setContentPane(tablePane);
        /*
		 * Container container = new Container(); container.add(tablePane,
		 * BorderLayout.CENTER); container.add(buttonPane,
		 * BorderLayout.PAGE_END); container.setVisible(true);
		 *///
        frame.add(tablePane, BorderLayout.CENTER);
        frame.add(buttonPane, BorderLayout.PAGE_END);

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                displayGUI();
            }
        });
    }

    public class TableModel extends AbstractTableModel {

        private final String[] columnNames = {"Enabled", "Token Name",
                "Token Colour",};

        private final Object[][] data = new Object[DATA_SIZE][3];;
        private static final int DATA_SIZE = 100;// Default is a size of 100 different
        // tokens as defined in
        // constructor
        final static int ENABLED_COL = 0;
        final static int NAME_COL = 1;
        final static int COLOR_COL = 2;


        public TableModel() {
            setRowColors();

            int index = 0;
            for (Token token : petriNetController.getNetTokens()) {
                Object[] tokenClass = {
                        token.isEnabled(),
                        token.getId(),
                        token.getColor()};
                data[index] = tokenClass;
                index++;
            }
        }

        /**
         * Sets the first 6 rows as basic different colors. The rest of the rows are assigned
         * to a random color
         */
        private void setRowColors() {

            final Random randomNumberGenerator = new Random();
            for (int i = 0; i < DATA_SIZE; i++) {
                // Set rows 0-6 as the basic different colours and the rest as
                // a random colour
                data[i][ENABLED_COL] = Boolean.FALSE;
                data[i][NAME_COL] = "";
                switch (i) {
                    case 0:
                        data[i][COLOR_COL] = Color.black;
                        break;
                    case 1:
                        data[i][COLOR_COL] = Color.RED;
                        break;
                    case 2:
                        data[i][COLOR_COL] = Color.BLUE;
                        break;
                    case 3:
                        data[i][COLOR_COL] = Color.YELLOW;
                        break;
                    case 4:
                        data[i][COLOR_COL] = Color.GREEN;
                        break;
                    case 5:
                        data[i][COLOR_COL] = Color.ORANGE;
                        break;
                    case 6:
                        data[i][COLOR_COL] = Color.PINK;
                        break;
                    default:
                        data[i][COLOR_COL] = new Color(randomNumberGenerator.nextInt(256),
                                randomNumberGenerator.nextInt(256), randomNumberGenerator.nextInt(256));
                }
            }
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return data.length;
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            return data[row][col];
        }

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
            return true;
        }


        public boolean isValid() {
            boolean enabledRowFound = false;
            for (Object[] row : data) {
                if ((Boolean) row[ENABLED_COL] && row[NAME_COL].equals("")) {
                    JOptionPane.showMessageDialog(new JPanel(), "The token name cannot be empty", "Warning", JOptionPane.WARNING_MESSAGE);
                    return false;
                }

                if ((Boolean) row[ENABLED_COL])
                    enabledRowFound = true;
            }
            if (!enabledRowFound) {
                JOptionPane.showMessageDialog(new JPanel(), "At least one token must be enabled", "Warning", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            return true;
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            if (col == ENABLED_COL) { // The enabled column has been changed
                if ((Boolean) value) {
                    for (int i = 0; i < DATA_SIZE; i++) {
                        if (i != row && (Boolean) data[i][ENABLED_COL]) {
                            if (data[i][NAME_COL]
                                    .equals(data[row][NAME_COL])) {
                                JOptionPane
                                        .showMessageDialog(
                                                new JPanel(),
                                                "Another token exists with that name",
                                                "Warning",
                                                JOptionPane.WARNING_MESSAGE);
                                return;
                            }
                        }
                    }

                }
            } else if (col == NAME_COL) { // The name column has been changed

                if ((Boolean) data[row][ENABLED_COL]) {
                    for (int i = 0; i < DATA_SIZE; i++) {
                        if (i != row && (Boolean) data[i][ENABLED_COL]) {
                            if (data[i][NAME_COL]
                                    .equals(value)) {
                                JOptionPane
                                        .showMessageDialog(
                                                new JPanel(),
                                                "Another token exists with that name",
                                                "Warning",
                                                JOptionPane.WARNING_MESSAGE);
                                return;
                            }
                        }
                    }
                }
            }
            for (Token token : petriNetController.getNetTokens()) {
                if (token.getId().equals(data[row][NAME_COL])) {
                    if (token.isLocked()) {
                        JOptionPane
                                .showMessageDialog(
                                        new JPanel(),
                                        "Places exist that use this token. "
                                                + "Such markings must be removed before this class can be edited",
                                        "Warning",
                                        JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }


            String modifiedTokenName = (String) data[row][NAME_COL];
            data[row][col] = value;
            if (isExistingToken(row)) {
                updateToken(modifiedTokenName, row, col);
            } else {
                createNewToken(row);
            }
            fireTableCellUpdated(row, col);
        }

        /**
         * Creates new token with data at row
         *
         * @param row
         */
        private void createNewToken(int row) {
            Boolean enabled = (Boolean) data[row][ENABLED_COL];
            String name = (String) data[row][NAME_COL];
            Color color = (Color) data[row][COLOR_COL];
            petriNetController.createNewToken(name, enabled, color);
        }

        /**
         * @param row
         */
        private void updateToken(String tokenName, int row, int col) {
            Boolean enabled = (Boolean) data[row][ENABLED_COL];
            String name = (String) data[row][NAME_COL];
            Color color = (Color) data[row][COLOR_COL];
            petriNetController.updateToken(tokenName, name, enabled, color);
        }

        /**
         * @param row
         * @return true if the row being edited is an existing token row
         */
        private boolean isExistingToken(int row) {
            Collection<Token> tokens = petriNetController.getNetTokens();
            return row < tokens.size();
        }
    }


}