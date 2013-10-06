package pipe.models;

import java.io.Serializable;
import java.util.Observable;



public class Marking extends Observable implements Serializable
{
    private Token _token;
    private String _currentMarking;

    public Marking()
    {
        _token = new Token();
    }

    public Marking(Token token, String marking)
    {
        _token = token;
        _currentMarking = marking;
    }
    
    public Marking(Token token, int marking){
    	_token = token;
        _currentMarking = marking+"";
    }

    public Token getToken()
    {
        return _token;
    }

    public void setToken(Token token)
    {
        _token = token;
        notifyObservers();
    }

    public void setCurrentMarking(String marking)
    {
        _currentMarking = marking;
        notifyObservers();
    }
    
    public void setCurrentMarking(int marking)
    {
        _currentMarking = marking+"";
        notifyObservers();
    }

    public String getCurrentMarking()
    {
        return _currentMarking;
    }
}
