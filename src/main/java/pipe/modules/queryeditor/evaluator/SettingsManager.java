/**
 * SettingsManager
 * 
 * This class implements the settings panel for analysing performance
 * queries. Server and analysis settings can be effected here.
 * 
 * @author Tamas Suto
 * @date 14/01/08
 */

package pipe.modules.queryeditor.evaluator;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import pipe.common.AnalysisSettings;
import pipe.modules.clientCommon.ServerInfo;
import pipe.modules.clientCommon.ServerList;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.evaluator.gui.EvaluationSettingsPanel;
import pipe.modules.queryeditor.evaluator.gui.EvaluationSettingsServerPanel;

public class SettingsManager
{

	private static JDialog							preferencesDialog;
	private static EvaluationSettingsServerPanel	serverSettingsPanel;
	private static EvaluationSettingsPanel			analysisSettingsPanel;

	private static AnalysisSettings					analysisSettings;

	private static void closeWindow()
	{
		SettingsManager.preferencesDialog.setVisible(false);
		SettingsManager.preferencesDialog.dispose();
		SettingsManager.preferencesDialog = null;
	}

	private static boolean extractPreferences()
	{
		final String serverAddress = SettingsManager.serverSettingsPanel.getSelectedServer().getAddress();
		final int serverPort = SettingsManager.serverSettingsPanel.getSelectedServer().getPort();
		final boolean clearCache = SettingsManager.analysisSettingsPanel.clearCache.isSelected();
		final boolean autoTimeRange = SettingsManager.analysisSettingsPanel.autoTimeRange.isSelected();

		final Level serverLoggingLevel = (Level) SettingsManager.analysisSettingsPanel.serverLoggingLevelBox.getSelectedItem();
		final Level clientLoggingLevel = (Level) SettingsManager.analysisSettingsPanel.clientLoggingLevelBox.getSelectedItem();

		final int numProcessors = SettingsManager.serverSettingsPanel.getNumProcessors();
		final String startTimeString = SettingsManager.analysisSettingsPanel.startTime.getText();
		final String endTimeString = SettingsManager.analysisSettingsPanel.endTime.getText();
		final double timeStep = ((Double) SettingsManager.analysisSettingsPanel.timeStep.getValue()).doubleValue();
		final String method = (String) SettingsManager.analysisSettingsPanel.method.getSelectedItem();

		try
		{
			final double startTime = Double.valueOf(startTimeString.trim()).doubleValue();
			final double endTime = Double.valueOf(endTimeString.trim()).doubleValue();
			if (startTime < endTime && startTime >= 0 || startTime == 1 && endTime == 1 && timeStep == 1)
			{
				// Final check that the values are appropriate
				SettingsManager.analysisSettings = new AnalysisSettings(serverAddress,
																		serverPort,
																		clearCache,
																		autoTimeRange,
																		numProcessors,
																		startTime,
																		endTime,
																		timeStep,
																		method,
																		serverLoggingLevel,
																		clientLoggingLevel);
				return true;
			}
			else
			{
				JOptionPane.showMessageDialog(	QueryManager.getEditor().getContentPane(),
												"Please ensure that you have specified valid start and end times.",
												"Warning",
												JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		catch (final NumberFormatException nfe)
		{
			// This is really bad, do not copy this pattern
			return false;
		}
	}

	public static AnalysisSettings getAnalysisSettings()
	{
		if (SettingsManager.analysisSettings == null)
		{
			SettingsManager.setupDefaultPreferences();
		}
		return SettingsManager.analysisSettings;
	}

	public static void preferenceManagerDialog()
	{
		// Get AnalysisSettings, so that the fields can be populated with either
		// default or user-defined values
		SettingsManager.analysisSettings = SettingsManager.getAnalysisSettings();

		// Set up dialog
		SettingsManager.preferencesDialog = new JDialog(QueryManager.getEditor(), "Settings", true);
		final Container contentPane = SettingsManager.preferencesDialog.getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));

		// Add server settings panel
		SettingsManager.serverSettingsPanel = new EvaluationSettingsServerPanel(SettingsManager.preferencesDialog);
		contentPane.add(SettingsManager.serverSettingsPanel.getPanel());

		// Add analysis settings panel
		SettingsManager.analysisSettingsPanel = new EvaluationSettingsPanel();
		contentPane.add(SettingsManager.analysisSettingsPanel.getPanel());

		// Add OK button
		final JButton okButton = new JButton("OK");
		final ActionListener okButtonListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				if (SettingsManager.extractPreferences())
				{
					SettingsManager.closeWindow();
				}
			}
		};
		okButton.addActionListener(okButtonListener);
		contentPane.add(okButton);

		SettingsManager.preferencesDialog.pack();
		SettingsManager.preferencesDialog.setLocationRelativeTo(null);
		SettingsManager.preferencesDialog.setVisible(true);
	}

	public static void resetPreferences()
	{
		SettingsManager.preferencesDialog = null;
		SettingsManager.serverSettingsPanel = null;
		SettingsManager.analysisSettingsPanel = null;
		SettingsManager.analysisSettings = null;
	}

	private static void setupDefaultPreferences()
	{
		// Server settings
		final ServerList servers = new ServerList();
		final ServerInfo defaultServer = servers.get(0);
		final String defaultServerAddress = defaultServer.getAddress();
		final int defaultServerPort = defaultServer.getPort();
		final boolean defaultClearCache = false;
		final boolean defaultAutoTimeRange = true;

		final Level serverLoggingLevel = Level.OFF;
		final Level clientLoggingLevel = Level.OFF;

		// Analysis settings
		final double startTime = 1.0;
		final double endTime = 1.0;
		final double timeStep = 1.0;
		final String method = "Laguerre";
		final int noOfProcessors = 24;

		// Store default settings in analysisSettings
		SettingsManager.analysisSettings = new AnalysisSettings(defaultServerAddress,
																defaultServerPort,
																defaultClearCache,
																defaultAutoTimeRange,
																noOfProcessors,
																startTime,
																endTime,
																timeStep,
																method,
																serverLoggingLevel,
																clientLoggingLevel);
	}

}
