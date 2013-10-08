package pipe.controllers;


import pipe.controllers.interfaces.IController;
import pipe.models.Place;
import pipe.views.PlaceView;

import java.util.ArrayList;

public class PlaceController implements IController
{
    private ArrayList<Place> _models;
    private ArrayList<PlaceView> _views;

    public PlaceController(Place model)
    {
        if(_models == null)
            _models = new ArrayList<Place>();
        if(_views == null)
            _views = new ArrayList<PlaceView>();
        _models.add(model);
        _views.add(new PlaceView(this, model));
    }

    public PlaceController()
    {
        
    }

    public void addModel(Place model)
    {
        _models.add(model);
    }

    public void removeModel(Place model)
    {
        _models.remove(model);
    }

    public void addView(PlaceView view)
    {
        _views.add(view);
    }

    public void removeView(PlaceView view)
    {
        _views.remove(view);
    }

}
