package pipe.actions.file;

import org.junit.Before;
import org.junit.Test;
import pipe.actions.gui.file.SaveAsAction;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.views.PipeApplicationView;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import java.awt.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class SaveAsActionTest {
    SaveAsAction saveAsAction;
    PipeApplicationView mockView;

    PipeApplicationController mockController;

    PetriNetController mockPetriNetController;

    FileDialog mockFileChooser;

    @Before
    public void setUp() {
        mockView = mock(PipeApplicationView.class);
        mockController = mock(PipeApplicationController.class);
        mockFileChooser= mock(FileDialog.class);
        mockPetriNetController = mock(PetriNetController.class);
        when(mockController.getActivePetriNetController()).thenReturn(mockPetriNetController);
        saveAsAction = new SaveAsAction(mockView, mockController, mockFileChooser);
    }

    @Test
    public void setShortDescription()
    {
        Object shortDescription = saveAsAction.getValue(Action.SHORT_DESCRIPTION);
        assertEquals("Save as...", shortDescription);
    }

    @Test
    public void setKeyboardShortcut()
    {
        Object acceleratorKey = saveAsAction.getValue(Action.ACCELERATOR_KEY);
        KeyStroke stroke = KeyStroke.getKeyStroke("meta shift S");
        assertEquals(stroke, acceleratorKey);
    }

    @Test
    public void performsSaveAsWhenPetriNetHasNoFile()
            throws InvocationTargetException, ParserConfigurationException, NoSuchMethodException,
            IllegalAccessException, TransformerException {
        when(mockPetriNetController.getFileName()).thenReturn("");
        File file = new File("test.xml");
        when(mockFileChooser.getFile()).thenReturn(file.getPath());
        saveAsAction.actionPerformed(null);
        verify(mockController).saveAsCurrentPetriNet(file);
    }

    @Test
    public void performsSaveAsWhenEvenIfPetriNetHasFile()
            throws InvocationTargetException, ParserConfigurationException, NoSuchMethodException,
            IllegalAccessException, TransformerException {
        File file = new File("test.xml");
        when(mockPetriNetController.getFileName()).thenReturn(file.getPath());
        when(mockFileChooser.getFile()).thenReturn(file.getPath());
        saveAsAction.actionPerformed(null);
        verify(mockController).saveAsCurrentPetriNet(file);
    }


    @Test
    public void updatesPetriNetName() {
        when(mockPetriNetController.getFileName()).thenReturn("");
        File file = new File("test.xml");
        when(mockFileChooser.getFile()).thenReturn(file.getPath());
        saveAsAction.actionPerformed(null);
        verify(mockPetriNetController).setFileName(file.getName());
    }

}
