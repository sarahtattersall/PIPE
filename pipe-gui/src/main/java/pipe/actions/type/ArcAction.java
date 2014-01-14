package pipe.actions.type;

import pipe.actions.TypeAction;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.controllers.arcCreator.ArcActionCreator;
import pipe.gui.PetriNetTab;
import pipe.models.component.Connectable;
import pipe.visitor.connectable.arc.ArcSourceVisitor;
import pipe.views.PipeApplicationView;
import pipe.views.TemporaryArcView;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;

public class ArcAction extends TypeAction {

    private final ArcSourceVisitor sourceVisitor;
    private final ArcActionCreator arcCreator;
    private final PipeApplicationController controller;
    private final PipeApplicationView applicationView;
    private TemporaryArcView<? extends Connectable> temporaryArcView = null;

    public ArcAction(final String name, final int typeID,
                     final String tooltip, final String keystroke, ArcSourceVisitor sourceVisitor,
                     ArcActionCreator arcCreator, PipeApplicationController controller,
                     PipeApplicationView applicationView) {
        super(name, typeID, tooltip, keystroke);
        this.sourceVisitor = sourceVisitor;
        this.arcCreator = arcCreator;
        this.controller = controller;
        this.applicationView = applicationView;
    }

    /**
     * Changes the temporary arc's end point
     *
     * @param event              mouse event that has just been fired
     * @param petriNetController current petri net controller for the tab showing
     */
    @Override
    public void doAction(MouseEvent event, PetriNetController petriNetController) {
        if (temporaryArcView != null) {
            temporaryArcView.setEnd(event.getPoint());
            PetriNetTab tab = applicationView.getCurrentTab();
            tab.validate();
            tab.repaint();
        }
    }

    /**
     * @param connectable        the item clicked on
     * @param petriNetController the controller for the current petrinet
     */
    @Override
    public <T extends Connectable> void doConnectableAction(T connectable,
                                                            PetriNetController petriNetController) {
        if (temporaryArcView == null && sourceVisitor.canStart(connectable)) {
            PetriNetTab tab = applicationView.getCurrentTab();
            createTemporaryArc(connectable, tab);

        } else if (temporaryArcView != null && canCreateArcHere(connectable)) {
            createArc(connectable);
        }
    }

    /**
     * @param connectable Source of the temporary arc
     * @param tab         Tab to add TemporaryArc to
     * @param <T>         Source class
     * @return created Tempoary arc
     */
    private <T extends Connectable> void createTemporaryArc(T connectable, final PetriNetTab tab) {

        temporaryArcView = new TemporaryArcView<T>(connectable);
        temporaryArcView.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {
            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {

                switch (keyEvent.getKeyCode()) {
                    case KeyEvent.VK_META:
                    case KeyEvent.VK_WINDOWS:
                    case KeyEvent.VK_SPACE:
                        tab.setMetaDown(true);
                        break;

                    case KeyEvent.VK_ESCAPE:
                    case KeyEvent.VK_DELETE:
                        tab.remove(temporaryArcView);
                        tab.repaint();
                        temporaryArcView = null;
                    default:
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
            }
        });

        tab.add(temporaryArcView);
        temporaryArcView.requestFocusInWindow();
    }

    private <T extends Connectable> void createArc(T connectable) {

        arcCreator.create(temporaryArcView.getSourceConnectable(), connectable);
        PetriNetTab tab = applicationView.getCurrentTab();
        tab.remove(temporaryArcView);
        tab.repaint();
        temporaryArcView = null;
    }

    private <T extends Connectable> boolean canCreateArcHere(T connectable) {
        return arcCreator.canCreate(temporaryArcView.getSourceConnectable(), connectable);
    }


}

