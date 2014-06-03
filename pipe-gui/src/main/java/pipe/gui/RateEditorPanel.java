package pipe.gui;

import pipe.controllers.PetriNetController;
import uk.ac.imperial.pipe.models.petrinet.RateParameter;
import uk.ac.imperial.pipe.parsers.FunctionalResults;

import javax.swing.*;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * This is a panel for modifying and creating rate parameters
 */
public class RateEditorPanel extends JPanel {
    private final JTable table;

    private final RateModel model;

    public RateEditorPanel(PetriNetController petriNetController) {
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
        return model.isExistingDatum(datum);
    }

    public Iterable<RateModel.Datum> getDeletedData() {
        return model.deletedData;
    }

    public class RateModel extends AbstractComponentTableModel<RateModel.Datum> {


        private static final int ID_COL = 0;

        private static final int VALUE_COL = 1;

        private final PetriNetController petriNetController;


        public RateModel(PetriNetController petriNetController) {
            this.petriNetController = petriNetController;
            COLUMN_NAMES = new String[]{"Name", "Value"};
            for (RateParameter rateParameter : petriNetController.getRateParameters()) {
                Datum initial = new Datum(rateParameter.getId(), rateParameter.getExpression());
                modifiedData.add(new Datum(initial, rateParameter.getId(), rateParameter.getExpression()));
                count++;
            }
            for (int index = modifiedData.size(); index < DATA_SIZE; index++) {
                modifiedData.add(new Datum("", ""));
            }
        }

        /**
         * Updates the table the values added.
         * @param value new value
         * @param rowIndex index of row changed
         * @param colIndex index of column changed
         */
        @Override
        public void updateTableAt(Object value, int rowIndex, int colIndex) {

            String id = modifiedData.get(rowIndex).id;
            String expression = modifiedData.get(rowIndex).expression;

            if (colIndex == ID_COL) {
                id = (String) value;
            } else {
                expression = (String) value;
            }

            modifiedData.get(rowIndex).id = id;
            modifiedData.get(rowIndex).expression = expression;
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
                    if (!isValidRate(datum.id, datum.expression)) {
                        return false;
                    }
                }
                if (datum.id.isEmpty() && !datum.expression.isEmpty()) {
                    showWarning("The rate name cannot be empty");
                    return false;
                }
                if (!datum.id.isEmpty() && datum.expression.isEmpty()) {
                    showWarning("The rate value cannot be empty");
                    return false;
                }
                if (!datum.id.isEmpty()) {
                    if (!isValidRate(datum.id, datum.expression)) {
                        return false;
                    }
                }
            }
            return true;
        }

        /**
         *
         * Pops up an error if the rate is invalid
         * @param rate
         * @return if rate is valid or not
         */
        private boolean isValidRate(String id, String rate) {
            FunctionalResults<Double> results = petriNetController.parseFunctionalExpression(rate);
            if (results.hasErrors()) {
                showWarning("Error! Invalid rate for: " + id + "\n" + "Problem is: " + results.getErrorString(", "));
                return false;
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
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == ID_COL) {
                return modifiedData.get(rowIndex).id;
            }
            return modifiedData.get(rowIndex).expression;
        }

        /**
         * Holds rate parameter information in the table
         */
        public class Datum extends AbstractDatum {
            public String expression;

            private Datum(String id, String expression) {
                super(id);
                this.expression = expression;
            }

            private Datum(Datum initial, String id, String expression) {
                super(initial, id);
                this.expression = expression;
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
