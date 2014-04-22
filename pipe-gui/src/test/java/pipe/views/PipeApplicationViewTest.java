package pipe.views;

import static org.junit.Assert.assertEquals;

import java.awt.Color;
import java.util.LinkedList;

import org.junit.Before;

import pipe.models.petrinet.PetriNet;

public class PipeApplicationViewTest
{

	private PipeApplicationView pipeApplicationView;

	private LinkedList<TokenView> tokenViews;
	private TokenView oneTokenView;
	private TokenView twoTokenView;
	private PetriNetView petriNetView;

	@Before
	public void setUp() throws Exception
	{
		petriNetView = new PetriNetView(null, new PetriNet());
		tokenViews = new LinkedList<>();
		oneTokenView = new TokenView("Alpha", Color.black);
		twoTokenView = new TokenView("Beta", Color.blue);
		tokenViews.add(oneTokenView);
		tokenViews.add(twoTokenView);
	}


}
