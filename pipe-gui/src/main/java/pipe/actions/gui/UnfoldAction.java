package pipe.actions.gui;

import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.models.petrinet.PetriNet;
import pipe.petrinet.unfold.Expander;
import pipe.views.PipeApplicationView;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * Unfolds a coloured Petri net to an ordinary Petri net.
 */
public class UnfoldAction extends GuiAction {
    final PipeApplicationView pipeApplicationView;

    final PipeApplicationController pipeApplicationController;

    public UnfoldAction(PipeApplicationView pipeApplicationView, PipeApplicationController pipeApplicationController) {
        super("unfoldAction", "Unfold Petri Net", KeyEvent.VK_U,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | InputEvent.SHIFT_DOWN_MASK);
        this.pipeApplicationView = pipeApplicationView;
        this.pipeApplicationController = pipeApplicationController;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PetriNetController controller = pipeApplicationController.getActivePetriNetController();

        //TODO: SHOW MESSAGES
        //        if(controller.getNetTokens().size() > 1 && controller.hasFunctionalRatesOrWeights()){
        //        	JOptionPane.showMessageDialog(null, "This is CGSPN. The analysis will only apply to default color (black). \r\n"+"This net contains functional rates or weights. The unfolder will replace these rates or weights with " +
        //        			"their current constant tokens.",
        //					"Information", JOptionPane.INFORMATION_MESSAGE);
        //        }else if(!(pipeApplicationView.getCurrentPetriNetView().getEnabledTokenClassNumber()>1) && pipeApplicationView.getCurrentPetriNetView().hasFunctionalRatesOrWeights()){
        //        	JOptionPane.showMessageDialog(null, "This net contains functional rates or weights. The unfolder will replace these rates or weights with " +
        //        			"their current constant tokens.",
        //					"Information", JOptionPane.INFORMATION_MESSAGE);
        //        }else if((pipeApplicationView.getCurrentPetriNetView().getEnabledTokenClassNumber()>1) && !pipeApplicationView.getCurrentPetriNetView().hasFunctionalRatesOrWeights()){
        //        	JOptionPane.showMessageDialog(null, "This is CGSPN. The analysis will only apply to default color (black). ",
        //					"Information", JOptionPane.INFORMATION_MESSAGE);
        //        }

        PetriNet petriNet = controller.getPetriNet();
        Expander expander = new Expander(petriNet);
        PetriNet unfolded = expander.unfold();
        //TODO: REIMPLEMENT
//        pipeApplicationController.createNewTab(unfolded, pipeApplicationView);
    }
}
