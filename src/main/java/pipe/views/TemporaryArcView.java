package pipe.views;

import pipe.models.component.Connectable;

import javax.swing.*;

public class TemporaryArcView<T extends Connectable<?,T>> extends JComponent {
    private T source;

    public TemporaryArcView(T source) {

    }
}
