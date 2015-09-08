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

import Hack.ComputerParts.TextFileGUI;
import Hack.Controller.HackSimulatorGUI;
import Hack.Gates.GatesPanelGUI;

/**
 * An interface for the GUI of the Hardware Simulator.
 */
public interface HardwareSimulatorGUI extends HackSimulatorGUI {

	/**
	 * Returns the GateInfo component.
	 */
	public GateInfoGUI getGateInfo();

	/**
	 * Returns the Gates panel.
	 */
	public GatesPanelGUI getGatesPanel();

	/**
	 * Returns the HDLView.
	 */
	public TextFileGUI getHDLView();

	/**
	 * Returns the input pins table.
	 */
	public PinsGUI getInputPins();

	/**
	 * Returns the internal pins table.
	 */
	public PinsGUI getInternalPins();

	/**
	 * Returns the output pins table.
	 */
	public PinsGUI getOutputPins();

	/**
	 * Returns the part pins table.
	 */
	public PartPinsGUI getPartPins();

	/**
	 * Returns the parts table.
	 */
	public PartsGUI getParts();

	/**
	 * Hides the Internal pins table.
	 */
	public void hideInternalPins();

	/**
	 * Hides the Part pins table.
	 */
	public void hidePartPins();

	/**
	 * Hides the Parts table.
	 */
	public void hideParts();

	/**
	 * Displays the Internal pins table.
	 */
	public void showInternalPins();

	/**
	 * Displays the Part pins table.
	 */
	public void showPartPins();

	/**
	 * Displays the Parts table.
	 */
	public void showParts();

}
