package pipe.controllers;

import org.junit.Before;
import org.junit.Test;
import pipe.historyActions.AddArcPathPoint;
import pipe.historyActions.ArcPathPointType;
import pipe.historyActions.SetArcWeightAction;
import pipe.historyActions.HistoryManager;
import pipe.models.component.arc.Arc;
import pipe.models.component.arc.ArcPoint;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class ArcControllerTest {
    HistoryManager historyManager;
    Arc<Place, Transition> mockArc;
    ArcController<Place, Transition> controller;

    @Before
    public void setUp() {
        mockArc = mock(Arc.class);
        historyManager = mock(HistoryManager.class);

        controller = new ArcController(mockArc, historyManager);
    }

    @Test
    public void setWeightCreatesHistoryItem() {
        Token defaultToken = new Token("Default", true, 0, new Color(0, 0, 0));
        String oldWeight = "5";
        when(mockArc.getWeightForToken(defaultToken)).thenReturn(oldWeight);


        String newWeight = "51";
        controller.setWeight(defaultToken, newWeight);
        verify(historyManager).newEdit();

        SetArcWeightAction<Place, Transition> weightAction =
                new SetArcWeightAction<Place, Transition>(mockArc, defaultToken, oldWeight, newWeight);
        verify(historyManager).addEdit(weightAction);
    }

    @Test
    public void setWeightUpdatesArcWeight() {
        Token defaultToken = new Token("Default", true, 0, new Color(0, 0, 0));
        String oldWeight = "5";
        when(mockArc.getWeightForToken(defaultToken)).thenReturn(oldWeight);


        String newWeight = "51";
        controller.setWeight(defaultToken, newWeight);
        verify(mockArc).setWeight(defaultToken, newWeight);
    }

    @Test
    public void setWeightsCreatesHistoryItem() {
        Map<Token, String> tokenWeights = new HashMap<Token, String>();
        Token defaultToken = new Token("Default", true, 0, new Color(0, 0, 0));
        String oldWeight = "5";
        when(mockArc.getWeightForToken(defaultToken)).thenReturn(oldWeight);


        String newWeight = "51";
        tokenWeights.put(defaultToken, newWeight);
        controller.setWeights(tokenWeights);
        verify(historyManager).newEdit();

        SetArcWeightAction weightAction = new SetArcWeightAction(mockArc, defaultToken, oldWeight, newWeight);
        verify(historyManager).addEdit(weightAction);
    }

    @Test
    public void setWeightsUpdatesArc() {
        Map<Token, String> tokenWeights = new HashMap<Token, String>();
        Token defaultToken = new Token("Default", true, 0, new Color(0, 0, 0));
        String oldWeight = "5";
        when(mockArc.getWeightForToken(defaultToken)).thenReturn(oldWeight);


        String newWeight = "51";
        tokenWeights.put(defaultToken, newWeight);
        controller.setWeights(tokenWeights);
        verify(mockArc).setWeight(defaultToken, newWeight);
    }

    @Test
    public void splittingArcPointSplitsHalfWay() {
        ArcPoint nextPoint = new ArcPoint(new Point2D.Double(10, 10), false);
        ArcPoint splitPoint = new ArcPoint(new Point2D.Double(0, 0), false);

        when(mockArc.getNextPoint(splitPoint)).thenReturn(nextPoint);

        controller.splitArcPoint(splitPoint);
        ArcPoint expected = new ArcPoint(new Point2D.Double(5, 5), false);
        verify(mockArc).addIntermediatePoint(expected);
    }

    @Test
    public void splittingArcPointKeepsArcUncurved() {
        ArcPoint nextPoint = new ArcPoint(new Point2D.Double(10, 10), false);
        ArcPoint splitPoint = new ArcPoint(new Point2D.Double(0, 0), false);

        when(mockArc.getNextPoint(splitPoint)).thenReturn(nextPoint);

        controller.splitArcPoint(splitPoint);
        ArcPoint expected = new ArcPoint(new Point2D.Double(5, 5), false);
        verify(mockArc).addIntermediatePoint(expected);
    }

    @Test
    public void splittingArcPointKeepsArcCurved() {
        ArcPoint nextPoint = new ArcPoint(new Point2D.Double(10, 10), false);
        ArcPoint splitPoint = new ArcPoint(new Point2D.Double(0, 0), true);

        when(mockArc.getNextPoint(splitPoint)).thenReturn(nextPoint);

        controller.splitArcPoint(splitPoint);
        ArcPoint expected = new ArcPoint(new Point2D.Double(5, 5), true);
        verify(mockArc).addIntermediatePoint(expected);
    }

    @Test
    public void splittingCreatesHistoryItem() {
        ArcPoint nextPoint = new ArcPoint(new Point2D.Double(10, 10), false);
        ArcPoint splitPoint = new ArcPoint(new Point2D.Double(0, 0), true);

        when(mockArc.getNextPoint(splitPoint)).thenReturn(nextPoint);

        controller.splitArcPoint(splitPoint);
        ArcPoint expected = new ArcPoint(new Point2D.Double(5, 5), true);
        AddArcPathPoint addArcPointAction = new AddArcPathPoint(mockArc, expected);
        verify(historyManager).addNewEdit(addArcPointAction);
    }

    @Test
    public void toggleCreatesHistoryItem() {
        ArcPoint point = new ArcPoint(new Point2D.Double(0, 0), true);
        controller.toggleArcPointType(point);
        ArcPathPointType arcPathPointType = new ArcPathPointType(point);
        verify(historyManager).addNewEdit(arcPathPointType);
    }

    @Test
    public void arcPointCreatesAtSpecifiedPoint() {
        Point2D.Double point = new Point2D.Double(4, 1);
        controller.addPoint(point);

        ArcPoint expected = new ArcPoint(point, false);
        verify(mockArc).addIntermediatePoint(expected);
    }

    //    @Test
    //    public void addPointCreatesHistoryItem() {
    //        ArcPoint point = new ArcPoint(new Point2D.Double(0, 0), true);
    //        applicationController.addPoint(point.getPoint());
    //        AddArcPathPoint addArcPointAction = new AddArcPathPoint(mockArc, point);
    //        verify(historyManager).addNewEdit(addArcPointAction);
    //    }
}
