package pipe.controllers;

import pipe.models.Arc;
import pipe.models.NormalArc;
import pipe.views.ArcView;
import pipe.views.ConnectableView;
import pipe.views.NormalArcView;

import java.util.ArrayList;

public class ArcController
{
    private ArrayList<Arc> _models;
    private ArrayList<ArcView> _views;

    public ArcController(NormalArc model)
    {
        if(_models == null)
            _models = new ArrayList<Arc>();
        if(_views == null)
            _views = new ArrayList<ArcView>();
        _models.add(model);
        _views.add(new NormalArcView(this, model));
    }

    public void addModel(Arc arc)
    {
        _models.add(arc);
    }

    public void removeModel(Arc arc)
    {
        _models.remove(arc);
    }

    public void addView(ArcView arc)
    {
        _views.add(arc);
    }

    public void removeView(ArcView arc)
    {
        _views.remove(arc);
    }



    public NormalArcView copy(NormalArcView normalArcView)
    {
        return new NormalArcView(normalArcView);
    }

    public ConnectableView getSourceOf(ArcView arc) throws Exception
    {
        int index = _views.indexOf(arc);
        if (index <0)
            throw new Exception("An arc is not registered with the controller");
        return arc.getSource();
    }


    public ConnectableView getTargetOf(ArcView arc) throws Exception
    {
        int index = _views.indexOf(arc);
        if (index <0)
            throw new Exception("An arc is not registered with the controller");
        return arc.getTarget();
    }
}
