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

import Hack.CPUEmulator.KeyboardGUI;
import Hack.CPUEmulator.ScreenGUI;
import Hack.ComputerParts.BusGUI;
import Hack.ComputerParts.LabeledPointedMemoryGUI;
import Hack.ComputerParts.MemorySegmentGUI;
import Hack.ComputerParts.PointedMemorySegmentGUI;
import Hack.Controller.HackSimulatorGUI;

/**
 * An interface for a GUI of the VM emulator.
 */
public interface VMEmulatorGUI extends HackSimulatorGUI {

	/**
	 * Returns the arg memory segment component.
	 */
	public MemorySegmentGUI getArgSegment();

	/**
	 * Returns the bus GUI component.
	 */
	public BusGUI getBus();

	/**
	 * Returns the calculator GUI component.
	 */
	public CalculatorGUI getCalculator();

	/**
	 * Returns the call stack GUI component.
	 */
	public CallStackGUI getCallStack();

	/**
	 * Returns the keyboard GUI component.
	 */
	public KeyboardGUI getKeyboard();

	/**
	 * Returns the local memory segment component.
	 */
	public MemorySegmentGUI getLocalSegment();

	/**
	 * Returns the Program GUI component.
	 */
	public VMProgramGUI getProgram();

	/**
	 * Returns the RAM GUI component.
	 */
	public LabeledPointedMemoryGUI getRAM();

	/**
	 * Returns the screen GUI component.
	 */
	public ScreenGUI getScreen();

	/**
	 * Returns the Stack GUI component.
	 */
	public PointedMemorySegmentGUI getStack();

	/**
	 * Returns the static memory segment component.
	 */
	public MemorySegmentGUI getStaticSegment();

	/**
	 * Returns the temp memory segment component.
	 */
	public MemorySegmentGUI getTempSegment();

	/**
	 * Returns the that memory segment component.
	 */
	public MemorySegmentGUI getThatSegment();

	/**
	 * Returns the this memory segment component.
	 */
	public MemorySegmentGUI getThisSegment();

	/**
	 * Returns the method stack GUI component.
	 */
	public PointedMemorySegmentGUI getWorkingStack();

	/**
	 * Sets the focus on the VMEmulator's frame.
	 */
	public void requestFocus();
}
