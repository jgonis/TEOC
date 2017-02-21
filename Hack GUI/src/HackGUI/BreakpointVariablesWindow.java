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
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import Hack.Controller.Breakpoint;

/**
 * This class represents the window of adding or editing a breakpoint.
 */
public class BreakpointVariablesWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8241104878264322642L;
	
	// Creating labels.
	private JLabel nameLbl = new JLabel();
	private JLabel valueLbl = new JLabel();

	// Creating text fields.
	private JTextField nameTxt = new JTextField();
	private JTextField valueTxt = new JTextField();

	// Creating the combo box of variables.
	private JComboBox<String> nameCombo = new JComboBox<String>();

	// Creating the ok and cancel buttons.
	private JButton okButton = new JButton();
	private JButton cancelButton = new JButton();

	// Creating ok and cancel icons.
	private ImageIcon okIcon;
	private ImageIcon cancelIcon;

	// A vector conatining the listeners to this component.
	private Vector<BreakpointChangedListener> listeners;

	// The breakpoint which is being added or changed.
	private Breakpoint breakpoint;

	/**
	 * Constructing a new BreakpointVariablesWindow.
	 */
	public BreakpointVariablesWindow() {
		super("Breakpoint Variables");
		listeners = new Vector<BreakpointChangedListener>();
		ClassLoader classLoader = getClass().getClassLoader();

		okIcon = new ImageIcon(classLoader.getResource("ok.gif"));
		cancelIcon = new ImageIcon(classLoader.getResource("cancel.gif"));
		jbInit();
	}

	/**
	 * Registers the given BreakpointChangedListener as a listener to this
	 * component.
	 */
	public void addListener(BreakpointChangedListener listener) {
		listeners.addElement(listener);
	}

	/**
	 * Implementing the action of pressing the cancel button.
	 */
	public void cancelButton_actionPerformed() {
		setVisible(false);
	}

	// Initializes this component.
	private void jbInit() {
		nameLbl.setFont(Utilities.thinLabelsFont);
		nameLbl.setText("Name :");
		nameLbl.setBounds(new Rectangle(9, 10, 61, 19));
		this.getContentPane().setLayout(null);
		valueLbl.setBounds(new Rectangle(9, 42, 61, 19));
		valueLbl.setFont(Utilities.thinLabelsFont);
		valueLbl.setText("Value :");
		nameTxt.setBounds(new Rectangle(53, 10, 115, 19));
		valueTxt.setBounds(new Rectangle(53, 42, 115, 19));
		nameCombo.setBounds(new Rectangle(180, 10, 124, 19));
		nameCombo.addActionListener(e -> nameCombo_actionPerformed());
		okButton.setToolTipText("Ok");
		okButton.setIcon(okIcon);
		okButton.setBounds(new Rectangle(61, 74, 63, 44));
		okButton.addActionListener(e -> okButton_actionPerformed());
		cancelButton.setBounds(new Rectangle(180, 74, 63, 44));
		cancelButton.addActionListener(e -> cancelButton_actionPerformed());
		cancelButton.setToolTipText("Cancel");
		cancelButton.setIcon(cancelIcon);
		this.getContentPane().add(nameLbl, null);
		this.getContentPane().add(valueLbl, null);
		this.getContentPane().add(nameTxt, null);
		this.getContentPane().add(valueTxt, null);
		this.getContentPane().add(okButton, null);
		this.getContentPane().add(cancelButton, null);
		this.getContentPane().add(nameCombo, null);

		setSize(320, 160);
		setLocation(500, 250);
	}

	/**
	 * Implementing the action of changing the selected item in the combo box.
	 */
	public void nameCombo_actionPerformed() {
		String name = (String) nameCombo.getSelectedItem();
		nameTxt.setText(name);
	}

	/**
	 * Notify all the BreakpointChangedListeners on actions taken in it, by
	 * creating a BreakpointChangedEvent and sending it using the
	 * breakpointChanged method to all of the listeners.
	 */
	public void notifyListeners() {
		BreakpointChangedEvent event = new BreakpointChangedEvent(this, breakpoint);
		for (int i = 0; i < listeners.size(); i++) {
			listeners.elementAt(i).breakpointChanged(event);
		}
	}

	/**
	 * Implementing the action of pressing the ok button.
	 */
	public void okButton_actionPerformed() {
		breakpoint = new Breakpoint(nameTxt.getText(), valueTxt.getText());
		setVisible(false);
		notifyListeners();
	}

	/**
	 * Un-registers the given BreakpointChangedListener from being a listener to
	 * this component.
	 */
	public void removeListener(BreakpointChangedListener listener) {
		listeners.removeElement(listener);
	}

	/**
	 * Sets the name of the breakpoint.
	 */
	public void setBreakpointName(String name) {
		nameTxt.setText(name);
	}

	/**
	 * Sets the value of the breakpoint.
	 */
	public void setBreakpointValue(String value) {
		valueTxt.setText(value);
	}

	/**
	 * Sets the selected value in the combobox to the given index.
	 */
	public void setNameCombo(int index) {
		nameCombo.setSelectedIndex(index);
	}

	/**
	 * Sets the list of recognized variables with the given one.
	 */
	public void setVariables(String[] newVars) {
		for (String newVar : newVars) {
			nameCombo.addItem(newVar);
		}
	}

	/**
	 * Shows the breakpoint variables window.
	 */
	public void showWindow() {
		nameTxt.requestFocus();
		setVisible(true);
	}
}
