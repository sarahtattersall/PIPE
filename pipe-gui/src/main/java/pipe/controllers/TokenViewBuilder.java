package pipe.controllers;

import pipe.views.TokenView;
import uk.ac.imperial.pipe.models.petrinet.Token;

public class TokenViewBuilder {

    private final Token model;

    public TokenViewBuilder(Token model) {
        this.model = model;
    }

    public TokenView build() {
        return new TokenView(model);
    }

}
