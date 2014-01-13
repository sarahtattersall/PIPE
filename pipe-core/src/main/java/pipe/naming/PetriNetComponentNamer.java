package pipe.naming;

/**
 * Namer is used to find unique names for {@link pipe.models.component.PetriNetComponent}'s within a
 * {@link pipe.models.PetriNet}
 */
public interface PetriNetComponentNamer {
    /**
     * @return a unique name for the {@link pipe.models.component.PetriNetComponent}
     */
    String getName();
}
