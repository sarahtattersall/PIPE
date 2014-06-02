package pipe.actions.file;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.actions.gui.file.SaveAction;
import pipe.controllers.PetriNetController;
import pipe.controllers.application.PipeApplicationController;
import pipe.views.PipeApplicationView;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;
import uk.ac.imperial.pipe.models.petrinet.name.NormalPetriNetName;
import uk.ac.imperial.pipe.models.petrinet.name.PetriNetFileName;
import uk.ac.imperial.pipe.models.petrinet.name.PetriNetName;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.awt.FileDialog;
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
    PetriNet mockPetriNet;

    @Mock
    FileDialog mockFileChooser;

    @Before
    public void setUp() {
        saveAction = new SaveAction(mockController, mockFileChooser);
        when(mockController.getActivePetriNetController()).thenReturn(mockPetriNetController);
        when(mockPetriNetController.getPetriNet()).thenReturn(mockPetriNet);
    }

    @Test
    public void performsSaveAsWhenPetriNetHasNoFile()
            throws InvocationTargetException, ParserConfigurationException, NoSuchMethodException,
            IllegalAccessException, TransformerException {
        PetriNetName normalName = new NormalPetriNetName("");
        when(mockPetriNet.getName()).thenReturn(normalName);
        File file = new File("test.xml");
        when(mockFileChooser.getFiles()).thenReturn(new File[]{file});
        saveAction.actionPerformed(null);
        verify(mockController).saveAsCurrentPetriNet(file);
    }

    @Test
    public void performsSaveWhenPetriNetHasFile()
            throws InvocationTargetException, ParserConfigurationException, NoSuchMethodException,
            IllegalAccessException, TransformerException {
        File file = mock(File.class);
        when(file.getAbsolutePath()).thenReturn("");
        PetriNetName fileName = new PetriNetFileName(file);
        when(mockPetriNet.getName()).thenReturn(fileName);

        saveAction.actionPerformed(null);

        verify(mockController).saveAsCurrentPetriNet(file);
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
