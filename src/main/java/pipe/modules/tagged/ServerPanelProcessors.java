package pipe.modules.tagged;

import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import pipe.modules.clientCommon.ServerPanel;

/**
 * This class extends the features of ServerPanel by
 * allowing the user to select the number of processors
 * they will use for calculating the passage time distribution 
 * 
 * @author Barry Kearns
 * @date September 2007
 */

class ServerPanelProcessors extends ServerPanel
{
    private final JSpinner processorNo;

	public ServerPanelProcessors(JDialog parent)
	{
		// Use parent to construct basic server panel
		super(parent);
        JPanel serverPanel = super.getPanel();
		
		// Add additional processor entry components
		serverPanel.add(new JLabel("No. Processors"));
		
		processorNo = new JSpinner(new SpinnerNumberModel(8, 2, 10000, 1));
		serverPanel.add(processorNo);
		
		
		serverPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, serverPanel.getPreferredSize().height));
	}
	
	// Returns the number of processors entered in the spinner
	public int getNumProcessors() throws NumberFormatException
	{
		// Test that it's a valid int, exception raise if not
		Integer value =	(Integer)processorNo.getValue();
		
		// Return string representation
		return value.intValue();
	}

}
