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

package Hack.HardwareSimulator;

import Hack.ComputerParts.ComputerPartEvent;
import Hack.ComputerParts.ComputerPartGUI;
import Hack.ComputerParts.InteractiveValueComputerPart;
import Hack.Gates.GateClass;
import Hack.Gates.Node;
import Hack.Gates.PinInfo;

/**
 * Represents a collection of pins, using the Nodes implementation. Enables
 * changing pins values.
 */
public class Pins extends InteractiveValueComputerPart {

	// The gui of the Pins
	private PinsGUI m_gui;

	// The type of this pin (out of the type constants in GateClass)
	private byte type;

	// The nodes array
	private Node[] nodes;

	// The array of pins
	private PinInfo[] pins;

	/**
	 * Constructs a new Pins with the given pin type and Pins GUI.
	 */
	public Pins(byte type, PinsGUI gui) {
		super(gui != null);
		this.m_gui = gui;
		this.type = type;

		pins = new PinInfo[0];
		nodes = new Node[0];

		if (hasGUI) {
			m_gui.addListener(this);
			m_gui.addErrorListener(this);
			m_gui.setContents(pins);
		}
	}

	@Override
	public void doSetValueAt(int index, short value) {
		nodes[index].set(value);
	}

	/**
	 * Returns the number of pins.
	 */
	public int getCount() {
		return nodes.length;
	}

	@Override
	public ComputerPartGUI getGUI() {
		return m_gui;
	}

	/**
	 * Returns the Info for the pin at the given index.
	 */
	public PinInfo getPinInfo(int index) {
		return pins[index];
	}

	@Override
	public short getValueAt(int index) {
		return nodes[index].get();
	}

	/**
	 * Returns true if the width of the given value is less or equal to the
	 * width of the pin at the given index.
	 */
	public boolean isLegalWidth(int pinIndex, short value) {
		int maxWidth = pins[pinIndex].width;
		int width = value > 0 ? (int) (Math.log(value) / Math.log(2)) + 1 : 1;
		return (width <= maxWidth);
	}

	@Override
	public void refreshGUI() {
		if (displayChanges) {
			for (int i = 0; i < pins.length; i++) {
				pins[i].value = nodes[i].get();
			}
			m_gui.setContents(pins);
		}
	}

	@Override
	public void reset() {
		m_gui.reset();
		for (Node node : nodes) {
			node.set((short) 0);
		}
		refreshGUI();
	}

	/**
	 * Sets the nodes with the given nodes array according to the given
	 * GateClass.
	 */
	public void setNodes(Node[] nodes, GateClass gateClass) {
		this.nodes = nodes;
		pins = new PinInfo[nodes.length];
		for (int i = 0; i < pins.length; i++) {
			pins[i] = gateClass.getPinInfo(type, i);
			pins[i].value = nodes[i].get();

			nodes[i].addListener(new NodePinsAdapter(this, i));
		}

		if (hasGUI) {
			m_gui.setContents(pins);
		}
	}

	/**
	 * Called when a value of a pin was changed.
	 */
	@Override
	public void valueChanged(ComputerPartEvent event) {
		clearErrorListeners();
		int index = event.getIndex();
		short value = event.getValue();
		if (isLegalWidth(index, value)) {
			setValueAt(index, value, true);
		} else {
			notifyErrorListeners("Value doesn't match the pin's width");
			quietUpdateGUI(index, nodes[event.getIndex()].get());
		}
	}
}
