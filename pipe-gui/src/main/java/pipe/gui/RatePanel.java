package pipe.gui;

import pipe.controllers.PetriNetController;
import pipe.exceptions.InvalidRateException;
import pipe.exceptions.PetriNetComponentNotFoundException;
import pipe.models.component.rate.RateParameter;
import pipe.utilities.gui.GuiUtils;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

/**
 * This is a panel for modifying and creating rate parameters
 */
public class RatePanel extends JPanel {
    private final JTable table;

    private final RateModel model;

    public RatePanel(PetriNetController petriNetController) {
        model = new RateModel(petriNetController);
        table = new JTable(model);

        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setFillsViewportHeight(true);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selected = table.getSelectedRow();
                model.deleteRow(selected);
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);
        add(deleteButton);
    }

    public List<RateModel.Datum> getTableData() {
        return model.getTableData();
    }

    public boolean isDataValid() {
        return model.isValid();
    }

    public boolean isExistingRateParameter(RateModel.Datum datum) {
        return model.isExistingRateParameter(datum);
    }

    public Iterable<RateModel.Datum> getDeletedData() {
        return model.deletedData;
    }

    public class RateModel extends AbstractTableModel {

        /**
         * Names of columsn to appear in order
         */
        private final String[] COLUMN_NAMES = {"Name", "Value"};

        /**
         * Maximum number of items to be shown
         */
        private final int DATA_SIZE = 100;

        /**
         * Data in the table once it has been modified.
         */
        private final List<Datum> modifiedData = new ArrayList<>();

        /**
         * Data that has been deleted from the table
         */
        private final Collection<Datum> deletedData = new HashSet<>();

        private final int ID_COL = 0;

        private final int VALUE_COL = 1;

        private final PetriNetController petriNetController;

        public RateModel(PetriNetController petriNetController) {
            this.petriNetController = petriNetController;

            for (RateParameter rateParameter : petriNetController.getRateParameters()) {
                Datum initial = new Datum(rateParameter.getId(), rateParameter.getExpression());
                modifiedData.add(new Datum(initial, rateParameter.getId(), rateParameter.getExpression()));
            }
            for (int index = modifiedData.size(); index < DATA_SIZE; index++) {
                modifiedData.add(new Datum("", ""));
            }
        }

        @Override
        public String getColumnName(int col) {
            return COLUMN_NAMES[col];
        }

        /**
         *
         * @param row
         * @param col
         * @return true for all cells
         */
        @Override
        public boolean isCellEditable(int row, int col) {
            return true;
        }

        /**
         * Updates the table the values added.
         * @param value new value
         * @param rowIndex index of row changed
         * @param colIndex index of column changed
         */
        @Override
        public void setValueAt(Object value, int rowIndex, int colIndex) {
            String id = modifiedData.get(rowIndex).id;
            String expression = modifiedData.get(rowIndex).expression;

            if (colIndex == ID_COL) {
                id = (String) value;
            } else {
                expression = (String) value;
            }

            modifiedData.get(rowIndex).id = id;
            modifiedData.get(rowIndex).expression = expression;

            fireTableCellUpdated(rowIndex, colIndex);
        }

        /**
         *
         * Checks the following conditions:
         * - Name is not empty
         * - Rate value is not empty
         *
         * @return true if the values in the table are valid for each rate parameter
         */
        public boolean isValid() {
            for (int row = 0; row < getRowCount(); row++) {
                Datum datum = modifiedData.get(row);
                if (isExistingRateParameter(datum)) {
                    if (datum.id.isEmpty()) {
                        showWarning("The rate name cannot be empty");
                        return false;
                    }
                } else if (datum.id.isEmpty() && !datum.expression.isEmpty()) {
                    showWarning("The rate name cannot be empty");
                    return false;
                } else if (!datum.id.isEmpty() && datum.expression.isEmpty()) {
                    showWarning("The rate value cannot be empty");
                    return false;
                }
            }
            return true;
        }

        /**
         * Shows a warning to the user as a dialog
         * @param warningMessage message to show
         */
        private void showWarning(String warningMessage) {
            JOptionPane.showMessageDialog(new JPanel(), warningMessage, "Warning", JOptionPane.WARNING_MESSAGE);
        }

        @Override
        public int getRowCount() {
            return modifiedData.size();
        }

        @Override
        public int getColumnCount() {
            return COLUMN_NAMES.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == ID_COL) {
                return modifiedData.get(rowIndex).id;
            }
            return modifiedData.get(rowIndex).expression;
        }

        /**
         * @param datum
         * @return true if the row being edited is an existing rate parameter in the Petri net
         */
        private boolean isExistingRateParameter(Datum datum) {
            return datum.initial != null;
        }

        public List<Datum> getTableData() {
            return modifiedData;
        }

        public void deleteRow(int row) {
            deletedData.add(modifiedData.get(row));
            modifiedData.remove(row);
            fireTableRowsDeleted(row, row);
        }

        /**
         * Holds rate parameter information in the table
         */
        public class Datum {
            public String id;

            public String expression;

            /**
             * Mapping to an initial datum item
             * This may be null if the Datum was not originally a rate parameter in the Petri net
             * <p/>
             * It will contain a value if the data is a modified datum
             * and it maps directly to some initial datum
             */
            public Datum initial = null;


            private Datum(String id, String expression) {
                this.id = id;
                this.expression = expression;
            }

            private Datum(Datum initial, String id, String expression) {
                this.id = id;
                this.expression = expression;
                this.initial = initial;
            }

            public boolean hasBeenSet() {
                return !this.id.equals("");
            }

            @Override
            public int hashCode() {
                int result = id != null ? id.hashCode() : 0;
                result = 31 * result + (expression != null ? expression.hashCode() : 0);
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

                if (expression != null ? !expression.equals(datum.expression) : datum.expression != null) {
                    return false;
                }
                if (id != null ? !id.equals(datum.id) : datum.id != null) {
                    return false;
                }

                return true;
            }
        }
    }

}
