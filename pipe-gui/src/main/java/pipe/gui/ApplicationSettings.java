package pipe.gui;

import pipe.gui.model.PipeApplicationModel;
import pipe.controllers.PipeApplicationController;
import pipe.views.PipeApplicationView;

import java.io.File;

public class ApplicationSettings
{
    private static final String _imgPath =  File.separator + "images" + File.separator;
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
        return "extras"+ File.separator +"examples";
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
}
