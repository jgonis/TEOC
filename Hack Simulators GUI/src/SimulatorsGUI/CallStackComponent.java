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

	/**
	 * The Cell Renderer for the call stack's table.
	 */
	public class callStackTableCellRenderer extends DefaultTableCellRenderer {

		/**
		 * Returns the cell renderer component.
		 */
		public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused,
				int row, int column) {
			setForeground(null);
			setBackground(null);

			setRenderer(row, column);
			super.getTableCellRendererComponent(table, value, selected, focused, row, column);

			return this;
		}

		/**
		 * Sets a new cell renderer.
		 */
		public void setRenderer(int row, int column) {
			if (row == (methodNames.size() - 1))
				setForeground(Color.blue);
		}
	}

	// An inner class representing the model of the CallStack table.
	class CallStackTableModel extends AbstractTableModel {

		/**
		 * Returns the number of columns.
		 */
		public int getColumnCount() {
			return 1;
		}

		/**
		 * Returns the names of the columns.
		 */
		public String getColumnName(int col) {
			return "";
		}

		/**
		 * Returns the number of rows.
		 */
		public int getRowCount() {
			return methodNames.size();
		}

		/**
		 * Returns the value at a specific row and column.
		 */
		public Object getValueAt(int row, int col) {
			return methodNames.elementAt(row);
		}

		/**
		 * Returns true of this table cells are editable, false - otherwise.
		 */
		public boolean isCellEditable(int row, int col) {
			return false;
		}
	}

	// Default number of visible rows
	protected static final int DEFAULT_VISIBLE_ROWS = 10;

	// The vector containing the method names of this call stack.
	private Vector methodNames;

	// The table representing this callStack.
	private JTable callStackTable;

	// The model of the table;
	private CallStackTableModel model;

	// The containing scroll panel
	private JScrollPane scrollPane;

	// The name label
	private JLabel nameLbl = new JLabel();

	/**
	 * Constructs a new CallStackComponent.
	 */
	public CallStackComponent() {
		methodNames = new Vector();
		model = new CallStackTableModel();
		callStackTable = new JTable(model);
		jbInit();

	}

	/**
	 * The action of the table gaining focus (empty implementation).
	 */
	public void callStackTable_focusGained(FocusEvent e) {
	}

	/**
	 * The action of the table loosing focus
	 */
	public void callStackTable_focusLost(FocusEvent e) {
		callStackTable.clearSelection();
	}

	/**
	 * Returns the cell renderer of this component.
	 */
	protected DefaultTableCellRenderer getCellRenderer() {
		return new callStackTableCellRenderer();
	}

	/**
	 * Returns the width of the table.
	 */
	public int getTableWidth() {
		return 190;
	}

	// Initializing this component.
	private void jbInit() {
		callStackTable.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				callStackTable_focusGained(e);
			}

			public void focusLost(FocusEvent e) {
				callStackTable_focusLost(e);
			}
		});
		callStackTable.setTableHeader(null);
		callStackTable.setDefaultRenderer(callStackTable.getColumnClass(0), getCellRenderer());
		scrollPane = new JScrollPane(callStackTable);
		setVisibleRows(DEFAULT_VISIBLE_ROWS);
		scrollPane.setLocation(0, 27);
		setBorder(BorderFactory.createEtchedBorder());
		this.setLayout(null);
		nameLbl.setText("Call Stack");
		nameLbl.setBounds(new Rectangle(3, 4, 70, 23));
		nameLbl.setFont(Utilities.labelsFont);
		this.add(scrollPane, null);
		this.add(nameLbl, null);
	}

	/**
	 * Resets the contents of this CallStackComponent.
	 */
	public void reset() {
		methodNames.removeAllElements();
		callStackTable.revalidate();
		callStackTable.clearSelection();

	}

	/**
	 * Sets the call stack with the given vector of method names.
	 */
	public void setContents(Vector newMethodNames) {
		methodNames = (Vector) newMethodNames.clone();
		callStackTable.revalidate();

		Rectangle r = callStackTable.getCellRect(newMethodNames.size() - 1, 0, true);
		callStackTable.scrollRectToVisible(r);
		repaint();
	}

	/**
	 * Sets the number of visible rows.
	 */
	public void setVisibleRows(int num) {
		int tableHeight = num * callStackTable.getRowHeight();
		scrollPane.setSize(getTableWidth(), tableHeight + 3);
		setPreferredSize(new Dimension(getTableWidth(), tableHeight + 30));
		setSize(getTableWidth(), tableHeight + 30);
	}
}
