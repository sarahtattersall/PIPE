/**
 * 
 */
package pipe.modules.queryeditor.evaluator;

import java.io.IOException;

/**
 * @author dazz
 * 
 */
interface CommunicatorStarter
{
	public boolean hasStarted();

	public void startCommunicator() throws IOException;
}
