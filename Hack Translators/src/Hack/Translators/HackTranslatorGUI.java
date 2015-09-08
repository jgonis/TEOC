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

package Hack.Translators;

import java.io.File;

import Hack.ComputerParts.TextFileGUI;

/**
 * The GUI of the HackTranslator.
 */
public interface HackTranslatorGUI {

	/**
	 * Registers the given HackTranslatorEventListener as a listener to this
	 * GUI.
	 */
	public void addHackTranslatorListener(HackTranslatorEventListener listener);

	/**
	 * Disables the fast forward action.
	 */
	public void disableFastForward();

	/**
	 * Disables the full compilation action.
	 */
	public void disableFullCompilation();

	/**
	 * Disables loading a new source file.
	 */
	public void disableLoadSource();

	/**
	 * Disables the rewind action.
	 */
	public void disableRewind();

	/**
	 * Disables the save action.
	 */
	public void disableSave();

	/**
	 * Disables the single step action.
	 */
	public void disableSingleStep();

	/**
	 * Disables selecting a row in the source.
	 */
	public void disableSourceRowSelection();

	/**
	 * Disables the stop action.
	 */
	public void disableStop();

	/**
	 * Displays the given message, according to the given type.
	 */
	public void displayMessage(String message, boolean error);

	/**
	 * Enables the fast forward action.
	 */
	public void enableFastForward();

	/**
	 * Enables the full compilation action.
	 */
	public void enableFullCompilation();

	/**
	 * Enables loading a new source file.
	 */
	public void enableLoadSource();

	/**
	 * Enables the rewind action.
	 */
	public void enableRewind();

	/**
	 * Enables the save action.
	 */
	public void enableSave();

	/**
	 * Enables the single step action.
	 */
	public void enableSingleStep();

	/**
	 * Enables selecting a row in the source.
	 */
	public void enableSourceRowSelection();

	/**
	 * Enables the stop action.
	 */
	public void enableStop();

	/**
	 * Returns the GUI of the Destination file.
	 */
	public TextFileGUI getDestination();

	/**
	 * Returns the GUI of the Source file.
	 */
	public TextFileGUI getSource();

	/**
	 * Notify all the HackTranslatorEventListeners on actions taken in it, by
	 * creating a HackTranslatorEvent (with the action and supplied data) and
	 * sending it using the actionPerformed method to all the listeners.
	 */
	public void notifyHackTranslatorListeners(byte action, Object data);

	/**
	 * Un-registers the given HackTranslatorEventListener from being a listener
	 * to this GUI.
	 */
	public void removeHackTranslatorListener(HackTranslatorEventListener listener);

	/**
	 * Sets the name of the html file that contains the "about" information.
	 */
	public void setAboutFileName(String fileName);

	/**
	 * Sets the name of the Destination file with the given name.
	 */
	public void setDestinationName(String name);

	/**
	 * Sets the name of the Source file with the given name.
	 */
	public void setSourceName(String name);

	/**
	 * Sets the title of the translator with the given title.
	 */
	public void setTitle(String title);

	/**
	 * Sets the name of the html file that contains the help usage.
	 */
	public void setUsageFileName(String fileName);

	/**
	 * Sets the working dir name with the given one.
	 */
	public void setWorkingDir(File file);
}
