package pipe.models.petrinet.name;

public interface NormalNameVisitor extends NameVisitor {
    public void visit(NormalPetriNetName name);
}
