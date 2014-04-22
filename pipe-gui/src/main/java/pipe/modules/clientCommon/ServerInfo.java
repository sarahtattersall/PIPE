package pipe.modules.clientCommon;

public class ServerInfo
{
	final String address;
	final int port;
	
	public ServerInfo(String address, int port)
	{
		this.address = address;
		this.port = port;
	}
	
	public String getAddress()
	{
		return address;
	}
	
	public int getPort()
	{
		return port; 
	}
}