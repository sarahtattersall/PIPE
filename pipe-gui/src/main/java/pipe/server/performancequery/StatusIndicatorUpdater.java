/**
 * 
 */
package pipe.server.performancequery;

/**
 * @author dazz
 * 
 */
public interface StatusIndicatorUpdater
{
	void sendLine(String ln);

	void updateNodeStatus(final String status, final String nodeID);
}
