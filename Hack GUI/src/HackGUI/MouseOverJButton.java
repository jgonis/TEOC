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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;

/**
 * A button which draws a raised border when the mouse cursor is over it.
 */
public class MouseOverJButton extends JButton implements MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8261952395023761198L;

	/**
	 * Constructs a new MouseOverJButton.
	 */
	public MouseOverJButton() {
		setBorder(null);
		addMouseListener(this);
	}

	/**
	 * Empty implementation.
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	/**
	 * Called just after the cursor enters the bounds of the listened-to
	 * component.
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		setBorder(BorderFactory.createRaisedBevelBorder());
	}

	/**
	 * Called just after the cursor exits the bounds of the listened-to
	 * component.
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		setBorder(null);
	}

	/**
	 * Empty implementation.
	 */
	@Override
	public void mousePressed(MouseEvent e) {
	}

	/**
	 * Empty implementation.
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
	}
}
