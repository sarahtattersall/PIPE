package pipe.modules.clientCommon;

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
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 * This class creates a Server list panel providing
 * a combo-box for selecting the server and buttons to add / remove servers from the list.
 * The list of servers is loaded from ServerList
 * 
 * @author Barry Kearns
 * @date September 2007
 */


public class ServerPanel
{
	private final JDialog parentDialog;
	private final JPanel serverPanel;
	private final ServerList serverlist;
	private final JComboBox serverListCombo;
	private final JButton addButton;
    private JButton removeButton;

	//  Pop up  dialog compenents
	private JDialog addServer;
	private JButton cancelButton, okButton;
	private JTextField servAddrBox, servPortBox;
	
	
	// Create new server panel
	public ServerPanel(JDialog parent)
	{
		parentDialog = parent;
		
		serverPanel = new JPanel();
		serverPanel.setBorder((new TitledBorder(new EtchedBorder(),"Server Settings")) );
		
		// Load server list and generate combo-box
		serverlist = new ServerList();
		serverListCombo = serverlist.toJComboBox(null);
		
		// Add server button
		addButton = new JButton();
		addButton.setText("Add");
		addButton.setMnemonic(KeyEvent.VK_A);
        ActionListener serverButtons = new ActionListener()
        {

            public void actionPerformed(ActionEvent event)
            {
                if(event.getSource() == addButton)
                {
                    addServer = addServerPanel();
                    addServer.setVisible(true);
                }

                else if(event.getSource() == removeButton)
                {
                    String selectedItem = (String) serverListCombo.getSelectedItem();
                    serverlist.remove(selectedItem);
                    serverlist.save();
                    serverListCombo.removeItem(selectedItem);
                }

            }

        };
        addButton.addActionListener(serverButtons);
		
		// Remove server button
		removeButton = new JButton();
		removeButton.setText("Remove");
		removeButton.setMnemonic(KeyEvent.VK_R);
		removeButton.addActionListener(serverButtons);
		
		// Add components to panel
		serverPanel.add(serverListCombo);
		serverPanel.add(addButton);
		serverPanel.add(removeButton);
		
		serverPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE,serverPanel.getPreferredSize().height));
	}
	
	

	  public JPanel getPanel()
	  {		    
			return serverPanel;
	  }
	  
	  public int getSelectedServerIndex()
	  {
		  return serverListCombo.getSelectedIndex();
	  }
	  
	  public ServerInfo getSelectedServer()
	  {
		  return serverlist.get( serverListCombo.getSelectedIndex() );
	  }


    /**
	 * This method creates the JDialog used as for adding new Servers to the drop combobox.
	 * Components in each of the three sections are added to the component panel
	 * which in turn is added to global panel to make up the dialog box
     * @return
     */
	 
	 private JDialog addServerPanel() 
	 {
		 JDialog addServ = new JDialog(parentDialog, "Add Server", true);
		 JPanel panel =  new JPanel(new GridLayout(3, 1));
		 JPanel componentPanel;
		  
		 // Add 'Server Address' panel
		 componentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		 
		 JLabel servAddr = new JLabel( "Server Address: " );
		 servAddrBox = new JTextField(15);
		 componentPanel.add(servAddr);
		 componentPanel.add(servAddrBox);
		 panel.add(componentPanel);
		 
		 // Add 'Server Port' panel
		 componentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		 JLabel servPort = new JLabel("Port: ");
		 servPortBox = new JTextField(8);
		 componentPanel.add(servPort);
		 componentPanel.add(servPortBox);
		 panel.add(componentPanel);
		 	 
		 
		 // Add button panel
		 componentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		 
		 cancelButton = new JButton("Cancel");
		 cancelButton.setMnemonic(KeyEvent.VK_C);
		 cancelButton.addActionListener(addServClick);
		 componentPanel.add(cancelButton);
		 
		 okButton = new JButton("OK");
		 okButton.addActionListener(addServClick);
		 okButton.setMnemonic(KeyEvent.VK_O);
		 componentPanel.add(okButton);
		 panel.add(componentPanel);
		 

		 // Set up dialog with new panel
		 addServ.add(panel);
		 addServ.pack();
		 addServ.setResizable(false);
		 addServ.setLocationRelativeTo(null);
		 
		 
		 // Return Dialog
		 return addServ;
	 }
	 
	 
	 /**
	  * This action listener responds to the buttons used in the "Add Server" pop up dialog
	  * i.e the Cancel / OK buttons 
	  */

     private final ActionListener addServClick = new ActionListener() {
		 public void actionPerformed(ActionEvent event)
		 {
			 if (event.getSource() == cancelButton)
			 {
				 closeWindow();
			 }
			 
			 else if (event.getSource() == okButton)
			 {
				 String servAdd = "";
				 int portNum;
				 
				 servAdd = servAddrBox.getText();
				 
				 try{
					 portNum = Integer.parseInt( servPortBox.getText() );
					 
					 serverlist.add(servAdd, portNum);
					 serverlist.save();
					 
					 String serverDislayName = servAdd + " : " + portNum;
					 serverListCombo.addItem(serverDislayName);
					 serverListCombo.setSelectedItem(serverDislayName);
					 
					 closeWindow();				 
				 }
				 catch (Exception exp)
				 {
					 System.out.println("Invalid Port number!");
				 }
				 
			
			 }
		 }
		 
		 private void closeWindow()
		 {
				addServer.setVisible(false);
				addServer.dispose();		 
		 }
	 };  

}


