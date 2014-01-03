package pipe.actions;

import pipe.gui.ApplicationSettings;
import pipe.utilities.Expander;
import pipe.views.PetriNetView;
import pipe.views.PipeApplicationView;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Alex Charalambous, June 2010: Unfolds a coloured Petri net
 *         to an ordinary Petri net.
 */
public class UnfoldAction extends GuiAction
{

    public UnfoldAction(String name, String tooltip, String keystroke)
    {
        super(name, tooltip, keystroke);
    }

    public void actionPerformed(ActionEvent e)
    {
        PipeApplicationView pipeApplicationView = ApplicationSettings.getApplicationView();
        if(pipeApplicationView.getCurrentPetriNetView().getEnabledTokenClassNumber()>1 && pipeApplicationView.getCurrentPetriNetView().hasFunctionalRatesOrWeights()){
        	JOptionPane.showMessageDialog(null, "This is CGSPN. The analysis will only apply to default color (black). \r\n"+"This net contains functional rates or weights. The unfolder will replace these rates or weights with " +
        			"their current constant values.",
					"Information", JOptionPane.INFORMATION_MESSAGE);
        }else if(!(pipeApplicationView.getCurrentPetriNetView().getEnabledTokenClassNumber()>1) && pipeApplicationView.getCurrentPetriNetView().hasFunctionalRatesOrWeights()){
        	JOptionPane.showMessageDialog(null, "This net contains functional rates or weights. The unfolder will replace these rates or weights with " +
        			"their current constant values.",
					"Information", JOptionPane.INFORMATION_MESSAGE);
        }else if((pipeApplicationView.getCurrentPetriNetView().getEnabledTokenClassNumber()>1) && !pipeApplicationView.getCurrentPetriNetView().hasFunctionalRatesOrWeights()){
        	JOptionPane.showMessageDialog(null, "This is CGSPN. The analysis will only apply to default color (black). ",
					"Information", JOptionPane.INFORMATION_MESSAGE);
        }
        Expander expander = new Expander(ApplicationSettings.getApplicationView().getCurrentPetriNetView());
        PetriNetView unfolded = expander.unfold();
        ApplicationSettings.getApplicationController().createNewTabFromFile(
                expander.saveAsXml(unfolded), false);
    }
}
