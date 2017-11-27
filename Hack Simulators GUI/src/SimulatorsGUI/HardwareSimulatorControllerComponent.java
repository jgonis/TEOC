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
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;

import Hack.HardwareSimulator.HardwareSimulatorControllerEvent;
import Hack.HardwareSimulator.HardwareSimulatorControllerGUI;
import HackGUI.ControllerComponent;
import HackGUI.MouseOverJButton;

/**
 * The GUI Component of the Hardware Simulator.
 */
public class HardwareSimulatorControllerComponent extends ControllerComponent
		implements HardwareSimulatorControllerGUI/* , ChipNameListener */ {

	private static final long serialVersionUID = -6483469225807068246L;
	// The buttons of this component.
	private MouseOverJButton loadChipButton;
	private MouseOverJButton tickTockButton;
	private MouseOverJButton evalButton;

	// The icons of the buttons.
	private ImageIcon loadChipIcon;
	private ImageIcon tickTockIcon;
	private ImageIcon evalIcon;

	// The settings window and chip loading window.
	private ChipLoaderFileChooser settingsWindow;

	// The menu items of this component.
	private JMenuItem loadChipMenuItem, evalMenuItem, tickTockMenuItem/* , folderSettingsMenuItem */;

	// The chip file chooser
	private JFileChooser chipFileChooser;

	/**
	 * Constructs a new HardwareSimulatorControllerComponent.
	 */
	public HardwareSimulatorControllerComponent() {
		m_scriptComponent.updateSize(516, 592);
		m_outputComponent.updateSize(516, 592);
		m_comparisonComponent.updateSize(516, 592);
	}

	/**
	 * Arranges the menu bar.
	 */
	@Override
	protected void arrangeMenu() {

		super.arrangeMenu();

		m_fileMenu.removeAll();

		loadChipMenuItem = new JMenuItem("Load Chip", KeyEvent.VK_L);
		loadChipMenuItem.addActionListener(e -> loadChipMenuItem_actionPerformed());
		m_fileMenu.add(loadChipMenuItem);

		m_fileMenu.add(m_scriptMenuItem);

		m_fileMenu.addSeparator();
		m_fileMenu.add(m_exitMenuItem);

		m_runMenu.removeAll();
		m_runMenu.add(m_singleStepMenuItem);
		m_runMenu.add(m_ffwdMenuItem);
		m_runMenu.add(m_stopMenuItem);
		m_runMenu.add(m_rewindMenuItem);
		m_runMenu.addSeparator();

		evalMenuItem = new JMenuItem("Eval", KeyEvent.VK_E);
		evalMenuItem.addActionListener(e -> evalMenuItem_actionPerformed());
		m_runMenu.add(evalMenuItem);

		tickTockMenuItem = new JMenuItem("Tick Tock", KeyEvent.VK_C);
		tickTockMenuItem.addActionListener(e -> tickTockMenuItem_actionPerformed());

		m_runMenu.add(tickTockMenuItem);
		m_runMenu.addSeparator();
		m_runMenu.add(m_breakpointsMenuItem);
	}

	/**
	 * Arranges the tool bar.
	 */
	@Override
	protected void arrangeToolBar() {
		m_toolBar.setSize(TOOLBAR_WIDTH, TOOLBAR_HEIGHT);
		m_toolBar.add(loadChipButton);
		m_toolBar.addSeparator(separatorDimension);
		m_toolBar.add(m_singleStepButton);
		m_toolBar.add(m_ffwdButton);
		m_toolBar.add(m_stopButton);
		m_toolBar.add(m_rewindButton);
		m_toolBar.addSeparator(separatorDimension);
		m_toolBar.add(evalButton);
		m_toolBar.add(tickTockButton);
		m_toolBar.addSeparator(separatorDimension);
		m_toolBar.add(m_scriptButton);
		m_toolBar.add(m_breakButton);
		m_toolBar.addSeparator(separatorDimension);
		m_toolBar.add(m_speedSlider);
		m_toolBar.addSeparator(separatorDimension);
		m_toolBar.add(m_animationCombo);
		m_toolBar.add(m_formatCombo);
		m_toolBar.add(m_additionalDisplayCombo);
	}

	@Override
	public void disableEval() {
		evalButton.setEnabled(false);
		evalMenuItem.setEnabled(false);
	}

	@Override
	public void disableTickTock() {
		tickTockButton.setEnabled(false);
		tickTockMenuItem.setEnabled(false);
	}

	@Override
	public void enableEval() {
		evalButton.setEnabled(true);
		evalMenuItem.setEnabled(true);
	}

	@Override
	public void enableTickTock() {
		tickTockButton.setEnabled(true);
		tickTockMenuItem.setEnabled(true);
	}

	/**
	 * Implementing the action of pressing the eval button.
	 */
	public void evalButton_actionPerformed() {
		notifyControllerListeners(HardwareSimulatorControllerEvent.EVAL_CLICKED, null);
	}

	/**
	 * Implementing the action of choosing the eval menu item from the menu bar.
	 */
	public void evalMenuItem_actionPerformed() {
		notifyControllerListeners(HardwareSimulatorControllerEvent.EVAL_CLICKED, null);
	}

	/**
	 * Initializes this component.
	 */
	@Override
	protected void init() {
		super.init();

		settingsWindow = new ChipLoaderFileChooser();
		settingsWindow.addListener(this);

		chipFileChooser = new JFileChooser();
		chipFileChooser.setFileFilter(new HDLFileFilter());

		try {
			initLoadChipButton();
			initTickTockButton();
			initEvalButton();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Initializing the eval button.
	private void initEvalButton() throws URISyntaxException {
		evalIcon = new ImageIcon(Paths.get(ClassLoader.getSystemResource( "calculator2.gif").toURI()).toString());
		evalButton = new MouseOverJButton();
		evalButton.setMaximumSize(new Dimension(39, 39));
		evalButton.setMinimumSize(new Dimension(39, 39));
		evalButton.setPreferredSize(new Dimension(39, 39));
		evalButton.setToolTipText("Eval");
		evalButton.setIcon(evalIcon);
		evalButton.addActionListener(e -> evalButton_actionPerformed());
	}

	// Initializing the load chip button.
	private void initLoadChipButton() throws URISyntaxException {
		loadChipIcon = new ImageIcon(Paths.get(ClassLoader.getSystemResource("chip.gif").toURI()).toString());
		loadChipButton = new MouseOverJButton();
		loadChipButton.setMaximumSize(new Dimension(39, 39));
		loadChipButton.setMinimumSize(new Dimension(39, 39));
		loadChipButton.setPreferredSize(new Dimension(39, 39));
		loadChipButton.setToolTipText("Load Chip");
		loadChipButton.setIcon(loadChipIcon);
		loadChipButton.addActionListener(e -> loadChipButton_actionPerformed());
	}

	// Initializing the tick tock button.
	private void initTickTockButton() throws URISyntaxException {
		tickTockIcon = new ImageIcon(Paths.get(ClassLoader.getSystemResource("clock2.gif").toURI()).toString() );
		tickTockButton = new MouseOverJButton();
		tickTockButton.setMaximumSize(new Dimension(39, 39));
		tickTockButton.setMinimumSize(new Dimension(39, 39));
		tickTockButton.setPreferredSize(new Dimension(39, 39));
		tickTockButton.setToolTipText("Tick Tock");
		tickTockButton.setIcon(tickTockIcon);
		tickTockButton.addActionListener(e -> tickTockButton_actionPerformed());
	}

	/**
	 * Implementing the action of pressing the load chip button.
	 */
	public void loadChipButton_actionPerformed() {
		loadChipPressed();
	}

	/**
	 * Implementing the action of choosing the load chip menu item from the menu
	 * bar.
	 */
	public void loadChipMenuItem_actionPerformed() {
		loadChipPressed();
	}

	// Called when the load chip button is pressed.
	private void loadChipPressed() {
		int returnVal = chipFileChooser.showDialog(this, "Load Chip");
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			notifyControllerListeners(HardwareSimulatorControllerEvent.CHIP_CHANGED,
					chipFileChooser.getSelectedFile().getAbsoluteFile());
		}
	}

	@Override
	public void setWorkingDir(File file) {
		super.setWorkingDir(file);
		chipFileChooser.setCurrentDirectory(file);
	}

	/**
	 * Implementing the action of pressing the tick tock button.
	 */
	public void tickTockButton_actionPerformed() {
		notifyControllerListeners(HardwareSimulatorControllerEvent.TICKTOCK_CLICKED, null);
	}

	/**
	 * Implementing the action of choosing the tick tock menu item from the menu
	 * bar.
	 */
	public void tickTockMenuItem_actionPerformed() {
		notifyControllerListeners(HardwareSimulatorControllerEvent.TICKTOCK_CLICKED, null);
	}
}
