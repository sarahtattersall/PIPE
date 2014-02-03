package pipe.naming;

/**
 * Namer is used to find unique names for {@link pipe.models.component.PetriNetComponent}'s within a
 * {@link pipe.models.petrinet.PetriNet}
 */
public interface PetriNetComponentNamer {
    /**
     * @return a unique name for the {@link pipe.models.component.PetriNetComponent}
     */
    String getName();

    /**
     *
     * @param name
     * @return true if name doesn't exist anywhere else in the {@link pipe.models.component.PetriNetComponent}
     */
    boolean isUniqueName(String name);
}
