package pipe.views;

import static org.junit.Assert.assertEquals;

import java.awt.Color;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import pipe.historyActions.HistoryItem;
import pipe.views.viewComponents.NameLabel;

public class ArcViewTest
{

	private MarkingView markingView;
	private TokenView tokenView;
	private ArcView arcView;
	@Before
	public void setUp() throws Exception
	{
		tokenView = new TokenView(true, "Default", Color.black);
	}
	@Test
	public void verifyDeletesMarkingViewWhenItRequestsUpdate() throws Exception
	{
		ConnectableView connectableView = null;
		arcView = new TestingArcView(connectableView);
		assertEquals(0, arcView.getWeight().size());
		LinkedList<MarkingView> weight = new LinkedList<MarkingView>(); 
		markingView = new MarkingView(tokenView, 0); 
		weight.add(markingView); 
		arcView._weight = weight;
		assertEquals(1, arcView.getWeight().size());
		assertEquals(0, arcView.weightLabel.size());
		((NormalArcView) arcView).buildNameLabels(weight); 
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
