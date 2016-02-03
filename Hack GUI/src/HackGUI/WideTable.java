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

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JTable;
import javax.swing.table.TableModel;

/**
 * A table with a pre-determind width.
 */
public class WideTable extends JTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2031038100218191424L;
	// The width of the table.
	private int width;

	/**
	 * Constructs a new WideTable with the given model and width.
	 */
	public WideTable(TableModel model, int width) {
		super(model);
		this.width = width;
	}

	@Override
	public Rectangle getBounds() {
		Rectangle rect = super.getBounds();
		rect.width = width;
		return rect;
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension dimension = super.getPreferredSize();
		dimension.width = width;
		return dimension;
	}

	@Override
	public Dimension getSize() {
		return new Dimension(width, super.getHeight());
	}

	@Override
	public int getWidth() {
		return width;
	}
}
