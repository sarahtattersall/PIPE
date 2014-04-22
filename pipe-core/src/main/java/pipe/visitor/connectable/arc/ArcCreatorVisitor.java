package pipe.visitor.connectable.arc;

import pipe.models.component.place.PlaceVisitor;
import pipe.models.component.transition.TransitionVisitor;

/**
 * tiny type, an arc creator
 */
public interface ArcCreatorVisitor extends PlaceVisitor, TransitionVisitor {
}
