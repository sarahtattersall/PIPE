package pipe.utilities;

import static org.junit.Assert.*;

import java.awt.Color;
import java.util.LinkedList;

import org.junit.Test;

import pipe.views.MarkingView;
import pipe.views.TokenView;

public class CopierTest
{

	
	private TokenView tokenView;
	private MarkingView markingView;

	@Test
	public void verifyMediumCopyOfTokenViewObservables() throws Exception
	{
		tokenView = new TokenView(true, "Default", Color.black);
		markingView = new MarkingView(tokenView, 1);
		assertEquals(1, tokenView.countObservers());
		LinkedList<MarkingView> markingViews = new LinkedList<MarkingView>(); 
		markingViews.add(markingView);
		@SuppressWarnings("unused")
		LinkedList<MarkingView> newMarkingViews = Copier.mediumCopy(markingViews);   
		assertEquals(2, tokenView.countObservers());
		
	}

}
