package pipe.controllers;

import pipe.controllers.interfaces.IController;
import pipe.models.Transition;
import pipe.views.TransitionView;

import java.util.ArrayList;

public class TransitionController implements IController
{
    private ArrayList<Transition> _models;
    private ArrayList<TransitionView> _views;

    public TransitionController(Transition model)
    {
        if(_models == null)
            _models = new ArrayList<Transition>();
        if(_views == null)
            _views = new ArrayList<TransitionView>();
        _models.add(model);
        _views.add(new TransitionView(this, model));
    }

    public TransitionController()
    {
        
    }

    public void addModel(Transition model)
    {
        _models.add(model);
    }

    public void removeModel(Transition model)
    {
        _models.remove(model);
    }

    public void addView(TransitionView view)
    {
        _views.add(view);
    }

    public void removeView(TransitionView view)
    {
        _views.remove(view);
    }
}
