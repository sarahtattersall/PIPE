package pipe.views;

import org.junit.Before;
import org.junit.Test;
import pipe.models.PipeObservable;
import pipe.models.component.token.Token;

import java.awt.Color;
import java.util.Observable;
import java.util.Observer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MarkingViewTest implements Observer {
    private MarkingView markingView;

    private TokenView tokenView;

    @Before
    public void setUp() throws Exception {
        tokenView = new TokenView("Fred", Color.black);
    }

    @Test
    public void verifyTokenViewCanBeReplacedWithNewInstance() throws Exception {
        Token model = tokenView.getModel();
        markingView = new MarkingView(tokenView, 3);
        assertEquals("Fred", markingView.getToken().getID());
        TokenView newTokenView = new TokenView("Mary", Color.green);
        newTokenView.updateModelFromPrevious(tokenView);
        assertEquals(newTokenView, markingView.getToken());
        assertEquals("Mary", markingView.getToken().getID());
        assertEquals(Color.green, markingView.getToken().getColor());
        assertEquals(model, markingView.getToken().getModel());
    }

    @Test
    public void verifyTokenViewThatIsLaterDisabledGeneratesNullUpdate() throws Exception {
        markingView = new MarkingView(tokenView, 3);
        assertEquals(tokenView, markingView.getToken());
        tokenView.disableAndNotifyObservers();
        assertNull(markingView.getToken());
    }

    @Override
    public void update(Observable oldObject, Object newObject) {
        MarkingView view = null;
        if (oldObject instanceof PipeObservable) {
            view = (MarkingView) ((PipeObservable) oldObject).getObservable();
        }
        assertEquals(view, markingView);
    }

}
