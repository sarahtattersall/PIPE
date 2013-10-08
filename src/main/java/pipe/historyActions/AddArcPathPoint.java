package pipe.historyActions;


import pipe.views.ArcView;
import pipe.views.viewComponents.ArcPath;
import pipe.views.viewComponents.ArcPathPoint;

public class AddArcPathPoint extends HistoryItem
{
    private final ArcPath arcPath;
    private final ArcPathPoint point;
    private final Integer index;

    public AddArcPathPoint(ArcView _arc, ArcPathPoint _point)
    {
        arcPath = _arc.getArcPath();
        point = _point;
        index = point.getIndex();
    }

    public void undo()
    {
        point.delete();
    }

    public void redo()
    {
        arcPath.insertPoint(index, point);
        arcPath.updateArc();
    }

}
