package pipe.actions.type;

import pipe.actions.TypeAction;
import pipe.controllers.PetriNetController;
import pipe.historyActions.AddPetriNetObject;
import pipe.historyActions.HistoryManager;
import pipe.models.component.Connectable;
import pipe.models.component.annotation.Annotation;
import pipe.models.petrinet.PetriNet;

import java.awt.*;
import java.awt.event.MouseEvent;

public class AnnotationAction extends TypeAction {

    @Override
    public void doAction(MouseEvent event, PetriNetController petriNetController) {
        if (event.getClickCount() > 0) {
            Point point = event.getPoint();
            Annotation annotation = getAnnotation(point, petriNetController);
            PetriNet net = petriNetController.getPetriNet();
            petriNetController.getHistoryManager().addNewEdit(new AddPetriNetObject(annotation, net));
        }
    }

    private Annotation getAnnotation(Point point, PetriNetController petriNetController) {

        int x = new Double(point.getX()).intValue();
        int y = new Double(point.getY()).intValue();
        Annotation annotation = new Annotation(x, y, "blah blah blah", 100, 50, true);

        PetriNet petriNet = petriNetController.getPetriNet();
        petriNet.addAnnotaiton(annotation);
        return annotation;
    }

    @Override
    public void doConnectableAction(Connectable connectable, PetriNetController petriNetController) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public AnnotationAction(String name, int typeID,
                            String tooltip, String keystroke) {
        super(name, typeID, tooltip, keystroke);
    }


}
