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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import Hack.ComputerParts.ComputerPartEvent;
import Hack.ComputerParts.ComputerPartEventListener;
import Hack.Events.ErrorEvent;
import Hack.Events.ErrorEventListener;
import Hack.Gates.PinInfo;
import Hack.HardwareSimulator.PinsGUI;
import HackGUI.Format;
import HackGUI.Utilities;

/**
 * This class represents the gui of a pins list. Every pin row contains the
 * pin's name and value (which may be in a specified width). The GUI can display
 * its values in decimal, hexadecimal or binary format.
 */
public class PinsComponent extends JPanel implements PinsGUI, MouseListener, PinValueListener {

	private static final long serialVersionUID = 8940912076099582111L;

	// An inner class which implemets the cell renderer of the pins table,
	// giving
	// the feature of alignment, flashing and highlighting.
	class PinsTableCellRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = -7929735129174897198L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused,
				int row, int column) {
			setEnabled((table == null) || table.isEnabled());

			if (column == 0) {
				setHorizontalAlignment(SwingConstants.CENTER);
				setForeground(null);
				setBackground(null);
			} else {
				setHorizontalAlignment(SwingConstants.RIGHT);
				for (int i = 0; i < m_highlightIndex.size(); i++) {
					if (row == ((Integer) m_highlightIndex.elementAt(i)).intValue()) {
						setForeground(Color.blue);
						break;
					} else {
						setForeground(null);
					}
				}
				if (row == m_flashIndex) {
					setBackground(Color.orange);
				} else {
					setBackground(null);
				}

			}

			super.getTableCellRendererComponent(table, value, selected, focused, row, column);

			return this;
		}
	}

	// An inner class representing the model of the breakpoint table.
	class PinsTableModel extends AbstractTableModel {
		
		private static final long serialVersionUID = 7484833797433734711L;
		String[] m_columnNames = { "Name", "Value" };

		/**
		 * Returns the number of columns.
		 */
		@Override
		public int getColumnCount() {
			return m_columnNames.length;
		}

		/**
		 * Returns the names of the columns.
		 */
		@Override
		public String getColumnName(int col) {
			return m_columnNames[col];
		}

		/**
		 * Returns the number of rows.
		 */
		@Override
		public int getRowCount() {
			return m_pins.length;
		}

		/**
		 * Returns the value at a specific row and column.
		 */
		@Override
		public Object getValueAt(int row, int col) {

			if (col == 0) {
				return m_pins[row].name + (m_pins[row].width > 1 ? "[" + m_pins[row].width + "]" : "");
			} else {
				return m_valueStr[row];
			}
		}

		/**
		 * Returns true of this table cells are editable, false - otherwise.
		 */
		@Override
		public boolean isCellEditable(int row, int col) {
			if (m_isEnabled && (col == 1) && (m_dataFormat != Format.BIN_FORMAT)
					&& ((m_endEnabling == -1) || ((row >= m_startEnabling) && (row <= m_endEnabling)))) {
				return true;
			} else {
				return false;
			}
		}

		/**
		 * Sets the value at a specific row and column.
		 */
		@Override
		public void setValueAt(Object value, int row, int col) {

			String data = (String) value;
			if (!m_valueStr[row].equals(data)) {
				try {
					m_valueStr[row] = data;

					if (data.equals("") && m_hideNullValue) {
						m_pins[row].value = m_nullValue;
					} else {
						m_pins[row].value = Format.translateValueToShort(data, m_dataFormat);
					}

					notifyListeners((short) row, m_pins[row].value);
				} catch (NumberFormatException nfe) {
					notifyErrorListeners("Illegal value");
					m_valueStr[row] = Format.translateValueToString(m_pins[row].value, m_dataFormat);
				}
				repaint();
			}
		}
	}

	// A component which represents the binary value of the pin.
	protected BinaryComponent m_binary;

	// The table of the input component.
	protected JTable m_pinsTable;

	// An array containing the info of the pins.
	private PinInfo[] m_pins;

	// The values of the pins in a String representation.
	private String[] m_valueStr;

	// The current format.
	protected int m_dataFormat;

	// A vector containing the listeners to this object.
	private Vector<ComputerPartEventListener> m_listeners;

	// A vector containing the error listeners to this object.
	private Vector<ErrorEventListener> m_errorEventListeners;

	// The scroll pane on which the table is placed.
	protected JScrollPane m_scrollPane;

	// The index of the flashed row in the table.
	protected int m_flashIndex = -1;

	// A vector containing the index of the rows that should be highlighted.
	protected Vector<Integer> m_highlightIndex;

	// The location of this component relative to its top level ancestor.
	protected Point m_topLevelLocation;

	// The renderer of the table containing the pins list.
	private PinsTableCellRenderer m_renderer = new PinsTableCellRenderer();

	// The name of this pins component.
	private JLabel m_nameLbl = new JLabel();

	// a boolean field specifying if the user can enter values into the table.
	private boolean m_isEnabled = true;

	// The null value of this component
	protected short m_nullValue;

	// A boolean field specifying if the null value should be activated or not.
	protected boolean m_hideNullValue;

	// The start and end enabled indices.
	protected int m_startEnabling, m_endEnabling;

	// The index of the last selected row.
	private int m_lastSelectedRow;

	/**
	 * Constructs a new PinsComponent.
	 */
	public PinsComponent() {
		m_dataFormat = Format.DEC_FORMAT;
		JTextField tf = new JTextField();
		tf.setFont(Utilities.bigBoldValueFont);
		tf.setBorder(null);
		DefaultCellEditor editor = new DefaultCellEditor(tf);
		m_startEnabling = -1;
		m_endEnabling = -1;

		m_pins = new PinInfo[0];
		m_valueStr = new String[0];
		m_listeners = new Vector<ComputerPartEventListener>();
		m_errorEventListeners = new Vector<ErrorEventListener>();
		m_highlightIndex = new Vector<Integer>();
		m_binary = new BinaryComponent();
		m_pinsTable = new JTable(getTableModel());
		m_pinsTable.setDefaultRenderer(m_pinsTable.getColumnClass(0), m_renderer);

		m_pinsTable.getColumnModel().getColumn(getValueColumn()).setCellEditor(editor);

		jbInit();

	}

	/**
	 * Registers the given ErrorEventListener as a listener to this GUI.
	 */
	@Override
	public void addErrorListener(ErrorEventListener listener) {
		m_errorEventListeners.addElement(listener);
	}

	@Override
	public void addListener(ComputerPartEventListener listener) {
		m_listeners.addElement(listener);
	}

	// Determines the width of each column in the table.
	protected void determineColumnWidth() {
		TableColumn column = null;
		for (int i = 0; i < 2; i++) {
			column = m_pinsTable.getColumnModel().getColumn(i);
			if (i == 0) {
				column.setPreferredWidth(116);
			} else {
				column.setPreferredWidth(124);
			}
		}
	}

	/**
	 * Disables user input into the source.
	 */
	@Override
	public void disableUserInput() {
		m_isEnabled = false;
	}

	/**
	 * Enables user input into the source.
	 */
	@Override
	public void enableUserInput() {
		m_isEnabled = true;
	}

	/**
	 * flashes the value at the given index.
	 */
	@Override
	public void flash(int index) {
		m_flashIndex = index;
		Utilities.tableCenterScroll(this, m_pinsTable, index);
	}

	/**
	 * Returns the coordinates of the top left corner of the value at the given
	 * index.
	 */
	@Override
	public Point getCoordinates(int index) {
		JScrollBar bar = m_scrollPane.getVerticalScrollBar();
		Rectangle r = m_pinsTable.getCellRect(index, 1, true);
		m_pinsTable.scrollRectToVisible(r);
		return new Point((int) (r.getX() + m_topLevelLocation.getX()),
				(int) ((r.getY() + m_topLevelLocation.getY()) - bar.getValue()));
	}

	/**
	 * Returns the location of the given index relative to the panel.
	 */
	public Point getLocation(int index) {
		Rectangle r = m_pinsTable.getCellRect(index, 0, true);
		Point p = Utilities.getTopLevelLocation(this, m_pinsTable);
		return new Point((int) (r.getX() + p.getX()), (int) (r.getY() + p.getY()));
	}

	/**
	 * Returns the appropriate table model.
	 */
	protected TableModel getTableModel() {
		return new PinsTableModel();
	}

	/**
	 * Returns the width of the table.
	 */
	public int getTableWidth() {
		return 241;
	}

	/**
	 * Returns the value at the given index in its string representation.
	 */
	@Override
	public String getValueAsString(int index) {
		return m_valueStr[index];
	}

	/**
	 * Returns the index of the values column.
	 */
	protected int getValueColumn() {
		return 1;
	}

	/**
	 * Hides the existing flash.
	 */
	@Override
	public void hideFlash() {
		m_flashIndex = -1;
		repaint();
	}

	/**
	 * Hides all highlightes.
	 */
	@Override
	public void hideHighlight() {
		m_highlightIndex.removeAllElements();
		repaint();
	}

	/**
	 * Highlights the value at the given index.
	 */
	@Override
	public void highlight(int index) {
		m_highlightIndex.addElement(new Integer(index));
		repaint();
	}

	// Initialization of this component.
	private void jbInit() {
		m_pinsTable.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				pinsTable_focusGained(e);
			}

			@Override
			public void focusLost(FocusEvent e) {
				pinsTable_focusLost(e);
			}
		});
		m_pinsTable.addMouseListener(this);
		m_pinsTable.getTableHeader().setReorderingAllowed(false);
		m_pinsTable.getTableHeader().setResizingAllowed(false);
		this.setLayout(null);
		m_scrollPane = new JScrollPane(m_pinsTable);
		m_scrollPane.setLocation(0, 27);
		setBorder(BorderFactory.createEtchedBorder());

		m_binary.setSize(new Dimension(240, 52));
		m_binary.setLayout(null);
		m_binary.setVisible(false);
		m_binary.addListener(this);
		determineColumnWidth();
		m_nameLbl.setText("Name :");
		m_nameLbl.setBounds(new Rectangle(3, 3, 102, 21));
		m_nameLbl.setFont(Utilities.labelsFont);
		m_pinsTable.setFont(Utilities.valueFont);
		this.add(m_binary, null);
		this.add(m_scrollPane, null);
		this.add(m_nameLbl, null);

	}

	/**
	 * Implementing the action of double-clicking the mouse on the text field to
	 * popup the binary input component and of clicking the mouse on the text
	 * field to cancel the binary input component. This generates the same feel
	 * as the (hexa)decimal input method
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		if (m_isEnabled && ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0)) {
			if (m_binary.isVisible()) {
				m_binary.hideBinary();
				// The pinsTable didn't get the selection message since it
				// was disabled. Enable it and select the correct row.
				m_pinsTable.setEnabled(true);
				m_pinsTable.changeSelection(m_pinsTable.rowAtPoint(e.getPoint()), m_pinsTable.columnAtPoint(e.getPoint()),
						false, false);
				m_pinsTable.grabFocus();
			}
			if (e.getClickCount() == 2) {
				if (m_dataFormat == Format.BIN_FORMAT) {
					m_pinsTable.setEnabled(false);
					m_binary.setLocation((int) getLocation(m_pinsTable.getSelectedRow() + 1).getX(),
							(int) getLocation(m_pinsTable.getSelectedRow() + 1).getY());
					m_binary.setValue(m_pins[m_pinsTable.getSelectedRow()].value);
					m_binary.setNumOfBits(m_pins[m_pinsTable.getSelectedRow()].width);
					m_binary.showBinary();
				}
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	// Empty implementations.
	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	/**
	 * Notifies all the ErrorEventListener on an error in this gui by creating
	 * an ErrorEvent (with the error message) and sending it using the
	 * errorOccured method to all the listeners.
	 */
	@Override
	public void notifyErrorListeners(String errorMessage) {
		ErrorEvent event = new ErrorEvent(this, errorMessage);
		for (int i = 0; i < m_errorEventListeners.size(); i++) {
			((ErrorEventListener) m_errorEventListeners.elementAt(i)).errorOccured(event);
		}
	}

	@Override
	public void notifyListeners() {
		for (int i = 0; i < m_listeners.size(); i++) {
			((ComputerPartEventListener) m_listeners.elementAt(i)).guiGainedFocus();
		}
	}

	@Override
	public void notifyListeners(int index, short value) {
		ComputerPartEvent event = new ComputerPartEvent(this, index, value);
		for (int i = 0; i < m_listeners.size(); i++) {
			((ComputerPartEventListener) m_listeners.elementAt(i)).valueChanged(event);
		}
	}

	// The action of the table gaining focus
	public void pinsTable_focusGained(FocusEvent e) {
		notifyListeners();
	}

	// The action of the table loosing focus
	public void pinsTable_focusLost(FocusEvent e) {
		m_lastSelectedRow = m_pinsTable.getSelectedRow();
		m_pinsTable.clearSelection();
	}

	/**
	 * Called when there was a change in one of the pin values. The event
	 * contains the changed value in a string representation and a boolean which
	 * is true if the user pressed the 'ok' button and false if the user pressed
	 * the 'cancel' button.
	 */
	@Override
	public void pinValueChanged(PinValueEvent e) {
		m_pinsTable.setEnabled(true);
		if (e.getIsOk()) {
			m_pins[m_lastSelectedRow].value = translateValueToShort(e.getValueStr());
			m_valueStr[m_lastSelectedRow] = translateValueToString(m_pins[m_lastSelectedRow].value,
					m_pins[m_lastSelectedRow].width);
		}
		notifyListeners(m_lastSelectedRow, m_pins[m_lastSelectedRow].value);
	}

	/**
	 * Un-registers the given ErrorEventListener from being a listener to this
	 * GUI.
	 */
	@Override
	public void removeErrorListener(ErrorEventListener listener) {
		m_errorEventListeners.removeElement(listener);
	}

	@Override
	public void removeListener(ComputerPartEventListener listener) {
		m_listeners.removeElement(listener);
	}

	/**
	 * Resets the contents of the Pins component.
	 */
	@Override
	public void reset() {
		m_pinsTable.clearSelection();
		repaint();
		// resetting the flash and highlight
		hideFlash();
		hideHighlight();
	}

	/**
	 * Sets the pins list's contents with the given array of PinInfo objects.
	 */
	@Override
	public void setContents(PinInfo[] newPins) {
		m_pins = new PinInfo[newPins.length];
		m_valueStr = new String[newPins.length];
		System.arraycopy(newPins, 0, m_pins, 0, newPins.length);
		for (int i = 0; i < newPins.length; i++) {
			m_valueStr[i] = translateValueToString(newPins[i].value, newPins[i].width);
		}
		m_pinsTable.clearSelection();
		m_pinsTable.revalidate();
		repaint();
	}

	@Override
	public void setDimmed(boolean cond) {
		m_pinsTable.setEnabled(!cond);
	}

	/**
	 * Sets the enabled range of this segment. Any address outside this range
	 * will be disabled for user input. If gray is true, addresses outside the
	 * range will be gray colored.
	 */
	@Override
	public void setEnabledRange(int start, int end, boolean gray) {
		m_startEnabling = start;
		m_endEnabling = end;
	}

	/**
	 * Sets the null value of this component.
	 */
	@Override
	public void setNullValue(short value, boolean hideNullValue) {
		m_nullValue = value;
		this.m_hideNullValue = hideNullValue;
	}

	/**
	 * Sets the numeric format with the given code (out of the format constants
	 * in HackController).
	 */
	@Override
	public void setNumericFormat(int formatCode) {
		m_dataFormat = formatCode;
		for (int i = 0; i < m_pins.length; i++) {
			m_valueStr[i] = translateValueToString(m_pins[i].value, m_pins[i].width);
		}
		repaint();

	}

	/**
	 * Sets the name of this pins component.
	 */
	public void setPinsName(String name) {
		m_nameLbl.setText(name);
	}

	/**
	 * Sets the top level location.
	 */
	public void setTopLevelLocation(Component top) {
		m_topLevelLocation = Utilities.getTopLevelLocation(top, m_pinsTable);
	}

	/**
	 * Sets the element at the given index with the given value.
	 */
	@Override
	public void setValueAt(int index, short value) {
		m_pins[index].value = value;
		m_valueStr[index] = translateValueToString(value, m_pins[index].width);
	}

	/**
	 * Sets the number of visible rows.
	 */
	public void setVisibleRows(int num) {
		int tableHeight = num * m_pinsTable.getRowHeight();
		m_scrollPane.setSize(getTableWidth(), tableHeight + 3);
		setPreferredSize(new Dimension(getTableWidth(), tableHeight + 30));
		setSize(getTableWidth(), tableHeight + 30);
	}

	/**
	 * Translates a given string to a short according to the current format.
	 */
	protected short translateValueToShort(String data) {
		return Format.translateValueToShort(data, m_dataFormat);
	}

	/**
	 * Translates a given short to a string according to the current format.
	 */
	protected String translateValueToString(short value, int width) {
		String result = null;

		if ((value == m_nullValue) && m_hideNullValue) {
			result = "";
		} else {
			result = Format.translateValueToString(value, m_dataFormat);
			if (m_dataFormat == Format.BIN_FORMAT) {
				result = result.substring(result.length() - width, result.length());
			}
		}

		return result;
	}
}
