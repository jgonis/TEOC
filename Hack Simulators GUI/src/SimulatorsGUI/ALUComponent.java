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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;

import Hack.CPUEmulator.ALUGUI;
import HackGUI.Format;
import HackGUI.Utilities;

/**
 * This class represents the gui of an ALU.
 */
public class ALUComponent extends JPanel implements ALUGUI {

	private static final long serialVersionUID = 1L;

	// location constants
	private final static int START_LOCATION_ZERO_X = 7;
	private final static int START_LOCATION_ZERO_Y = 39;
	private final static int START_LOCATION_ONE_Y = 85;
	private final static int START_LOCATION_TWO_X = 237;
	private final static int START_LOCATION_TWO_Y = 61;
	private final static int START_ALU_X = 159;
	private final static int FINISH_ALU_X = 216;
	private final static int LOCATION_WIDTH = 124;
	private final static int LOCATION_HEIGHT = 19;

	// A wide stroke for painting the bounds of the alu.
	private final static BasicStroke WIDE_STROKE = new BasicStroke(3.0f);

	// A regular thin stroke.
	private final static BasicStroke REGULAR_STROKE = new BasicStroke(1.0f);

	// The format in which the value is represented: decimal, hexadecimal
	// or binary.
	protected int m_dataFormat;

	// The value in location0
	protected short m_location0Value;

	// The value in location1
	protected short m_location1Value;

	// The value in location2
	protected short m_location2Value;

	// Creating the three text fields.
	protected JTextField m_location0 = new JTextField();
	protected JTextField m_location1 = new JTextField();
	protected JTextField m_location2 = new JTextField();

	// the command of this ALU.
	private JTextField m_commandLbl = new JTextField();

	// The initial ALU color.
	private Color m_aluColor = new Color(107, 194, 46);

	// The label with the string "ALU".
	private JLabel m_nameLbl = new JLabel();

	// The border of the alu's command.
	private Border m_commandBorder;

	// creating the labels of the inputs and outputs.
	private JLabel m_location0Lbl = new JLabel();
	private JLabel m_location1Lbl = new JLabel();
	private JLabel m_location2Lbl = new JLabel();

	// The null value of this ALU
	protected short m_nullValue;

	// a boolean field specifying if the null value should be activated or not.
	protected boolean m_hideNullValue;

	/**
	 * Constructs a new ALUComponent.
	 */
	public ALUComponent() {
		m_dataFormat = Format.DEC_FORMAT;
		jbInit();
	}

	/**
	 * Starts the alu's flashing.
	 */
	@Override
	public void bodyFlash() {
		m_aluColor = Color.red;
		m_commandLbl.setBackground(Color.red);
		repaint();
	}

	/**
	 * Flashes the ALU command.
	 */
	@Override
	public void commandFlash() {
		m_commandLbl.setBackground(Color.red);
		repaint();
	}

	/**
	 * Enabling and diabling user inputs. those methods aren't implemented
	 * because in the ALU the text fields are always disabled.
	 */
	public void disableUserInput() {
	}

	public void enableUserInput() {
	}

	/**
	 * flashes the value at the given index.
	 */
	@Override
	public void flash(int index) {
		switch (index) {
		case 0:
			m_location0.setBackground(Color.orange);
			break;
		case 1:
			m_location1.setBackground(Color.orange);
			break;
		case 2:
			m_location2.setBackground(Color.orange);
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
		case 0:
			return new Point((int) (location.getX() + m_location0.getLocation().getX()),
					(int) (location.getY() + m_location0.getLocation().getY()));
		case 1:
			return new Point((int) (location.getX() + m_location1.getLocation().getX()),
					(int) (location.getY() + m_location1.getLocation().getY()));
		case 2:
			return new Point((int) (location.getX() + m_location2.getLocation().getX()),
					(int) (location.getY() + m_location2.getLocation().getY()));
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
			return m_location0.getText();
		case 1:
			return m_location1.getText();
		case 2:
			return m_location2.getText();
		default:
			return null;
		}
	}

	/**
	 * Stops the alu's flashing.
	 */
	@Override
	public void hideBodyFlash() {
		m_aluColor = new Color(107, 194, 46);
		m_commandLbl.setBackground(new Color(107, 194, 46));
		repaint();
	}

	/**
	 * Hides the ALU's command flash.
	 */
	@Override
	public void hideCommandFlash() {
		m_commandLbl.setBackground(new Color(107, 194, 46));
		repaint();
	}

	/**
	 * hides the existing flash.
	 */
	@Override
	public void hideFlash() {
		m_location0.setBackground(null);
		m_location1.setBackground(null);
		m_location2.setBackground(null);
	}

	/**
	 * Hides all highlightes.
	 */
	@Override
	public void hideHighlight() {
		m_location0.setDisabledTextColor(Color.black);
		m_location1.setDisabledTextColor(Color.black);
		m_location2.setDisabledTextColor(Color.black);
		repaint();
	}

	/**
	 * Highlights the value at the given index.
	 */
	@Override
	public void highlight(int index) {

		switch (index) {
		case 0:
			m_location0.setDisabledTextColor(Color.blue);
			break;
		case 1:
			m_location1.setDisabledTextColor(Color.blue);
			break;
		case 2:
			m_location2.setDisabledTextColor(Color.blue);
			break;
		}
		repaint();
	}

	// Initializes this component.
	private void jbInit() {
		setOpaque(false);
		m_commandBorder = BorderFactory.createLineBorder(Color.black, 1);
		this.setLayout(null);
		m_location0.setForeground(Color.black);
		m_location0.setDisabledTextColor(Color.black);
		m_location0.setEditable(false);
		m_location0.setHorizontalAlignment(SwingConstants.RIGHT);
		m_location0.setBounds(
				new Rectangle(START_LOCATION_ZERO_X, START_LOCATION_ZERO_Y, LOCATION_WIDTH, LOCATION_HEIGHT));
		m_location0.setBackground(UIManager.getColor("Button.background"));
		m_location0.setEnabled(false);
		m_location0.setFont(Utilities.valueFont);
		m_location1.setHorizontalAlignment(SwingConstants.RIGHT);
		m_location1
				.setBounds(new Rectangle(START_LOCATION_ZERO_X, START_LOCATION_ONE_Y, LOCATION_WIDTH, LOCATION_HEIGHT));
		m_location1.setForeground(Color.black);
		m_location1.setDisabledTextColor(Color.black);
		m_location1.setEditable(false);
		m_location1.setBackground(UIManager.getColor("Button.background"));
		m_location1.setEnabled(false);
		m_location1.setFont(Utilities.valueFont);
		m_location2.setHorizontalAlignment(SwingConstants.RIGHT);
		m_location2.setBounds(new Rectangle(START_LOCATION_TWO_X, START_LOCATION_TWO_Y, LOCATION_WIDTH, LOCATION_HEIGHT));
		m_location2.setForeground(Color.black);
		m_location2.setDisabledTextColor(Color.black);
		m_location2.setEditable(false);
		m_location2.setBackground(UIManager.getColor("Button.background"));
		m_location2.setEnabled(false);
		m_location2.setFont(Utilities.valueFont);
		m_commandLbl.setBackground(new Color(107, 194, 46));
		m_commandLbl.setEnabled(false);
		m_commandLbl.setFont(Utilities.labelsFont);
		m_commandLbl.setForeground(Color.black);
		m_commandLbl.setBorder(m_commandBorder);
		m_commandLbl.setDisabledTextColor(Color.black);
		m_commandLbl.setEditable(false);
		m_commandLbl.setHorizontalAlignment(SwingConstants.CENTER);
		m_commandLbl.setBounds(new Rectangle(163, 62, 50, 16));
		m_location0Lbl.setText("D Input :");
		m_location0Lbl.setBounds(new Rectangle(START_LOCATION_ZERO_X, START_LOCATION_ZERO_Y - 16, 56, 16));
		m_location0Lbl.setFont(Utilities.smallLabelsFont);
		m_location0Lbl.setForeground(Color.black);
		m_location1Lbl.setText("M/A Input :");
		m_location1Lbl.setBounds(new Rectangle(START_LOCATION_ZERO_X, START_LOCATION_ONE_Y - 16, 70, 16));
		m_location1Lbl.setFont(Utilities.smallLabelsFont);
		m_location1Lbl.setForeground(Color.black);
		m_location2Lbl.setText("ALU output :");
		m_location2Lbl.setBounds(new Rectangle(START_LOCATION_TWO_X, START_LOCATION_TWO_Y - 16, 72, 16));
		m_location2Lbl.setFont(Utilities.smallLabelsFont);
		m_location2Lbl.setForeground(Color.black);
		m_nameLbl.setText("ALU");
		m_nameLbl.setFont(Utilities.labelsFont);
		m_nameLbl.setBounds(new Rectangle(6, 0, 50, 22));
		this.add(m_commandLbl, null);
		this.add(m_location1, null);
		this.add(m_location0, null);
		this.add(m_location2, null);
		this.add(m_location0Lbl, null);
		this.add(m_location1Lbl, null);
		this.add(m_location2Lbl, null);
		this.add(m_nameLbl, null);

		setBorder(BorderFactory.createEtchedBorder());
		setPreferredSize(new Dimension(368, 122));
		setSize(368, 122);
	}

	/**
	 * Paint this ALUComponent.
	 */
	@Override
	public void paintComponent(Graphics g) {

		Graphics2D g2 = (Graphics2D) g;
		g2.setPaint(Color.black);

		// fill and stroke GeneralPath
		int x4Points[] = { START_ALU_X, FINISH_ALU_X, FINISH_ALU_X, START_ALU_X };
		int y4Points[] = { 23, 56, 83, 116 };

		GeneralPath filledPolygon = new GeneralPath(Path2D.WIND_EVEN_ODD, x4Points.length);
		filledPolygon.moveTo(x4Points[0], y4Points[0]);

		for (int index = 1; index < x4Points.length; index++) {
			filledPolygon.lineTo(x4Points[index], y4Points[index]);

		}
		;
		filledPolygon.closePath();
		g2.setPaint(m_aluColor);
		g2.fill(filledPolygon);
		g2.setStroke(WIDE_STROKE);
		g2.setPaint(Color.black);
		g2.draw(filledPolygon);
		g2.setStroke(REGULAR_STROKE);

		// Drawing the lines.
		g2.draw(new Line2D.Double(START_LOCATION_ZERO_X + LOCATION_WIDTH, START_LOCATION_ZERO_Y + (LOCATION_HEIGHT / 2),
				START_ALU_X, START_LOCATION_ZERO_Y + (LOCATION_HEIGHT / 2)));
		g2.draw(new Line2D.Double(START_LOCATION_ZERO_X + LOCATION_WIDTH, START_LOCATION_ONE_Y + (LOCATION_HEIGHT / 2),
				START_ALU_X, START_LOCATION_ONE_Y + (LOCATION_HEIGHT / 2)));
		g2.draw(new Line2D.Double(FINISH_ALU_X, START_LOCATION_TWO_Y + (LOCATION_HEIGHT / 2), START_LOCATION_TWO_X - 1,
				START_LOCATION_TWO_Y + (LOCATION_HEIGHT / 2)));

	}

	/**
	 * Resets the contents of this ALUComponent.
	 */
	@Override
	public void reset() {
		m_location0.setText(Format.translateValueToString(m_nullValue, m_dataFormat));
		m_location1.setText(Format.translateValueToString(m_nullValue, m_dataFormat));
		m_location2.setText(Format.translateValueToString(m_nullValue, m_dataFormat));
		setCommand("");
		hideFlash();
		hideHighlight();
	}

	/**
	 * Sets the command with the given one.
	 */
	@Override
	public void setCommand(String command) {
		m_commandLbl.setText(command);
	}

	/**
	 * Sets the null value.
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
		m_dataFormat = formatCode;
		m_location0.setText(Format.translateValueToString(m_location0Value, formatCode));
		m_location1.setText(Format.translateValueToString(m_location1Value, formatCode));
		m_location2.setText(Format.translateValueToString(m_location2Value, formatCode));
	}

	/**
	 * Sets the element at the given index with the given value.
	 */
	@Override
	public void setValueAt(int index, short value) {

		String data = Format.translateValueToString(value, m_dataFormat);
		switch (index) {
		case 0:
			this.m_location0Value = value;
			m_location0.setText(data);
			break;
		case 1:
			this.m_location1Value = value;
			m_location1.setText(data);
			break;
		case 2:
			this.m_location2Value = value;
			m_location2.setText(data);
			break;
		}
	}

	/**
	 * Translates a given short to a string according to the current format.
	 */
	protected String translateValueToString(short value) {
		if (m_hideNullValue) {
			if (value == m_nullValue) {
				return "";
			} else {
				return Format.translateValueToString(value, m_dataFormat);
			}
		} else {
			return Format.translateValueToString(value, m_dataFormat);
		}

	}
}
