package pipe.animation;

import java.io.Serializable;

public class TokenCount implements Serializable {
    public final String token;

    public int count;

    public TokenCount(String token, int count) {

        this.token = token;
        this.count = count;
    }

    @Override
    public int hashCode() {
        int result = token.hashCode();
        result = 31 * result + count;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TokenCount)) {
            return false;
        }

        TokenCount that = (TokenCount) o;

        if (count != that.count) {
            return false;
        }
        if (!token.equals(that.token)) {
            return false;
        }

        return true;
    }


}
