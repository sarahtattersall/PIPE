package pipe.models;

import pipe.views.ArcView;

import java.io.Serializable;
import java.util.LinkedList;

/*
 * 
 *  @author yufei wang(modification)
 */
public class Connectable extends Observable implements Serializable
{
    private final LinkedList<ArcView> _inboundArcViews;
    private final LinkedList<ArcView> _outboundArcViews;
    private String _id;
    private String _name;

    Connectable(String id, String name)
    {
        _id = id;
        _name = name;
        _inboundArcViews = new LinkedList<ArcView>();
        _outboundArcViews = new LinkedList<ArcView>();
    }

    public LinkedList<ArcView> outboundArcs()
    {
        return _outboundArcViews;
    }

    public LinkedList<ArcView> inboundArcs()
    {
        return _inboundArcViews;
    }

    public void addInbound(ArcView newArcView)
    {
        _inboundArcViews.add(newArcView);
    }

    public void addOutbound(ArcView newArcView)
    {
        _outboundArcViews.add(newArcView);
    }

    public void addInboundOrOutbound(ArcView newArcView)
    {
        if(newArcView.getSource()._model == this)
            _outboundArcViews.add(newArcView);
        else
            _inboundArcViews.add(newArcView);
    }

    public void removeFromArcs(ArcView oldArcView)
    {
        _outboundArcViews.remove(oldArcView);
    }

    public void removeToArc(ArcView oldArcView)
    {
        _inboundArcViews.remove(oldArcView);
    }
    
    public String getName(){
    	return _name;
    }
    
    public String getId(){
    	return _id;
    }
}
