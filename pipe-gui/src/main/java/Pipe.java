import pipe.controllers.application.PipeApplicationController;
import pipe.actions.gui.PipeApplicationModel;
import pipe.views.PipeApplicationBuilder;
import pipe.views.PipeApplicationView;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
public class Pipe
{

    protected static PipeApplicationView applicationView;

	private Pipe(String version)
    {
        PipeApplicationModel applicationModel = new PipeApplicationModel(version);
        PipeApplicationController applicationController = new PipeApplicationController(applicationModel);
        PipeApplicationBuilder builder = new PipeApplicationBuilder();
        applicationView = builder.build(applicationController, applicationModel);
        applicationController.createEmptyPetriNet();

    }
    public static void main(String args[])
    {
        Runnable runnable = pipeRunnable();
        SwingUtilities.invokeLater(runnable);
    }
	protected static Runnable pipeRunnable()
	{
		Runnable runnable = new Runnable()
                            {
                                public void run()
                                {
                                    @SuppressWarnings("unused")
									Pipe pipe = new Pipe("v5.0.0");
                                }
                            };
		return runnable;
	}
    protected static void runPipeForTesting() throws InterruptedException, InvocationTargetException
    {
    	SwingUtilities.invokeAndWait(pipeRunnable());
    }
}
