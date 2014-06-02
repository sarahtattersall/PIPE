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

/**
 * This class listens for changes in PetriNet
 * and creates/deletes the relevant views as appropriate
 */
public class PetriNetChangeListener implements PropertyChangeListener {

    /**
     * Pipe appliaction model, needed for building items so that
     * the listeners can do the correct thing
     */
    private final PipeApplicationModel applicationModel;

    /**
     * PetriNetTab that this listener refers to
     */
    private final PetriNetTab petriNetTab;

    private final PetriNetController controller;

    /**
     * Contains the property name and method it maps to
     */
    private Map<String, Method> eventMethods = new HashMap<>();

    public PetriNetChangeListener(PipeApplicationModel applicationModel, PetriNetTab petriNetTab, PetriNetController controller) {
        this.applicationModel = applicationModel;
        this.petriNetTab = petriNetTab;
        this.controller = controller;
        registerMethods();
    }

    private void registerMethods() {
        for (Method method : this.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(EventAction.class)) {
                EventAction annotation = method.getAnnotation(EventAction.class);
                eventMethods.put(annotation.value(), method);
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        String name = propertyChangeEvent.getPropertyName();
        if (eventMethods.containsKey(name)) {
            Method method = eventMethods.get(name);
            try {
                method.invoke(this, propertyChangeEvent);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @EventAction(PetriNet.NEW_PLACE_CHANGE_MESSAGE)
    private void newPlace(PropertyChangeEvent propertyChangeEvent) {
        Place place = (Place) propertyChangeEvent.getNewValue();
        PlaceViewBuilder builder = new PlaceViewBuilder(place, controller);
        PlaceView view = builder.build(petriNetTab, applicationModel);
        petriNetTab.addNewPetriNetComponent(view);
    }

    @EventAction(PetriNet.NEW_TRANSITION_CHANGE_MESSAGE)
    private void newTransition(PropertyChangeEvent propertyChangeEvent) {
        Transition transition = (Transition) propertyChangeEvent.getNewValue();
        TransitionViewBuilder builder = new TransitionViewBuilder(transition, controller);
        TransitionView view = builder.build(petriNetTab, applicationModel);

        petriNetTab.addNewPetriNetComponent(view);

    }

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

    @EventAction(PetriNet.NEW_RATE_PARAMETER_CHANGE_MESSAGE)
    private void newRate(PropertyChangeEvent propertyChangeEvent) {

    }

    @EventAction(PetriNet.NEW_ANNOTATION_CHANGE_MESSAGE)
    private void newAnnotation(PropertyChangeEvent propertyChangeEvent) {
        Annotation annotation = (Annotation) propertyChangeEvent.getNewValue();
        AnnotationViewBuilder builder = new AnnotationViewBuilder(annotation, controller);

        AnnotationView view = builder.build(petriNetTab, applicationModel);
        petriNetTab.addNewPetriNetComponent(view);

    }

    @EventAction("newStateGroup")
    private void newStateGroup(PropertyChangeEvent propertyChangeEvent) {

    }

    @EventAction(PetriNet.DELETE_PLACE_CHANGE_MESSAGE)
    private void deletePlace(PropertyChangeEvent propertyChangeEvent) {
        Place place = (Place) propertyChangeEvent.getOldValue();
        petriNetTab.deletePetriNetComponent(place.getId());

    }

    @EventAction(PetriNet.DELETE_TRANSITION_CHANGE_MESSAGE)
    private void deleteTransition(PropertyChangeEvent propertyChangeEvent) {
        Transition transition = (Transition) propertyChangeEvent.getOldValue();
        petriNetTab.deletePetriNetComponent(transition.getId());

    }

    @EventAction(PetriNet.DELETE_ARC_CHANGE_MESSAGE)
    private void deleteArc(PropertyChangeEvent propertyChangeEvent) {
        Arc<? extends Connectable, ? extends Connectable> arc =
                (Arc<? extends Connectable, ? extends Connectable>) propertyChangeEvent.getOldValue();
        petriNetTab.deletePetriNetComponent(arc.getId());
    }

    @EventAction(PetriNet.DELETE_RATE_PARAMETER_CHANGE_MESSAGE)
    private void deleteRate(PropertyChangeEvent propertyChangeEvent) {
    }

    @EventAction(PetriNet.DELETE_ANNOTATION_CHANGE_MESSAGE)
    private void deleteAnnotation(PropertyChangeEvent propertyChangeEvent) {
        Annotation annotation = (Annotation) propertyChangeEvent.getOldValue();
        petriNetTab.deletePetriNetComponent(annotation.getId());
    }

    @EventAction("deleteStateGroup")
    private void deleteStateGroup(PropertyChangeEvent propertyChangeEvent) {

    }


    /**
     * This interface is for annotating methods with their change event
     */
    @Retention(RetentionPolicy.RUNTIME) // Make this annotation accessible at runtime via reflection.
    @Target({ElementType.METHOD})       // This annotation can only be applied to class methods.
    private @interface EventAction {
        /**
         * @return the events property name to listen out for
         */
        String value();
    }
}
