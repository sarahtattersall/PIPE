package pipe.controllers;

import pipe.controllers.interfaces.IController;
import pipe.views.TokenView;
import uk.ac.imperial.pipe.models.component.token.Token;

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

}
