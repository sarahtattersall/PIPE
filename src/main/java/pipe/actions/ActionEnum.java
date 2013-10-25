package pipe.actions;

import pipe.actions.file.FileAction;
import pipe.models.PipeApplicationModel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * User: st809
 * Date: 25/10/2013
 * Time: 20:21
 * To change this template use File | Settings | File Templates.
 */
public enum ActionEnum {
    CREATE ("Create"), OPEN ("Open"), CLOSE ("Close"), SAVE ("Save"), SAVEAS ("SaveAs"), PRINT ("Print"), EXPORTPNG ("ExportPNG"),
    EXPORTTN ("ExportTN"), EXPORTPS ("ExportPS"), IMPORT ("Import");


    /**
     * camelCase name in getNameAction() method in PipeApplicationModel
     */
    private String name;

     ActionEnum(String name) {
         this.name = name;
     }
    //TODO: Is there a nicer way to do this?

    /**
     *
     * @param model PipeApplicationModel of Action to get
     * @return FileAction based on enum type
     */
    public FileAction get(PipeApplicationModel model) {
        String methodName = getMethodName();
        try {
            Method method = PipeApplicationModel.class.getMethod(methodName, null);
            return (FileAction) method.invoke(model);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return Java bean method name for action, based on getters in {@link PipeApplicationModel}
     */
    private String getMethodName() {

        StringBuilder methodBuilder = new StringBuilder();
        methodBuilder.append("get");
        methodBuilder.append(name);
        methodBuilder.append("Action");

        return methodBuilder.toString();
    }
}
