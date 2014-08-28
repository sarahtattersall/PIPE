package pipe.gui.rcat;

import pipe.gui.widget.RCATForm;
import uk.ac.imperial.pipe.exceptions.PetriNetComponentException;
import uk.ac.imperial.pipe.models.petrinet.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Controller for the RCAT Module
 * @author Tanvi Potdar
 */
public class BuildingBlockCreator {

    /**
     * called by the splitBB Jbutton to split the Petri Net into building blocks
     * @param petriNet
     * @return
     * @throws PetriNetComponentException
     */
    public Collection<BuildingBlock> splitIntoBuildingBlocks(PetriNet petriNet) throws PetriNetComponentException {

        Collection<BuildingBlock> listOfBuildingBlocks = new HashSet<>();

        RcatPlaceVisitor rcatPlaceVisitor = new RcatPlaceVisitor(petriNet);
        Collection<Place> visitedPlaces = rcatPlaceVisitor.visitedPlaces;

        for(Place place: petriNet.getPlaces()){
            if(! visitedPlaces.contains(place)){
                place.accept(rcatPlaceVisitor);
                BuildingBlock buildingBlock = rcatPlaceVisitor.buildingBlock;
                listOfBuildingBlocks.add(buildingBlock);
            }
        }

        return listOfBuildingBlocks;

    }

    /**
     * class that creates a Visitor for the places in a Petri Net
     * so as to delineate the actions required of a place when it
     * is visited, i.e.,
     * 1. get its neighbours
     * 2. create a building block using the place and its neighbours
     */

    private static class RcatPlaceVisitor implements PlaceVisitor {
        /**
         * current petri net
         */
        private PetriNet petriNet;
        /**
         * set of places that the visitor has already visited
         */
        private final Collection<Place> visitedPlaces = new HashSet<>();
        /**
         * building block created for the place in question
         */
        private BuildingBlock buildingBlock;

        /**
         * Constructor for the RCATPlaceVisitor class
         * @param petriNet in use
         */
        RcatPlaceVisitor(PetriNet petriNet) {
            this.petriNet = petriNet;
        }

        /**
         * specifies the actions that should happen when a
         * place is visited by the RCATPlaceVisitor
         * @param place
         * @throws PetriNetComponentException
         */
        @Override
        public void visit(Place place) throws PetriNetComponentException {
            visitedPlaces.add(place);
            searchForBuildingBlock(place);
        }

        /**
         *creates a building block for the place in question
         * @param place
         * @throws PetriNetComponentException
         */
        private void searchForBuildingBlock(Place place) throws PetriNetComponentException {
            Collection<Place> bbPlaces = new HashSet<>();
            bbPlaces.add(place);

            for(Place neighbour: getNeighbours(place)){
                if(!visitedPlaces.contains(neighbour)){
                    this.visit(neighbour);
                }
                bbPlaces.add(neighbour);
            }

            buildingBlock = new BuildingBlock(bbPlaces, getAllTransitionsInBuildingBlock(bbPlaces));
        }

        /**
         * gets all the transitions in a building block
         * @param places in the building block
         * @return all the transitions in each place in a Building Block
         */
        public Collection<Transition> getAllTransitionsInBuildingBlock(Collection<Place> places){
            Collection<Transition> allTrans = new HashSet<>();
            for(Place place: places){
                for(Arc arc: petriNet.getArcs()){
                    if(place.equals(arc.getSource())){
                        allTrans.add((Transition)arc.getTarget());
                    }
                    if(place.equals(arc.getTarget())){
                        allTrans.add((Transition)arc.getSource());
                    }
                }
            }
            return allTrans;
        }

        /**
         *gets the neighbours of the place in question
         * @param place
         * @return the neighbours of the place, i.e , all the places that have the ~ relation with p
         * The ~ relation: p1~p2 if they share inbound arcs
         */
        public Iterable<Place> getNeighbours(Place place){
            Collection<Place> neighbours = new HashSet<>();
            Collection<Transition> visitedTransitions = new HashSet<>();
            Collection<Transition> outputTransitionsForSinglePlace = new ArrayList<>();

            for(uk.ac.imperial.pipe.models.petrinet.Arc arc: petriNet.getArcs()){
                if(place.equals(arc.getSource())){
                    outputTransitionsForSinglePlace.add((Transition) arc.getTarget());
                }
            }

            for(Transition transition: outputTransitionsForSinglePlace){
                if(! (visitedTransitions.contains(transition))){
                    Collection<InboundArc> inboundArcs = petriNet.inboundArcs(transition);
                    for(InboundArc inboundArc : inboundArcs){
                        Place inboundPlace = inboundArc.getSource();
                        if(!place.equals(inboundPlace)){
                            neighbours.add(inboundPlace);
                        }
                    }

                }
                visitedTransitions.add(transition);
            }
            return neighbours;
        }


    }



}
