package pipe.models.petrinet.name;

/**
 * Interface for the name of a Petri net.
 * <p/>
 * It is an object rather than a String because this allows for easy
 * update if the name should change because
 * a) either the file is renamed
 * b) an unamed Petri net is changed during save
 */
public interface PetriNetName {
    /**
     * @return the name of this Petri net
     */
    String getName();

    /**
     *
     * @param nameVisitor visitor which will process this name in some way
     */
    void visit(NameVisitor nameVisitor);
}
