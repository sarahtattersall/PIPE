/*
 * Translatable.java
 */

package pipe.gui;

import pipe.views.PetriNetView;
import pipe.views.PetriNetViewComponent;


/**
 * This is the interface that a component must implement so that it can be 
 * copied and pasted.
 * @author Pere Bonet
 */
public interface CopyPasteable {

   /** 
    * copy()
    * @return a copy of the PetriNetViewComponent
    */
   public PetriNetViewComponent copy();

   /** 
    * paste()
    * @param despX
    * @param despY
    * @param notInTheSameView
    * @param model
    * @return a copy of the saved PetriNetViewComponent that can be added to a PetriNetTab
    * instance
    */
   public PetriNetViewComponent paste(double despX, double despY,
           boolean notInTheSameView, PetriNetView model);
   
   /**
    * isCopyPasteable();
    * @return true if this object can be copied and pasted
    */
   public boolean isCopyPasteable();
   
}
