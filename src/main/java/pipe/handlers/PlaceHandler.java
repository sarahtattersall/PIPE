package pipe.handlers;

import pipe.controllers.PetriNetController;
import pipe.gui.ApplicationSettings;
import pipe.gui.Constants;
import pipe.historyActions.HistoryManager;
import pipe.models.Marking;
import pipe.models.Place;
import pipe.utilities.Copier;
import pipe.views.ArcView;
import pipe.views.PipeApplicationView;
import pipe.views.TransitionView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Collection;
import java.util.List;

public class PlaceHandler extends PlaceTransitionObjectHandler<Place> {


    public PlaceHandler(Container contentpane, Place obj, PetriNetController controller) {
        super(contentpane, obj, controller);
    }

    JPopupMenu getPopup(MouseEvent e) {
//        int index = 0;
//        JPopupMenu popup = super.getPopup(e);
//
//        JMenuItem menuItem = new JMenuItem("Edit Place");
//        ActionListener actionListener = new ActionListener()
//                                            {
//                                                public void actionPerformed(ActionEvent e)
//                                                {
//                                                    ((PlaceView) component).showEditor();
//                                                }
//                                            };
//        menuItem.addActionListener(actionListener);
//        popup.insert(menuItem, index++);
//
//        menuItem = new JMenuItem(new ShowHideInfoAction((PlaceView) component));
//        if(((PlaceView) component).getAttributesVisible())
//        {
//            menuItem.setText("Hide Attributes");
//        }
//        else
//        {
//            menuItem.setText("Show Attributes");
//        }
//        popup.insert(menuItem, index++);
//        popup.insert(new JPopupMenu.Separator(), index);
//
//        return popup;
        return null;
    }

    // Steve Doubleday: refactored to simplify testing
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (e.getClickCount() == 2 && ApplicationSettings.getApplicationModel().isEditionAllowed() &&
                    (ApplicationSettings.getApplicationModel().getMode() == Constants.PLACE ||
                            ApplicationSettings.getApplicationModel().getMode() == Constants.SELECT)) {
                //((PlaceView) component).showEditor();
            } else {
//                List<Marking> oldMarkings = Copier.mediumCopyMarkings(component.getTokens());
                HistoryManager historyManager =
                        ApplicationSettings.getApplicationView().getCurrentTab().getHistoryManager();


                PipeApplicationView view = ApplicationSettings.getApplicationView();
                switch (ApplicationSettings.getApplicationModel().getMode()) {
                    case Constants.ADDTOKEN:
                        petriNetController.addTokenToPlace(component, view.getSelectedTokenName());
                        break;
                    case Constants.DELTOKEN:
                        petriNetController.deleteTokenInPlace(component, view.getSelectedTokenName());
                        break;
                    default:
                        break;
                }
            }
        } else if (SwingUtilities.isRightMouseButton(e)) {
            if (ApplicationSettings.getApplicationModel().isEditionAllowed() && enablePopup) {
                JPopupMenu m = getPopup(e);
                if (m != null) {
                    //int x = component.getNameXOffset();//ZoomController.getZoomedValue(component
                    //                                      .getNameOffsetXObject().intValue(), component
                    //.getZoomPercentage());
                    //int y = //ZoomController.getZoomedValue(component
                    //                                      .getNameOffsetYObject().intValue(), component
                    //.getZoomPercentage());
                    //m.show(component, x, y);
                }
            }
        }/*
         * else if (SwingUtilities.isMiddleMouseButton(e)){ ; }
		 */
    }

    protected void deleteToken(List<Marking> oldMarkings, HistoryManager historyManager) {
        for (Marking currentMarkingView : oldMarkings) {
//		    if(currentMarkingView.getToken().hasSameId(
//		            ((PlaceView) component).getActiveTokenView()))
//		    {
//		        if(currentMarkingView.getCurrentMarking() > 0)
//		        {
//		            currentMarkingView.setCurrentMarking(currentMarkingView.getCurrentMarking() - 1);
//		            //historyManager.addNewEdit(((PlaceView) component)
//		            //                               .setCurrentMarking(oldMarkingViews));
//		        }
//		    }
        }
    }

    protected void addToken(List<Marking> oldMarkings, HistoryManager historyManager) {
        for (Marking m : oldMarkings) {
//		    if(m.getToken().hasSameId(((PlaceView) component).getActiveTokenView()))
//		    {
//		        m.setCurrentMarking(m.getCurrentMarking() + 1);
//		        m.setToken(ApplicationSettings.getApplicationView().getCurrentPetriNetView().getTokenClassFromID(m.getToken().getID()));
//		        historyManager.addNewEdit(((PlaceView) component).setCurrentMarking(oldMarkingViews));
//		        break;
//		    }
        }
    }

    private void updateArcAndTran() {
        Collection<ArcView> arcs = ApplicationSettings.getApplicationView().getCurrentPetriNetView().getArcsArrayList();
        for (ArcView arc : arcs) {
            arc.repaint();
        }
        Collection<TransitionView> trans =
                ApplicationSettings.getApplicationView().getCurrentPetriNetView().getTransitionsArrayList();
        for (TransitionView transition : trans) {
            transition.update();
        }
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        //
//        if(!ApplicationSettings.getApplicationModel().isEditionAllowed() || e.isControlDown())
//        {
//            return;
//        }
//
//        HistoryManager historyManager = ApplicationSettings.getApplicationView().getCurrentTab().getHistoryManager();
//        if(e.isShiftDown())
//        {
//            double oldCapacity = component.getCapacity();
//            LinkedList<MarkingView> oldMarkingViews = Copier.mediumCopy(((PlaceView) component).getCurrentMarkingView());
//
//            double newCapacity = oldCapacity - e.getWheelRotation();
//            if(newCapacity < 0)
//            {
//                newCapacity = 0;
//            }
//
//            historyManager.newEdit(); // new "transaction""
//            for(MarkingView m : oldMarkingViews)
//            {
//                if(m.getToken().hasSameId(
//                        ((PlaceView) component).getActiveTokenView()))
//                {
//                    if((newCapacity > 0)
//                            && (m.getCurrentMarking() > newCapacity))
//                    {
//                        historyManager.addEdit(((PlaceView) component)
//                                                    .setCurrentMarking(oldMarkingViews));
//                    }
//                    updateArcAndTran();
//                }
//            }
//
//            historyManager.addEdit(((PlaceView) component).setCapacity(newCapacity));
//        }
//        else
//        {
//            LinkedList<MarkingView> oldMarkingViews = Copier.mediumCopy(((PlaceView) component).getCurrentMarkingView());
//            int markingChange = e.getWheelRotation();
//            for(MarkingView m : oldMarkingViews)
//            {
//                if(m.getToken().hasSameId(
//                        ((PlaceView) component).getActiveTokenView()))
//                {
//                    //m.setToken(Pipe.getCurrentPetriNetView().getTokenClassFromID(m.getToken().getID()));
//                    int oldMarking = m.getCurrentMarking();
//                    int newMarking = m.getCurrentMarking() - markingChange;
//                    if(newMarking < 0)
//                    {
//                        newMarking = 0;
//                    }
//                    if(oldMarking != newMarking)
//                    {
//                        m.setCurrentMarking(newMarking);
//                        historyManager.addNewEdit(((PlaceView) component)
//                                                       .setCurrentMarking(oldMarkingViews));
//                    }
//                    updateArcAndTran();
//                    break;
//                }
//
//            }
//        }
    }

}
