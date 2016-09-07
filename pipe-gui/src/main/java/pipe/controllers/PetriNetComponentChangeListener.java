package pipe.controllers;

import pipe.gui.PetriNetTab;
import pipe.actions.gui.PipeApplicationModel;
import pipe.views.*;
import uk.ac.imperial.pipe.models.petrinet.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class listens for changes in PetriNet components 
 * and creates/deletes the relevant views as appropriate
 */
public class PetriNetComponentChangeListener implements PropertyChangeListener {

    /**
     * Class logger
     */
    private static final Logger LOGGER = Logger.getLogger(PetriNetComponentChangeListener.class.getName());

    /**
     * Pipe application model, needed for building items so that
     * the listeners can do the correct thing
     */
    private final PipeApplicationModel applicationModel;

    /**
     * PetriNetTab that this listener refers to
     */
    private final PetriNetTab petriNetTab;

    /**
     * Petri net controller for the Petri net displayed on the tab
     */
    private final PetriNetController controller;

    /**
     * Contains the property name and method it maps to
     */
    private Map<String, Method> eventMethods = new HashMap<>();

    /**
     * Constructor
     * @param applicationModel main PIPE application model
     * @param petriNetTab Petri net tab
     * @param controller conroller for the Petri net displayed graphically on the tab
     */
    public PetriNetComponentChangeListener(PipeApplicationModel applicationModel, PetriNetTab petriNetTab, PetriNetController controller) {
        this.applicationModel = applicationModel;
        this.petriNetTab = petriNetTab;
        this.controller = controller;
        registerMethods();
    }

    /**
     * Register the methods that respond to Petri net pub-sub events
     */
    private void registerMethods() {
        for (Method method : this.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(EventAction.class)) {
                EventAction annotation = method.getAnnotation(EventAction.class);
                eventMethods.put(annotation.value(), method);
            }
        }
    }

    /**
     * When a property of the Petri net has changed this triggers
     * its corresponding registered method to be called
     *
     * If no method exists for the message it will go unpassed
     * @param propertyChangeEvent property change event 
     */
    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        String name = propertyChangeEvent.getPropertyName();
        if (eventMethods.containsKey(name)) {
            Method method = eventMethods.get(name);
            try {
                method.invoke(this, propertyChangeEvent);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                LOGGER.log(Level.SEVERE, e.toString());
            }
        }
    }

    /**
     *
     * When a new place is added to the Petri net it creates a new view and adds it to the
     * Petri net tab
     *
     * @param propertyChangeEvent
     */
    @EventAction(PetriNet.NEW_PLACE_CHANGE_MESSAGE)
    private void newPlace(PropertyChangeEvent propertyChangeEvent) {
        Place place = (Place) propertyChangeEvent.getNewValue();
        PlaceViewBuilder builder = new PlaceViewBuilder(place, controller);
        PlaceView view = builder.build(petriNetTab, applicationModel);
        petriNetTab.addNewPetriNetComponent(view);
    }

    /**
     * When a new transition is added to the Petri net it creates a new view
     * and adds it to the Petri net tab
     * @param propertyChangeEvent
     */
    @EventAction(PetriNet.NEW_TRANSITION_CHANGE_MESSAGE)
    private void newTransition(PropertyChangeEvent propertyChangeEvent) {
        Transition transition = (Transition) propertyChangeEvent.getNewValue();
        TransitionViewBuilder builder = new TransitionViewBuilder(transition, controller);
        TransitionView view = builder.build(petriNetTab, applicationModel);

        petriNetTab.addNewPetriNetComponent(view);

    }

    /**
     * When a new arc is added to the Petri net it creates a new view
     * and adds it to the Petri net tab
     * @param propertyChangeEvent
     */
    @EventAction(PetriNet.NEW_ARC_CHANGE_MESSAGE)
    private void newArc(PropertyChangeEvent propertyChangeEvent) {
        Arc<? extends Connectable, ? extends Connectable> arc =
                (Arc<? extends Connectable, ? extends Connectable>) propertyChangeEvent.getNewValue();

        if (arc.getType().equals(ArcType.INHIBITOR)) {
            Arc<Place, Transition> inhibitorArc = (Arc<Place, Transition>) arc;
            InhibitorArcViewBuilder builder = new InhibitorArcViewBuilder(inhibitorArc, controller);
            InhibitorArcView view = builder.build(petriNetTab, applicationModel);
            petriNetTab.addNewPetriNetComponent(view);
        } else {
            NormalArcViewBuilder builder = new NormalArcViewBuilder(arc, controller);
            ArcView<? extends Connectable, ? extends Connectable> view = builder.build(petriNetTab, applicationModel);
            petriNetTab.addNewPetriNetComponent(view);
        }

    }
    /**
     * When a new rate is added it does nothing
     * @param propertyChangeEvent
     */
    @EventAction(PetriNet.NEW_RATE_PARAMETER_CHANGE_MESSAGE)
    private void newRate(PropertyChangeEvent propertyChangeEvent) {
        //TODO: ?

    }
    /**
     * When a new annotation is added to the Petri net it creates a new view
     * and adds it to the Petri net tab
     * @param propertyChangeEvent
     */
    @EventAction(PetriNet.NEW_ANNOTATION_CHANGE_MESSAGE)
    private void newAnnotation(PropertyChangeEvent propertyChangeEvent) {
        Annotation annotation = (Annotation) propertyChangeEvent.getNewValue();
        AnnotationViewBuilder builder = new AnnotationViewBuilder(annotation, controller);

        AnnotationView view = builder.build(petriNetTab, applicationModel);
        petriNetTab.addNewPetriNetComponent(view);

    }

    /**
     * When a place is deleted it removes the view from the tab
     * @param propertyChangeEvent
     */
    @EventAction(PetriNet.DELETE_PLACE_CHANGE_MESSAGE)
    private void deletePlace(PropertyChangeEvent propertyChangeEvent) {
        Place place = (Place) propertyChangeEvent.getOldValue();
        petriNetTab.deletePetriNetComponent(place.getId());

    }

    /**
     * When a transition is deleted it removes the view from the tab
     * @param propertyChangeEvent
     */
    @EventAction(PetriNet.DELETE_TRANSITION_CHANGE_MESSAGE)
    private void deleteTransition(PropertyChangeEvent propertyChangeEvent) {
        Transition transition = (Transition) propertyChangeEvent.getOldValue();
        petriNetTab.deletePetriNetComponent(transition.getId());

    }

    /**
     * When an arc is deleted it removes the view from the tab
     * @param propertyChangeEvent
     */
    @EventAction(PetriNet.DELETE_ARC_CHANGE_MESSAGE)
    private void deleteArc(PropertyChangeEvent propertyChangeEvent) {
        Arc<? extends Connectable, ? extends Connectable> arc =
                (Arc<? extends Connectable, ? extends Connectable>) propertyChangeEvent.getOldValue();
        petriNetTab.deletePetriNetComponent(arc.getId());
    }

    /**
     * When a rate parameter is deleted it does nothing because they now have
     * no graphical display
     * @param propertyChangeEvent
     */
    @EventAction(PetriNet.DELETE_RATE_PARAMETER_CHANGE_MESSAGE)
    private void deleteRate(PropertyChangeEvent propertyChangeEvent) {
        //TODO: ?
    }

    /**
     * When an annotation is deleted it removes the view from the tab
     * @param propertyChangeEvent
     */
    @EventAction(PetriNet.DELETE_ANNOTATION_CHANGE_MESSAGE)
    private void deleteAnnotation(PropertyChangeEvent propertyChangeEvent) {
        Annotation annotation = (Annotation) propertyChangeEvent.getOldValue();
        petriNetTab.deletePetriNetComponent(annotation.getId());
    }


    /**
     * This interface is for annotating methods with their change event
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    private @interface EventAction {
        /**
         * @return the events property name to listen out for
         */
        String value();
    }
}
