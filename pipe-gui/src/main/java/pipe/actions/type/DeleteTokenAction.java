package pipe.actions.type;

import pipe.controllers.PlaceController;
import pipe.models.component.token.Token;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class DeleteTokenAction extends TokenAction {
    public DeleteTokenAction() {
       super("Delete token", "Delete a token (alt-D)", KeyEvent.VK_D, InputEvent.ALT_DOWN_MASK);
    }

    @Override
    protected void performTokenAction(PlaceController placeController, Token token) {
        placeController.deleteTokenInPlace(token);
    }


}
