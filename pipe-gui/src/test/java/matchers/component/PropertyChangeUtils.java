package matchers.component;

import java.beans.PropertyChangeEvent;

import org.mockito.ArgumentMatcher;

public class PropertyChangeUtils {
    private PropertyChangeUtils() {

    }

    public static PropertyChangeValues hasValues(String message, Object oldValue, Object newValue) {
        return new PropertyChangeValues(message, oldValue, newValue);
    }

    public static class PropertyChangeValues extends ArgumentMatcher<PropertyChangeEvent> {
        private final String message;

        private final Object oldValue;

        private final Object newValue;

        public PropertyChangeValues(String message, Object oldValue, Object newValue) {
            this.message = message;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        @Override
        public boolean matches(Object argument) {
            PropertyChangeEvent event = (PropertyChangeEvent) argument;
            if ((newValue == null && event.getNewValue() != null) || (oldValue == null && event.getOldValue() != null)) {
                return false;
            }

            if ((newValue == null && event.getNewValue() == null) || (oldValue == null && event.getOldValue() == null)) {
                return event.getPropertyName().equals(message);
            }
            return (event.getNewValue().equals(newValue) && event.getPropertyName().equals(message)
                    && event.getOldValue().equals(oldValue));

        }
    }
}
