package pipe.historyActions;

import pipe.models.component.rate.RateParameter;

/**
 * Responsible for undo/redoing the change to the rateParameter id
 */
public class RateParameterId extends HistoryItem {
    /**
     * Rate parameter whos id must change
     */
    private final RateParameter rateParameter;

    /**
     * Previous id value
     */
    private final String oldId;

    /**
     * Latest id value
     */
    private final String newId;

    public RateParameterId(RateParameter rateParameter, String oldId, String newId) {
        this.rateParameter = rateParameter;
        this.oldId = oldId;
        this.newId = newId;
    }

    @Override
    public void undo() {
        rateParameter.setId(oldId);
        rateParameter.setName(oldId);
    }

    @Override
    public void redo() {
        rateParameter.setName(newId);
        rateParameter.setId(newId);

    }
}
