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

import Hack.Controller.*;

import java.io.File;

/**
 * A HardwareSimulator application.
 */
public class HardwareSimulatorApplication extends HackApplication {

    /**
     * Constructs a new HardwareSimulatorApplication with the given controller GUI
     * component, the simulator GUI component, the default script name and the names of the
     * help files.
     */
    public HardwareSimulatorApplication(HardwareSimulatorControllerGUI controllerComponent,
                                        HardwareSimulatorGUI simulatorComponent,
                                        File defaultScript,
                                        File contentsFileName,
                                        File aboutFileName) {
        super(new HardwareSimulator(simulatorComponent),
              controllerComponent,
              simulatorComponent,
              defaultScript,
              contentsFileName,
              aboutFileName);
    }

    protected void createController(HackSimulator simulator,
                                    ControllerGUI controllerComponent,
                                    File defaultScript)
     throws ScriptException, ControllerException {
        HardwareSimulatorController c = new HardwareSimulatorController((HardwareSimulatorControllerGUI)controllerComponent,
                                                                        (HardwareSimulator)simulator,
                                                                        defaultScript);
    }
}
