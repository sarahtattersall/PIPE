/**
 * ServerSettingsBasicPanel
 * 
 * This class creates a Server list panel providing a combo box for selecting the 
 * server and buttons to add / remove servers from the list. The list of servers 
 * is loaded from ServerList.
 * 
 * @author Barry Kearns
 * @author Tamas Suto
 * @date 15/01/08
 */

package pipe.modules.queryeditor.evaluator.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import pipe.common.AnalysisSettings;
import pipe.modules.clientCommon.ServerInfo;
import pipe.modules.clientCommon.ServerList;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.evaluator.SettingsManager;

public class ServerSettingsBasicPanel
{

	private final JDialog		parentDialog;
	private final JPanel		serverPanel;
	private final ServerList	serverlist;
	private final JComboBox		serverListCombo;
	private final JButton		addButton, removeButton;

	// Pop up dialog compenents
	private JDialog				addServer;
	private JButton				cancelButton, okButton;
	private JTextField			servAddrBox, servPortBox;

    private final ActionListener				addServClick	= new ActionListener()
												{
													public void actionPerformed(ActionEvent event)
													{
														if (event.getSource() == ServerSettingsBasicPanel.this.cancelButton)
														{
															this.closeWindow();
														}
														else if (event.getSource() == ServerSettingsBasicPanel.this.okButton)
														{
															String servAdd = "";
															int portNum;

															servAdd = ServerSettingsBasicPanel.this.servAddrBox.getText();

															portNum = Integer.parseInt(ServerSettingsBasicPanel.this.servPortBox.getText());

															if (!ServerSettingsBasicPanel.this.serverlist.containsEntry(servAdd,
																														portNum))
															{
																ServerSettingsBasicPanel.this.serverlist.add(	servAdd,
																												portNum);
																ServerSettingsBasicPanel.this.serverlist.save();
																String serverDislayName = servAdd + " : " +
																							portNum;
																ServerSettingsBasicPanel.this.serverListCombo.addItem(serverDislayName);
																ServerSettingsBasicPanel.this.serverListCombo.setSelectedItem(serverDislayName);
																this.closeWindow();
															}
															else
															{
																JOptionPane.showMessageDialog(	QueryManager.getEditor()
																											.getContentPane(),
																								"This entry already exists in the server list.\n"
																								+ "Please choose another server and port.",
																								"Warning",
																								JOptionPane.ERROR_MESSAGE);
															}

														}
													}

													private void closeWindow()
													{
														ServerSettingsBasicPanel.this.addServer.setVisible(false);
														ServerSettingsBasicPanel.this.addServer.dispose();
													}
												};

	// Create new server panel
    ServerSettingsBasicPanel(JDialog parent) {
		// Extract selected server and specified port from analysisSettings
		final AnalysisSettings analysisSettings = SettingsManager.getAnalysisSettings();
		final String extractedServerAddress = analysisSettings.serverAddress;
		final int extractedServerPort = analysisSettings.serverPort;
		final String extractedServerEntry = extractedServerAddress + " : " + extractedServerPort;

		this.parentDialog = parent;
		this.serverPanel = new JPanel();
		this.serverPanel.setBorder((new TitledBorder(new EtchedBorder(), "Server")));

		// Load server list and generate combo-box
		this.serverlist = new ServerList();
		this.serverListCombo = this.serverlist.toJComboBox(extractedServerEntry);

		// Add server button
		this.addButton = new JButton();
		this.addButton.setText("Add");
		this.addButton.setMnemonic(KeyEvent.VK_A);
        ActionListener serverButtons = new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                if(event.getSource() == ServerSettingsBasicPanel.this.addButton)
                {
                    ServerSettingsBasicPanel.this.addServer = ServerSettingsBasicPanel.this.addServerPanel();
                    ServerSettingsBasicPanel.this.addServer.setVisible(true);
                }
                else if(event.getSource() == ServerSettingsBasicPanel.this.removeButton)
                {
                    String selectedItem = (String) ServerSettingsBasicPanel.this.serverListCombo.getSelectedItem();
                    ServerSettingsBasicPanel.this.serverlist.remove(selectedItem);
                    ServerSettingsBasicPanel.this.serverlist.save();
                    ServerSettingsBasicPanel.this.updateServerComboBox();
                }
            }
        };
        this.addButton.addActionListener(serverButtons);

		// Remove server button
		this.removeButton = new JButton();
		this.removeButton.setText("Remove");
		this.removeButton.setMnemonic(KeyEvent.VK_R);
		this.removeButton.addActionListener(serverButtons);

		// Add components to panel
		this.serverPanel.add(this.serverListCombo);
		this.serverPanel.add(this.addButton);
		this.serverPanel.add(this.removeButton);
		this.serverPanel.setMaximumSize(new Dimension(	Integer.MAX_VALUE,
														this.serverPanel.getPreferredSize().height));
	}

	private JDialog addServerPanel()
	{
		final JDialog addServ = new JDialog(this.parentDialog, "Add Server", true);
		final JPanel panel = new JPanel(new GridLayout(3, 1));
		JPanel componentPanel;

		// Add 'Server Address' panel
		componentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		final JLabel servAddr = new JLabel("Server Address: ");
		this.servAddrBox = new JTextField(15);
		componentPanel.add(servAddr);
		componentPanel.add(this.servAddrBox);
		panel.add(componentPanel);

		// Add 'Server Port' panel
		componentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		final JLabel servPort = new JLabel("Port: ");
		this.servPortBox = new JTextField(8);
		componentPanel.add(servPort);
		componentPanel.add(this.servPortBox);
		panel.add(componentPanel);

		// Add button panel
		componentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		this.okButton = new JButton("OK");
		this.okButton.addActionListener(this.addServClick);
		this.okButton.setMnemonic(KeyEvent.VK_O);
		componentPanel.add(this.okButton);
		panel.add(componentPanel);

		this.cancelButton = new JButton("Cancel");
		this.cancelButton.setMnemonic(KeyEvent.VK_C);
		this.cancelButton.addActionListener(this.addServClick);
		componentPanel.add(this.cancelButton);

		// Set up dialog with new panel
		addServ.add(panel);
		addServ.pack();
		addServ.setResizable(false);
		addServ.setLocationRelativeTo(null);

		// Return Dialog
		return addServ;
	}

	public JPanel getPanel()
	{
		return this.serverPanel;
	}

	public ServerInfo getSelectedServer()
	{
		return this.serverlist.get(this.serverListCombo.getSelectedIndex());
	}

	public int getSelectedServerIndex()
	{
		return this.serverListCombo.getSelectedIndex();
	}

	private void updateServerComboBox()
	{
		this.serverListCombo.setModel(this.serverlist.getComboBoxModel(null));
	}

}
