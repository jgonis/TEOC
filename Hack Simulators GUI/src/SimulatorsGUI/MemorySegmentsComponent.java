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

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import HackGUI.MemorySegmentComponent;

/**
 * This Panel contains six MemorySegmentComponents: static, local, arg, this,
 * that, and temp - and provides the split pane feature between them.
 */
public class MemorySegmentsComponent extends JPanel {

	private static final long serialVersionUID = 7783589319862445076L;

	// The spllit pane containing static and local.
	private JSplitPane m_segmentsSplitPane1;

	// The split pane between arg and the previous split pane.
	private JSplitPane m_segmentsSplitPane2;

	// The split pane between this and the previous split pane.
	private JSplitPane m_segmentsSplitPane3;

	// The split pane between that and the previous split pane.
	private JSplitPane m_segmentsSplitPane4;

	// The split pane between temp and the previous split pane.
	private JSplitPane m_segmentsSplitPane5;

	// 'Static' memory segment
	private MemorySegmentComponent m_staticSegment;

	// 'Local' memory segment
	private MemorySegmentComponent m_localSegment;

	// 'Arg' memory segment
	private MemorySegmentComponent m_argSegment;

	// 'This' memory segment
	private MemorySegmentComponent m_thisSegment;

	// 'That' memory segment
	private MemorySegmentComponent m_thatSegment;

	// 'Temp' memory segment
	private MemorySegmentComponent m_tempSegment;

	/**
	 * Constructs a new MemorySegmentsComponent.
	 */
	public MemorySegmentsComponent() {
		// creating the segments and giving them names.
		m_staticSegment = new MemorySegmentComponent();
		m_staticSegment.setSegmentName("Static");
		m_localSegment = new MemorySegmentComponent();
		m_localSegment.setSegmentName("Local");
		m_argSegment = new MemorySegmentComponent();
		m_argSegment.setSegmentName("Argument");
		m_thisSegment = new MemorySegmentComponent();
		m_thisSegment.setSegmentName("This");
		m_thatSegment = new MemorySegmentComponent();
		m_thatSegment.setSegmentName("That");
		m_tempSegment = new MemorySegmentComponent();
		m_tempSegment.setSegmentName("Temp");

		// creating the split panes.
		m_segmentsSplitPane5 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, m_thatSegment, m_tempSegment);
		m_segmentsSplitPane4 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, m_thisSegment, m_segmentsSplitPane5);
		m_segmentsSplitPane3 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, m_argSegment, m_segmentsSplitPane4);
		m_segmentsSplitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, m_localSegment, m_segmentsSplitPane3);
		m_segmentsSplitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, m_staticSegment, m_segmentsSplitPane2);

		// providing a one touch expandable feature to the split panes.
		m_segmentsSplitPane1.setOneTouchExpandable(true);
		m_segmentsSplitPane2.setOneTouchExpandable(true);
		m_segmentsSplitPane3.setOneTouchExpandable(true);
		m_segmentsSplitPane4.setOneTouchExpandable(true);
		m_segmentsSplitPane5.setOneTouchExpandable(true);

		// disabling the automatic border of each one of the first four
		// split panes. enabling the border of the fifth one.
		m_segmentsSplitPane5.setBorder(null);
		m_segmentsSplitPane4.setBorder(null);
		m_segmentsSplitPane3.setBorder(null);
		m_segmentsSplitPane2.setBorder(null);

		m_segmentsSplitPane1.setDividerLocation(30 + (m_staticSegment.getTable().getRowHeight() * 5));
		m_segmentsSplitPane2.setDividerLocation(30 + (m_localSegment.getTable().getRowHeight() * 5));
		m_segmentsSplitPane3.setDividerLocation(30 + (m_argSegment.getTable().getRowHeight() * 5));
		m_segmentsSplitPane4.setDividerLocation(30 + (m_thisSegment.getTable().getRowHeight() * 5));
		m_segmentsSplitPane5.setDividerLocation(30 + (m_thatSegment.getTable().getRowHeight() * 2));

		m_segmentsSplitPane1.setSize(new Dimension(195, 587));
		m_segmentsSplitPane1.setPreferredSize(new Dimension(195, 587));
	}

	/**
	 * Returns arg memory segment.
	 */
	public MemorySegmentComponent getArgSegment() {
		return m_argSegment;
	}

	/**
	 * Returns local memory segment.
	 */
	public MemorySegmentComponent getLocalSegment() {
		return m_localSegment;
	}

	/**
	 * Returns the split pane which contains all of the other split peanes.
	 */
	public JSplitPane getSplitPane() {
		return m_segmentsSplitPane1;
	}

	/**
	 * Returns static memory segment.
	 */
	public MemorySegmentComponent getStaticSegment() {
		return m_staticSegment;
	}

	/**
	 * Returns temp memory segment.
	 */
	public MemorySegmentComponent getTempSegment() {
		return m_tempSegment;
	}

	/**
	 * Returns that memory segment.
	 */
	public MemorySegmentComponent getThatSegment() {
		return m_thatSegment;
	}

	/**
	 * Returns this memory segment.
	 */
	public MemorySegmentComponent getThisSegment() {
		return m_thisSegment;
	}
}
