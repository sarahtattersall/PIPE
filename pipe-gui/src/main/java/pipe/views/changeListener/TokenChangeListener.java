package pipe.views.changeListener;

import pipe.views.PipeApplicationView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class TokenChangeListener implements PropertyChangeListener {
    private final PipeApplicationView applicationView;

    public TokenChangeListener(PipeApplicationView applicationView) {
        this.applicationView = applicationView;
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        applicationView.refreshTokenClassChoices();
    }
}
