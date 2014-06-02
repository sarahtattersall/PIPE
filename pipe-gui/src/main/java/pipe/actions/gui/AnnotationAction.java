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

public class AnnotationAction extends CreateAction {

    public AnnotationAction(PipeApplicationModel applicationModel) {
        super("Annotation", "Add an annotation (alt-N)", KeyEvent.VK_N, InputEvent.ALT_DOWN_MASK, applicationModel);
    }

    @Override
    public void doAction(MouseEvent event, PetriNetController petriNetController) {
        if (event.getClickCount() > 0) {
            Point point = event.getPoint();
            Annotation annotation = getAnnotation(point, petriNetController);
            PetriNet net = petriNetController.getPetriNet();

            registerUndoEvent(new AddPetriNetObject(annotation, net));
        }
    }

    @Override
    public void doConnectableAction(Connectable connectable, PetriNetController petriNetController) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private Annotation getAnnotation(Point point, PetriNetController petriNetController) {

        int x = new Double(point.getX()).intValue();
        int y = new Double(point.getY()).intValue();
        Annotation annotation = new AnnotationImpl(x, y, "Enter text here", 100, 50, true);

        PetriNet petriNet = petriNetController.getPetriNet();
        petriNet.addAnnotation(annotation);
        return annotation;
    }


}
