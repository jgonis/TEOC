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
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import Hack.ComputerParts.ComputerPartEvent;
import Hack.ComputerParts.ComputerPartEventListener;
import Hack.ComputerParts.RegisterGUI;
import Hack.Events.ErrorEvent;
import Hack.Events.ErrorEventListener;

/**
 * This class represents the GUI of a register.
 */
public class RegisterComponent extends JPanel implements RegisterGUI {

	// The label with the name of this register.
	protected JLabel registerName = new JLabel();

	// The text field containing the value of this register.
	protected JTextField registerValue = new JTextField();

	// A vector containing the listeners to this object.
	private Vector listeners;

	// A vector containing the error listeners to this object.
	private Vector errorEventListeners;

	// The value of the register
	protected short value;

	// The old value of this component
	protected String oldValue;

	// The format in which the value is represented: decimal, hexadecimal or
	// binary format.
	protected int dataFormat;

	// The null value of this component.
	protected short nullValue;

	// A boolean field specifying if the null value should be activated or not.
	protected boolean hideNullValue;

	/**
	 * Constructs a new RegisterComponent.
	 */
	public RegisterComponent() {
		dataFormat = Format.DEC_FORMAT;
		listeners = new Vector();
		errorEventListeners = new Vector();
		// initializes the register
		value = 0;
		registerValue.setText(translateValueToString(value));

		jbInit();
		setVisible(true);
	}

	/**
	 * Registers the given ErrorEventListener as a listener to this GUI.
	 */
	@Override
	public void addErrorListener(ErrorEventListener listener) {
		errorEventListeners.addElement(listener);
	}

	@Override
	public void addListener(ComputerPartEventListener listener) {
		listeners.addElement(listener);
	}

	/**
	 * Disables user input into the source.
	 */
	@Override
	public void disableUserInput() {
		registerValue.setEnabled(false);
	}

	/**
	 * Enables user input into the source.
	 */
	@Override
	public void enableUserInput() {
		registerValue.setEnabled(true);
	}

	/**
	 * flashes the value at the given index.
	 */
	@Override
	public void flash(int index) {
		registerValue.setBackground(Color.orange);
	}

	/**
	 * Returns the coordinates of the top left corner of the value at the given
	 * index.
	 */
	@Override
	public Point getCoordinates(int index) {
		Point location = getLocation();
		return new Point((int) location.getX() + registerValue.getX(), (int) location.getY() + registerValue.getY());
	}

	/**
	 * Returns the value at the given index in its string representation.
	 */
	@Override
	public String getValueAsString(int index) {
		return registerValue.getText();
	}

	/**
	 * hides the existing flash.
	 */
	@Override
	public void hideFlash() {
		registerValue.setBackground(Color.white);
	}

	/**
	 * Hides all highlightes.
	 */
	@Override
	public void hideHighlight() {
		registerValue.setForeground(Color.black);
	}

	/**
	 * Highlights the value at the given index.
	 */
	@Override
	public void highlight(int index) {
		registerValue.setForeground(Color.blue);
	}

	// Initializes this register.
	private void jbInit() {
		registerValue.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				registerValue_focusGained(e);
			}

			@Override
			public void focusLost(FocusEvent e) {
				registerValue_focusLost(e);
			}
		});

		registerName.setFont(Utilities.labelsFont);
		registerName.setBounds(new Rectangle(8, 3, 41, 18));
		this.setLayout(null);
		registerValue.setFont(Utilities.valueFont);
		registerValue.setDisabledTextColor(Color.black);
		registerValue.setHorizontalAlignment(SwingConstants.RIGHT);
		registerValue.setBounds(new Rectangle(36, 3, 124, 18));
		registerValue.addActionListener(e -> registerValue_actionPerformed(e));
		this.add(registerValue, null);
		this.add(registerName, null);

		setPreferredSize(new Dimension(164, 24));
		setSize(164, 24);
		setBorder(BorderFactory.createEtchedBorder());
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

	@Override
	public void notifyListeners(int address, short value) {
		ComputerPartEvent event = new ComputerPartEvent(this, 0, value);
		for (int i = 0; i < listeners.size(); i++) {
			((ComputerPartEventListener) listeners.elementAt(i)).valueChanged(event);
		}
	}

	/**
	 * Implements the action of changing the text of this register.
	 */
	public void registerValue_actionPerformed(ActionEvent e) {
		valueChanged();
	}

	/**
	 * The action of the text field gaining the focus.
	 */
	public void registerValue_focusGained(FocusEvent e) {
		oldValue = registerValue.getText();
		notifyListeners();
	}

	/**
	 * The action of the text field loosing the focus.
	 */
	public void registerValue_focusLost(FocusEvent e) {
		valueChanged();
	}

	/**
	 * Un-registers the given ErrorEventListener from being a listener to this
	 * GUI.
	 */
	@Override
	public void removeErrorListener(ErrorEventListener listener) {
		errorEventListeners.removeElement(listener);
	}

	@Override
	public void removeListener(ComputerPartEventListener listener) {
		listeners.removeElement(listener);
	}

	/**
	 * Resets the contents of this RegisterComponent.
	 */
	@Override
	public void reset() {
		value = nullValue;
		if (hideNullValue) {
			oldValue = "";
		}
		registerValue.setText(translateValueToString(nullValue));
		hideFlash();
		hideHighlight();
	}

	/**
	 * Sets the enabled range of this segment. Any address outside this range
	 * will be disabled for user input. If gray is true, addresses outside the
	 * range will be gray colored.
	 */
	@Override
	public void setEnabledRange(int start, int end, boolean gray) {
	}

	@Override
	public void setName(String name) {
		registerName.setText(name);
	}

	/**
	 * Sets the null value of this component.
	 */
	@Override
	public void setNullValue(short newValue, boolean hideNullValue) {
		nullValue = newValue;
		this.hideNullValue = hideNullValue;
		if ((value == nullValue) && hideNullValue) {
			oldValue = "";
		}
	}

	/**
	 * Sets the numeric format with the given code (out of the format constants
	 * in HackController).
	 */
	@Override
	public void setNumericFormat(int formatCode) {
		dataFormat = formatCode;
		registerValue.setText(Format.translateValueToString(value, formatCode));
	}

	/**
	 * Sets the value of the register with the given value.
	 */
	@Override
	public void setValueAt(int index, short value) {
		String data = translateValueToString(value);
		this.value = value;
		registerValue.setText(data);
	}

	/**
	 * Translates a given short to a string according to the current format.
	 */
	protected String translateValueToString(short value) {
		if (hideNullValue) {
			if (value == nullValue) {
				return "";
			} else {
				return Format.translateValueToString(value, dataFormat);
			}
		} else {
			return Format.translateValueToString(value, dataFormat);
		}
	}

	// Implementing the action of changing the register's value.
	private void valueChanged() {
		String text = registerValue.getText();
		if (!text.equals(oldValue)) {
			try {
				value = Format.translateValueToShort(text, dataFormat);
				notifyListeners(0, value);
				oldValue = text;
			} catch (NumberFormatException nfe) {
				notifyErrorListeners("Illegal value");
				registerValue.setText(translateValueToString(value));
			}
		}
	}
}
