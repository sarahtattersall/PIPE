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
import pipe.models.component.Arc;
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
        Arc mockArc = mock(Arc.class);
        Point2D.Double start = new Point2D.Double(50, 39);
        Point2D.Double end = new Point2D.Double(100, 500);
        when(mockArc.getStartPoint()).thenReturn(start);
        when(mockArc.getEndPoint()).thenReturn(end);

        arcView = new NormalArcView(mockArc, mockController);
        ArcPath path = arcView.getArcPath();

        assertEquals(2, path.getNumPoints());
        assertEquals(start, path.getPoint(0));
        assertEquals(end, path.getPoint(1));
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
