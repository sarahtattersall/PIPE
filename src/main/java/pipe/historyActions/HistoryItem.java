package pipe.historyActions;

public abstract class HistoryItem
{
    public abstract void undo();
    public abstract void redo();

    public String toString()
    {
        return this.getClass().toString();
    }

}
