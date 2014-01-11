package pipe.math;

import pipe.models.component.Place;
import pipe.models.component.Transition;

import java.util.HashMap;
import java.util.Map;

/**
 * Incidence matrix for storing values for Places and Transitions
 */
public class IncidenceMatrix {

    /**
     * Holds weight between connection
     */
    final Map<Connection, Integer> matrixValues = new HashMap<Connection, Integer>();


    public void put(Place place, Transition transition, int value) {
        Connection connection = new Connection(place, transition);
        matrixValues.put(connection, value);

    }

    /**
     *
     * Behaives like a dense matrix, so if place, transition are not in matrixValues zero is returned
     *
     * @param place
     * @param transition
     * @return place transition weight
     */
    public int get(Place place, Transition transition) {
        Connection connection = new Connection(place, transition);
        return matrixValues.containsKey(connection) ? matrixValues.get(connection) : 0;
    }


    /**
     * A connection is a pair of Place and Transition and represents x and y in a matrix
     */
    private class Connection {
        final Place place;
        final Transition transition;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Connection that = (Connection) o;

            if (!place.equals(that.place)) return false;
            if (!transition.equals(that.transition)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = place.hashCode();
            result = 31 * result + transition.hashCode();
            return result;
        }

        private Connection(Place place, Transition transition) {
            this.place = place;
            this.transition = transition;
        }
    }
}
