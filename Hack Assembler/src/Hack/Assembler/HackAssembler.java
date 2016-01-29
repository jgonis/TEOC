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

package Hack.Assembler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

import Hack.ComputerParts.TextFileEvent;
import Hack.ComputerParts.TextFileGUI;
import Hack.Translators.HackTranslator;
import Hack.Translators.HackTranslatorEvent;
import Hack.Translators.HackTranslatorException;
import Hack.Utilities.Conversions;
import Hack.Utilities.Definitions;

/**
 * A translator from assmebly (.asm) to hack machine language (.hack)
 */
public class HackAssembler extends HackTranslator {

	// the reader of the comparison file
	private BufferedReader m_comparisonReader;

	// the name of the comparison .hack file
	private String m_comparisonFileName;

	// the symbol table
	private Hashtable<String, Short> m_symbolTable;

	// The comarison program array
	private short[] m_comparisonProgram;

	// The HackAssembler translator;
	private HackAssemblerTranslator m_translator;

	// Index of the next location for unrecognized labels
	private short m_varIndex;

	/**
	 * Constructs a new HackAssembler with the size of the program memory. The
	 * given null value will be used to fill the program initially. A non null
	 * sourceFileName specifies a source file to be loaded. The gui is assumed
	 * to be not null.
	 */
	public HackAssembler(HackAssemblerGUI gui, int size, short nullValue, String sourceFileName)
			throws HackTranslatorException {
		super(gui, size, nullValue, sourceFileName);

		gui.enableLoadComparison();
		gui.hideComparison();
	}

	/**
	 * Constructs a new HackAssembler with the size of the program memory and
	 * .asm source file name. The given null value will be used to fill the
	 * program initially. The compiled program can later be fetched using the
	 * getProgram() method. If save is true, the compiled program will be saved
	 * automatically into a ".hack" file that will have the same name as the
	 * source but with the .hack extension.
	 */
	public HackAssembler(String fileName, int size, short nullValue, boolean save) throws HackTranslatorException {
		super(fileName, size, nullValue, save);
	}

	@Override
	public void actionPerformed(HackTranslatorEvent event) {
		super.actionPerformed(event);

		switch (event.getAction()) {
		case HackTranslatorEvent.SOURCE_LOAD:
			m_comparisonFileName = "";
			m_comparisonReader = null;
			((HackAssemblerGUI) m_gui).setComparisonName("");
			((HackAssemblerGUI) m_gui).hideComparison();
			break;

		case HackAssemblerEvent.COMPARISON_LOAD:
			clearMessage();
			String fileName = (String) event.getData();
			try {
				checkComparisonFile(fileName);
				m_comparisonFileName = fileName;
				saveWorkingDir(new File(fileName));
				resetComparisonFile();
				((HackAssemblerGUI) m_gui).showComparison();
			} catch (HackTranslatorException ae) {
				m_gui.displayMessage(ae.getMessage(), true);
			}
			break;
		}
	}

	// Checks the given comparison file name and throws an AssemblerException
	// if not legal.
	private void checkComparisonFile(String fileName) throws HackTranslatorException {
		if (!fileName.endsWith("." + getDestinationExtension())) {
			throw new HackTranslatorException(fileName + " is not a ." + getDestinationExtension() + " file");
		}

		File file = new File(fileName);
		if (!file.exists()) {
			throw new HackTranslatorException("File " + fileName + " does not exist");
		}
	}

	// Compares the given commands to the next commands in the comparison file.
	private boolean compare(int[] compiledRange) {
		boolean result = true;
		int length = (compiledRange[1] - compiledRange[0]) + 1;

		for (int i = 0; (i < length) && result; i++) {
			result = (m_program[compiledRange[0] + i] == m_comparisonProgram[compiledRange[0] + i]);
		}

		return result;
	}

	// If the line is a label, returns null.
	@Override
	protected void compileLine(String line) throws HackTranslatorException {

		try {
			AssemblyLineTokenizer input = new AssemblyLineTokenizer(line);

			if (!input.isEnd() && !input.isToken("(")) {
				if (input.isToken("@")) {
					input.advance(true);
					boolean numeric = true;
					String label = input.token();
					input.ensureEnd();
					try {
						Short.parseShort(label);
					} catch (NumberFormatException nfe) {
						numeric = false;
					}

					if (!numeric) {
						Short address = m_symbolTable.get(label);
						if (address == null) {
							address = new Short(m_varIndex++);
							m_symbolTable.put(label, address);
						}

						addCommand(m_translator.textToCode("@" + address.shortValue()));
					} else {
						addCommand(m_translator.textToCode(line));
					}
				} else { // try to compile normaly, if error - try to compile as
							// compact assembly
					try {
						addCommand(m_translator.textToCode(line));
					} catch (AssemblerException ae) {
						int openAddressPos = line.indexOf("[");
						if (openAddressPos >= 0) {
							int lastPos = line.lastIndexOf("[");
							int closeAddressPos = line.indexOf("]");

							if ((openAddressPos != lastPos) || (openAddressPos > closeAddressPos)
									|| ((openAddressPos + 1) == closeAddressPos)) {
								throw new AssemblerException("Illegal use of the [] notation");
							}

							String address = line.substring(openAddressPos + 1, closeAddressPos);
							compileLine("@" + address);
							compileLine(line.substring(0, openAddressPos).concat(line.substring(closeAddressPos + 1)));
						} else {
							throw new AssemblerException(ae.getMessage());
						}
					}
				}
			}
		} catch (IOException ioe) {
			throw new HackTranslatorException("Error reading from file " + m_sourceFileName);
		} catch (AssemblerException ae) {
			throw new HackTranslatorException(ae.getMessage(), m_sourcePC);
		}
	}

	@Override
	protected int[] compileLineAndCount(String line) throws HackTranslatorException {
		int[] compiledRange = super.compileLineAndCount(line);

		// check comparison
		if ((compiledRange != null) && (m_comparisonReader != null)) {
			int length = (compiledRange[1] - compiledRange[0]) + 1;
			boolean compare = compare(compiledRange);

			if (m_inFullCompilation) {
				if (!compare) {
					if (m_gui != null) {
						m_programSize = (m_destPC + length) - 1;
						showProgram(m_programSize);
						m_gui.getSource().addHighlight(m_sourcePC, true);
						m_gui.getDestination().addHighlight(m_destPC - 1, true);
						((HackAssemblerGUI) m_gui).getComparison().addHighlight(m_destPC - 1, true);
						m_gui.enableRewind();
						m_gui.enableLoadSource();
					}
				}
			} else {
				if (compare) {
					((HackAssemblerGUI) m_gui).getComparison().addHighlight((m_destPC + length) - 2, true);
				} else {
					m_gui.getDestination().addHighlight(m_destPC - 1, true);
					((HackAssemblerGUI) m_gui).getComparison().addHighlight(m_destPC - 1, true);
				}
			}

			if (!compare) {
				throw new HackTranslatorException("Comparison failure");
			}
		}

		return compiledRange;
	}

	@Override
	protected void end(boolean hidePointers) {
		super.end(hidePointers);
		((HackAssemblerGUI) m_gui).disableLoadComparison();
	}

	@Override
	protected void fastForward() {
		((HackAssemblerGUI) m_gui).disableLoadComparison();
		super.fastForward();
	}

	@Override
	protected void finalizeCompilation() {
	}

	// Generates The symbol table by attaching each label with it's appropriate
	// value according to it's location in the program
	private void generateSymbolTable() throws HackTranslatorException {
		m_symbolTable = Definitions.getInstance().getAddressesTable();
		short pc = 0;
		String line;
		String label;

		try (BufferedReader sourceReader = new BufferedReader(new FileReader(m_sourceFileName))) {
			
			while ((line = sourceReader.readLine()) != null) {

				AssemblyLineTokenizer input = new AssemblyLineTokenizer(line);

				if (!input.isEnd()) {
					if (input.isToken("(")) {
						input.advance(true);
						label = input.token();
						input.advance(true);
						if (!input.isToken(")")) {
							error("')' expected");
						}

						input.ensureEnd();

						m_symbolTable.put(label, new Short(pc));
					} else if (input.contains("[")) {
						pc += 2;
					} else {
						pc++;
					}
				}
			}
		} catch (IOException ioe) {
			throw new HackTranslatorException("Error reading from file " + m_sourceFileName);
		}
	}

	@Override
	protected String getCodeString(short code, int pc, boolean display) {
		return Conversions.decimalToBinary(code, 16);
	}

	@Override
	protected String getDestinationExtension() {
		return "hack";
	}

	@Override
	protected String getName() {
		return "Assembler";
	}

	@Override
	protected String getSourceExtension() {
		return "asm";
	}

	@Override
	protected void hidePointers() {
		super.hidePointers();

		if (m_comparisonReader != null) {
			((HackAssemblerGUI) m_gui).getComparison().clearHighlights();
		}
	}

	@Override
	protected void init(int size, short nullValue) {
		super.init(size, nullValue);
		m_translator = HackAssemblerTranslator.getInstance();
	}

	@Override
	protected void initCompilation() throws HackTranslatorException {
		if ((m_gui != null) && (m_inFullCompilation || !m_compilationStarted)) {
			((HackAssemblerGUI) m_gui).disableLoadComparison();
		}
	}

	@Override
	protected void initSource() throws HackTranslatorException {
		generateSymbolTable();
	}

	// opens the comparison file for reading.
	private void resetComparisonFile() throws HackTranslatorException {
		try {
			m_comparisonReader = new BufferedReader(new FileReader(m_comparisonFileName));

			if (m_gui != null) {
				TextFileGUI comp = ((HackAssemblerGUI) m_gui).getComparison();
				comp.reset();
				comp.setContents(m_comparisonFileName);

				m_comparisonProgram = new short[comp.getNumberOfLines()];
				for (int i = 0; i < comp.getNumberOfLines(); i++) {
					if (comp.getLineAt(i).length() != Definitions.BITS_PER_WORD) {
						throw new HackTranslatorException("Error in file " + m_comparisonFileName + ": Line " + i
								+ " does not contain exactly " + Definitions.BITS_PER_WORD + " characters");
					}
					try {
						m_comparisonProgram[i] = (short) Conversions.binaryToInt(comp.getLineAt(i));
					} catch (NumberFormatException nfe) {
						throw new HackTranslatorException("Error in file " + m_comparisonFileName + ": Line " + i
								+ " does not contain only 1/0 characters");
					}
				}
			}
		} catch (IOException ioe) {
			throw new HackTranslatorException("Error reading from file " + m_comparisonFileName);
		}
	}

	@Override
	protected void restartCompilation() {
		super.restartCompilation();

		m_varIndex = Definitions.VAR_START_ADDRESS;

		if (m_gui != null) {
			((HackAssemblerGUI) m_gui).enableLoadComparison();
		}
	}

	@Override
	protected void rewind() {
		super.rewind();

		if (m_comparisonReader != null) {
			((HackAssemblerGUI) m_gui).getComparison().clearHighlights();
			((HackAssemblerGUI) m_gui).getComparison().hideSelect();
		}
	}

	@Override
	public void rowSelected(TextFileEvent event) {
		super.rowSelected(event);

		int[] range = rowIndexToRange(event.getRowIndex());
		if (range != null) {
			if (m_comparisonReader != null) {
				((HackAssemblerGUI) m_gui).getComparison().select(range[0], range[1]);
			}
		} else {
			if (m_comparisonReader != null) {
				((HackAssemblerGUI) m_gui).getComparison().hideSelect();
			}
		}
	}

	@Override
	protected void stop() {
		super.stop();
		((HackAssemblerGUI) m_gui).disableLoadComparison();
	}

	@Override
	protected void successfulCompilation() throws HackTranslatorException {
		if (m_comparisonReader == null) {
			super.successfulCompilation();
		} else {
			if (m_gui != null) {
				((HackAssemblerGUI) m_gui).displayMessage("File compilation & comparison succeeded", false);
			}
		}
	}
}
