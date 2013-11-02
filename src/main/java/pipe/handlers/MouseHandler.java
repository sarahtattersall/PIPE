package pipe.handlers;

import pipe.controllers.PetriNetController;
import pipe.gui.*;
import pipe.handlers.mouse.MouseUtilities;
import pipe.historyActions.AddPetriNetObject;
import pipe.models.PetriNet;
import pipe.models.PipeApplicationModel;
import pipe.models.Place;
import pipe.models.Transition;
import pipe.views.*;
import pipe.views.viewComponents.AnnotationNote;
import pipe.views.viewComponents.RateParameter;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
* Created with IntelliJ IDEA.
* User: st809
* Date: 02/11/2013
* Time: 09:17
* To change this template use File | Settings | File Templates.
*/
public class MouseHandler extends MouseInputAdapter
{
    private PetriNetViewComponent pn;
    private final PetriNetTab _petriNetTab;
    private final PetriNet petriNet;
    private final PetriNetView petriNetView;
    private Point dragStart;
    private PetriNetController petriNetController;
    private final MouseUtilities mouseUtilities;

    public MouseHandler(MouseUtilities mouseUtilities, PetriNetController controller, PetriNet net, PetriNetTab petriNetTab, PetriNetView petriNetView)
    {
        super();
        this._petriNetTab = petriNetTab;
        this.petriNet = net;
        this.petriNetView = petriNetView;
        this.petriNetController = controller;
        this.mouseUtilities = mouseUtilities;
    }


    private Point adjustPoint(Point p, int zoom)
    {
        int offset = (int) (ZoomController.getScaleFactor(zoom) * Constants.PLACE_TRANSITION_HEIGHT / 2);

        int x = ZoomController.getUnzoomedValue(p.x - offset, zoom);
        int y = ZoomController.getUnzoomedValue(p.y - offset, zoom);

        p.setLocation(x, y);
        return p;
    }


    private ConnectableView newPlace(Point p)
    {
        p = adjustPoint(p, _petriNetTab.getZoom());
        //TODO: MOVE THIS OUT TO CONTROLLER, ALSO NEED TO ADD TO PETRINET MODEL...
        Place place = new Place("", "");
        place.setX(Grid.getModifiedX(p.x));
        place.setY(Grid.getModifiedY(p.y));

        petriNet.addPlace(place);
        petriNet.notifyObservers();

        pn = new PlaceView((double) Grid.getModifiedX(p.x), (double) Grid.getModifiedY(p.y));
        ((PlaceView) pn).setModel(place);

        //place.registerObserver(pn);

        petriNetView.addPetriNetObject(pn);
        _petriNetTab.addNewPetriNetObject(pn);
        return (ConnectableView) pn;
    }


    private ConnectableView newTransition(Point p, boolean timed)
    {
        p = adjustPoint(p, _petriNetTab.getZoom());
        //TODO: MOVE THIS OUT TO CONTROLLER, ALSO NEED TO ADD TO PETRINET MODEL...
        Transition transition = new Transition("", "");
        transition.setX((double) Grid.getModifiedX(p.x));
        transition.setY((double) Grid.getModifiedY(p.y));
        transition.setTimed(timed);

        //TODO: ADd observer
        //transition.registerObserver(pn);

        pn = new TransitionView((double) Grid.getModifiedX(p.x), (double) Grid.getModifiedY(p.y));
        ((TransitionView) pn).setTimed(timed);
        ((TransitionView) pn).setModel(transition);
        petriNetView.addPetriNetObject(pn);
        _petriNetTab.addNewPetriNetObject(pn);
        return (ConnectableView) pn;
    }


    public void mousePressed(MouseEvent e)
    {
        PipeApplicationModel applicationModel = ApplicationSettings.getApplicationModel();
        Point start = e.getPoint();
        Point p;

        if(mouseUtilities.isLeftMouse(e))
        {
            int mode = applicationModel.getMode();
            switch(mode)
            {
                case Constants.PLACE:
                    ConnectableView pto = newPlace(e.getPoint());
                    _petriNetTab.getHistoryManager().addNewEdit(new AddPetriNetObject(pto, _petriNetTab, petriNetView));
                    if(e.isControlDown())
                    {
                        applicationModel.enterFastMode(Constants.FAST_TRANSITION);
                        pn.dispatchEvent(e);
                    }
                    break;

                case Constants.IMMTRANS:
                case Constants.TIMEDTRANS:
                    boolean timed = (mode == Constants.TIMEDTRANS);
                    pto = newTransition(e.getPoint(), timed);
                    _petriNetTab.getHistoryManager().addNewEdit(new AddPetriNetObject(pto, _petriNetTab, petriNetView));
                    if(e.isControlDown())
                    {
                        applicationModel.enterFastMode(Constants.FAST_PLACE);
                        pn.dispatchEvent(e);
                    }
                    break;

                case Constants.ARC:
                case Constants.INHIBARC:
                    if(petriNetController.isCurrentlyCreatingArc())
                        addPoint(e);
                    break;

                case Constants.ANNOTATION:
                    p = adjustPoint(e.getPoint(), _petriNetTab.getZoom());
                    pn = new AnnotationNote(p.x, p.y);
                    petriNetView.addPetriNetObject(pn);
                    _petriNetTab.addNewPetriNetObject(pn);
                    _petriNetTab.getHistoryManager().addNewEdit(new AddPetriNetObject(pn, _petriNetTab, petriNetView));
                    ((AnnotationNote) pn).enableEditMode();
                    break;

                case Constants.RATE:
                    try
                    {
                        String label = JOptionPane.showInputDialog("Rate Parameter Label:", "");
                        if(label == null)
                            break;

                        if(label.length() == 0)
                            throw new Exception("label Incorrect");
                        else if(petriNetView.existsRateParameter(label))
                            throw new Exception("label Already Defined");

                        String value = JOptionPane.showInputDialog("Rate Parameter Value:", "");

                        p = adjustPoint(e.getPoint(), _petriNetTab.getZoom());

                        pn = new RateParameter(label,Double.parseDouble(value), p.x, p.y);
                        petriNetView.addPetriNetObject(pn);
                        _petriNetTab.addNewPetriNetObject(pn);
                        _petriNetTab.getHistoryManager().addNewEdit(new AddPetriNetObject(pn, _petriNetTab,
                                petriNetView));
                    }
                    catch(NumberFormatException nfe)
                    {
                        JOptionPane.showMessageDialog(null, "Enter a rate","Invalid entry", JOptionPane.ERROR_MESSAGE);
                    }
                    catch(Exception exc)
                    {
                        String message = exc.getMessage();
                        if(message == null)
                        {
                            message = "Unknown Error!";
                        }
                        JOptionPane.showMessageDialog(null, message,"Invalid entry", JOptionPane.ERROR_MESSAGE);
                    }
                    break;

                case Constants.FAST_PLACE:
                    ConnectableView createPTO;
                    if(e.isMetaDown() || _petriNetTab.isMetaDown())
                    {
                        if(petriNetController.isCurrentlyCreatingArc())
                        {
                            addPoint(e);
                        }
                    }
                    else
                    {
                        if(!petriNetController.isCurrentlyCreatingArc())
                            break;
                        _petriNetTab._wasNewPertiNetComponentCreated = true;
                        createPTO = newPlace(e.getPoint());
                        _petriNetTab.getHistoryManager().addNewEdit(new AddPetriNetObject(createPTO, _petriNetTab,
                                petriNetView));
                        pn.getMouseListeners()[0].mouseReleased(e);
                        if(e.isControlDown())
                        {
                            applicationModel.setMode(Constants.FAST_TRANSITION);
                            pn.getMouseListeners()[0].mousePressed(e);
                        }
                        else
                            applicationModel.resetMode();
                    }
                    break;

                case Constants.FAST_TRANSITION:
                    if(e.isMetaDown() || _petriNetTab.isMetaDown())
                    {
                        if(petriNetController.isCurrentlyCreatingArc())
                            addPoint(e);
                    }
                    else
                    {
                        if(!petriNetController.isCurrentlyCreatingArc())
                            break;
                        _petriNetTab._wasNewPertiNetComponentCreated = true;
                        timed = e.isAltDown();
                        if(applicationModel.getOldMode() == Constants.TIMEDTRANS)
                            timed = !timed;
                        createPTO = newTransition(e.getPoint(), timed);
                        _petriNetTab.getHistoryManager().addNewEdit(new AddPetriNetObject(createPTO, _petriNetTab,
                                petriNetView));
                        pn.getMouseListeners()[0].mouseReleased(e);
                        if(e.isControlDown())
                        {
                            applicationModel.setMode(Constants.FAST_PLACE);
                            pn.getMouseListeners()[0].mousePressed(e);
                        }
                        else
                            applicationModel.resetMode();
                    }
                    break;

                case Constants.DRAG:
                    dragStart = new Point(start);
                    break;

                default:
                    break;
            }
        }
        else
        {
            _petriNetTab.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            dragStart = new Point(start);
        }
        _petriNetTab.updatePreferredSize();
    }


    private void addPoint(final MouseEvent e)
    {
        int x = Grid.getModifiedX(e.getX());
        int y = Grid.getModifiedY(e.getY());
        boolean shiftDown = e.isShiftDown();
        petriNetController.addArcPoint(x, y, shiftDown);
    }


    public void mouseReleased(MouseEvent e)
    {
        _petriNetTab.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }


    public void mouseMoved(MouseEvent event)
    {
        if(petriNetController.isCurrentlyCreatingArc())
        {
            petriNetController.addArcPoint(Grid.getModifiedX(event.getX()), Grid.getModifiedY(
                    event.getY()), event.isShiftDown());
        }
        //TODO: THIS SHOULDNT BE IN PipeApplicationController
        //PipeApplicationController controller = ApplicationSettings.getApplicationController();
        //controller.mouseMoved(event);
        //if(_createArcView != null)
        //    _createArcView.setEndPoint(Grid.getModifiedX(event.getX()), Grid.getModifiedY(event.getY()), event.isShiftDown());
    }


    public void mouseDragged(MouseEvent e)
    {
        _petriNetTab.drag(dragStart, e.getPoint());
    }


    public void mouseWheelMoved(MouseWheelEvent e)
    {
        if(!e.isControlDown())
        {
        }
        else
        {
            if(e.getWheelRotation() > 0)
                _petriNetTab.zoomIn();
            else
                _petriNetTab.zoomOut();
        }
    }

}
