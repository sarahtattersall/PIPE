package pipe.actions.gui.create;

import pipe.controllers.PlaceController;
import pipe.models.component.token.Token;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class AddTokenAction extends TokenAction {

    public AddTokenAction() {
        super("Add token", "Add a token (alt-t)", KeyEvent.VK_T, InputEvent.ALT_DOWN_MASK);
    }

    @Override
    protected void performTokenAction(PlaceController placeController, Token token) {
        placeController.addTokenToPlace(token);
    }

}
