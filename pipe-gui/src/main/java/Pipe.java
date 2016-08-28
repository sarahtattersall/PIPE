import pipe.actions.gui.PipeApplicationModel;
import pipe.controllers.application.PipeApplicationController;
import pipe.views.PipeApplicationBuilder;
import pipe.views.PipeApplicationView;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

public final class Pipe {

    protected static PipeApplicationView applicationView;

    private Pipe(String version) {
        PipeApplicationModel applicationModel = new PipeApplicationModel(version);
        PipeApplicationController applicationController = new PipeApplicationController(applicationModel);
        PipeApplicationBuilder builder = new PipeApplicationBuilder();
        applicationView = builder.build(applicationController, applicationModel);
        applicationController.createEmptyPetriNet();

    }

    public static void main(String[] args) {
        Runnable runnable = pipeRunnable();
        SwingUtilities.invokeLater(runnable);
    }

    protected static Runnable pipeRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                new Pipe("v5.1.0"); 
            }
        };
    }

    protected static void runPipeForTesting() throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeAndWait(pipeRunnable());
    }
}
