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

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;

import Hack.VirtualMachine.HVMInstruction;
import HackGUI.Utilities;

/**
 * This class represents a search window for the use of ProgramComponent.
 */
public class SearchProgramWindow extends JFrame {

	private static final long serialVersionUID = 1867258185736199360L;

	// creating the label of this window
	private JLabel m_instructionLbl = new JLabel();

	// creating the text field of this window.
	private JTextField m_instruction = new JTextField();

	// creating ok and cancel buttons.
	private JButton m_okButton = new JButton();
	private JButton m_cancelButton = new JButton();

	// creating ok and cancel icons.
	private ImageIcon m_okIcon = new ImageIcon(Utilities.imagesDir + "ok.gif");
	private ImageIcon m_cancelIcon = new ImageIcon(Utilities.imagesDir + "cancel.gif");

	// The table of this component.
	private JTable m_table;

	// an array containing the HVMInstructions.
	private HVMInstruction[] m_instructions;

	/**
	 * Constructs a new SearchWindow.
	 */
	public SearchProgramWindow(JTable table, HVMInstruction[] instructions) {
		super("Search");
		this.m_table = table;

		jbInit();

	}

	/**
	 * Implementing the action of pressing the cancel button.
	 */
	public void cancelButton_actionPerformed(ActionEvent e) {
		setVisible(false);
	}

	/**
	 * Returns the index of row which the user searched for.
	 */
	private int getSearchedRowIndex() {
		int rowIndex = -1;
		String searchedStr = m_instruction.getText();
		StringTokenizer tokenizer = new StringTokenizer(searchedStr);
		String firstToken = "", secondToken = "", thirdToken = "";
		int numOfTokens = tokenizer.countTokens();
		switch (numOfTokens) {
		case 0:
			break;
		case 1:
			firstToken = tokenizer.nextToken();
			for (int i = 0; i < m_instructions.length; i++) {
				String[] formattedStr = m_instructions[i].getFormattedStrings();
				if (formattedStr[0].equalsIgnoreCase(firstToken)) {
					rowIndex = i;
					break;
				}
			}
			break;
		case 2:
			firstToken = tokenizer.nextToken();
			secondToken = tokenizer.nextToken();
			for (int i = 0; i < m_instructions.length; i++) {
				String[] formattedStr = m_instructions[i].getFormattedStrings();
				if (formattedStr[0].equalsIgnoreCase(firstToken) && formattedStr[1].equalsIgnoreCase(secondToken)) {
					rowIndex = i;
					break;
				}
			}
			break;
		case 3:
			firstToken = tokenizer.nextToken();
			secondToken = tokenizer.nextToken();
			thirdToken = tokenizer.nextToken();

			for (int i = 0; i < m_instructions.length; i++) {
				String[] formattedStr = m_instructions[i].getFormattedStrings();
				if (formattedStr[0].equalsIgnoreCase(firstToken) && formattedStr[1].equalsIgnoreCase(secondToken)
						&& formattedStr[2].equalsIgnoreCase(thirdToken)) {
					rowIndex = i;
					break;
				}
			}
			break;
		default:
			break;
		}
		return rowIndex;
	}

	/**
	 * Implementing the action of pressing 'enter' on the text field.
	 */
	public void instruction_actionPerformed(ActionEvent e) {
		okButton_actionPerformed(e);
	}

	// Initialization of this component.
	private void jbInit() {
		m_instructionLbl.setFont(Utilities.thinLabelsFont);
		m_instructionLbl.setText("Text to find :");
		m_instructionLbl.setBounds(new Rectangle(9, 22, 79, 23));
		this.getContentPane().setLayout(null);
		m_instruction.setBounds(new Rectangle(82, 25, 220, 18));
		m_instruction.addActionListener(e -> instruction_actionPerformed(e));
		m_okButton.setToolTipText("OK");
		m_okButton.setIcon(m_okIcon);
		m_okButton.setBounds(new Rectangle(66, 68, 63, 44));
		m_okButton.addActionListener(e -> okButton_actionPerformed(e));
		m_cancelButton.setBounds(new Rectangle(190, 68, 63, 44));
		m_cancelButton.addActionListener(e -> cancelButton_actionPerformed(e));
		m_cancelButton.setToolTipText("CANCEL");
		m_cancelButton.setIcon(m_cancelIcon);
		this.getContentPane().add(m_instruction, null);
		this.getContentPane().add(m_okButton, null);
		this.getContentPane().add(m_cancelButton, null);
		this.getContentPane().add(m_instructionLbl, null);

		setSize(320, 150);
		setLocation(250, 250);
	}

	/**
	 * Implementing the action of pressing the OK button.
	 */
	public void okButton_actionPerformed(ActionEvent e) {
		try {
			int row = getSearchedRowIndex();
			if (row != -1) {
				Rectangle r = m_table.getCellRect(row, 0, true);
				m_table.scrollRectToVisible(r);
				m_table.setRowSelectionInterval(row, row);
				setVisible(false);
			}
		} catch (NumberFormatException nfe) {
		} catch (IllegalArgumentException iae) {
		}
	}

	/**
	 * Sets the array of instructions of this search window.
	 */
	public void setInstructions(HVMInstruction[] instructions) {
		this.m_instructions = instructions;
	}

	/**
	 * Shows the search window.
	 */
	public void showWindow() {
		setVisible(true);
		m_instruction.requestFocus();
	}
}
