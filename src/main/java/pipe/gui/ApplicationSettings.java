package pipe.gui;

import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.models.PipeApplicationModel;
import pipe.views.PipeApplicationView;

public class ApplicationSettings
{
    private static final String _imgPath = "." + System.getProperty("file.separator") + "images" + System.getProperty("file.separator");
    private static PipeApplicationView _applicationView;
    private static PipeApplicationController _applicationController;
    private static PipeApplicationModel _applicationModel;

    public static void register(PipeApplicationView view)
    {
        _applicationView = view;
    }

    public static void register(PipeApplicationController applicationController)
    {
        _applicationController = applicationController;
    }

    public static void register(PipeApplicationModel applicationModel)
    {
        _applicationModel = applicationModel;
    }

    public static String getImagePath()
    {
        return _imgPath;
    }

    public static String getExamplesDirectoryPath()
    {
        return "extras"+ System.getProperty("file.separator") +"examples";
    }

    public static PipeApplicationView getApplicationView()
    {
        return _applicationView;
    }

    public static PipeApplicationController getApplicationController()
    {
        return _applicationController;
    }

    public static PipeApplicationModel getApplicationModel()
    {
        return _applicationModel;
    }

    public static PetriNetController getPetriNetController()
    {
        return _applicationController.getPetriNetController();
    }
}
