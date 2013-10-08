package pipe.actions;

import pipe.gui.ApplicationSettings;
import pipe.historyActions.HistoryItem;
import pipe.views.GroupTransitionView;
import pipe.views.PetriNetView;
import pipe.views.PipeApplicationView;
import pipe.views.TransitionView;

import java.awt.event.ActionEvent;
import java.util.LinkedList;

/**
 * @author Alex Charalambous, June 2010: Ungroups any transitions that have
 *         been previously grouped. Only does anything if this is a
 *         coloured petri net
 */
public class UngroupTransitionsAction extends GuiAction
{

    public UngroupTransitionsAction(String name, String tooltip, String keystroke)
    {
        super(name, tooltip, keystroke);
    }

    public void actionPerformed(ActionEvent e)
    {
        /* NOTE: With the current implementation we must clear the undo
        * history before performing this action otherwise undoing a
        * previous grouping could cause a mixup. In the future this
        * should all be done as a single undo transaction.
        */
        PipeApplicationView applicationView = ApplicationSettings.getApplicationView();
        applicationView.getCurrentTab().getHistoryManager().clear();
        LinkedList<GroupTransitionView> transitionsToUngroup = new LinkedList<GroupTransitionView>();
        if(applicationView.getCurrentPetriNetView().getTokenViews().size() > 1)
        {
            PetriNetView model = applicationView.getCurrentPetriNetView();
            TransitionView[] transitionViews = model.getTransitionViews();
            for(TransitionView transitionView : transitionViews)
            {
                if(transitionView.isGrouped())
                {
                    if(!transitionsToUngroup.contains(transitionView.getGroup()))
                        transitionsToUngroup.add(transitionView.getGroup());
                }
            }
            for(GroupTransitionView groupTransitionView : transitionsToUngroup)
            {
                HistoryItem edit = groupTransitionView.ungroupTransitions();
                applicationView.getCurrentTab().getHistoryManager().addNewEdit(edit);
                groupTransitionView.deleteAssociatedArcs();
                groupTransitionView.setVisible(false);
                groupTransitionView.getNameLabel().setVisible(false);
            }

        }
    }
}
