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

import Hack.Controller.HackController;
import Hack.HardwareSimulator.HardwareSimulator;
import Hack.HardwareSimulator.HardwareSimulatorApplication;
import Hack.HardwareSimulator.HardwareSimulatorControllerGUI;
import Hack.HardwareSimulator.HardwareSimulatorGUI;
import Hack.Utilities.ResourceTempFileGenerator;
import SimulatorsGUI.HardwareSimulatorComponent;
import SimulatorsGUI.HardwareSimulatorControllerComponent;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;


/**
 * The Hardware Simulator.
 */
public class HardwareSimulatorMain {
    /**
     * The command line Hardware Simulator program.
     */
    public static void main(String[] args) {
        if (args.length > 1)
            System.err.println("Usage: java HardwareSimulatorMain [script name]");
        else if (args.length == 0) {
            SwingUtilities.invokeLater(() -> {
                HardwareSimulatorGUI simulatorGUI = new HardwareSimulatorComponent();
                HardwareSimulatorControllerGUI controllerGUI = new HardwareSimulatorControllerComponent();
                try {
                    File defaultHWFile = ResourceTempFileGenerator.createTempFileFromResource(HardwareSimulatorMain.class,"defaultHW", ".txt");
                    File defaultUsageFile = ResourceTempFileGenerator.createTempFileFromResource(HardwareSimulatorMain.class, "hwUsage", ".html");
                    File defaultAboutFile = ResourceTempFileGenerator.createTempFileFromResource(HardwareSimulatorMain.class, "hwAbout", ".html");
                    HardwareSimulatorApplication application =
                            new HardwareSimulatorApplication(controllerGUI,
                                                             simulatorGUI,
                                                             defaultHWFile,
                                                             defaultUsageFile,
                                                             defaultAboutFile);
                } catch (IOException ioe){
                    System.exit(-1);
                }
            });
        } else
            new HackController(new HardwareSimulator(), args[0]);
    }

    private static File createTempFileFromResource(Class<?> resourceLocator,
                                                   String resourceName,
                                                   String resourceSuffix) throws IOException {
        InputStream resourceStream = resourceLocator.getResourceAsStream("/" + resourceName + resourceSuffix);
        File resourceFile = File.createTempFile(resourceName, resourceSuffix);
        resourceFile.deleteOnExit();
        if (resourceStream != null) {
            Files.copy(resourceStream, resourceFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return resourceFile;
        } else {
            return null;
        }
    }
}
