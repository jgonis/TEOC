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

package Hack.VMEmulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

import Hack.ComputerParts.ComputerPartGUI;
import Hack.ComputerParts.InteractiveComputerPart;
import Hack.Controller.ProgramException;
import Hack.Events.ProgramEvent;
import Hack.Events.ProgramEventListener;
import Hack.Utilities.Definitions;
import Hack.Utilities.HackFileFilter;
import Hack.VirtualMachine.HVMInstructionSet;

/**
 * A list of VM instructions, with a program counter.
 */
public class VMProgram extends InteractiveComputerPart implements ProgramEventListener {

	// The task that loads a new program into the emulator
	class LoadProgramTask implements Runnable {

		private String fileName;

		public LoadProgramTask(String fileName) {
			this.fileName = fileName;
		}

		@Override
		public void run() {
			clearErrorListeners();
			try {
				loadProgram(fileName);
			} catch (ProgramException pe) {
				notifyErrorListeners(pe.getMessage());
			}
		}
	}

	// pseudo address for returning to built-in functions
	public static final short BUILTIN_FUNCTION_ADDRESS = -1;
	// Possible values for the current status - has the user allowed
	// access to built-in vm functions?
	private static final int BUILTIN_ACCESS_UNDECIDED = 0;
	private static final int BUILTIN_ACCESS_AUTHORIZED = 1;

	private static final int BUILTIN_ACCESS_DENIED = 2;

	// listeners to program changes
	private Vector<ProgramEventListener> listeners;
	// The list of VM instructions
	private VMEmulatorInstruction[] instructions;
	private int instructionsLength;

	private int visibleInstructionsLength;
	// The program counter - points to the next instruction that should be
	// executed.
	private short nextPC;
	private short currentPC;

	private short prevPC;

	// The gui of the program.
	private VMProgramGUI m_gui;

	// The address of the initial instruction
	private short startAddress;

	// Mapping from file names to an array of two elements, containing the start
	// and
	// end addresses of the corresponding static segment.
	private Hashtable<String, Object> staticRange;
	// Addresses of functions by name
	private Hashtable<String, Short> m_functions;

	private short infiniteLoopForBuiltInsAddress;

	// The current index of the static variables
	private int currentStaticIndex;

	// The largest static variable index found in the current file.
	private int largestStaticIndex;

	// Has the user allowed access to built-in vm functions?
	private int builtInAccessStatus;

	// Is the program currently being read in the middle of a /* */ comment?
	private boolean isSlashStar;

	/**
	 * Constructs a new empty program with the given GUI.
	 */
	public VMProgram(VMProgramGUI gui) {
		super(gui != null);
		this.m_gui = gui;
		listeners = new Vector<ProgramEventListener>();
		staticRange = new Hashtable<String, Object>();
		m_functions = new Hashtable<String, Short>();

		if (hasGUI) {
			m_gui.addProgramListener(this);
			m_gui.addErrorListener(this);
		}

		reset();
	}

	/**
	 * Registers the given ProgramEventListener as a listener to this GUI.
	 */
	public void addProgramListener(ProgramEventListener listener) {
		listeners.add(listener);
	}

	// Scans the given file and creates symbols for its functions & label names.
	private void buildProgram(File file, Hashtable<String, Short> symbols) throws ProgramException {
		int lineNumber = 0;

		try (BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()))) {
			String line;
			String label;
			String instructionName;
			String currentFunction = null;
			short indexInFunction = 0;
			byte opCode;
			short arg0, arg1;
			short pc = nextPC;
			HVMInstructionSet instructionSet = HVMInstructionSet.getInstance();

			isSlashStar = false;

			while ((line = unCommentLine(reader.readLine())) != null) {
				lineNumber++;

				if (!line.trim().equals("")) {
					StringTokenizer tokenizer = new StringTokenizer(line);
					instructionName = tokenizer.nextToken();

					opCode = instructionSet.instructionStringToCode(instructionName);
					if (opCode == HVMInstructionSet.UNKNOWN_INSTRUCTION) {
						throw new ProgramException(
								"in line " + lineNumber + ": unknown instruction - " + instructionName);
					}

					switch (opCode) {
					case HVMInstructionSet.PUSH_CODE:
						String segment = tokenizer.nextToken();
						try {
							arg0 = translateSegment(segment, instructionSet);
						} catch (ProgramException pe) {
							throw new ProgramException("in line " + lineNumber + pe.getMessage());
						}
						arg1 = Short.parseShort(tokenizer.nextToken());
						if (arg1 < 0) {
							throw new ProgramException("in line " + lineNumber + ": Illegal argument - " + line);
						}

						if ((arg0 == HVMInstructionSet.STATIC_SEGMENT_CODE) && (arg1 > largestStaticIndex)) {
							largestStaticIndex = arg1;
						}

						instructions[pc] = new VMEmulatorInstruction(opCode, arg0, arg1, indexInFunction);
						break;

					case HVMInstructionSet.POP_CODE:
						tokenizer.countTokens();
						segment = tokenizer.nextToken();
						try {
							arg0 = translateSegment(segment, instructionSet);
						} catch (ProgramException pe) {
							throw new ProgramException("in line " + lineNumber + pe.getMessage());
						}
						arg1 = Short.parseShort(tokenizer.nextToken());

						if (arg1 < 0) {
							throw new ProgramException("in line " + lineNumber + ": Illegal argument - " + line);
						}

						if ((arg0 == HVMInstructionSet.STATIC_SEGMENT_CODE) && (arg1 > largestStaticIndex)) {
							largestStaticIndex = arg1;
						}

						instructions[pc] = new VMEmulatorInstruction(opCode, arg0, arg1, indexInFunction);
						break;

					case HVMInstructionSet.FUNCTION_CODE:
						currentFunction = tokenizer.nextToken();
						indexInFunction = 0;
						arg0 = Short.parseShort(tokenizer.nextToken());

						if (arg0 < 0) {
							throw new ProgramException("in line " + lineNumber + ": Illegal argument - " + line);
						}

						instructions[pc] = new VMEmulatorInstruction(opCode, arg0, indexInFunction);
						instructions[pc].setStringArg(currentFunction);
						break;

					case HVMInstructionSet.CALL_CODE:
						String functionName = tokenizer.nextToken();
						try {
							arg0 = getAddress(functionName);
						} catch (ProgramException pe) {
							throw new ProgramException("in line " + lineNumber + ": " + pe.getMessage());
						}
						arg1 = Short.parseShort(tokenizer.nextToken());

						if ((arg1 < 0) || (((arg0 < 0) || (arg0 > Definitions.ROM_SIZE))
								&& (arg0 != BUILTIN_FUNCTION_ADDRESS))) {
							throw new ProgramException("in line " + lineNumber + ": Illegal argument - " + line);
						}

						instructions[pc] = new VMEmulatorInstruction(opCode, arg0, arg1, indexInFunction);
						instructions[pc].setStringArg(functionName);
						break;

					case HVMInstructionSet.LABEL_CODE:
						label = currentFunction + "$" + tokenizer.nextToken();
						instructions[pc] = new VMEmulatorInstruction(opCode, (short) (-1));
						instructions[pc].setStringArg(label);
						indexInFunction--; // since Label is not a "physical"
											// instruction
						break;

					case HVMInstructionSet.GOTO_CODE:
						label = currentFunction + "$" + tokenizer.nextToken();
						Short labelAddress = symbols.get(label);
						if (labelAddress == null) {
							throw new ProgramException("in line " + lineNumber + ": Unknown label - " + label);
						}
						arg0 = labelAddress.shortValue();

						if ((arg0 < 0) || (arg0 > Definitions.ROM_SIZE)) {
							throw new ProgramException("in line " + lineNumber + ": Illegal argument - " + line);
						}

						instructions[pc] = new VMEmulatorInstruction(opCode, arg0, indexInFunction);
						instructions[pc].setStringArg(label);
						break;

					case HVMInstructionSet.IF_GOTO_CODE:
						label = currentFunction + "$" + tokenizer.nextToken();
						labelAddress = symbols.get(label);
						if (labelAddress == null) {
							throw new ProgramException("in line " + lineNumber + ": Unknown label - " + label);
						}

						arg0 = labelAddress.shortValue();

						if ((arg0 < 0) || (arg0 > Definitions.ROM_SIZE)) {
							throw new ProgramException("in line " + lineNumber + ": Illegal argument - " + line);
						}

						instructions[pc] = new VMEmulatorInstruction(opCode, arg0, indexInFunction);
						instructions[pc].setStringArg(label);
						break;

					// All other instructions have either 1 or 0 arguments and
					// require no
					// special treatment
					default:
						if (tokenizer.countTokens() == 0) {
							instructions[pc] = new VMEmulatorInstruction(opCode, indexInFunction);
						} else {
							arg0 = Short.parseShort(tokenizer.nextToken());

							if (arg0 < 0) {
								throw new ProgramException("in line " + lineNumber + ": Illegal argument - " + line);
							}

							instructions[pc] = new VMEmulatorInstruction(opCode, arg0, indexInFunction);
						}
						break;
					}

					// check end of command
					if (tokenizer.hasMoreTokens()) {
						throw new ProgramException("in line " + lineNumber + ": Too many arguments - " + line);
					}

					pc++;
					indexInFunction++;
				}

				nextPC = pc;
			}
			reader.close();
		} catch (IOException ioe) {
			throw new ProgramException("Error while reading from file");
		} catch (NumberFormatException nfe) {
			throw new ProgramException("Illegal 16-bit value");
		} catch (NoSuchElementException nsee) {
			throw new ProgramException("In line " + lineNumber + ": unexpected end of command");
		}
		if (isSlashStar) {
			throw new ProgramException("Unterminated /* comment at end of file");
		}
	}

	public short getAddress(String functionName) throws ProgramException {
		Short address = m_functions.get(functionName);
		if (address != null) {
			return address.shortValue();
		} else {
			String className = functionName.substring(0, functionName.indexOf("."));
			if (staticRange.get(className) == null) {
				// The class is not implemented by a VM file - search for a
				// built-in implementation later. Display a popup to confirm
				// this as this is not a feature from the book but a later
				// addition.
				if (builtInAccessStatus == BUILTIN_ACCESS_UNDECIDED) {
					if (hasGUI && m_gui.confirmBuiltInAccess()) {
						builtInAccessStatus = BUILTIN_ACCESS_AUTHORIZED;
					} else {
						builtInAccessStatus = BUILTIN_ACCESS_DENIED;
					}
				}
				if (builtInAccessStatus == BUILTIN_ACCESS_AUTHORIZED) {
					return BUILTIN_FUNCTION_ADDRESS;
				}
			}
			// Either:
			// 1.The class is implemented by a VM file and no implementation
			// for the function is found - don't override with built-in
			// - or -
			// 2.The user did not authorize using built-in implementations.
			throw new ProgramException(className + ".vm not found " + "or function " + functionName + " not found in "
					+ className + ".vm");
		}
	}

	/**
	 * Returns the current value of the program counter.
	 */
	public short getCurrentPC() {
		return currentPC;
	}

	/**
	 * Returns the GUI of the computer part.
	 */
	@Override
	public ComputerPartGUI getGUI() {
		return m_gui;
	}

	/**
	 * Returns the next VMEmulatorInstruction and increments the PC by one. The
	 * PC will be incremented by more if the next instruction is a label.
	 */
	public VMEmulatorInstruction getNextInstruction() {
		VMEmulatorInstruction result = null;

		if (nextPC < instructionsLength) {
			result = instructions[nextPC];
			prevPC = currentPC;
			currentPC = nextPC;

			do {
				nextPC++;
			} while ((nextPC < instructionsLength)
					&& (instructions[nextPC].getOpCode() == HVMInstructionSet.LABEL_CODE));

			setGUIPC();
		}

		return result;
	}

	/**
	 * Returns the next program counter.
	 */
	public short getPC() {
		return nextPC;
	}

	/**
	 * Returns the previous value of the program counter.
	 */
	public short getPreviousPC() {
		return prevPC;
	}

	/**
	 * Returns the size of the program.
	 */
	public int getSize() {
		return instructionsLength;
	}

	/**
	 * Returns the static variable address range of the given class name, in the
	 * form of a 2-elements array {startAddress, endAddress}. If unknown class
	 * name, returns null.
	 */
	public int[] getStaticRange(String className) {
		return (int[]) staticRange.get(className);
	}

	/**
	 * Creates a vm program. If the given file is a dir, creates a program
	 * composed of the vm files in the dir. The vm files are scanned twice: in
	 * the first scan a symbol table (that maps function & label names into
	 * addresses) is built. In the second scan, the instructions array is built.
	 * Throws ProgramException if an error occurs while loading the program.
	 */
	public void loadProgram(String fileName) throws ProgramException {
		File file = new File(fileName);
		if (!file.exists()) {
			throw new ProgramException("cannot find " + fileName);
		}

		File[] files;

		if (file.isDirectory()) {
			files = file.listFiles(new HackFileFilter(".vm"));
			if ((files == null) || (files.length == 0)) {
				throw new ProgramException("No vm files found in " + fileName);
			}
		} else {
			files = new File[] { file };
		}

		if (displayChanges) {
			m_gui.showMessage("Loading...");
		}

		// First scan
		staticRange.clear();
		m_functions.clear();
		builtInAccessStatus = BUILTIN_ACCESS_UNDECIDED;
		Hashtable<String, Short> symbols = new Hashtable<String, Short>();
		nextPC = 0;
		for (File file2 : files) {
			String name = file2.getName();
			String className = name.substring(0, name.indexOf("."));
			// put some dummy into static range - just to tell the function
			// getAddress in the second pass which classes exist
			staticRange.put(className, new Boolean(true));
			try {
				updateSymbolTable(file2, symbols, m_functions);
			} catch (ProgramException pe) {
				if (displayChanges) {
					m_gui.hideMessage();
				}
				throw new ProgramException(name + ": " + pe.getMessage());
			}
		}
		boolean addCallBuiltInSysInit = false;
		if ((file.isDirectory() || (symbols.get("Main.main") != null)) && (symbols.get("Sys.init") == null)) {
			// If the program is in multiple files or there's a Main.main
			// function it is assumed that it should be run by calling Sys.init.
			// If no Sys.init is found, add an invisible line with a call
			// to Sys.init to start on - the builtin version will be called.
			addCallBuiltInSysInit = true;
			getAddress("Sys.init"); // confirm calling the built-in Sys.init
			++nextPC; // A "call Sys.init 0" line will be added
		}

		instructions = new VMEmulatorInstruction[nextPC + 4];

		// Second scan
		nextPC = 0;
		currentStaticIndex = Definitions.VAR_START_ADDRESS;
		for (File file2 : files) {
			String name = file2.getName();
			String className = name.substring(0, name.indexOf("."));

			largestStaticIndex = -1;
			int[] range = new int[2];
			range[0] = currentStaticIndex;

			try {
				// functions is not passed as an argument since it is accessed
				// through getAddress()
				buildProgram(file2, symbols);
			} catch (ProgramException pe) {
				if (displayChanges) {
					m_gui.hideMessage();
				}
				throw new ProgramException(name + ": " + pe.getMessage());
			}

			currentStaticIndex += largestStaticIndex + 1;
			range[1] = currentStaticIndex - 1;
			staticRange.put(className, range);
		}
		instructionsLength = visibleInstructionsLength = nextPC;
		if (builtInAccessStatus == BUILTIN_ACCESS_AUTHORIZED) {
			// Add some "invisible" code in the end to make everything work
			instructionsLength += 4;
			if (addCallBuiltInSysInit) {
				instructionsLength += 1;
			}
			short indexInInvisibleCode = 0;
			// Add a jump to the end (noone should get here since
			// both calls to built-in functions indicate that
			// that this is a function-based program and not a script
			// a-la proj7, but just to be on the safe side...).
			instructions[nextPC] = new VMEmulatorInstruction(HVMInstructionSet.GOTO_CODE, (short) instructionsLength,
					indexInInvisibleCode);
			instructions[nextPC].setStringArg("afterInvisibleCode");
			nextPC++;
			// Add a small infinite loop for built-in
			// methods to call (for example when Sys.halt is
			// called it must call a non-built-in infinite loop
			// because otherwise the current script would not
			// finish running - a problem for the OS tests.
			instructions[nextPC] = new VMEmulatorInstruction(HVMInstructionSet.LABEL_CODE, (short) -1);
			instructions[nextPC].setStringArg("infiniteLoopForBuiltIns");
			nextPC++;
			infiniteLoopForBuiltInsAddress = nextPC;
			instructions[nextPC] = new VMEmulatorInstruction(HVMInstructionSet.GOTO_CODE, nextPC,
					++indexInInvisibleCode);
			instructions[nextPC].setStringArg("infiniteLoopForBuiltIns");
			nextPC++;
			if (addCallBuiltInSysInit) { // Add a call to the built-in Sys.init
				instructions[nextPC] = new VMEmulatorInstruction(HVMInstructionSet.CALL_CODE, getAddress("Sys.init"),
						(short) 0, ++indexInInvisibleCode);
				instructions[nextPC].setStringArg("Sys.init");
				startAddress = nextPC;
				nextPC++;
			}
			// Add the label that the first invisible code line jumps to
			instructions[nextPC] = new VMEmulatorInstruction(HVMInstructionSet.LABEL_CODE, (short) -1);
			instructions[nextPC].setStringArg("afterInvisibleCode");
			nextPC++;
		}

		if (!addCallBuiltInSysInit) {
			Short sysInitAddress = symbols.get("Sys.init");
			if (sysInitAddress == null) {
				startAddress = 0;
			} else {
				startAddress = sysInitAddress.shortValue();
			}
		}

		if (displayChanges) {
			m_gui.hideMessage();
		}

		nextPC = startAddress;
		setGUIContents();

		notifyProgramListeners(ProgramEvent.LOAD, fileName);
	}

	/**
	 * Notifies all the ProgramEventListeners on a change in the VM's program by
	 * creating a ProgramEvent (with the new event type and program's file name)
	 * and sending it using the programChanged function to all the listeners.
	 */
	protected void notifyProgramListeners(byte eventType, String programFileName) {
		ProgramEvent event = new ProgramEvent(this, eventType, programFileName);

		for (int i = 0; i < listeners.size(); i++) {
			listeners.elementAt(i).programChanged(event);
		}
	}

	/**
	 * Called when the current program file/directory is changed. The event
	 * contains the source object, the event type and the program's file/dir (if
	 * any).
	 */
	@Override
	public void programChanged(ProgramEvent event) {
		switch (event.getType()) {
		case ProgramEvent.LOAD:
			LoadProgramTask task = new LoadProgramTask(event.getProgramFileName());
			Thread t = new Thread(task);
			t.start();
			break;
		case ProgramEvent.CLEAR:
			reset();
			notifyProgramListeners(ProgramEvent.CLEAR, null);
			break;
		}
	}

	@Override
	public void refreshGUI() {
		if (displayChanges) {
			m_gui.setContents(instructions, visibleInstructionsLength);
			m_gui.setCurrentInstruction(nextPC);
		}
	}

	/**
	 * Un-registers the given ProgramEventListener from being a listener to this
	 * GUI.
	 */
	public void removeProgramListener(ProgramEventListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Resets the program (erases all commands).
	 */
	@Override
	public void reset() {
		instructions = new VMEmulatorInstruction[0];
		visibleInstructionsLength = instructionsLength = 0;
		currentPC = -999;
		prevPC = -999;
		nextPC = -1;
		setGUIContents();
	}

	/**
	 * Restarts the program from the beginning.
	 */
	public void restartProgram() {
		currentPC = -999;
		prevPC = -999;
		nextPC = startAddress;
		setGUIPC();
	}

	// Sets the gui's contents (if a gui exists)
	private void setGUIContents() {
		if (displayChanges) {
			m_gui.setContents(instructions, visibleInstructionsLength);
			m_gui.setCurrentInstruction(nextPC);
		}
	}

	// Sets the GUI's current instruction index
	private void setGUIPC() {
		if (displayChanges) {
			m_gui.setCurrentInstruction(nextPC);
		}
	}

	/**
	 * Sets the program counter with the given address.
	 */
	public void setPC(short address) {
		prevPC = currentPC;
		currentPC = nextPC;
		nextPC = address;
		setGUIPC();
	}

	/**
	 * Sets the program counter to a specially created infinite loop in the end
	 * of the programs for access by built-in functions, de-facto halting the
	 * program. important so that tests and other scripts finish counting (since
	 * a built-in infinite loop doesn't count as steps). also needed because
	 * there is no good way to use the stop button to stop an infinite loop in a
	 * built-in jack class. A message containing information may be provided
	 * (can be null).
	 */
	public void setPCToInfiniteLoopForBuiltIns(String message) {
		if (hasGUI) {
			m_gui.notify(message);
		}
		setPC(infiniteLoopForBuiltInsAddress);
	}

	// Returns the numeric representation of the given string segment.
	// Throws an exception if unknown segment.
	private static byte translateSegment(String segment, HVMInstructionSet instructionSet) throws ProgramException {
		byte code = instructionSet.segmentVMStringToCode(segment);
		if (code == HVMInstructionSet.UNKNOWN_SEGMENT) {
			throw new ProgramException(": Illegal memory segment - " + segment);
		}

		return code;
	}

	// Returns the "un-commented" version of the given line.
	// Comments can be either with // or /*.
	// The field isSlashStar holds the current /* comment state.
	private String unCommentLine(String line) {
		String result = line;

		if (line != null) {
			if (isSlashStar) {
				int posStarSlash = line.indexOf("*/");
				if (posStarSlash >= 0) {
					isSlashStar = false;
					result = unCommentLine(line.substring(posStarSlash + 2));
				} else {
					result = "";
				}
			} else {
				int posSlashSlash = line.indexOf("//");
				int posSlashStar = line.indexOf("/*");
				if ((posSlashSlash >= 0) && ((posSlashStar < 0) || (posSlashStar > posSlashSlash))) {
					result = line.substring(0, posSlashSlash);
				} else if (posSlashStar >= 0) {
					isSlashStar = true;
					result = line.substring(0, posSlashStar) + unCommentLine(line.substring(posSlashStar + 2));
				}
			}
		}

		return result;
	}

	// Scans the given file and creates symbols for its functions & label names.
	private void updateSymbolTable(File file, Hashtable<String, Short> symbols, Hashtable<String, Short> functions)
			throws ProgramException {
		int lineNumber = 0;
		try (BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()))) {

			String line;
			String currentFunction = null;
			String label;

			isSlashStar = false;

			while ((line = unCommentLine(reader.readLine())) != null) {
				lineNumber++;
				if (!line.trim().equals("")) {
					if (line.startsWith("function ")) {
						StringTokenizer tokenizer = new StringTokenizer(line);
						tokenizer.nextToken();
						currentFunction = tokenizer.nextToken();
						if (symbols.containsKey(currentFunction)) {
							throw new ProgramException("subroutine " + currentFunction + " already exists");
						}
						functions.put(currentFunction, new Short(nextPC));
						symbols.put(currentFunction, new Short(nextPC));
					} else if (line.startsWith("label ")) {
						StringTokenizer tokenizer = new StringTokenizer(line);
						tokenizer.nextToken();
						label = currentFunction + "$" + tokenizer.nextToken();
						symbols.put(label, new Short((short) (nextPC + 1)));
					}

					nextPC++;
				}
			}
			reader.close();
		} catch (IOException ioe) {
			throw new ProgramException("Error while reading from file");
		} catch (NoSuchElementException nsee) {
			throw new ProgramException("In line " + lineNumber + ": unexpected end of command");
		}
		if (isSlashStar) {
			throw new ProgramException("Unterminated /* comment at end of file");
		}
	}
}
