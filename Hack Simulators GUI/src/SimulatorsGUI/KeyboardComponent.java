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
import java.net.URISyntaxException;
import java.nio.file.Paths;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import Hack.CPUEmulator.KeyboardGUI;

/**
 * A keyboard GUI component. Receives key input by using key events.
 */
public class KeyboardComponent extends JPanel implements KeyboardGUI {

	private static final long serialVersionUID = -3357924013065805531L;

	// The icon of the keyboard.
	private ImageIcon m_keyboardIcon;

	// The text field on which the letter are appearing.
	private JTextField m_keyNameText = new JTextField();

	// The keyboard's button.
	private JButton m_keyButton = new JButton();

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
		m_keyNameText.setText("");
	}

	/**
	 * Returns the keyboard's button.
	 */
	@Override
	public JComponent getKeyEventHandler() {
		return m_keyButton;
	}

	// Initializes this component.
	private void jbInit() {
		try {
			m_keyboardIcon = new ImageIcon(Paths.get(ClassLoader.getSystemResource("keyboard.gif").toURI()).toString());
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		m_keyNameText.setBounds(new Rectangle(258, 0, 258, 27));
		m_keyNameText.setEnabled(false);
		m_keyNameText.setFont(new Font("Times New Roman", 1, 14));
		m_keyNameText.setDisabledTextColor(Color.black);
		m_keyNameText.setEditable(false);
		m_keyNameText.setHorizontalAlignment(SwingConstants.CENTER);
		m_keyNameText.setBackground(SystemColor.info);
		this.setLayout(null);
		m_keyButton.setIcon(m_keyboardIcon);
		m_keyButton.setBounds(new Rectangle(0, 0, 258, 27));
		m_keyButton.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				keyButton_focusGained();
			}

			@Override
			public void focusLost(FocusEvent e) {
				keyButton_focusLost();
			}
		});
		this.add(m_keyButton, null);
		this.add(m_keyNameText, null);

		setPreferredSize(new Dimension(516, 27));
		setSize(516, 27);
	}

	/**
	 * Implements the action of gaining the focus (changing the background of
	 * this component).
	 */
	public void keyButton_focusGained() {
		m_keyButton.setBackground(UIManager.getColor("TextField.selectionBackground"));
	}

	/**
	 * Implements the action of losing the focus (changing the background of
	 * this component back to the original color).
	 */
	public void keyButton_focusLost() {
		m_keyButton.setBackground(UIManager.getColor("Button.background"));
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
		m_keyNameText.setText(keyName);
	}
}
