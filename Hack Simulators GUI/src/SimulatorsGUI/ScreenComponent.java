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
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.Timer;

import Hack.CPUEmulator.ScreenGUI;
import Hack.Utilities.Definitions;

/**
 * A Screen GUI component.
 */
public class ScreenComponent extends JPanel implements ScreenGUI, ActionListener {

	private static final long serialVersionUID = 6379766144947101096L;
	// the clock intervals for animation
	private static final int ANIMATION_CLOCK_INTERVALS = 50;
	private static final int STATIC_CLOCK_INTERVALS = 500;

	// The screen memory array
	private short[] m_data;

	// redraw flag
	private boolean m_redraw = true;

	// screen location at a given index
	private int[] m_x, m_y;

	// The screen redrawing timer
	protected Timer m_timer;

	/**
	 * Constructs a new Sceen with given height & width (in words) and amount of
	 * bits per word.
	 */
	public ScreenComponent() {
		setOpaque(true);
		setBackground(Color.white);
		setBorder(BorderFactory.createEtchedBorder());
		Insets borderInsets = getBorder().getBorderInsets(this);
		int borderWidth = borderInsets.left + borderInsets.right;
		int borderHeight = borderInsets.top + borderInsets.bottom;
		setPreferredSize(
				new Dimension(Definitions.SCREEN_WIDTH + borderWidth, Definitions.SCREEN_HEIGHT + borderHeight));
		setSize(Definitions.SCREEN_WIDTH + borderWidth, Definitions.SCREEN_HEIGHT + borderHeight);

		m_data = new short[Definitions.SCREEN_SIZE];
		m_x = new int[Definitions.SCREEN_SIZE];
		m_y = new int[Definitions.SCREEN_SIZE];
		m_x[0] = borderInsets.left;
		m_y[0] = borderInsets.top;

		// updates pixels indice
		for (int i = 1; i < Definitions.SCREEN_SIZE; i++) {
			m_x[i] = m_x[i - 1] + Definitions.BITS_PER_WORD;
			m_y[i] = m_y[i - 1];
			if (m_x[i] == (Definitions.SCREEN_WIDTH + borderInsets.left)) {
				m_x[i] = borderInsets.left;
				m_y[i]++;
			}
		}

		m_timer = new Timer(STATIC_CLOCK_INTERVALS, this);
		m_timer.start();
	}

	/**
	 * Called at constant intervals
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (m_redraw) {
			repaint();
			m_redraw = false;
		}
	}

	/**
	 * Called when the screen needs to be painted.
	 */
	@Override
	public void paintComponent(Graphics g) {

		super.paintComponent(g);

		for (int i = 0; i < Definitions.SCREEN_SIZE; i++) {
			if (m_data[i] != 0) {
				if (m_data[i] == 0xffff) {
					g.drawLine(m_x[i], m_y[i], m_x[i] + 15, m_y[i]);
				} else {
					short value = m_data[i];
					for (int j = 0; j < 16; j++) {
						if ((value & 0x1) == 1) {
							// since there's no drawPixel, uses drawLine to draw
							// one pixel
							g.drawLine(m_x[i] + j, m_y[i], m_x[i] + j, m_y[i]);
						}

						value = (short) (value >> 1);
					}
				}
			}
		}
	}

	/**
	 * Refreshes this component.
	 */
	@Override
	public void refresh() {
		if (m_redraw) {
			repaint();
			m_redraw = false;
		}
	}

	/**
	 * Resets the content of this component.
	 */
	@Override
	public void reset() {
		for (int i = 0; i < m_data.length; i++) {
			m_data[i] = 0;
		}

		m_redraw = true;
	}

	/**
	 * Updates the screen's contents with the given values array. (Assumes that
	 * the length of the values array equals the screen memory size.
	 */
	@Override
	public void setContents(short[] values) {
		m_data = values;
		m_redraw = true;
	}

	/**
	 * Updates the screen at the given index with the given value (Assumes legal
	 * index)
	 */
	@Override
	public void setValueAt(int index, short value) {
		m_data[index] = value;
		m_redraw = true;
	}

	/**
	 * Starts the animation.
	 */
	@Override
	public void startAnimation() {
		m_timer.setDelay(ANIMATION_CLOCK_INTERVALS);
	}

	/**
	 * Stops the animation.
	 */
	@Override
	public void stopAnimation() {
		m_timer.setDelay(STATIC_CLOCK_INTERVALS);
	}
}
