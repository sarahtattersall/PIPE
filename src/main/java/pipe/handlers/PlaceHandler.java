package pipe.handlers;

import pipe.actions.ShowHideInfoAction;
import pipe.gui.ApplicationSettings;
import pipe.gui.Constants;
import pipe.gui.ZoomController;
import pipe.historyActions.HistoryManager;
import pipe.utilities.Copier;
import pipe.views.ArcView;
import pipe.views.MarkingView;
import pipe.views.PlaceView;
import pipe.views.TransitionView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

public class PlaceHandler extends PlaceTransitionObjectHandler
{

    public PlaceHandler(Container contentpane, PlaceView obj)
    {
        super(contentpane, obj);
    }

    JPopupMenu getPopup(MouseEvent e)
    {
        int index = 0;
        JPopupMenu popup = super.getPopup(e);

        JMenuItem menuItem = new JMenuItem("Edit Place");
        ActionListener actionListener = new ActionListener()
                                            {
                                                public void actionPerformed(ActionEvent e)
                                                {
                                                    ((PlaceView) component).showEditor();
                                                }
                                            };
        menuItem.addActionListener(actionListener);
        popup.insert(menuItem, index++);

        menuItem = new JMenuItem(new ShowHideInfoAction((PlaceView) component));
        if(((PlaceView) component).getAttributesVisible())
        {
            menuItem.setText("Hide Attributes");
        }
        else
        {
            menuItem.setText("Show Attributes");
        }
        popup.insert(menuItem, index++);
        popup.insert(new JPopupMenu.Separator(), index);

        return popup;
    }
    // Steve Doubleday: refactored to simplify testing
    public void mouseClicked(MouseEvent e)
    {
        if(SwingUtilities.isLeftMouseButton(e))
        {
            if(e.getClickCount() == 2
                    && ApplicationSettings.getApplicationModel().isEditionAllowed()
                    && (ApplicationSettings.getApplicationModel().getMode() == Constants.PLACE || ApplicationSettings.getApplicationModel().getMode() == Constants.SELECT))
            {
                ((PlaceView) component).showEditor();
            }
            else
            {
                LinkedList<MarkingView> oldMarkingViews = Copier.mediumCopy(((PlaceView) component).getCurrentMarkingView());
                HistoryManager historyManager = ApplicationSettings.getApplicationView().getCurrentTab().getHistoryManager();
                switch(ApplicationSettings.getApplicationModel().getMode())
                {
                    case Constants.ADDTOKEN:
					addToken(oldMarkingViews, historyManager);
                        updateArcAndTran();
                        break;
                    case Constants.DELTOKEN:
					deleteToken(oldMarkingViews, historyManager);
                        updateArcAndTran();
                        break;
                    default:
                        break;
                }
            }
        }
        else if(SwingUtilities.isRightMouseButton(e))
        {
            if(ApplicationSettings.getApplicationModel().isEditionAllowed() && enablePopup)
            {
                JPopupMenu m = getPopup(e);
                if(m != null)
                {
                    int x = ZoomController.getZoomedValue(component
                                                                  .getNameOffsetXObject().intValue(), component
                            .getZoomPercentage());
                    int y = ZoomController.getZoomedValue(component
                                                                  .getNameOffsetYObject().intValue(), component
                            .getZoomPercentage());
                    m.show(component, x, y);
                }
            }
        }/*
		 * else if (SwingUtilities.isMiddleMouseButton(e)){ ; }
		 */
    }

	protected void deleteToken(LinkedList<MarkingView> oldMarkingViews,
			HistoryManager historyManager)
	{
		for(MarkingView currentMarkingView : oldMarkingViews)
		{
		    if(currentMarkingView.getToken().hasSameId(
		            ((PlaceView) component).getActiveTokenView()))
		    {
		        if(currentMarkingView.getCurrentMarking() > 0)
		        {
		            currentMarkingView.setCurrentMarking(currentMarkingView.getCurrentMarking() - 1);
		            historyManager.addNewEdit(((PlaceView) component)
		                                           .setCurrentMarking(oldMarkingViews));
		        }
		    }
		}
	}

	protected void addToken(LinkedList<MarkingView> oldMarkingViews,
			HistoryManager historyManager)
	{
		for(MarkingView m : oldMarkingViews)
		{
		    if(m.getToken().hasSameId(((PlaceView) component).getActiveTokenView()))
		    {
		        m.setCurrentMarking(m.getCurrentMarking() + 1);
		        m.setToken(ApplicationSettings.getApplicationView().getCurrentPetriNetView().getTokenClassFromID(m.getToken().getID()));
		        historyManager.addNewEdit(((PlaceView) component).setCurrentMarking(oldMarkingViews));
		        break;
		    }
		}
	}
    
    private void updateArcAndTran(){
   	    Collection<ArcView> arcs= ApplicationSettings.getApplicationView().getCurrentPetriNetView().getArcsArrayList();
        for (ArcView arc : arcs)
        {
            arc.repaint();
        }
        Collection<TransitionView> trans = ApplicationSettings.getApplicationView().getCurrentPetriNetView().getTransitionsArrayList();
        for (TransitionView transition : trans)
        {
            transition.update();
        }
   }

    public void mouseWheelMoved(MouseWheelEvent e)
    {
        //
        if(!ApplicationSettings.getApplicationModel().isEditionAllowed() || e.isControlDown())
        {
            return;
        }

        HistoryManager historyManager = ApplicationSettings.getApplicationView().getCurrentTab().getHistoryManager();
        if(e.isShiftDown())
        {
            int oldCapacity = ((PlaceView) component).getCapacity();
            LinkedList<MarkingView> oldMarkingViews = Copier.mediumCopy(((PlaceView) component).getCurrentMarkingView());

            int newCapacity = oldCapacity - e.getWheelRotation();
            if(newCapacity < 0)
            {
                newCapacity = 0;
            }

            historyManager.newEdit(); // new "transaction""
            for(MarkingView m : oldMarkingViews)
            {
                if(m.getToken().hasSameId(
                        ((PlaceView) component).getActiveTokenView()))
                {
                    if((newCapacity > 0)
                            && (m.getCurrentMarking() > newCapacity))
                    {
                        historyManager.addEdit(((PlaceView) component)
                                                    .setCurrentMarking(oldMarkingViews));
                    }
                    updateArcAndTran();
                }
            }
            
            historyManager.addEdit(((PlaceView) component).setCapacity(newCapacity));
        }
        else
        {
            LinkedList<MarkingView> oldMarkingViews = Copier.mediumCopy(((PlaceView) component).getCurrentMarkingView());
            int markingChange = e.getWheelRotation();
            for(MarkingView m : oldMarkingViews)
            {
                if(m.getToken().hasSameId(
                        ((PlaceView) component).getActiveTokenView()))
                {
                    //m.setToken(Pipe.getCurrentPetriNetView().getTokenClassFromID(m.getToken().getID()));
                    int oldMarking = m.getCurrentMarking();
                    int newMarking = m.getCurrentMarking() - markingChange;
                    if(newMarking < 0)
                    {
                        newMarking = 0;
                    }
                    if(oldMarking != newMarking)
                    {
                        m.setCurrentMarking(newMarking);
                        historyManager.addNewEdit(((PlaceView) component)
                                                       .setCurrentMarking(oldMarkingViews));
                    }
                    updateArcAndTran();
                    break;
                }
                
            }
        }
    }

}
