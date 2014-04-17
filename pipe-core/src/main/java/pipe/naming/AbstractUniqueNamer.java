package pipe.naming;

import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashSet;

/**
 * Used for naming components, listens to component
 *
 * This abstract class provides the implementation for getting unique names
 * It is up to subclasses to provide the implementation
 */
public abstract class AbstractUniqueNamer implements UniqueNamer {

    private final String namePrefix;

    protected final Collection<String> names = new HashSet<>();

    protected final PropertyChangeListener nameListener =  new NameChangeListener(names);

    /**
     * @param namePrefix Value to prefix component names with, e.g. "P" for place
     */
    protected AbstractUniqueNamer(String namePrefix) {

        this.namePrefix = namePrefix;
    }



    @Override
    public String getName() {
        int nameNumber = 0;
        String name = namePrefix + nameNumber;
        while (names.contains(name)) {
            nameNumber++;
            name = namePrefix + nameNumber;
        }
        return name;
    }

    @Override
    public boolean isUniqueName(String name) {
        return !names.contains(name);
    }
}
