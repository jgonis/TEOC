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

package SimulatorsGUI;

import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.JPanel;

import Hack.Controller.HackSimulatorGUI;

/**
 * The GUI component of a Hack Simulator.
 */
public abstract class HackSimulatorComponent extends JPanel implements HackSimulatorGUI {

	// The current additional display
	protected JComponent currentAdditionalDisplay = null;

	// The names of the help files
	protected String usageFileName, aboutFileName;

	@Override
	public String getAboutFileName() {
		return aboutFileName;
	}

	/**
	 * Returns the location on the simulator panel of the additional display.
	 */
	protected abstract Point getAdditionalDisplayLocation();

	@Override
	public String getUsageFileName() {
		return usageFileName;
	}

	@Override
	public void setAboutFileName(String fileName) {
		aboutFileName = fileName;
	}

	@Override
	public void setAdditionalDisplay(JComponent additionalComponent) {
		if (currentAdditionalDisplay != null) {
			remove(currentAdditionalDisplay);
		}
		currentAdditionalDisplay = additionalComponent;

		if (additionalComponent != null) {
			additionalComponent.setLocation(getAdditionalDisplayLocation());
			add(additionalComponent, 1);
			additionalComponent.revalidate();
		}

		revalidate();
		repaint();
	}

	@Override
	public void setUsageFileName(String fileName) {
		usageFileName = fileName;
	}

}
