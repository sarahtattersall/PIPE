package pipe.gui;

import pipe.actions.gui.GuiAction;
import pipe.controllers.PetriNetController;
import pipe.controllers.application.PipeApplicationController;
import pipe.gui.widgets.EscapableDialog;
import pipe.historyActions.LayoutPetriNetEvent;
import pipe.views.PipeApplicationView;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;
import uk.ac.imperial.pipe.models.petrinet.Place;
import uk.ac.imperial.pipe.models.petrinet.Transition;

import javax.swing.*;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class LayoutAction extends GuiAction {


    private final PipeApplicationController pipeApplicationController;

    private final PipeApplicationView applicationView;

    public LayoutAction(PipeApplicationController pipeApplicationController, PipeApplicationView applicationView) {
        super("Layout", "Layout", KeyEvent.VK_P, InputEvent.ALT_DOWN_MASK);
        this.pipeApplicationController = pipeApplicationController;
        this.applicationView = applicationView;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PetriNetController petriNetController = pipeApplicationController.getActivePetriNetController();
        PetriNet petriNet = petriNetController.getPetriNet();
        showLayoutEditor(petriNet);
    }

    public void showLayoutEditor(PetriNet petriNet) {
        EscapableDialog guiDialog = new EscapableDialog(applicationView, "PIPE 5", true);
        Container contentPane = guiDialog.getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));

        final Map<String, Point> previousLocations = getLocations(petriNet);

        LayoutForm formLayout = new LayoutForm(petriNet, new LayoutForm.ChangeAction() {
            @Override
            public void changed(PetriNet petriNet) {
                registerUndoEvent(new LayoutPetriNetEvent(petriNet, previousLocations, getLocations(petriNet)));
            }
        });
        contentPane.add(formLayout.getMainPanel());
        guiDialog.setResizable(false);
        guiDialog.pack();
        guiDialog.setLocationRelativeTo(null);
        guiDialog.setVisible(true);
    }

    private Map<String, Point> getLocations(PetriNet petriNet) {
        Map<String, Point> pointMap = new HashMap<>();
        for (Place place : petriNet.getPlaces()) {
            pointMap.put(place.getId(), new Point(place.getX(), place.getY()));
        }

        for (Transition transition : petriNet.getTransitions()) {
            pointMap.put(transition.getId(), new Point(transition.getX(), transition.getY()));
        }
        return pointMap;
    }
}
