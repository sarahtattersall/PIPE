package pipe.actions.gui.create;

import pipe.controllers.PlaceController;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class DeleteTokenAction extends TokenAction {
    public DeleteTokenAction() {
        super("Delete token", "Delete a token (alt-D)", KeyEvent.VK_D, InputEvent.ALT_DOWN_MASK);
    }

    @Override
    protected void performTokenAction(PlaceController placeController, Token token) {

        Map<Token, Integer> tokenCount = new HashMap<>();
        tokenCount.put(token, placeController.getTokenCount(token) - 1);
        setTokenCounts(placeController, tokenCount);
    }


}
