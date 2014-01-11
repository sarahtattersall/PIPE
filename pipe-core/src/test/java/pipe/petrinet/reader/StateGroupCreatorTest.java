//package pipe.petrinet;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.NodeList;
//import pipe.common.dataLayer.StateElement;
//import pipe.common.dataLayer.StateGroup;
//import pipe.petrinet.reader.creator.StateGroupCreator;
//import pipe.utilities.transformers.PNMLTransformer;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//
//public class StateGroupCreatorTest {
//    StateGroupCreator creator;
//    Element stateGroupElement;
//    /**
//     * Range in which to declare doubles equal
//     */
//    private static final double DOUBLE_DELTA = 0.001;
//
//    @Before
//    public void setUp()
//    {
//        PNMLTransformer transformer = new PNMLTransformer();
//        Document document = transformer.transformPNML("pipe-gui/src/test/resources/xml/stategroup/stategroup.xml");
//        Element rootElement = document.getDocumentElement();
//        NodeList nodes = rootElement.getChildNodes();
//        stateGroupElement = (Element) nodes.item(1);
//    }
//
//    @Test
//    public void createsStateGroup() {
//        creator = new StateGroupCreator();
//        StateGroup stateGroup = creator.create(stateGroupElement);
//
//        assertNotNull(stateGroup);
//        assertEquals("SG0", stateGroup.getId());
//        assertEquals("SG0Name", stateGroup.getName());
//    }
//
//    @Test
//    public void handlesStateCondition() {
//        creator = new StateGroupCreator();
//        StateGroup stateGroup = creator.create(stateGroupElement);
//
//        StateElement element = stateGroup.getCondition("P0");
//        assertNotNull(element);
//
//        assertEquals("P0", element.getPlaceA());
//        assertEquals(">", element.getOperator());
//        assertEquals("4", element.getPlaceB());
//    }
//}
