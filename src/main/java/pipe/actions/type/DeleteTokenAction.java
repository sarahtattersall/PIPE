package pipe.actions.type;

import pipe.controllers.PlaceController;
import pipe.models.component.Token;

public class DeleteTokenAction extends TokenAction {
    public DeleteTokenAction(final String name, final int typeID,
                     final String tooltip, final String keystroke) {
        super(name, typeID, tooltip, keystroke);
    }

    @Override
    protected void performTokenAction(PlaceController placeController, Token token) {
        placeController.deleteTokenInPlace(token);
    }


}
