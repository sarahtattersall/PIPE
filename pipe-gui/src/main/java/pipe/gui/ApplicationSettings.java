package pipe.gui;

import pipe.gui.model.PipeApplicationModel;
import pipe.views.PipeApplicationView;

import java.io.File;

public class ApplicationSettings
{
    private static final String _imgPath =  File.separator + "images" + File.separator;
    private static PipeApplicationView _applicationView;

    private static PipeApplicationModel _applicationModel;

    public static void register(PipeApplicationView view)
    {
        _applicationView = view;
    }

    public static String getImagePath()
    {
        return _imgPath;
    }

    public static String getExamplesDirectoryPath()
    {
        return "extras"+ File.separator +"examples";
    }

    public static PipeApplicationModel getApplicationModel()
    {
        return _applicationModel;
    }
}
