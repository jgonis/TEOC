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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import Hack.ComputerParts.TextFileEvent;
import Hack.ComputerParts.TextFileEventListener;
import Hack.ComputerParts.TextFileGUI;

/**
 * A component for displaying a text file.
 */
public class TextFileComponent extends JPanel implements TextFileGUI {

	// An inner class representing the cell renderer of the table.
	public class TextFileCellRenderer extends DefaultTableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused,
				int row, int column) {
			setForeground(null);
			setBackground(null);

			setRenderer(row, column);
			super.getTableCellRendererComponent(table, value, selected, focused, row, column);

			return this;
		}

		public void setRenderer(int row, int column) {
			if (highlightedLines.contains(new Integer(row))) {
				setBackground(Color.yellow);
			} else {
				setBackground(null);
			}

			if (emphasizedLines.contains(new Integer(row))) {
				setForeground(Color.red);
			} else {
				setForeground(null);
			}
		}
	}

	// An inner class representing the model of the breakpoint table.
	class TextFileTableModel extends AbstractTableModel {

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
			// return rows.length;
			return rowsVector.size();
		}

		/**
		 * Returns the value at a specific row and column.
		 */
		@Override
		public Object getValueAt(int row, int col) {
			// return rows[row];
			return rowsVector.elementAt(row);
		}

		/**
		 * Returns true of this table cells are editable, false - otherwise.
		 */
		@Override
		public boolean isCellEditable(int row, int col) {
			return false;
		}
	}

	// A vector containing the listeners to this component.
	private Vector listeners;

	private Vector rowsVector;

	// The model of the table
	private TextFileTableModel model = new TextFileTableModel();

	// The table.
	private JTable textFileTable;

	// The scroll pane on which the table is placed.
	private JScrollPane scrollPane;

	// The label with the name of this component
	private JLabel nameLbl = new JLabel();

	// A set of indices of highlighted rows.
	private Set highlightedLines;

	// A set of indices of emphasized rows.
	private Set emphasizedLines;

	// Indicates whether this component is enabled.
	private boolean isEnabled;

	/**
	 * Constructs a new TextFileComponent
	 */
	public TextFileComponent() {
		listeners = new Vector();
		rowsVector = new Vector();
		textFileTable = new WideTable(model, 1000);
		textFileTable.setDefaultRenderer(textFileTable.getColumnClass(0), getCellRenderer());
		textFileTable.setTableHeader(null);
		highlightedLines = new HashSet();
		emphasizedLines = new HashSet();
		enableUserInput();
		jbInit();
	}

	@Override
	public void addEmphasis(int index) {
		emphasizedLines.add(new Integer(index));
		repaint();
	}

	@Override
	public void addHighlight(int index, boolean clear) {
		if (clear) {
			highlightedLines.clear();
		}

		highlightedLines.add(new Integer(index));
		Utilities.tableCenterScroll(this, textFileTable, index);
		repaint();
	}

	@Override
	public void addLine(String line) {
		rowsVector.addElement(line);
		textFileTable.revalidate();
		repaint();
		addHighlight(rowsVector.size() - 1, false);
	}

	@Override
	public void addTextFileListener(TextFileEventListener listener) {
		listeners.addElement(listener);
	}

	@Override
	public void clearHighlights() {
		highlightedLines.clear();
		repaint();
	}

	/**
	 * Disables user input to the component.
	 */
	public void disableUserInput() {
		textFileTable.setRowSelectionAllowed(false);
		isEnabled = false;
	}

	/**
	 * Enables user input to the component.
	 */
	public void enableUserInput() {
		textFileTable.setRowSelectionAllowed(true);
		isEnabled = true;
	}

	/**
	 * Returns the cell renderer of this component.
	 */
	protected DefaultTableCellRenderer getCellRenderer() {
		return new TextFileCellRenderer();
	}

	@Override
	public String getLineAt(int index) {
		return (String) rowsVector.elementAt(index);
	}

	@Override
	public int getNumberOfLines() {
		return rowsVector.size();
	}

	/**
	 * Returns the width of the table.
	 */
	public int getTableWidth() {
		return 241;
	}

	@Override
	public void hideSelect() {
		textFileTable.clearSelection();
	}

	// The initialization of this component.
	private void jbInit() {

		textFileTable.setShowHorizontalLines(false);
		ListSelectionModel rowSM = textFileTable.getSelectionModel();
		rowSM.addListSelectionListener(e -> {
			if (!isEnabled || e.getValueIsAdjusting()) {
				return;
			}
			ListSelectionModel lsm = (ListSelectionModel) e.getSource();
			if (!lsm.isSelectionEmpty()) {
				int selectedRow = lsm.getMinSelectionIndex();
				notifyTextFileListeners((String) rowsVector.elementAt(selectedRow), selectedRow);
			}
		});
		this.setLayout(null);

		scrollPane = new JScrollPane(textFileTable);
		scrollPane.setLocation(0, 27);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(scrollPane.getHorizontalScrollBar().getBlockIncrement());

		nameLbl.setBounds(new Rectangle(3, 3, 102, 21));
		nameLbl.setFont(Utilities.labelsFont);
		textFileTable.setFont(Utilities.valueFont);
		setBorder(BorderFactory.createEtchedBorder());

		this.add(scrollPane, null);
		this.add(nameLbl, null);

	}

	@Override
	public void notifyTextFileListeners(String row, int rowNum) {
		TextFileEvent event = new TextFileEvent(this, row, rowNum);
		for (int i = 0; i < listeners.size(); i++) {
			((TextFileEventListener) listeners.elementAt(i)).rowSelected(event);
		}
	}

	@Override
	public void removeEmphasis(int index) {
		emphasizedLines.remove(new Integer(index));
		repaint();
	}

	@Override
	public void removeTextFileListener(TextFileEventListener listener) {
		listeners.removeElement(listener);
	}

	/**
	 * Resets the content of this component.
	 */
	@Override
	public void reset() {
		highlightedLines.clear();
		rowsVector.removeAllElements();
		textFileTable.revalidate();
		textFileTable.clearSelection();
		repaint();
	}

	@Override
	public void select(int from, int to) {
		textFileTable.setRowSelectionInterval(from, to);
		Utilities.tableCenterScroll(this, textFileTable, from);
	}

	@Override
	public void setContents(String fileName) {
		BufferedReader reader;
		rowsVector.removeAllElements();
		try {
			reader = new BufferedReader(new FileReader(fileName));
			String line;
			while ((line = reader.readLine()) != null) {
				rowsVector.addElement(line);
			}
			reader.close();
		} catch (IOException ioe) {
		}
		textFileTable.clearSelection();
		textFileTable.revalidate();
		repaint();
	}

	@Override
	public void setContents(String[] lines) {
		rowsVector.removeAllElements();
		for (String line : lines) {
			rowsVector.addElement(line);
		}
		textFileTable.revalidate();
		repaint();
	}

	@Override
	public void setLineAt(int index, String line) {
		rowsVector.setElementAt(line, index);
		addHighlight(index, false);
	}

	@Override
	public void setName(String name) {
		nameLbl.setText(name);
	}

	/**
	 * Sets the number of visible rows.
	 */
	public void setVisibleRows(int num) {
		int tableHeight = num * textFileTable.getRowHeight();
		scrollPane.setSize(getTableWidth(), tableHeight + 3);
		setPreferredSize(new Dimension(getTableWidth(), tableHeight + 30));
		setSize(getTableWidth(), tableHeight + 30);
		textFileTable.getParent().setSize(new Dimension(1000, tableHeight));
	}
}
