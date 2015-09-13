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

import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;

import javax.swing.BorderFactory;

import Hack.CPUEmulator.KeyboardGUI;
import Hack.CPUEmulator.ScreenGUI;
import Hack.ComputerParts.BusGUI;
import Hack.ComputerParts.LabeledPointedMemoryGUI;
import Hack.ComputerParts.MemorySegmentGUI;
import Hack.ComputerParts.PointedMemorySegmentGUI;
import Hack.VMEmulator.CalculatorGUI;
import Hack.VMEmulator.CallStackGUI;
import Hack.VMEmulator.VMEmulatorGUI;
import Hack.VMEmulator.VMProgramGUI;
import HackGUI.AbsolutePointedMemorySegmentComponent;
import HackGUI.BusComponent;
import HackGUI.LabeledMemoryComponent;
import HackGUI.TrimmedValuesOnlyAbsoluteMemorySegmentComponent;

/**
 * This class represents the gui of the VMEmulator.
 */
public class VMEmulatorComponent extends HackSimulatorComponent implements VMEmulatorGUI {
 
	private static final long serialVersionUID = -1814109754899106821L;
	// The dimension of this window.
	private static final int WIDTH = 1018;
	private static final int HEIGHT = 611;

	// The keyboard of the VMEmulator component.
	private KeyboardComponent m_keyboard;

	// The screen of the VMEmulator component.
	private ScreenComponent m_screen;

	// The call stack of the VMEmulator component.
	private CallStackComponent m_callStack;

	// The program of the VMEmulator component.
	private ProgramComponent m_program;

	// The ram of the VMEmulator component.
	private LabeledMemoryComponent m_ram;

	// The stack of the VMEmulator component.
	private AbsolutePointedMemorySegmentComponent m_stack;

	// The memory segments of the VMEmulator component.
	private MemorySegmentsComponent m_segments;

	// The bus of the VMEmulator component.
	private BusComponent m_bus;

	// The calculator of this emulator.
	private StackCalculator m_calculator;

	// The working stack of the VMEmulator component.
	private TrimmedValuesOnlyAbsoluteMemorySegmentComponent m_workingStack;

	/**
	 * Constructs a new VMEmulatorGUI.
	 */
	public VMEmulatorComponent() {
		m_bus = new BusComponent();
		m_screen = new ScreenComponent();
		m_keyboard = new KeyboardComponent();
		m_ram = new LabeledMemoryComponent();
		m_ram.setName("RAM");
		m_callStack = new CallStackComponent();
		m_program = new ProgramComponent();
		m_segments = new MemorySegmentsComponent();
		m_workingStack = new TrimmedValuesOnlyAbsoluteMemorySegmentComponent();
		m_workingStack.setSegmentName("Stack");
		m_stack = new AbsolutePointedMemorySegmentComponent();
		m_calculator = new StackCalculator();
		setSegmentsRam();
		setStackName();

		jbInit();

		// Setting the top level location of the components.
		m_ram.setTopLevelLocation(this);
		m_segments.getStaticSegment().setTopLevelLocation(this);
		m_segments.getLocalSegment().setTopLevelLocation(this);
		m_segments.getArgSegment().setTopLevelLocation(this);
		m_segments.getThisSegment().setTopLevelLocation(this);
		m_segments.getThatSegment().setTopLevelLocation(this);
		m_segments.getTempSegment().setTopLevelLocation(this);
		m_stack.setTopLevelLocation(this);
		m_workingStack.setTopLevelLocation(this);
	}

	@Override
	public Point getAdditionalDisplayLocation() {
		return new Point(492, 10);
	}

	/**
	 * Returns the arg memory segment component.
	 */
	@Override
	public MemorySegmentGUI getArgSegment() {
		return m_segments.getArgSegment();
	}

	/**
	 * Returns the bus GUI component.
	 */
	@Override
	public BusGUI getBus() {
		return m_bus;
	}

	/**
	 * Returns the calculator GUI component.
	 */
	@Override
	public CalculatorGUI getCalculator() {
		return m_calculator;
	}

	/**
	 * Returns the call stack GUI component.
	 */
	@Override
	public CallStackGUI getCallStack() {
		return m_callStack;
	}

	/**
	 * Returns the keyboard GUI component.
	 */
	@Override
	public KeyboardGUI getKeyboard() {
		return m_keyboard;
	}

	/**
	 * Returns the local memory segment component.
	 */
	@Override
	public MemorySegmentGUI getLocalSegment() {
		return m_segments.getLocalSegment();
	}

	/**
	 * Returns the Program GUI component.
	 */
	@Override
	public VMProgramGUI getProgram() {
		return m_program;
	}

	/**
	 * Returns the RAM GUI component.
	 */
	@Override
	public LabeledPointedMemoryGUI getRAM() {
		return m_ram;
	}

	/**
	 * Returns the screen GUI component.
	 */
	@Override
	public ScreenGUI getScreen() {
		return m_screen;
	}

	/**
	 * Returns the Stack GUI component.
	 */
	@Override
	public PointedMemorySegmentGUI getStack() {
		return m_stack;
	}

	/**
	 * Returns the static memory segment component.
	 */
	@Override
	public MemorySegmentGUI getStaticSegment() {
		return m_segments.getStaticSegment();
	}

	/**
	 * Returns the temp memory segment component.
	 */
	@Override
	public MemorySegmentGUI getTempSegment() {
		return m_segments.getTempSegment();
	}

	/**
	 * Returns the that memory segment component.
	 */
	@Override
	public MemorySegmentGUI getThatSegment() {
		return m_segments.getThatSegment();
	}

	/**
	 * Returns the this memory segment component.
	 */
	@Override
	public MemorySegmentGUI getThisSegment() {
		return m_segments.getThisSegment();
	}

	/**
	 * Returns the working stack.
	 */
	@Override
	public PointedMemorySegmentGUI getWorkingStack() {
		return m_workingStack;
	}

	// Initialization of this component.
	private void jbInit() {
		this.setLayout(null);
		m_keyboard.setBounds(492, 270, m_keyboard.getWidth(), m_keyboard.getHeight());
		m_screen.setBounds(492, 10, m_screen.getWidth(), m_screen.getHeight());
		m_program.setVisibleRows(15);
		m_program.setBounds(new Rectangle(6, 10, m_program.getWidth(), m_program.getHeight()));
		m_ram.setVisibleRows(15);
		m_ram.setBounds(new Rectangle(766, 327, m_ram.getWidth(), m_ram.getHeight()));

		m_stack.setVisibleRows(15);
		m_stack.setBounds(new Rectangle(561, 327, m_stack.getWidth(), m_stack.getHeight()));
		m_segments.getSplitPane().setBounds(
				new Rectangle(289, 10, m_segments.getSplitPane().getWidth(), m_segments.getSplitPane().getHeight()));
		m_bus.setBounds(new Rectangle(0, 0, WIDTH, HEIGHT));

		m_calculator.setBorder(BorderFactory.createLoweredBevelBorder());
		m_calculator.setBounds(new Rectangle(137, 331, 148, 103));
		m_calculator.setVisible(false);
		m_workingStack.setVisibleRows(7);
		m_workingStack.setBounds(new Rectangle(8, 304, m_workingStack.getWidth(), m_workingStack.getHeight()));

		m_callStack.setVisibleRows(7);
		m_callStack.setBounds(new Rectangle(8, 458, m_callStack.getWidth(), m_callStack.getHeight()));

		this.add(m_bus, null);
		this.add(m_screen, null);
		this.add(m_keyboard, null);
		this.add(m_program, null);
		this.add(m_workingStack, null);
		this.add(m_callStack, null);
		this.add(m_calculator, null);
		this.add(m_stack, null);
		this.add(m_ram, null);
		this.add(m_callStack, null);
		this.add(m_segments.getSplitPane(), null);

		setSize(WIDTH, HEIGHT);
	}

	@Override
	public void loadProgram() {
		m_program.loadProgram();
	}

	// Sets the memory component of the memory segments with the current RAM.
	private void setSegmentsRam() {
		// Setting the memory of the segments.
		m_segments.getStaticSegment().setMemoryComponent(m_ram);
		m_segments.getLocalSegment().setMemoryComponent(m_ram);
		m_segments.getArgSegment().setMemoryComponent(m_ram);
		m_segments.getThisSegment().setMemoryComponent(m_ram);
		m_segments.getThatSegment().setMemoryComponent(m_ram);
		m_segments.getTempSegment().setMemoryComponent(m_ram);
		m_stack.setMemoryComponent(m_ram);
		m_workingStack.setMemoryComponent(m_ram);
		// registers the segments to listen to the repain event of the ram.
		m_ram.addChangeListener(m_segments.getStaticSegment());
		m_ram.addChangeListener(m_segments.getLocalSegment());
		m_ram.addChangeListener(m_segments.getArgSegment());
		m_ram.addChangeListener(m_segments.getThisSegment());
		m_ram.addChangeListener(m_segments.getThatSegment());
		m_ram.addChangeListener(m_segments.getTempSegment());
		m_ram.addChangeListener(m_stack);
		m_ram.addChangeListener(m_workingStack);
	}

	// Sets the name of the stack.
	private void setStackName() {
		m_stack.setSegmentName("Global Stack");
	}

	@Override
	public void setWorkingDir(File file) {
		m_program.setWorkingDir(file);
	}
}
