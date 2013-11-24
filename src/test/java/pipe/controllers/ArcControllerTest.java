package pipe.controllers;

import org.junit.Before;
import org.junit.Test;
import pipe.historyActions.ArcWeight;
import pipe.historyActions.HistoryManager;
import pipe.models.component.Arc;
import pipe.models.component.Token;
import utils.TokenUtils;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ArcControllerTest {
    HistoryManager historyManager;
    Arc arc;
    ArcController controller;

    @Before
    public void setUp() {
        arc = mock(Arc.class);
        historyManager = mock(HistoryManager.class);

        controller = new ArcController(arc, historyManager);
    }

    @Test
    public void setWeightCreatesHistoryItem() {
        Token defaultToken = TokenUtils.createDefaultToken();
        String oldWeight = "5";
        when(arc.getWeightForToken(defaultToken)).thenReturn(oldWeight);


        String newWeight = "51";
        controller.setWeight(defaultToken, newWeight);
        verify(historyManager).newEdit();

        ArcWeight weightAction = new ArcWeight(arc, defaultToken, oldWeight, newWeight);
        verify(historyManager).addEdit(weightAction);
    }

    @Test
    public void setWeightUpdatesArcWeight() {
        Token defaultToken = TokenUtils.createDefaultToken();
        String oldWeight = "5";
        when(arc.getWeightForToken(defaultToken)).thenReturn(oldWeight);


        String newWeight = "51";
        controller.setWeight(defaultToken, newWeight);
        verify(arc).setWeight(defaultToken, newWeight);
    }

    @Test
    public void setWeightsCreatesHistoryItem() {
        Map<Token, String> tokenWeights = new HashMap<Token, String>();
        Token defaultToken = TokenUtils.createDefaultToken();
        String oldWeight = "5";
        when(arc.getWeightForToken(defaultToken)).thenReturn(oldWeight);


        String newWeight = "51";
        tokenWeights.put(defaultToken, newWeight);
        controller.setWeights(tokenWeights);
        verify(historyManager).newEdit();

        ArcWeight weightAction = new ArcWeight(arc, defaultToken, oldWeight, newWeight);
        verify(historyManager).addEdit(weightAction);
    }

    @Test
    public void setWeightsUpdatesArc() {
        Map<Token, String> tokenWeights = new HashMap<Token, String>();
        Token defaultToken = TokenUtils.createDefaultToken();
        String oldWeight = "5";
        when(arc.getWeightForToken(defaultToken)).thenReturn(oldWeight);


        String newWeight = "51";
        tokenWeights.put(defaultToken, newWeight);
        controller.setWeights(tokenWeights);
        verify(arc).setWeight(defaultToken, newWeight);
    }
}
