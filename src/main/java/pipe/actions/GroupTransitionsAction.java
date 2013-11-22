package pipe.actions;

import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.ApplicationSettings;
import pipe.gui.PetriNetTab;
import pipe.models.Marking;
import pipe.models.NormalArc;
import pipe.models.Token;
import pipe.views.*;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author Alex Charalambous, June 2010: Groups any transitions that have
 *         the same inputs and outputs. Only does anything if this is a
 *         coloured petri net
 *     @author yufeiwang (minor change)
 */
public class GroupTransitionsAction extends GuiAction
{

    public GroupTransitionsAction()
    {
        super("groupTransitions", "Group any possible transitions", "shift ctrl G");
    }

    public void actionPerformed(ActionEvent e)
    {
        /* NOTE: With the current implementation we must clear the undo
        * history before performing this action otherwise undoing a
        * previous grouping could cause a mixup. In the future this
        * should all be done as a single undo transaction.
        */
        PipeApplicationView applicationView = ApplicationSettings.getApplicationView();
        PipeApplicationController controller = ApplicationSettings.getApplicationController();
        PetriNetController petriNetController = controller.getActivePetriNetController();
        petriNetController.getHistoryManager().clear();

        LinkedList<GroupTransitionView> newGroupTransitionViews = new LinkedList<GroupTransitionView>();
        System.out.println(applicationView.getCurrentPetriNetView().getTokenViews().size());
        if(applicationView.getCurrentPetriNetView().getTokenViews().size() > 1)
        {
            PetriNetTab petriNetTab = applicationView.getCurrentTab();
            PetriNetView model = applicationView.getCurrentPetriNetView();
            TransitionView[] transitionViews = model.getTransitionViews();
            Boolean firstAddition;
            for(int i = 0; i < transitionViews.length - 1; i++)
            {
                GroupTransitionView newGroupTransitionView = new GroupTransitionView(transitionViews[i], transitionViews[i].getModel().getX(), transitionViews[i].getModel().getY());
                firstAddition = true;
                if(!transitionViews[i].isGrouped())
                {
                    for(int j = i + 1; j < transitionViews.length; j++)
                    {
                        if(!transitionViews[j].isGrouped())
                        {
                            boolean allSourcesAndTargetsMatch = true;
                            for(ArcView arcViewFromT1 : transitionViews[i].outboundArcs())
                            {
                                boolean thisPairMatches = false;
                                for(ArcView arcViewFromT2 : transitionViews[j].outboundArcs())
                                {
                                    if(arcViewFromT2.getTarget().equals(arcViewFromT1.getTarget()))
                                    {
                                        thisPairMatches = true;
                                        break;
                                    }
                                }
                                if(!thisPairMatches)
                                {
                                    allSourcesAndTargetsMatch = false;
                                    break;
                                }
                            }
                            ArcView arcViewToT1 = transitionViews[i].inboundArcs().iterator().next();
                            boolean thisPairMatches = false;
                            for(ArcView arcViewToT2 : transitionViews[j].inboundArcs())
                            {
                                if(arcViewToT2.getSource().equals(
                                        arcViewToT1.getSource()))
                                {
                                    thisPairMatches = true;
                                    break;
                                }
                            }
                            if(!thisPairMatches)
                            {
                                allSourcesAndTargetsMatch = false;
                                break;
                            }
                            if(allSourcesAndTargetsMatch)
                            {
                                // First entry in groupTransition. Hence initialize.
                                if(!transitionViews[i].isGrouped())
                                {

                                    // Add new input arcs to our new Grouped transition
                                    LinkedList<ArcView> arcsTo = transitionViews[i].inboundArcs();
                                    for(ArcView tempArcView : arcsTo)
                                    {
                                        ArcView newArcView = new NormalArcView(tempArcView.getStartPositionX(),
                                                                               tempArcView.getStartPositionY(),
                                                                               tempArcView.getArcPath().getPoint(1).getX(),
                                                                               tempArcView.getArcPath().getPoint(1).getY(),
                                                                               tempArcView.getSource(),
                                                                               newGroupTransitionView,
                                                                               new LinkedList<MarkingView>(), "", false, new NormalArc(tempArcView.getSource().getModel(), newGroupTransitionView.getModel(), new HashMap<Token, String>()), petriNetController);
                                        newGroupTransitionView.addInbound(newArcView);
                                        tempArcView.getSource().addOutbound(newArcView);
                                        newArcView.addToView(petriNetTab);
                                    }

                                    LinkedList<ArcView> arcsFrom = transitionViews[i].outboundArcs();
                                    for(ArcView tempArcView : arcsFrom)
                                    {
                                        ArcView newArcView = new NormalArcView(tempArcView.getStartPositionX(), tempArcView.getStartPositionY(),
                                                                               tempArcView.getArcPath().getPoint(1).getX(), tempArcView.getArcPath().getPoint(1).getY(), newGroupTransitionView, tempArcView.getTarget(),
                                                                               new LinkedList<MarkingView>(), "", false,  new NormalArc(newGroupTransitionView.getModel(),tempArcView.getSource().getModel(), new HashMap<Token, String>()), petriNetController);
                                        newGroupTransitionView.addOutbound(newArcView);
                                        tempArcView.getTarget().addInbound(newArcView);
                                        newArcView.addToView(petriNetTab);
                                    }
                                    newGroupTransitionView
                                            .addTransition(transitionViews[i]);
                                    if(firstAddition)
                                    {
                                        newGroupTransitionView.setName(transitionViews[i].getId());
                                        firstAddition = false;
                                    }
                                    else
                                    {
                                        newGroupTransitionView.setName(newGroupTransitionView.getName() +
                                                                               "_" + transitionViews[i].getId());
                                    }
                                    transitionViews[i]
                                            .bindToGroup(newGroupTransitionView);
                                    newGroupTransitionViews.add(newGroupTransitionView);
                                }
                                newGroupTransitionView
                                        .addTransition(transitionViews[j]);
                                if(firstAddition)
                                {
                                    newGroupTransitionView.setName(transitionViews[j].getId());
                                    firstAddition = false;
                                }
                                else
                                {
                                    newGroupTransitionView.setName(newGroupTransitionView.getName() +
                                                                           "_" + transitionViews[j].getId());
                                }
                                transitionViews[j]
                                        .bindToGroup(newGroupTransitionView);
                            }
                        }

                    }
                }
            }
            for(GroupTransitionView groupTransitionView : newGroupTransitionViews)
            {
                for(TransitionView t : groupTransitionView.getTransitions())
                {
                    t.hideFromCanvas();
                    t.hideAssociatedArcs();
                }
                model.addPetriNetObject(groupTransitionView);
                petriNetTab.addNewPetriNetObject(groupTransitionView);
                groupTransitionView.setVisible(true);
            }

        }
    }
}
