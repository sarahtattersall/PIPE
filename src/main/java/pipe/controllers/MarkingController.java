package pipe.controllers;

import pipe.models.Marking;
import pipe.views.MarkingView;

import java.util.ArrayList;

public class MarkingController
{
    private TokenController _tokenController;

    private ArrayList<Marking> _models;
    private ArrayList<MarkingView> _views;

    public MarkingController(Marking model)
    {
        if(_models == null)
            _models = new ArrayList<Marking>();
        if(_views == null)
            _views = new ArrayList<MarkingView>();
        _models.add(model);
        _views.add(new MarkingView(this, model));
    }

    public void addModel(Marking model)
    {
        _models.add(model);
    }

    public void removeModel(Marking model)
    {
        _models.remove(model);
    }

    public void addView(MarkingView view)
    {
        _views.add(view);
    }

    public void removeView(MarkingView view)
    {
        _views.remove(view);
    }

    public MarkingController(TokenController tokenController)
    {
        _tokenController = tokenController;
    }

    public TokenController getTokenController()
    {
        return _tokenController;
    }
}
