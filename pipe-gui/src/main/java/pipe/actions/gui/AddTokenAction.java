package pipe.actions.gui;

import pipe.controllers.PlaceController;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Action to add a token to a place. The token chosen to add is the ony ehtat is specified in the token
 * drop down on the tool bar,
 */
public class AddTokenAction extends TokenAction {

    /**
     * Constructor
     * @param applicationModel model of the entire application
     */
    public AddTokenAction(PipeApplicationModel applicationModel) {
        super("Add token", "Add a token (alt-t)", KeyEvent.VK_T, InputEvent.ALT_DOWN_MASK, applicationModel);
    }

    /**
     *
     * Adds a single token with the specified id to the place that has been selected
     *
     * @param placeController controller for the place the token is being added to
     * @param token token id of the token to add to the place
     */
    @Override
    protected final void performTokenAction(PlaceController placeController, String token) {
        Map<String, Integer> tokenCount = new HashMap<>();
        tokenCount.put(token, placeController.getTokenCount(token) + 1);
        setTokenCounts(placeController, tokenCount);
    }

}
