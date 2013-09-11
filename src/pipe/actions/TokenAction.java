package pipe.actions;

import pipe.gui.ApplicationSettings;
import pipe.views.ArcView;
import pipe.views.PipeApplicationView;
import pipe.gui.TokenDialog;
import pipe.gui.TokenPanel;
import pipe.views.TokenView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;

public class TokenAction extends GuiAction
{

    public TokenAction()
    {
        super("SpecifyTokenClasses", "Specify tokens", "shift ctrl T");
    }

    public void actionPerformed(ActionEvent e)
    {
        PipeApplicationView pipeApplicationView = ApplicationSettings.getApplicationView();

        TokenPanel dialogContent = new TokenPanel();
        JDialog guiDialog = new TokenDialog(pipeApplicationView,
                                            "Tokens", true, dialogContent);
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

        guiDialog.setVisible(true);
        TokenPanel.TableModel x = (TokenPanel.TableModel) dialogContent.table.getModel();
        int rows = x.getRowCount();
        // If OK was pressed
        if(((TokenDialog) guiDialog).shouldAcceptChanges())
        {
            dialogContent.validate();
            LinkedList<TokenView> tokenViews = new LinkedList<TokenView>();
            for(int i = 0; i < rows; i++)
            {
                // Update token classes using data entered from the user
                TokenView tc = new TokenView(
                        (Boolean) x.getValueAt(i, 0), (String) x
                                .getValueAt(i, 1), (Color) x.getValueAt(i,
                                                                        2));
                tokenViews.add(tc);
            }
            pipeApplicationView.getCurrentPetriNetView().setTokenViews(tokenViews);
            pipeApplicationView.refreshTokenClassChoices();
        }
    }
}
