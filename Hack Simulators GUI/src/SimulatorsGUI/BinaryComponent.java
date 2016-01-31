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
import java.awt.Event;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import Hack.Utilities.Conversions;
import HackGUI.Utilities;

/**
 * This class represents a 16-bits binary number.
 */
public class BinaryComponent extends JPanel implements MouseListener, KeyListener {

	private static final long serialVersionUID = -3834166816477856835L;
	
	// Creating the text fields.
	private JTextField m_bit0 = new JTextField(1);
	private JTextField m_bit1 = new JTextField(1);
	private JTextField m_bit2 = new JTextField(1);
	private JTextField m_bit3 = new JTextField(1);
	private JTextField m_bit4 = new JTextField(1);
	private JTextField m_bit5 = new JTextField(1);
	private JTextField m_bit6 = new JTextField(1);
	private JTextField m_bit7 = new JTextField(1);
	private JTextField m_bit8 = new JTextField(1);
	private JTextField m_bit9 = new JTextField(1);
	private JTextField m_bit10 = new JTextField(1);
	private JTextField m_bit11 = new JTextField(1);
	private JTextField m_bit12 = new JTextField(1);
	private JTextField m_bit13 = new JTextField(1);
	private JTextField m_bit14 = new JTextField(1);
	private JTextField m_bit15 = new JTextField(1);

	// An array containing all of the text fields.
	private JTextField[] m_bits = new JTextField[16];

	// The value of this component in a String representation.
	private StringBuffer m_valueStr;

	// Creating buttons.
	private JButton m_okButton = new JButton();
	private JButton m_cancelButton = new JButton();

	// Creating icons.
	private ImageIcon m_okIcon = new ImageIcon(Utilities.imagesDir + "smallok.gif");
	private ImageIcon m_cancelIcon = new ImageIcon(Utilities.imagesDir + "smallcancel.gif");

	// A vector conatining the listeners to this component.
	private Vector<PinValueListener> m_listeners;

	// A boolean value which is true if the user pressed the ok button and
	// false otherwise.
	private boolean m_isOk = false;

	// The border of this component.
	private Border m_binaryBorder;

	// The number of available bits.
	private int m_numberOfBits;

	/**
	 * Constructs a new BinaryComponent.
	 */
	public BinaryComponent() {
		m_listeners = new Vector<PinValueListener>();

		jbInit();
	}

	/**
	 * Registers the given PinValueListener as a listener to this component.
	 */
	public void addListener(PinValueListener listener) {
		m_listeners.addElement(listener);
	}

	/**
	 * Approve the change (called when OK is pressed or ENTER is pressed
	 */
	private void approve() {
		m_isOk = true;
		updateValue();
		notifyListeners();
		setVisible(false);
	}

	/**
	 * Implementing the action of pressing the cancel button.
	 */
	public void cancelButton_actionPerformed() {
		hideBinary();
	}

	/**
	 * Returns the value of this component.
	 */
	public short getValue() {
		return (short) Conversions.binaryToInt(m_valueStr.toString());
	}

	/**
	 * Hides the binary component as though the cancel button was pressed
	 */
	public void hideBinary() {
		m_isOk = false;
		notifyListeners();
		setVisible(false);
	}

	// Initialization of this component.
	private void jbInit() {
		m_binaryBorder = BorderFactory.createLineBorder(Color.black, 3);
		this.setLayout(null);
		m_bit0.setFont(Utilities.valueFont);
		m_bit0.setText("0");
		m_bit0.setHorizontalAlignment(SwingConstants.RIGHT);
		m_bit0.setBounds(new Rectangle(211, 8, 13, 19));
		m_bit0.addMouseListener(this);
		m_bit0.addKeyListener(this);
		m_bit1.setFont(Utilities.valueFont);
		m_bit1.setText("0");
		m_bit1.setHorizontalAlignment(SwingConstants.RIGHT);
		m_bit1.setBounds(new Rectangle(198, 8, 13, 19));
		m_bit1.addMouseListener(this);
		m_bit1.addKeyListener(this);
		m_bit2.setFont(Utilities.valueFont);
		m_bit2.setText("0");
		m_bit2.setHorizontalAlignment(SwingConstants.RIGHT);
		m_bit2.setBounds(new Rectangle(185, 8, 13, 19));
		m_bit2.addMouseListener(this);
		m_bit2.addKeyListener(this);
		m_bit3.setFont(Utilities.valueFont);
		m_bit3.setText("0");
		m_bit3.setHorizontalAlignment(SwingConstants.RIGHT);
		m_bit3.setBounds(new Rectangle(172, 8, 13, 19));
		m_bit3.addMouseListener(this);
		m_bit3.addKeyListener(this);
		m_bit4.setFont(Utilities.valueFont);
		m_bit4.setText("0");
		m_bit4.setHorizontalAlignment(SwingConstants.RIGHT);
		m_bit4.setBounds(new Rectangle(159, 8, 13, 19));
		m_bit4.addMouseListener(this);
		m_bit4.addKeyListener(this);
		m_bit5.setFont(Utilities.valueFont);
		m_bit5.setText("0");
		m_bit5.setHorizontalAlignment(SwingConstants.RIGHT);
		m_bit5.setBounds(new Rectangle(146, 8, 13, 19));
		m_bit5.addMouseListener(this);
		m_bit5.addKeyListener(this);
		m_bit6.setFont(Utilities.valueFont);
		m_bit6.setText("0");
		m_bit6.setHorizontalAlignment(SwingConstants.RIGHT);
		m_bit6.setBounds(new Rectangle(133, 8, 13, 19));
		m_bit6.addMouseListener(this);
		m_bit6.addKeyListener(this);
		m_bit7.setFont(Utilities.valueFont);
		m_bit7.setText("0");
		m_bit7.setHorizontalAlignment(SwingConstants.RIGHT);
		m_bit7.setBounds(new Rectangle(120, 8, 13, 19));
		m_bit7.addMouseListener(this);
		m_bit7.addKeyListener(this);
		m_bit8.setFont(Utilities.valueFont);
		m_bit8.setText("0");
		m_bit8.setHorizontalAlignment(SwingConstants.RIGHT);
		m_bit8.setBounds(new Rectangle(107, 8, 13, 19));
		m_bit8.addMouseListener(this);
		m_bit8.addKeyListener(this);
		m_bit9.setFont(Utilities.valueFont);
		m_bit9.setText("0");
		m_bit9.setHorizontalAlignment(SwingConstants.RIGHT);
		m_bit9.setBounds(new Rectangle(94, 8, 13, 19));
		m_bit9.addMouseListener(this);
		m_bit9.addKeyListener(this);
		m_bit10.setFont(Utilities.valueFont);
		m_bit10.setText("0");
		m_bit10.setHorizontalAlignment(SwingConstants.RIGHT);
		m_bit10.setBounds(new Rectangle(81, 8, 13, 19));
		m_bit10.addMouseListener(this);
		m_bit10.addKeyListener(this);
		m_bit11.setFont(Utilities.valueFont);
		m_bit11.setText("0");
		m_bit11.setHorizontalAlignment(SwingConstants.RIGHT);
		m_bit11.setBounds(new Rectangle(68, 8, 13, 19));
		m_bit11.addMouseListener(this);
		m_bit11.addKeyListener(this);
		m_bit12.setFont(Utilities.valueFont);
		m_bit12.setText("0");
		m_bit12.setHorizontalAlignment(SwingConstants.RIGHT);
		m_bit12.setBounds(new Rectangle(55, 8, 13, 19));
		m_bit12.addMouseListener(this);
		m_bit12.addKeyListener(this);
		m_bit13.setFont(Utilities.valueFont);
		m_bit13.setText("0");
		m_bit13.setHorizontalAlignment(SwingConstants.RIGHT);
		m_bit13.setBounds(new Rectangle(42, 8, 13, 19));
		m_bit13.addMouseListener(this);
		m_bit13.addKeyListener(this);
		m_bit14.setFont(Utilities.valueFont);
		m_bit14.setText("0");
		m_bit14.setHorizontalAlignment(SwingConstants.RIGHT);
		m_bit14.setBounds(new Rectangle(29, 8, 13, 19));
		m_bit14.addMouseListener(this);
		m_bit14.addKeyListener(this);
		m_bit15.setFont(Utilities.valueFont);
		m_bit15.setText("0");
		m_bit15.setHorizontalAlignment(SwingConstants.RIGHT);
		m_bit15.setBounds(new Rectangle(16, 8, 13, 19));
		m_bit15.addMouseListener(this);
		m_bit15.addKeyListener(this);
		m_okButton.setHorizontalTextPosition(SwingConstants.CENTER);
		m_okButton.setIcon(m_okIcon);
		m_okButton.setBounds(new Rectangle(97, 29, 23, 20));
		m_okButton.addActionListener(e -> okButton_actionPerformed());
		m_cancelButton.setHorizontalTextPosition(SwingConstants.CENTER);
		m_cancelButton.setIcon(m_cancelIcon);
		m_cancelButton.setBounds(new Rectangle(125, 29, 23, 20));
		m_cancelButton.addActionListener(e -> cancelButton_actionPerformed());
		this.setBorder(m_binaryBorder);
		this.add(m_bit15, null);
		this.add(m_bit14, null);
		this.add(m_bit13, null);
		this.add(m_bit12, null);
		this.add(m_bit11, null);
		this.add(m_bit10, null);
		this.add(m_bit9, null);
		this.add(m_bit8, null);
		this.add(m_bit7, null);
		this.add(m_bit6, null);
		this.add(m_bit5, null);
		this.add(m_bit4, null);
		this.add(m_bit3, null);
		this.add(m_bit2, null);
		this.add(m_bit1, null);
		this.add(m_bit0, null);
		this.add(m_cancelButton, null);
		this.add(m_okButton, null);

		m_bits[0] = m_bit15;
		m_bits[1] = m_bit14;
		m_bits[2] = m_bit13;
		m_bits[3] = m_bit12;
		m_bits[4] = m_bit11;
		m_bits[5] = m_bit10;
		m_bits[6] = m_bit9;
		m_bits[7] = m_bit8;
		m_bits[8] = m_bit7;
		m_bits[9] = m_bit6;
		m_bits[10] = m_bit5;
		m_bits[11] = m_bit4;
		m_bits[12] = m_bit3;
		m_bits[13] = m_bit2;
		m_bits[14] = m_bit1;
		m_bits[15] = m_bit0;

	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	/**
	 * Implementing the action of inserting a letter into the text field, or
	 * pressing enter / escape.
	 */
	@Override
	public void keyTyped(KeyEvent e) {
		JTextField t = (JTextField) e.getSource();
		if ((e.getKeyChar() == '0') || (e.getKeyChar() == '1')) {
			t.transferFocus();
			t.selectAll();
		} else if (e.getKeyChar() == Event.ENTER) {
			approve();
		} else if (e.getKeyChar() == Event.ESCAPE) {
			hideBinary();
		} else {
			t.selectAll();
			t.getToolkit().beep();
		}
	}

	/**
	 * Implementing the action of double-clicking the mouse on the text field.
	 * "0" --> "1" "1" --> "0"
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			JTextField t = (JTextField) e.getSource();
			if (t.getText().equals("0")) {
				t.setText("1");
			} else if (t.getText().equals("1")) {
				t.setText("0");
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	// Empty implementations
	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	/**
	 * Notify all the PinValueListeners on actions taken in it, by creating a
	 * PinValueEvent and sending it using the pinValueChanged method to all of
	 * the listeners.
	 */
	public void notifyListeners() {
		PinValueEvent event = new PinValueEvent(this, m_valueStr.toString(), m_isOk);
		for (int i = 0; i < m_listeners.size(); i++) {
			m_listeners.elementAt(i).pinValueChanged(event);
		}
	}

	/**
	 * Implementing the action of pressing the ok button.
	 */
	public void okButton_actionPerformed() {
		approve();
	}

	/**
	 * Un-registers the given PinValueListener from being a listener to this
	 * component.
	 */
	public void removeListener(PinValueListener listener) {
		m_listeners.removeElement(listener);
	}

	/**
	 * Sets the number of bits of this component.
	 */
	public void setNumOfBits(int num) {
		m_numberOfBits = num;
		for (int i = 0; i < m_bits.length; i++) {
			if (i < (m_bits.length - num)) {
				m_bits[i].setText("");
				m_bits[i].setBackground(Color.darkGray);
				m_bits[i].setEnabled(false);
			} else {
				m_bits[i].setBackground(Color.white);
				m_bits[i].setEnabled(true);
			}
		}
	}

	/**
	 * Sets the value of this component.
	 */
	public void setValue(short value) {
		m_valueStr = new StringBuffer(Conversions.decimalToBinary(value, 16));
		for (int i = 0; i < m_bits.length; i++) {
			m_bits[i].setText(String.valueOf(m_valueStr.charAt(i)));
		}
	}

	/**
	 * Shows the Binary component and gives focus to the first available bit.
	 */
	public void showBinary() {
		setVisible(true);
		m_bits[16 - m_numberOfBits].grabFocus();
	}

	// Updates the value of this component.
	private void updateValue() {
		m_valueStr = new StringBuffer(16);
		char currentChar;
		for (JTextField bit : m_bits) {
			if (bit.getText().equals("")) {
				currentChar = '0';
			} else {
				currentChar = bit.getText().charAt(0);
			}
			m_valueStr.append(currentChar);
		}
	}
}
