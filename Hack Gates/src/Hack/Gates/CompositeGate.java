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

package Hack.Gates;

public class CompositeGate extends Gate {

	// the internal pins
	protected Node[] m_internalPins;

	// The contained parts (Gates), sorted in topological order.
	protected Gate[] m_parts;

	@Override
	protected void clockDown() {
		if (m_gateClass.isClocked) {
			for (Gate part : m_parts) {
				part.tock();
			}
		}
	}

	@Override
	protected void clockUp() {
		if (m_gateClass.isClocked) {
			for (Gate part : m_parts) {
				part.tick();
			}
		}
	}

	/**
	 * Returns the internal pins.
	 */
	public Node[] getInternalNodes() {
		return m_internalPins;
	}

	/**
	 * Returns the node according to the given node name (may be input, output
	 * or internal). If doesn't exist, returns null.
	 */
	@Override
	public Node getNode(String name) {
		Node result = super.getNode(name);

		if (result == null) {
			byte type = m_gateClass.getPinType(name);
			int index = m_gateClass.getPinNumber(name);
			if (type == CompositeGateClass.INTERNAL_PIN_TYPE) {
				result = m_internalPins[index];
			}
		}

		return result;
	}

	/**
	 * Returns the parts (internal gates) of this gate, sorted in topological
	 * order.
	 */
	public Gate[] getParts() {
		return m_parts;
	}

	/**
	 * Initializes the gate
	 */
	public void init(Node[] inputPins, Node[] outputPins, Node[] internalPins, Gate[] parts, GateClass gateClass) {
		m_inputPins = inputPins;
		m_outputPins = outputPins;
		m_internalPins = internalPins;
		m_parts = parts;
		m_gateClass = gateClass;
		setDirty();
	}

	@Override
	protected void reCompute() {
		for (Gate part : m_parts) {
			part.eval();
		}
	}

}
