package pipe.models.petrinet.name;

public class NormalPetriNetName implements PetriNetName {
    String name;

    public NormalPetriNetName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void visit(NameVisitor visitor) {
        if (visitor instanceof FileNameVisitor) {
            ((NormalNameVisitor) visitor).visit(this);
        }
    }
}
