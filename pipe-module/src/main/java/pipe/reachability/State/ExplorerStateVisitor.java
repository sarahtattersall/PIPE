package pipe.reachability.state;

public interface ExplorerStateVisitor {
    void visit(HashedExplorerState state);

    void visit(CompressedExplorerState state);
}
