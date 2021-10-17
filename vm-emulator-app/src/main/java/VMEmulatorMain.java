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

import Hack.Controller.ControllerGUI;
import Hack.Controller.HackController;
import Hack.Utilities.ResourceTempFileGenerator;
import Hack.VMEmulator.VMEmulator;
import Hack.VMEmulator.VMEmulatorApplication;
import Hack.VMEmulator.VMEmulatorGUI;
import HackGUI.ControllerComponent;
import SimulatorsGUI.VMEmulatorComponent;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

/**
 * The VM Emulator.
 */
public class VMEmulatorMain {
    /**
     * The command line VM Emulator program.
     */
    public static void main(String[] args) {
        if (args.length > 1)
            System.err.println("Usage: java CPUEmulatorMain [script name]");
        else if (args.length == 0) {
            SwingUtilities.invokeLater(() -> {
                try {
                    File defaultVMFile = ResourceTempFileGenerator.createTempFileFromResource(VMEmulatorMain.class, "defaultVM", ".txt");
                    File defaultUsageFile = ResourceTempFileGenerator.createTempFileFromResource(VMEmulatorMain.class, "vmUsage", ".html");
                    File defaultAboutFile = ResourceTempFileGenerator.createTempFileFromResource(VMEmulatorMain.class, "vmAbout", ".html");

                    VMEmulatorGUI simulatorGUI = new VMEmulatorComponent();
                    ControllerGUI controllerGUI = new ControllerComponent();

                    VMEmulatorApplication application = new VMEmulatorApplication(controllerGUI,
                                                                                  simulatorGUI,
                                                                                  defaultVMFile,
                                                                                  defaultUsageFile,
                                                                                  defaultAboutFile);
                } catch(IOException ioe) {
                    System.exit(-1);
                }

            });
        } else
            new HackController(new VMEmulator(), args[0]);
    }
}

