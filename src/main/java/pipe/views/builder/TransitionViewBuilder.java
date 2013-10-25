package pipe.views.builder;

import pipe.models.Transition;
import pipe.views.TransitionView;

public class TransitionViewBuilder {
    private final Transition transition;

    public TransitionViewBuilder(Transition transition) {
        this.transition = transition;
    }

    public TransitionView build() {
        TransitionView view =
                new TransitionView(transition.getX(), transition.getY(), transition.getId(), transition.getName(),
                        transition.getNameXOffset(), transition.getNameYOffset(), transition.isTimed(),
                        transition.isInfiniteServer(), transition.getAngle(), transition);
        return view;
    }
}
