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

package Hack.Translators;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.Timer;

import Hack.ComputerParts.TextFileEvent;
import Hack.ComputerParts.TextFileEventListener;
import Hack.Utilities.Definitions;

/**
 * This object provides translation services.
 */
public abstract class HackTranslator implements HackTranslatorEventListener, ActionListener, TextFileEventListener {

	// The fast forward task
	class FastForwardTask implements Runnable {
		@Override
		public void run() {
			fastForward();
		}
	}

	// The full compilation task
	class FullCompilationTask implements Runnable {

		@Override
		public void run() {
			m_gui.displayMessage("Please wait...", false);

			try {
				restartCompilation();
				fullCompilation();
			} catch (HackTranslatorException ae) {
				end(false);
				m_gui.getSource().addHighlight(m_sourcePC, true);
				m_gui.displayMessage(ae.getMessage(), true);
			}
		}
	}

	// The load source task
	class LoadSourceTask implements Runnable {
		private String fileName;

		@Override
		public void run() {
			try {
				loadSource(fileName);
			} catch (HackTranslatorException ae) {
				m_gui.setSourceName("");
				m_gui.displayMessage(ae.getMessage(), true);
			}
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
	}

	// The single step task
	class SingleStepTask implements Runnable {
		@Override
		public void run() {
			if (!m_singleStepLocked) {
				singleStep();
			}
		}
	}

	// The delay in ms between each step in fast forward
	private static final int FAST_FORWARD_DELAY = 750;

	// Returns the version string
	private static String getVersionString() {
		return " (" + Definitions.version + ")";
	}

	// the writer of the destination file
	private PrintWriter m_writer;

	// the name of the source file
	protected String m_sourceFileName;

	// the name of the destination file
	protected String m_destFileName;

	// The size of the program.
	protected int m_programSize;

	// The program array
	protected short[] m_program;

	// The source code array
	protected String[] m_source;

	// The gui of the HackTranslator
	protected HackTranslatorGUI m_gui;

	// times the fast forward process
	private Timer m_timer;

	// locked when single step in process
	private boolean m_singleStepLocked;

	// The Single Step task object
	private SingleStepTask m_singleStepTask;

	// The full compilation task object
	private FullCompilationTask m_fullCompilationTask;

	// The fast forward task object
	private FastForwardTask m_fastForwardTask;

	// The load source task object
	private LoadSourceTask m_loadSourceTask;

	// True when compilation started already with singlestep or fastforward
	protected boolean m_compilationStarted;

	// The index of the next location to compile into in the destination file.
	protected int m_destPC;

	// The index of the next location to compile in the source file.
	protected int m_sourcePC;

	// If true, change to the translator will be displayed in its GUI.
	private boolean m_updateGUI;

	// Maps between lines in the source files and their corresponding compiled
	// lines
	// in the destination. The key is the pc of the source (Integer) and
	// the value is an int array of length 2, containing start and end pc of the
	// destination
	// file.
	protected Hashtable<Integer, int[]> m_compilationMap;

	// true only in the process of full compilation
	protected boolean m_inFullCompilation;

	// true only in the process of Fast Forward
	protected boolean m_inFastForward;

	/**
	 * Constructs a new HackTranslator with the size of the program memory. The
	 * given null value will be used to fill the program initially. A non null
	 * sourceFileName specifies a source file to be loaded. The gui is assumed
	 * to be not null.
	 */
	public HackTranslator(HackTranslatorGUI gui, int size, short nullValue, String sourceFileName)
			throws HackTranslatorException {
		this.m_gui = gui;
		gui.addHackTranslatorListener(this);
		gui.getSource().addTextFileListener(this);
		gui.setTitle(getName() + getVersionString());
		m_singleStepTask = new SingleStepTask();
		m_fullCompilationTask = new FullCompilationTask();
		m_fastForwardTask = new FastForwardTask();
		m_loadSourceTask = new LoadSourceTask();
		m_timer = new Timer(FAST_FORWARD_DELAY, this);
		init(size, nullValue);

		File workingDir = loadWorkingDir();
		gui.setWorkingDir(workingDir);

		if (sourceFileName == null) {
			gui.disableSingleStep();
			gui.disableFastForward();
			gui.disableStop();
			gui.disableRewind();
			gui.disableFullCompilation();
			gui.disableSave();
			gui.enableLoadSource();
			gui.disableSourceRowSelection();
		} else {
			loadSource(sourceFileName);
			gui.setSourceName(sourceFileName);
		}
	}

	/**
	 * Constructs a new HackTranslator with the size of the program memory and
	 * source file name. The given null value will be used to fill the program
	 * initially. The compiled program can later be fetched using the
	 * getProgram() method. If save is true, the compiled program will be saved
	 * automatically into a destination file that will have the same name as the
	 * source but with the destination extension.
	 */
	public HackTranslator(String fileName, int size, short nullValue, boolean save) throws HackTranslatorException {
		if (fileName.indexOf(".") < 0) {
			fileName = fileName + "." + getSourceExtension();
		}

		checkSourceFile(fileName);

		m_source = new String[0];
		init(size, nullValue);

		loadSource(fileName);
		fullCompilation();

		if (save) {
			save();
		}
	}

	/**
	 * Called by the timer in constant intervals (when in run mode).
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (!m_singleStepLocked) {
			singleStep();
		}
	}

	@Override
	public void actionPerformed(HackTranslatorEvent event) {
		Thread t;

		switch (event.getAction()) {
		case HackTranslatorEvent.SOURCE_LOAD:
			String fileName = (String) event.getData();
			File file = new File(fileName);
			saveWorkingDir(file);
			m_gui.setTitle(getName() + getVersionString() + " - " + fileName);
			m_loadSourceTask.setFileName(fileName);
			t = new Thread(m_loadSourceTask);
			t.start();
			break;

		case HackTranslatorEvent.SAVE_DEST:
			clearMessage();
			fileName = (String) event.getData();
			try {
				checkDestinationFile(fileName);
				m_destFileName = fileName;
				file = new File(fileName);
				saveWorkingDir(file);
				m_gui.setTitle(getName() + getVersionString() + " - " + fileName);
				save();
			} catch (HackTranslatorException ae) {
				m_gui.setDestinationName("");
				m_gui.displayMessage(ae.getMessage(), true);
			}
			break;

		case HackTranslatorEvent.SINGLE_STEP:
			clearMessage();
			if (m_sourceFileName == null) {
				m_gui.displayMessage("No source file specified", true);
			} else if (m_destFileName == null) {
				m_gui.displayMessage("No destination file specified", true);
			} else {
				t = new Thread(m_singleStepTask);
				t.start();
			}
			break;

		case HackTranslatorEvent.FAST_FORWARD:
			clearMessage();
			t = new Thread(m_fastForwardTask);
			t.start();
			break;

		case HackTranslatorEvent.STOP:
			stop();
			break;

		case HackTranslatorEvent.REWIND:
			clearMessage();
			rewind();
			break;

		case HackTranslatorEvent.FULL_COMPILATION:
			clearMessage();
			t = new Thread(m_fullCompilationTask);
			t.start();
			break;

		}
	}

	/**
	 * Adds the given command to the next position in the program. Throws
	 * HackTranslatorException if the program is too large
	 */
	protected void addCommand(short command) throws HackTranslatorException {
		if (m_destPC >= m_program.length) {
			throw new HackTranslatorException("Program too large");
		}

		m_program[m_destPC++] = command;
		if (m_updateGUI) {
			m_gui.getDestination().addLine(getCodeString(command, m_destPC - 1, true));
		}
	}

	// Checks the given destination file name and throws a
	// HackTranslatorException
	// if not legal.
	private void checkDestinationFile(String fileName) throws HackTranslatorException {
		if (!fileName.endsWith("." + getDestinationExtension())) {
			throw new HackTranslatorException(fileName + " is not a ." + getDestinationExtension() + " file");
		}
	}

	// Checks the given source file name and throws a HackTranslatorException
	// if not legal.
	private void checkSourceFile(String fileName) throws HackTranslatorException {
		if (!fileName.endsWith("." + getSourceExtension())) {
			throw new HackTranslatorException(fileName + " is not a ." + getSourceExtension() + " file");
		}

		File file = new File(fileName);
		if (!file.exists()) {
			throw new HackTranslatorException("file " + fileName + " does not exist");
		}
	}

	// Clears the message display.
	protected void clearMessage() {
		m_gui.displayMessage("", false);
	}

	/**
	 * Compiles the given line and adds the compiled code to the program. If the
	 * line is not legal, throws a HackTranslatorException.
	 */
	protected abstract void compileLine(String line) throws HackTranslatorException;

	/*
	 * Compiles the given line, adds the compiled code to the program and
	 * returns the start and end program index of the new compiled code in a
	 * 2-element array. If the line is a directive (doesn't compile to any
	 * physical code), returns null. If the line is not legal, throws a
	 * HackTranslatorException.
	 */
	protected int[] compileLineAndCount(String line) throws HackTranslatorException {
		int[] result = null;

		int startPC = m_destPC;
		compileLine(line);
		int length = m_destPC - startPC;

		if (length > 0) {
			result = new int[] { startPC, m_destPC - 1 };
		}

		return result;
	}

	/**
	 * Dumps the contents of the translated program into the destination file
	 */
	private void dumpToFile() {
		for (short i = 0; i < m_programSize; i++) {
			m_writer.println(getCodeString(m_program[i], i, false));
		}
		m_writer.close();
	}

	/**
	 * Ends the compilers operation (only rewind or a new source can activate
	 * the compiler after this), with an option to hide the pointers as well.
	 */
	protected void end(boolean hidePointers) {
		m_timer.stop();
		m_gui.disableSingleStep();
		m_gui.disableFastForward();
		m_gui.disableStop();
		m_gui.enableRewind();
		m_gui.disableFullCompilation();
		m_gui.enableLoadSource();

		m_inFastForward = false;

		if (hidePointers) {
			hidePointers();
		}
	}

	/**
	 * Throws a HackTranslatorException with the given message and the current
	 * line number.
	 */
	protected void error(String message) throws HackTranslatorException {
		throw new HackTranslatorException(message, m_sourcePC);
	}

	/**
	 * starts the fast forward mode.
	 */
	protected void fastForward() {
		m_gui.disableSingleStep();
		m_gui.disableFastForward();
		m_gui.enableStop();
		m_gui.disableRewind();
		m_gui.disableFullCompilation();
		m_gui.disableLoadSource();

		m_inFastForward = true;

		m_timer.start();
	}

	/**
	 * Finalizes the compilation process. Executed when a compilation is ended.
	 */
	protected abstract void finalizeCompilation();

	// Translates the whole source. Assumes a legal sourceReader & writer.
	private void fullCompilation() throws HackTranslatorException {

		try {
			m_inFullCompilation = true;
			initCompilation();

			if (m_gui != null) {
				m_gui.disableSingleStep();
				m_gui.disableFastForward();
				m_gui.disableRewind();
				m_gui.disableFullCompilation();
				m_gui.disableLoadSource();

				m_gui.getSource().setContents(m_sourceFileName);
			}

			m_updateGUI = false;

			while (m_sourcePC < m_source.length) {
				int[] compiledRange = compileLineAndCount(m_source[m_sourcePC]);
				if (compiledRange != null) {
					m_compilationMap.put(new Integer(m_sourcePC), compiledRange);
				}

				m_sourcePC++;
			}

			successfulCompilation();
			finalizeCompilation();

			m_programSize = m_destPC;

			if (m_gui != null) {
				showProgram(m_programSize);
				m_gui.getDestination().clearHighlights();
				m_gui.enableRewind();
				m_gui.enableLoadSource();
				m_gui.enableSave();
				m_gui.enableSourceRowSelection();
			}

			m_inFullCompilation = false;

		} catch (HackTranslatorException hte) {
			m_inFullCompilation = false;
			throw new HackTranslatorException(hte.getMessage());
		}
	}

	/**
	 * Returns the string version of the given code in the given program
	 * location. If display is true, the version is for display purposes.
	 * Otherwise, the version should be the final one.
	 */
	protected abstract String getCodeString(short code, int pc, boolean display);

	/**
	 * Returns the extension of the destination file names.
	 */
	protected abstract String getDestinationExtension();

	/**
	 * Returns the name of the translator.
	 */
	protected abstract String getName();

	/**
	 * Returns the translated machine code program array
	 */
	public short[] getProgram() {
		return m_program;
	}

	/**
	 * Returns the extension of the source file names.
	 */
	protected abstract String getSourceExtension();

	/**
	 * Hides all the pointers.
	 */
	protected void hidePointers() {
		m_gui.getSource().clearHighlights();
		m_gui.getDestination().clearHighlights();
		m_gui.getSource().hideSelect();
		m_gui.getDestination().hideSelect();
	}

	/**
	 * initializes the HackTranslator.
	 */
	protected void init(int size, short nullValue) {
		m_program = new short[size];
		for (int i = 0; i < size; i++) {
			m_program[i] = nullValue;
		}
		m_programSize = 0;
	}

	/**
	 * Initializes the compilation process. Executed when a compilation is
	 * started.
	 */
	protected abstract void initCompilation() throws HackTranslatorException;

	/**
	 * Initializes the source file.
	 */
	protected abstract void initSource() throws HackTranslatorException;

	// Loads the given source file and displays it in the Source GUI
	private void loadSource(String fileName) throws HackTranslatorException {
		String line;
		Vector<String> formattedLines = new Vector<String>();
		Vector<String> lines = null;
		String errorMessage = null;

		try {
			if (m_gui != null) {
				m_gui.disableSingleStep();
				m_gui.disableFastForward();
				m_gui.disableStop();
				m_gui.disableRewind();
				m_gui.disableFullCompilation();
				m_gui.disableSave();
				m_gui.disableLoadSource();
				m_gui.disableSourceRowSelection();

				m_gui.displayMessage("Please wait...", false);
			}

			checkSourceFile(fileName);
			m_sourceFileName = fileName;

			lines = new Vector<String>();
			try (BufferedReader sourceReader = new BufferedReader(new FileReader(m_sourceFileName))) {

				while ((line = sourceReader.readLine()) != null) {
					formattedLines.addElement(line);
	
					if (m_gui != null) {
						lines.addElement(line);
					}
				}
			} catch(IOException ioe) {		
				errorMessage = "error reading from file " + m_sourceFileName;
			}

			m_source = new String[formattedLines.size()];
			formattedLines.toArray(m_source);

			if (m_gui != null) {
				String[] linesArray = new String[lines.size()];
				lines.toArray(linesArray);
				m_gui.getSource().setContents(linesArray);
			}

			m_destFileName = m_sourceFileName.substring(0, m_sourceFileName.indexOf('.')) + "." + getDestinationExtension();

			initSource();
			restartCompilation();
			resetProgram();

			if (m_gui != null) {
				m_gui.setDestinationName(m_destFileName);
				m_gui.displayMessage("", false);
			}

		} catch (HackTranslatorException hte) {
			errorMessage = hte.getMessage();
		}

		if (errorMessage != null) {
			if (m_gui != null) {
				m_gui.enableLoadSource();
			}

			throw new HackTranslatorException(errorMessage);
		}
	}

	// Returns the working dir that is saved in the data file, or "" if data
	// file doesn't exist.
	protected File loadWorkingDir() {
		String dir = ".";

		try (BufferedReader r = new BufferedReader(new FileReader("bin/" + getName() + ".dat"))) {
			dir = r.readLine();
		} catch (IOException ioe) {
		}

		return new File(dir);
	}

	/**
	 * Replaces the command in program location pc with the given command.
	 */
	protected void replaceCommand(int pc, short command) {
		m_program[pc] = command;
		if (m_updateGUI) {
			m_gui.getDestination().setLineAt(pc, getCodeString(command, pc, true));
		}
	}

	/**
	 * Resets the program
	 */
	protected void resetProgram() {
		m_programSize = 0;
		if (m_gui != null) {
			m_gui.getDestination().reset();
		}
	}

	/**
	 * Restarts the compilation from the beginning of the source.
	 */
	protected void restartCompilation() {
		m_compilationMap = new Hashtable<Integer, int[]>();
		m_sourcePC = 0;
		m_destPC = 0;

		if (m_gui != null) {
			m_compilationStarted = false;
			m_gui.getDestination().reset();
			hidePointers();

			m_gui.enableSingleStep();
			m_gui.enableFastForward();
			m_gui.disableStop();
			m_gui.enableRewind();
			m_gui.enableFullCompilation();
			m_gui.disableSave();
			m_gui.enableLoadSource();
			m_gui.disableSourceRowSelection();
		}
	}

	/**
	 * Rewinds to the beginning of the compilation.
	 */
	protected void rewind() {
		restartCompilation();
		resetProgram();
	}

	/**
	 * Returns the range in the compilation map that corresponds to the given
	 * rowIndex.
	 */
	protected int[] rowIndexToRange(int rowIndex) {
		Integer key = new Integer(rowIndex);
		return m_compilationMap.get(key);
	}

	/**
	 * Called when a line is selected in the source file.
	 */
	@Override
	public void rowSelected(TextFileEvent event) {
		int index = event.getRowIndex();
		int[] range = rowIndexToRange(index);
		m_gui.getSource().addHighlight(index, true);
		m_gui.getSource().hideSelect();
		if (range != null) {
			m_gui.getDestination().clearHighlights();
			for (int i = range[0]; i <= range[1]; i++) {
				m_gui.getDestination().addHighlight(i, false);
			}
		} else {
			m_gui.getDestination().clearHighlights();
		}
	}

	// Saves the program into the given dest file name.
	private void save() throws HackTranslatorException {
		try {
			m_writer = new PrintWriter(new FileWriter(m_destFileName));
			dumpToFile();
			m_writer.close();
		} catch (IOException ioe) {
			throw new HackTranslatorException("could not create file " + m_destFileName);
		}
	}

	/**
	 * Saves the given working dir into the data file.
	 */
	protected void saveWorkingDir(File file) {
		try (PrintWriter r = new PrintWriter(new FileWriter("bin/" + getName() + ".dat"))) {
			r.println(file.getAbsolutePath());
		} catch (IOException ioe) {
		}

		m_gui.setWorkingDir(file);
	}

	/**
	 * Displayes the first numOfCommands commands from the program in the dest
	 * window.
	 */
	protected void showProgram(int numOfCommands) {
		m_gui.getDestination().reset();

		String[] lines = new String[numOfCommands];

		for (int i = 0; i < numOfCommands; i++) {
			lines[i] = getCodeString(m_program[i], i, true);
		}

		m_gui.getDestination().setContents(lines);
	}

	// Reads a single line from the source, compiles it and writes the result to
	// the
	// detination.
	private void singleStep() {
		m_singleStepLocked = true;

		try {
			initCompilation();

			if (!m_compilationStarted) {
				m_compilationStarted = true;
			}

			m_gui.getSource().addHighlight(m_sourcePC, true);
			m_gui.getDestination().clearHighlights();
			m_updateGUI = true;
			int[] compiledRange = compileLineAndCount(m_source[m_sourcePC]);

			if (compiledRange != null) {
				m_compilationMap.put(new Integer(m_sourcePC), compiledRange);
			}

			m_sourcePC++;

			if (m_sourcePC == m_source.length) {
				successfulCompilation();
				m_programSize = m_destPC;
				m_gui.enableSave();
				m_gui.enableSourceRowSelection();
				end(false);
			}

			finalizeCompilation();

		} catch (HackTranslatorException ae) {
			m_gui.displayMessage(ae.getMessage(), true);
			end(false);
		}

		m_singleStepLocked = false;
	}

	/**
	 * Stops the fast forward mode.
	 */
	protected void stop() {
		m_timer.stop();
		m_gui.enableSingleStep();
		m_gui.enableFastForward();
		m_gui.disableStop();
		m_gui.enableRewind();
		m_gui.enableLoadSource();
		m_gui.enableFullCompilation();

		m_inFastForward = false;
	}

	/**
	 * Executed when a compilation is successful.
	 */
	protected void successfulCompilation() throws HackTranslatorException {
		if (m_gui != null) {
			m_gui.displayMessage("File compilation succeeded", false);
		}
	}
}
