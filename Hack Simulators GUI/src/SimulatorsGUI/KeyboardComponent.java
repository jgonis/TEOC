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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import Hack.CPUEmulator.KeyboardGUI;
import HackGUI.Utilities;

/**
 * A keyboard GUI component. Receives key input by using key events.
 */
public class KeyboardComponent extends JPanel implements KeyboardGUI {

	// The icon of the keyboard.
	private ImageIcon keyboardIcon = new ImageIcon(Utilities.imagesDir + "keyboard.gif");

	// The text field on which the letter are appearing.
	private JTextField keyNameText = new JTextField();

	// The keyboard's button.
	private JButton keyButton = new JButton();

	/**
	 * Constructs a new keyboard component.
	 */
	public KeyboardComponent() {
		jbInit();
	}

	/**
	 * Clears the key display.
	 */
	@Override
	public void clearKey() {
		keyNameText.setText("");
	}

	/**
	 * Returns the keyboard's button.
	 */
	@Override
	public JComponent getKeyEventHandler() {
		return keyButton;
	}

	// Initializes this component.
	private void jbInit() {
		keyNameText.setBounds(new Rectangle(258, 0, 258, 27));
		keyNameText.setEnabled(false);
		keyNameText.setFont(new Font("Times New Roman", 1, 14));
		keyNameText.setDisabledTextColor(Color.black);
		keyNameText.setEditable(false);
		keyNameText.setHorizontalAlignment(SwingConstants.CENTER);
		keyNameText.setBackground(SystemColor.info);
		this.setLayout(null);
		keyButton.setIcon(keyboardIcon);
		keyButton.setBounds(new Rectangle(0, 0, 258, 27));
		keyButton.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				keyButton_focusGained(e);
			}

			@Override
			public void focusLost(FocusEvent e) {
				keyButton_focusLost(e);
			}
		});
		this.add(keyButton, null);
		this.add(keyNameText, null);

		setPreferredSize(new Dimension(516, 27));
		setSize(516, 27);
	}

	/**
	 * Implements the action of gaining the focus (changing the background of
	 * this component).
	 */
	public void keyButton_focusGained(FocusEvent e) {
		keyButton.setBackground(UIManager.getColor("TextField.selectionBackground"));
	}

	/**
	 * Implements the action of losing the focus (changing the background of
	 * this component back to the original color).
	 */
	public void keyButton_focusLost(FocusEvent e) {
		keyButton.setBackground(UIManager.getColor("Button.background"));
	}

	/**
	 * Resets the contents of this KeyboardComponent.
	 */
	@Override
	public void reset() {
	}

	/**
	 * Displayes the given key name.
	 */
	@Override
	public void setKey(String keyName) {
		keyNameText.setText(keyName);
	}
}
