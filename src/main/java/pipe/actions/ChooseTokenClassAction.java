package pipe.actions;

import pipe.gui.ApplicationSettings;
import pipe.views.PipeApplicationView;
import pipe.views.TokenView;

import java.awt.event.ActionEvent;
import java.util.LinkedList;

/**
 * @author Alex Charalambous, June 2010: Handles the combo box used to
 *         select the current token class in use.
 */
public class ChooseTokenClassAction extends GuiAction
{

    public ChooseTokenClassAction(String name, String tooltip, String keystroke)
    {
        super(name, tooltip, keystroke);
    }

    public void actionPerformed(ActionEvent e)
    {
        PipeApplicationView pipeApplicationView = ApplicationSettings.getApplicationView();
        String currentSelection = (String) pipeApplicationView.tokenClassComboBox.getSelectedItem();
        LinkedList<TokenView> tokenViews = pipeApplicationView.getCurrentPetriNetView().getTokenViews();
        for(TokenView tc : tokenViews)
        {
            if(tc.getID().equals(currentSelection))
            {
                pipeApplicationView.getCurrentPetriNetView().setActiveTokenView(tc);
                break;
            }
        }
    }
}
