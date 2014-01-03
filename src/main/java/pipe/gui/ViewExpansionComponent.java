package pipe.gui;

import pipe.views.PetriNetView;
import pipe.views.PetriNetViewComponent;

/**
 * @author unknown
 */
public class ViewExpansionComponent 
        extends PetriNetViewComponent {

   private int originalX = 0;
   private int originalY = 0;
   
   
   private ViewExpansionComponent() {
      super();
   }
   
  
   public ViewExpansionComponent(int x, int y){
      this();
      originalX = x;
      originalY = y;
      setLocation(x,y);
   }
   

   public void zoomUpdate(int zoom) {
      double scaleFactor = ZoomController.getScaleFactor(zoom);
      setLocation((int)(originalX * scaleFactor),(int)(originalY * scaleFactor));
   }   

   
   public void addedToGui() {
   }

   
   public PetriNetViewComponent copy() {
      return null;
   }

   
   public PetriNetViewComponent paste(double despX, double despY, boolean inAnotherView, PetriNetView model) {
      return null;
   }

   
   public int getLayerOffset() {
      return 0;
   }

    @Override
    public void addToPetriNetTab(PetriNetTab tab) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    public void translate(int x, int y) {
   }

}
