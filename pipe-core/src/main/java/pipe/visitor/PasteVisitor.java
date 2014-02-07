package pipe.visitor;

import pipe.models.component.Connectable;
import pipe.models.component.PetriNetComponent;
import pipe.models.component.arc.Arc;
import pipe.models.component.arc.ArcPoint;
import pipe.models.component.arc.ArcVisitor;
import pipe.models.component.place.Place;
import pipe.models.component.place.PlaceVisitor;
import pipe.models.component.transition.Transition;
import pipe.models.component.transition.TransitionVisitor;
import pipe.models.petrinet.PetriNet;
import pipe.naming.MultipleNamer;

import java.util.*;

/**
 * Paste visitor pastes components into a petri net
 */
public class PasteVisitor implements TransitionVisitor, ArcVisitor, PlaceVisitor {

    private final MultipleNamer multipleNamer;

    private final PetriNet petriNet;

    private final Collection<PetriNetComponent> components = new HashSet<PetriNetComponent>();

    /**
     * Maps original to copied connectable
     */
    private final Map<Connectable, Connectable> createdConnectables = new HashMap<Connectable, Connectable>();

    public Collection<PetriNetComponent> getCreatedComponents() {
        return createdComponents;
    }

    private final Collection<PetriNetComponent> createdComponents = new LinkedList<PetriNetComponent>();

    private final double xOffset;

    private final double yOffset;

    public PasteVisitor(PetriNet petriNet, Collection<PetriNetComponent> components, MultipleNamer multipleNamer) {
        this(petriNet, components, multipleNamer, 0, 0);
    }

    public PasteVisitor(PetriNet petriNet, Collection<PetriNetComponent> components, MultipleNamer multipleNamer, double xOffset, double yOffset) {
        this.petriNet = petriNet;
        this.multipleNamer = multipleNamer;
        this.components.addAll(components);
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    @Override
    public <T extends Connectable, S extends Connectable> void visit(Arc<S, T> arc) {
        S source = arc.getSource();
        T target = arc.getTarget();

        if (components.contains(source)) {
            source = (S) createdConnectables.get(source);
        }

        if (components.contains(target)) {
            target = (T) createdConnectables.get(target);
        }

        Arc<S, T> newArc = new Arc<>(source, target, arc.getTokenWeights(), arc.getType());
//        setId(newArc);
        copyIntermediatePoints(arc, newArc);
        petriNet.addArc(newArc);
        createdComponents.add(newArc);

    }

    /**
     * Copies the original arc intermediate points into the new arc.
     *
     * @param arc original arc
     * @param newArc newly created arc
     */
    private  void copyIntermediatePoints(Arc<? extends Connectable, ? extends Connectable> arc, Arc<? extends Connectable, ? extends Connectable> newArc) {
        for (ArcPoint arcPoint : arc.getIntermediatePoints()) {
            ArcPoint newArcPoint = new ArcPoint(arcPoint);
            newArc.addIntermediatePoint(newArcPoint);
        }
    }


    @Override
    public void visit(Place place) {
        Place newPlace = new Place(place);
        setId(newPlace);
        setName(newPlace);
        setOffset(newPlace);
        petriNet.addPlace(newPlace);
        createdConnectables.put(place, newPlace);
        createdComponents.add(newPlace);
    }

    @Override
    public void visit(Transition transition) {
        Transition newTransition = new Transition(transition);
        setId(newTransition);
        setName(newTransition);
        setOffset(newTransition);
        petriNet.addTransition(newTransition);
        createdConnectables.put(transition, newTransition);
        createdComponents.add(newTransition);
    }


    private void setOffset(Connectable connectable) {
        connectable.setX(connectable.getX() + xOffset);
        connectable.setY(connectable.getY() + yOffset);
    }

    private void setId(Place place) {
        place.setId(multipleNamer.getPlaceName());
    }


    private void setId(Transition transition) {
        transition.setId(multipleNamer.getTransitionName());
    }

    private void setName(Place place) {
        place.setName(multipleNamer.getPlaceName());
    }


    private void setName(Transition transition) {
        transition.setName(multipleNamer.getTransitionName());
    }
}
