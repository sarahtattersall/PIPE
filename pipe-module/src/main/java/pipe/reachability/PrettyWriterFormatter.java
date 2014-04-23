package pipe.reachability;

public class PrettyWriterFormatter implements WriterFormatter {

    @Override
    public String format(State state, State successor, double rate) {
        return String.format("%s to %s with rate %f\n", state.toString(), successor.toString(), rate);
    }

    @Override
    public Record read(String line) {
        return null;
    }
}
