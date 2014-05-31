package pipe.views.builder;

import pipe.controllers.TokenController;
import pipe.views.TokenView;
import uk.ac.imperial.pipe.models.petrinet.Token;

public class TokenViewBuilder {

    private final Token model;
    private TokenController controller;

    public TokenViewBuilder(Token model) {
        this.model = model;
    }

    public TokenViewBuilder withTokenController(TokenController controller) {
        this.controller = controller;
        return this;
    }

    public TokenView build() {
        return new TokenView(controller, model);
    }

}
