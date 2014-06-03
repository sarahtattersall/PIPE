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
public abstract class AbstractComponentTableModel<D extends AbstractDatum> extends AbstractTableModel {

    /**
     * Number of non-empty rows
     */
    protected int count = 0;

    /**
     * Maximum number of items to be shown
     */
    protected final static int DATA_SIZE = 100;


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
    protected String[] COLUMN_NAMES;


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

    @Override
    public int getRowCount() {
        return modifiedData.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    /**
     * @param datum datum to check in model
     * @return true if the row being edited is an existing component in the Petri net
     */
    protected boolean isExistingDatum(D datum) {
        return datum.initial != null;
    }

    public List<D> getTableData() {
        return modifiedData;
    }

    public void deleteRow(int row) {
        if (isExistingDatum(modifiedData.get(row))) {
            deletedData.add(modifiedData.get(row));
            count--;
        }
        modifiedData.remove(row);
        fireTableRowsDeleted(row, row);
    }

    public Collection<D> getDeletedData() {
        return deletedData;
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int colIndex) {
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
     * @param value
     * @param rowIndex
     * @param colIndex
     */
    protected  abstract void updateTableAt(Object value, int rowIndex, int colIndex);

}
