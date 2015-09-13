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
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableCellRenderer;

import Hack.Assembler.AssemblerException;
import Hack.Assembler.HackAssemblerTranslator;
import Hack.CPUEmulator.ROM;
import Hack.CPUEmulator.ROMGUI;
import Hack.Events.ProgramEvent;
import Hack.Events.ProgramEventListener;
import HackGUI.Format;
import HackGUI.MouseOverJButton;
import HackGUI.PointedMemoryComponent;
import HackGUI.TranslationException;
import HackGUI.Utilities;

/**
 * This class represents the GUI of a ROM.
 */
public class ROMComponent extends PointedMemoryComponent implements ROMGUI {

	private static final long serialVersionUID = 4704137661485714096L;

	/**
	 * An inner class which implemets the cell renderer of the rom table, giving
	 * the feature of coloring the background of a specific cell.
	 */
	public class ROMTableCellRenderer extends PointedMemoryTableCellRenderer {

		private static final long serialVersionUID = -1063111905556674224L;

		@Override
		public void setRenderer(int row, int column) {
			super.setRenderer(row, column);

			if ((dataFormat == ASM_FORMAT) && (column == 1)) {
				setHorizontalAlignment(SwingConstants.LEFT);
			}
		}
	}

	// The ASM format.
	private final static int ASM_FORMAT = ROM.ASM_FORMAT;

	// A vector containing the listeners to this object.
	private Vector<ProgramEventListener> m_programEventListeners;

	// The load file button.
	protected MouseOverJButton m_loadButton = new MouseOverJButton();

	// The icon on the load file button.
	private ImageIcon m_loadIcon = new ImageIcon(Utilities.imagesDir + "open2.gif");

	// The file filter of this component.
	private FileFilter m_filter;

	// The file chooser component.
	private JFileChooser m_fileChooser;

	// The hack assembler translator.
	private HackAssemblerTranslator m_translator = HackAssemblerTranslator.getInstance();

	// The text field containing the message (for example "Loading...").
	private JTextField m_messageTxt = new JTextField();

	// The possible numeric formats.
	//String[] format = { "Asm", "Dec", "Hex", "Bin" };
	private Vector<String> m_format = new Vector<String>(Arrays.asList("Asm", "Dec", "Hex", "Bin"));

	// The combo box for choosing the numeric format.
	protected JComboBox<String> m_romFormat = new JComboBox<String>(m_format);

	/**
	 * Constructs a new ROMComponent.
	 */
	public ROMComponent() {
		dataFormat = ASM_FORMAT;
		m_programEventListeners = new Vector<ProgramEventListener>();
		m_filter = new ROMFileFilter();
		m_fileChooser = new JFileChooser();
		m_fileChooser.setFileFilter(m_filter);
		jbInit();
	}

	/**
	 * Registers the given ProgramEventListener as a listener to this GUI.
	 */
	@Override
	public void addProgramListener(ProgramEventListener listener) {
		m_programEventListeners.addElement(listener);
	}

	@Override
	protected DefaultTableCellRenderer getCellRenderer() {
		return new ROMTableCellRenderer();
	}

	/**
	 * Returns the value at the given index in its string representation.
	 */
	@Override
	public String getValueAsString(int index) {
		if (dataFormat != ASM_FORMAT) {
			return super.getValueAsString(index);
		} else {
			return Format.translateValueToString(values[index], Format.DEC_FORMAT);
		}
	}

	/**
	 * Hides the displayed message.
	 */
	@Override
	public void hideMessage() {
		m_messageTxt.setText("");
		m_messageTxt.setVisible(false);
		m_loadButton.setVisible(true);
		searchButton.setVisible(true);
		m_romFormat.setVisible(true);
	}

	// Initializes this rom.
	private void jbInit() {
		m_loadButton.setIcon(m_loadIcon);
		m_loadButton.setBounds(new Rectangle(97, 2, 31, 25));
		m_loadButton.setToolTipText("Load Program");
		m_loadButton.addActionListener(e -> loadButton_actionPerformed(e));
		m_messageTxt.setBackground(SystemColor.info);
		m_messageTxt.setEnabled(false);
		m_messageTxt.setFont(Utilities.labelsFont);
		m_messageTxt.setPreferredSize(new Dimension(70, 20));
		m_messageTxt.setDisabledTextColor(Color.red);
		m_messageTxt.setEditable(false);
		m_messageTxt.setHorizontalAlignment(SwingConstants.CENTER);
		m_messageTxt.setBounds(new Rectangle(37, 3, 154, 22));
		m_messageTxt.setVisible(false);
		m_romFormat.setPreferredSize(new Dimension(125, 23));
		m_romFormat.setBounds(new Rectangle(39, 3, 56, 23));
		m_romFormat.setFont(Utilities.thinLabelsFont);
		m_romFormat.setToolTipText("Display Format");
		m_romFormat.addActionListener(e -> romFormat_actionPerformed(e));
		this.add(m_messageTxt, null);
		this.add(m_loadButton);
		this.add(m_romFormat, null);
	}

	/**
	 * Implements the action of clicking the load button.
	 */
	public void loadButton_actionPerformed(ActionEvent e) {
		loadProgram();
	}

	/**
	 * Opens the file chooser for loading a new program.
	 */
	public void loadProgram() {
		int returnVal = m_fileChooser.showDialog(this, "Load ROM");
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			notifyProgramListeners(ProgramEvent.LOAD, m_fileChooser.getSelectedFile().getAbsolutePath());
		}
	}

	/**
	 * Overrides to add the program clear functionality.
	 */
	@Override
	public void notifyClearListeners() {
		super.notifyClearListeners();
		notifyProgramListeners(ProgramEvent.CLEAR, null);
	}

	/**
	 * Notifies all the ProgramEventListeners on a change in the ROM's program
	 * by creating a ProgramEvent (with the new event type and program's file
	 * name) and sending it using the programChanged method to all the
	 * listeners.
	 */
	@Override
	public void notifyProgramListeners(byte eventType, String programFileName) {
		ProgramEvent event = new ProgramEvent(this, eventType, programFileName);
		for (int i = 0; i < m_programEventListeners.size(); i++) {
			((ProgramEventListener) m_programEventListeners.elementAt(i)).programChanged(event);
		}
	}

	/**
	 * Un-registers the given ProgramEventListener from being a listener to this
	 * GUI.
	 */
	@Override
	public void removeProgramListener(ProgramEventListener listener) {
		m_programEventListeners.removeElement(listener);
	}

	/**
	 * Implemeting the action of changing the selected item in the combo box
	 */
	public void romFormat_actionPerformed(ActionEvent e) {
		String newFormat = (String) m_romFormat.getSelectedItem();
		if (newFormat.equals(m_format.get(0))) {
			setNumericFormat(ASM_FORMAT);
		} else if (newFormat.equals(m_format.get(1))) {
			setNumericFormat(Format.DEC_FORMAT);
		} else if (newFormat.equals(m_format.get(2))) {
			setNumericFormat(Format.HEX_FORMAT);
		} else if (newFormat.equals(m_format.get(3))) {
			setNumericFormat(Format.BIN_FORMAT);
		}
	}

	@Override
	public void setNumericFormat(int formatCode) {
		super.setNumericFormat(formatCode);
		switch (formatCode) {
		case ASM_FORMAT:
			m_romFormat.setSelectedIndex(0);
			break;
		case Format.DEC_FORMAT:
			m_romFormat.setSelectedIndex(1);
			break;
		case Format.HEX_FORMAT:
			m_romFormat.setSelectedIndex(2);
			break;
		case Format.BIN_FORMAT:
			m_romFormat.setSelectedIndex(3);
			break;
		}
	}

	/**
	 * Sets the current program file name with the given name.
	 */
	@Override
	public void setProgram(String programFileName) {
	}

	/**
	 * Sets the current working dir.
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
		m_loadButton.setVisible(false);
		searchButton.setVisible(false);
		m_romFormat.setVisible(false);
		m_messageTxt.setVisible(true);
	}

	/**
	 * Translates a given string to a short according to the current format.
	 */
	@Override
	protected short translateValueToShort(String data) throws TranslationException {
		short result = 0;
		if (dataFormat != ASM_FORMAT) {
			result = super.translateValueToShort(data);
		} else {
			try {
				result = m_translator.textToCode(data);
			} catch (AssemblerException ae) {
				throw new TranslationException(ae.getMessage());
			}
		}
		return result;
	}

	/**
	 * Translates a given short to a string according to the current format.
	 */
	@Override
	protected String translateValueToString(short value) {
		String result = null;
		if (dataFormat != ASM_FORMAT) {
			result = super.translateValueToString(value);
		} else {
			try {
				result = m_translator.codeToText(value);
			} catch (AssemblerException ae) {
			}
		}
		return result;
	}
}
