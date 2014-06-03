package pipe.gui;

import pipe.controllers.PetriNetController;
import uk.ac.imperial.pipe.models.petrinet.Token;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * @author Alex Charalambous, June 2010: ColorDrawer, ColorPicker,
 *         TokenPanel and TokenDialog are four classes used to display
 *         the Token Classes dialog (accessible through the button toolbar).
 */

public class TokenEditorPanel extends JPanel {
    private final TableModel model;


    public TokenEditorPanel(final PetriNetController petriNetController) {
        model = new TableModel(petriNetController);
        final JTable table = new JTable(model);

        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setFillsViewportHeight(true);

        table.setDefaultRenderer(Color.class, new ColorDrawer(true));
        table.setDefaultEditor(Color.class, new ColorPicker());

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            /**
             * Only delete data if it is not the last token left!
             * @param e
             */
            //TODO: This does not take into account adding new tokens before pressing ok
            //       make sure it does....
            @Override
            public void actionPerformed(ActionEvent e) {
                int selected = table.getSelectedRow();
                if (model.count > 1) {
                    model.deleteRow(selected);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);
        add(deleteButton);
    }

    public boolean isDataValid() {
        return model.isValid();
    }

    public List<Datum> getTableData() {
        return model.getTableData();
    }

    public Collection<Datum> getDeletedData() {
        return model.getDeletedData();
    }

    public boolean isExistingDatum(Datum datum) {
        return model.isExistingDatum(datum);
    }

    /**
     * Private class to hold the data in the table.
     */
    public static class Datum extends AbstractDatum {

        /**
         * Color of the token
         */
        public Color color;

        public Datum(String name, Color color) {
            super(name);
            this.color = color;
        }

        public Datum(Datum initial, String name, Color color) {
            super(initial, name);
            this.color = color;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Datum)) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }

            Datum datum = (Datum) o;

            if (!color.equals(datum.color)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + color.hashCode();
            return result;
        }
    }

    /**
     * TableModel for editing the Tokens
     */
    public static class TableModel extends AbstractComponentTableModel<Datum> {

        private static final int NAME_COL = 0;

        public TableModel(PetriNetController petriNetController) {
            columnNames = new String[]{"Token Name Details", "Token Colour"};
            for (Token token : petriNetController.getNetTokens()) {
                Datum initial = new Datum(token.getId(), token.getColor());
                modifiedData.add(new Datum(initial, token.getId(), token.getColor()));
                count++;
            }
            initialiseEmptyRowColours(modifiedData.size());
        }

        /**
         * Sets the first 6 rows as basic different colors. The rest of the rows are assigned
         * to a random color
         */
        private void initialiseEmptyRowColours(int startIndex) {

            Random randomNumberGenerator = new Random();
            for (int i = startIndex; i < DATA_SIZE; i++) {
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
                modifiedData.add(new Datum("", color));
            }
        }

        @Override
        public Class<?> getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        @Override
        public Object getValueAt(int row, int col) {
            if (col == NAME_COL) {
                return modifiedData.get(row).id;
            }
            return modifiedData.get(row).color;

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
        public void updateTableAt(Object value, int row, int col) {
            String id = modifiedData.get(row).id;
            Color color = modifiedData.get(row).color;

            if (col == NAME_COL) { // The name column has been changed
                id = (String) value;
                for (int i = 0; i < modifiedData.size(); i++) {
                    if (i != row && modifiedData.get(i).id.equals(value)) {
                            JOptionPane.showMessageDialog(new JPanel(), "Another token exists with that name",
                                    "Warning", JOptionPane.WARNING_MESSAGE);
                            return;
                    }
                }
            } else {
                color = (Color) value;
            }


            //            //TODO: DO THIS IN A BETTER WAY
            //            if (row < initialData.length) {
            //                Datum initial = initialData[row];
            //                String originalTokenName = initial.name;
            //                for (Place place : petriNetController.getPetriNet().getPlaces()) {
            //                    for (Map.Entry<Token, Integer> entry : place.getTokenCounts().entrySet()) {
            //                        if (entry.getKey().getId().equals(originalTokenName)) {
            //                            if (entry.getValue() > 0) {
            //                                JOptionPane.showMessageDialog(new JPanel(), "Places exist that use this token. "
            //                                        + "Such markings must be removed before this class can be edited", "Warning",
            //                                        JOptionPane.WARNING_MESSAGE);
            //                                return;
            //                            }
            //                        }
            //                    }
            //                }
            //            }

            modifiedData.get(row).id = id;
            modifiedData.get(row).color = color;
        }

        /**
         * Applies the changes to the actual model
         */
        public void setChanges() {

        }

        /**
         * Checks to see if all the tokens in the table are valid.
         * If they are not it will print the relevant error message for the first
         * invalid token it comes across.
         *
         * @return true if all the tokens in the table are valid, false if not
         */
        public boolean isValid() {
            return true;
        }
    }

}