package pipe.actions.file;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.actions.gui.file.SaveAction;
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
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SaveActionTest {
    SaveAction saveAction;

    @Mock
    PipeApplicationView mockView;

    @Mock
    PipeApplicationController mockController;

    @Mock
    PetriNetController mockPetriNetController;

    @Mock
    FileDialog mockFileChooser;

    @Before
    public void setUp() {
        saveAction = new SaveAction(mockView, mockController, mockFileChooser);
        when(mockController.getActivePetriNetController()).thenReturn(mockPetriNetController);
    }

    @Test
    public void performsSaveAsWhenPetriNetHasNoFile()
            throws InvocationTargetException, ParserConfigurationException, NoSuchMethodException,
            IllegalAccessException, TransformerException {
        when(mockPetriNetController.getFileName()).thenReturn("");
        File file = new File("test.xml");
        when(mockFileChooser.getFile()).thenReturn(file.getPath());
        saveAction.actionPerformed(null);
        verify(mockController).saveCurrentPetriNet(file);
    }


    @Test
    public void updatesPetriNetNameIfNotSet() {
        when(mockPetriNetController.getFileName()).thenReturn("");
        File file = new File("test.xml");
        when(mockFileChooser.getFile()).thenReturn(file.getPath());
        saveAction.actionPerformed(null);
        verify(mockPetriNetController).setFileName(file.getName());
    }


    @Test
    public void doesNotUpdatePetriNetIfUsingName() {
        File file = new File("test.xml");
        when(mockPetriNetController.getFileName()).thenReturn(file.getPath());

        saveAction.actionPerformed(null);
        verify(mockPetriNetController, never()).setFileName(file.getName());
    }

    @Test
    public void performsSaveWhenPetriNetHasFile()
            throws InvocationTargetException, ParserConfigurationException, NoSuchMethodException,
            IllegalAccessException, TransformerException {
        File file = new File("test.xml");
        when(mockPetriNetController.getFileName()).thenReturn(file.getPath());

        saveAction.actionPerformed(null);

        verify(mockController).saveCurrentPetriNet(file);
        verify(mockFileChooser, never()).setVisible(true);

    }

    @Test
    public void setShortDescription() {
        Object shortDescription = saveAction.getValue(Action.SHORT_DESCRIPTION);
        assertEquals("Save", shortDescription);
    }

    @Test
    public void setKeyboardShortcut() {
        Object acceleratorKey = saveAction.getValue(Action.ACCELERATOR_KEY);
        KeyStroke stroke = KeyStroke.getKeyStroke("meta S");
        assertEquals(stroke, acceleratorKey);
    }

}
