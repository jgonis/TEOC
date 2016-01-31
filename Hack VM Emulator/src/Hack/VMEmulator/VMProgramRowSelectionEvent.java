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

package Hack.VMEmulator;

import java.util.EventObject;

import Hack.VirtualMachine.HVMInstruction;

/**
 * An event for notifying an VMProgramRowSelectionEventListener on a change in
 * the selected row, which was done through the HDL view GUI.
 */
public class VMProgramRowSelectionEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8340777105123583020L;

	// the changed row index;
	private int rowIndex;

	// the changed row instruction
	private HVMInstruction rowInstruction;

	/**
	 * Constructs a new VMProgramRowSelectionEvent with the given source and the
	 * selected row instruction and index.
	 */
	public VMProgramRowSelectionEvent(Object source, HVMInstruction rowInstruction, int rowIndex) {
		super(source);
		this.rowInstruction = rowInstruction;
		this.rowIndex = rowIndex;
	}

	/**
	 * Returns the selected row index.
	 */
	public int getRowIndex() {
		return rowIndex;
	}

	/**
	 * Returns the selected row instruction.
	 */
	public HVMInstruction getRowInstruction() {
		return rowInstruction;
	}
}
