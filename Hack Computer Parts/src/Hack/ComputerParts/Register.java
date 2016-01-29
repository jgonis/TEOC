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

package Hack.ComputerParts;

/**
 * A computer register. Holds a 16-bit value.
 */
public class Register extends InteractiveValueComputerPart {

	// The 16-bit value.
	protected short m_value;

	// The register's gui component
	protected RegisterGUI m_gui;

	/**
	 * Constructs a new register with the given GUI component (optional).
	 */
	public Register(RegisterGUI gui) {
		super(gui != null);
		init(gui);
	}

	/**
	 * Constructs a new register with the given GUI component and the legal
	 * values range.
	 */
	public Register(RegisterGUI gui, short minValue, short maxValue) {
		super(gui != null, minValue, maxValue);
		init(gui);
	}

	@Override
	public void doSetValueAt(int index, short value) {
		m_value = value;
	}

	/**
	 * Returns the value of the register.
	 */
	public short get() {
		return getValueAt(0);
	}

	@Override
	public ComputerPartGUI getGUI() {
		return m_gui;
	}

	@Override
	public short getValueAt(int index) {
		return m_value;
	}

	// Initalizes the register
	private void init(RegisterGUI gui) {
		m_gui = gui;

		if (hasGUI) {
			gui.addListener(this);
			gui.addErrorListener(this);
		}
	}

	@Override
	public void refreshGUI() {
		super.refreshGUI();

		if (displayChanges) {
			quietUpdateGUI(0, m_value);
		}
	}

	@Override
	public void reset() {
		super.reset();
		m_value = nullValue;
	}

	/**
	 * Sets the value of the register with the given value.
	 */
	public void store(short value) {
		setValueAt(0, value, false);
	}
}
