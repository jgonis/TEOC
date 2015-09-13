/********************************************************************************
 * The contents of this file are subject to the GNU General Public License      *
 * (GPL) Version 2 or later (the "License"); you may not use this file except   *
 * in compliance with the License. You may obtain a copy of the License at      *
 * http://www.gnu.org/copyleft/gpl.html                                         *
 *                                                                              *
 * Software distributed under the License is distributed on an "AS IS" basis,   *
 * without warranty of any kind, either expressed or implied. See the License   *
 * for the specific language governing rights and limitations under the         *
 * License.                                                                     *
 *                                                                              *
 * This file was originally developed as part of the software suite that        *
 * supports the book "The Elements of Computing Systems" by Nisan and Schocken, *
 * MIT Press 2005. If you modify the contents of this file, please document and *
 * mark your changes clearly, for the benefit of others.                        *
 ********************************************************************************/

package SimulatorsGUI;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import Hack.Gates.BuiltInGate;
import Hack.Gates.CompositeGate;
import Hack.Gates.Gate;
import Hack.HardwareSimulator.PartsGUI;
import HackGUI.Utilities;

/**
 * This class represents the gui of the parts component.
 */
public class PartsComponent extends JPanel implements PartsGUI {

	private static final long serialVersionUID = 2167548443728732840L;

	// An inner class representing the model of the parts table.
	class PartsTableModel extends AbstractTableModel {

		private static final long serialVersionUID = -7035425722364238863L;
		String[] columnNames = { "Chip Name", "Type", "Clocked" };

		/**
		 * Returns the the class of a specific column.
		 */
		@Override
		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		/**
		 * Returns the number of columns.
		 */
		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		/**
		 * Returns the names of the columns.
		 */
		@Override
		public String getColumnName(int col) {
			return columnNames[col];
		}

		/**
		 * Returns the number of rows.
		 */
		@Override
		public int getRowCount() {
			return m_parts.length;
		}

		/**
		 * Returns the value at a specific row and column.
		 */
		@Override
		public Object getValueAt(int row, int col) {
			Object result = null;
			if (col == 0) {
				result = m_parts[row].getGateClass().getName();
			} else if (col == 1) {
				if (m_parts[row] instanceof CompositeGate) {
					result = COMPOSITE_GATE;
				} else if (m_parts[row] instanceof BuiltInGate) {
					result = BUILTIN_GATE;
				}
			} else if (col == 2) {
				result = new Boolean(m_parts[row].getGateClass().isClocked());
			}
			return result;
		}

		/**
		 * Returns true of this table cells are editable, false - otherwise.
		 */
		@Override
		public boolean isCellEditable(int row, int col) {
			return false;
		}
	}

	// The strings representing a composite and BuiltIn gates.
	private final static String BUILTIN_GATE = "BuiltIn";

	private final static String COMPOSITE_GATE = "Composite";

	// The table containing the parts data
	private JTable m_partsTable;

	// The array of gates
	private Gate[] m_parts;

	// The scroll pane in which the table is placed.
	private JScrollPane m_scrollPane;

	// The model of this table.
	private PartsTableModel m_model;

	// The name of this component.
	private JLabel m_nameLbl = new JLabel();

	/**
	 * Constructs a new PartsComponent.
	 */
	public PartsComponent() {
		m_parts = new Gate[0];
		m_model = new PartsTableModel();
		m_partsTable = new JTable(m_model);
		jbInit();
	}

	// Determines the width of each column in the table.
	private void determineColumnWidth() {
		TableColumn column = null;
		for (int i = 0; i < 3; i++) {
			column = m_partsTable.getColumnModel().getColumn(i);
			if (i == 0) {
				column.setPreferredWidth(110);
			} else if (i == 1) {
				column.setPreferredWidth(72);
			} else if (i == 2) {
				column.setPreferredWidth(55);
			}
		}
	}

	/**
	 * Returns the width of the table.
	 */
	public int getTableWidth() {
		return 241;
	}

	// Initialization of this component.
	private void jbInit() {
		this.setLayout(null);
		m_partsTable.setFont(Utilities.valueFont);
		m_partsTable.getTableHeader().setReorderingAllowed(false);
		m_partsTable.getTableHeader().setResizingAllowed(false);

		m_partsTable.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				partsTable_focusGained(e);
			}

			@Override
			public void focusLost(FocusEvent e) {
				partsTable_focusLost(e);
			}
		});

		setBorder(BorderFactory.createEtchedBorder());
		m_scrollPane = new JScrollPane(m_partsTable);
		m_scrollPane.setLocation(0, 27);
		m_nameLbl.setText("Name :");
		m_nameLbl.setBounds(new Rectangle(3, 3, 102, 21));
		m_nameLbl.setFont(Utilities.labelsFont);
		this.add(m_scrollPane, null);
		this.add(m_nameLbl, null);
		determineColumnWidth();
	}

	/**
	 * The action of the table gaining focus (empty implementation)
	 */
	public void partsTable_focusGained(FocusEvent e) {
	}

	// The action of the table loosing focus
	public void partsTable_focusLost(FocusEvent e) {
		m_partsTable.clearSelection();
	}

	/**
	 * Resets the contents of this parts compoennt.
	 */
	@Override
	public void reset() {
		m_partsTable.clearSelection();
		repaint();
	}

	/**
	 * Sets the contents with the given parts (gates) array.
	 */
	@Override
	public void setContents(Gate[] newParts) {
		m_parts = new Gate[newParts.length];
		System.arraycopy(newParts, 0, m_parts, 0, newParts.length);
		m_partsTable.clearSelection();
		m_partsTable.revalidate();
	}

	/**
	 * Sets the name of this component.
	 */
	@Override
	public void setName(String name) {
		m_nameLbl.setText(name);
	}

	/**
	 * Sets the number of visible rows.
	 */
	public void setVisibleRows(int num) {
		int tableHeight = num * m_partsTable.getRowHeight();
		m_scrollPane.setSize(getTableWidth(), tableHeight + 3);
		setPreferredSize(new Dimension(getTableWidth(), tableHeight + 30));
		setSize(getTableWidth(), tableHeight + 30);
	}
}
