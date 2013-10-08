package pipe.controllers;

import pipe.controllers.interfaces.IController;
import pipe.models.Token;
import pipe.views.TokenView;

import java.util.ArrayList;

public class TokenController implements IController{
    private ArrayList<TokenView> _views;
    private ArrayList<Token> _models;

    public TokenController(Token model)
    {
        if(_models == null)
            _models = new ArrayList<Token>();
        if(_views == null)
            _views = new ArrayList<TokenView>();
        _models.add(model);
        _views.add(new TokenView(this, model));
    }

    public TokenController()
    {
        if(_models == null)
            _models = new ArrayList<Token>();
        if(_views == null)
            _views = new ArrayList<TokenView>();
    }

    public void addModel(Token model)
    {
        _models.add(model);
    }

    public void removeModel(Token model)
    {
        _models.remove(model);
    }

    public void addView(TokenView view)
    {
        _views.add(view);
    }

    public void removeView(TokenView view)
    {
        _views.remove(view);
    }
/*

    public void setCurrentMarking(int marking)
    {
        _model.setCurrentMarking(marking);
    }

    public int getCurrentMarking()
    {
        return _model.getCurrentMarking();
    }

    public Matrix getIncidenceMatrix()
    {
        return _model.getIncidenceMatrix();
    }

    public int[][] getIncidenceMatrix(ArrayList<ArcView> arcsArray, ArrayList<TransitionView> transitionsArray, ArrayList<PlaceView> placesArray)
    {
        return _model.getIncidenceMatrix(arcsArray, transitionsArray, placesArray);
    }

    public int[][] simpleMatrix()
    {
        return _model.simpleMatrix();
    }

    public void setIncidenceMatrix(Matrix incidenceMatrix)
    {
        _model.setIncidenceMatrix(incidenceMatrix);
    }

    public Matrix getForwardsIncidenceMatrix()
    {
        return _model.getForwardsIncidenceMatrix();
    }

    public int[][] getForwardsIncidenceMatrix(ArrayList<ArcView> arcsArray, ArrayList<TransitionView> transitionsArray, ArrayList<PlaceView> placesArray)
    {
        return _model.getForwardsIncidenceMatrix(arcsArray, transitionsArray, placesArray);
    }

    public int[][] simpleForwardsIncidenceMatrix()
    {
        return _model.simpleForwardsIncidenceMatrix();
    }

    public void setForwardsIncidenceMatrix(Matrix forwardsIncidenceMatrix)
    {
        _model.setForwardsIncidenceMatrix(forwardsIncidenceMatrix);
    }

    public Matrix getBackwardsIncidenceMatrix()
    {
        return _model.getBackwardsIncidenceMatrix();
    }

    public int[][] getBackwardsIncidenceMatrix(ArrayList<ArcView> arcsArray, ArrayList<TransitionView> transitionsArray, ArrayList<PlaceView> placesArray)
    {
        return _model.getBackwardsIncidenceMatrix(arcsArray, transitionsArray, placesArray);
    }

    public int[][] simpleBackwardsIncidenceMatrix()
    {
        return _model.simpleBackwardsIncidenceMatrix();
    }

    public void setBackwardsIncidenceMatrix(Matrix backwardsIncidenceMatrix)
    {
        _model.setBackwardsIncidenceMatrix(backwardsIncidenceMatrix);
    }

    public Matrix getInhibitionMatrix()
    {
        return _model.getInhibitionMatrix();
    }

    public int[][] getInhibitionMatrix(ArrayList<InhibitorArcView> inhibitorArrayView,ArrayList<TransitionView> transitionsArray, ArrayList<PlaceView> placesArray)
    {
        return _model.getInhibitionMatrix(inhibitorArrayView, transitionsArray, placesArray);
    }

    public void setInhibitionMatrix(Matrix inhibitionMatrix)
    {
        _model.setInhibitionMatrix(inhibitionMatrix);
    }

    public void incrementLock()
    {
        _model.incrementLock();
    }

    public void decrementLock()
    {
        _model.decrementLock();
    }

    public boolean isLocked()
    {
        return _model.isLocked();
    }

    public int getLockCount()
    {
        return _model.getLockCount();
    }

    public void setLockCount(int newLockCount)
    {
        _model.setLockCount(newLockCount);
    }

    public boolean isEnabled()
    {
        return _model.isEnabled();
    }

    public void setEnabled(boolean enabled)
    {
        _model.setEnabled(enabled);
    }

    public String getID()
    {
        return _model.getId();
    }

    public void setID(String id)
    {
        _model.setId(id);
    }

    public boolean hasSameId(TokenView otherTokenView)
    {
        return otherTokenView.getID().equals(getID());
    }

    public void createIncidenceMatrix(ArrayList<ArcView> arcsArray, ArrayList<TransitionView> transitionsArray, ArrayList<PlaceView> placesArray)
    {
        _model.createIncidenceMatrix(arcsArray, transitionsArray, placesArray);
    }

    public void createInhibitionMatrix(ArrayList<InhibitorArcView> inhibitorsArray,ArrayList<TransitionView> transitionsArray, ArrayList<PlaceView> placesArray)
    {
        _model.createInhibitionMatrix(inhibitorsArray, transitionsArray, placesArray);
    }
*/


}
