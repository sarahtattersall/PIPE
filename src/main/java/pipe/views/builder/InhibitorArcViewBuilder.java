package pipe.views.builder;

import pipe.models.InhibitorArc;
import pipe.models.NormalArc;
import pipe.views.InhibitorArcView;
import pipe.views.MarkingView;
import pipe.views.NormalArcView;

import java.util.LinkedList;

public class InhibitorArcViewBuilder {
    private final InhibitorArc arc;

    public InhibitorArcViewBuilder(InhibitorArc arc) {
        this.arc = arc;
    }

    /*
    double startPositionXInput, double startPositionYInput,
    double endPositionXInput, double endPositionYInput,
    ConnectableView sourceInput,
    ConnectableView targetInput, LinkedList<MarkingView> weightInput,
    String idInput, boolean taggedInput, NormalArc model) {     */
    public InhibitorArcView build() {
        double startX = arc.getSource().getX();
        double startY = arc.getSource().getY();
        double endX = arc.getTarget().getX();
        double endY = arc.getTarget().getY();
        InhibitorArcView view = new InhibitorArcView(startX, startY, endX, endY,
                                               null, null, new LinkedList<MarkingView>(),
                                               arc.getId(), arc);
        return view;

    }
}
