package pipe.views;

import pipe.models.petrinet.ExprEvaluator;
import pipe.controllers.MarkingController;
import pipe.models.Marking;
import pipe.models.PipeObservable;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;

//  Steve Doubleday:  changed from IObserver to Observer interface to make use of 
//  the update(Observable Object) method for TokenViews

public class MarkingView extends JComponent implements Serializable, Observer {
    private TokenView _tokenView;
    private final Marking _model;
    private MarkingController _controller;
    private PipeObservable _pipeObservable;

    public MarkingView(MarkingController controller, Marking model) {
        _controller = controller;
        _model = model;
        _tokenView = new TokenView(_controller.getTokenController(), _model.getToken());
        _tokenView.addObserver(this);
        _pipeObservable = new PipeObservable(this);
    }

    public MarkingView(TokenView tokenView, String marking) {
        _tokenView = tokenView;
        _tokenView.addObserver(this);
        _model = new Marking(tokenView.getModel(), marking);
        _pipeObservable = new PipeObservable(this);
    }


    public MarkingView(TokenView tokenView, int marking) {
        _tokenView = tokenView;
        _model = new Marking(tokenView.getModel(), marking);
        _tokenView.addObserver(this);
        _pipeObservable = new PipeObservable(this);
    }

    public TokenView getToken() {
        return _tokenView;
    }

    public void setToken(TokenView tokenView) {
        _tokenView = tokenView;
        if (_tokenView != null) {
            _tokenView.addObserver(this);
            _model.setToken(tokenView.getModel());
        }
    }

    public void setCurrentMarking(int marking) {
        _model.setCurrentMarking(marking + "");
    }

    public void setCurrentMarking(String marking) {
        _model.setCurrentMarking(marking);
    }

    public int getCurrentMarking() {
        try {
            int result = Integer.parseInt(_model.getCurrentMarking());
            return result;
        } catch (NumberFormatException e) {

            //TODO: DONT PASS NULL
            ExprEvaluator paser = new ExprEvaluator(null);
            int result;
            try {
                result = paser.parseAndEvalExpr(_model.getCurrentMarking(), _model.getToken().getId());
            } catch (Exception e1) {
                return showErrorMessage();
            }
            return result;
        }
    }

    private int showErrorMessage() {
        String message =
                "Errors in marking-dependent arc weight expression." + "\r\n The computation should be aborted";
        String title = "Error";
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.YES_NO_OPTION);
        return -1;
    }

    public String getCurrentFunctionalMarking() {
        return _model.getCurrentMarking();
    }


    public void update(Graphics canvas, Insets insets, int count, int tempTotalMarking) {
        _tokenView.update(canvas, insets, count, tempTotalMarking, getCurrentMarking());
    }

    @Override
    public void update(Observable oldObj, Object newObj) {
        if (oldObj instanceof TokenView) {
            if (newObj == null) {
                setToken(null);
            } else if (newObj instanceof TokenView) {
                setToken((TokenView) newObj);
            }
            setChanged();
            notifyObservers(null);
        }
    }

    // Delegate to Observable
    public void addObserver(Observer observer) {
        _pipeObservable.addObserver(observer);
    }

    public void notifyObservers(Object arg) {
        _pipeObservable.notifyObservers(arg);
    }

    public void setChanged() {
        _pipeObservable.setChanged();
    }

    protected PipeObservable getObservable() {
        return _pipeObservable;
    }
}
