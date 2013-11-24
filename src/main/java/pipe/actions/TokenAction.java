package pipe.actions;

import pipe.gui.ApplicationSettings;
import pipe.gui.TokenDialog;
import pipe.gui.TokenPanel;
import pipe.views.PipeApplicationView;
import pipe.views.TokenView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
// 
// Steve Doubleday: refactored to simplify testing
//
public class TokenAction extends GuiAction
{
	private static final long serialVersionUID = 1L;
	protected static final String PROBLEM_ENCOUNTERED_SAVING_UPDATES = "Problem encountered saving updates to tokens.  Changes will be discarded; please re-enter.\n";
	private String errorMessage;
	private TokenPanel dialogContent;
	private PipeApplicationView pipeApplicationView;
	private JDialog guiDialog;
	private ActionEvent forcedAction;

	public TokenAction()
    {
        super("SpecifyTokenClasses", "Specify tokens", "shift ctrl T");
        setErrorMessage(""); 
    }

    public void actionPerformed(ActionEvent e)
    {
        buildTokenGuiClasses();
        finishBuildingGui(); 
        updateTokenViewsFromGui();
    }

	public void updateTokenViewsFromGui()
	{
		TokenPanel.TableModel x = (TokenPanel.TableModel) dialogContent.table.getModel();
		int rows = x.getRowCount();
        // If OK was pressed
        if(((TokenDialog) guiDialog).shouldAcceptChanges())
        {
            dialogContent.validate();
            buildAndUpdateTokenViews(x, rows);
        }
	}

	protected void buildAndUpdateTokenViews(TokenPanel.TableModel x, int rows)
	{
		LinkedList<TokenView> tokenViews = convertInputToTokenViews(x, rows);
		updateTokenViews(tokenViews);
		pipeApplicationView.refreshTokenClassChoices();
	}

	protected LinkedList<TokenView> convertInputToTokenViews(
			TokenPanel.TableModel x, int rows)
	{
		LinkedList<TokenView> tokenViews = new LinkedList<TokenView>();
		for(int i = 0; i < rows; i++)
		{
		    // Update token classes using data entered from the user
		    TokenView tc = buildTokenView(x, i);
		    filterValidTokenViews(tokenViews, tc);
		}
		return tokenViews;
	}

	protected void filterValidTokenViews(LinkedList<TokenView> tokenViews,
			TokenView tc)
	{
		// Only add TokenViews that are enabled with a non-blank ID
		if (tc.isValid()) tokenViews.add(tc);
	}
	protected void updateTokenViews(LinkedList<TokenView> tokenViews)
	{
		try 
		{
			pipeApplicationView.getCurrentPetriNetView().updateOrReplaceTokenViews(tokenViews);
		}
		catch (Exception e)
		{
			setErrorMessage(PROBLEM_ENCOUNTERED_SAVING_UPDATES +
					"Details: "+e.getMessage());
			showWarningAndReEnterTokenDialog(); 
		}
	}
	public void buildTokenGuiClasses()
	{
		pipeApplicationView = ApplicationSettings.getApplicationView();
		
		dialogContent = new TokenPanel();
		guiDialog = new TokenDialog(pipeApplicationView, "Tokens", true, dialogContent);
	}

	public void finishBuildingGui()
	{
		guiDialog.setSize(600, 200);
		guiDialog.setLocationRelativeTo(null);
		dialogContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10,
				10));
		dialogContent.setOpaque(true);
		
		JPanel buttonPane = new JPanel();
		buttonPane
		.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		buttonPane
		.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		buttonPane.add(Box.createHorizontalGlue());
		JButton ok = new JButton("OK");
		ok.addActionListener((ActionListener) guiDialog);
		buttonPane.add(ok);
		buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener((ActionListener) guiDialog);
		buttonPane.add(cancel);
		
		guiDialog.add(dialogContent, BorderLayout.CENTER);
		guiDialog.add(buttonPane, BorderLayout.PAGE_END);
		dialogContent.setVisible(true);
		
		if (forcedAction != null) forceContinue(); 
		else guiDialog.setVisible(true);
	}
	private void forceContinue()
	{
		((TokenDialog) guiDialog).actionPerformed(forcedAction);
		forcedAction = null; 
	}

	protected void showWarningAndReEnterTokenDialog()
	{
		JOptionPane
		.showMessageDialog(
				new JPanel(),
				getErrorMessage(),
				"Warning",
				JOptionPane.WARNING_MESSAGE);
		setErrorMessage(""); 
		actionPerformed(null);
	}
	protected TokenView buildTokenView(TokenPanel.TableModel x, int i)
	{
		TokenView tc = new TokenView(
		        (Boolean) x.getValueAt(i, 0), (String) x
		                .getValueAt(i, 1), (Color) x.getValueAt(i,
		                                                        2));
		return tc;
	}
	protected void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}
	protected String getErrorMessage()
	{
		return this.errorMessage;
	}
	protected void setPipeApplicationViewForTesting(PipeApplicationView pipeApplicationView)
	{
		this.pipeApplicationView = pipeApplicationView;
	}

	public TokenPanel getDialogContentForTesting()
	{
		return dialogContent;
	}

	public TokenDialog getGuiDialogForTesting()
	{
		return (TokenDialog) guiDialog;
	}

	public void forceOkForTesting()
	{
		forcedAction = new ActionEvent(this, 0, "OK");   
	}
}
