package pipe.views;

import static org.junit.Assert.assertEquals;

import java.awt.Color;
import java.util.LinkedList;

import javax.swing.JToolBar;

import org.junit.Before;
import org.junit.Test;

import pipe.models.PetriNet;

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
		tokenViews = new LinkedList<TokenView>(); 
		oneTokenView = new TokenView(true, "Alpha", Color.black); 
		twoTokenView = new TokenView(true, "Beta", Color.blue);
		tokenViews.add(oneTokenView);
		tokenViews.add(twoTokenView);
	}
	@Test
	public void verifyTokenClassesBuiltAndRefreshed() throws Exception
	{
		pipeApplicationView = new TestingPipeApplicationView(); // constructor only for testing 
		pipeApplicationView.addTokenClassComboBox(new JToolBar(), null);
		assertEquals("Default",pipeApplicationView.tokenClassComboBox.getModel().getElementAt(0));  
		assertEquals(1,pipeApplicationView.tokenClassComboBox.getModel().getSize()); 

		petriNetView.updateOrReplaceTokenViews(tokenViews); 
		pipeApplicationView.refreshTokenClassChoices();
		assertEquals("Alpha",pipeApplicationView.tokenClassComboBox.getModel().getElementAt(0));  //DefaultComboBoxModel
		assertEquals("Beta",pipeApplicationView.tokenClassComboBox.getModel().getElementAt(1));  //DefaultComboBoxModel
		assertEquals(2,pipeApplicationView.tokenClassComboBox.getModel().getSize()); 
	}
	private class TestingPipeApplicationView extends PipeApplicationView
	{
		private static final long serialVersionUID = 1L;

		@Override
		public PetriNetView getCurrentPetriNetView()
		{
			return petriNetView; 
		}
	}

}
