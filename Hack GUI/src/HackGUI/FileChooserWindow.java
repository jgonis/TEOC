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

package HackGUI;

import java.awt.Rectangle;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

/**
 * This class repersents the GUI of the component which allows the user to load
 * a certain file.
 */
public class FileChooserWindow extends JFrame implements EnterPressedListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6918852068703748475L;

	// Creating the file chooser component.
	private ViewableFileChooserComponent fileChooser;

	// Creating the ok and cancel buttons.
	private JButton okButton = new JButton();
	private JButton cancelButton = new JButton();

	// Creating the icons.
	private ImageIcon okIcon;
	private ImageIcon cancelIcon;

	// the listeners to this component.
	private Vector<FilesTypeListener> listeners;

	/**
	 * Constructs a new FilesChooserWindow.
	 */
	public FileChooserWindow(FileFilter filter) {
		listeners = new Vector<FilesTypeListener>();
		fileChooser = new ViewableFileChooserComponent();
		fileChooser.setFilter(filter);
	
		try {
			okIcon = new ImageIcon(Paths.get(ClassLoader.getSystemResource("ok.gif").toURI()).toString());
			cancelIcon = new ImageIcon(Paths.get(ClassLoader.getSystemResource("cancel.gif").toURI()).toString());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
		fileChooser.showCurrentFileName();
		setVisible(false);
	}

	/**
	 * Called when the user pressed the enter button.
	 */
	@Override
	public void enterPressed() {
		String file = null;
		file = fileChooser.getFileName();
		fileChooser.setCurrentFileName(file);
		if (!(file == null)) {
			notifyListeners(file);
		}
		setVisible(false);
	}

	/**
	 * Returns the textfield.
	 */
	public JTextField getTextField() {
		return fileChooser.getTextField();
	}

	// Initialization.
	private void jbInit() {
		fileChooser.addListener(this);
		fileChooser.setWindowLocation(647, 3);
		this.getContentPane().setLayout(null);
		setTitle("Files selection");
		fileChooser.setBounds(new Rectangle(5, 2, 482, 48));
		okButton.setToolTipText("OK");
		okButton.setIcon(okIcon);
		okButton.setBounds(new Rectangle(124, 64, 63, 44));
		okButton.addActionListener(e -> okButton_actionPerformed());
		cancelButton.setBounds(new Rectangle(282, 63, 63, 44));
		cancelButton.addActionListener(e -> cancelButton_actionPerformed());
		cancelButton.setToolTipText("CANCEL");
		cancelButton.setIcon(cancelIcon);
		this.getContentPane().add(fileChooser, null);
		this.getContentPane().add(cancelButton, null);
		this.getContentPane().add(okButton, null);
		setSize(496, 150);
		setLocation(145, 250);
	}

	/**
	 * Notify all the FilesTypeListeners on actions taken in it, by creating a
	 * FilesTypeEvent and sending it using the filesNamesChanged method to all
	 * of the listeners.
	 */
	public void notifyListeners(String fileName) {
		FilesTypeEvent event = new FilesTypeEvent(this, fileName, null, null);

		for (int i = 0; i < listeners.size(); i++) {
			listeners.elementAt(i).filesNamesChanged(event);
		}
	}

	/**
	 * Implementing the action of pressing the ok button.
	 */
	public void okButton_actionPerformed() {

		String file = null;
		file = fileChooser.getFileName();
		fileChooser.setCurrentFileName(file);
		setVisible(false);
		if (!(file == null)) {
			notifyListeners(file);
		}
	}

	/**
	 * Un-registers the given FilesTypeListener from being a listener to this
	 * component.
	 */
	public void removeListener(FilesTypeListener listener) {
		listeners.removeElement(listener);
	}

	/**
	 * Sets the file name (which is written inside the text field).
	 */
	public void setFileName(String name) {
		fileChooser.setCurrentFileName(name);
		fileChooser.showCurrentFileName();
	}

	/**
	 * Sets the name of the file chooser.
	 */
	@Override
	public void setName(String name) {
		fileChooser.setName(name);
	}

	/**
	 * Shows the file chooser window.
	 */
	public void showWindow() {
		setVisible(true);
		fileChooser.getTextField().requestFocus();
	}
}
