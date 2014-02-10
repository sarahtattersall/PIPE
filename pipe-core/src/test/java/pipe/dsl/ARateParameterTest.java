package pipe.dsl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pipe.models.component.Connectable;
import pipe.models.component.rate.RateParameter;
import pipe.models.component.token.Token;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;

public class ARateParameterTest {
    private Map<String, Token> tokens;

    private Map<String, Connectable> connectables;

    private Map<String, RateParameter> rateParameters;

    @Before
    public void setUp() {
        tokens = new HashMap<>();
        connectables = new HashMap<>();
        rateParameters = new HashMap<>();
    }

    @Test
    public void createsRateParameter() {
        RateParameter rateParameter =
                ARateParameter.withId("Foo").andExpression("5.0").create(tokens, connectables, rateParameters);
        RateParameter expected = new RateParameter("5.0", "Foo", "Foo");

        assertEquals(expected, rateParameter);
    }


    @Test
    public void addsRateParameterToRateParameters() {
        RateParameter rateParameter =
                ARateParameter.withId("Foo").andExpression("5.0").create(tokens, connectables, rateParameters);
        assertThat(rateParameters).containsEntry("Foo", rateParameter);
    }


}
