package pipe.views.builder;

import pipe.controllers.PetriNetController;
import pipe.models.Marking;
import pipe.models.NormalArc;
import pipe.views.MarkingView;
import pipe.views.NormalArcView;
import pipe.views.TokenView;

import java.awt.geom.Point2D;
import java.util.LinkedList;

public class NormalArcViewBuilder {
    private final NormalArc arc;
    private final PetriNetController controller;

    public NormalArcViewBuilder(NormalArc arc, PetriNetController controller) {
        this.arc = arc;
        this.controller = controller;
    }

    /*
    double startPositionXInput, double startPositionYInput,
    double endPositionXInput, double endPositionYInput,
    ConnectableView sourceInput,
    ConnectableView targetInput, LinkedList<MarkingView> weightInput,
    String idInput, boolean taggedInput, NormalArc model) {     */
    public NormalArcView build() {
        double startX = arc.getSource().getX();
        double startY = arc.getSource().getY();
        double endX = arc.getTarget().getX();
        double endY = arc.getTarget().getY();

        LinkedList<MarkingView> markings = new LinkedList<MarkingView>();
//        for (Marking marking : arc.getWeight()) {
//            TokenViewBuilder builder = new TokenViewBuilder(marking.getToken());
//            TokenView token = builder.build();
//            MarkingView view = new MarkingView(token, marking.getCurrentMarking());
//            markings.add(view);
//        }

        NormalArcView view =
                new NormalArcView(startX, startY, endX, endY, null, null, markings, arc.getId(), arc.isTagged(), arc, controller);
        return view;

    }
}
