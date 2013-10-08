package pipe.server;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.logging.Level;
import pipe.server.performancequery.ClientUpdater;
import pipe.server.performancequery.ServerLoggingHandler;


public class CommunicationsManager {

	// Static ports 
	private static final int serverPort = 55500;
	public static final int statusPort = serverPort + 1;
	public static final int loggingPort = statusPort + 1;
	
	private static int bindAttempts = 0;
	
	public static ClientUpdater clientUpdater;

	
	public static void safeBind(ServerSocketChannel server, int port, String source) throws IOException {
		final InetSocketAddress address = new InetSocketAddress(port);

		if (ServerLoggingHandler.logger != null) {
			ServerLoggingHandler.logger.log(Level.INFO, "*** Attempting bind to port " + port + "(source " + source + ")");
		}
		
		try {
			server.socket().bind(address);
			bindAttempts = 0;
		} catch (BindException e) {
			if (bindAttempts < 10) {
				bindAttempts ++;
				try {
					Thread.currentThread().sleep(1000);
				} catch (InterruptedException exc) {
					// do nothing
				}
				safeBind(server, port, source);	
			}
			else {
				if (ServerLoggingHandler.logger != null) {
					ServerLoggingHandler.logger.log(Level.SEVERE, "*** Could not bind to port " + port + "(source " + source + ") ", e);
				}
			}
		}
	}
	
}
