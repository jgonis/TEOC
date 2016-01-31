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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import Hack.VMEmulator.CalculatorGUI;
import HackGUI.Format;
import HackGUI.Utilities;

/**
 * This class represents an operation performed on the Stack.
 */
public class StackCalculator extends JPanel implements CalculatorGUI {

	private static final long serialVersionUID = 8803665242452281293L;
	// wide and regular strokes for the painting.
	private final static BasicStroke WIDE_STROKE = new BasicStroke(3.0f);
	private final static BasicStroke REGULAR_STROKE = new BasicStroke(1.0f);
	// The input, command and output textfields.
	private JTextField m_firstInput = new JTextField();
	private JTextField m_command = new JTextField();

	private JTextField m_secondInput = new JTextField();
	private JTextField m_output = new JTextField();

	// The null value of this component.
	protected short m_nullValue;

	// A boolean field specifying if the null value should be activated or not.
	protected boolean m_hideNullValue;

	/**
	 * Constructs a new StackCalculator.
	 */
	public StackCalculator() {

		jbInit();
	}

	/**
	 * Disabling user inputs. this method isn't implemented because in this
	 * component the text fields are always disabled.
	 */
	public void disableUserInput() {
	}

	/**
	 * Enabling user inputs. this method isn't implemented because in this
	 * component the text fields are always disabled.
	 */
	public void enableUserInput() {
	}

	/**
	 * flashes the value at the given index.
	 */
	@Override
	public void flash(int index) {
		switch (index) {
		case 0:
			m_firstInput.setBackground(Color.orange);
			break;
		case 1:
			m_secondInput.setBackground(Color.orange);
			break;
		case 2:
			m_output.setBackground(Color.orange);
			break;
		}
	}

	/**
	 * Returns the coordinates of the top left corner of the value at the given
	 * index.
	 */
	@Override
	public Point getCoordinates(int index) {
		Point location = getLocation();
		switch (index) {
		// The first input
		case 0:
			return new Point((int) (location.getX() + m_firstInput.getLocation().getX()),
					(int) (location.getY() + m_firstInput.getLocation().getY()));
		// The second input
		case 1:
			return new Point((int) (location.getX() + m_secondInput.getLocation().getX()),
					(int) (location.getY() + m_secondInput.getLocation().getY()));
		// The output
		case 2:
			return new Point((int) (location.getX() + m_output.getLocation().getX()),
					(int) (location.getY() + m_output.getLocation().getY()));
		default:
			return null;
		}
	}

	/**
	 * Returns the value at the given index in its string representation.
	 */
	@Override
	public String getValueAsString(int index) {
		switch (index) {
		case 0:
			return m_firstInput.getText();
		case 1:
			return m_secondInput.getText();
		case 2:
			return m_output.getText();
		default:
			return "";
		}
	}

	/**
	 * Hides the calculator GUI.
	 */
	@Override
	public void hideCalculator() {
		setVisible(false);
	}

	/**
	 * hides the existing flash.
	 */
	@Override
	public void hideFlash() {
		m_firstInput.setBackground(UIManager.getColor("Button.background"));
		m_secondInput.setBackground(UIManager.getColor("Button.background"));
		m_output.setBackground(UIManager.getColor("Button.background"));
	}

	/**
	 * Hides all highlightes.
	 */
	@Override
	public void hideHighlight() {
		m_firstInput.setForeground(Color.black);
		m_secondInput.setForeground(Color.black);
		m_output.setForeground(Color.black);
	}

	/**
	 * Hides the left input.
	 */
	@Override
	public void hideLeftInput() {
		m_firstInput.setVisible(false);
	}

	/**
	 * Highlights the value at the given index.
	 */
	@Override
	public void highlight(int index) {
		switch (index) {
		case 0:
			m_firstInput.setForeground(Color.blue);
			break;
		case 1:
			m_secondInput.setForeground(Color.blue);
			break;
		case 2:
			m_output.setForeground(Color.blue);
			break;
		}
	}

	// Initialization of this component.
	private void jbInit() {
		this.setLayout(null);
		m_firstInput.setHorizontalAlignment(SwingConstants.RIGHT);
		m_firstInput.setBounds(new Rectangle(18, 8, 124, 19));
		m_firstInput.setBackground(UIManager.getColor("Button.background"));
		m_firstInput.setFont(Utilities.valueFont);
		m_command.setFont(Utilities.bigLabelsFont);
		m_command.setHorizontalAlignment(SwingConstants.CENTER);
		m_command.setBounds(new Rectangle(2, 34, 13, 19));
		m_command.setBackground(UIManager.getColor("Button.background"));
		m_command.setBorder(null);
		m_secondInput.setHorizontalAlignment(SwingConstants.RIGHT);
		m_secondInput.setBounds(new Rectangle(18, 34, 124, 19));
		m_secondInput.setBackground(UIManager.getColor("Button.background"));
		m_secondInput.setFont(new java.awt.Font("Courier New", 0, 12));
		m_output.setHorizontalAlignment(SwingConstants.RIGHT);
		m_output.setBounds(new Rectangle(18, 70, 124, 19));
		m_output.setBackground(UIManager.getColor("Button.background"));
		m_output.setFont(Utilities.valueFont);
		this.add(m_secondInput, null);
		this.add(m_firstInput, null);
		this.add(m_output, null);
		this.add(m_command, null);
	}

	/**
	 * Paints the line of this component.
	 */
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setPaint(Color.black);
		g2.setStroke(WIDE_STROKE);
		g2.draw(new Line2D.Double(18, 60, 142, 60));
		g2.setStroke(REGULAR_STROKE);
	}

	/**
	 * Resets the contents of this component.
	 */
	@Override
	public void reset() {
		m_firstInput.setText(translateValueToString(m_nullValue));
		m_secondInput.setText(translateValueToString(m_nullValue));
		m_output.setText(translateValueToString(m_nullValue));
		hideFlash();
		hideHighlight();
	}

	/**
	 * Sets the null value of this component.
	 */
	@Override
	public void setNullValue(short value, boolean hideNullValue) {
		m_nullValue = value;
		this.m_hideNullValue = hideNullValue;
	}

	/**
	 * Sets the numeric format with the given code (out of the format constants
	 * in HackController).
	 */
	@Override
	public void setNumericFormat(int formatCode) {
	}

	/**
	 * Sets the operator of the calculator with the given operator.
	 */
	@Override
	public void setOperator(char operator) {
		m_command.setText(String.valueOf(operator));
	}

	/**
	 * Sets the element at the given index with the given value.
	 */
	@Override
	public void setValueAt(int index, short value) {
		String data = translateValueToString(value);
		switch (index) {
		case 0:
			m_firstInput.setText(data);
			break;
		case 1:
			m_secondInput.setText(data);
			break;
		case 2:
			m_output.setText(data);
			break;
		}
	}

	/**
	 * Displays the calculator GUI.
	 */
	@Override
	public void showCalculator() {
		setVisible(true);
	}

	/**
	 * Displays the left input.
	 */
	@Override
	public void showLeftInput() {
		m_firstInput.setVisible(true);
	}

	/**
	 * Translates a given short to a string according to the current format.
	 */
	protected String translateValueToString(short value) {
		if (m_hideNullValue && value == m_nullValue)
				return "";
			
		return Format.translateValueToString(value, Format.DEC_FORMAT);
	}
}
