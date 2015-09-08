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

package HackGUI;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

/**
 * A Memory component that has an address pointer and displays only its values
 * (no addresses).
 */
public class ValuesOnlyPointedMemoryComponent extends PointedMemoryComponent {

	// An inner class representing the model of the table.
	public class ValuesOnlyPointedMemoryTableModel extends MemoryTableModel {

		/**
		 * Returns the number of columns.
		 */
		@Override
		public int getColumnCount() {
			return 1;
		}

		/**
		 * Returns the value at a specific row and column.
		 */
		@Override
		public Object getValueAt(int row, int col) {
			return super.getValueAt(row, col + 1);
		}

		/**
		 * Returns true of this table cells are editable, false - otherwise.
		 */
		@Override
		public boolean isCellEditable(int row, int col) {
			return super.isCellEditable(row, col + 1);
		}

	}

	public class ValuesOnlyPointedTableCellRenderer extends PointedMemoryTableCellRenderer {

		@Override
		public void setRenderer(int row, int column) {
			super.setRenderer(row, column + 1);
		}
	}

	// Determines the width of each column in the table.
	@Override
	protected void determineColumnWidth() {
	}

	@Override
	protected DefaultTableCellRenderer getCellRenderer() {
		return new ValuesOnlyPointedTableCellRenderer();
	}

	/**
	 * Returns the table model of this component.
	 */
	@Override
	protected TableModel getTableModel() {
		return new ValuesOnlyPointedMemoryTableModel();
	}

	/**
	 * Returns the index of the values column.
	 */
	@Override
	protected int getValueColumnIndex() {
		return 0;
	}
}
