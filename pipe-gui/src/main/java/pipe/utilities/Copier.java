package pipe.utilities;

import pipe.models.Marking;
import pipe.views.MarkingView;

import java.io.*;
import java.util.LinkedList;
import java.util.List;


/**
 * @author Alex Charalambous (June 2010): It is common
 *         for complex objects such as Markings and TokenClasses
 *         to be cloned. As the default clone procedure only does
 *         a shadow copy this class creates a deep copy of any
 *         object passed in (given that the object is serializable)
 *         Especially useful for undo functions.
 */

public class Copier 
{
    public static Object deepCopy(Object obj)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream;
        Object deepCopy = null;
        try
        {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

            objectOutputStream.writeObject(obj);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                    byteArrayOutputStream.toByteArray());
            ObjectInputStream objectInputStream = new ObjectInputStream(
                    byteArrayInputStream);
            deepCopy = objectInputStream.readObject();

        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        catch(ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        return deepCopy;

    }

    public static List<Marking> mediumCopyMarkings(List<Marking> markingViews)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream;
        LinkedList<Marking> mediumCopy = null;
        try
        {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(markingViews);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                    byteArrayOutputStream.toByteArray());
            ObjectInputStream objectInputStream = new ObjectInputStream(
                    byteArrayInputStream);
            mediumCopy = (LinkedList<Marking>) objectInputStream.readObject();
            // This defines the medium copy. Replace token classes with
            // the actual reference from the model so that any updates to the model
            // are reflected in this marking.
            for(int i = 0; i < mediumCopy.size(); i++)
            {
                mediumCopy.get(i).setToken(markingViews.get(i).getToken());
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        catch(ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        return mediumCopy;

    }

    public static List<MarkingView> mediumCopy(List<MarkingView> markingViews)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream;
        LinkedList<MarkingView> mediumCopy = null;
        try
        {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(markingViews);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                    byteArrayOutputStream.toByteArray());
            ObjectInputStream objectInputStream = new ObjectInputStream(
                    byteArrayInputStream);
            mediumCopy = (LinkedList<MarkingView>) objectInputStream.readObject();
            // This defines the medium copy. Replace token classes with
            // the actual reference from the model so that any updates to the model
            // are reflected in this marking.
            for(int i = 0; i < mediumCopy.size(); i++)
            {
                mediumCopy.get(i).setToken(markingViews.get(i).getToken());
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        catch(ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        return mediumCopy;

    }
}
