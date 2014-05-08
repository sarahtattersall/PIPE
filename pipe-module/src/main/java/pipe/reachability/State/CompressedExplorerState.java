package pipe.reachability.state;

public class CompressedExplorerState {
    private final int hash1;

    private final int hash2;

    public CompressedExplorerState(int hash1, int hash2) {
        this.hash1 = hash1;
        this.hash2 = hash2;
    }

    @Override
    public int hashCode() {
        return hash1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompressedExplorerState)) {
            return false;
        }

        CompressedExplorerState that = (CompressedExplorerState) o;

        return that.hash2 == this.hash2;
    }

    public void accept(ExplorerStateVisitor visitor) {
        visitor.visit(this);
    }
}
