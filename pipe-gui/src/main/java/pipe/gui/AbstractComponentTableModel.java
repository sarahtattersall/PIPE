package pipe.gui;

import javax.swing.table.AbstractTableModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @param <D> table row datum item
 */
@SuppressWarnings("serial")
public abstract class AbstractComponentTableModel<D extends AbstractDatum> extends AbstractTableModel {

    /**
     * Number of non-empty rows
     */
    protected int count = 0;

    /**
     * Maximum number of items to be shown
     */
    protected static final int DATA_SIZE = 100;


    /**
     * Data in the table once it has been modified.
     */
    protected final List<D> modifiedData = new ArrayList<>();


    /**
     * Data that has been deleted from the table
     */
    protected final Collection<D> deletedData = new HashSet<>();

    /**
     * Names of columns in table to appear in order
     */
    protected String[] columnNames;


    /**
     *
     * @param col column 
     * @return the name for the column
     */
    @Override
    public final String getColumnName(int col) {
        return columnNames[col];
    }


    /**
     *
     * @param row of the table
     * @param col column of the table
     * @return true for all cells
     */
    @Override
    public final boolean isCellEditable(int row, int col) {
        return true;
    }

    /**
     *
     * @return number of rows
     */
    @Override
    public final int getRowCount() {
        return modifiedData.size();
    }

    /**
     *
     * @return number of columns
     */
    @Override
    public final int getColumnCount() {
        return columnNames.length;
    }

    /**
     * @param datum datum to check in model
     * @return true if the row being edited is an existing component in the Petri net
     */
    protected final boolean isExistingDatum(D datum) {
        return datum.initial != null;
    }

    /**
     *
     * @return table data
     */
    public final List<D> getTableData() {
        return modifiedData;
    }

    /**
     * Deletes the row from the model
     * @param row of the table 
     */
    public final void deleteRow(int row) {
        if (isExistingDatum(modifiedData.get(row))) {
            deletedData.add(modifiedData.get(row));
            count--;
        }
        modifiedData.remove(row);
        fireTableRowsDeleted(row, row);
    }

    /**
     *
     * @return all deleted rows
     */
    public final Collection<D> getDeletedData() {
        return deletedData;
    }

    /**
     *
     * Updates the value in the table a (rowIndex, colIndex) with value
     *
     * @param value of the cell
     * @param rowIndex index in row
     * @param colIndex index in column
     */
    @Override
    public final void setValueAt(Object value, int rowIndex, int colIndex) {
        D datum = modifiedData.get(rowIndex);

        updateTableAt(value, rowIndex, colIndex);

        if (!isExistingDatum(datum) && !datum.id.isEmpty()) {
            count++;
        }
        fireTableCellUpdated(rowIndex, colIndex);
    }

    /**
     *
     * Inherited method for updating the actual item
     *
     * @param value of the cell 
     * @param rowIndex index in row
     * @param colIndex index in column 
     */
    protected abstract void updateTableAt(Object value, int rowIndex, int colIndex);

}
