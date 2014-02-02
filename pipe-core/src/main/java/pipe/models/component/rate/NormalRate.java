package pipe.models.component.rate;

public class NormalRate implements Rate {
    public final String rate;

    public NormalRate(String rate) {
        this.rate = rate;
    }

    @Override
    public String getExpression() {
        return rate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        NormalRate that = (NormalRate) o;

        if (!rate.equals(that.rate)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return rate.hashCode();
    }
}
