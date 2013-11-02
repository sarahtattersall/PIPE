import java.lang.reflect.InvocationTargetException;

import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.CopyPasteManager;
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

        PetriNetController netController = new PetriNetController();
        CopyPasteManager copyPaste = new CopyPasteManager();
        applicationController = new PipeApplicationController(applicationModel, netController, copyPaste);
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
