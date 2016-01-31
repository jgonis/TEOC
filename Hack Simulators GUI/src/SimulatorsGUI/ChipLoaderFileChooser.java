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

import java.awt.Rectangle;
import java.io.File;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;

import HackGUI.FileChooserComponent;
import HackGUI.FilesTypeEvent;
import HackGUI.FilesTypeListener;
import HackGUI.Utilities;

/**
 * This class represents the gui of the chip loader file chooser.
 */
public class ChipLoaderFileChooser extends JFrame {

	private static final long serialVersionUID = 6754237597607653977L;
	// creating the file chooser components.
	private FileChooserComponent workingDir = new FileChooserComponent();
	private FileChooserComponent builtInDir = new FileChooserComponent();

	// creating the ok and cancel icons.
	private ImageIcon okIcon = new ImageIcon(Utilities.imagesDir + "ok.gif");
	private ImageIcon cancelIcon = new ImageIcon(Utilities.imagesDir + "cancel.gif");

	// creating the ok and cancel buttons.
	private JButton okButton = new JButton();
	private JButton cancelButton = new JButton();

	// the vector containing the listeners to this component.
	private Vector<FilesTypeListener> listeners;

	/**
	 * Constructs a new ChipLoaderFileChooser.
	 */
	public ChipLoaderFileChooser() {
		super("Directories Selection");
		listeners = new Vector<FilesTypeListener>();
		setSelectionToDirectory();
		setNames();
		jbInit();
	}

	/**
	 * Registers the given FilesTypeListener as a listener to this component.
	 */
	public void addListener(FilesTypeListener listener) {
		listeners.addElement(listener);
	}

	/**
	 * Implementing the action of pressing the cancel button.
	 */
	public void cancelButton_actionPerformed() {
		workingDir.showCurrentFileName();
		builtInDir.showCurrentFileName();
		setVisible(false);
	}

	// Initializes this component.
	private void jbInit() {
		this.getContentPane().setLayout(null);
		workingDir.setBounds(new Rectangle(5, 2, 485, 48));
		builtInDir.setBounds(new Rectangle(5, 38, 485, 48));
		okButton.setToolTipText("OK");
		okButton.setIcon(okIcon);
		okButton.setBounds(new Rectangle(90, 95, 63, 44));
		okButton.addActionListener(e -> okButton_actionPerformed());
		cancelButton.setBounds(new Rectangle(265, 95, 63, 44));
		cancelButton.addActionListener(e -> cancelButton_actionPerformed());
		cancelButton.setToolTipText("CANCEL");
		cancelButton.setIcon(cancelIcon);
		this.getContentPane().add(workingDir, null);
		this.getContentPane().add(builtInDir, null);
		this.getContentPane().add(okButton, null);
		this.getContentPane().add(cancelButton, null);

		setSize(470, 210);
		setLocation(250, 250);
	}

	/**
	 * Notify all the FilesTypeListeners on actions taken in it, by creating a
	 * FilesTypeEvent and sending it using the filesNamesChanged method to all
	 * of the listeners.
	 */
	public void notifyListeners(String working,
			String builtIn/* , String composite */) {
		FilesTypeEvent event = new FilesTypeEvent(this, working, builtIn, null/* , composite */);

		for (int i = 0; i < listeners.size(); i++) {
			listeners.elementAt(i).filesNamesChanged(event);
		}
	}

	/**
	 * Implementing the action of pressing the ok button.
	 */
	public void okButton_actionPerformed() {

		String working = null;
		String builtIn = null;

		if (workingDir.isFileNameChanged()) {
			working = workingDir.getFileName();
			workingDir.setCurrentFileName(working);
			workingDir.showCurrentFileName();
		}

		if (builtInDir.isFileNameChanged()) {
			builtIn = builtInDir.getFileName();
			builtInDir.setCurrentFileName(builtIn);
			builtInDir.showCurrentFileName();
		}

		if (!((working == null) && (builtIn == null))) {
			notifyListeners(working, builtIn);
		}
		setVisible(false);
	}

	/**
	 * Un-registers the given FilesTypeListener from being a listener to this
	 * component.
	 */
	public void removeListener(FilesTypeListener listener) {
		listeners.removeElement(listener);
	}

	/**
	 * Sets the BuiltIn HDL directory.
	 */
	public void setBuiltInDir(File file) {
		builtInDir.setCurrentFileName(file.getName());
		builtInDir.showCurrentFileName();
	}

	// sets the names of the file choosers.
	private void setNames() {
		workingDir.setName("Working Dir :");
		builtInDir.setName("BuiltIn Dir :");
	}

	// Sets the selection mode to directories only.
	private void setSelectionToDirectory() {
		workingDir.setSelectionToDirectories();
		builtInDir.setSelectionToDirectories();
	}

	/**
	 * Sets the current HDL directory.
	 */
	public void setWorkingDir(File file) {
		workingDir.setCurrentFileName(file.getName());
		workingDir.showCurrentFileName();
	}

	/**
	 * Shows the file chooser.
	 */
	public void showWindow() {
		setVisible(true);
		workingDir.getTextField().requestFocus();
	}
}
