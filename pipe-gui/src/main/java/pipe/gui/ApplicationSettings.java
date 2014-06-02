package pipe.gui;

import pipe.gui.model.PipeApplicationModel;
import pipe.views.PipeApplicationView;

import java.io.File;

public class ApplicationSettings
{
    private static final String _imgPath =  File.separator + "images" + File.separator;

    public static String getImagePath()
    {
        return _imgPath;
    }

    public static String getExamplesDirectoryPath()
    {
        return "extras"+ File.separator +"examples";
    }

}
