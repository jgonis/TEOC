
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

import javax.swing.UIManager;

import Hack.Controller.ControllerGUI;
import Hack.Controller.HackController;
import Hack.VMEmulator.VMEmulator;
import Hack.VMEmulator.VMEmulatorApplication;
import Hack.VMEmulator.VMEmulatorGUI;
import HackGUI.ControllerComponent;
import SimulatorsGUI.VMEmulatorComponent;

/**
 * The VM Emulator.
 */
public class VMEmulatorMain {
	/**
	 * The command line VM Emulator program.
	 */
	public static void main(String[] args) {
		if (args.length > 1) {
			System.err.println("Usage: java CPUEmulatorMain [script name]");
		} else if (args.length == 0) {

			VMEmulatorGUI simulatorGUI = new VMEmulatorComponent();
			ControllerGUI controllerGUI = new ControllerComponent();
			new VMEmulatorApplication(controllerGUI, simulatorGUI, "bin/scripts/defaultVM.txt", "bin/help/vmUsage.html",
					"bin/help/vmAbout.html");
		} else {
			new HackController(new VMEmulator(), args[0]);
		}
	}
}
