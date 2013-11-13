package pipe.views;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import pipe.controllers.PetriNetController;
import pipe.gui.ApplicationSettings;
import pipe.historyActions.HistoryItem;
import pipe.models.Arc;
import pipe.models.NormalArc;
import pipe.views.viewComponents.ArcPath;
import pipe.views.viewComponents.NameLabel;

public class ArcViewTest
{

	private MarkingView markingView;
	private TokenView tokenView;
	private ArcView arcView;
    private PetriNetController mockController;

	@Before
	public void setUp() throws Exception
	{
		tokenView = new TokenView(true, "Default", Color.black);
        mockController = mock(PetriNetController.class);
	}

    @Test
    public void ArcViewSetsCorrectPathInConstructor()
    {
        NormalArc mockArc = mock(NormalArc.class);
        Point2D.Double start = new Point2D.Double(50, 39);
        Point2D.Double end = new Point2D.Double(100, 500);
        when(mockArc.getStartPoint()).thenReturn(start);
        when(mockArc.getEndPoint()).thenReturn(end);

        arcView = new NormalArcView(0, 0, 0, 0, null, null, new LinkedList<MarkingView>(),"id", true,  mockArc, mockController);
        ArcPath path = arcView.getArcPath();

        assertEquals(2, path.getNumPoints());
        assertEquals(start, path.getPoint(0));
        assertEquals(end, path.getPoint(1));
    }

	@Test
	public void verifyDeletesMarkingViewWhenItRequestsUpdate() throws Exception
	{
        PipeApplicationView view = null;
        ApplicationSettings.register(view);

		ConnectableView connectableView = null;
		arcView = new TestingArcView(connectableView);

	    assertEquals(0, arcView.getWeight().size());

        LinkedList<MarkingView> weight = new LinkedList<MarkingView>();
		markingView = new MarkingView(tokenView, 0); 
		weight.add(markingView);

        arcView._weight = weight;

        assertEquals(1, arcView.getWeight().size());
		assertEquals(0, arcView.weightLabel.size());

//        ((NormalArcView) arcView).buildNameLabels(weight);

        assertEquals(markingView, arcView.getWeight().get(0));
		assertEquals(1, arcView.weightLabel.size());

        arcView.addThisAsObserverToWeight(weight);
		// notify is asynchronous, but it during testing it seems to run in one thread, i.e., sequentially,
		// so haven't bothered with sleeping til done (see MarkingViewTest)
		tokenView.disableAndNotifyObservers();
		assertEquals("tokenView, then markingView request deletion",0, arcView.getWeight().size());
		assertEquals(0, arcView.weightLabel.size());
	}

	private class TestingArcView extends NormalArcView
	{
		private static final long serialVersionUID = 1L;
		public TestingArcView(ConnectableView newSource)
		{
			super(newSource);
		}
		@Override
		protected void updateHistory(HistoryItem historyItem)
		{
		}
		@Override
		protected void removeLabelFromParentContainer(NameLabel label)
		{
		}
	}
}
