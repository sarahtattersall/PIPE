package pipe.actions;

import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.ApplicationSettings;
import pipe.models.PetriNet;
import pipe.petrinet.unfold.Expander;
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
    final PipeApplicationView pipeApplicationView;
    final PipeApplicationController pipeApplicationController;

    public UnfoldAction(String name, String tooltip, String keystroke, PipeApplicationView pipeApplicationView,
                        PipeApplicationController pipeApplicationController)
    {
        super(name, tooltip, keystroke);
        this.pipeApplicationView = pipeApplicationView;
        this.pipeApplicationController = pipeApplicationController;
    }

    public void actionPerformed(ActionEvent e)
    {
        PetriNetController controller = pipeApplicationController.getActivePetriNetController();

        //TODO: SHOW MESSAGES
//        if(controller.getNetTokens().size() > 1 && controller.hasFunctionalRatesOrWeights()){
//        	JOptionPane.showMessageDialog(null, "This is CGSPN. The analysis will only apply to default color (black). \r\n"+"This net contains functional rates or weights. The unfolder will replace these rates or weights with " +
//        			"their current constant values.",
//					"Information", JOptionPane.INFORMATION_MESSAGE);
//        }else if(!(pipeApplicationView.getCurrentPetriNetView().getEnabledTokenClassNumber()>1) && pipeApplicationView.getCurrentPetriNetView().hasFunctionalRatesOrWeights()){
//        	JOptionPane.showMessageDialog(null, "This net contains functional rates or weights. The unfolder will replace these rates or weights with " +
//        			"their current constant values.",
//					"Information", JOptionPane.INFORMATION_MESSAGE);
//        }else if((pipeApplicationView.getCurrentPetriNetView().getEnabledTokenClassNumber()>1) && !pipeApplicationView.getCurrentPetriNetView().hasFunctionalRatesOrWeights()){
//        	JOptionPane.showMessageDialog(null, "This is CGSPN. The analysis will only apply to default color (black). ",
//					"Information", JOptionPane.INFORMATION_MESSAGE);
//        }

        PetriNet petriNet = controller.getPetriNet();
        Expander expander = new Expander(petriNet);
        PetriNet unfolded = expander.unfold();
        pipeApplicationController.createNewTab(unfolded, pipeApplicationView);
//        ApplicationSettings.getApplicationController().createNewTabFromFile(
//                expander.saveAsXml(unfolded), pipeApplicationView, false);
    }
}
