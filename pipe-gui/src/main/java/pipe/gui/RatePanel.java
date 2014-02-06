package pipe.gui;

import pipe.controllers.PetriNetController;
import pipe.exceptions.PetriNetComponentNotFound;
import pipe.models.component.rate.RateParameter;
import pipe.utilities.gui.GuiUtils;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.Collection;

public class RatePanel extends JPanel {
    private final JTable table;

    private final RateModel model;

    public RatePanel(PetriNetController petriNetController) {
        model = new RateModel(petriNetController);
        table = new JTable(model);

        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);
    }

    public void setChanges() {
        model.setChanges();
    }

    public boolean isDataValid() {
        return model.isValid();
    }

    private class RateModel extends AbstractTableModel {

        private final String[] COLUMN_NAMES = {"Name", "Value"};

        private final int DATA_SIZE = 100;

        private final Datum[] initialData;

        private final Datum[] modifiedData = new Datum[DATA_SIZE];

        private final int ID_COL = 0;

        private final int VALUE_COL = 1;

        private final PetriNetController petriNetController;

        public RateModel(PetriNetController petriNetController) {
            this.petriNetController = petriNetController;

            initialData = new Datum[petriNetController.getRateParameters().size()];
            int index = 0;
            for (RateParameter rateParameter : petriNetController.getRateParameters()) {
                initialData[index] = new Datum(rateParameter.getId(), rateParameter.getExpression());
                modifiedData[index] = new Datum(rateParameter.getId(), rateParameter.getExpression());
                index++;
            }
            for (;index < modifiedData.length; index++) {
                modifiedData[index] = new Datum("", "");
            }
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return true;
        }

        @Override
        public void setValueAt(Object value, int rowIndex, int colIndex) {
            String id = modifiedData[rowIndex].id;
            String expression = modifiedData[rowIndex].expression;

            if (colIndex == ID_COL) {
                id = (String) value;
            } else {
                expression = (String) value;
            }

            modifiedData[rowIndex].id = id;
            modifiedData[rowIndex].expression = expression;

            fireTableCellUpdated(rowIndex, colIndex);
        }


        /**
         * @param row
         * @return true if the row being edited is an existing token row
         */
        private boolean isExistingRateParameter(int row) {
            Collection<RateParameter> rateParameters = petriNetController.getRateParameters();
            return row < rateParameters.size();
        }

        @Override
        public String getColumnName(int col) {
            return COLUMN_NAMES[col];
        }


        @Override
        public int getRowCount() {
            return modifiedData.length;
        }

        @Override
        public int getColumnCount() {
            return COLUMN_NAMES.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == ID_COL) {
                return modifiedData[rowIndex].id;
            }
            return modifiedData[rowIndex].expression;
        }

        public boolean isValid() {
            for (int row = 0; row < getRowCount(); row++) {
                Datum datum = modifiedData[row];
                if (isExistingRateParameter(row)) {
                    if (datum.id.isEmpty()) {
                        showWarning("The token name cannot be empty");
                        return false;
                    }
                } else if (datum.id.isEmpty() && !datum.expression.isEmpty())  {
                    showWarning("The token name cannot be empty");
                    return false;
                } else if (!datum.id.isEmpty() && datum.expression.isEmpty()) {
                    showWarning("The rate cannot be empty");
                    return false;
                }
            }
            return true;
        }

        private void showWarning(String warningMessage) {
            JOptionPane.showMessageDialog(new JPanel(), warningMessage, "Warning",
                    JOptionPane.WARNING_MESSAGE);
        }

        /**
         * Applies the changes to the actual model
         */
        public void setChanges() {
            for (int row = 0; row < getRowCount(); row++) {
                Datum modified = modifiedData[row];
                if (isExistingRateParameter(row)) {
                    Datum initial = initialData[row];
                    if (!modified.equals(initial) && modified.hasBeenSet()) {
                        try {
                            petriNetController.updateRateParameter(initial.id, modified.id, modified.expression);
                        } catch (PetriNetComponentNotFound petriNetComponentNotFound) {
                            GuiUtils.displayErrorMessage(null, petriNetComponentNotFound.getMessage());
                        }
                    }
                } else if (modified.hasBeenSet()) {
                    petriNetController.createNewRateParameter(modified.id, modified.expression);
                }
            }
        }


        private class Datum {
            public String id;

            public String expression;


            private Datum(String id, String expression) {
                this.id = id;
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
