package pipe.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.util.LinkedList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import org.junit.Before;
import org.junit.Test;

import pipe.models.PetriNet;
import pipe.views.PetriNetView;
import pipe.views.PipeApplicationView;
import pipe.views.TokenView;

public class TokenActionTest
{

	private TokenAction tokenAction;
	private LinkedList<TokenView> tokenViews;
	private TokenView oneTokenView;
	private TokenView twoTokenView;
	private TokenView newOneTokenView;
	private TokenView newTwoTokenView;
	private PetriNetView petriNetView;
	private TestingPipeApplicationView pipeApplicationView;
	private LinkedList<TokenView> newTokenViews;

	@Before
	public void setUp() throws Exception
	{
		tokenAction = new TestingTokenAction(); 
		tokenViews = new LinkedList<TokenView>(); 
		newTokenViews = new LinkedList<TokenView>(); 
		oneTokenView = new TokenView(true, "Alpha", Color.black); 
		twoTokenView = new TokenView(true, "Beta", Color.blue);
		petriNetView = new PetriNetView(null, new PetriNet());
		tokenViews.add(oneTokenView);
		tokenViews.add(twoTokenView);
		assertTrue(petriNetView.updateOrReplaceTokenViews(tokenViews)); 
		oneTokenView = petriNetView.getTokenViews().get(0);  // updated Default
		assertEquals(2, petriNetView.getTokenViews().size());
		pipeApplicationView = new TestingPipeApplicationView(); 
		tokenAction.setPipeApplicationViewForTesting(pipeApplicationView); 
	}
	@Test
	public void verifyTokenViewsUpdatedWithValidTokenViewsAndTokenClassesComboBoxRefreshedEvenIfDisabled() throws Exception
	{
		newOneTokenView = new TokenView(true, "Alpha", Color.green); 
		newTwoTokenView = new TokenView(true, "Delta", Color.blue);
		tokenAction.filterValidTokenViews(newTokenViews, newOneTokenView);
		tokenAction.filterValidTokenViews(newTokenViews, newTwoTokenView);
		tokenAction.filterValidTokenViews(newTokenViews, new TokenView(false, "", Color.red));
		tokenAction.filterValidTokenViews(newTokenViews, new TokenView(false, "Omega", Color.yellow));
		assertEquals("only invalid token view is filtered out",3, newTokenViews.size()); 
		tokenAction.updateTokenViews(newTokenViews); 
		assertEquals("", tokenAction.getErrorMessage());
		assertEquals(3, petriNetView.getAllTokenViews().size());
		assertEquals(oneTokenView, petriNetView.getAllTokenViews().get(0)); 
		assertEquals("Alpha", petriNetView.getAllTokenViews().get(0).getID()); 
		assertEquals(Color.green, petriNetView.getAllTokenViews().get(0).getColor()); 
		assertEquals(twoTokenView, petriNetView.getAllTokenViews().get(1)); 
		assertEquals("Delta", petriNetView.getAllTokenViews().get(1).getID()); 
		assertEquals(Color.blue, petriNetView.getAllTokenViews().get(1).getColor());
		assertFalse("still returned for display",petriNetView.getAllTokenViews().get(2).isEnabled()); 
		assertEquals("Omega", petriNetView.getAllTokenViews().get(2).getID()); 
		assertEquals(Color.yellow, petriNetView.getAllTokenViews().get(2).getColor());
		pipeApplicationView.buildComboBoxForTesting(); 
		pipeApplicationView.refreshTokenClassChoices();
		assertEquals("Alpha",pipeApplicationView.tokenClassComboBox.getModel().getElementAt(0));  //DefaultComboBoxModel
		assertEquals("Delta",pipeApplicationView.tokenClassComboBox.getModel().getElementAt(1));  //DefaultComboBoxModel
		assertNull("disabled toke view doesn't show in the combo box",
				pipeApplicationView.tokenClassComboBox.getModel().getElementAt(2));
	}
	@Test
	public void verifyErrorMessageGeneratedIfTokenViewsInvalid() throws Exception
	{
		newOneTokenView = new TokenView(true, "", Color.green); 
		newTokenViews.add(newOneTokenView);
		tokenAction.updateTokenViews(newTokenViews); 
		assertTrue(tokenAction.getErrorMessage().startsWith(TokenAction.PROBLEM_ENCOUNTERED_SAVING_UPDATES));
	}
	private class TestingTokenAction extends TokenAction
	{
		private static final long serialVersionUID = 1L;

		@Override
		protected void showWarningAndReEnterTokenDialog()
		{
		}
	}
	private class TestingPipeApplicationView extends PipeApplicationView
	{
		private static final long serialVersionUID = 1L;
		public TestingPipeApplicationView()
		{
			super(); 
		}
		public void buildComboBoxForTesting()
		{
	        String[] tokenClassChoices = buildTokenClassChoices();
	        DefaultComboBoxModel model = new DefaultComboBoxModel(tokenClassChoices);
	        tokenClassComboBox = new JComboBox(model);
		}
		@Override
		public PetriNetView getCurrentPetriNetView()
		{
			return petriNetView; 
		}
	}
}
