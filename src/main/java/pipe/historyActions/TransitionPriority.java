package pipe.historyActions;

import pipe.views.TransitionView;

public class TransitionPriority extends HistoryItem
{
    private final TransitionView _transitionView;
    private final Integer _newPriority;
    private final Integer _oldPriority;

    public TransitionPriority(TransitionView transitionView, Integer oldPriority, Integer newPriority)
    {
        _transitionView = transitionView;
        _oldPriority = oldPriority;
        _newPriority = newPriority;
    }

    public void undo()
    {
        _transitionView.setPriority(_oldPriority);
    }

    public void redo()
    {
        _transitionView.setPriority(_newPriority);
    }

}
