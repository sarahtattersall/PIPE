package pipe.handlers;

import pipe.actions.TypeAction;
import pipe.controllers.PetriNetController;
import pipe.gui.*;
import pipe.handlers.mouse.MouseUtilities;
import pipe.models.petrinet.PetriNet;
import pipe.gui.model.PipeApplicationModel;
import pipe.views.PetriNetViewComponent;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
* PetriNetMouseHandler handles mouse press inputs on a given petri net tab.
* It works out what action is selected (e.g. create new place) and makes this happen.
*/
public class PetriNetMouseHandler extends MouseInputAdapter
{
    private PetriNetViewComponent pn;
    private final PetriNetTab petriNetTab;
    private Point dragStart;
    private PetriNetController petriNetController;
    private final MouseUtilities mouseUtilities;

    public PetriNetMouseHandler(MouseUtilities mouseUtilities, PetriNetController controller, PetriNet net,
                                PetriNetTab petriNetTab)
    {
        super();
        this.petriNetTab = petriNetTab;
        this.petriNetController = controller;
        this.mouseUtilities = mouseUtilities;
    }

    @Override
    public void mousePressed(MouseEvent event)
    {
        PipeApplicationModel applicationModel = ApplicationSettings.getApplicationModel();
        TypeAction action = applicationModel.getSelectedAction();
//        Point start = e.getPoint();
//        Point p;

        if(mouseUtilities.isLeftMouse(event))
        {

//            Point point = adjustPoint(event.getPoint(), petriNetTab.getZoom());
//            MouseEvent accurateEvent = SwingUtilities.convertMouseEvent(event.getComponent(), event,
//                    ApplicationSettings.getApplicationView().getCurrentTab());
            action.doAction(event, petriNetController);
        }

//            int mode = applicationModel.getMode();
//            switch(mode)
//            {
//
//                case Constants.ARC:
//                case Constants.INHIBARC:
//                    if(petriNetController.isCurrentlyCreatingArc())
//                        addIntermediatePoint(e);
//                    break;
//
//                case Constants.ANNOTATION:
//                    p = adjustPoint(e.getPoint(), petriNetTab.getZoom());
//                    pn = new AnnotationNote(p.x, p.y);
//                    petriNetView.addPetriNetObject(pn);
//                    petriNetTab.addNewPetriNetComponent(pn);
//                    petriNetTab.getHistoryManager().addNewEdit(new AddPetriNetObject(pn, petriNetTab, petriNet));
//                    ((AnnotationNote) pn).enableEditMode();
//                    break;
//
//                case Constants.RATE:
//                    try
//                    {
//                        String label = JOptionPane.showInputDialog("Rate Parameter Label:", "");
//                        if(label == null)
//                            break;
//
//                        if(label.length() == 0)
//                            throw new Exception("label Incorrect");
//                        else if(petriNetView.existsRateParameter(label))
//                            throw new Exception("label Already Defined");
//
//                        String value = JOptionPane.showInputDialog("Rate Parameter Value:", "");
//
//                        p = adjustPoint(e.getPoint(), petriNetTab.getZoom());

//                        pn = new RateParameter(label,Double.parseDouble(value), p.x, p.y);
//                        petriNetView.addPetriNetObject(pn);
//                        petriNetTab.addNewPetriNetComponent(pn);
//                        petriNetTab.getHistoryManager().addNewEdit(new AddPetriNetObject(pn, petriNetTab,
//                                petriNetView));
//                    }
//                    catch(NumberFormatException nfe)
//                    {
//                        JOptionPane.showMessageDialog(null, "Enter a rate","Invalid entry", JOptionPane.ERROR_MESSAGE);
//                    }
//                    catch(Exception exc)
//                    {
//                        String message = exc.getMessage();
//                        if(message == null)
//                        {
//                            message = "Unknown Error!";
//                        }
//                        JOptionPane.showMessageDialog(null, message,"Invalid entry", JOptionPane.ERROR_MESSAGE);
//                    }
//                    break;
//
//                case Constants.FAST_PLACE:
                    //TODO: REIMPLEMENT
//                    if(e.isMetaDown() || petriNetTab.isMetaDown())
//                    {
//                        if(petriNetController.isCurrentlyCreatingArc())
//                        {
//                            addIntermediatePoint(e);
//                        }
//                    }
//                    else
//                    {
//                        if(!petriNetController.isCurrentlyCreatingArc())
//                            break;
//                        petriNetTab._wasNewPertiNetComponentCreated = true;
//                        PetriNetComponent createPTO = newPlace(e.getPoint());
//                        petriNetController.getHistoryManager().addNewEdit(new AddPetriNetObject(createPTO, petriNet));
//                        pn.getMouseListeners()[0].mouseReleased(e);
//                        if(e.isControlDown())
//                        {
//                            applicationModel.setMode(Constants.FAST_TRANSITION);
//                            pn.getMouseListeners()[0].mousePressed(e);
//                        }
//                        else
//                            applicationModel.resetMode();
//                    }
//                    break;
//
//                case Constants.FAST_TRANSITION:
                    //TODO: REIMPLEMENT
//                    if(e.isMetaDown() || petriNetTab.isMetaDown())
//                    {
//                        if(petriNetController.isCurrentlyCreatingArc())
//                            addIntermediatePoint(e);
//                    }
//                    else
//                    {
//                        if(!petriNetController.isCurrentlyCreatingArc())
//                            break;
//                        petriNetTab._wasNewPertiNetComponentCreated = true;
//                        timed = e.isAltDown();
//                        if(applicationModel.getOldMode() == Constants.TIMEDTRANS)
//                            timed = !timed;
//                        createPTO = newTransition(e.getPoint(), timed);
//                        petriNetController.getHistoryManager().addNewEdit(new AddPetriNetObject(createPTO, petriNet));
//                        pn.getMouseListeners()[0].mouseReleased(e);
//                        if(e.isControlDown())
//                        {
//                            applicationModel.setMode(Constants.FAST_PLACE);
//                            pn.getMouseListeners()[0].mousePressed(e);
//                        }
//                        else
//                            applicationModel.resetMode();
//                    }
//                    break;
//
//                case Constants.DRAG:
//                    dragStart = new Point(start);
//                    break;
//
//                default:
//                    break;
//            }
//        }
//        else
//        {
//            petriNetTab.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
//            dragStart = new Point(start);
//        }
//        petriNetTab.updatePreferredSize();
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        petriNetTab.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }


    @Override
    public void mouseMoved(MouseEvent event)
    {
        PipeApplicationModel applicationModel = ApplicationSettings.getApplicationModel();
        TypeAction action = applicationModel.getSelectedAction();
        action.doAction(event, petriNetController);
//        if(petriNetController.isCurrentlyCreatingArc())
//        {
//            petriNetController.setEndPoint(Grid.getModifiedValue(event.getX()), Grid.getModifiedY(
//                    event.getY()), event.isShiftDown());
//        }
        //TODO: THIS SHOULDNT BE IN PipeApplicationController
        //PipeApplicationController controller = ApplicationSettings.getApplicationController();
        //controller.mouseMoved(event);
        //if(_createArcView != null)
        //    _createArcView.setEndPoint(Grid.getModifiedValue(event.getX()), Grid.getModifiedY(event.getY()), event.isShiftDown());
    }


    @Override
    public void mouseDragged(MouseEvent e)
    {
        petriNetTab.drag(dragStart, e.getPoint());
    }


    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        if(!e.isControlDown())
        {
        }
        else
        {
//            if(e.getWheelRotation() > 0)
//                petriNetTab.zoomIn();
//            else
//                petriNetTab.zoomOut();
        }
    }

}
