package pipe.actions.gui;

import pipe.controllers.PetriNetController;
import pipe.historyActions.component.AddPetriNetObject;
import uk.ac.imperial.pipe.models.petrinet.Annotation;
import uk.ac.imperial.pipe.models.petrinet.AnnotationImpl;
import uk.ac.imperial.pipe.models.petrinet.Connectable;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;

import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * Action responsible for adding an annotation to the Petri net.
 * Clicking on this action and then pressing on the canvas will trigger
 * an annotation to be created
 */
public final class AnnotationAction extends CreateAction {

    /**
     * Default height of the annotation
     */
    public static final int WIDTH = 100;

    /**
     * Default width of the annotation
     */
    public static final int HEIGHT = 50;

    /**
     * Action constructor
     * @param applicationModel model of the entire application model
     */
    public AnnotationAction(PipeApplicationModel applicationModel) {
        super("Annotation", "Add an annotation (alt-N)", KeyEvent.VK_N, InputEvent.ALT_DOWN_MASK, applicationModel);
    }

    /**
     * Called when this action is selected and the user has pressed on the canvas
     * Adds a new annotation and registers an undo event for its addition.
     * @param event              mouse event
     * @param petriNetController controller for the petri net
     */
    @Override
    public void doAction(MouseEvent event, PetriNetController petriNetController) {
        if (event.getClickCount() > 0) {
            Point point = event.getPoint();
            Annotation annotation = getAnnotation(point, petriNetController);
            PetriNet net = petriNetController.getPetriNet();
            registerUndoEvent(new AddPetriNetObject(annotation, net));
        }
    }

    /**
     * Noop action when the user has selected this action but presses on an existing
     * Petri net component on the canvas.
     * @param connectable        item clicked
     * @param petriNetController controller for the petri net
     */
    @Override
    public void doConnectableAction(Connectable connectable, PetriNetController petriNetController) {
       //Noop
    }

    /**
     * Creates a new annotation.
     *
     * @param point top left x,y location of the annotation
     * @param petriNetController controller for the Petri net this annotation is being added to.
     * @return newly created action at the specified point
     */
    private Annotation getAnnotation(Point point, PetriNetController petriNetController) {

        int x = (int) point.getX();
        int y = (int) point.getY();
        Annotation annotation = new AnnotationImpl(x, y, "Enter text here", WIDTH, HEIGHT, true);

        PetriNet petriNet = petriNetController.getPetriNet();
        petriNet.addAnnotation(annotation);
        return annotation;
    }


}
