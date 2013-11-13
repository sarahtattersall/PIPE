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
 * @author yufeiwang (minor change)
 */
public class Expander
{
    private final PetriNetView _petriNetView;
    private TokenView _defaultTokenView;

    private ArrayList<PlaceView> _newPlaceViews;
    private ArrayList<ArcView> _newArcViews;
    private ArrayList<TransitionView> _newTransitionViews;

    public Expander(PetriNetView netView)
    {
        _petriNetView = netView;
        _newPlaceViews = new ArrayList<PlaceView>();
        _newArcViews = new ArrayList<ArcView>();
        _newTransitionViews = new ArrayList<TransitionView>();
        boolean foundDefaultClass = false;
        // Set default token
        for(TokenView tc : netView.getTokenViews())
        {
            if(tc.getID().equals("Default"))
            {
                foundDefaultClass = true;
                _defaultTokenView = tc;
                break;
            }
        }
        // If there is no token with name Default
        // then select class which has a token colour of black
        if(!foundDefaultClass)
        {
            for(TokenView tc : netView.getTokenViews())
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
        // to the first enabled token.
        if(!foundDefaultClass)
        {
            for(TokenView tc : netView.getTokenViews())
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
        // saveAsXml(newPetriNet);
        return createPetriNetView();
    }

    // Will iterate through each transition, analyse its input and output arcs
    // and create new places and connecting arcs as necessary.
    private void unfoldTransitions()
    {
        // Get current transitions. These should remain as is (remember they are
        // ungrouped
        // and hence number of transitions in folded model = no. of transitions
        // in unfolded model)
        TransitionView[] transitionViews = _petriNetView.getTransitionViews();
        _newPlaceViews = new ArrayList<PlaceView>();
        _newArcViews = new ArrayList<ArcView>();
        _newTransitionViews = new ArrayList<TransitionView>();
        for(TransitionView transitionView : transitionViews)
        {

            // Create a copy of existing transition and add it to the list
            String transIdInput = transitionView.getId();
            //String functionalRate = transitionView.getRateExpr();
            String functionalRate = transitionView.getRate()+"";
           // double rateInput = transitionView.getRate();
            boolean timedTransition = transitionView.isTimed();
            boolean infServer = transitionView.isInfiniteServer();
            int angleInput = transitionView.getAngle();
            int priority = transitionView.getPriority();

            //TODO: WORK OUT HOW TO GET CONTROLLER
            TransitionView newTransitionView = new TransitionView(transIdInput, transIdInput, 0, 0, timedTransition, infServer, angleInput, new Transition(transIdInput, transIdInput,functionalRate, priority), transitionView.getPetriNetController());
            _newTransitionViews.add(newTransitionView);
            analyseArcs(transitionView, newTransitionView, transitionView.outboundArcs());
            analyseArcs(transitionView, newTransitionView, transitionView.inboundArcs());
        }

    }
    // Steve Doubleday:  added PlaceViews and ArcViews as observers for new MarkingViews
    public void analyseArcs(TransitionView transitionView, TransitionView newTransitionView, LinkedList<ArcView> arcViews)
    {
        for(ArcView arcView : arcViews)
        {
            PlaceView oldPlaceView = (PlaceView) arcView.getTheOtherEndFor(transitionView);
            String newPlaceName = oldPlaceView.getId();
            int newMarking = 0;
            int newArcWeight = 0;
            List<MarkingView> markings = arcView.getWeight();
            for(MarkingView markingView : markings)
            {
                if(markingView.getCurrentMarking() > 0)
                {
                    newPlaceName += "_" + markingView.getToken().getID();
                    for(MarkingView placeMarkingView : oldPlaceView.getCurrentMarkingView())
                    {
                        if(markingView.getToken().getID().equals(placeMarkingView.getToken().getID()))
                        {
                            newMarking = placeMarkingView.getCurrentMarking();
                            newArcWeight = markingView.getCurrentMarking();
                        }
                    }
                }
            }
            PlaceView newPlaceView = null;
            for(PlaceView placeView : _newPlaceViews)
            {
                if(placeView.getId().equals(newPlaceName))
                {
                    newPlaceView = placeView;
                }
            }
            if(newPlaceView == null)
            {
                LinkedList<MarkingView> markingViewInput = new LinkedList<MarkingView>();
                MarkingView placeMarkingView = new MarkingView(_defaultTokenView, newMarking+"");
                markingViewInput.add(placeMarkingView);

                Place place = new Place(oldPlaceView.getId(), oldPlaceView.getName());
                //TODO: WORK OUT HOW TO GET CONTROLLER
                newPlaceView = new PlaceView(newPlaceName, newPlaceName, markingViewInput, place, null);
                placeMarkingView.addObserver(newPlaceView);
                _newPlaceViews.add(newPlaceView);
            }
            LinkedList<MarkingView> weight = new LinkedList<MarkingView>();
            Map<Token, String> weightModel = new HashMap<Token, String>();
            MarkingView arcMarkingView = new MarkingView(_defaultTokenView, newArcWeight+"");
            weight.add(arcMarkingView);
            weightModel.put(_defaultTokenView.getModel(), newArcWeight+"");

            ArcView newArcView;
            if(transitionView.outboundArcs() == arcViews)
                newArcView = new NormalArcView( arcView.getStartPositionX(), arcView.getStartPositionY(), newPlaceView.getX(), newPlaceView.getY(), newTransitionView, newPlaceView, weight, arcView.getId(), false, new NormalArc(newTransitionView.getModel(), newPlaceView.getModel(), weightModel), arcView.getPetriNetController());
            else
                newArcView = new NormalArcView(newPlaceView.getX(), newPlaceView.getY(), arcView.getStartPositionX(), arcView.getStartPositionY(), newPlaceView, newTransitionView, weight, arcView.getId(), false, new NormalArc(newPlaceView.getModel(), newTransitionView.getModel(), weightModel), arcView.getPetriNetController());
            newPlaceView.addInboundOrOutbound(newArcView);
            newTransitionView.addInboundOrOutbound(newArcView);
            arcMarkingView.addObserver(newArcView); 
            _newArcViews.add(newArcView);
        }
    }

    private PetriNetView createPetriNetView()
    {
        //TODO: PASS IN CONTROLLER? WHAT DOES THE EXPANDER DO?
        PetriNetView petriNetView = new PetriNetView(null, new PetriNet());
        LinkedList<TokenView> tokenViews = new LinkedList<TokenView>();
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
                double prevX = _newPlaceViews.get(i).getX();
                double prevY = _newPlaceViews.get(i).getY();
                for(int j = i + 1; j < size; j++)
                {
                    currentX = _newPlaceViews.get(j).getX();
                    currentY = _newPlaceViews.get(j).getY();
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
            writer.saveTo(file, false);
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
