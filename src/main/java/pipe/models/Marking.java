package pipe.models;

import java.io.Serializable;
import java.util.Observable;


// TODO: Explain what marking is in comment
public class Marking extends Observable implements Serializable
{
    private Token token;
    private String marking;

    public Marking(Token token, String marking)
    {
        this.token = token;
        this.marking = marking;
    }
    
    public Marking(Token token, int marking){
    	this.token = token;
        this.marking = Integer.toString(marking);
    }

    public Token getToken()
    {
        return token;
    }

    public void setToken(Token token)
    {
        this.token = token;
        notifyObservers();
    }

    public void setCurrentMarking(String marking)
    {
        this.marking = marking;
        notifyObservers();
    }
    
    public void setCurrentMarking(int marking)
    {
        this.marking = marking+"";
        notifyObservers();
    }

    public String getCurrentMarking()
    {
        return marking;
    }
}
