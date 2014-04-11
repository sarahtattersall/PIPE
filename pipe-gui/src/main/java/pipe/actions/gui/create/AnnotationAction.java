package pipe.actions.gui.create;

import pipe.controllers.PetriNetController;
import pipe.historyActions.component.AddPetriNetObject;
import pipe.models.component.Connectable;
import pipe.models.component.annotation.Annotation;
import pipe.models.petrinet.PetriNet;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class AnnotationAction extends CreateAction {

    @Override
    public void doAction(MouseEvent event, PetriNetController petriNetController) {
        if (event.getClickCount() > 0) {
            Point point = event.getPoint();
            Annotation annotation = getAnnotation(point, petriNetController);
            PetriNet net = petriNetController.getPetriNet();

            registerUndoEvent(new AddPetriNetObject(annotation, net));
        }
    }

    private Annotation getAnnotation(Point point, PetriNetController petriNetController) {

        int x = new Double(point.getX()).intValue();
        int y = new Double(point.getY()).intValue();
        Annotation annotation = new Annotation(x, y, "Enter text here", 100, 50, true);

        PetriNet petriNet = petriNetController.getPetriNet();
        petriNet.addAnnotation(annotation);
        return annotation;
    }

    @Override
    public void doConnectableAction(Connectable connectable, PetriNetController petriNetController) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public AnnotationAction() {
        super("Annotation", "Add an annotation (alt-N)", KeyEvent.VK_N, InputEvent.ALT_DOWN_MASK);
    }


}
