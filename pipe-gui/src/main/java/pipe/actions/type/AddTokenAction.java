package pipe.actions.type;

import pipe.controllers.PlaceController;
import pipe.models.component.token.Token;

public class AddTokenAction extends TokenAction {

    public AddTokenAction(final String name, final int typeID,
                          final String tooltip, final String keystroke) {
        super(name, typeID, tooltip, keystroke);
    }

    @Override
    protected void performTokenAction(PlaceController placeController, Token token) {
        placeController.addTokenToPlace(token);
    }

}
