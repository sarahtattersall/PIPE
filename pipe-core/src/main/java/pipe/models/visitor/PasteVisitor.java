package pipe.models.visitor;

import pipe.models.PetriNet;
import pipe.models.component.*;

import java.util.*;

public class PasteVisitor implements PetriNetComponentVisitor {


    private final PetriNet petriNet;
    private final Collection<PetriNetComponent> components = new HashSet<PetriNetComponent>();
    private final Map<String, Connectable> createdConnectables = new HashMap<String, Connectable>();

    public Collection<PetriNetComponent> getCreatedComponents() {
        return createdComponents;
    }

    private final Collection<PetriNetComponent> createdComponents = new LinkedList<PetriNetComponent>();
    private final double xOffset;
    private final double yOffset;

    public PasteVisitor(PetriNet petriNet, Collection<PetriNetComponent> components) {
        this.petriNet = petriNet;
        this.components.addAll(components);
        xOffset = 0;
        yOffset = 0;
    }

    public PasteVisitor(PetriNet petriNet, Collection<PetriNetComponent> components, double xOffset, double yOffset) {
        this.petriNet = petriNet;
        this.components.addAll(components);
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    @Override
    public <T extends Connectable, S extends Connectable> void visit(Arc<S, T> arc) {
        S source = arc.getSource();
        T target = arc.getTarget();

        if (components.contains(source)) {
            source = (S) createdConnectables.get(source.getId() + "_copied");
        }

        if (components.contains(target)) {
            target = (T) createdConnectables.get(target.getId() + "_copied");
        }

        Arc<S, T> newArc = new Arc<S, T>(source, target, arc.getTokenWeights(), arc.getStrategy());
        petriNet.addArc(newArc);
        createdComponents.add(newArc);

    }

    @Override
    public void visit(Place place) {
        Place newPlace = new Place(place);
        setOffset(newPlace);
        petriNet.addPlace(newPlace);
        createdConnectables.put(newPlace.getId(), newPlace);
        createdComponents.add(newPlace);
    }

    @Override
    public void visit(Transition transition) {
        Transition newTransition = new Transition(transition);
        setOffset(newTransition);
        petriNet.addTransition(newTransition);
        createdConnectables.put(newTransition.getId(), newTransition);
        createdComponents.add(newTransition);
    }

    @Override
    public void visit(Token token) {

    }

    @Override
    public void visit(Annotation annotation) {

    }

    private void setOffset(Connectable connectable) {
        connectable.setX(connectable.getX() + xOffset);
        connectable.setY(connectable.getY() + yOffset);
    }

}
