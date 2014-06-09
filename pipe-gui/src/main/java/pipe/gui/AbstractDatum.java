package pipe.gui;

/**
 * Abstract Datum item for rate and token editng tables
 */
public class AbstractDatum {
    /**
     * Mapping to an initial datum item
     * This may be null if the Datum was not originally a token in the Petri net
     * <p/>
     * It will contain a value if the data is a modified datum
     * and it maps directly to some initial datum
     */
    public AbstractDatum initial = null;

    /**
     * Datum id
     */
    public String id;

    /**
     * Constructor
     * @param id
     */
    AbstractDatum(String id) {
        this.id = id;
    }

    /**
     * Constructor
     * @param initial
     * @param id
     */
    AbstractDatum(AbstractDatum initial, String id) {
        this.id = id;
        this.initial = initial;
    }

    /**
     *
     * @return true if the id has been changed
     */
    public final boolean hasBeenSet() {
        return !this.id.equals("");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractDatum)) {
            return false;
        }

        AbstractDatum that = (AbstractDatum) o;

        if (!id.equals(that.id)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
