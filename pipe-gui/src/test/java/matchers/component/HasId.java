package matchers.component;

import pipe.models.component.Connectable;

public class HasId<T extends Connectable> implements Has<T> {
    String id;
    public HasId(String id) {
        this.id = id;
    }

    @Override
    public boolean matches(T component) {
        return component.getId().equals(id);
    }
}
