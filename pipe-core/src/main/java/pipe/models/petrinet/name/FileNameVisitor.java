package pipe.models.petrinet.name;

public interface FileNameVisitor extends NameVisitor {
    void visit (PetriNetFileName name);
}
