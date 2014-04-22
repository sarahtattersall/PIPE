package pipe.models.petrinet;

import pipe.models.component.place.Place;
import pipe.models.component.transition.Transition;

import java.util.HashMap;
import java.util.Map;

/**
 * Incidence matrix for storing values for Places and Transitions
 */
public class IncidenceMatrix {

    /**
     * Holds weight between connected components
     */
    final Map<MatrixElement, Integer> matrixValues = new HashMap<MatrixElement, Integer>();


    public void put(Place place, Transition transition, int value) {
        MatrixElement matrixElement = new MatrixElement(place, transition);
        matrixValues.put(matrixElement, value);

    }

    /**
     * Behaives like a dense matrix, so if place, transition are not in matrixValues zero is returned
     *
     * @param place
     * @param transition
     * @return place transition weight
     */
    public int get(Place place, Transition transition) {
        MatrixElement matrixElement = new MatrixElement(place, transition);
        return matrixValues.containsKey(matrixElement) ? matrixValues.get(matrixElement) : 0;
    }


    /**
     * A connection is a pair of Place and Transition and represents x and y in a matrix
     */
    private static class MatrixElement {
        final Place place;

        final Transition transition;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            MatrixElement that = (MatrixElement) o;

            if (!place.equals(that.place)) {
                return false;
            }
            if (!transition.equals(that.transition)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = place.hashCode();
            result = 31 * result + transition.hashCode();
            return result;
        }

        private MatrixElement(Place place, Transition transition) {
            this.place = place;
            this.transition = transition;
        }
    }
}
