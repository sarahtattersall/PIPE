import java.lang.reflect.InvocationTargetException;

import pipe.controllers.PipeApplicationController;
import pipe.models.PipeApplicationModel;
import pipe.views.PipeApplicationView;

import javax.swing.*;
public class Pipe
{

	private PipeApplicationModel applicationModel;
	@SuppressWarnings("unused")
	private PipeApplicationController applicationController;

    private PipeApplicationView applicationView;

	private Pipe(String version)
    {
        applicationModel = new PipeApplicationModel(version);
        applicationController = new PipeApplicationController(applicationModel);
        applicationView = new PipeApplicationView(applicationController, applicationModel);
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
									Pipe pipe = new Pipe("v4.3.0");
                                }
                            };
		return runnable;
	}
    protected static void runPipeForTesting() throws InterruptedException, InvocationTargetException
    {
    	SwingUtilities.invokeAndWait(pipeRunnable()); 
    }
}
