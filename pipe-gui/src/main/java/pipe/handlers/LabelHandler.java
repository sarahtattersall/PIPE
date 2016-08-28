package pipe.handlers;

import pipe.controllers.AbstractConnectableController;
import pipe.views.ConnectableView;
import pipe.views.TextLabel;
import uk.ac.imperial.pipe.models.petrinet.Connectable;

import javax.swing.*;
import java.awt.Point;
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

    private final AbstractConnectableController<T> controller;

    /**
     * Name label for the corresponding connectable
     */
    private final TextLabel textLabel;


    /**
     * Constructor
     * @param textLabel name label for the connectable
     * @param connectable connectable with a name label
     * @param controller for the connectable 
     */
    public LabelHandler(TextLabel textLabel, ConnectableView<T> connectable,
                        AbstractConnectableController<T> controller) {
        this.connectable = connectable;
        this.textLabel = textLabel;
        this.controller = controller;
    }


    /**
     * When the mouse wheel is moved the event is dispatched to its connectable
     * @param e mouse event 
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        connectable.dispatchEvent(e);
    }

    /**
     * Drags the components name label
     * @param e mouse event 
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        Point point = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), connectable.getParent());
        controller.moveNameLabel(point);
    }

}
