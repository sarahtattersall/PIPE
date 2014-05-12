package pipe.actions.gui.edit;

import pipe.controllers.ArcController;
import uk.ac.imperial.pipe.models.component.arc.ArcPoint;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class DeleteArcPathPointAction extends AbstractAction {
    private final ArcPoint component;

    private final ArcController<?, ?> arcController;

    public DeleteArcPathPointAction(ArcPoint component, ArcController<?,?> arcController) {
        this.component = component;
        this.arcController = arcController;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        arcController.deletePoint(component);
    }
}
