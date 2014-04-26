package pipe.reachability;

/**
 * Record of state space exploration
 * Contains state to successor with rate
 */
public class Record {

    public final State state;
    public final State successor;
    public final double rate;

    public Record(State state, State successor, double rate) {
        this.state = state;
        this.successor = successor;
        this.rate = rate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Record)) {
            return false;
        }

        Record record = (Record) o;

        if (Double.compare(record.rate, rate) != 0) {
            return false;
        }
        if (!state.equals(record.state)) {
            return false;
        }
        if (!successor.equals(record.successor)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = state.hashCode();
        result = 31 * result + successor.hashCode();
        temp = Double.doubleToLongBits(rate);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
