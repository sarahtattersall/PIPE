package pipe.gui;

import pipe.views.TokenView;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.LinkedList;
import java.util.Random;

/**
 * @author Alex Charalambous, June 2010: ColorDrawer, ColorPicker,
 *         TokenPanel and TokenDialog are four classes used to display
 *         the Token Classes dialog (accessible through the button toolbar).
 */

public class TokenPanel extends JPanel {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public final JTable table;
	private final LinkedList<TokenView> _tokenViews;

	public TokenPanel() {
		// super(new GridLayout(1,0));
        this._tokenViews = ApplicationSettings.getApplicationView().getCurrentPetriNetView().getTokenViews();
		table = new JTable(new TableModel());
		table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		table.setFillsViewportHeight(true);

		JScrollPane scrollPane = new JScrollPane(table);
		table.setDefaultRenderer(Color.class, new ColorDrawer(true));
		table.setDefaultEditor(Color.class, new ColorPicker());

		add(scrollPane);
	}

	public class TableModel extends AbstractTableModel {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		private final String[] columnNames = { "Enabled", "Token Name",
				"Token Colour", };

		private final Object[][] data;
		private static final int dataSize = 100;// Default is a size of 100 different
		// tokens as defined in
		// constructor
		final static int enabledCol = 0;
		final static int nameCol = 1;
		final static int colorCol = 2;

		public TableModel() {
			super();
			data = new Object[dataSize][3];
			Random generator = new Random();
			for (int i = 0; i < dataSize; i++) {
				// Set rows 0-6 as the basic different colours and the rest as
				// a random colour
				switch (i) {
				case 0:
					data[i][enabledCol] = Boolean.FALSE;
					data[i][nameCol] = "";
					data[i][colorCol] = Color.black;
					break;
				case 1:
					data[i][enabledCol] = Boolean.FALSE;
					data[i][nameCol] = "";
					data[i][colorCol] = Color.RED;
					break;
				case 2:
					data[i][enabledCol] = Boolean.FALSE;
					data[i][nameCol] = "";
					data[i][colorCol] = Color.BLUE;
					break;
				case 3:
					data[i][enabledCol] = Boolean.FALSE;
					data[i][nameCol] = "";
					data[i][colorCol] = Color.YELLOW;
					break;
				case 4:
					data[i][enabledCol] = Boolean.FALSE;
					data[i][nameCol] = "";
					data[i][colorCol] = Color.GREEN;
					break;
				case 5:
					data[i][enabledCol] = Boolean.FALSE;
					data[i][nameCol] = "";
					data[i][colorCol] = Color.ORANGE;
					break;
				case 6:
					data[i][enabledCol] = Boolean.FALSE;
					data[i][nameCol] = "";
					data[i][colorCol] = Color.PINK;
					break;
				default:
					data[i][enabledCol] = Boolean.FALSE;
					data[i][nameCol] = "";
					data[i][colorCol] = new Color(generator.nextInt(256),
							generator.nextInt(256), generator.nextInt(256));
				}
			}

			int noTokenClasses = _tokenViews.size();
			for (int i = 0; i < noTokenClasses; i++) {
				Object[] tokenClass = {
                        _tokenViews.get(i).isEnabled(),
                        _tokenViews.get(i).getID(),
                        _tokenViews.get(i).getColor()};
				data[i] = tokenClass;
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


        public boolean isValid()
        {
            boolean enabledRowFound = false;
            for(Object[] row : data)
            {
                if((Boolean)row[enabledCol] && row[nameCol].equals(""))
                {
                    JOptionPane.showMessageDialog(new JPanel(),"The token name cannot be empty", "Warning",JOptionPane.WARNING_MESSAGE);
                    return false;
                }

                if((Boolean)row[enabledCol])
                    enabledRowFound = true;
            }
            if(!enabledRowFound)
            {
                JOptionPane.showMessageDialog(new JPanel(), "At least one token must be enabled", "Warning", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            return true;
        }

		public void setValueAt(Object value, int row, int col) {
			boolean shouldChange = true;
			if (col == enabledCol) { // The enabled column has been changed
				if ((Boolean) value) {
					for (int i = 0; i < dataSize; i++) {
						if (i != row && (Boolean) data[i][enabledCol]) {
							if (data[i][nameCol]
									.equals(data[row][nameCol])) {
								shouldChange = false;
								JOptionPane
										.showMessageDialog(
												new JPanel(),
												"Another token exists with that name",
												"Warning",
												JOptionPane.WARNING_MESSAGE);
								break;
							}
						}
					}

				}
			} else if (col == nameCol) { // The name column has been changed

				if ((Boolean) data[row][enabledCol]) {
					for (int i = 0; i < dataSize; i++) {
						if (i != row && (Boolean) data[i][enabledCol]) {
							if (data[i][nameCol]
									.equals(value)) {
								shouldChange = false;
								JOptionPane
										.showMessageDialog(
												new JPanel(),
												"Another token exists with that name",
												"Warning",
												JOptionPane.WARNING_MESSAGE);
								break;
							}
						}
					}
				}
			}
			if (shouldChange) {
				for (TokenView tc : _tokenViews) {
					if (tc.getID().equals(data[row][nameCol])) {
						if (tc.isLocked()) {
							shouldChange = false;
							JOptionPane
									.showMessageDialog(
											new JPanel(),
											"Places exist that use this token. "
													+ "Such markings must be removed before this class can be edited",
											"Warning",
											JOptionPane.WARNING_MESSAGE);
							break;
						}
					}
				}
			}

			if (shouldChange) {
				data[row][col] = value;
				fireTableCellUpdated(row, col);
			}
		}
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
}