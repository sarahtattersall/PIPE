package matchers.component;

import org.mockito.ArgumentMatcher;

import java.beans.PropertyChangeEvent;

public class PropertyChangeWithValues extends ArgumentMatcher<PropertyChangeEvent> {
    private final String message;

    private final Object oldValue;

    private final Object newValue;

    public PropertyChangeWithValues(String message, Object oldValue, Object newValue) {
        this.message = message;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    @Override
    public boolean matches(Object argument) {
        PropertyChangeEvent event = (PropertyChangeEvent) argument;
        return (event.getNewValue().equals(newValue) && event.getPropertyName().equals(message)
                && event.getOldValue().equals(oldValue));

    }
}
