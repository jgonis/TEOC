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
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import Hack.Events.ErrorEvent;
import Hack.Events.ErrorEventListener;
import Hack.Events.ProgramEvent;
import Hack.Events.ProgramEventListener;
import Hack.VMEmulator.VMEmulatorInstruction;
import Hack.VMEmulator.VMProgramGUI;
import Hack.VirtualMachine.HVMInstruction;
import HackGUI.MouseOverJButton;
import HackGUI.Utilities;

/**
 * This class represents the gui of a Program.
 */
public class ProgramComponent extends JPanel implements VMProgramGUI {

	private static final long serialVersionUID = 2365635652188891777L;

	// An inner class which implemets the cell renderer of the program table,
	// giving
	// the feature of coloring the background of a specific cell.
	class ColoredTableCellRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 3308953547155138893L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused,
				int row, int column) {
			setEnabled((table == null) || table.isEnabled());
			setBackground(null);
			setForeground(null);

			if (column == 0) {
				setHorizontalAlignment(SwingConstants.CENTER);
			} else {
				setHorizontalAlignment(SwingConstants.LEFT);
			}
			if (row == m_instructionIndex) {
				setBackground(Color.yellow);
			} else {
				HVMInstruction currentInstruction = m_instructions[row];
				String op = (currentInstruction.getFormattedStrings())[0];
				if (op.equals("function") && ((column == 1) || (column == 2))) {
					setBackground(new Color(190, 171, 210));
				}
			}

			super.getTableCellRendererComponent(table, value, selected, focused, row, column);

			return this;
		}
	}

	// An inner class representing the model of the CallStack table.
	class ProgramTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 8847588244074393662L;

		/**
		 * Returns the number of columns.
		 */
		@Override
		public int getColumnCount() {
			return 3;
		}

		/**
		 * Returns the names of the columns.
		 */
		@Override
		public String getColumnName(int col) {
			return null;
		}

		/**
		 * Returns the number of rows.
		 */
		@Override
		public int getRowCount() {
			return m_instructions.length;
		}

		/**
		 * Returns the value at a specific row and column.
		 */
		@Override
		public Object getValueAt(int row, int col) {

			String[] formattedString = m_instructions[row].getFormattedStrings();

			switch (col) {
			case 0:
				short index = m_instructions[row].getIndexInFunction();
				if (index >= 0) {
					return new Short(index);
				} else {
					return "";
				}
			case 1:
				return formattedString[0];
			case 2:
				return formattedString[1] + " " + formattedString[2];
			default:
				return null;

			}
		}

		/**
		 * Returns true of this table cells are editable, false - otherwise.
		 */
		@Override
		public boolean isCellEditable(int row, int col) {
			return false;
		}
	}

	// A vector containing the listeners to this object.
	private Vector<ProgramEventListener> m_listeners;

	// A vector containing the error listeners to this object.
	private Vector<ErrorEventListener> m_errorEventListeners;

	// The table representing this program
	protected JTable m_programTable;

	// The model of the table;
	private ProgramTableModel m_model;

	// The HVMInstructions of this program.
	protected VMEmulatorInstruction[] m_instructions;

	// Creating the browse button.
	protected MouseOverJButton m_browseButton = new MouseOverJButton();

	// Creating the icon of the button.
	private ImageIcon m_browseIcon = new ImageIcon(Utilities.imagesDir + "open2.gif");

	// The file chooser window.
	// private FileChooserWindow fileChooser = new FileChooserWindow(null);
	private JFileChooser m_fileChooser = new JFileChooser();

	// The current instruction index (yellow background).
	private int m_instructionIndex;

	// The text field with the message (for example "Loading...").
	private JTextField m_messageTxt = new JTextField();

	// The cell renderer of this table.
	private ColoredTableCellRenderer m_coloredRenderer = new ColoredTableCellRenderer();

	// Creating the search button.
	private MouseOverJButton m_searchButton = new MouseOverJButton();

	// Creating the icon for the search button.
	private ImageIcon m_searchIcon = new ImageIcon(Utilities.imagesDir + "find.gif");

	// The window of searching a specific location in memory.
	private SearchProgramWindow m_searchWindow;

	// The scroll pane on which the table is placed.
	private JScrollPane m_scrollPane;

	// The name of this component ("Program :").
	private JLabel m_nameLbl = new JLabel();

	// Creating the clear button.
	protected MouseOverJButton m_clearButton = new MouseOverJButton();

	// Creating the icon for the clear button.
	private ImageIcon m_clearIcon = new ImageIcon(Utilities.imagesDir + "smallnew.gif");

	/**
	 * Constructs a new ProgramComponent.
	 */
	public ProgramComponent() {
		m_listeners = new Vector<ProgramEventListener>();
		m_errorEventListeners = new Vector<ErrorEventListener>();
		m_instructions = new VMEmulatorInstruction[0];
		m_model = new ProgramTableModel();
		m_programTable = new JTable(m_model);
		m_programTable.setDefaultRenderer(m_programTable.getColumnClass(0), m_coloredRenderer);
		m_searchWindow = new SearchProgramWindow(m_programTable, m_instructions);

		jbInit();

	}

	/**
	 * Registers the given ErrorEventListener as a listener to this GUI.
	 */
	@Override
	public void addErrorListener(ErrorEventListener listener) {
		m_errorEventListeners.addElement(listener);
	}

	/**
	 * Registers the given ProgramEventListener as a listener to this GUI.
	 */
	@Override
	public void addProgramListener(ProgramEventListener listener) {
		m_listeners.addElement(listener);
	}

	/**
	 * Implementing the action of pressing the browse button.
	 */
	public void browseButton_actionPerformed(ActionEvent e) {
		loadProgram();
	}

	/**
	 * Implementing the action of pressing the clear button.
	 */
	public void clearButton_actionPerformed(ActionEvent e) {
		Object[] options = { "Yes", "No", "Cancel" };
		int pressedButtonValue = JOptionPane.showOptionDialog(this.getParent(),
				"Are you sure you want to clear the program?", "Warning Message", JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.WARNING_MESSAGE, null, options, options[2]);

		if (pressedButtonValue == JOptionPane.YES_OPTION) {
			notifyProgramListeners(ProgramEvent.CLEAR, null);
		}
	}

	/**
	 * Displays a confirmation window asking the user permission to use built-in
	 * vm functions
	 */
	@Override
	public boolean confirmBuiltInAccess() {
		String message = "No implementation was found for some functions which are called in the VM code.\n"
				+ "The VM Emulator provides built-in implementations for the OS functions.\n"
				+ "If available, should this built-in implementation be used for functions which were not implemented in the VM code?";
		return (JOptionPane.showConfirmDialog(this.getParent(), message, "Confirmation Message",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION);
	}

	// Determines the width of each column in the table.
	private void determineColumnWidth() {
		TableColumn column = null;
		for (int i = 0; i < 3; i++) {
			column = m_programTable.getColumnModel().getColumn(i);
			if (i == 0) {
				column.setPreferredWidth(30);
			} else if (i == 1) {
				column.setPreferredWidth(40);
			} else if (i == 2) {
				column.setPreferredWidth(100);
			}
		}
	}

	/**
	 * Returns the width of the table.
	 */
	public int getTableWidth() {
		return 225;
	}

	/**
	 * Hides the displayed message.
	 */
	@Override
	public void hideMessage() {
		m_messageTxt.setText("");
		m_messageTxt.setVisible(false);
		m_searchButton.setVisible(true);
		m_clearButton.setVisible(true);
		m_browseButton.setVisible(true);
	}

	// Initialization of this component.
	private void jbInit() {
		m_fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		m_fileChooser.setFileFilter(new VMFileFilter());
		m_programTable.getTableHeader().setReorderingAllowed(false);
		m_programTable.getTableHeader().setResizingAllowed(false);
		m_scrollPane = new JScrollPane(m_programTable);
		m_scrollPane.setLocation(0, 27);
		m_browseButton.setToolTipText("Load Program");
		m_browseButton.setIcon(m_browseIcon);
		m_browseButton.setBounds(new Rectangle(119, 2, 31, 24));
		m_browseButton.addActionListener(e -> browseButton_actionPerformed(e));
		m_messageTxt.setBackground(SystemColor.info);
		m_messageTxt.setEnabled(false);
		m_messageTxt.setFont(Utilities.labelsFont);
		m_messageTxt.setPreferredSize(new Dimension(70, 20));
		m_messageTxt.setDisabledTextColor(Color.red);
		m_messageTxt.setEditable(false);
		m_messageTxt.setBounds(new Rectangle(91, 2, 132, 23));
		m_messageTxt.setVisible(false);

		m_searchButton.setToolTipText("Search");
		m_searchButton.setIcon(m_searchIcon);
		m_searchButton.setBounds(new Rectangle(188, 2, 31, 24));
		m_searchButton.addActionListener(e -> searchButton_actionPerformed(e));
		this.setForeground(Color.lightGray);
		this.setLayout(null);
		m_nameLbl.setText("Program");
		m_nameLbl.setBounds(new Rectangle(5, 5, 73, 20));
		m_nameLbl.setFont(Utilities.labelsFont);

		m_clearButton.addActionListener(e -> clearButton_actionPerformed(e));
		m_clearButton.setBounds(new Rectangle(154, 2, 31, 24));
		m_clearButton.setIcon(m_clearIcon);
		m_clearButton.setToolTipText("Clear");
		this.add(m_scrollPane, null);
		this.add(m_nameLbl, null);
		this.add(m_searchButton, null);
		this.add(m_clearButton, null);
		this.add(m_messageTxt, null);
		this.add(m_browseButton, null);
		determineColumnWidth();
		m_programTable.setTableHeader(null);
		setBorder(BorderFactory.createEtchedBorder());
	}

	/**
	 * Opens the program file chooser for loading a program.
	 */
	public void loadProgram() {
		int returnVal = m_fileChooser.showDialog(this, "Load Program");
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			notifyProgramListeners(ProgramEvent.LOAD, m_fileChooser.getSelectedFile().getAbsolutePath());
		}
	}

	/**
	 * Displays a notification window with the given message.
	 */
	@Override
	public void notify(String message) {
		JOptionPane.showMessageDialog(this.getParent(), message, "Information Message",
				JOptionPane.INFORMATION_MESSAGE);
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

	/**
	 * Notifies all the ProgramEventListeners on a change in the program by
	 * creating a ProgramEvent (with the new event type and program's directory
	 * name) and sending it using the programChanged method to all the
	 * listeners.
	 */
	@Override
	public void notifyProgramListeners(byte eventType, String programFileName) {
		ProgramEvent event = new ProgramEvent(this, eventType, programFileName);
		for (int i = 0; i < m_listeners.size(); i++) {
			((ProgramEventListener) m_listeners.elementAt(i)).programChanged(event);
		}
	}

	/**
	 * Un-registers the given ErrorEventListener from being a listener to this
	 * GUI.
	 */
	@Override
	public void removeErrorListener(ErrorEventListener listener) {
		m_errorEventListeners.removeElement(listener);
	}

	/**
	 * Un-registers the given ProgramEventListener from being a listener to this
	 * GUI.
	 */
	@Override
	public void removeProgramListener(ProgramEventListener listener) {
		m_listeners.removeElement(listener);
	}

	/**
	 * Resets the contents of this ProgramComponent.
	 */
	@Override
	public void reset() {
		m_instructions = new VMEmulatorInstruction[0];
		m_programTable.clearSelection();
		repaint();
	}

	/**
	 * Implementing the action of pressing the search button.
	 */
	public void searchButton_actionPerformed(ActionEvent e) {
		m_searchWindow.showWindow();
	}

	/**
	 * Sets the contents of the gui with the first instructionsLength
	 * instructions from the given array of instructions.
	 */
	@Override
	public synchronized void setContents(VMEmulatorInstruction[] newInstructions, int newInstructionsLength) {
		m_instructions = new VMEmulatorInstruction[newInstructionsLength];
		System.arraycopy(newInstructions, 0, m_instructions, 0, newInstructionsLength);
		m_programTable.revalidate();
		try {
			wait(100);
		} catch (InterruptedException ie) {
		}
		m_searchWindow.setInstructions(m_instructions);
	}

	/**
	 * Sets the current instruction with the given instruction index.
	 */
	@Override
	public void setCurrentInstruction(int instructionIndex) {
		this.m_instructionIndex = instructionIndex;
		Utilities.tableCenterScroll(this, m_programTable, instructionIndex);
	}

	/**
	 * Sets the name label.
	 */
	public void setNameLabel(String name) {
		m_nameLbl.setText(name);
	}

	/**
	 * Sets the number of visible rows.
	 */
	public void setVisibleRows(int num) {
		int tableHeight = num * m_programTable.getRowHeight();
		m_scrollPane.setSize(getTableWidth(), tableHeight + 3);
		setPreferredSize(new Dimension(getTableWidth(), tableHeight + 30));
		setSize(getTableWidth(), tableHeight + 30);
	}

	/**
	 * Sets the working directory with the given directory File.
	 */
	public void setWorkingDir(File file) {
		m_fileChooser.setCurrentDirectory(file);
	}

	/**
	 * Displays the given message.
	 */
	@Override
	public void showMessage(String message) {
		m_messageTxt.setText(message);
		m_messageTxt.setVisible(true);
		m_searchButton.setVisible(false);
		m_clearButton.setVisible(false);
		m_browseButton.setVisible(false);
	}
}
