package pipe.views;

import parser.ExprEvaluator;
import parser.MarkingDividedByNumberException;
import pipe.controllers.MarkingController;
import pipe.models.Marking;
import pipe.models.interfaces.IObserver;

import javax.swing.*;

import net.sourceforge.jeval.EvaluationException;

import java.awt.*;
import java.io.Serializable;

public class MarkingView extends JComponent implements Serializable, IObserver
{
    private TokenView _tokenView;
    private final Marking _model;
    private MarkingController _controller;
    
   

    public MarkingView(MarkingController controller, Marking model)
    {
        _controller = controller;
        _model = model;
        _model.registerObserver(this);
        _tokenView = new TokenView(_controller.getTokenController(), _model.getToken());
    }

    public MarkingView(TokenView tokenView, String marking)
    {
        _tokenView = tokenView;
        _model = new Marking(
        		tokenView.getModel()
        		, marking);
    }
    

    public MarkingView(TokenView tokenView, int marking)
    {
        _tokenView = tokenView;
        _model = new Marking(tokenView.getModel(), marking);
    }

    public TokenView getToken()
    {
        return _tokenView;
    }

    public void setToken(TokenView tokenView)
    {
        _tokenView = tokenView;
        _model.setToken(tokenView.getModel());
    }

    public void setCurrentMarking(int marking)
    {
        _model.setCurrentMarking(marking+"");
    }
    public void setCurrentMarking(String marking)
    {
        _model.setCurrentMarking(marking);
    }

    public int getCurrentMarking()
    {
		try {
			return Integer.parseInt(_model.getCurrentMarking());
		} catch (NumberFormatException e) {
			
			ExprEvaluator paser = new ExprEvaluator();
			int result;
			try {
				result = paser.parseAndEvalExpr(_model.getCurrentMarking(),_model.getToken().getId());
			} catch (EvaluationException e1) {
    			return showErrorMessage();
			} catch (MarkingDividedByNumberException e1) {
				return showErrorMessage();
			}	catch(Exception e1){
				return showErrorMessage();
			}
			return result;
		}
	}
    
    private int showErrorMessage(){
    	String message = "Errors in marking-dependent arc weight expression."+
				"\r\n The computation should be aborted";
        String title = "Error";
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.YES_NO_OPTION);
        return -1;
    }
    
    public String getCurrentFunctionalMarking(){
    	return _model.getCurrentMarking();
    }

    @Override
    public void update()
    {
       // paint()
    }

    public void update(Graphics canvas, Insets insets, int count, int tempTotalMarking)
    {
        _tokenView.update(canvas,insets,count, tempTotalMarking, getCurrentMarking());
    }
}
