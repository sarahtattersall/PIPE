package pipe.visitor;

import pipe.models.component.Connectable;
import pipe.models.component.PetriNetComponent;
import pipe.models.component.arc.Arc;
import pipe.models.component.arc.ArcPoint;
import pipe.models.component.arc.ArcPointVisitor;
import pipe.models.component.arc.ArcVisitor;
import pipe.models.component.place.Place;
import pipe.models.component.place.PlaceVisitor;
import pipe.models.component.transition.Transition;
import pipe.models.component.transition.TransitionVisitor;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.List;

/**
 * Translates PetriNetComponents by a given amount
 */
public class TranslationVisitor implements ArcVisitor, ArcPointVisitor, PlaceVisitor, TransitionVisitor {
    private final Point2D translation;

    private final Collection<PetriNetComponent> selected;

    public TranslationVisitor(Point2D translation, Collection<PetriNetComponent> selected) {
        this.translation = translation;
        this.selected = selected;
    }

    @Override
    public <T extends Connectable, S extends Connectable> void visit(Arc<S, T> arc) {
        if (selected.contains(arc.getSource()) && selected.contains(arc.getTarget())) {
            List<ArcPoint> points = arc.getIntermediatePoints();
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
        place.setX(place.getX() + translation.getX());
        place.setY(place.getY() + translation.getY());

    }

    @Override
    public void visit(Transition transition) {
        transition.setX(transition.getX() + translation.getX());
        transition.setY(transition.getY() + translation.getY());

    }

    @Override
    public void visit(ArcPoint arcPoint) {
        double x = arcPoint.getX() + translation.getX();
        double y = arcPoint.getY() + translation.getY();
        arcPoint.setPoint(new Point2D.Double(x, y));
    }
}
