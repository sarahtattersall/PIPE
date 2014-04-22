package pipe.visitor;

import pipe.models.component.annotation.AnnotationVisitor;
import pipe.models.component.arc.ArcVisitor;
import pipe.models.component.place.PlaceVisitor;
import pipe.models.component.rate.RateVisitor;
import pipe.models.component.token.TokenVisitor;
import pipe.models.component.transition.TransitionVisitor;

/**
 * Interface for visiting all Petri net components
 */
public interface AllComponentVisitor
        extends PlaceVisitor, TransitionVisitor, ArcVisitor, AnnotationVisitor, TokenVisitor, RateVisitor {
}
