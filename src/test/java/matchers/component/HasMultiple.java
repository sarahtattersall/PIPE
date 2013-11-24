package matchers.component;

import org.mockito.ArgumentMatcher;
import pipe.models.component.Connectable;

import java.util.LinkedList;
import java.util.List;

/**
 * Class that can have multiple @link{Has} items for a Connectable type
 */
public class HasMultiple<T extends Connectable> extends ArgumentMatcher<T> {
    List<Has> has_items = new LinkedList<Has>();

    public HasMultiple(Has... items)
    {
        for (Has has : items) {
            has_items.add(has);
        }
    }

    @Override
    public boolean matches(Object item) {
        T connectable = (T) item;
        for (Has has : has_items)
        {
            if (!has.matches(connectable))
            {
                return false;
            }
        }
        return true;
    }
}