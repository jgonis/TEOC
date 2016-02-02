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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
import Hack.ComputerParts.MemorySegmentGUI;
import Hack.Events.ErrorEvent;
import Hack.Events.ErrorEventListener;

/**
 * This class represents the gui of a memory segment.
 */
public class MemorySegmentComponent extends JPanel implements MemorySegmentGUI, MemoryChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3481739682692645357L;

	// An inner class which implemets the cell renderer of the memory table,
	// giving
	// the feature of aligning the text in the cells.
	class MemorySegmentTableCellRenderer extends DefaultTableCellRenderer {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1198143476283978171L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused,
				int row, int column) {
			setForeground(null);
			setBackground(null);

			setRenderer(row, column);
			super.getTableCellRendererComponent(table, value, selected, focused, row, column);

			return this;
		}

		public void setEnabledRange(int row) {
			if ((((row + startAddress) < startEnabling) || ((row + startAddress) > endEnabling)) && hideDisabledRange) {
				setForeground(Color.white);
			}
		}

		public void setRenderer(int row, int column) {
			if (column == 0) {
				setHorizontalAlignment(SwingConstants.CENTER);
			} else if (column == 1) {
				setHorizontalAlignment(SwingConstants.RIGHT);

				for (int i = 0; i < highlightIndex.size(); i++) {
					if (row == ((Integer) highlightIndex.elementAt(i)).intValue()) {
						setForeground(Color.blue);
						break;
					}
				}

				if (row == flashIndex) {
					setBackground(Color.orange);
				}
			}
			setEnabledRange(row);
		}
	}

	// An inner class representing the model of this table.
	class MemorySegmentTableModel extends AbstractTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 7251440894882755452L;

		/**
		 * Returns the number of columns.
		 */
		@Override
		public int getColumnCount() {
			return 2;
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
			if (memory != null) {
				return Math.max(memory.getMemorySize() - startAddress, 0);
			} else {
				return 0;
			}
		}

		/**
		 * Returns the value at a specific row and column.
		 */
		@Override
		public Object getValueAt(int row, int col) {
			if (col == 0) {
				return String.valueOf(row);
			} else {
				return getStrAt(row);
			}
		}

		/**
		 * Returns true of this table cells are editable, false - otherwise.
		 */
		@Override
		public boolean isCellEditable(int row, int col) {
			boolean result = false;
			if (isEnabled && (col == 1) && ((endEnabling == -1)
					|| (((row + startAddress) >= startEnabling) && ((row + startAddress) <= endEnabling)))) {
				result = true;
			}

			return result;
		}

		/**
		 * Sets the value at a specific row and column.
		 */
		@Override
		public void setValueAt(Object value, int row, int col) {
			String data = ((String) value).trim();
			if (!getStrAt(row).equals(data)) {
				try {
					short currentValue;
					if (data.equals("") && hideNullValue) {
						currentValue = nullValue;
					} else {
						currentValue = Format.translateValueToShort(data, memory.dataFormat);
					}
					notifyListeners((short) row, currentValue);
				} catch (NumberFormatException nfe) {
					notifyErrorListeners("Illegal value");
				}
				repaint();
			}
		}
	}

	/**
	 * The current format.
	 */
	public int dataFormat;

	// The memory component.
	protected MemoryComponent memory;

	// The start address of this segment.
	protected int startAddress = 0;

	// The table of this segment.
	protected JTable segmentTable;

	// The model of the segment's table.
	protected MemorySegmentTableModel model;

	// The scroll pane on which the table shows.
	protected JScrollPane scrollPane;

	// The label with this segment's name.
	protected JLabel nameLbl = new JLabel();

	// A vector containing the values that should be highlighted.
	protected Vector<Integer> highlightIndex;

	// The listeners of this component.
	private Vector<ComputerPartEventListener> listeners;

	// The error listeners of this component.
	private Vector<ErrorEventListener> errorEventListeners;

	// The index of the flashed row.
	protected int flashIndex = -1;

	// The location of this component relative to its top level ancestor.
	protected Point topLevelLocation;

	// The top level component.
	private Component topLevelComponent;

	// a boolean field specifying if the user can enter values into the table
	protected boolean isEnabled = true;

	// The null value of this component
	protected short nullValue;

	// A boolean field specifying if the null value should be activated or not.
	protected boolean hideNullValue;

	// The start and end row indices of the enabled region.
	protected int startEnabling, endEnabling;

	// If true, the disabled region is shaded.
	protected boolean hideDisabledRange;

	/**
	 * Constructs a new MemorySegmentComponent.
	 */
	public MemorySegmentComponent() {
		dataFormat = Format.DEC_FORMAT;
		listeners = new Vector();
		errorEventListeners = new Vector();
		highlightIndex = new Vector();
		segmentTable = new JTable(getTableModel());
		segmentTable.setDefaultRenderer(segmentTable.getColumnClass(0), getCellRenderer());
		startEnabling = -1;
		endEnabling = -1;

		JTextField tf = new JTextField();
		tf.setFont(Utilities.bigBoldValueFont);
		tf.setBorder(null);
		DefaultCellEditor editor = new DefaultCellEditor(tf);
		segmentTable.getColumnModel().getColumn(getColumnValue()).setCellEditor(editor);

		jbInit();
	}

	/**
	 * Registers the given ErrorEventListener as a listener to this GUI.
	 */
	@Override
	public void addErrorListener(ErrorEventListener listener) {
		errorEventListeners.addElement(listener);
	}

	/**
	 * Registers the given ComputerPartEventListener as a listener to this GUI.
	 */
	@Override
	public void addListener(ComputerPartEventListener listener) {
		listeners.addElement(listener);
	}

	// Determines the width of each column in the table.
	protected void determineColumnWidth() {
		if (segmentTable.getColumnCount() == 2) {
			TableColumn column = null;
			for (int i = 0; i < 2; i++) {
				column = segmentTable.getColumnModel().getColumn(i);
				if (i == 0) {
					column.setPreferredWidth(30);
				} else {
					column.setPreferredWidth(100);
				}
			}
		}
	}

	/**
	 * Disables user input into the source.
	 */
	@Override
	public void disableUserInput() {
		isEnabled = false;
	}

	/**
	 * Enables user input into the source.
	 */
	@Override
	public void enableUserInput() {
		isEnabled = true;
	}

	/**
	 * flashes the value at the given index.
	 */
	@Override
	public void flash(int index) {
		flashIndex = index;
		Utilities.tableCenterScroll(this, segmentTable, index);
	}

	/**
	 * Returns the cell renderer of this component.
	 */
	protected DefaultTableCellRenderer getCellRenderer() {
		return new MemorySegmentTableCellRenderer();
	}

	/**
	 * Returns the number of columns in this table.
	 */
	protected int getColumnValue() {
		return 1;
	}

	/**
	 * Returns the coordinates of the top left corner of the value at the given
	 * index.
	 */
	@Override
	public Point getCoordinates(int index) {
		scrollPane.getVerticalScrollBar();
		Rectangle r = segmentTable.getCellRect(index, 1, true);
		segmentTable.scrollRectToVisible(r);
		setTopLevelLocation();
		return new Point((int) (r.getX() + topLevelLocation.getX()), (int) (r.getY() + topLevelLocation.getY()));
	}

	// Returns the string at the given location
	private String getStrAt(int index) {
		short currentValue = memory.getValueAsShort((short) (index + startAddress));
		if ((currentValue == nullValue) && hideNullValue) {
			return "";
		} else {
			return Format.translateValueToString(currentValue, dataFormat);
		}
	}

	/**
	 * Returns the table.
	 */
	public JTable getTable() {
		return segmentTable;
	}

	/**
	 * Returns the appropriate table model.
	 */
	protected TableModel getTableModel() {
		return new MemorySegmentTableModel();
	}

	/**
	 * Returns the width of the table.
	 */
	public int getTableWidth() {
		return 193;
	}

	/**
	 * Returns the value at the given index in its string representation.
	 */
	@Override
	public String getValueAsString(int index) {
		return Format.translateValueToString(memory.getValueAsShort((short) (index + startAddress)), dataFormat);
	}

	/**
	 * hides the existing flash.
	 */
	@Override
	public void hideFlash() {
		flashIndex = -1;
		repaint();

	}

	/**
	 * Hides all highlightes.
	 */
	@Override
	public void hideHighlight() {
		highlightIndex.removeAllElements();
		repaint();
	}

	/**
	 * Hides all selections.
	 */
	@Override
	public void hideSelect() {
		segmentTable.clearSelection();
	}

	/**
	 * Highlights the value at the given index.
	 */
	@Override
	public void highlight(int index) {
		highlightIndex.addElement(new Integer(index));
		repaint();
	}

	// Initializes this component.
	private void jbInit() {
		segmentTable.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				segmentTable_focusGained();
			}

			@Override
			public void focusLost(FocusEvent e) {
				segmentTable_focusLost();
			}
		});
		segmentTable.setTableHeader(null);
		scrollPane = new JScrollPane(segmentTable);
		scrollPane.setMinimumSize(new Dimension(getTableWidth(), 0));

		setLayout(new BorderLayout(0, 0));

		determineColumnWidth();
		nameLbl.setPreferredSize(new Dimension(getTableWidth(), 25));
		nameLbl.setMinimumSize(new Dimension(getTableWidth(), 0));
		nameLbl.setFont(Utilities.labelsFont);

		segmentTable.setFont(Utilities.valueFont);
		setBorder(BorderFactory.createEtchedBorder());

		setMinimumSize(new Dimension(getTableWidth(), 0));

		this.add(nameLbl, BorderLayout.NORTH);
		this.add(scrollPane, BorderLayout.CENTER);
	}

	/**
	 * Notifies all the ErrorEventListener on an error in this gui by creating
	 * an ErrorEvent (with the error message) and sending it using the
	 * errorOccured method to all the listeners.
	 */
	@Override
	public void notifyErrorListeners(String errorMessage) {
		ErrorEvent event = new ErrorEvent(this, errorMessage);
		for (int i = 0; i < errorEventListeners.size(); i++) {
			((ErrorEventListener) errorEventListeners.elementAt(i)).errorOccured(event);
		}
	}

	@Override
	public void notifyListeners() {
		new ComputerPartEvent(this);
		for (int i = 0; i < listeners.size(); i++) {
			((ComputerPartEventListener) listeners.elementAt(i)).guiGainedFocus();
		}
	}

	/**
	 * Notifies all the ComputerPartEventListeners on a change in the memory by
	 * creating a MemoryEvent (with the changed address and value) and sending
	 * it using the memoryChanged method to all the listeners.
	 */
	@Override
	public void notifyListeners(int address, short value) {
		ComputerPartEvent event = new ComputerPartEvent(this, address, value);
		for (int i = 0; i < listeners.size(); i++) {
			((ComputerPartEventListener) listeners.elementAt(i)).valueChanged(event);
		}
	}

	/**
	 * Un-registers the given ErrorEventListener from being a listener to this
	 * GUI.
	 */
	@Override
	public void removeErrorListener(ErrorEventListener listener) {
		errorEventListeners.removeElement(listener);
	}

	/**
	 * Un-registers the given ComputerPartEventListener from being a listener to
	 * this GUI.
	 */
	@Override
	public void removeListener(ComputerPartEventListener listener) {
		listeners.removeElement(listener);
	}

	/**
	 * Called when repaint is required.
	 */
	@Override
	public void repaintChange() {
		repaint();
	}

	/**
	 * Resets the contents of this MemorySegmentComponent.
	 */
	@Override
	public void reset() {
		segmentTable.clearSelection();
		hideFlash();
		hideHighlight();
	}

	/**
	 * Called when revalidate is required.
	 */
	@Override
	public void revalidateChange() {
		segmentTable.revalidate();
		repaint();
	}

	@Override
	public void scrollTo(int index) {
		Utilities.tableCenterScroll(this, segmentTable, index);
	}

	/**
	 * The action of the table gaining the focus.
	 */
	public void segmentTable_focusGained() {
		segmentTable.clearSelection();
		notifyListeners();
	}

	/**
	 * The action of the table loosing the focus.
	 */
	public void segmentTable_focusLost() {
		segmentTable.clearSelection();
	}

	/**
	 * Sets the enabled range of this segment. Any address outside this range
	 * will be disabled for user input. If hide is true, addresses outside the
	 * range will be hidden.
	 */
	@Override
	public void setEnabledRange(int start, int end, boolean hide) {
		startEnabling = start;
		endEnabling = end;
		hideDisabledRange = hide;
		repaint();
	}

	/**
	 * Sets the MemoryComponent of this class to the given one.
	 */
	public void setMemoryComponent(MemoryComponent memory) {
		this.memory = memory;
	}

	/**
	 * Sets the null value of this component.
	 */
	@Override
	public void setNullValue(short value, boolean hideNullValue) {
		nullValue = value;
		this.hideNullValue = hideNullValue;
	}

	/**
	 * Sets the numeric format with the given code (out of the format constants
	 * in HackController).
	 */
	@Override
	public void setNumericFormat(int formatCode) {
		dataFormat = formatCode;
	}

	/**
	 * Sets the name of this MemorySegmentComponent.
	 */
	public void setSegmentName(String name) {
		nameLbl.setText(name);
	}

	/**
	 * Sets the start address.
	 */
	@Override
	public void setStartAddress(int index) {
		startAddress = index;
		segmentTable.revalidate();
	}

	/**
	 * Sets the top level location.
	 */
	public void setTopLevelLocation() {
		topLevelLocation = Utilities.getTopLevelLocation(topLevelComponent, segmentTable);
	}

	/**
	 * Sets the top level location.
	 */
	public void setTopLevelLocation(Component top) {
		topLevelComponent = top;
		setTopLevelLocation();
	}

	/**
	 * Sets the starting address with the given address. This should display the
	 * relevant memory segment (the data should be taken from the main memory
	 * gui).
	 */
	@Override
	public void setValueAt(int index, short value) {
		Rectangle r = segmentTable.getCellRect(index, 0, true);
		segmentTable.scrollRectToVisible(r);
		repaint();
	}

	/**
	 * Sets the number of visible rows.
	 */
	public void setVisibleRows(int num) {
		int tableHeight = num * segmentTable.getRowHeight();
		scrollPane.setSize(getTableWidth(), tableHeight + 3);
		setPreferredSize(new Dimension(getTableWidth(), tableHeight + 30));
		setSize(getTableWidth(), tableHeight + 30);
	}
}
