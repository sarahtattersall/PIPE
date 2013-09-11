import pipe.controllers.PipeApplicationController;
import pipe.models.PipeApplicationModel;

import javax.swing.*;
public class Pipe
{

    private Pipe(String version)
    {
        PipeApplicationModel applicationModel = new PipeApplicationModel(version);
        PipeApplicationController applicationController = new PipeApplicationController(applicationModel);
    }

    public static void main(String args[])
    {
        Runnable runnable = new Runnable()
                            {
                                public void run()
                                {
                                    Pipe pipe = new Pipe("v4.3.0");
                                }
                            };
        SwingUtilities.invokeLater(runnable);
    }
}
