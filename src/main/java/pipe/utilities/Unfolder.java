package pipe.utilities;

import org.w3c.dom.DOMException;
import pipe.controllers.PetriNetController;
import pipe.exceptions.TokenLockedException;
import pipe.gui.ApplicationSettings;
import pipe.models.*;
import pipe.utilities.writers.PNMLWriter;
import pipe.views.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 
 * @author yufei wang(minor change)
 */

class Unfolder
{
    private final PetriNetView _model;
    private TokenView _defaultTokenView;

    private ArrayList<PlaceView> _newPlaceViews;
    private ArrayList<ArcView> _newArcViews;
    private ArrayList<TransitionView> _newTransitionViews;

    public Unfolder(PetriNetView model)
    {
        this._model = model;
        boolean foundDefaultClass = false;
        // Set default token
        for(TokenView tc : model.getTokenViews())
        {
            if(tc.getID().equals("Default"))
            {
                foundDefaultClass = true;
                _defaultTokenView = tc;
                break;
            }
        }
        // If there is no token class with name Default
        // then select class which has a token colour of black
        if(!foundDefaultClass)
        {
            for(TokenView tc : model.getTokenViews())
            {
                if(tc.getColor().getBlue() == 0
                        && tc.getColor().getGreen() == 0
                        && tc.getColor().getRed() == 0)
                {
                    foundDefaultClass = true;
                    _defaultTokenView = tc;
                    break;
                }
            }

        }

        // If no such class exists then set default class
        // to the first enabled token class.
        if(!foundDefaultClass)
        {
            for(TokenView tc : model.getTokenViews())
            {
                if(tc.isEnabled())
                {
                    _defaultTokenView = tc;
                    foundDefaultClass = true;
                    break;
                }
            }
        }
    }

    public PetriNetView unfold()
    {
        unfoldTransitions();
        return createPetriNetView();
    }

    // Will iterate through each transition, analyse its input and output arcs
    // and create new places and connecting arcs as necessary.
    
    // Steve Doubleday (Oct 2013):  added PlaceViews and ArcViews as observers to new MarkingViews
    private void unfoldTransitions()
    {
        // Get current transitions. These should remain as is (remember they are
        // ungrouped
        // and hence number of transitions in folded _model = no. of transitions
        // in unfolded _model)
        TransitionView[] transitionViews = _model.getTransitionViews();
        _newPlaceViews = new ArrayList<PlaceView>();
        _newArcViews = new ArrayList<ArcView>();
        _newTransitionViews = new ArrayList<TransitionView>();
        for(TransitionView transitionView : transitionViews)
        {

            // Create a copy of existing transition and add it to the list
            double transPositionXInput = transitionView.getModel().getX();
            double transPositionYInput = transitionView.getModel().getY();
            String transIdInput = transitionView.getId();
           // double rateInput = transitionView.getRate();
            String functionalRate = transitionView.getRateExpr();
            boolean timedTransition = transitionView.isTimed();
            boolean infServer = transitionView.isInfiniteServer();
            int angleInput = transitionView.getAngle();
            int priority = transitionView.getPriority();
            TransitionView newTransitionView = new TransitionView(transIdInput, transIdInput, 0, 0, timedTransition,
                    infServer, angleInput, new Transition(transIdInput, transIdInput, functionalRate,priority),
                    transitionView.getPetriNetController());
            _newTransitionViews.add(newTransitionView);

            // Now analyse all arcs connected to this transition
            for(ArcView outboundArcView : transitionView.outboundArcs())
            {
                PlaceView oldPlaceView = (PlaceView) outboundArcView.getTarget();
                String newPlaceName = oldPlaceView.getId();
                int newMarking = 0;
                int newArcWeight = 0;
                List<MarkingView> markingViews =  outboundArcView.getWeight();
                for(MarkingView m : markingViews)
                {
                    if(m.getCurrentMarking() > 0)
                    {
                        newPlaceName += "_" + m.getToken().getID();
                        // Now calculate marking of new place to be created
                        for(MarkingView placeMarkingView : oldPlaceView
                                .getCurrentMarkingView())
                        {
                            if(m.getToken().getID().equals(
                                    placeMarkingView.getToken().getID()))
                            {
                                newMarking = placeMarkingView.getCurrentMarking();
                                newArcWeight = m.getCurrentMarking();
                            }
                        }
                    }
                }

                /*
                     * if(newMarkings.isEmpty()){ newMarking = 0; } else{ newMarking
                     * = newMarkings.get(0); for(int j = 1; j < newMarkings.size();
                     * j++){ // Marking of new place is equal to the minimum of
                     * newMarkings newMarking = Math.min(newMarking,
                     * newMarkings.get(j)); } }
                     */

                PlaceView newPlaceView = null;
                for(PlaceView p : _newPlaceViews)
                {
                    if(p.getId().equals(newPlaceName))
                    {
                        newPlaceView = p;
                    }
                }

                if(newPlaceView == null)
                {
                    // Create a new place
                    LinkedList<MarkingView> markingViewInput = new LinkedList<MarkingView>();
                    MarkingView placeMarkingView = new MarkingView(_defaultTokenView, newMarking+"");
                    markingViewInput
                            .add(placeMarkingView);


                    Place place = new Place(oldPlaceView.getId(), oldPlaceView.getName());
                    place.setCapacity(oldPlaceView.getCapacity());
                    place.setMarkingXOffset(oldPlaceView.getMarkingOffsetXObject());
                    place.setMarkingYOffset(oldPlaceView.getMarkingOffsetYObject());
                    newPlaceView = new PlaceView(newPlaceName, newPlaceName, markingViewInput, place,
                            outboundArcView.getPetriNetController());

                    placeMarkingView.addObserver(newPlaceView); 
                    _newPlaceViews.add(newPlaceView);
                }

                // Create a new Arc
                double startPositionXInput = outboundArcView.getStartPositionX();
                double startPositionYInput = outboundArcView.getStartPositionY();
                double endPositionXInput = newPlaceView.getModel().getX();
                double endPositionYInput = newPlaceView.getModel().getY();
                ConnectableView target = newPlaceView;
                LinkedList<MarkingView> weight = new LinkedList<MarkingView>();
                MarkingView arcMarkingView = new MarkingView(_defaultTokenView, newArcWeight+""); 
                weight.add(arcMarkingView);
                Map<Token, String> weightModel = new HashMap<Token, String>();
                weightModel.put(_defaultTokenView.getModel(), newArcWeight + "");
                String idInput = outboundArcView.getId();

                ArcView newArcView = new NormalArcView(startPositionXInput, startPositionYInput,
                                                       endPositionXInput, endPositionYInput, newTransitionView, target,
                                                       weight, idInput, false, new NormalArc(newTransitionView.getModel(),
                        target.getModel(), weightModel), outboundArcView.getPetriNetController());
                arcMarkingView.addObserver(newPlaceView);
                // Join arc, place and transition and add all to appropriate
                // lists
                newPlaceView.addInbound(newArcView);
                newTransitionView.addOutbound(newArcView);
                _newArcViews.add(newArcView);

            }

            // Now do exactly the same for the arcs entering the transition
            for(ArcView inboundArcView : transitionView.inboundArcs())
            {
                PlaceView oldPlaceView = (PlaceView) inboundArcView.getSource();
                String newPlaceName = oldPlaceView.getId();
                int newMarking = 0;
                int newArcWeight = 0;
                List<MarkingView> markingViews = inboundArcView.getWeight();
                for(MarkingView m : markingViews)
                {
                    if(m.getCurrentMarking() > 0)
                    {
                        newPlaceName += "_" + m.getToken().getID();
                        // Now calculate marking of new place to be created
                        for(MarkingView placeMarkingView : oldPlaceView
                                .getCurrentMarkingView())
                        {
                            if(m.getToken().getID().equals(
                                    placeMarkingView.getToken().getID()))
                            {
                                newMarking = placeMarkingView.getCurrentMarking();
                                newArcWeight = m.getCurrentMarking();
                            }
                        }
                    }
                }

                PlaceView newPlaceView = null;
                for(PlaceView p : _newPlaceViews)
                {
                    if(p.getId().equals(newPlaceName))
                    {
                        newPlaceView = p;
                    }
                }
                if(newPlaceView == null)
                {
                    // Create a new place
                    LinkedList<MarkingView> markingViewInput = new LinkedList<MarkingView>();
                    MarkingView placeMarkingView = new MarkingView(_defaultTokenView, newMarking+"");
                    markingViewInput
                            .add(placeMarkingView);


                    Place place = new Place(oldPlaceView.getId(), oldPlaceView.getName());
                    place.setCapacity(oldPlaceView.getCapacity());
                    place.setMarkingXOffset(oldPlaceView.getMarkingOffsetXObject());
                    place.setMarkingYOffset(oldPlaceView.getMarkingOffsetYObject());
                    newPlaceView = new PlaceView(newPlaceName, newPlaceName, markingViewInput, place,
                            inboundArcView.getPetriNetController());

                    placeMarkingView.addObserver(newPlaceView); 
                    _newPlaceViews.add(newPlaceView);
                }

                // Create a new Arc
                double startPositionXInput = newPlaceView.getModel().getX();
                double startPositionYInput = newPlaceView.getModel().getY();
                double endPositionXInput = inboundArcView.getStartPositionX();
                double endPositionYInput = inboundArcView.getStartPositionY();
                ConnectableView source = newPlaceView;
                LinkedList<MarkingView> weight = new LinkedList<MarkingView>();
                MarkingView arcMarkingView = new MarkingView(_defaultTokenView, newArcWeight+""); 
                weight.add(arcMarkingView);
                Map<Token, String> weightModel = new HashMap<Token, String>();
                weightModel.put(_defaultTokenView.getModel(), newArcWeight+"");
                String idInput = inboundArcView.getId();

                ArcView newArcView = new NormalArcView(startPositionXInput, startPositionYInput,endPositionXInput, endPositionYInput, source, newTransitionView,weight, idInput, false, new NormalArc(source.getModel(), newTransitionView.getModel(), weightModel), inboundArcView.getPetriNetController());
                arcMarkingView.addObserver(newArcView); 
                // Join arc, place and transition and add all to appropriate
                // lists
                newPlaceView.addOutbound(newArcView);
                newTransitionView.addInbound(newArcView);
                _newArcViews.add(newArcView);
            }
        }

    }

    private PetriNetView createPetriNetView()
    {
        //TODO: PASS IN CONTROLLER> WHAT DOES THE UNFOLDER DO?
        PetriNetView petriNetView = new PetriNetView(null, new PetriNet());
        LinkedList<TokenView> tokenViews = new LinkedList<TokenView>(); //TODO replace with TokenSetController 
        tokenViews.add(_defaultTokenView);
        try
		{
			petriNetView.updateOrReplaceTokenViews(tokenViews);
		}
		catch (TokenLockedException e)
		{
			e.printStackTrace(); // should not throw at initial creation
		}
        petriNetView.setActiveTokenView(_defaultTokenView);
        for(TransitionView t : _newTransitionViews)
        {
            petriNetView.addPetriNetObject(t);
        }
        for(PlaceView p : _newPlaceViews)
        {
            petriNetView.addPetriNetObject(p);
        }
        for(ArcView a : _newArcViews)
        {
            petriNetView.addPetriNetObject(a);
        }

        return petriNetView;
    }

    // Not used. Could be implemented in a better way
    void organizeNetGraphically()
    {
        int thresholdXDistanceApart = 100;
        int thresholdYDistanceApart = 30;
        double currentX;
        double currentY;
        int size = _newTransitionViews.size();
        // otherwise there are no transitons to seperate from each other
        if(size > 1)
        {
            boolean madeAMove = true;
            while(madeAMove)
            {
                madeAMove = false;
                /*
                     * for(int i = 0; i < size-1; i++){ double prevX =
                     * newTransitions.get(i).getPositionX(); double prevY =
                     * newTransitions.get(i).getPositionY(); for(int j = i+1;
                     * j<size; j++){ currentX =
                     * newTransitions.get(j).getPositionX(); currentY =
                     * newTransitions.get(j).getPositionY(); if((currentX - prevX) >
                     * -thresholdXDistanceApart && Math.abs(currentY - prevY) <
                     * thresholdYDistanceApart){
                     * newTransitions.get(j).setPositionX(-thresholdXDistanceApart +
                     * prevX); System.out.println("shifting" +
                     * newTransitions.get(i).getId() + ": " +
                     * -thresholdXDistanceApart + prevX); } else if((currentX -
                     * prevX) < thresholdXDistanceApart && Math.abs(currentY -
                     * prevY) < thresholdYDistanceApart){
                     * newTransitions.get(j).setPositionX(thresholdXDistanceApart +
                     * prevX); System.out.println("shifting" +
                     * newTransitions.get(i).getId() + ": " +
                     * thresholdXDistanceApart + prevX); madeAMove = true; break; }
                     *
                     * } }
                     */
            }
        }

        // Now align all x position of places with respective transitions
        for(PlaceView _newPlaceView : _newPlaceViews)
        {
            Iterator<?> it = _newPlaceView.getConnectFromIterator();
            if(it.hasNext())
            {
                ArcView a = (ArcView) it.next();
                TransitionView t = (TransitionView) a.getTarget();
//                _newPlaceView.setPositionX(t.getPositionX());
            }
        }

        // Now separate all places from each other
        size = _newPlaceViews.size();
        // otherwise there are no transitons to seperate from each other
        if(size > 1)
        {
            for(int i = 0; i < size - 1; i++)
            {
                double prevX = _newPlaceViews.get(i).getModel().getX();
                double prevY = _newPlaceViews.get(i).getModel().getY();
                for(int j = i + 1; j < size; j++)
                {
                    currentX = _newPlaceViews.get(j).getModel().getX();
                    currentY = _newPlaceViews.get(j).getModel().getY();
                    /*
                          * if((currentX - prevX) < -thresholdXDistanceApart){
                          * newPlaces.get(j).setPositionX(-thresholdXDistanceApart +
                          * prevX && Math.abs(currentY - prevY) <
                          * thresholdYDistanceApart); } else
                          */
                    if((currentX - prevX) < thresholdXDistanceApart
                            && Math.abs(currentY - prevY) < thresholdYDistanceApart)
                    {
//                        _newPlaceViews.get(j).setPositionX(
//                                thresholdXDistanceApart + prevX);
                    }
                }
            }
        }

    }

    public File saveAsXml(PetriNetView dataLayer)
    {
        File file = null;
        try
        {
            file = File.createTempFile("unfoldedNet", ".xml");
            file.deleteOnExit();
            PNMLWriter writer = new PNMLWriter(dataLayer);
            writer.saveTo(file,false);
        }
        catch(NullPointerException e)
        {
            e.printStackTrace();
        }
        catch(DOMException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return file;
    }
}
