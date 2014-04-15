package utils;

import org.mockito.ArgumentMatcher;

import java.beans.PropertyChangeEvent;

public class PropertyChangeUtils {

    public static PropertyChangeNamer hasName(String name) {
        return new PropertyChangeNamer(name);
    }

    public static class PropertyChangeNamer extends ArgumentMatcher<PropertyChangeEvent> {

        private String name;

        private PropertyChangeNamer(String name) {
            this.name = name;
        }

        @Override
        public boolean matches(Object argument) {
            PropertyChangeEvent event = (PropertyChangeEvent) argument;
            return event.getPropertyName().equals(name);
        }
    }
}
