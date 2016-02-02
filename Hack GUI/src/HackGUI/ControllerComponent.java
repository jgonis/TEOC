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

import Hack.Controller.Breakpoint;
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

	private static final long serialVersionUID = 1L;
	// The dimensions of the tool bar.
	protected static final int TOOLBAR_WIDTH = 1016;
	protected static final int TOOLBAR_HEIGHT = 55;

	// The dimensions of this window.
	private static final int CONTROLLER_WIDTH = 1024;
	private static final int CONTROLLER_HEIGHT = 741;

	// The dimensions of the toolbar's separator.
	protected static final Dimension separatorDimension = new Dimension(3, TOOLBAR_HEIGHT - 5);

	// The vector of listeners to this component.
	private Vector<ControllerEventListener> m_listeners;

	// The fast forward button.
	protected MouseOverJButton m_ffwdButton;

	// The stop button.
	protected MouseOverJButton m_stopButton;

	// The rewind button.
	protected MouseOverJButton m_rewindButton;

	// The load script button.
	protected MouseOverJButton m_scriptButton;

	// The breakpoints button.
	protected MouseOverJButton m_breakButton;

	// The single step button.
	protected MouseOverJButton m_singleStepButton;

	// The load program button.
	protected MouseOverJButton m_loadProgramButton;

	// Creating the file chooser window & the breakpoint window.
	private JFileChooser m_fileChooser = new JFileChooser();
	private BreakpointWindow m_breakpointWindow = new BreakpointWindow();

	// Creating the icons for the buttons.
	private ImageIcon m_rewindIcon = new ImageIcon(Utilities.imagesDir + "vcrrewind.gif");
	private ImageIcon m_ffwdIcon = new ImageIcon(Utilities.imagesDir + "vcrfastforward.gif");
	private ImageIcon m_singleStepIcon = new ImageIcon(Utilities.imagesDir + "vcrforward.gif");
	private ImageIcon m_stopIcon = new ImageIcon(Utilities.imagesDir + "vcrstop.gif");
	private ImageIcon m_breakIcon = new ImageIcon(Utilities.imagesDir + "redflag.gif");
	private ImageIcon m_loadProgramIcon = new ImageIcon(Utilities.imagesDir + "opendoc.gif");
	private ImageIcon m_scriptIcon = new ImageIcon(Utilities.imagesDir + "scroll.gif");

	// The speed slider.
	protected JSlider m_speedSlider;

	// A combo box which controls the format of all the components.
	protected TitledComboBox m_formatCombo;

	// A combo box for choosing the additional disply.
	protected TitledComboBox m_additionalDisplayCombo;

	// A combo box for choosing the animation type.
	protected TitledComboBox m_animationCombo;

	// The toolbar of the controller.
	protected JToolBar m_toolBar;

	// The components of the menu
	protected JMenuBar m_menuBar;
	protected JMenu m_fileMenu, m_viewMenu, m_runMenu, m_helpMenu;
	protected JMenuItem m_singleStepMenuItem, m_ffwdMenuItem, m_stopMenuItem, m_rewindMenuItem, m_exitMenuItem;
	protected JMenuItem m_usageMenuItem, m_aboutMenuItem;
	protected JMenu m_animationSubMenu, m_numericFormatSubMenu, m_additionalDisplaySubMenu;
	protected JMenuItem m_breakpointsMenuItem, m_scriptMenuItem, m_programMenuItem;
	protected JRadioButtonMenuItem m_decMenuItem, m_hexaMenuItem, m_binMenuItem;
	protected JRadioButtonMenuItem m_scriptDisplayMenuItem, m_outputMenuItem, m_compareMenuItem, m_noAdditionalDisplayMenuItem;
	protected JRadioButtonMenuItem m_partAnimMenuItem, m_fullAnimMenuItem, m_noAnimMenuItem;

	// the message label (status line)
	protected JLabel m_messageLbl = new JLabel();

	// component for displaying the script, output file and comparison file.
	protected FileDisplayComponent m_scriptComponent;
	protected FileDisplayComponent m_outputComponent;
	protected FileDisplayComponent m_comparisonComponent;

	// HTML viewers for the usage and about windows.
	private HTMLViewFrame m_usageWindow, m_aboutWindow;

	/**
	 * Constructs a new ControllerComponent.
	 */
	public ControllerComponent() {
		m_listeners = new Vector<ControllerEventListener>();
		m_formatCombo = new TitledComboBox("Format:", "Numeric display format",
				new String[] { "Decimal", "Hexa", "Binary" }, 75);
		m_additionalDisplayCombo = new TitledComboBox("View:", "View options",
				new String[] { "Script", "Output", "Compare", "Screen" }, 80);
		m_animationCombo = new TitledComboBox("Animate:", "Animtion type",
				new String[] { "Program flow", "Program & data flow", "No animation" }, 135);
		m_scriptComponent = new FileDisplayComponent();
		m_outputComponent = new FileDisplayComponent();
		m_comparisonComponent = new FileDisplayComponent();

		init();
		jbInit();
	}

	/**
	 * Called when the about window menu item was selected.
	 */
	public void aboutMenuItem_actionPerformed() {
		if (m_aboutWindow != null) {
			m_aboutWindow.setVisible(true);
		}
	}

	/**
	 * Registers the given ControllerEventListener as a listener to this GUI.
	 */
	@Override
	public void addControllerListener(ControllerEventListener listener) {
		m_listeners.addElement(listener);
	}

	/**
	 * Called when a choice was made in the additional display combo box.
	 */
	public void additionalDisplayCombo_actionPerformed() {
		int selectedIndex = m_additionalDisplayCombo.getSelectedIndex();
		switch (selectedIndex) {
		case HackController.SCRIPT_ADDITIONAL_DISPLAY:
			if (!m_scriptMenuItem.isSelected()) {
				m_scriptMenuItem.setSelected(true);
			}
			break;

		case HackController.OUTPUT_ADDITIONAL_DISPLAY:
			if (!m_outputMenuItem.isSelected()) {
				m_outputMenuItem.setSelected(true);
			}
			break;

		case HackController.COMPARISON_ADDITIONAL_DISPLAY:
			if (!m_compareMenuItem.isSelected()) {
				m_compareMenuItem.setSelected(true);
			}
			break;

		case HackController.NO_ADDITIONAL_DISPLAY:
			if (!m_noAdditionalDisplayMenuItem.isSelected()) {
				m_noAdditionalDisplayMenuItem.setSelected(true);
			}
			break;
		}

		notifyControllerListeners(ControllerEvent.ADDITIONAL_DISPLAY_CHANGE, new Integer(selectedIndex));
	}

	/**
	 * Called when a choice was made in the animation type combo box.
	 */
	public void animationCombo_actionPerformed() {
		int selectedIndex = m_animationCombo.getSelectedIndex();
		switch (selectedIndex) {
		case HackController.DISPLAY_CHANGES:
			if (!m_partAnimMenuItem.isSelected()) {
				m_partAnimMenuItem.setSelected(true);
			}
			break;

		case HackController.ANIMATION:
			if (!m_fullAnimMenuItem.isSelected()) {
				m_fullAnimMenuItem.setSelected(true);
			}
			break;

		case HackController.NO_DISPLAY_CHANGES:
			if (!m_noAnimMenuItem.isSelected()) {
				m_noAnimMenuItem.setSelected(true);
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
		m_fileMenu = new JMenu("File");
		m_fileMenu.setMnemonic(KeyEvent.VK_F);
		m_menuBar.add(m_fileMenu);

		m_viewMenu = new JMenu("View");
		m_viewMenu.setMnemonic(KeyEvent.VK_V);
		m_menuBar.add(m_viewMenu);

		m_runMenu = new JMenu("Run");
		m_runMenu.setMnemonic(KeyEvent.VK_R);
		m_menuBar.add(m_runMenu);

		// Build the second menu.
		m_helpMenu = new JMenu("Help");
		m_helpMenu.setMnemonic(KeyEvent.VK_H);
		m_menuBar.add(m_helpMenu);

		m_programMenuItem = new JMenuItem("Load Program", KeyEvent.VK_O);
		m_programMenuItem.addActionListener(e -> programMenuItem_actionPerformed());
		m_fileMenu.add(m_programMenuItem);

		m_scriptMenuItem = new JMenuItem("Load Script", KeyEvent.VK_P);
		m_scriptMenuItem.addActionListener(e -> scriptMenuItem_actionPerformed());
		m_fileMenu.add(m_scriptMenuItem);
		m_fileMenu.addSeparator();

		m_exitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
		m_exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.ALT_MASK));
		m_exitMenuItem.addActionListener(e -> exitMenuItem_actionPerformed());
		m_fileMenu.add(m_exitMenuItem);

		m_viewMenu.addSeparator();

		ButtonGroup animationRadioButtons = new ButtonGroup();

		m_animationSubMenu = new JMenu("Animate");
		m_animationSubMenu.setMnemonic(KeyEvent.VK_A);
		m_viewMenu.add(m_animationSubMenu);

		m_partAnimMenuItem = new JRadioButtonMenuItem("Program flow");
		m_partAnimMenuItem.setMnemonic(KeyEvent.VK_P);
		m_partAnimMenuItem.setSelected(true);
		m_partAnimMenuItem.addActionListener(e -> partAnimMenuItem_actionPerformed());
		animationRadioButtons.add(m_partAnimMenuItem);
		m_animationSubMenu.add(m_partAnimMenuItem);

		m_fullAnimMenuItem = new JRadioButtonMenuItem("Program & data flow");
		m_fullAnimMenuItem.setMnemonic(KeyEvent.VK_D);
		m_fullAnimMenuItem.addActionListener(e -> fullAnimMenuItem_actionPerformed());
		animationRadioButtons.add(m_fullAnimMenuItem);
		m_animationSubMenu.add(m_fullAnimMenuItem);

		m_noAnimMenuItem = new JRadioButtonMenuItem("No Animation");
		m_noAnimMenuItem.setMnemonic(KeyEvent.VK_N);
		m_noAnimMenuItem.addActionListener(e -> noAnimMenuItem_actionPerformed());
		animationRadioButtons.add(m_noAnimMenuItem);
		m_animationSubMenu.add(m_noAnimMenuItem);

		ButtonGroup additionalDisplayRadioButtons = new ButtonGroup();

		m_additionalDisplaySubMenu = new JMenu("View");
		m_additionalDisplaySubMenu.setMnemonic(KeyEvent.VK_V);
		m_viewMenu.add(m_additionalDisplaySubMenu);

		m_scriptDisplayMenuItem = new JRadioButtonMenuItem("Script");
		m_scriptDisplayMenuItem.setMnemonic(KeyEvent.VK_S);
		m_scriptDisplayMenuItem.setSelected(true);
		m_scriptDisplayMenuItem.addActionListener(e -> scriptDisplayMenuItem_actionPerformed());
		additionalDisplayRadioButtons.add(m_scriptDisplayMenuItem);
		m_additionalDisplaySubMenu.add(m_scriptDisplayMenuItem);

		m_outputMenuItem = new JRadioButtonMenuItem("Output");
		m_outputMenuItem.setMnemonic(KeyEvent.VK_O);
		m_outputMenuItem.addActionListener(e -> outputMenuItem_actionPerformed());
		additionalDisplayRadioButtons.add(m_outputMenuItem);
		m_additionalDisplaySubMenu.add(m_outputMenuItem);

		m_compareMenuItem = new JRadioButtonMenuItem("Compare");
		m_compareMenuItem.setMnemonic(KeyEvent.VK_C);
		m_compareMenuItem.addActionListener(e -> compareMenuItem_actionPerformed());
		additionalDisplayRadioButtons.add(m_compareMenuItem);
		m_additionalDisplaySubMenu.add(m_compareMenuItem);

		m_noAdditionalDisplayMenuItem = new JRadioButtonMenuItem("Screen");
		m_noAdditionalDisplayMenuItem.setMnemonic(KeyEvent.VK_N);
		m_noAdditionalDisplayMenuItem.addActionListener(e -> noAdditionalDisplayMenuItem_actionPerformed());
		additionalDisplayRadioButtons.add(m_noAdditionalDisplayMenuItem);
		m_additionalDisplaySubMenu.add(m_noAdditionalDisplayMenuItem);

		ButtonGroup formatRadioButtons = new ButtonGroup();

		m_numericFormatSubMenu = new JMenu("Format");
		m_numericFormatSubMenu.setMnemonic(KeyEvent.VK_F);
		m_viewMenu.add(m_numericFormatSubMenu);

		m_decMenuItem = new JRadioButtonMenuItem("Decimal");
		m_decMenuItem.setMnemonic(KeyEvent.VK_D);
		m_decMenuItem.setSelected(true);
		m_decMenuItem.addActionListener(e -> decMenuItem_actionPerformed());
		formatRadioButtons.add(m_decMenuItem);
		m_numericFormatSubMenu.add(m_decMenuItem);

		m_hexaMenuItem = new JRadioButtonMenuItem("Hexadecimal");
		m_hexaMenuItem.setMnemonic(KeyEvent.VK_H);
		m_hexaMenuItem.addActionListener(e -> hexaMenuItem_actionPerformed());
		formatRadioButtons.add(m_hexaMenuItem);
		m_numericFormatSubMenu.add(m_hexaMenuItem);

		m_binMenuItem = new JRadioButtonMenuItem("Binary");
		m_binMenuItem.setMnemonic(KeyEvent.VK_B);
		m_binMenuItem.addActionListener(e -> binMenuItem_actionPerformed());
		formatRadioButtons.add(m_binMenuItem);
		m_numericFormatSubMenu.add(m_binMenuItem);

		m_viewMenu.addSeparator();

		m_singleStepMenuItem = new JMenuItem("Single Step", KeyEvent.VK_S);
		m_singleStepMenuItem.setAccelerator(KeyStroke.getKeyStroke("F11"));
		m_singleStepMenuItem.addActionListener(e -> singleStepMenuItem_actionPerformed());
		m_runMenu.add(m_singleStepMenuItem);

		m_ffwdMenuItem = new JMenuItem("Run", KeyEvent.VK_F);
		m_ffwdMenuItem.setAccelerator(KeyStroke.getKeyStroke("F5"));
		m_ffwdMenuItem.addActionListener(e -> ffwdMenuItem_actionPerformed());
		m_runMenu.add(m_ffwdMenuItem);

		m_stopMenuItem = new JMenuItem("Stop", KeyEvent.VK_T);
		m_stopMenuItem.setAccelerator(KeyStroke.getKeyStroke("shift F5"));
		m_stopMenuItem.addActionListener(e -> stopMenuItem_actionPerformed());
		m_runMenu.add(m_stopMenuItem);

		m_rewindMenuItem = new JMenuItem("Reset", KeyEvent.VK_R);
		m_rewindMenuItem.addActionListener(e -> rewindMenuItem_actionPerformed());
		m_runMenu.add(m_rewindMenuItem);

		m_runMenu.addSeparator();

		m_breakpointsMenuItem = new JMenuItem("Breakpoints", KeyEvent.VK_B);
		m_breakpointsMenuItem.addActionListener(e -> breakpointsMenuItem_actionPerformed());
		m_runMenu.add(m_breakpointsMenuItem);

		m_usageMenuItem = new JMenuItem("Usage", KeyEvent.VK_U);
		m_usageMenuItem.setAccelerator(KeyStroke.getKeyStroke("F1"));
		m_usageMenuItem.addActionListener(e -> usageMenuItem_actionPerformed());
		m_helpMenu.add(m_usageMenuItem);

		m_aboutMenuItem = new JMenuItem("About ...", KeyEvent.VK_A);
		m_aboutMenuItem.addActionListener(e -> aboutMenuItem_actionPerformed());
		m_helpMenu.add(m_aboutMenuItem);

	}

	/**
	 * Adds the controls to the toolbar.
	 */
	protected void arrangeToolBar() {
		m_toolBar.add(m_loadProgramButton);
		m_toolBar.addSeparator(separatorDimension);
		m_toolBar.add(m_singleStepButton);
		m_toolBar.add(m_ffwdButton);
		m_toolBar.add(m_stopButton);
		m_toolBar.add(m_rewindButton);
		m_toolBar.addSeparator(separatorDimension);
		m_toolBar.add(m_scriptButton);
		m_toolBar.add(m_breakButton);
		m_toolBar.addSeparator(separatorDimension);
		m_toolBar.add(m_speedSlider);
		m_toolBar.add(m_animationCombo);
		m_toolBar.add(m_additionalDisplayCombo);
		m_toolBar.add(m_formatCombo);
	}

	/**
	 * Called when the numeric format's "binary" menu item was selected.
	 */
	public void binMenuItem_actionPerformed() {
		if (!m_formatCombo.isSelectedIndex(HackController.BINARY_FORMAT)) {
			m_formatCombo.setSelectedIndex(HackController.BINARY_FORMAT);
		}
	}

	/**
	 * Called when the breakpoints button was pressed.
	 */
	public void breakButton_actionPerformed() {
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
	public void breakpointsMenuItem_actionPerformed() {
		showBreakpoints();
	}

	/**
	 * Called when the additonal display's "comparison" menu item was selected.
	 */
	public void compareMenuItem_actionPerformed() {
		if (!m_additionalDisplayCombo.isSelectedIndex(HackController.COMPARISON_ADDITIONAL_DISPLAY)) {
			m_additionalDisplayCombo.setSelectedIndex(HackController.COMPARISON_ADDITIONAL_DISPLAY);
		}
	}

	/**
	 * Called when the numeric format's "decimal" menu item was selected.
	 */
	public void decMenuItem_actionPerformed() {
		if (!m_formatCombo.isSelectedIndex(HackController.DECIMAL_FORMAT)) {
			m_formatCombo.setSelectedIndex(HackController.DECIMAL_FORMAT);
		}
	}

	/**
	 * Disables the animation mode buttons.
	 */
	@Override
	public void disableAnimationModes() {
		m_animationCombo.setEnabled(false);
		m_partAnimMenuItem.setEnabled(false);
		m_fullAnimMenuItem.setEnabled(false);
		m_noAnimMenuItem.setEnabled(false);
	}

	/**
	 * Disables the fast forward action.
	 */
	@Override
	public void disableFastForward() {
		m_ffwdButton.setEnabled(false);
		m_ffwdMenuItem.setEnabled(false);
	}

	/**
	 * Disables the load program action.
	 */
	@Override
	public void disableLoadProgram() {
		m_loadProgramButton.setEnabled(false);
	}

	/**
	 * Disables the rewind action.
	 */
	@Override
	public void disableRewind() {
		m_rewindButton.setEnabled(false);
		m_rewindMenuItem.setEnabled(false);
	}

	/**
	 * Disables the eject action.
	 */
	@Override
	public void disableScript() {
		m_scriptButton.setEnabled(false);
		m_scriptMenuItem.setEnabled(false);
	}

	/**
	 * Disables the single step action.
	 */
	@Override
	public void disableSingleStep() {
		m_singleStepButton.setEnabled(false);
		m_singleStepMenuItem.setEnabled(false);
	}

	/**
	 * Disables the speed slider.
	 */
	@Override
	public void disableSpeedSlider() {
		m_speedSlider.setEnabled(false);
	}

	/**
	 * Disables the stop action.
	 */
	@Override
	public void disableStop() {
		m_stopButton.setEnabled(false);
		m_stopMenuItem.setEnabled(false);
	}

	@Override
	public void displayMessage(String message, boolean error) {
		if (error) {
			m_messageLbl.setForeground(Color.red);
		} else {
			m_messageLbl.setForeground(UIManager.getColor("Label.foreground"));
		}
		m_messageLbl.setText(message);
		m_messageLbl.setToolTipText(message);
	}

	/**
	 * Enables the animation mode buttons.
	 */
	@Override
	public void enableAnimationModes() {
		m_animationCombo.setEnabled(true);
		m_partAnimMenuItem.setEnabled(true);
		m_fullAnimMenuItem.setEnabled(true);
		m_noAnimMenuItem.setEnabled(true);
	}

	/**
	 * Enables the fast forward action.
	 */
	@Override
	public void enableFastForward() {
		m_ffwdButton.setEnabled(true);
		m_ffwdMenuItem.setEnabled(true);
	}

	/**
	 * Enables the load program action.
	 */
	@Override
	public void enableLoadProgram() {
		m_loadProgramButton.setEnabled(true);
	}

	/**
	 * Enables the rewind action.
	 */
	@Override
	public void enableRewind() {
		m_rewindButton.setEnabled(true);
		m_rewindMenuItem.setEnabled(true);
	}

	/**
	 * Enables the eject action.
	 */
	@Override
	public void enableScript() {
		m_scriptButton.setEnabled(true);
		m_scriptMenuItem.setEnabled(true);
	}

	/**
	 * Enables the single step action.
	 */
	@Override
	public void enableSingleStep() {
		m_singleStepButton.setEnabled(true);
		m_singleStepMenuItem.setEnabled(true);
	}

	/**
	 * Enables the speed slider.
	 */
	@Override
	public void enableSpeedSlider() {
		m_speedSlider.setEnabled(true);
	}

	/**
	 * Enables the stop action.
	 */
	@Override
	public void enableStop() {
		m_stopButton.setEnabled(true);
		m_stopMenuItem.setEnabled(true);
	}

	/**
	 * Called when the exit menu item was selected.
	 */
	public void exitMenuItem_actionPerformed() {
		System.exit(0);
	}

	/**
	 * Called when the fast forward button was pressed.
	 */
	public void ffwdButton_actionPerformed() {
		notifyControllerListeners(ControllerEvent.FAST_FORWARD, null);
	}

	/**
	 * Called when the fast forward menu item was selected.
	 */
	public void ffwdMenuItem_actionPerformed() {
		notifyControllerListeners(ControllerEvent.FAST_FORWARD, null);
	}

	/**
	 * Called when the names of the files were changed. The event contains the
	 * three strings representing the names of the files.
	 */
	@Override
	public void filesNamesChanged(FilesTypeEvent event) {
		if (event.getFirstFile() != null) {
			m_scriptComponent.setContents(event.getFirstFile());
			notifyControllerListeners(ControllerEvent.SCRIPT_CHANGE, event.getFirstFile());
		}
		if (event.getSecondFile() != null) {
			m_outputComponent.setContents(event.getSecondFile());
		}
		if (event.getThirdFile() != null) {
			m_comparisonComponent.setContents(event.getThirdFile());
		}
	}

	/**
	 * Called when a choice was made in the numeric format combo box.
	 */
	public void formatCombo_actionPerformed() {
		int selectedIndex = m_formatCombo.getSelectedIndex();
		switch (selectedIndex) {
		case HackController.DECIMAL_FORMAT:
			if (!m_decMenuItem.isSelected()) {
				m_decMenuItem.setSelected(true);
			}
			break;

		case HackController.HEXA_FORMAT:
			if (!m_hexaMenuItem.isSelected()) {
				m_hexaMenuItem.setSelected(true);
			}
			break;

		case HackController.BINARY_FORMAT:
			if (!m_binMenuItem.isSelected()) {
				m_binMenuItem.setSelected(true);
			}
			break;
		}

		notifyControllerListeners(ControllerEvent.NUMERIC_FORMAT_CHANGE, new Integer(selectedIndex));
	}

	/**
	 * Called when the animation's "animate" menu item was selected.
	 */
	public void fullAnimMenuItem_actionPerformed() {
		if (!m_animationCombo.isSelectedIndex(HackController.ANIMATION)) {
			m_animationCombo.setSelectedIndex(HackController.ANIMATION);
		}
	}

	@Override
	public JComponent getComparisonComponent() {
		return m_comparisonComponent;
	}

	@Override
	public JComponent getOutputComponent() {
		return m_outputComponent;
	}

	@Override
	public JComponent getScriptComponent() {
		return m_scriptComponent;
	}

	/**
	 * Called when the numeric format's "hexa" menu item was selected.
	 */
	public void hexaMenuItem_actionPerformed() {
		if (!m_formatCombo.isSelectedIndex(HackController.HEXA_FORMAT)) {
			m_formatCombo.setSelectedIndex(HackController.HEXA_FORMAT);
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
		m_speedSlider = new JSlider(SwingConstants.HORIZONTAL, 1, HackController.NUMBER_OF_SPEED_UNITS, 1);
		m_loadProgramButton = new MouseOverJButton();
		m_ffwdButton = new MouseOverJButton();
		m_stopButton = new MouseOverJButton();
		m_rewindButton = new MouseOverJButton();
		m_scriptButton = new MouseOverJButton();
		m_breakButton = new MouseOverJButton();
		m_singleStepButton = new MouseOverJButton();
	}

	// Initializes this component.
	private void jbInit() {
		m_fileChooser.setFileFilter(new ScriptFileFilter());
		this.getContentPane().setLayout(null);

		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();

		JLabel slowLabel = new JLabel("Slow");
		slowLabel.setFont(Utilities.thinLabelsFont);
		JLabel fastLabel = new JLabel("Fast");
		fastLabel.setFont(Utilities.thinLabelsFont);
		labelTable.put(new Integer(1), slowLabel);
		labelTable.put(new Integer(5), fastLabel);

		m_speedSlider.addChangeListener(e -> SpeedSlider_stateChanged(e));
		m_speedSlider.setLabelTable(labelTable);
		m_speedSlider.setMajorTickSpacing(1);
		m_speedSlider.setPaintTicks(true);
		m_speedSlider.setPaintLabels(true);
		m_speedSlider.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		m_speedSlider.setPreferredSize(new Dimension(95, 50));
		m_speedSlider.setMinimumSize(new Dimension(95, 50));
		m_speedSlider.setToolTipText("Speed");
		m_speedSlider.setMaximumSize(new Dimension(95, 50));

		m_loadProgramButton.addActionListener(e -> loadProgramButton_actionPerformed());
		m_loadProgramButton.setMaximumSize(new Dimension(39, 39));
		m_loadProgramButton.setMinimumSize(new Dimension(39, 39));
		m_loadProgramButton.setPreferredSize(new Dimension(39, 39));
		m_loadProgramButton.setSize(new Dimension(39, 39));
		m_loadProgramButton.setToolTipText("Load Program");
		m_loadProgramButton.setIcon(m_loadProgramIcon);

		m_ffwdButton.setMaximumSize(new Dimension(39, 39));
		m_ffwdButton.setMinimumSize(new Dimension(39, 39));
		m_ffwdButton.setPreferredSize(new Dimension(39, 39));
		m_ffwdButton.setToolTipText("Run");
		m_ffwdButton.setIcon(m_ffwdIcon);
		m_ffwdButton.addActionListener(e -> ffwdButton_actionPerformed());

		m_stopButton.addActionListener(e -> stopButton_actionPerformed());
		m_stopButton.setMaximumSize(new Dimension(39, 39));
		m_stopButton.setMinimumSize(new Dimension(39, 39));
		m_stopButton.setPreferredSize(new Dimension(39, 39));
		m_stopButton.setToolTipText("Stop");
		m_stopButton.setIcon(m_stopIcon);

		m_rewindButton.setMaximumSize(new Dimension(39, 39));
		m_rewindButton.setMinimumSize(new Dimension(39, 39));
		m_rewindButton.setPreferredSize(new Dimension(39, 39));
		m_rewindButton.setToolTipText("Reset");
		m_rewindButton.setIcon(m_rewindIcon);
		m_rewindButton.addActionListener(e -> rewindButton_actionPerformed());

		m_scriptButton.setMaximumSize(new Dimension(39, 39));
		m_scriptButton.setMinimumSize(new Dimension(39, 39));
		m_scriptButton.setPreferredSize(new Dimension(39, 39));
		m_scriptButton.setToolTipText("Load Script");
		m_scriptButton.setIcon(m_scriptIcon);
		m_scriptButton.addActionListener(e -> scriptButton_actionPerformed());

		m_breakButton.addActionListener(e -> breakButton_actionPerformed());
		m_breakButton.setMaximumSize(new Dimension(39, 39));
		m_breakButton.setMinimumSize(new Dimension(39, 39));
		m_breakButton.setPreferredSize(new Dimension(39, 39));
		m_breakButton.setToolTipText("Open breakpoint panel");
		m_breakButton.setIcon(m_breakIcon);

		m_breakpointWindow.addBreakpointListener(this);

		m_singleStepButton.addActionListener(e -> singleStepButton_actionPerformed());
		m_singleStepButton.setMaximumSize(new Dimension(39, 39));
		m_singleStepButton.setMinimumSize(new Dimension(39, 39));
		m_singleStepButton.setPreferredSize(new Dimension(39, 39));
		m_singleStepButton.setSize(new Dimension(39, 39));
		m_singleStepButton.setToolTipText("Single Step");
		m_singleStepButton.setIcon(m_singleStepIcon);

		m_animationCombo.addActionListener(e -> animationCombo_actionPerformed());

		m_formatCombo.addActionListener(e -> formatCombo_actionPerformed());

		m_additionalDisplayCombo.addActionListener(e -> additionalDisplayCombo_actionPerformed());

		m_messageLbl.setFont(Utilities.statusLineFont);
		m_messageLbl.setBorder(BorderFactory.createLoweredBevelBorder());
		m_messageLbl.setBounds(new Rectangle(0, 667, CONTROLLER_WIDTH - 8, 25));

		m_toolBar = new JToolBar();
		m_toolBar.setSize(new Dimension(TOOLBAR_WIDTH, TOOLBAR_HEIGHT));
		m_toolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 0));
		m_toolBar.setFloatable(false);
		m_toolBar.setLocation(0, 0);
		m_toolBar.setBorder(BorderFactory.createEtchedBorder());
		arrangeToolBar();
		this.getContentPane().add(m_toolBar, null);
		m_toolBar.revalidate();
		m_toolBar.repaint();
		repaint();

		// Creating the menu bar
		m_menuBar = new JMenuBar();
		arrangeMenu();
		setJMenuBar(m_menuBar);

		this.setDefaultCloseOperation(3);
		this.getContentPane().add(m_messageLbl, null);

		setControllerSize();

		// sets the frame to be visible.
		setVisible(true);

	}

	/**
	 * Called when the load program button was pressed.
	 */
	public void loadProgramButton_actionPerformed() {
		notifyControllerListeners(ControllerEvent.LOAD_PROGRAM, null);
	}

	/**
	 * Called when the additonal display's "no display" menu item was selected.
	 */
	public void noAdditionalDisplayMenuItem_actionPerformed() {
		if (!m_additionalDisplayCombo.isSelectedIndex(HackController.NO_ADDITIONAL_DISPLAY)) {
			m_additionalDisplayCombo.setSelectedIndex(HackController.NO_ADDITIONAL_DISPLAY);
		}
	}

	/**
	 * Called when the animation's "no animation" menu item was selected.
	 */
	public void noAnimMenuItem_actionPerformed() {
		if (!m_animationCombo.isSelectedIndex(HackController.NO_DISPLAY_CHANGES)) {
			m_animationCombo.setSelectedIndex(HackController.NO_DISPLAY_CHANGES);
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
		for (int i = 0; i < m_listeners.size(); i++) {
			m_listeners.elementAt(i).actionPerformed(event);
		}
	}

	/**
	 * Called when the output file is updated.
	 */
	@Override
	public void outputFileUpdated() {
		m_outputComponent.refresh();
	}

	/**
	 * Called when the additional display's "output" menu item was selected.
	 */
	public void outputMenuItem_actionPerformed() {
		if (!m_additionalDisplayCombo.isSelectedIndex(HackController.OUTPUT_ADDITIONAL_DISPLAY)) {
			m_additionalDisplayCombo.setSelectedIndex(HackController.OUTPUT_ADDITIONAL_DISPLAY);
		}
	}

	/**
	 * Called when the animation's "display changes" menu item was selected.
	 */
	public void partAnimMenuItem_actionPerformed() {
		if (!m_animationCombo.isSelectedIndex(HackController.DISPLAY_CHANGES)) {
			m_animationCombo.setSelectedIndex(HackController.DISPLAY_CHANGES);
		}
	}

	/**
	 * Called when the load program menu item was selected.
	 */
	public void programMenuItem_actionPerformed() {
		notifyControllerListeners(ControllerEvent.LOAD_PROGRAM, null);
	}

	/**
	 * Un-registers the given ControllerEventListener from being a listener to
	 * this GUI.
	 */
	@Override
	public void removeControllerListener(ControllerEventListener listener) {
		m_listeners.removeElement(listener);
	}

	/**
	 * Called when the rewind button was pressed.
	 */
	public void rewindButton_actionPerformed() {
		notifyControllerListeners(ControllerEvent.REWIND, null);
	}

	/**
	 * Called when the rewind menu item was selected.
	 */
	public void rewindMenuItem_actionPerformed() {
		notifyControllerListeners(ControllerEvent.REWIND, null);
	}

	/**
	 * Called when the load script button was pressed.
	 */
	public void scriptButton_actionPerformed() {
		scriptPressed();
	}

	/**
	 * Called when the additonal display's "script" menu item was selected.
	 */
	public void scriptDisplayMenuItem_actionPerformed() {
		if (!m_additionalDisplayCombo.isSelectedIndex(HackController.SCRIPT_ADDITIONAL_DISPLAY)) {
			m_additionalDisplayCombo.setSelectedIndex(HackController.SCRIPT_ADDITIONAL_DISPLAY);
		}
	}

	/**
	 * Called when the load script menu item was selected.
	 */
	public void scriptMenuItem_actionPerformed() {
		scriptPressed();
	}

	// called when the load script button is pressed.
	private void scriptPressed() {
		int returnVal = m_fileChooser.showDialog(this, "Load Script");
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			notifyControllerListeners(ControllerEvent.SCRIPT_CHANGE, m_fileChooser.getSelectedFile().getAbsoluteFile());
			m_scriptComponent.setContents(m_fileChooser.getSelectedFile().getAbsolutePath());
		}
	}

	@Override
	public void setAdditionalDisplay(int display) {
		if (!m_additionalDisplayCombo.isSelectedIndex(display)) {
			m_additionalDisplayCombo.setSelectedIndex(display);
		}
	}

	/**
	 * Sets the animation mode (int code, out of the possible animation
	 * constants in HackController)
	 */
	@Override
	public void setAnimationMode(int mode) {
		if (!m_animationCombo.isSelectedIndex(mode)) {
			m_animationCombo.setSelectedIndex(mode);
		}
	}

	/**
	 * Sets the breakpoints list with the given one.
	 */
	@Override
	public void setBreakpoints(Vector<Breakpoint> breakpoints) {
		// sending the given Vector to the breakpoint panel.
		m_breakpointWindow.setBreakpoints(breakpoints);
	}

	/**
	 * Sets the comparison file name with the given one.
	 */
	@Override
	public void setComparisonFile(String fileName) {
		m_comparisonComponent.setContents(fileName);
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
		m_comparisonComponent.setSelectedRow(line);
	}

	/**
	 * Sets the current output line.
	 */
	@Override
	public void setCurrentOutputLine(int line) {
		m_outputComponent.setSelectedRow(line);
	}

	/**
	 * Sets the current script line.
	 */
	@Override
	public void setCurrentScriptLine(int line) {
		m_scriptComponent.setSelectedRow(line);

	}

	/**
	 * Sets the numeric format with the given code (out of the format constants
	 * in HackController).
	 */
	@Override
	public void setNumericFormat(int formatCode) {
		if (!m_formatCombo.isSelectedIndex(formatCode)) {
			m_formatCombo.setSelectedIndex(formatCode);
		}
	}

	/**
	 * Sets the output file name with the given one.
	 */
	@Override
	public void setOutputFile(String fileName) {
		m_outputComponent.setContents(fileName);
	}

	/**
	 * Sets the script file name with the given one.
	 */
	@Override
	public void setScriptFile(String fileName) {
		m_scriptComponent.setContents(fileName);
	}

	@Override
	public void setSimulator(HackSimulatorGUI simulator) {
		((JComponent) simulator).setLocation(0, TOOLBAR_HEIGHT);
		this.getContentPane().add((JComponent) simulator, null);
		((JComponent) simulator).revalidate();
		repaint();

		if (simulator.getUsageFileName() != null) {
			m_usageWindow = new HTMLViewFrame(simulator.getUsageFileName());
			m_usageWindow.setSize(450, 430);
		}

		if (simulator.getAboutFileName() != null) {
			m_aboutWindow = new HTMLViewFrame(simulator.getAboutFileName());
			m_aboutWindow.setSize(450, 420);
		}
	}

	/**
	 * Sets the speed (int code, between 1 and NUMBER_OF_SPEED_UNTIS)
	 */
	@Override
	public void setSpeed(int speed) {
		m_speedSlider.setValue(speed);
		repaint();
	}

	/**
	 * Sets the list of recognized variables with the given one.
	 */
	@Override
	public void setVariables(String[] newVars) {
		m_breakpointWindow.setVariables(newVars);
	}

	@Override
	public void setWorkingDir(File file) {
		m_fileChooser.setCurrentDirectory(file);
	}

	/**
	 * Shows the breakpoint panel.
	 */
	@Override
	public void showBreakpoints() {
		m_breakpointWindow.getTable().clearSelection();
		m_breakpointWindow.setVisible(true);
		if (m_breakpointWindow.getState() == Frame.ICONIFIED) {
			m_breakpointWindow.setState(Frame.NORMAL);
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
	public void singleStepButton_actionPerformed() {
		notifyControllerListeners(ControllerEvent.SINGLE_STEP, null);
	}

	/**
	 * Called when the single step menu item was selected.
	 */
	public void singleStepMenuItem_actionPerformed() {
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
	public void stopButton_actionPerformed() {
		notifyControllerListeners(ControllerEvent.STOP, null);
	}

	/**
	 * Called when the stop menu item was selected.
	 */
	public void stopMenuItem_actionPerformed() {
		notifyControllerListeners(ControllerEvent.STOP, null);
	}

	/**
	 * Called when the usage window menu item was selected.
	 */
	public void usageMenuItem_actionPerformed() {
		if (m_usageWindow != null) {
			m_usageWindow.setVisible(true);
		}
	}
}
