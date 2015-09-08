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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;

import Hack.Controller.ControllerEvent;
import Hack.Controller.ControllerEventListener;
import Hack.Controller.ControllerGUI;
import Hack.Controller.HackController;
import Hack.Controller.HackSimulatorGUI;

/**
 * This class represents the GUI of the controller component.
 */
public class ControllerComponent extends JFrame
		implements ControllerGUI, FilesTypeListener, BreakpointsChangedListener {

	// The dimensions of the tool bar.
	protected static final int TOOLBAR_WIDTH = 1016;
	protected static final int TOOLBAR_HEIGHT = 55;

	// The dimensions of this window.
	private static final int CONTROLLER_WIDTH = 1024;
	private static final int CONTROLLER_HEIGHT = 741;

	// The dimensions of the toolbar's separator.
	protected static final Dimension separatorDimension = new Dimension(3, TOOLBAR_HEIGHT - 5);

	// The vector of listeners to this component.
	private Vector listeners;

	// The fast forward button.
	protected MouseOverJButton ffwdButton;

	// The stop button.
	protected MouseOverJButton stopButton;

	// The rewind button.
	protected MouseOverJButton rewindButton;

	// The load script button.
	protected MouseOverJButton scriptButton;

	// The breakpoints button.
	protected MouseOverJButton breakButton;

	// The single step button.
	protected MouseOverJButton singleStepButton;

	// The load program button.
	protected MouseOverJButton loadProgramButton;

	// Creating the file chooser window & the breakpoint window.
	private JFileChooser fileChooser = new JFileChooser();
	private BreakpointWindow breakpointWindow = new BreakpointWindow();

	// Creating the icons for the buttons.
	private ImageIcon rewindIcon = new ImageIcon(Utilities.imagesDir + "vcrrewind.gif");
	private ImageIcon ffwdIcon = new ImageIcon(Utilities.imagesDir + "vcrfastforward.gif");
	private ImageIcon singleStepIcon = new ImageIcon(Utilities.imagesDir + "vcrforward.gif");
	private ImageIcon stopIcon = new ImageIcon(Utilities.imagesDir + "vcrstop.gif");
	private ImageIcon breakIcon = new ImageIcon(Utilities.imagesDir + "redflag.gif");
	private ImageIcon loadProgramIcon = new ImageIcon(Utilities.imagesDir + "opendoc.gif");
	private ImageIcon scriptIcon = new ImageIcon(Utilities.imagesDir + "scroll.gif");

	// The speed slider.
	protected JSlider speedSlider;

	// A combo box which controls the format of all the components.
	protected TitledComboBox formatCombo;

	// A combo box for choosing the additional disply.
	protected TitledComboBox additionalDisplayCombo;

	// A combo box for choosing the animation type.
	protected TitledComboBox animationCombo;

	// The toolbar of the controller.
	protected JToolBar toolBar;

	// The components of the menu
	protected JMenuBar menuBar;
	protected JMenu fileMenu, viewMenu, runMenu, helpMenu;
	protected JMenuItem singleStepMenuItem, ffwdMenuItem, stopMenuItem, rewindMenuItem, exitMenuItem;
	protected JMenuItem usageMenuItem, aboutMenuItem;
	protected JMenu animationSubMenu, numericFormatSubMenu, additionalDisplaySubMenu;
	protected JMenuItem breakpointsMenuItem, scriptMenuItem, programMenuItem;
	protected JRadioButtonMenuItem decMenuItem, hexaMenuItem, binMenuItem;
	protected JRadioButtonMenuItem scriptDisplayMenuItem, outputMenuItem, compareMenuItem, noAdditionalDisplayMenuItem;
	protected JRadioButtonMenuItem partAnimMenuItem, fullAnimMenuItem, noAnimMenuItem;

	// the message label (status line)
	protected JLabel messageLbl = new JLabel();

	// component for displaying the script, output file and comparison file.
	protected FileDisplayComponent scriptComponent;
	protected FileDisplayComponent outputComponent;
	protected FileDisplayComponent comparisonComponent;

	// HTML viewers for the usage and about windows.
	private HTMLViewFrame usageWindow, aboutWindow;

	/**
	 * Constructs a new ControllerComponent.
	 */
	public ControllerComponent() {
		listeners = new Vector();
		formatCombo = new TitledComboBox("Format:", "Numeric display format",
				new String[] { "Decimal", "Hexa", "Binary" }, 75);
		additionalDisplayCombo = new TitledComboBox("View:", "View options",
				new String[] { "Script", "Output", "Compare", "Screen" }, 80);
		animationCombo = new TitledComboBox("Animate:", "Animtion type",
				new String[] { "Program flow", "Program & data flow", "No animation" }, 135);
		scriptComponent = new FileDisplayComponent();
		outputComponent = new FileDisplayComponent();
		comparisonComponent = new FileDisplayComponent();

		init();
		jbInit();
	}

	/**
	 * Called when the about window menu item was selected.
	 */
	public void aboutMenuItem_actionPerformed(ActionEvent e) {
		if (aboutWindow != null) {
			aboutWindow.setVisible(true);
		}
	}

	/**
	 * Registers the given ControllerEventListener as a listener to this GUI.
	 */
	@Override
	public void addControllerListener(ControllerEventListener listener) {
		listeners.addElement(listener);
	}

	/**
	 * Called when a choice was made in the additional display combo box.
	 */
	public void additionalDisplayCombo_actionPerformed(ActionEvent e) {
		int selectedIndex = additionalDisplayCombo.getSelectedIndex();
		switch (selectedIndex) {
		case HackController.SCRIPT_ADDITIONAL_DISPLAY:
			if (!scriptMenuItem.isSelected()) {
				scriptMenuItem.setSelected(true);
			}
			break;

		case HackController.OUTPUT_ADDITIONAL_DISPLAY:
			if (!outputMenuItem.isSelected()) {
				outputMenuItem.setSelected(true);
			}
			break;

		case HackController.COMPARISON_ADDITIONAL_DISPLAY:
			if (!compareMenuItem.isSelected()) {
				compareMenuItem.setSelected(true);
			}
			break;

		case HackController.NO_ADDITIONAL_DISPLAY:
			if (!noAdditionalDisplayMenuItem.isSelected()) {
				noAdditionalDisplayMenuItem.setSelected(true);
			}
			break;
		}

		notifyControllerListeners(ControllerEvent.ADDITIONAL_DISPLAY_CHANGE, new Integer(selectedIndex));
	}

	/**
	 * Called when a choice was made in the animation type combo box.
	 */
	public void animationCombo_actionPerformed(ActionEvent e) {
		int selectedIndex = animationCombo.getSelectedIndex();
		switch (selectedIndex) {
		case HackController.DISPLAY_CHANGES:
			if (!partAnimMenuItem.isSelected()) {
				partAnimMenuItem.setSelected(true);
			}
			break;

		case HackController.ANIMATION:
			if (!fullAnimMenuItem.isSelected()) {
				fullAnimMenuItem.setSelected(true);
			}
			break;

		case HackController.NO_DISPLAY_CHANGES:
			if (!noAnimMenuItem.isSelected()) {
				noAnimMenuItem.setSelected(true);
			}
			break;
		}

		notifyControllerListeners(ControllerEvent.ANIMATION_MODE_CHANGE, new Integer(selectedIndex));
	}

	/**
	 * Adds the menu items to the menuber.
	 */
	protected void arrangeMenu() {

		// Build the first menu.
		fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(fileMenu);

		viewMenu = new JMenu("View");
		viewMenu.setMnemonic(KeyEvent.VK_V);
		menuBar.add(viewMenu);

		runMenu = new JMenu("Run");
		runMenu.setMnemonic(KeyEvent.VK_R);
		menuBar.add(runMenu);

		// Build the second menu.
		helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);
		menuBar.add(helpMenu);

		programMenuItem = new JMenuItem("Load Program", KeyEvent.VK_O);
		programMenuItem.addActionListener(e -> programMenuItem_actionPerformed(e));
		fileMenu.add(programMenuItem);

		scriptMenuItem = new JMenuItem("Load Script", KeyEvent.VK_P);
		scriptMenuItem.addActionListener(e -> scriptMenuItem_actionPerformed(e));
		fileMenu.add(scriptMenuItem);
		fileMenu.addSeparator();

		exitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
		exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.ALT_MASK));
		exitMenuItem.addActionListener(e -> exitMenuItem_actionPerformed(e));
		fileMenu.add(exitMenuItem);

		viewMenu.addSeparator();

		ButtonGroup animationRadioButtons = new ButtonGroup();

		animationSubMenu = new JMenu("Animate");
		animationSubMenu.setMnemonic(KeyEvent.VK_A);
		viewMenu.add(animationSubMenu);

		partAnimMenuItem = new JRadioButtonMenuItem("Program flow");
		partAnimMenuItem.setMnemonic(KeyEvent.VK_P);
		partAnimMenuItem.setSelected(true);
		partAnimMenuItem.addActionListener(e -> partAnimMenuItem_actionPerformed(e));
		animationRadioButtons.add(partAnimMenuItem);
		animationSubMenu.add(partAnimMenuItem);

		fullAnimMenuItem = new JRadioButtonMenuItem("Program & data flow");
		fullAnimMenuItem.setMnemonic(KeyEvent.VK_D);
		fullAnimMenuItem.addActionListener(e -> fullAnimMenuItem_actionPerformed(e));
		animationRadioButtons.add(fullAnimMenuItem);
		animationSubMenu.add(fullAnimMenuItem);

		noAnimMenuItem = new JRadioButtonMenuItem("No Animation");
		noAnimMenuItem.setMnemonic(KeyEvent.VK_N);
		noAnimMenuItem.addActionListener(e -> noAnimMenuItem_actionPerformed(e));
		animationRadioButtons.add(noAnimMenuItem);
		animationSubMenu.add(noAnimMenuItem);

		ButtonGroup additionalDisplayRadioButtons = new ButtonGroup();

		additionalDisplaySubMenu = new JMenu("View");
		additionalDisplaySubMenu.setMnemonic(KeyEvent.VK_V);
		viewMenu.add(additionalDisplaySubMenu);

		scriptDisplayMenuItem = new JRadioButtonMenuItem("Script");
		scriptDisplayMenuItem.setMnemonic(KeyEvent.VK_S);
		scriptDisplayMenuItem.setSelected(true);
		scriptDisplayMenuItem.addActionListener(e -> scriptDisplayMenuItem_actionPerformed(e));
		additionalDisplayRadioButtons.add(scriptDisplayMenuItem);
		additionalDisplaySubMenu.add(scriptDisplayMenuItem);

		outputMenuItem = new JRadioButtonMenuItem("Output");
		outputMenuItem.setMnemonic(KeyEvent.VK_O);
		outputMenuItem.addActionListener(e -> outputMenuItem_actionPerformed(e));
		additionalDisplayRadioButtons.add(outputMenuItem);
		additionalDisplaySubMenu.add(outputMenuItem);

		compareMenuItem = new JRadioButtonMenuItem("Compare");
		compareMenuItem.setMnemonic(KeyEvent.VK_C);
		compareMenuItem.addActionListener(e -> compareMenuItem_actionPerformed(e));
		additionalDisplayRadioButtons.add(compareMenuItem);
		additionalDisplaySubMenu.add(compareMenuItem);

		noAdditionalDisplayMenuItem = new JRadioButtonMenuItem("Screen");
		noAdditionalDisplayMenuItem.setMnemonic(KeyEvent.VK_N);
		noAdditionalDisplayMenuItem.addActionListener(e -> noAdditionalDisplayMenuItem_actionPerformed(e));
		additionalDisplayRadioButtons.add(noAdditionalDisplayMenuItem);
		additionalDisplaySubMenu.add(noAdditionalDisplayMenuItem);

		ButtonGroup formatRadioButtons = new ButtonGroup();

		numericFormatSubMenu = new JMenu("Format");
		numericFormatSubMenu.setMnemonic(KeyEvent.VK_F);
		viewMenu.add(numericFormatSubMenu);

		decMenuItem = new JRadioButtonMenuItem("Decimal");
		decMenuItem.setMnemonic(KeyEvent.VK_D);
		decMenuItem.setSelected(true);
		decMenuItem.addActionListener(e -> decMenuItem_actionPerformed(e));
		formatRadioButtons.add(decMenuItem);
		numericFormatSubMenu.add(decMenuItem);

		hexaMenuItem = new JRadioButtonMenuItem("Hexadecimal");
		hexaMenuItem.setMnemonic(KeyEvent.VK_H);
		hexaMenuItem.addActionListener(e -> hexaMenuItem_actionPerformed(e));
		formatRadioButtons.add(hexaMenuItem);
		numericFormatSubMenu.add(hexaMenuItem);

		binMenuItem = new JRadioButtonMenuItem("Binary");
		binMenuItem.setMnemonic(KeyEvent.VK_B);
		binMenuItem.addActionListener(e -> binMenuItem_actionPerformed(e));
		formatRadioButtons.add(binMenuItem);
		numericFormatSubMenu.add(binMenuItem);

		viewMenu.addSeparator();

		singleStepMenuItem = new JMenuItem("Single Step", KeyEvent.VK_S);
		singleStepMenuItem.setAccelerator(KeyStroke.getKeyStroke("F11"));
		singleStepMenuItem.addActionListener(e -> singleStepMenuItem_actionPerformed(e));
		runMenu.add(singleStepMenuItem);

		ffwdMenuItem = new JMenuItem("Run", KeyEvent.VK_F);
		ffwdMenuItem.setAccelerator(KeyStroke.getKeyStroke("F5"));
		ffwdMenuItem.addActionListener(e -> ffwdMenuItem_actionPerformed(e));
		runMenu.add(ffwdMenuItem);

		stopMenuItem = new JMenuItem("Stop", KeyEvent.VK_T);
		stopMenuItem.setAccelerator(KeyStroke.getKeyStroke("shift F5"));
		stopMenuItem.addActionListener(e -> stopMenuItem_actionPerformed(e));
		runMenu.add(stopMenuItem);

		rewindMenuItem = new JMenuItem("Reset", KeyEvent.VK_R);
		rewindMenuItem.addActionListener(e -> rewindMenuItem_actionPerformed(e));
		runMenu.add(rewindMenuItem);

		runMenu.addSeparator();

		breakpointsMenuItem = new JMenuItem("Breakpoints", KeyEvent.VK_B);
		breakpointsMenuItem.addActionListener(e -> breakpointsMenuItem_actionPerformed(e));
		runMenu.add(breakpointsMenuItem);

		usageMenuItem = new JMenuItem("Usage", KeyEvent.VK_U);
		usageMenuItem.setAccelerator(KeyStroke.getKeyStroke("F1"));
		usageMenuItem.addActionListener(e -> usageMenuItem_actionPerformed(e));
		helpMenu.add(usageMenuItem);

		aboutMenuItem = new JMenuItem("About ...", KeyEvent.VK_A);
		aboutMenuItem.addActionListener(e -> aboutMenuItem_actionPerformed(e));
		helpMenu.add(aboutMenuItem);

	}

	/**
	 * Adds the controls to the toolbar.
	 */
	protected void arrangeToolBar() {
		toolBar.add(loadProgramButton);
		toolBar.addSeparator(separatorDimension);
		toolBar.add(singleStepButton);
		toolBar.add(ffwdButton);
		toolBar.add(stopButton);
		toolBar.add(rewindButton);
		toolBar.addSeparator(separatorDimension);
		toolBar.add(scriptButton);
		toolBar.add(breakButton);
		toolBar.addSeparator(separatorDimension);
		toolBar.add(speedSlider);
		toolBar.add(animationCombo);
		toolBar.add(additionalDisplayCombo);
		toolBar.add(formatCombo);
	}

	/**
	 * Called when the numeric format's "binary" menu item was selected.
	 */
	public void binMenuItem_actionPerformed(ActionEvent e) {
		if (!formatCombo.isSelectedIndex(HackController.BINARY_FORMAT)) {
			formatCombo.setSelectedIndex(HackController.BINARY_FORMAT);
		}
	}

	/**
	 * Called when the breakpoints button was pressed.
	 */
	public void breakButton_actionPerformed(ActionEvent e) {
		showBreakpoints();
	}

	/**
	 * Called when there was a change in the breakpoints vector. The event
	 * contains the vector of breakpoints.
	 */
	@Override
	public void breakpointsChanged(BreakpointsChangedEvent event) {
		notifyControllerListeners(ControllerEvent.BREAKPOINTS_CHANGE, event.getBreakpoints());
	}

	/**
	 * Called when the breakpoints menu item was selected.
	 */
	public void breakpointsMenuItem_actionPerformed(ActionEvent e) {
		showBreakpoints();
	}

	/**
	 * Called when the additonal display's "comparison" menu item was selected.
	 */
	public void compareMenuItem_actionPerformed(ActionEvent e) {
		if (!additionalDisplayCombo.isSelectedIndex(HackController.COMPARISON_ADDITIONAL_DISPLAY)) {
			additionalDisplayCombo.setSelectedIndex(HackController.COMPARISON_ADDITIONAL_DISPLAY);
		}
	}

	/**
	 * Called when the numeric format's "decimal" menu item was selected.
	 */
	public void decMenuItem_actionPerformed(ActionEvent e) {
		if (!formatCombo.isSelectedIndex(HackController.DECIMAL_FORMAT)) {
			formatCombo.setSelectedIndex(HackController.DECIMAL_FORMAT);
		}
	}

	/**
	 * Disables the animation mode buttons.
	 */
	@Override
	public void disableAnimationModes() {
		animationCombo.setEnabled(false);
		partAnimMenuItem.setEnabled(false);
		fullAnimMenuItem.setEnabled(false);
		noAnimMenuItem.setEnabled(false);
	}

	/**
	 * Disables the fast forward action.
	 */
	@Override
	public void disableFastForward() {
		ffwdButton.setEnabled(false);
		ffwdMenuItem.setEnabled(false);
	}

	/**
	 * Disables the load program action.
	 */
	@Override
	public void disableLoadProgram() {
		loadProgramButton.setEnabled(false);
	}

	/**
	 * Disables the rewind action.
	 */
	@Override
	public void disableRewind() {
		rewindButton.setEnabled(false);
		rewindMenuItem.setEnabled(false);
	}

	/**
	 * Disables the eject action.
	 */
	@Override
	public void disableScript() {
		scriptButton.setEnabled(false);
		scriptMenuItem.setEnabled(false);
	}

	/**
	 * Disables the single step action.
	 */
	@Override
	public void disableSingleStep() {
		singleStepButton.setEnabled(false);
		singleStepMenuItem.setEnabled(false);
	}

	/**
	 * Disables the speed slider.
	 */
	@Override
	public void disableSpeedSlider() {
		speedSlider.setEnabled(false);
	}

	/**
	 * Disables the stop action.
	 */
	@Override
	public void disableStop() {
		stopButton.setEnabled(false);
		stopMenuItem.setEnabled(false);
	}

	@Override
	public void displayMessage(String message, boolean error) {
		if (error) {
			messageLbl.setForeground(Color.red);
		} else {
			messageLbl.setForeground(UIManager.getColor("Label.foreground"));
		}
		messageLbl.setText(message);
		messageLbl.setToolTipText(message);
	}

	/**
	 * Enables the animation mode buttons.
	 */
	@Override
	public void enableAnimationModes() {
		animationCombo.setEnabled(true);
		partAnimMenuItem.setEnabled(true);
		fullAnimMenuItem.setEnabled(true);
		noAnimMenuItem.setEnabled(true);
	}

	/**
	 * Enables the fast forward action.
	 */
	@Override
	public void enableFastForward() {
		ffwdButton.setEnabled(true);
		ffwdMenuItem.setEnabled(true);
	}

	/**
	 * Enables the load program action.
	 */
	@Override
	public void enableLoadProgram() {
		loadProgramButton.setEnabled(true);
	}

	/**
	 * Enables the rewind action.
	 */
	@Override
	public void enableRewind() {
		rewindButton.setEnabled(true);
		rewindMenuItem.setEnabled(true);
	}

	/**
	 * Enables the eject action.
	 */
	@Override
	public void enableScript() {
		scriptButton.setEnabled(true);
		scriptMenuItem.setEnabled(true);
	}

	/**
	 * Enables the single step action.
	 */
	@Override
	public void enableSingleStep() {
		singleStepButton.setEnabled(true);
		singleStepMenuItem.setEnabled(true);
	}

	/**
	 * Enables the speed slider.
	 */
	@Override
	public void enableSpeedSlider() {
		speedSlider.setEnabled(true);
	}

	/**
	 * Enables the stop action.
	 */
	@Override
	public void enableStop() {
		stopButton.setEnabled(true);
		stopMenuItem.setEnabled(true);
	}

	/**
	 * Called when the exit menu item was selected.
	 */
	public void exitMenuItem_actionPerformed(ActionEvent e) {
		System.exit(0);
	}

	/**
	 * Called when the fast forward button was pressed.
	 */
	public void ffwdButton_actionPerformed(ActionEvent e) {
		notifyControllerListeners(ControllerEvent.FAST_FORWARD, null);
	}

	/**
	 * Called when the fast forward menu item was selected.
	 */
	public void ffwdMenuItem_actionPerformed(ActionEvent e) {
		notifyControllerListeners(ControllerEvent.FAST_FORWARD, null);
	}

	/**
	 * Called when the names of the files were changed. The event contains the
	 * three strings representing the names of the files.
	 */
	@Override
	public void filesNamesChanged(FilesTypeEvent event) {
		if (event.getFirstFile() != null) {
			scriptComponent.setContents(event.getFirstFile());
			notifyControllerListeners(ControllerEvent.SCRIPT_CHANGE, event.getFirstFile());
		}
		if (event.getSecondFile() != null) {
			outputComponent.setContents(event.getSecondFile());
		}
		if (event.getThirdFile() != null) {
			comparisonComponent.setContents(event.getThirdFile());
		}
	}

	/**
	 * Called when a choice was made in the numeric format combo box.
	 */
	public void formatCombo_actionPerformed(ActionEvent e) {
		int selectedIndex = formatCombo.getSelectedIndex();
		switch (selectedIndex) {
		case HackController.DECIMAL_FORMAT:
			if (!decMenuItem.isSelected()) {
				decMenuItem.setSelected(true);
			}
			break;

		case HackController.HEXA_FORMAT:
			if (!hexaMenuItem.isSelected()) {
				hexaMenuItem.setSelected(true);
			}
			break;

		case HackController.BINARY_FORMAT:
			if (!binMenuItem.isSelected()) {
				binMenuItem.setSelected(true);
			}
			break;
		}

		notifyControllerListeners(ControllerEvent.NUMERIC_FORMAT_CHANGE, new Integer(selectedIndex));
	}

	/**
	 * Called when the animation's "animate" menu item was selected.
	 */
	public void fullAnimMenuItem_actionPerformed(ActionEvent e) {
		if (!animationCombo.isSelectedIndex(HackController.ANIMATION)) {
			animationCombo.setSelectedIndex(HackController.ANIMATION);
		}
	}

	@Override
	public JComponent getComparisonComponent() {
		return comparisonComponent;
	}

	@Override
	public JComponent getOutputComponent() {
		return outputComponent;
	}

	@Override
	public JComponent getScriptComponent() {
		return scriptComponent;
	}

	/**
	 * Called when the numeric format's "hexa" menu item was selected.
	 */
	public void hexaMenuItem_actionPerformed(ActionEvent e) {
		if (!formatCombo.isSelectedIndex(HackController.HEXA_FORMAT)) {
			formatCombo.setSelectedIndex(HackController.HEXA_FORMAT);
		}
	}

	/**
	 * Hides the controller.
	 */
	public void hideController() {
		setVisible(false);
	}

	// Initializes the buttons and speed slider
	protected void init() {
		speedSlider = new JSlider(SwingConstants.HORIZONTAL, 1, HackController.NUMBER_OF_SPEED_UNITS, 1);
		loadProgramButton = new MouseOverJButton();
		ffwdButton = new MouseOverJButton();
		stopButton = new MouseOverJButton();
		rewindButton = new MouseOverJButton();
		scriptButton = new MouseOverJButton();
		breakButton = new MouseOverJButton();
		singleStepButton = new MouseOverJButton();
	}

	// Initializes this component.
	private void jbInit() {
		fileChooser.setFileFilter(new ScriptFileFilter());
		this.getContentPane().setLayout(null);

		Hashtable labelTable = new Hashtable();

		JLabel slowLabel = new JLabel("Slow");
		slowLabel.setFont(Utilities.thinLabelsFont);
		JLabel fastLabel = new JLabel("Fast");
		fastLabel.setFont(Utilities.thinLabelsFont);
		labelTable.put(new Integer(1), slowLabel);
		labelTable.put(new Integer(5), fastLabel);

		speedSlider.addChangeListener(e -> SpeedSlider_stateChanged(e));
		speedSlider.setLabelTable(labelTable);
		speedSlider.setMajorTickSpacing(1);
		speedSlider.setPaintTicks(true);
		speedSlider.setPaintLabels(true);
		speedSlider.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		speedSlider.setPreferredSize(new Dimension(95, 50));
		speedSlider.setMinimumSize(new Dimension(95, 50));
		speedSlider.setToolTipText("Speed");
		speedSlider.setMaximumSize(new Dimension(95, 50));

		loadProgramButton.addActionListener(e -> loadProgramButton_actionPerformed(e));
		loadProgramButton.setMaximumSize(new Dimension(39, 39));
		loadProgramButton.setMinimumSize(new Dimension(39, 39));
		loadProgramButton.setPreferredSize(new Dimension(39, 39));
		loadProgramButton.setSize(new Dimension(39, 39));
		loadProgramButton.setToolTipText("Load Program");
		loadProgramButton.setIcon(loadProgramIcon);

		ffwdButton.setMaximumSize(new Dimension(39, 39));
		ffwdButton.setMinimumSize(new Dimension(39, 39));
		ffwdButton.setPreferredSize(new Dimension(39, 39));
		ffwdButton.setToolTipText("Run");
		ffwdButton.setIcon(ffwdIcon);
		ffwdButton.addActionListener(e -> ffwdButton_actionPerformed(e));

		stopButton.addActionListener(e -> stopButton_actionPerformed(e));
		stopButton.setMaximumSize(new Dimension(39, 39));
		stopButton.setMinimumSize(new Dimension(39, 39));
		stopButton.setPreferredSize(new Dimension(39, 39));
		stopButton.setToolTipText("Stop");
		stopButton.setIcon(stopIcon);

		rewindButton.setMaximumSize(new Dimension(39, 39));
		rewindButton.setMinimumSize(new Dimension(39, 39));
		rewindButton.setPreferredSize(new Dimension(39, 39));
		rewindButton.setToolTipText("Reset");
		rewindButton.setIcon(rewindIcon);
		rewindButton.addActionListener(e -> rewindButton_actionPerformed(e));

		scriptButton.setMaximumSize(new Dimension(39, 39));
		scriptButton.setMinimumSize(new Dimension(39, 39));
		scriptButton.setPreferredSize(new Dimension(39, 39));
		scriptButton.setToolTipText("Load Script");
		scriptButton.setIcon(scriptIcon);
		scriptButton.addActionListener(e -> scriptButton_actionPerformed(e));

		breakButton.addActionListener(e -> breakButton_actionPerformed(e));
		breakButton.setMaximumSize(new Dimension(39, 39));
		breakButton.setMinimumSize(new Dimension(39, 39));
		breakButton.setPreferredSize(new Dimension(39, 39));
		breakButton.setToolTipText("Open breakpoint panel");
		breakButton.setIcon(breakIcon);

		breakpointWindow.addBreakpointListener(this);

		singleStepButton.addActionListener(e -> singleStepButton_actionPerformed(e));
		singleStepButton.setMaximumSize(new Dimension(39, 39));
		singleStepButton.setMinimumSize(new Dimension(39, 39));
		singleStepButton.setPreferredSize(new Dimension(39, 39));
		singleStepButton.setSize(new Dimension(39, 39));
		singleStepButton.setToolTipText("Single Step");
		singleStepButton.setIcon(singleStepIcon);

		animationCombo.addActionListener(e -> animationCombo_actionPerformed(e));

		formatCombo.addActionListener(e -> formatCombo_actionPerformed(e));

		additionalDisplayCombo.addActionListener(e -> additionalDisplayCombo_actionPerformed(e));

		messageLbl.setFont(Utilities.statusLineFont);
		messageLbl.setBorder(BorderFactory.createLoweredBevelBorder());
		messageLbl.setBounds(new Rectangle(0, 667, CONTROLLER_WIDTH - 8, 25));

		toolBar = new JToolBar();
		toolBar.setSize(new Dimension(TOOLBAR_WIDTH, TOOLBAR_HEIGHT));
		toolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 0));
		toolBar.setFloatable(false);
		toolBar.setLocation(0, 0);
		toolBar.setBorder(BorderFactory.createEtchedBorder());
		arrangeToolBar();
		this.getContentPane().add(toolBar, null);
		toolBar.revalidate();
		toolBar.repaint();
		repaint();

		// Creating the menu bar
		menuBar = new JMenuBar();
		arrangeMenu();
		setJMenuBar(menuBar);

		this.setDefaultCloseOperation(3);
		this.getContentPane().add(messageLbl, null);

		setControllerSize();

		// sets the frame to be visible.
		setVisible(true);

	}

	/**
	 * Called when the load program button was pressed.
	 */
	public void loadProgramButton_actionPerformed(ActionEvent e) {
		notifyControllerListeners(ControllerEvent.LOAD_PROGRAM, null);
	}

	/**
	 * Called when the additonal display's "no display" menu item was selected.
	 */
	public void noAdditionalDisplayMenuItem_actionPerformed(ActionEvent e) {
		if (!additionalDisplayCombo.isSelectedIndex(HackController.NO_ADDITIONAL_DISPLAY)) {
			additionalDisplayCombo.setSelectedIndex(HackController.NO_ADDITIONAL_DISPLAY);
		}
	}

	/**
	 * Called when the animation's "no animation" menu item was selected.
	 */
	public void noAnimMenuItem_actionPerformed(ActionEvent e) {
		if (!animationCombo.isSelectedIndex(HackController.NO_DISPLAY_CHANGES)) {
			animationCombo.setSelectedIndex(HackController.NO_DISPLAY_CHANGES);
		}
	}

	/**
	 * Notify all the ControllerEventListeners on actions taken in it, by
	 * creating a ControllerEvent (with the action and supplied data) and
	 * sending it using the actionPerformed method to all the listeners.
	 */
	@Override
	public void notifyControllerListeners(byte action, Object data) {
		ControllerEvent event = new ControllerEvent(this, action, data);
		for (int i = 0; i < listeners.size(); i++) {
			((ControllerEventListener) listeners.elementAt(i)).actionPerformed(event);
		}
	}

	/**
	 * Called when the output file is updated.
	 */
	@Override
	public void outputFileUpdated() {
		outputComponent.refresh();
	}

	/**
	 * Called when the additonal display's "output" menu item was selected.
	 */
	public void outputMenuItem_actionPerformed(ActionEvent e) {
		if (!additionalDisplayCombo.isSelectedIndex(HackController.OUTPUT_ADDITIONAL_DISPLAY)) {
			additionalDisplayCombo.setSelectedIndex(HackController.OUTPUT_ADDITIONAL_DISPLAY);
		}
	}

	/**
	 * Called when the animation's "display changes" menu item was selected.
	 */
	public void partAnimMenuItem_actionPerformed(ActionEvent e) {
		if (!animationCombo.isSelectedIndex(HackController.DISPLAY_CHANGES)) {
			animationCombo.setSelectedIndex(HackController.DISPLAY_CHANGES);
		}
	}

	/**
	 * Called when the load program menu item was selected.
	 */
	public void programMenuItem_actionPerformed(ActionEvent e) {
		notifyControllerListeners(ControllerEvent.LOAD_PROGRAM, null);
	}

	/**
	 * Un-registers the given ControllerEventListener from being a listener to
	 * this GUI.
	 */
	@Override
	public void removeControllerListener(ControllerEventListener listener) {
		listeners.removeElement(listener);
	}

	/**
	 * Called when the rewind button was pressed.
	 */
	public void rewindButton_actionPerformed(ActionEvent e) {
		notifyControllerListeners(ControllerEvent.REWIND, null);
	}

	/**
	 * Called when the rewind menu item was selected.
	 */
	public void rewindMenuItem_actionPerformed(ActionEvent e) {
		notifyControllerListeners(ControllerEvent.REWIND, null);
	}

	/**
	 * Called when the load script button was pressed.
	 */
	public void scriptButton_actionPerformed(ActionEvent e) {
		scriptPressed();
	}

	/**
	 * Called when the additonal display's "script" menu item was selected.
	 */
	public void scriptDisplayMenuItem_actionPerformed(ActionEvent e) {
		if (!additionalDisplayCombo.isSelectedIndex(HackController.SCRIPT_ADDITIONAL_DISPLAY)) {
			additionalDisplayCombo.setSelectedIndex(HackController.SCRIPT_ADDITIONAL_DISPLAY);
		}
	}

	/**
	 * Called when the load script menu item was selected.
	 */
	public void scriptMenuItem_actionPerformed(ActionEvent e) {
		scriptPressed();
	}

	// called when the load script button is pressed.
	private void scriptPressed() {
		int returnVal = fileChooser.showDialog(this, "Load Script");
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			notifyControllerListeners(ControllerEvent.SCRIPT_CHANGE, fileChooser.getSelectedFile().getAbsoluteFile());
			scriptComponent.setContents(fileChooser.getSelectedFile().getAbsolutePath());
		}
	}

	@Override
	public void setAdditionalDisplay(int display) {
		if (!additionalDisplayCombo.isSelectedIndex(display)) {
			additionalDisplayCombo.setSelectedIndex(display);
		}
	}

	/**
	 * Sets the animation mode (int code, out of the possible animation
	 * constants in HackController)
	 */
	@Override
	public void setAnimationMode(int mode) {
		if (!animationCombo.isSelectedIndex(mode)) {
			animationCombo.setSelectedIndex(mode);
		}
	}

	/**
	 * Sets the breakpoints list with the given one.
	 */
	@Override
	public void setBreakpoints(Vector breakpoints) {
		// sending the given Vector to the breakpoint panel.
		breakpointWindow.setBreakpoints(breakpoints);
	}

	/**
	 * Sets the comparison file name with the given one.
	 */
	@Override
	public void setComparisonFile(String fileName) {
		comparisonComponent.setContents(fileName);
	}

	/**
	 * Sets the controller's size according to the size constants.
	 */
	protected void setControllerSize() {
		setSize(new Dimension(CONTROLLER_WIDTH, CONTROLLER_HEIGHT));
	}

	/**
	 * Sets the current comparison line.
	 */
	@Override
	public void setCurrentComparisonLine(int line) {
		comparisonComponent.setSelectedRow(line);
	}

	/**
	 * Sets the current output line.
	 */
	@Override
	public void setCurrentOutputLine(int line) {
		outputComponent.setSelectedRow(line);
	}

	/**
	 * Sets the current script line.
	 */
	@Override
	public void setCurrentScriptLine(int line) {
		scriptComponent.setSelectedRow(line);

	}

	/**
	 * Sets the numeric format with the given code (out of the format constants
	 * in HackController).
	 */
	@Override
	public void setNumericFormat(int formatCode) {
		if (!formatCombo.isSelectedIndex(formatCode)) {
			formatCombo.setSelectedIndex(formatCode);
		}
	}

	/**
	 * Sets the output file name with the given one.
	 */
	@Override
	public void setOutputFile(String fileName) {
		outputComponent.setContents(fileName);
	}

	/**
	 * Sets the script file name with the given one.
	 */
	@Override
	public void setScriptFile(String fileName) {
		scriptComponent.setContents(fileName);
	}

	@Override
	public void setSimulator(HackSimulatorGUI simulator) {
		((JComponent) simulator).setLocation(0, TOOLBAR_HEIGHT);
		this.getContentPane().add((JComponent) simulator, null);
		((JComponent) simulator).revalidate();
		repaint();

		if (simulator.getUsageFileName() != null) {
			usageWindow = new HTMLViewFrame(simulator.getUsageFileName());
			usageWindow.setSize(450, 430);
		}

		if (simulator.getAboutFileName() != null) {
			aboutWindow = new HTMLViewFrame(simulator.getAboutFileName());
			aboutWindow.setSize(450, 420);
		}
	}

	/**
	 * Sets the speed (int code, between 1 and NUMBER_OF_SPEED_UNTIS)
	 */
	@Override
	public void setSpeed(int speed) {
		speedSlider.setValue(speed);
		repaint();
	}

	/**
	 * Sets the list of recognized variables with the given one.
	 */
	@Override
	public void setVariables(String[] newVars) {
		breakpointWindow.setVariables(newVars);
	}

	@Override
	public void setWorkingDir(File file) {
		fileChooser.setCurrentDirectory(file);
	}

	/**
	 * Shows the breakpoint panel.
	 */
	@Override
	public void showBreakpoints() {
		breakpointWindow.getTable().clearSelection();
		breakpointWindow.setVisible(true);
		if (breakpointWindow.getState() == Frame.ICONIFIED) {
			breakpointWindow.setState(Frame.NORMAL);
		}
	}

	/**
	 * Shows the controller.
	 */
	public void showController() {
		setVisible(true);
	}

	/**
	 * Called when the single step button was pressed.
	 */
	public void singleStepButton_actionPerformed(ActionEvent e) {
		notifyControllerListeners(ControllerEvent.SINGLE_STEP, null);
	}

	/**
	 * Called when the single step menu item was selected.
	 */
	public void singleStepMenuItem_actionPerformed(ActionEvent e) {
		notifyControllerListeners(ControllerEvent.SINGLE_STEP, null);
	}

	/**
	 * Called when the speed slider was moved.
	 */
	public void SpeedSlider_stateChanged(ChangeEvent e) {
		JSlider source = (JSlider) e.getSource();
		if (!source.getValueIsAdjusting()) {
			int speed = source.getValue();
			notifyControllerListeners(ControllerEvent.SPEED_CHANGE, new Integer(speed));
		}
	}

	/**
	 * Called when the stop button was pressed.
	 */
	public void stopButton_actionPerformed(ActionEvent e) {
		notifyControllerListeners(ControllerEvent.STOP, null);
	}

	/**
	 * Called when the stop menu item was selected.
	 */
	public void stopMenuItem_actionPerformed(ActionEvent e) {
		notifyControllerListeners(ControllerEvent.STOP, null);
	}

	/**
	 * Called when the usage window menu item was selected.
	 */
	public void usageMenuItem_actionPerformed(ActionEvent e) {
		if (usageWindow != null) {
			usageWindow.setVisible(true);
		}
	}
}
