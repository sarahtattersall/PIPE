package pipe.petrinet.writer;


import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import pipe.models.component.Transition;
import pipe.petrinet.writer.reflectionCreator.ElementCreator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TransitionElementCreatorTest {
    private ElementCreator creator;
    private Document document;

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String RATE = "10";
    private static final int ANGLE = 45;
    private static final double POS_X = 550;
    private static final double POS_Y = 228;
    private static final double NAME_X = 5.0;
    private static final double NAME_Y = 2.4;
    private static final int PRIORITY = 6;
    private static final boolean INFINITE_SERVER = true;
    private static final boolean TIMED = false;

    @Before
    public void setUp() throws ParserConfigurationException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        document = builder.newDocument();
        creator = new ElementCreator();
    }

    private Transition createTransition() {
        Transition transition = new Transition(ID, NAME, RATE, PRIORITY);
        transition.setNameXOffset(NAME_X);
        transition.setNameYOffset(NAME_Y);
        transition.setX(POS_X);
        transition.setY(POS_Y);

        transition.setAngle(ANGLE);
        transition.setInfiniteServer(INFINITE_SERVER);
        transition.setTimed(TIMED);
        return transition;
    }

    @Test
    public void createsCorrectTag() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Transition transition = createTransition();
        Element element = creator.createElement(transition, document);
        assertEquals("transition", element.getTagName());
    }

    @Test
    public void writesCorrectId() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Transition transition = createTransition();
        Element element = creator.createElement(transition, document);

        String id = element.getAttribute("id");
        assertFalse(id.isEmpty());
        assertEquals(ID, id);
    }

    @Test
    public void writesCorrectName() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Transition transition = createTransition();
        Element element = creator.createElement(transition, document);

        String name = element.getAttribute("name");
        assertFalse(name.isEmpty());
        assertEquals(NAME, name);
    }

    @Test
    public void writesCorrectPosition() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Transition transition = createTransition();
        Element element = creator.createElement(transition, document);

        String positionX = element.getAttribute("positionX");
        assertFalse(positionX.isEmpty());
        assertEquals(String.valueOf(POS_X), positionX);

        String positionY = element.getAttribute("positionY");
        assertFalse(positionY.isEmpty());
        assertEquals(String.valueOf(POS_Y), positionY);
    }


    @Test
    public void writesCorrectNameOffset() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Transition transition = createTransition();
        Element element = creator.createElement(transition, document);

        String nameOffsetX = element.getAttribute("nameOffsetX");
        assertFalse(nameOffsetX.isEmpty());
        assertEquals(String.valueOf(NAME_X), nameOffsetX);

        String nameOffsetY = element.getAttribute("nameOffsetY");
        assertFalse(nameOffsetY.isEmpty());
        assertEquals(String.valueOf(NAME_Y), nameOffsetY);
    }

    @Test
    public void writesCorrectValueForInfiniteServer() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Transition transition = createTransition();
        Element element = creator.createElement(transition, document);

        String infiniteServer = element.getAttribute("infiniteServer");
        assertFalse(infiniteServer.isEmpty());
        assertEquals(String.valueOf(INFINITE_SERVER), infiniteServer);
    }

    @Test
    public void writesCorrectValueForTimed() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Transition transition = createTransition();
        Element element = creator.createElement(transition, document);

        String timed = element.getAttribute("timed");
        assertFalse(timed.isEmpty());
        assertEquals(String.valueOf(TIMED), timed);
    }


    @Test
    public void writesCorrectAngle() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Transition transition = createTransition();
        Element element = creator.createElement(transition, document);

        String angle = element.getAttribute("angle");
        assertFalse(angle.isEmpty());
        assertEquals(String.valueOf(ANGLE), angle);
    }

    @Test
    public void writesCorrectRate() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Transition transition = createTransition();
        Element element = creator.createElement(transition, document);

        String rate = element.getAttribute("rate");
        assertFalse(rate.isEmpty());
        assertEquals(RATE, rate);
    }

    @Test
    public void writesCorrectPriority() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Transition transition = createTransition();
        Element element = creator.createElement(transition, document);

        String priority = element.getAttribute("priority");
        assertFalse(priority.isEmpty());
        assertEquals(String.valueOf(PRIORITY), priority);
    }

}
