package pipe.dsl;

import org.junit.Before;
import org.junit.Test;
import pipe.models.component.place.Place;
import pipe.models.component.rate.RateParameter;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class ARateParameterTest {
    private Map<String, Token> tokens;

    private Map<String, Place> places;

    private Map<String, RateParameter> rateParameters;

    private Map<String, Transition> transitions;

    @Before
    public void setUp() {
        tokens = new HashMap<>();
        places = new HashMap<>();
        transitions = new HashMap<>();
        rateParameters = new HashMap<>();
    }

    @Test
    public void createsRateParameter() {
        RateParameter rateParameter =
                ARateParameter.withId("Foo").andExpression("5.0").create(tokens, places, transitions, rateParameters);
        RateParameter expected = new RateParameter("5.0", "Foo", "Foo");

        assertEquals(expected, rateParameter);
    }


    @Test
    public void addsRateParameterToRateParameters() {
        RateParameter rateParameter =
                ARateParameter.withId("Foo").andExpression("5.0").create(tokens, places, transitions, rateParameters);
        assertThat(rateParameters).containsEntry("Foo", rateParameter);
    }


}
