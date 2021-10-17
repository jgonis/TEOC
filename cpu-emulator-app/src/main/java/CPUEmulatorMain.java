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

import Hack.CPUEmulator.CPUEmulator;
import Hack.CPUEmulator.CPUEmulatorApplication;
import Hack.CPUEmulator.CPUEmulatorGUI;
import Hack.Controller.ControllerGUI;
import Hack.Controller.HackController;
import Hack.Utilities.ResourceTempFileGenerator;
import HackGUI.ControllerComponent;
import SimulatorsGUI.CPUEmulatorComponent;

import javax.swing.SwingUtilities;
import java.io.File;
import java.io.IOException;

/**
 * The CPU Emulator.
 */
public class CPUEmulatorMain {
    /**
     * The command line CPU Emulator program.
     */
    public static void main(String[] args) {
        if (args.length > 1)
            System.err.println("Usage: java CPUEmulatorMain [script name]");
        else if (args.length == 0) {
            SwingUtilities.invokeLater(() -> {
                try {
                    File defaultCPUFile = ResourceTempFileGenerator.createTempFileFromResource(CPUEmulatorMain.class,
                                                                                               "defaultCPU",
                                                                                               ".txt");
                    File defaultUsageFile = ResourceTempFileGenerator.createTempFileFromResource(CPUEmulatorMain.class,
                                                                                                 "cpuUsage",
                                                                                                 ".html");
                    File defaultAboutFile = ResourceTempFileGenerator.createTempFileFromResource(CPUEmulatorMain.class,
                                                                                                 "cpuAbout",
                                                                                                 ".html");

                    CPUEmulatorGUI simulatorGUI = new CPUEmulatorComponent();
                    ControllerGUI controllerGUI = new ControllerComponent();

                    new CPUEmulatorApplication(controllerGUI,
                                               simulatorGUI,
                                               defaultCPUFile,
                                               defaultUsageFile,
                                               defaultAboutFile);
                } catch(IOException ioe) {
                    System.exit(-1);
                }
            });
        } else
            new HackController(new CPUEmulator(), args[0]);
    }
}
