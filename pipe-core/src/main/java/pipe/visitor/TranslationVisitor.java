package pipe.visitor;

import pipe.models.component.Connectable;
import pipe.models.component.PetriNetComponent;
import pipe.models.component.annotation.Annotation;
import pipe.models.component.annotation.AnnotationVisitor;
import pipe.models.component.arc.*;
import pipe.models.component.place.Place;
import pipe.models.component.place.PlaceVisitor;
import pipe.models.component.transition.Transition;
import pipe.models.component.transition.TransitionVisitor;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.List;

/**
 * Translates PetriNetComponents by a given amount
 */
public class TranslationVisitor implements ArcVisitor, ArcPointVisitor, PlaceVisitor, TransitionVisitor,
        AnnotationVisitor {
    private final Point translation;

    private final Collection<PetriNetComponent> selected;

    public TranslationVisitor(Point translation, Collection<PetriNetComponent> selected) {
        this.translation = translation;
        this.selected = selected;
    }


    public <T extends Connectable, S extends Connectable> void visit(Arc<S, T> arc) {
        if (selected.contains(arc.getSource()) && selected.contains(arc.getTarget())) {
            List<ArcPoint> points = arc.getArcPoints();
            for (ArcPoint arcPoint : points) {
                Point2D point = arcPoint.getPoint();
                Point2D newPoint =
                        new Point2D.Double(point.getX() + translation.getX(), point.getY() + translation.getY());
                arcPoint.setPoint(newPoint);
            }
        }
    }

    @Override
    public void visit(Place place) {
        place.setX(place.getX() + translation.x);
        place.setY(place.getY() + translation.y);

    }

    @Override
    public void visit(Transition transition) {
        transition.setX(transition.getX() + translation.x);
        transition.setY(transition.getY() + translation.y);

    }

    @Override
    public void visit(ArcPoint arcPoint) {
        double x = arcPoint.getX() + translation.getX();
        double y = arcPoint.getY() + translation.getY();
        arcPoint.setPoint(new Point2D.Double(x, y));
    }

    @Override
    public void visit(Annotation annotation) {
        annotation.setX(annotation.getX() + (int)translation.getX());
        annotation.setY(annotation.getY() + (int)translation.getY());
    }

    @Override
    public void visit(InboundArc inboundArc) {
        visit(inboundArc);
    }

    @Override
    public void visit(OutboundArc outboundArc) {
        visit(outboundArc);
    }
}
