package matchers.component;

import org.mockito.ArgumentMatcher;
import pipe.views.ConnectableView;
import uk.ac.imperial.pipe.models.petrinet.Connectable;

public class HasModel<T1 extends Connectable, T2 extends ConnectableView<T1>> extends ArgumentMatcher<T2> {
    T1 model;
    public HasModel(T1 model) {
        this.model = model;
    }

    @Override
    public boolean matches(Object argument) {
        T2 view = (T2) argument;
        return view.getModel().equals(model);
    }
}
