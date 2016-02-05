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

/**
 * This class repersents the GUI of the component which allows the user to load
 * three kinds of files into the system: script file, output file and comparison
 * file.
 */
public class ControllerFileChooser extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6770520505629399787L;
	// Creating the file chooser components
	private FileChooserComponent outputFileChooser = new FileChooserComponent();
	private FileChooserComponent comparisonFileChooser = new FileChooserComponent();
	private FileChooserComponent scriptFileChooser = new FileChooserComponent();

	// Creating the ok and cancel buttons.
	private JButton okButton = new JButton();
	private JButton cancelButton = new JButton();

	// Creating icons.
	private ImageIcon okIcon;
	private ImageIcon cancelIcon;

	// the listeners to this component.
	private Vector<FilesTypeListener> listeners;

	/**
	 * Constructs a new FilesChooserWindow.
	 */
	public ControllerFileChooser() {
		listeners = new Vector<FilesTypeListener>();
		
		try {
			okIcon = new ImageIcon(Paths.get(ClassLoader.getSystemResource("ok.gif").toURI()).toString());
			cancelIcon = new ImageIcon(Paths.get(ClassLoader.getSystemResource("cancel.gif").toURI()).toString());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jbInit();

		// Sets the names of the file chooser components.
		scriptFileChooser.setName("Script File :");
		outputFileChooser.setName("Output File :");
		comparisonFileChooser.setName("Comparison File :");
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
		scriptFileChooser.showCurrentFileName();
		outputFileChooser.showCurrentFileName();
		comparisonFileChooser.showCurrentFileName();
		setVisible(false);
	}

	// Initialization this component
	private void jbInit() {
		this.getContentPane().setLayout(null);
		setTitle("Files selection");
		scriptFileChooser.setBounds(new Rectangle(5, 2, 485, 48));
		outputFileChooser.setBounds(new Rectangle(5, 38, 485, 48));
		comparisonFileChooser.setBounds(new Rectangle(5, 74, 485, 48));
		okButton.setToolTipText("OK");
		okButton.setIcon(okIcon);
		okButton.setBounds(new Rectangle(123, 134, 63, 44));
		okButton.addActionListener(e -> okButton_actionPerformed());
		cancelButton.setBounds(new Rectangle(283, 134, 63, 44));
		cancelButton.addActionListener(e -> cancelButton_actionPerformed());
		cancelButton.setToolTipText("CANCEL");
		cancelButton.setIcon(cancelIcon);
		this.getContentPane().add(scriptFileChooser, null);
		this.getContentPane().add(outputFileChooser, null);
		this.getContentPane().add(comparisonFileChooser, null);
		this.getContentPane().add(okButton, null);
		this.getContentPane().add(cancelButton, null);
		setSize(500, 210);
		setLocation(20, 415);
	}

	/**
	 * Notify all the FilesTypeListeners on actions taken in it, by creating a
	 * FilesTypeEvent and sending it using the filesNamesChanged method to all
	 * of the listeners.
	 */
	public void notifyListeners(String script, String output, String comparison) {
		FilesTypeEvent event = new FilesTypeEvent(this, script, output, comparison);

		for (int i = 0; i < listeners.size(); i++) {
			listeners.elementAt(i).filesNamesChanged(event);
		}
	}

	/**
	 * Implementing the action of pressing the ok button.
	 */
	public void okButton_actionPerformed() {

		String script = null;
		String output = null;
		String comparison = null;

		if (scriptFileChooser.isFileNameChanged() || !scriptFileChooser.getFileName().equals("")) {
			script = scriptFileChooser.getFileName();
			scriptFileChooser.setCurrentFileName(script);
			scriptFileChooser.showCurrentFileName();
		}

		if (outputFileChooser.isFileNameChanged() || !outputFileChooser.getFileName().equals("")) {
			output = outputFileChooser.getFileName();
			outputFileChooser.setCurrentFileName(output);
			outputFileChooser.showCurrentFileName();
		}

		if (comparisonFileChooser.isFileNameChanged() || !comparisonFileChooser.getFileName().equals("")) {
			comparison = comparisonFileChooser.getFileName();
			comparisonFileChooser.setCurrentFileName(comparison);
			comparisonFileChooser.showCurrentFileName();
		}
		if (!((script == null) && (output == null) && (comparison == null))) {
			notifyListeners(script, output, comparison);
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
	 * Sets the comparison file.
	 */
	public void setComparisonFile(String fileName) {
		comparisonFileChooser.setCurrentFileName(fileName);
		comparisonFileChooser.showCurrentFileName();
	}

	/**
	 * Sets the output file.
	 */
	public void setOutputFile(String fileName) {
		outputFileChooser.setCurrentFileName(fileName);
		outputFileChooser.showCurrentFileName();
	}

	/**
	 * Sets the directory of the script files.
	 */
	public void setScriptDir(String dir) {
		scriptFileChooser.setScriptDir(dir);
	}

	/**
	 * Sets the script file.
	 */
	public void setScriptFile(String fileName) {
		scriptFileChooser.setCurrentFileName(fileName);
		scriptFileChooser.showCurrentFileName();
	}

	// Shows the controller's file chooser
	public void showWindow() {
		setVisible(true);
		scriptFileChooser.getTextField().requestFocus();
	}
}
