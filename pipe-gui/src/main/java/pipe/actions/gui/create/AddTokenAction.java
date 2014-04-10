package pipe.actions.gui.create;

import pipe.controllers.PetriNetController;
import pipe.controllers.PlaceController;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class AddTokenAction extends TokenAction {

    public AddTokenAction() {
        super("Add token", "Add a token (alt-t)", KeyEvent.VK_T, InputEvent.ALT_DOWN_MASK);
    }

    @Override
    protected void performTokenAction(Place place, Token token) {
        Map<Token, Integer> tokenCount = new HashMap<>();
        tokenCount.put(token, place.getTokenCount(token) + 1);
        setTokenCounts(place, tokenCount);
    }

}
