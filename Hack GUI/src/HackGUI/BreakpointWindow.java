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
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import Hack.Controller.Breakpoint;

/**
 * This class represents the gui of a breakpoint panel.
 */
public class BreakpointWindow extends JFrame implements MouseListener, BreakpointChangedListener {

	private static final long serialVersionUID = -8333628702063043709L;

	// An inner class representing the model of the breakpoint table.
	class BreakpointTableModel extends AbstractTableModel {
		
		private static final long serialVersionUID = -2915405766410292378L;
		private String[] m_columnNames = { "Variable Name", "Value" };

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
			return m_breakpoints.size();
		}

		/**
		 * Returns the value at a specific row and column.
		 */
		@Override
		public Object getValueAt(int row, int col) {
			Breakpoint breakpoint = m_breakpoints.elementAt(row);

			if (col == 0) {
				return breakpoint.getVarName();
			} else {
				return breakpoint.getValue();
			}
		}

		/**
		 * Returns true of this table cells are editable, false - otherwise.
		 */
		@Override
		public boolean isCellEditable(int row, int col) {
			return false;
		}

		/**
		 * Removes a row from this table.
		 */
		public void removeRow(int index) {
			if (m_breakpoints.size() > 0) {
				m_breakpoints.removeElementAt(index);
			}
		}
	}

	// An inner class which implements the cell renderer of the breakpoint
	// table, giving
	// the feature of coloring the background of a specific cell.
	class ColoredTableCellRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = -2354977458846732061L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused,
				int row, int column) {
			setEnabled((table == null) || table.isEnabled());
			if (m_breakpoints.elementAt(row).isReached()) {
				setBackground(Color.red);
			} else {
				setBackground(null);
			}
			setHorizontalAlignment(SwingConstants.CENTER);
			super.getTableCellRendererComponent(table, value, selected, focused, row, column);
			return this;
		}
	}

	// The table of breakpoints.
	private JTable m_breakpointTable;

	// The vector of breakpoints.
	private Vector<Breakpoint> m_breakpoints;

	// The model of this table.
	private BreakpointTableModel m_model;

	// A vector containing the listeners to this object.
	private Vector<BreakpointsChangedListener> m_listeners;
	// The layout of this component.
	private FlowLayout m_flowLayout = new FlowLayout();
	// The add remove and ok buttons.
	private JButton m_addButton = new JButton();

	private JButton m_removeButton = new JButton();

	private JButton m_okButton = new JButton();
	// The cell renderer of the table.
	private ColoredTableCellRenderer m_coloredRenderer = new ColoredTableCellRenderer();
	// Creating icons.
	private ImageIcon m_addIcon = new ImageIcon(Utilities.imagesDir + "smallplus.gif");

	private ImageIcon m_removeIcon = new ImageIcon(Utilities.imagesDir + "smallminus.gif");

	private ImageIcon m_okIcon = new ImageIcon(Utilities.imagesDir + "ok2.gif");

	// Creating the window which allows adding and editing a given breakpoint.
	private BreakpointVariablesWindow m_variables = new BreakpointVariablesWindow();

	// The selected row in the breakpoint table.
	private int m_selectedRowIndex = -1;

	/**
	 * Constructs a new BreakpointWindow.
	 */
	public BreakpointWindow() {
		super("Breakpoint Panel");
		m_breakpoints = new Vector<Breakpoint>();
		m_model = new BreakpointTableModel();
		m_breakpointTable = new JTable(m_model);
		m_breakpointTable.setDefaultRenderer(m_breakpointTable.getColumnClass(0), m_coloredRenderer);
		m_listeners = new Vector<BreakpointsChangedListener>();
		setResizable(false);

		jbInit();
	}

	/**
	 * Registers the given BreakpointChangedListener as a listener to this
	 * component.
	 */
	public void addBreakpointListener(BreakpointsChangedListener listener) {
		m_listeners.addElement(listener);
	}

	/**
	 * Implementing the action of pressing the add button.
	 */
	public void addButton_actionPerformed() {
		m_breakpointTable.clearSelection();
		m_selectedRowIndex = -1;
		m_variables.setNameCombo(0);
		m_variables.setBreakpointName("");
		m_variables.setBreakpointValue("");
		m_variables.showWindow();
	}

	/**
	 * Called when there was a change in one of the breakpoints. The event
	 * contains the changed breakpoint.
	 */
	@Override
	public void breakpointChanged(BreakpointChangedEvent event) {
		Breakpoint p = event.getBreakpoint();
		if (m_selectedRowIndex == -1) {
			m_breakpoints.addElement(p);
		} else {
			m_breakpoints.setElementAt(p, m_selectedRowIndex);
		}
		m_breakpointTable.revalidate();
		repaint();
		notifyListeners();
	}

	/**
	 * Returns the breakpoints table.
	 */
	public JTable getTable() {
		return m_breakpointTable;
	}

	// Initializes this window.
	private void jbInit() {
		m_variables.addListener(this);
		this.getContentPane().setLayout(m_flowLayout);
		m_breakpointTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		m_breakpointTable.addMouseListener(this);
		m_breakpointTable.getSelectionModel();
		JScrollPane scrollPane = new JScrollPane(m_breakpointTable);
		scrollPane.setPreferredSize(new Dimension(190, 330));
		m_addButton.setPreferredSize(new Dimension(35, 25));
		m_addButton.setToolTipText("Add breakpoint");
		m_addButton.setIcon(m_addIcon);
		m_addButton.addActionListener(e -> addButton_actionPerformed());
		m_removeButton.setPreferredSize(new Dimension(35, 25));
		m_removeButton.setToolTipText("Remove breakpoint");
		m_removeButton.setIcon(m_removeIcon);
		m_removeButton.addActionListener(e -> removeButton_actionPerformed());
		m_okButton.setPreferredSize(new Dimension(35, 25));
		m_okButton.setToolTipText("OK");
		m_okButton.setIcon(m_okIcon);
		m_okButton.addActionListener(e -> okButton_actionPerformed());
		this.getContentPane().add(scrollPane, null);
		this.getContentPane().add(m_addButton, null);
		this.getContentPane().add(m_removeButton, null);
		this.getContentPane().add(m_okButton, null);
		setSize(210, 410);
		setLocation(250, 250);
	}

	/**
	 * Implementing the action of double-clicking the mouse on the table.
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			int row = m_breakpointTable.getSelectedRow();
			m_selectedRowIndex = row;
			m_variables.setBreakpointName(m_breakpoints.elementAt(row).getVarName());
			m_variables.setBreakpointValue(m_breakpoints.elementAt(row).getValue());
			// variables.setVisible(true);
			m_variables.showWindow();
		}

	}

	/**
	 * Empty implementation.
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
	}

	/**
	 * Empty implementation.
	 */
	@Override
	public void mouseExited(MouseEvent e) {
	}

	/**
	 * Empty implementation.
	 */
	@Override
	public void mousePressed(MouseEvent e) {
	}

	/**
	 * Empty implementation.
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
	}

	/**
	 * Notifies all the BreakpointChangedListeners on actions taken in it, by
	 * creating a BreakpointChangedEvent and sending it using the
	 * breakpointChanged method to all of the listeners.
	 */
	public void notifyListeners() {
		BreakpointsChangedEvent event = new BreakpointsChangedEvent(this, m_breakpoints);
		for (int i = 0; i < m_listeners.size(); i++) {
			m_listeners.elementAt(i).breakpointsChanged(event);
		}
	}

	/**
	 * Implementing the action of pressing the ok button.
	 */
	public void okButton_actionPerformed() {
		setVisible(false);
	}

	/**
	 * Un-registers the given BreakpointChangedListener from being a listener to
	 * this component.
	 */
	public void removeBreakpointListener(BreakpointsChangedListener listener) {
		m_listeners.removeElement(listener);
	}

	/**
	 * Implementing the action of pressing the remove button.
	 */
	public void removeButton_actionPerformed() {
		int selectedRow = m_breakpointTable.getSelectedRow();
		if ((selectedRow >= 0) && (selectedRow < m_breakpointTable.getRowCount())) {
			m_model.removeRow(m_breakpointTable.getSelectedRow());
			m_breakpointTable.revalidate();
			notifyListeners();
		}
	}

	/**
	 * Sets the breakpoints list with the given one.
	 */
	@SuppressWarnings("unchecked")
	public void setBreakpoints(Vector<Breakpoint> breakpoints) {
		this.m_breakpoints = (Vector<Breakpoint>) breakpoints.clone();
		m_breakpointTable.revalidate();
	}

	/**
	 * Sets the list of recognized variables with the given one.
	 */
	public void setVariables(String[] newVars) {
		m_variables.setVariables(newVars);
	}
}
