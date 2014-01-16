package pipe.visitor.connectable.arc;

import pipe.visitor.PlaceVisitor;
import pipe.visitor.TransitionVisitor;
import pipe.visitor.connectable.ConnectableVisitor;

/**
 * tiny type, an arc creator
 */
public interface ArcCreatorVisitor extends PlaceVisitor, TransitionVisitor {
}
