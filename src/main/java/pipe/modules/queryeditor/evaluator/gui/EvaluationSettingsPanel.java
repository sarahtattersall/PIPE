package pipe.modules.queryeditor.evaluator.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.logging.Level;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import pipe.common.AnalysisSettings;
import pipe.modules.queryeditor.evaluator.SettingsManager;

public class EvaluationSettingsPanel implements ItemListener
{

	private final JComponent	settingsPanel;

	public final JTextField			startTime;
	public final JTextField			endTime;
	public final JSpinner				timeStep;
	public final JComboBox			method;
	public final JCheckBox			autoTimeRange;
	public final JCheckBox			clearCache;
	public final JComboBox			serverLoggingLevelBox;
    public final JComboBox clientLoggingLevelBox;

	public EvaluationSettingsPanel() {

		this.settingsPanel = Box.createVerticalBox();

		final JPanel analysisSettingsPanel = new JPanel();
		analysisSettingsPanel.setBorder((new TitledBorder(new EtchedBorder(), "Analysis")));
		analysisSettingsPanel.setLayout(new GridLayout(2, 2));

		final JPanel startEndTime = new JPanel(new GridLayout(2, 2));
		startEndTime.add(new JLabel("Start Time:"));
		this.startTime = new JTextField(4);
		startEndTime.add(this.startTime);
		startEndTime.add(new JLabel("End Time:"));
		this.endTime = new JTextField(4);
		startEndTime.add(this.endTime);
		startEndTime.setMaximumSize(new Dimension(Integer.MAX_VALUE, startEndTime.getPreferredSize().height));

		// Time Step / Method panel
		final JPanel timeMethodPanel = new JPanel(new GridLayout(2, 2));
		timeMethodPanel.add(new JLabel("Time Step:"));
		this.timeStep = new JSpinner(new SpinnerNumberModel(0.10, 0.0, 100, 0.1));
		timeMethodPanel.add(this.timeStep);
		timeMethodPanel.add(new JLabel("Method:"));
		this.method = new JComboBox();
		this.method.addItem("Laguerre");
		this.method.addItem("Euler");
		timeMethodPanel.add(this.method);

		// Automatic settings
		this.autoTimeRange = new JCheckBox("Automatically determine time range");
		this.autoTimeRange.addItemListener(this);

		this.clearCache = new JCheckBox("Clear cached values from server");

		final Level[] levels = {Level.OFF,
				Level.ALL,
				Level.CONFIG,
				Level.FINE,
				Level.FINER,
				Level.FINEST,
				Level.INFO,
				Level.SEVERE,
				Level.WARNING};
		this.clientLoggingLevelBox = new JComboBox(levels);
		this.serverLoggingLevelBox = new JComboBox(levels);

		final Box loggingLevelBox = Box.createHorizontalBox();
		loggingLevelBox.setBorder(new TitledBorder(new EtchedBorder(), "Logging"));
		loggingLevelBox.add(new JLabel("Client Logging Level"));
		loggingLevelBox.add(Box.createGlue());
		loggingLevelBox.add(this.clientLoggingLevelBox);
		loggingLevelBox.add(Box.createGlue());
		loggingLevelBox.add(new JLabel("Server Logging Level"));
		loggingLevelBox.add(Box.createGlue());
		loggingLevelBox.add(this.serverLoggingLevelBox);

		// Add to main analysis panel
		analysisSettingsPanel.add(startEndTime);
		analysisSettingsPanel.add(timeMethodPanel);
		analysisSettingsPanel.add(this.clearCache);
		analysisSettingsPanel.add(this.autoTimeRange);

		analysisSettingsPanel.setMaximumSize(new Dimension(	Integer.MAX_VALUE,
															analysisSettingsPanel.getPreferredSize().height));

		// Extract values from analysisSettings and populate fields accordingly
		final AnalysisSettings analysisSettings = SettingsManager.getAnalysisSettings();
		final double extractedStartTime = analysisSettings.startTime;
		final double extractedEndTime = analysisSettings.endTime;
		final double extractedTimeStep = analysisSettings.timeStep;
		final String extractedMethod = analysisSettings.inversionMethod;
		final boolean extractedAutoTimeRange = analysisSettings.autoTimeRange;
		final boolean extractedClearCache = analysisSettings.clearCache;

		final Level clientLoggingLevel = analysisSettings.clientLoggingLevel;
		final Level serverLoggingLevel = analysisSettings.serverLoggingLevel;

		this.startTime.setText(Double.toString(extractedStartTime));
		this.endTime.setText(Double.toString(extractedEndTime));
		this.timeStep.setValue(extractedTimeStep);
		this.method.setSelectedItem(extractedMethod);

		this.clientLoggingLevelBox.setSelectedItem(clientLoggingLevel);
		this.serverLoggingLevelBox.setSelectedItem(serverLoggingLevel);

		if (extractedClearCache)
		{
			this.clearCache.setSelected(extractedClearCache);
		}
		if (extractedAutoTimeRange)
		{
			this.autoTimeRange.setSelected(true);
			this.startTime.setEnabled(false);
			this.endTime.setEnabled(false);
			this.timeStep.setEnabled(false);
		}

		this.settingsPanel.add(analysisSettingsPanel);
		this.settingsPanel.add(Box.createGlue());
		this.settingsPanel.add(loggingLevelBox);
	}

	public JComponent getPanel()
	{
		return this.settingsPanel;
	}

	public void itemStateChanged(final ItemEvent e)
	{
		final Object source = e.getItemSelectable();

		if (source == this.autoTimeRange)
		{
			if (this.autoTimeRange.isSelected())
			{
				this.startTime.setEnabled(false);
				this.endTime.setEnabled(false);
				this.timeStep.setEnabled(false);
			}
			else
			{
				this.startTime.setEnabled(true);
				this.endTime.setEnabled(true);
				this.timeStep.setEnabled(true);
			}
		}
	}

}
