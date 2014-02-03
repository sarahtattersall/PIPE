package pipe.gui;

import pipe.controllers.PetriNetController;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.Collection;
import java.util.Map;
import java.util.Random;

/**
 * @author Alex Charalambous, June 2010: ColorDrawer, ColorPicker,
 *         TokenPanel and TokenDialog are four classes used to display
 *         the Token Classes dialog (accessible through the button toolbar).
 */

public class TokenPanel extends JPanel {
    private final TableModel model;

    private JTable table;


    public TokenPanel(PetriNetController petriNetController) {
        model = new TableModel(petriNetController);
        table = new JTable(model);

        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);
        table.setDefaultRenderer(Color.class, new ColorDrawer(true));
        table.setDefaultEditor(Color.class, new ColorPicker());

        add(scrollPane);
    }

    public void setChanges() {
        model.setChanges();
    }

    public boolean isDataValid() {
        return model.isValid();
    }

    /**
     * TableModel for editing the Tokens
     */
    public class TableModel extends AbstractTableModel {

        /**
         * The default number of tokens to display
         */
        private static final int DATA_SIZE = 100;

        private final static int ENABLED_COL = 0;

        private final static int NAME_COL = 1;

        private final static int COLOR_COL = 2;

        private final String[] columnNames = {"Enabled", "Token Name Details", "Token Colour",};

        private final Datum[] initialData;

        private final Datum[] modifiedData = new Datum[DATA_SIZE];

        private final PetriNetController petriNetController;


        public TableModel(PetriNetController petriNetController) {
            this.petriNetController = petriNetController;
            initialiseRowColours();
            initialData = new Datum[petriNetController.getNetTokens().size()];
            int index = 0;
            for (Token token : petriNetController.getNetTokens()) {
                initialData[index] = new Datum(token.isEnabled(), token.getId(), token.getColor());
                modifiedData[index] = new Datum(token.isEnabled(), token.getId(), token.getColor());
                index++;
            }
        }

        /**
         * Sets the first 6 rows as basic different colors. The rest of the rows are assigned
         * to a random color
         */
        private void initialiseRowColours() {

            Random randomNumberGenerator = new Random();
            for (int i = 0; i < DATA_SIZE; i++) {
                Color color;
                switch (i) {
                    case 0:
                        color = Color.black;
                        break;
                    case 1:
                        color = Color.RED;
                        break;
                    case 2:
                        color = Color.BLUE;
                        break;
                    case 3:
                        color = Color.YELLOW;
                        break;
                    case 4:
                        color = Color.GREEN;
                        break;
                    case 5:
                        color = Color.ORANGE;
                        break;
                    case 6:
                        color = Color.PINK;
                        break;
                    default:
                        color = new Color(randomNumberGenerator.nextInt(256), randomNumberGenerator.nextInt(256),
                                randomNumberGenerator.nextInt(256));
                }
                modifiedData[i] = new Datum(color);
            }
        }

        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }

        @Override
        public Class<?> getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return true;
        }

        /**
         * Sets modifiedData with the new value
         * Also performs checks on the new value and displays
         * warnings if it is likely to be invalid
         *
         * @param value new value of the changed cell
         * @param row   row that has changed
         * @param col   column that has changed
         */
        @Override
        public void setValueAt(Object value, int row, int col) {
            boolean enabled = modifiedData[row].isEnabled;
            String name = modifiedData[row].name;
            Color color = modifiedData[row].color;

            if (col == ENABLED_COL) { // The enabled column has been changed
                enabled = (Boolean) value;
                if ((Boolean) value) {
                    for (int i = 0; i < DATA_SIZE; i++) {
                        if (i != row && modifiedData[i].isEnabled) {
                            if (modifiedData[i].name.equals(modifiedData[row].name)) {
                                JOptionPane.showMessageDialog(new JPanel(), "Another token exists with that name",
                                        "Warning", JOptionPane.WARNING_MESSAGE);
                                return;
                            }
                        }
                    }

                }
            } else if (col == NAME_COL) { // The name column has been changed
                name = (String) value;
                if (modifiedData[row].isEnabled) {
                    for (int i = 0; i < DATA_SIZE; i++) {
                        if (i != row && modifiedData[i].isEnabled) {
                            if (modifiedData[i].name.equals(value)) {
                                JOptionPane.showMessageDialog(new JPanel(), "Another token exists with that name",
                                        "Warning", JOptionPane.WARNING_MESSAGE);
                                return;
                            }
                        }
                    }
                }
            } else {
                color = (Color) value;
            }


            //TODO: DO THIS IN A BETTER WAY
            if (row < initialData.length) {
                Datum initial = initialData[row];
                String originalTokenName = initial.name;
                for (Place place : petriNetController.getPetriNet().getPlaces()) {
                    for (Map.Entry<Token, Integer> entry : place.getTokenCounts().entrySet()) {
                        if (entry.getKey().getId().equals(originalTokenName)) {
                            if (entry.getValue() > 0) {
                                JOptionPane.showMessageDialog(new JPanel(), "Places exist that use this token. "
                                        + "Such markings must be removed before this class can be edited", "Warning",
                                        JOptionPane.WARNING_MESSAGE);
                                return;
                            }
                        }
                    }
                }
            }

            modifiedData[row].isEnabled = enabled;
            modifiedData[row].name = name;
            modifiedData[row].color = color;

            fireTableCellUpdated(row, col);
        }

        /**
         * Applies the changes to the actual model
         */
        public void setChanges() {
            for (int row = 0; row < getRowCount(); row++) {
                Datum modified = modifiedData[row];
                if (isExistingToken(row)) {
                    Datum initial = initialData[row];
                    if (!modified.equals(initial) && modified.hasBeenSet()) {
                        petriNetController.updateToken(initial.name, modified.name, modified.isEnabled, modified.color);
                    }
                } else if (modified.hasBeenSet()) {
                    petriNetController.createNewToken(modified.name, modified.isEnabled, modified.color);
                }
            }
        }

        /**
         * @param row
         * @return true if the row being edited is an existing token row
         */
        private boolean isExistingToken(int row) {
            Collection<Token> tokens = petriNetController.getNetTokens();
            return row < tokens.size();
        }

        @Override
        public int getRowCount() {
            return modifiedData.length;
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int row, int col) {
            if (col == ENABLED_COL) {
                return modifiedData[row].isEnabled;
            }
            if (col == NAME_COL) {
                return modifiedData[row].name;
            } else {
                return modifiedData[row].color;
            }
        }

        /**
         * Checks to see if all the tokens in the table are valid.
         * If they are not it will print the relevant error message for the first
         * invalid token it comes across.
         *
         * @return true if all the tokens in the table are valid, false if not
         */
        public boolean isValid() {
            boolean enabledRowFound = false;
            for (Datum datum : modifiedData) {
                if (datum.isEnabled && datum.name.equals("")) {
                    JOptionPane.showMessageDialog(new JPanel(), "The token name cannot be empty", "Warning",
                            JOptionPane.WARNING_MESSAGE);
                    return false;
                }

                if (datum.isEnabled) {
                    enabledRowFound = true;
                }
            }
            if (!enabledRowFound) {
                JOptionPane.showMessageDialog(new JPanel(), "At least one token must be enabled", "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return false;
            }
            return true;
        }


    }

    /**
     * Private class to hold the data in the table.
     */
    private class Datum {
        public boolean isEnabled;

        public String name;

        public Color color;

        public Datum(Color color) {
            isEnabled = false;
            this.name = "";
            this.color = color;
        }

        public Datum(boolean isEnabled, String name, Color color) {
            this.isEnabled = isEnabled;
            this.name = name;
            this.color = color;
        }

        public boolean hasBeenSet() {
            return !this.name.equals("");
        }

        @Override
        public int hashCode() {
            int result = (isEnabled ? 1 : 0);
            result = 31 * result + (name != null ? name.hashCode() : 0);
            result = 31 * result + (color != null ? color.hashCode() : 0);
            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Datum datum = (Datum) o;

            if (isEnabled != datum.isEnabled) {
                return false;
            }
            if (color != null ? !color.equals(datum.color) : datum.color != null) {
                return false;
            }
            if (name != null ? !name.equals(datum.name) : datum.name != null) {
                return false;
            }

            return true;
        }
    }

}