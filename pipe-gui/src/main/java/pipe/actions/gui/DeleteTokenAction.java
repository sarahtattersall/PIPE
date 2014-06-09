package pipe.actions.gui;

import pipe.controllers.PlaceController;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Deletes the token from the place
 */
public class DeleteTokenAction extends TokenAction {
    /**
     * Constructor
     * @param applicationModel main PIPE application model
     */
    public DeleteTokenAction(PipeApplicationModel applicationModel) {
        super("Delete token", "Delete a token (alt-D)", KeyEvent.VK_D, InputEvent.ALT_DOWN_MASK, applicationModel);
    }

    /**
     * On performing this action a token of the specified id is decremented from the place that has been clicked on
     *
     * @param placeController controller for the clicked on place
     * @param token token id for the token that is to be decremented
     */
    @Override
    protected void performTokenAction(PlaceController placeController, String token) {

        Map<String, Integer> tokenCount = new HashMap<>();
        tokenCount.put(token, placeController.getTokenCount(token) - 1);
        setTokenCounts(placeController, tokenCount);
    }


}
