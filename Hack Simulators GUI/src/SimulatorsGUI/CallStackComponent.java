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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import Hack.VMEmulator.CallStackGUI;
import HackGUI.Utilities;

/**
 * This class represents the gui of a CallStack.
 */
public class CallStackComponent extends JPanel implements CallStackGUI {

	private static final long serialVersionUID = 3274288759472843939L;

	/**
	 * The Cell Renderer for the call stack's table.
	 */
	public class CallStackTableCellRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = -6568204416360733388L;

		/**
		 * Returns the cell renderer component.
		 */
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused,
				int row, int column) {
			setForeground(null);
			setBackground(null);

			setRenderer(row);
			super.getTableCellRendererComponent(table, value, selected, focused, row, column);

			return this;
		}

		/**
		 * Sets a new cell renderer.
		 */
		public void setRenderer(int row) {
			if (row == (m_methodNames.size() - 1)) {
				setForeground(Color.blue);
			}
		}
	}

	// An inner class representing the model of the CallStack table.
	class CallStackTableModel extends AbstractTableModel {

		private static final long serialVersionUID = -846057644169400305L;

		/**
		 * Returns the number of columns.
		 */
		@Override
		public int getColumnCount() {
			return 1;
		}

		/**
		 * Returns the names of the columns.
		 */
		@Override
		public String getColumnName(int col) {
			return "";
		}

		/**
		 * Returns the number of rows.
		 */
		@Override
		public int getRowCount() {
			return m_methodNames.size();
		}

		/**
		 * Returns the value at a specific row and column.
		 */
		@Override
		public Object getValueAt(int row, int col) {
			return m_methodNames.elementAt(row);
		}

		/**
		 * Returns true of this table cells are editable, false - otherwise.
		 */
		@Override
		public boolean isCellEditable(int row, int col) {
			return false;
		}
	}

	// Default number of visible rows
	protected static final int DEFAULT_VISIBLE_ROWS = 10;

	// The vector containing the method names of this call stack.
	private Vector<String> m_methodNames;

	// The table representing this callStack.
	private JTable m_callStackTable;

	// The model of the table;
	private CallStackTableModel m_model;

	// The containing scroll panel
	private JScrollPane m_scrollPane;

	// The name label
	private JLabel m_nameLbl = new JLabel();

	/**
	 * Constructs a new CallStackComponent.
	 */
	public CallStackComponent() {
		m_methodNames = new Vector<String>();
		m_model = new CallStackTableModel();
		m_callStackTable = new JTable(m_model);
		jbInit();

	}

	/**
	 * The action of the table gaining focus (empty implementation).
	 */
	public void callStackTable_focusGained() {
	}

	/**
	 * The action of the table loosing focus
	 */
	public void callStackTable_focusLost() {
		m_callStackTable.clearSelection();
	}

	/**
	 * Returns the cell renderer of this component.
	 */
	protected DefaultTableCellRenderer getCellRenderer() {
		return new CallStackTableCellRenderer();
	}

	/**
	 * Returns the width of the table.
	 */
	public int getTableWidth() {
		return 190;
	}

	// Initializing this component.
	private void jbInit() {
		m_callStackTable.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				callStackTable_focusGained();
			}

			@Override
			public void focusLost(FocusEvent e) {
				callStackTable_focusLost();
			}
		});
		m_callStackTable.setTableHeader(null);
		m_callStackTable.setDefaultRenderer(m_callStackTable.getColumnClass(0), getCellRenderer());
		m_scrollPane = new JScrollPane(m_callStackTable);
		setVisibleRows(DEFAULT_VISIBLE_ROWS);
		m_scrollPane.setLocation(0, 27);
		setBorder(BorderFactory.createEtchedBorder());
		this.setLayout(null);
		m_nameLbl.setText("Call Stack");
		m_nameLbl.setBounds(new Rectangle(3, 4, 70, 23));
		m_nameLbl.setFont(Utilities.labelsFont);
		this.add(m_scrollPane, null);
		this.add(m_nameLbl, null);
	}

	/**
	 * Resets the contents of this CallStackComponent.
	 */
	@Override
	public void reset() {
		m_methodNames.removeAllElements();
		m_callStackTable.revalidate();
		m_callStackTable.clearSelection();

	}

	/**
	 * Sets the call stack with the given vector of method names.
	 */
	@Override
	public void setContents(Vector<String> newMethodNames) {
		m_methodNames = new Vector<String>(newMethodNames);
		m_callStackTable.revalidate();

		Rectangle r = m_callStackTable.getCellRect(newMethodNames.size() - 1, 0, true);
		m_callStackTable.scrollRectToVisible(r);
		repaint();
	}

	/**
	 * Sets the number of visible rows.
	 */
	public void setVisibleRows(int num) {
		int tableHeight = num * m_callStackTable.getRowHeight();
		m_scrollPane.setSize(getTableWidth(), tableHeight + 3);
		setPreferredSize(new Dimension(getTableWidth(), tableHeight + 30));
		setSize(getTableWidth(), tableHeight + 30);
	}
}
