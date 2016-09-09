package pipe.views;


//TODO:FIX
public class ConnectableViewTest {

//    ConnectableView<Connectable> view;
//    Connectable mockModel;
//
//    private PetriNetController mockController;
//
//    @Before
//    public void setUp() {
//        mockModel = mock(Connectable.class);
//        view = new DummyConnectableView(mockModel);
//        mockController = mock(PetriNetController.class);
//    }
//
//    @Test
//    public void zoomingMovesNameLabel() {
//        double modelLocation = 50;
//        double xOffset = 10;
//        double yOffset = 40;
//        when(mockModel.getX()).thenReturn(modelLocation);
//        when(mockModel.getY()).thenReturn(modelLocation);
//        when(mockModel.getNameXOffset()).thenReturn(xOffset);
//        when(mockModel.getNameYOffset()).thenReturn(yOffset);
//
//        int zoomValue = 120;
//        view.zoomUpdate(zoomValue);
//        view.update();
//        NameLabel label = view.getNameLabel();
//        double expectedX = ZoomController.getZoomedValue(mockModel.getX(), zoomValue) + mockModel.getNameXOffset();
//        double expectedY = ZoomController.getZoomedValue(mockModel.getY(), zoomValue) + mockModel.getNameYOffset();
//
//        assertEquals(expectedX, label.getPositionX(), 0.001);
//        assertEquals(expectedY, label.getPositionY(), 0.001);
//    }
//
//    private class DummyConnectableView extends ConnectableView<Connectable>
//    {
//
//        DummyConnectableView(Connectable model) {
//            super("test", "test", 0, 0, model, mockController);
//        }
//
//        @Override
//        public void showEditor() {
//        }
//
//        @Override
//        public void toggleAttributesVisible() {
//        }
//
//        @Override
//        public void addToPetriNetTab(PetriNetTab tab) {
//        }
//
//        @Override
//        public PetriNetViewComponent copy() {
//            return null;
//        }
//
//        @Override
//        public PetriNetViewComponent paste(double despX, double despY, boolean notInTheSameView, PetriNetView model) {
//            return null;
//        }
//    }
}
