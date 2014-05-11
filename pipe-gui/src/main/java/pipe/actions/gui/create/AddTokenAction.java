package pipe.actions.gui.create;

import pipe.controllers.PlaceController;
import pipe.gui.model.PipeApplicationModel;
import pipe.models.component.token.Token;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Adds a token to a place
 */
public class AddTokenAction extends TokenAction {

    public AddTokenAction(PipeApplicationModel applicationModel) {
        super("Add token", "Add a token (alt-t)", KeyEvent.VK_T, InputEvent.ALT_DOWN_MASK, applicationModel);
    }

    @Override
    protected void performTokenAction(PlaceController placeController, String token) {
        Map<String, Integer> tokenCount = new HashMap<>();
        tokenCount.put(token, placeController.getTokenCount(token) + 1);
        setTokenCounts(placeController, tokenCount);
    }

}
