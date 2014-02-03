package pipe.views.changeListener;

import pipe.controllers.PetriNetController;
import pipe.gui.PetriNetTab;
import pipe.models.component.Connectable;
import pipe.models.component.annotation.Annotation;
import pipe.models.component.arc.Arc;
import pipe.models.component.arc.ArcType;
import pipe.models.component.place.Place;
import pipe.models.component.rate.RateParameter;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.PetriNet;
import pipe.views.*;
import pipe.views.builder.*;
import pipe.views.viewComponents.AnnotationView;

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
 * This class listens for changes in {@link pipe.models.petrinet.PetriNet}
 * and creates/deletes the relevant views as appropriate
 */
public class PetriNetChangeListener implements PropertyChangeListener {
    private final PipeApplicationView applicationView;

    /**
     * PetriNetTab that this listener refers to
     */
    private final PetriNetTab petriNetTab;

    private final PetriNetController controller;

    /**
     * Contains the property name and method it maps to
     */
    private Map<String, Method> eventMethods = new HashMap<String, Method>();

    public PetriNetChangeListener(PipeApplicationView applicationView, PetriNetTab petriNetTab,
                                  PetriNetController controller) {
        this.applicationView = applicationView;
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
        Method method = eventMethods.get(name);
        try {
            method.invoke(this, propertyChangeEvent);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @EventAction(PetriNet.NEW_PLACE_CHANGE_MESSAGE)
    private void newPlace(PropertyChangeEvent propertyChangeEvent) {
        Place place = (Place) propertyChangeEvent.getNewValue();
        PlaceViewBuilder builder = new PlaceViewBuilder(place, controller);
        PlaceView view = builder.build();
        petriNetTab.addNewPetriNetComponent(view);
    }

    @EventAction(PetriNet.NEW_TRANSITION_CHANGE_MESSAGE)
    private void newTransition(PropertyChangeEvent propertyChangeEvent) {
        Transition transition = (Transition) propertyChangeEvent.getNewValue();
        TransitionViewBuilder builder = new TransitionViewBuilder(transition, controller);
        TransitionView view = builder.build();

        petriNetTab.addNewPetriNetComponent(view);

    }

    @EventAction(PetriNet.NEW_ARC_CHANGE_MESSAGE)
    private void newArc(PropertyChangeEvent propertyChangeEvent) {
        Arc<? extends Connectable, ? extends Connectable> arc =
                (Arc<? extends Connectable, ? extends Connectable>) propertyChangeEvent.getNewValue();

        if (arc.getType().equals(ArcType.INHIBITOR)) {
            Arc<Place, Transition> inhibitorArc = (Arc<Place, Transition>) arc;
            InhibitorArcViewBuilder builder = new InhibitorArcViewBuilder(inhibitorArc, controller);
            InhibitorArcView view = builder.build();
            petriNetTab.addNewPetriNetComponent(view);
        } else {
            NormalArcViewBuilder builder = new NormalArcViewBuilder(arc, controller);
            ArcView<? extends Connectable, ? extends Connectable> view = builder.build();
            petriNetTab.addNewPetriNetComponent(view);
        }

    }

    /**
     * Refreshes the token class choices, and starts listening incase a token changes
     *
     * @param propertyChangeEvent
     */
    @EventAction(PetriNet.NEW_TOKEN_CHANGE_MESSAGE)
    private void newToken(PropertyChangeEvent propertyChangeEvent) {
        Token token = (Token) propertyChangeEvent.getNewValue();
        token.addPropertyChangeListener(new TokenChangeListener(applicationView));
        applicationView.refreshTokenClassChoices();
    }

    @EventAction(PetriNet.NEW_RATE_PARAMETER_CHANGE_MESSAGE)
    private void newRate(PropertyChangeEvent propertyChangeEvent) {
        RateParameter rateParameter = (RateParameter) propertyChangeEvent.getNewValue();
        applicationView.refreshRateParameters();

    }

    @EventAction(PetriNet.NEW_ANNOTATION_CHANGE_MESSAGE)
    private void newAnnotation(PropertyChangeEvent propertyChangeEvent) {
        Annotation annotation = (Annotation) propertyChangeEvent.getNewValue();
        AnnotationViewBuilder builder = new AnnotationViewBuilder(annotation, controller);

        AnnotationView view = builder.build();
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

    @EventAction(PetriNet.DELETE_TOKEN_CHANGE_MESSAGE)
    private void deleteToken(PropertyChangeEvent propertyChangeEvent) {
        applicationView.refreshTokenClassChoices();
    }

    @EventAction(PetriNet.DELETE_RATE_PARAMETER_CHANGE_MESSAGE)
    private void deleteRate(PropertyChangeEvent propertyChangeEvent) {
        applicationView.refreshRateParameters();
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
