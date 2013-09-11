package pipe.modules.queryeditor.evaluator.gui;

import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import pipe.common.AnalysisSettings;
import pipe.modules.queryeditor.evaluator.SettingsManager;

public class EvaluationSettingsServerPanel extends ServerSettingsBasicPanel
{

    private final JSpinner	processorNo;

	public EvaluationSettingsServerPanel(JDialog parent) {
		// Use parent to construct basic server panel
		super(parent);
        JPanel serverPanel = super.getPanel();

		// Extract processor number value from analysisSettings
		final AnalysisSettings analysisSettings = SettingsManager.getAnalysisSettings();
		final int noOfProcessorsExtracted = analysisSettings.numProcessors;

		// Add additional processor entry components
		serverPanel.add(new JLabel("No. Processors"));
		this.processorNo = new JSpinner(new SpinnerNumberModel(noOfProcessorsExtracted, 2, 10000, 1));
		serverPanel.add(this.processorNo);
		serverPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                                                 serverPanel.getPreferredSize().height));
	}

	public int getNumProcessors() throws NumberFormatException
	{
		// Test that it's a valid int, exception raise if not
		final Integer value = (Integer) this.processorNo.getValue();
		return value.intValue();
	}
}