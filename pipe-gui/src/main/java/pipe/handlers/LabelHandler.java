package pipe.handlers;

import pipe.views.ConnectableView;
import pipe.views.TextLabel;
import uk.ac.imperial.pipe.models.petrinet.Connectable;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;


/**
 * Handler for connectable views name label
 */
public class LabelHandler<T extends Connectable> extends javax.swing.event.MouseInputAdapter {


    /**
     * Connectable the name label refers to
     */
    private final ConnectableView<T> connectable;

    /**
     * Name label for the corresponding connectable
     */
    private final TextLabel textLabel;


    /**
     * Constructor
     * @param textLabel name label for the connectable
     * @param connectable connectable with a name label
     */
    public LabelHandler(TextLabel textLabel, ConnectableView<T> connectable) {
        this.connectable = connectable;
        this.textLabel = textLabel;
    }


    /**
     * When clicking on the name label the event is dispatched to its connectable view
     * @param e
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        connectable.dispatchEvent(e);
    }


    /**
     * Noop
     * @param e
     */
    @Override
    public void mousePressed(MouseEvent e) {
        //Noop
    }

    /**
     * When the mouse wheel is moved the event is dispatched to its connectable
     * @param e
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        connectable.dispatchEvent(e);
    }

    /**
     * Noop
     * @param e
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        //Noop
    }

}
