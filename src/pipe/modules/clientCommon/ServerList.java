package pipe.modules.clientCommon;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

/**
 * This class loads / save the list of servers from the file servers.lst
 * It provides methods for adding and removing servers from the list and 
 * can return the list in the form of a JComboBox
 * 
 * @author Barry Kearns
 * @date August 2007
 *
 */


public class ServerList {
	
	private final ArrayList<ServerInfo> serverInfos = new ArrayList<ServerInfo>();
	private File serverFile = null;

	public ServerList() {
		this("servers.lst");
	}
	
	
	private ServerList(String filename) {
		try{
			// Load the read the list of servers and ports from the file
			serverFile = new File(filename);
			BufferedReader serverList = new BufferedReader(new FileReader(serverFile));

			String currentLine, server;
			String[] components;
			int port;

			while ((currentLine = serverList.readLine()) != null) { 		        	             
				components = currentLine.split(":");
				if (components.length == 2) {
					server = components[0].trim();
					try{
						port =  Integer.parseInt(components[1].trim());
					} catch (NumberFormatException e) {
						System.out.println("Invalid port number: " + components[1]);
						continue; 
					}
					add(server, port);
				}
			}
			serverList.close();
		} catch (FileNotFoundException e) {
			System.err.println("Error finding server list file " + filename );
		} catch (IOException e) {} 
	}
	
	public ServerInfo get(int index) {
		return serverInfos.get(index);		
	}
	
	int getSize() {
		return serverInfos.size();
	}
	
	public boolean containsEntry(String serverAddr, int serverPort) {
		Iterator<ServerInfo> i = serverInfos.iterator();
		while (i.hasNext()) {
			ServerInfo server = i.next();
			String address = server.getAddress();
			int port = server.getPort();
			if (address.equals(serverAddr) && port == serverPort)
				return true;
		}
		return false;
	}
	
	public void add(String newAddr, int newPort) {
		serverInfos.add(new ServerInfo(newAddr, newPort));
	}
	
	public void remove(String itemToRemove) {
		ServerInfo toBeRemoved = null;
        Iterator<ServerInfo> i = serverInfos.iterator();
		while(i.hasNext()) {
			ServerInfo server = i.next();
			String serverAddress = server.getAddress();
			int port = server.getPort();
			String serverAddressWithPort = serverAddress + " : " + port;
			if (serverAddressWithPort.equals(itemToRemove))
				toBeRemoved = server;
		}
		if (toBeRemoved != null)
			serverInfos.remove(toBeRemoved);
	}
	
	public void save() {
		try {
			PrintWriter newServerFile = new PrintWriter(new BufferedWriter(new FileWriter(serverFile)));	
			for(int i=0; i< getSize(); i++)
				newServerFile.println(get(i).address + ":" + get(i).port);
			newServerFile.close();				
		}
		catch (IOException ioe) {
			System.out.println("Error saving new server information. " + ioe);
		}	 
	}
	
	public JComboBox toJComboBox(String selectedItem) {
		JComboBox combo = new JComboBox();
		DefaultComboBoxModel model = getComboBoxModel(selectedItem);
		combo.setModel(model);
		return combo;
	}
	
	public DefaultComboBoxModel getComboBoxModel(String selectedItem) {
		DefaultComboBoxModel model = new DefaultComboBoxModel();	
		for (int i=0; i< serverInfos.size(); i++) {
			String serverInfo = serverInfos.get(i).address + " : " + serverInfos.get(i).port;
			model.addElement(serverInfo);
			if (i == 0)
				model.setSelectedItem(serverInfo);
		}	
		if (selectedItem != null) 
			model.setSelectedItem(selectedItem);
		return model;
	}

}