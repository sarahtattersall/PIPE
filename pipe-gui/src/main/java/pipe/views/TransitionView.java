package pipe.views;

import org.jfree.util.ShapeUtilities;
import pipe.controllers.PetriNetController;
import pipe.gui.Constants;
import pipe.gui.PetriNetTab;
import pipe.gui.widgets.EscapableDialog;
import pipe.gui.widgets.TransitionEditorPanel;
import pipe.handlers.TransitionAnimationHandler;
import pipe.handlers.TransitionHandler;
import pipe.historyActions.HistoryItem;
import uk.ac.imperial.pipe.models.petrinet.Transition;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


public class TransitionView extends ConnectableView<Transition> {
    public boolean _highlighted;

    private boolean _enabled;

    public TransitionView(Transition model, PetriNetController controller) {
        super(model.getId(), model, controller, new Rectangle2D.Double(0, 0, model.getWidth(),
                model.getHeight()));
//        constructTransition();
        setChangeListener();

        _enabled = false;
        _highlighted = false;

        rotate(model.getAngle());
        //TODO: DEBUG WHY CANT CALL THIS IN CONSTRUCTOR
        //        changeToolTipText();

    }

    public void rotate(int angleInc) {
        ShapeUtilities.rotateShape(shape, Math.toRadians(angleInc), new Double(model.getCentre().getX()).floatValue(), new Double(model.getCentre().getY()).floatValue());
//        shape.transform(AffineTransform.getRotateInstance(Math.toRadians(angleInc), model.getHeight() / 2,
//                model.getHeight() / 2));
    }

    private void setChangeListener() {
        model.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                String name = propertyChangeEvent.getPropertyName();
                if (name.equals(Transition.PRIORITY_CHANGE_MESSAGE) || name.equals(Transition.RATE_CHANGE_MESSAGE)) {
                    repaint();
                } else if (name.equals(Transition.ANGLE_CHANGE_MESSAGE) || name.equals(Transition.TIMED_CHANGE_MESSAGE)
                        || name.equals(Transition.INFINITE_SEVER_CHANGE_MESSAGE)) {
                    repaint();
                } else if (name.equals(Transition.ENABLED_CHANGE_MESSAGE) || name.equals(
                        Transition.DISABLED_CHANGE_MESSAGE)) {
                    repaint();
                }
            }
        });
    }

    @Override
    public boolean isEnabled() {
        return _enabled;
    }

    @Override
    public void setEnabled(boolean status) {
        if (_enabled && !status) {
//            _delayValid = false;
        }

        _enabled = status;

    }

    public void update() {
        this.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (isSelected() && !_ignoreSelection) {
            g2.setColor(Constants.SELECTION_FILL_COLOUR);
        } else {
            g2.setColor(Constants.ELEMENT_FILL_COLOUR);
        }

        if (model.isTimed()) {
            if (model.isInfiniteServer()) {
                for (int i = 2; i >= 1; i--) {
                    g2.translate(2 * i, -2 * i);
                    g2.fill(shape);
                    Paint pen = g2.getPaint();
                    if (highlightView()) {
                        g2.setPaint(Constants.ENABLED_TRANSITION_COLOUR);
                    } else if (isSelected() && !_ignoreSelection) {
                        g2.setPaint(Constants.SELECTION_LINE_COLOUR);
                    } else {
                        g2.setPaint(Constants.ELEMENT_LINE_COLOUR);
                    }
                    g2.draw(shape);
                    g2.setPaint(pen);
                    g2.translate(-2 * i, 2 * i);
                }
            }
            g2.fill(shape);
        }

        if (highlightView()) {
            g2.setPaint(Constants.ENABLED_TRANSITION_COLOUR);
        } else if (isSelected() && !_ignoreSelection) {
            g2.setPaint(Constants.SELECTION_LINE_COLOUR);
        } else {
            g2.setPaint(Constants.ELEMENT_LINE_COLOUR);
        }

        g2.draw(shape);
        if (!model.isTimed()) {
            if (model.isInfiniteServer()) {
                for (int i = 2; i >= 1; i--) {
                    g2.translate(2 * i, -2 * i);
                    Paint pen = g2.getPaint();
                    g2.setPaint(Constants.ELEMENT_FILL_COLOUR);
                    g2.fill(shape);
                    g2.setPaint(pen);
                    g2.draw(shape);
                    g2.translate(-2 * i, 2 * i);
                }
            }
            g2.draw(shape);
            g2.fill(shape);
        }
//        changeToolTipText();
    }

    @Override
    void setCentre(double x, double y) {
        super.setCentre(x, y);
        update();
    }

    @Override
    public void showEditor() {
        EscapableDialog guiDialog = new EscapableDialog(petriNetController.getPetriNetTab().getApplicationView(), "PIPE", true);
        TransitionEditorPanel te = new TransitionEditorPanel(guiDialog.getRootPane(),
                petriNetController.getTransitionController(this.model), petriNetController);
        guiDialog.add(te);
        guiDialog.getRootPane().setDefaultButton(null);
        guiDialog.setResizable(false);
        guiDialog.pack();
        guiDialog.setLocationRelativeTo(null);
        guiDialog.setVisible(true);
        guiDialog.dispose();
    }

    @Override
    public void toggleAttributesVisible() {
        _attributesVisible = !_attributesVisible;
    }

    private void changeToolTipText() {
        if (this.isTimed()) {
            setToolTipText("r = " + model.getRate());
        } else {
            setToolTipText("\u03c0 = " + this.getPriority() + "; w = " + model.getRate());
        }
    }

    private void showErrorMessage() {
        String message =
                "Errors in marking-dependent transition rate expression." + "\r\n The computation should be aborted";
        String title = "Error";
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.YES_NO_OPTION);
    }

    public boolean isInfiniteServer() {
        return model.isInfiniteServer();
    }

    public boolean isTimed() {
        return model.isTimed();
    }

    public int getPriority() {
        return model.
                getPriority();
    }

    /**
     * @return true if in animate mode and the model is enabled
     */
    private boolean highlightView() {
        return model.isEnabled() && petriNetController.isInAnimationMode();
    }

    @Override
    public void addToPetriNetTab(PetriNetTab tab) {
        addLabelToContainer(tab);

        TransitionHandler transitionHandler = new TransitionHandler(this, tab, this.model, petriNetController);
        addMouseListener(transitionHandler);
        addMouseMotionListener(transitionHandler);
        addMouseWheelListener(transitionHandler);

        MouseListener transitionAnimationHandler = new TransitionAnimationHandler(this.model, petriNetController);
        addMouseListener(transitionAnimationHandler);
    }

    public int getAngle() {
        return model.getAngle();
    }

    @Override
    public boolean contains(int x, int y) {
        return shape.contains(x,y);
    }

    public void setModel(Transition model) {
        this.model = model;
    }

    private int showWithTimeout(JOptionPane pane, Component parent, String title) {
        final JDialog dialog = pane.createDialog(parent, title);
        Thread timeoutThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(2000);
                } catch (InterruptedException ex) {
                }
                javax.swing.SwingUtilities.invokeLater(new Runnable() //   from the event dispatch
                {
                    @Override
                    public void run() {
                        dialog.setVisible(false);
                    }
                });            //   thread
            }
        };
        timeoutThread.start();
        dialog.setVisible(true);
        Object selection = pane.getValue();                      // We get to this point when
        int result = JOptionPane.CLOSED_OPTION;                   // (1) The user makes a selection
        if (selection != null && selection instanceof Integer)        // or (2) the timeout thread closes
        {
            result = ((Integer) selection).intValue();             // the dialog.
        }
        return result;
    }

    //TODO: DELETE
    public HistoryItem groupTransitions() {
        return null;
    }

}
