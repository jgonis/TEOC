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

import java.awt.Rectangle;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

/**
 * This class represents a search window for the use of MemoryComponent,
 * ROMComponent, and ProgramComponent. The user must enter the location in
 * hexadecimal format.
 */
public class SearchMemoryWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 812906774078458868L;

	// Creating the label in this window.
	private JLabel instructionLbl = new JLabel();

	// Creating the text field in this window.
	private JTextField rowNumber = new JTextField();

	// Creating buttons.
	private JButton okButton = new JButton();
	private JButton cancelButton = new JButton();

	// Creating icons
	private ImageIcon okIcon;
	private ImageIcon cancelIcon;

	// The table to search in.
	private JTable table;

	// The container panel of the table
	private JPanel tableContainer;

	/**
	 * Constructs a new SearchWindow.
	 */
	public SearchMemoryWindow(JPanel tableContainer, JTable table) {
		super("Go to");
		this.table = table;
		this.tableContainer = tableContainer;
		try {
			okIcon = new ImageIcon(Paths.get(ClassLoader.getSystemResource("ok.gif").toURI()).toString());
			cancelIcon = new ImageIcon(Paths.get(ClassLoader.getSystemResource("cancel.gif").toURI()).toString());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		jbInit();
	}

	/**
	 * Implementing the action of pressing the cancel button.
	 */
	public void cancelButton_actionPerformed() {
		setVisible(false);
	}

	// Initialization of this component.
	private void jbInit() {
		instructionLbl.setFont(Utilities.thinLabelsFont);
		instructionLbl.setText("Enter Address :");
		instructionLbl.setBounds(new Rectangle(9, 22, 132, 23));
		this.getContentPane().setLayout(null);
		rowNumber.setBounds(new Rectangle(102, 25, 158, 18));
		rowNumber.addActionListener(e -> rowNumber_actionPerformed());
		okButton.setToolTipText("Ok");
		okButton.setIcon(okIcon);
		okButton.setBounds(new Rectangle(49, 60, 63, 44));
		okButton.addActionListener(e -> okButton_actionPerformed());
		cancelButton.setBounds(new Rectangle(176, 60, 63, 44));
		cancelButton.addActionListener(e -> cancelButton_actionPerformed());
		cancelButton.setToolTipText("Cancel");
		cancelButton.setIcon(cancelIcon);
		this.getContentPane().add(instructionLbl, null);
		this.getContentPane().add(rowNumber, null);
		this.getContentPane().add(okButton, null);
		this.getContentPane().add(cancelButton, null);

		setSize(300, 150);
		setLocation(250, 250);
	}

	/**
	 * Implementing the action of pressing the OK button.
	 */
	public void okButton_actionPerformed() {
		try {
			int row = Format.translateValueToShort(rowNumber.getText(), Format.DEC_FORMAT);
			table.setRowSelectionInterval(row, row);
			Utilities.tableCenterScroll(tableContainer, table, row);
			setVisible(false);
		} catch (NumberFormatException nfe) {
		} catch (IllegalArgumentException iae) {
		}

	}

	/**
	 * Implementing the action of pressing 'enter' on the text field (this
	 * action is similar to pressing the ok button).
	 */
	public void rowNumber_actionPerformed() {
		okButton_actionPerformed();
	}

	/**
	 * Shows the memory window.
	 */
	public void showWindow() {
		setVisible(true);
		rowNumber.requestFocus();
	}
}
