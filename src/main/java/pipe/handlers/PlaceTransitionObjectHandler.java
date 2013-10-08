package pipe.handlers;

import pipe.gui.*;
import pipe.historyActions.AddPetriNetObject;
import pipe.historyActions.HistoryManager;
import pipe.models.PipeApplicationModel;
import pipe.utilities.Copier;
import pipe.views.*;
import pipe.views.TokenView;
import pipe.views.MarkingView;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Class used to implement methods corresponding to mouse events on places.
 *
 * @author Pere Bonet - changed the mousePressed method to only allow the
 * creation of an arc by left-clicking
 * @author Matthew Worthington - modified the handler which was causing the
 * null pointer exceptions and incorrect petri nets xml representation.
 */
public class PlaceTransitionObjectHandler 
        extends PetriNetObjectHandler
{
   // STATIC ATTRIBUTES AND METHODS
	private static boolean mouseDown = false;
	   
	public static boolean isMouseDown(){
		   return mouseDown;
	}
	
   private ArcKeyboardEventHandler keyHandler = null;
   
   // constructor passing in all required objects
   PlaceTransitionObjectHandler(Container contentpane,
                                ConnectableView obj) {
      super(contentpane, obj);
      enablePopup = true;
   }
   
   
   private void createArc(ArcView newArcView, ConnectableView currentObject){
       TokenView tc = ApplicationSettings.getApplicationView().getCurrentPetriNetView().getActiveTokenView();
	   MarkingView m = new MarkingView(tc, 1+"");
	   m.addObserver(newArcView);  // Steve Doubleday:  ArcView tracks updates to MarkingView 
	   LinkedList<MarkingView> markingViews = new LinkedList<MarkingView>();
	   markingViews.add(m);
	   newArcView.setWeight(markingViews);
       newArcView.setZoom(ApplicationSettings.getApplicationView().getCurrentTab().getZoom());
      contentPane.add(newArcView);
      currentObject.addOutbound(newArcView);
       ApplicationSettings.getApplicationView().getCurrentTab()._createArcView = newArcView;
      // addPetriNetObject a handler for shift & esc actions drawing arc
      // this is removed when the arc is finished drawing:
      keyHandler = new ArcKeyboardEventHandler(newArcView);
      newArcView.addKeyListener(keyHandler);
      newArcView.requestFocusInWindow();
      newArcView.setSelectable(false);
   }
   
   
   public void mousePressed(MouseEvent e) {
      super.mousePressed(e);
      mouseDown = true;
      // Prevent creating arcs with a right-click or a middle-click
      if (e.getButton() != MouseEvent.BUTTON1) {
         return;
      }
      
      ConnectableView currentObject = (ConnectableView) my;
       switch (ApplicationSettings.getApplicationModel().getMode()) {
         case Constants.ARC:
            if (e.isControlDown()) {
               // user is holding Ctrl key; switch to fast mode
               if (this.my instanceof PlaceView) {
                   ApplicationSettings.getApplicationModel().enterFastMode(Constants.FAST_TRANSITION);
               } else if (this.my instanceof TransitionView) {
                   ApplicationSettings.getApplicationModel().enterFastMode(Constants.FAST_PLACE);
               }
            }
         case Constants.INHIBARC:
         case Constants.FAST_PLACE:
         case Constants.FAST_TRANSITION:
             if (ApplicationSettings.getApplicationView().getCurrentTab()._createArcView == null) {
                 if (ApplicationSettings.getApplicationModel().getMode() == Constants.INHIBARC){
                  if (currentObject instanceof PlaceView) {
                      createArc(new InhibitorArcView(currentObject), currentObject);
                  }
               } else {
                   createArc(new NormalArcView(currentObject), currentObject);
               }
            }
            break;
            
         default:
            break;
      }
   }
   
   
   public void mouseReleased(MouseEvent e) {
      boolean isNewArc = true; // true if we have to add a new arc to the Petri Net
      boolean fastMode = false;

       PetriNetTab view = ApplicationSettings.getApplicationView().getCurrentTab();
       PetriNetView model = ApplicationSettings.getApplicationView().getCurrentPetriNetView();
      HistoryManager historyManager = view.getHistoryManager();
       PipeApplicationModel app = ApplicationSettings.getApplicationModel();
      
      super.mouseReleased(e);
      
      ConnectableView currentObject = (ConnectableView) my;
      
      switch (app.getMode()) {
         case Constants.INHIBARC:
            InhibitorArcView createInhibitorArcView = (InhibitorArcView) view._createArcView;
            if (createInhibitorArcView != null) {
               if (!currentObject.getClass().equals(
                       createInhibitorArcView.getSource().getClass())) {
                  
                  Iterator arcsFrom =
                          createInhibitorArcView.getSource().getConnectFromIterator();
                  // search for pre-existent arcs from createInhibitorArc's 
                  // source to createInhibitorArc's target
                  while(arcsFrom.hasNext()) {
                     ArcView someArcView = ((ArcView)arcsFrom.next());
                     if (someArcView == createInhibitorArcView) {
                        break;
                     } else if (someArcView.getTarget() == currentObject &&
                             someArcView.getSource() == createInhibitorArcView.getSource()) {
                        isNewArc = false;
                        if (someArcView instanceof NormalArcView){
                           // user has drawn an inhibitor arc where there is 
                           // a normal arc already - nothing to do
                        } else if (someArcView instanceof InhibitorArcView) {
                           // user has drawn an inhibitor arc where there is 
                           // an inhibitor arc already - we increment arc's 
                           // weight
                           LinkedList<MarkingView> weight = Copier.mediumCopy(someArcView.getWeight());
                           for(MarkingView m:weight){
                        	   m.setCurrentMarking(m.getCurrentMarking()+1);
                           }
                           historyManager.addNewEdit(someArcView.setWeight(someArcView.getWeight()));
                        } else {
                           // This is not supposed to happen
                        }
                        createInhibitorArcView.delete();
                        someArcView.getTransition().removeArcCompareObject(
                                createInhibitorArcView);
                        someArcView.getTransition().updateConnected();
                        break;
                     }
                  }
                  
                  if (isNewArc) {
                     createInhibitorArcView.setSelectable(true);
                     createInhibitorArcView.setTarget(currentObject);
                     currentObject.addInbound(createInhibitorArcView);
                     // Evil hack to prevent the arc being added to PetriNetTab twice
                     contentPane.remove(createInhibitorArcView);
                     model.addArc(createInhibitorArcView);
                     view.addNewPetriNetObject(createInhibitorArcView);
                     historyManager.addNewEdit(
                             new AddPetriNetObject(createInhibitorArcView,
                             view, model));
                  }
                  
                  // arc is drawn, remove handler:
                  createInhibitorArcView.removeKeyListener(keyHandler);
                  keyHandler = null;
                  view._createArcView = null;
               }
            }
            break;
            
         case Constants.FAST_TRANSITION:
         case Constants.FAST_PLACE:
            fastMode = true;
         case Constants.ARC:
            ArcView createArcView = view._createArcView;
            if (createArcView != null) {
               if (currentObject != createArcView.getSource()) {
                  createArcView.setSelectable(true);
                  Iterator arcsFrom = createArcView.getSource().getConnectFromIterator();
                  // search for pre-existent arcs from createArc's source to 
                  // createArc's target                  
                  while(arcsFrom.hasNext()) {
                     ArcView someArcView = ((ArcView)arcsFrom.next());
                     if (someArcView == createArcView) {
                        break;
                     } else if (someArcView.getSource() == createArcView.getSource() &&
                             someArcView.getTarget() == currentObject) {
                        isNewArc = false;
                        if (someArcView instanceof NormalArcView) {
                           // user has drawn a normal arc where there is 
                           // a normal arc already - we increment arc's weight
                        	
                            LinkedList<MarkingView> weight = Copier.mediumCopy(someArcView.getWeight());
                            for(MarkingView m:weight){
                         	   m.setCurrentMarking(m.getCurrentMarking()+1);
                            }
                            historyManager.addNewEdit(someArcView.setWeight(weight));
                        } else{
                           // user has drawn a normal arc where there is 
                           // an inhibitor arc already - nothing to do
                           //System.out.println("DEBUG: arc normal i arc inhibidor!");
                        }
                        createArcView.delete();
                        someArcView.getTransition().removeArcCompareObject(createArcView);
                        someArcView.getTransition().updateConnected();
                        break; 
                     }
                  }
                  
                  NormalArcView inverse = null;
                  if (isNewArc) {
                     createArcView.setTarget(currentObject);
                     
                     //check if there is an inverse arc
                     Iterator arcsFromTarget =
                             createArcView.getTarget().getConnectFromIterator();
                     while (arcsFromTarget.hasNext()) {
                        ArcView anArcView = (ArcView)arcsFromTarget.next();
                        if (anArcView.getTarget() == createArcView.getSource()) {
                           if (anArcView instanceof NormalArcView) {
                              inverse = (NormalArcView) anArcView;
                              // inverse arc found
                              if (inverse.hasInverse()){
                                 // if inverse arc has an inverse arc, it means
                                 // that createArc is equal to inverse's inverse
                                 // arc so we only have to increment its weight
                                 isNewArc = false;
                                 
                                 LinkedList<MarkingView> weightInverse = Copier.mediumCopy(inverse.getInverse().getWeight());
                                 for(MarkingView m:weightInverse){
                              	   m.setCurrentMarking(m.getCurrentMarking()+1);
                                 }
                                 historyManager.addNewEdit( inverse.getInverse().setWeight(weightInverse));
                                 
                                 createArcView.delete();
                                 inverse.getTransition().removeArcCompareObject(
                                         createArcView);
                                 inverse.getTransition().updateConnected();
                              }
                              break;
                           }
                        }
                     }
                  }
                  
                  if (isNewArc) {
                     currentObject.addInbound(createArcView);
                     
                     // Evil hack to prevent the arc being added to PetriNetTab twice
                     contentPane.remove(createArcView);
                     
                     model.addArc((NormalArcView) createArcView);
                     view.addNewPetriNetObject(createArcView);
                     if (!fastMode) {
                        // we are not in fast mode so we have to set a new edit
                        // in historyManager for adding the new arc
                        historyManager.newEdit(); // new "transaction""
                     }
                     historyManager.addEdit(
                             new AddPetriNetObject(createArcView, view, model));
                     if (inverse != null) {
                        historyManager.addEdit(
                                inverse.setInverse((NormalArcView) createArcView,
                                Constants.JOIN_ARCS));
                     }
                  }
                  
                  // arc is drawn, remove handler:
                  createArcView.removeKeyListener(keyHandler);
                  keyHandler = null;
                  /**/
                  if (!isNewArc){
                     view.remove(createArcView);
                  }
                  /* */
                  view._createArcView = null;
               }
            }
            
            if (app.getMode() == Constants.FAST_PLACE ||
                    app.getMode() == Constants.FAST_TRANSITION) {
               if (view._wasNewPertiNetComponentCreated) {
                  // a new PNO has been created 
                  view._wasNewPertiNetComponentCreated = false;

                  if (currentObject instanceof TransitionView) {
                     app.setMode(Constants.FAST_PLACE);
                  } else if (currentObject instanceof PlaceView) {
                     app.setMode(Constants.FAST_TRANSITION);
                  }
               } else {
                  if (view._createArcView == null) {
                     // user has clicked on an existent PNO
                     app.resetMode();
                  } else {
                     if (currentObject instanceof TransitionView) {
                        app.setMode(Constants.FAST_PLACE);
                     } else if (currentObject instanceof PlaceView) {
                        app.setMode(Constants.FAST_TRANSITION);
                     }
                  }
               }
            }
            break;
            
         default:
            break;
      }
      
      mouseDown = false;
   }
   
}
