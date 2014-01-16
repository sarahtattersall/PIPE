package pipe.visitor.foo;

/**
 * Degenerate visitor interface for visiting {@link pipe.models.component.PetriNetComponent}
 * Used to implement the acyclic visitor pattern
 * This pattern is used to break dependency cycles and allows
 * for visitors to only implement those classes that they're actually
 * interested in.
 */
public interface PetriNetComponentVisitor {

}
