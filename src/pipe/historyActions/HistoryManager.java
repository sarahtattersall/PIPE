/*
 * UndoManager.java
 */
package pipe.historyActions;

import pipe.gui.ApplicationSettings;
import pipe.gui.Constants;
import pipe.gui.PetriNetTab;
import pipe.models.PipeApplicationModel;
import pipe.views.*;
import pipe.views.viewComponents.ArcPathPoint;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * Class to handle undo & redo functionality
 *
 * @author pere
 */
public class HistoryManager
{
    private static final int UNDO_BUFFER_CAPACITY = Constants.DEFAULT_BUFFER_SIZE;

    private int freePosition = 0; //index for new edits
    private int fillCount = 0; //
    private int startOfBuffer = 0; // index of the eldest element
    private int undoneEdits = 0;

    private final ArrayList<ArrayList> edits = new ArrayList(UNDO_BUFFER_CAPACITY);

    private final PetriNetTab _view;
    private final PetriNetView _model;
    private final PipeApplicationModel app;


    /**
     * Creates a new instance of HistoryManager
     * @param _view
     * @param _model
     */
    public HistoryManager(PetriNetTab _view, PetriNetView _model)
    {
        this._view = _view;
        this._model = _model;
        app = ApplicationSettings.getApplicationModel();
        app.setUndoActionEnabled(false);
        app.setRedoActionEnabled(false);
        for(int i = 0; i < UNDO_BUFFER_CAPACITY; i++)
        {
            edits.add(null);
        }
    }


    public void doRedo()
    {

        if(undoneEdits > 0)
        {
            checkArcBeingDrawn();
            checkMode();

            // The currentEdit to redo
            Iterator<HistoryItem> currentEdit = edits.get(freePosition).iterator();
            while(currentEdit.hasNext())
            {
                currentEdit.next().redo();
            }
            freePosition = (freePosition + 1) % UNDO_BUFFER_CAPACITY;
            fillCount++;
            undoneEdits--;
            if(undoneEdits == 0)
            {
                app.setRedoActionEnabled(false);
            }
            app.setUndoActionEnabled(true);
        }
    }


    public void doUndo()
    {

        if(fillCount > 0)
        {
            checkArcBeingDrawn();
            checkMode();

            if(--freePosition < 0)
            {
                freePosition += UNDO_BUFFER_CAPACITY;
            }
            fillCount--;
            undoneEdits++;

            // The currentEdit to undo (reverse order)
            ArrayList<HistoryItem> currentEdit = edits.get(freePosition);
            for(int i = currentEdit.size() - 1; i >= 0; i--)
            {
                currentEdit.get(i).undo();
            }

            if(fillCount == 0)
            {
                app.setUndoActionEnabled(false);
            }
            app.setRedoActionEnabled(true);
        }
    }


    public void clear()
    {
        freePosition = 0;
        fillCount = 0;
        startOfBuffer = 0;
        undoneEdits = 0;
        app.setUndoActionEnabled(false);
        app.setRedoActionEnabled(false);
    }


    public void newEdit()
    {
        ArrayList lastEdit = edits.get(currentIndex());
        if((lastEdit != null) && (lastEdit.isEmpty()))
        {
            return;
        }

        undoneEdits = 0;
        app.setUndoActionEnabled(true);
        app.setRedoActionEnabled(false);
        _view.setNetChanged(true);

        ArrayList<HistoryItem> compoundEdit = new ArrayList();
        edits.set(freePosition, compoundEdit);
        freePosition = (freePosition + 1) % UNDO_BUFFER_CAPACITY;
        if(fillCount < UNDO_BUFFER_CAPACITY)
        {
            fillCount++;
        }
        else
        {
            startOfBuffer = (startOfBuffer + 1) % UNDO_BUFFER_CAPACITY;
        }
    }


    public void addEdit(HistoryItem historyItem)
    {
        ArrayList<HistoryItem> compoundEdit = edits.get(currentIndex());
        compoundEdit.add(historyItem);
        //debug();
    }


    public void addNewEdit(HistoryItem historyItem)
    {
        newEdit(); // mark for a new "transtaction""
        addEdit(historyItem);
    }


    public void deleteSelection(PetriNetViewComponent pn)
    {
        deleteObject(pn);
    }


    public void deleteSelection(ArrayList<PetriNetViewComponent> selection)
    {
        for(PetriNetViewComponent pn : selection)
        {
            deleteObject(pn);
        }
    }


    public void translateSelection(ArrayList objects, int transX, int transY)
    {
        newEdit(); // new "transaction""
        Iterator<PetriNetViewComponent> iterator = objects.iterator();
        while(iterator.hasNext())
        {
            addEdit(new TranslatePetriNetObject(
                    iterator.next(), transX, transY));
        }
    }


    private int currentIndex()
    {
        int lastAdd = freePosition - 1;
        if(lastAdd < 0)
        {
            lastAdd += UNDO_BUFFER_CAPACITY;
        }
        return lastAdd;
    }


    // removes the arc currently being drawn if any
    private void checkArcBeingDrawn()
    {
        ArcView arcBeingDrawn = _view._createArcView;
        if(arcBeingDrawn != null)
        {
            if(arcBeingDrawn.getParent() != null)
            {
                arcBeingDrawn.getParent().remove(arcBeingDrawn);
            }
            _view._createArcView = null;
        }
    }


    private void checkMode()
    {
        if((app.getMode() == Constants.FAST_PLACE) ||
                (app.getMode() == Constants.FAST_TRANSITION))
        {
            app.resetMode();
        }
    }


    private void deleteObject(PetriNetViewComponent pn)
    {
        if(pn instanceof ArcPathPoint)
        {
            if(!((ArcPathPoint) pn).getArcPath().getArc().isSelected())
            {
                addEdit(new DeleteArcPathPoint(
                        ((ArcPathPoint) pn).getArcPath().getArc(),
                        (ArcPathPoint) pn, ((ArcPathPoint) pn).getIndex()));
            }
        }
        else
        {
            if(pn instanceof ConnectableView)
            {
                //
                Iterator arcsTo =
                        ((ConnectableView) pn).getConnectToIterator();
                while(arcsTo.hasNext())
                {
                    ArcView anArc = (ArcView) arcsTo.next();
                    if(!anArc.isDeleted())
                    {
                        addEdit(new DeletePetriNetObject(anArc, _view, _model));
                    }
                }
                //
                Iterator arcsFrom =
                        ((ConnectableView) pn).getConnectFromIterator();
                while(arcsFrom.hasNext())
                {
                    ArcView anArc = (ArcView) arcsFrom.next();
                    if(!anArc.isDeleted())
                    {
                        addEdit(new DeletePetriNetObject(anArc, _view, _model));
                    }
                }

            }
            else if(pn instanceof NormalArcView)
            {
                if(((NormalArcView) pn).hasInverse())
                {
                    if(((NormalArcView) pn).hasInvisibleInverse())
                    {
                        addEdit(((NormalArcView) pn).split());
                        NormalArcView inverse = ((NormalArcView) pn).getInverse();
                        addEdit(((NormalArcView) pn).clearInverse());
                        addEdit(new DeletePetriNetObject(inverse, _view, _model));
                        inverse.delete();
                    }
                    else
                    {
                        addEdit(((NormalArcView) pn).clearInverse());
                    }
                }
            }

            if(!pn.isDeleted())
            {
                addEdit(new DeletePetriNetObject(pn, _view, _model));
                pn.delete();
            }
        }
    }


    private void debug()
    {
        int i = startOfBuffer;
        System.out.println("");
        for(int k = 0; k < fillCount; k++)
        {
            Iterator<HistoryItem> currentEdit = edits.get(i).iterator();
            while(currentEdit.hasNext())
            {
                System.out.println("[" + i + "]" + currentEdit.next().toString());
            }
            i = (i + 1) % UNDO_BUFFER_CAPACITY;
        }
    }

}
